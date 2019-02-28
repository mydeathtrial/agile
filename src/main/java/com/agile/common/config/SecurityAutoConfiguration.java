package com.agile.common.config;

import com.agile.common.base.Constant;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.properties.KaptchaConfigProperties;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.security.FailureHandler;
import com.agile.common.security.JwtAuthenticationProvider;
import com.agile.common.security.LoginFilter;
import com.agile.common.security.LogoutHandler;
import com.agile.common.security.SecurityUserDetailsService;
import com.agile.common.security.TokenFilter;
import com.agile.common.security.TokenStrategy;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * @author 佟盟 on 2017/9/26
 */
@Configuration
@EnableConfigurationProperties(value = {SecurityProperties.class, KaptchaConfigProperties.class})
@EnableWebSecurity
@ConditionalOnProperty(name = "enable", prefix = "agile.security", havingValue = "true")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnClass({WebSecurityConfigurerAdapter.class, AuthenticationProvider.class})
@AutoConfigureAfter({DruidAutoConfiguration.class})
public class SecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private String[] immuneUrl;

    private Dao dao;

    private SecurityProperties securityProperties;

    private KaptchaConfigProperties kaptchaConfigProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers(immuneUrl).permitAll().anyRequest().authenticated()
                .and().logout().logoutUrl(securityProperties.getLoginOutUrl()).deleteCookies(securityProperties.getTokenHeader()).logoutSuccessHandler(logoutHandler())
                .and().exceptionHandling().accessDeniedHandler(new FailureHandler())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).sessionFixation().none()
                .and().csrf().disable().httpBasic().disable()
                .addFilterAt(tokenFilter(), LogoutFilter.class)
                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    SecurityUserDetailsService securityUserDetailsService() {
        return new SecurityUserDetailsService(dao);
    }

    @Bean
    @ConditionalOnClass({SessionAuthenticationStrategy.class})
    TokenStrategy tokenStrategy() {
        return new TokenStrategy(securityUserDetailsService(), securityProperties);
    }

    @Bean
    @ConditionalOnClass({AuthenticationProvider.class})
    AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(securityUserDetailsService());
    }

    @Bean
    LoginFilter loginFilter() {
        return new LoginFilter(jwtAuthenticationProvider(), tokenStrategy(), securityProperties, kaptchaConfigProperties);
    }

    @Bean
    TokenFilter tokenFilter() {
        return new TokenFilter(securityUserDetailsService(), securityProperties, immuneUrl);
    }

    @Bean
    LogoutHandler logoutHandler() {
        return new LogoutHandler();
    }

    @Autowired
    public SecurityAutoConfiguration(Dao dao, SecurityProperties securityProperties, KaptchaConfigProperties kaptchaConfigProperties) {
        immuneUrl = new String[]{
                "/static/**",
                "/favicon.ico",
                PropertiesUtil.getProperty("agile.druid.url") + "/**",
                PropertiesUtil.getProperty("agile.security.login-url"),
                PropertiesUtil.getProperty("agile.kaptcha.url"),
                "/actuator/**",
                "/actuator/*",
                "/actuator",
                "/jolokia"};
        this.immuneUrl = (String[]) ArrayUtil.addAll(immuneUrl, PropertiesUtil.getProperty("agile.security.exclude-url").split(Constant.RegularAbout.COMMA));
        this.dao = dao;
        this.securityProperties = securityProperties;
        this.kaptchaConfigProperties = kaptchaConfigProperties;
    }
}