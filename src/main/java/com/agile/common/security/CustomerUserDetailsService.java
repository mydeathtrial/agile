package com.agile.common.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author 佟盟
 * 日期 2019/3/15 12:50
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface CustomerUserDetailsService extends UserDetailsService {
    /**
     * 用户身份验证
     *
     * @param securityUser 用户信息
     * @throws AuthenticationException 身份验证失败异常
     */
    void validate(UserDetails securityUser) throws AuthenticationException;

    /**
     * 更新登陆信息
     */
    void updateLoginInfo(String userName, String oldToken, String newToken);

    /**
     * 终止
     */
    void stopLoginInfo(String userName, String token);

    /**
     * 新增登陆信息
     *
     * @param securityUser 用户信息
     * @param ip           登陆ip
     * @param token        令牌
     */
    void loadLoginInfo(UserDetails securityUser, String ip, String token);
}