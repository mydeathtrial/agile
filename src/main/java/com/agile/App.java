package com.agile;

import com.agile.common.annotation.EnableAgile;
import com.agile.common.base.AgileApp;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticSearchRestHealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;

/**
 * 入口工程
 */
@EnableAgile
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class, ElasticSearchRestHealthIndicatorAutoConfiguration.class, FreeMarkerAutoConfiguration.class})
public class App {
    public static void main(String[] args) {
        AgileApp.run(App.class, args);
    }
}
