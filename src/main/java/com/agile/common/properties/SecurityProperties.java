package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.security")
@Setter
@Getter
public class SecurityProperties {
    /**
     * 开关
     */
    private boolean enable = true;
    /**
     * 排除的地址
     */
    private String excludeUrl = "";
    /**
     * 登陆地址
     */
    private String loginUrl = "/login";
    /**
     * 登出地址
     */
    private String loginOutUrl = "/logout";
    /**
     * 验证码
     */
    private String verificationCode = "verification";
    /**
     * token加密盐值
     */
    private String tokenKey = "23617641641";
    /**
     * token超时时间
     */
    private int tokenTimeout;
    /**
     * token传递header名
     */
    private String tokenHeader = "AGILE_TOKEN";
    /**
     * 登陆账号表单名
     */
    private String loginUsername = "username";
    /**
     * 登陆密码表单名
     */
    private String loginPassword = "password";

    private TokenType tokenType = TokenType.EASY;

    /**
     * Token级别
     */
    public enum TokenType {
        /**
         * 容易
         */
        EASY,
        /**
         * 难
         */
        DIFFICULT
    }
}
