package com.agile.common.config;

import com.agile.common.properties.SpringMVCProperties;
import com.agile.common.view.JsonView;
import com.agile.common.view.PlainView;
import com.agile.common.view.XmlView;
import com.agile.common.viewResolver.JsonViewResolver;
import com.agile.common.viewResolver.JumpViewResolver;
import com.agile.common.viewResolver.PlainViewResolver;
import com.agile.common.viewResolver.XmlViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2017/8/22
 */
@Configuration
@EnableWebMvc
public class SpringMvcConfig implements WebMvcConfigurer {

    private static Map<String, MediaType> map = new HashMap<>();

    static {
        map.put("plain", MediaType.TEXT_PLAIN);
    }

    @Autowired
    public SpringMvcConfig(SpringMVCProperties springMVCProperties) {
        this.springMVCProperties = springMVCProperties;
    }

    public static Map<String, MediaType> getMap() {
        return map;
    }

    private final SpringMVCProperties springMVCProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.setOrder(-1).addResourceHandler("/static/**", "/favicon.ico")
                .addResourceLocations("classpath:com/agile/static/", "classpath:com/agile/static/img/", "classpath:com/agile/static/plus/jquery/", "classpath:com/agile/static/plus/swagger/");
    }

    /**
     * 视图解析器
     * 配置视图解析器视图列表
     *
     * @param manager 略
     * @return 视图解析器
     */
    @Bean
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
        List<ViewResolver> list = new ArrayList<>();
        list.add(new JsonViewResolver());
        list.add(new XmlViewResolver());
        list.add(new PlainViewResolver());
        list.add(new JumpViewResolver());

        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setContentNegotiationManager(manager);
        viewResolver.setViewResolvers(list);
        return viewResolver;
    }

    /**
     * 文件上传配置
     *
     * @return 文件上传下载解析器
     */
    @Bean
    public CommonsMultipartResolver contentCommonsMultipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(springMVCProperties.getUpload().getMaxUploadSize());
        resolver.setDefaultEncoding(springMVCProperties.getUpload().getDefaultEncoding());
        return resolver;
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(false)
                .favorPathExtension(true)
                .favorParameter(false)
                .defaultContentType(MediaType.APPLICATION_JSON_UTF8)
                .mediaTypes(map);
    }

    @Bean
    ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    ResourceBundleMessageSource resourceBundleMessageSource() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasename("com.agile.conf.message");
        return resourceBundleMessageSource;
    }

    @Bean
    JsonView jsonView() {
        return new JsonView();
    }

    @Bean
    PlainView plainView() {
        return new PlainView();
    }

    @Bean
    XmlView xmlView() {
        return new XmlView();
    }
}
