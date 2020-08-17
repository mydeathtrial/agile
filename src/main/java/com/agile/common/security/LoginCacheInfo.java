package com.agile.common.security;

import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.util.date.DateUtil;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.spring.util.spring.BeanUtil;
import cloud.agileframework.spring.util.spring.IdUtil;
import com.agile.common.base.Constant;
import com.agile.common.exception.NoSignInException;
import com.agile.common.exception.TokenIllegalException;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.TokenUtil;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 佟盟
 * 日期 2019/3/20 19:03
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class LoginCacheInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private Authentication authentication;
    private Map<Long, TokenInfo> sessionTokens = new HashMap<>();
    private static SecurityProperties securityProperties = BeanUtil.getBean(SecurityProperties.class);

    private static Cache cache = CacheUtil.getCache(Objects.requireNonNull(BeanUtil.getBean(SecurityProperties.class)).getTokenHeader());

    public static Cache getCache() {
        return cache;
    }

    private static CustomerUserDetailsService customerUserDetailsService = BeanUtil.getBean(CustomerUserDetailsService.class);


    /**
     * 创建登录信息
     *
     * @param username       账号
     * @param authentication 用户权限信息
     * @param sessionToken   本次会话令牌
     * @param token          信息令牌
     * @param start          开始时间
     * @param end            结束时间
     * @return
     */
    static LoginCacheInfo createLoginCacheInfo(String username, Authentication authentication, Long sessionToken, String token, Date start, Date end) {
        LoginCacheInfo loginCacheInfo = cache.get(username, LoginCacheInfo.class);
        Map<Long, TokenInfo> sessionTokens;

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        tokenInfo.setStart(start);
        tokenInfo.setEnd(end);

        if (loginCacheInfo == null) {
            loginCacheInfo = new LoginCacheInfo();
            loginCacheInfo.setUsername(username);
            loginCacheInfo.setAuthentication(authentication);
            sessionTokens = new HashMap<>(Constant.NumberAbout.ONE);
        } else {
            sessionTokens = loginCacheInfo.getSessionTokens();
            parsingTimeOut(sessionTokens);
        }
        sessionTokens.put(sessionToken, tokenInfo);
        loginCacheInfo.setSessionTokens(sessionTokens);
        return loginCacheInfo;
    }

    /**
     * 处理掉过期时间
     */
    public void parsingTimeOut() {
        parsingTimeOut(sessionTokens);
    }

    /**
     * 处理过期数据
     *
     * @param sessionTokens 会话令牌集合
     */
    private static void parsingTimeOut(Map<Long, TokenInfo> sessionTokens) {
        sessionTokens.values().removeIf(tokenInfo -> !tokenInfo.getEnd().after(DateUtil.getCurrentDate()));
    }

    /**
     * 根据token令牌获取用户缓存信息
     *
     * @param token 令牌
     * @return 用户缓存信息
     */
    static CurrentLoginInfo getCurrentLoginInfo(String token) {
        if (StringUtil.isBlank(token)) {
            throw new NoSignInException("账号尚未登录");
        }

        Claims claims = TokenUtil.getClaimsFromToken(token);
        if (claims == null) {
            throw new TokenIllegalException("身份令牌验证失败");
        } else {
            return refreshTimeOut(claims);
        }
    }

    /**
     * 刷新身份令牌
     *
     * @param currentLoginInfo 当前登陆用户信息
     * @return 新令牌
     */
    public static String refreshToken(CurrentLoginInfo currentLoginInfo) {
        LoginCacheInfo loginCacheInfo = currentLoginInfo.getLoginCacheInfo();
        Long oldSessionToken = currentLoginInfo.getSessionToken();

        //创建新会话令牌
        long newSessionToken = IdUtil.generatorId();

        //删除旧的缓存会话令牌
        loginCacheInfo.getSessionTokens().remove(oldSessionToken);

        //生成新的会话令牌缓存
        String token = TokenUtil.generateToken(currentLoginInfo.getLoginCacheInfo().getUsername(), newSessionToken, DateUtil.add(new Date(), Duration.of(1, ChronoUnit.YEARS)));

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        tokenInfo.setStart(new Date());
        tokenInfo.setEnd(DateUtil.add(new Date(), securityProperties.getTokenTimeout()));
        loginCacheInfo.getSessionTokens().put(newSessionToken, tokenInfo);

        //同步缓存
        cache.put(loginCacheInfo.getUsername(), loginCacheInfo);

        //更新数据库登录信息
        CustomerUserDetailsService securityUserDetailsService = BeanUtil.getBean(CustomerUserDetailsService.class);
        assert securityUserDetailsService != null;
        securityUserDetailsService.updateLoginInfo(loginCacheInfo.getUsername(), Long.toString(oldSessionToken), Long.toString(newSessionToken));

        return token;
    }

    /**
     * 验证当前redis缓存数据是否合法
     *
     * @param loginCacheInfo 登陆信息
     */
    public static void validateCacheDate(LoginCacheInfo loginCacheInfo) {
        LoginCacheInfo info = Optional.ofNullable(loginCacheInfo).orElseThrow(() -> new UsernameNotFoundException("Not Found Account"));
        customerUserDetailsService.validate((UserDetails) info.getAuthentication().getDetails());
    }

    /**
     * 刷新token过期时间
     *
     * @param claims 令牌信息
     * @return 当前登陆信息
     */
    private static CurrentLoginInfo refreshTimeOut(Claims claims) {
        Long sessionToken = claims.get(TokenUtil.AUTHENTICATION_SESSION_TOKEN, Long.class);
        String username = claims.get(TokenUtil.AUTHENTICATION_USER_NAME, String.class);

        LoginCacheInfo loginCacheInfo = cache.get(username, LoginCacheInfo.class);

        if (loginCacheInfo == null || loginCacheInfo.getSessionTokens().get(sessionToken) == null) {
            throw new TokenIllegalException("身份令牌验证失败");
        }
        TokenInfo sessionInfo = loginCacheInfo.getSessionTokens().get(sessionToken);
        if (!sessionInfo.getEnd().after(DateUtil.getCurrentDate()) || !claims.getExpiration().after(DateUtil.getCurrentDate())) {
            throw new TokenIllegalException("身份令牌已过期");
        }
        sessionInfo.setEnd(DateUtil.add(new Date(), securityProperties.getTokenTimeout()));
        cache.put(username, loginCacheInfo);

        return new CurrentLoginInfo(sessionToken, loginCacheInfo);
    }

    /**
     * 退出操作，根据token删除指定会话令牌
     *
     * @param token 令牌
     */
    public static void remove(String token) {
        remove(getCurrentLoginInfo(token));
    }

    /**
     * 退出操作，根据currentLoginInfo删除指定会话令牌
     *
     * @param currentLoginInfo 当前登录信息
     */
    public static void remove(CurrentLoginInfo currentLoginInfo) {
        currentLoginInfo.getLoginCacheInfo().getSessionTokens().remove(currentLoginInfo.getSessionToken());
        if (currentLoginInfo.getLoginCacheInfo().getSessionTokens().size() > 0) {
            cache.put(currentLoginInfo.getLoginCacheInfo().getUsername(), currentLoginInfo.getLoginCacheInfo());
        } else {
            cache.evict(currentLoginInfo.getLoginCacheInfo().getUsername());
        }
    }
}
