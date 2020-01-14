package com.agile.mvc;

import com.agile.common.annotation.EnableAgile;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.PasswordUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 入口工程
 *
 * @author tudou
 */
@EnableAgile
@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class App {
    public static void main(String[] args) {
        new SpringApplication(App.class).run(args);
        PasswordUtil.checkPasswordStrength("Idss@1234");
    }
}
