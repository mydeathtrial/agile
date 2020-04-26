package com.agile.common.container;

import com.agile.common.annotation.Mapping;
import com.agile.common.base.Constant;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.util.StringUtil;
import org.slf4j.Logger;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.util.ProxyUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 佟盟 on 2018/11/4
 */
public class AgileHandlerMapping extends RequestMappingHandlerMapping {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(AgileHandlerMapping.class);

    private final boolean useSuffixPatternMatch = true;
    private final boolean useRegisteredSuffixPatternMatch = false;
    private final boolean useTrailingSlashMatch = true;
    private final Map<String, RequestMappingInfo> cache = new HashMap<>();

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    private RequestMappingInfo createMappingInfo(Mapping mapping, RequestCondition<?> condition) {
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(this.resolveEmbeddedValuesInPatterns(mapping.path()))
                .methods(mapping.method())
                .params(mapping.params())
                .headers(mapping.headers())
                .consumes(mapping.consumes())
                .produces(mapping.produces())
                .mappingName(mapping.name());
        if (condition != null) {
            builder.customCondition(condition);
        }
        return builder.options(this.config).build();
    }

    @Nullable
    private RequestMappingInfo createMappingInfo(AnnotatedElement element) {
        Mapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, Mapping.class);
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class<?>) element) : this.getCustomMethodCondition((Method) element);
        return requestMapping != null ? this.createMappingInfo(requestMapping, condition) : null;
    }

    @Override
    public RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = this.createMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = this.createMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    private String createDefaultMappingPath(AnnotatedElement element) {
        StringBuilder path = new StringBuilder();
        if (element instanceof Class) {
            path.append(String.format("/api/%s", StringUtil.camelToSpilt(((Class<?>) element).getSimpleName(), Constant.RegularAbout.MINUS).toLowerCase()));
            //path.append(String.format("/api/{service:%s}", StringUtil.camelToUrlRegex(((Class) element).getSimpleName())));
        } else if (element instanceof Method) {
            path.append(String.format("/%s", StringUtil.camelToSpilt(((Method) element).getName(), Constant.RegularAbout.MINUS).toLowerCase()));
            //path.append(String.format("/{method:%s}", StringUtil.camelToUrlRegex(((Method) element).getName())));
        }
        return path.toString();
    }

    private RequestMappingInfo createDefaultMappingInfo(AnnotatedElement element, RequestCondition<?> condition) {
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths(this.resolveEmbeddedValuesInPatterns(new String[]{createDefaultMappingPath(element)})).methods().params().headers().consumes().produces().mappingName("");
        if (condition != null) {
            builder.customCondition(condition);
        }
        return builder.options(this.config).build();
    }

    private RequestMappingInfo createDefaultMappingInfo(AnnotatedElement element) {
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class<?>) element) : this.getCustomMethodCondition((Method) element);
        return this.createDefaultMappingInfo(element, condition);
    }

    public RequestMappingInfo getDefaultFroMethod(Method method, Class<?> handlerType) {

        RequestMappingInfo defaultMappingInfo = this.createDefaultMappingInfo(method);
        RequestMappingInfo defaultTypeInfo = this.createDefaultMappingInfo(handlerType);
        defaultMappingInfo = defaultTypeInfo.combine(defaultMappingInfo);
        return defaultMappingInfo;
    }

    @Override
    public void afterPropertiesSet() {
        this.config = new RequestMappingInfo.BuilderConfiguration();
        this.config.setUrlPathHelper(this.getUrlPathHelper());
        this.config.setPathMatcher(this.getPathMatcher());
        this.config.setSuffixPatternMatch(this.useSuffixPatternMatch);
        this.config.setTrailingSlashMatch(this.useTrailingSlashMatch);
        this.config.setRegisteredSuffixPatternMatch(this.useRegisteredSuffixPatternMatch);
        this.config.setContentNegotiationManager(this.getContentNegotiationManager());
    }

    @Override
    public void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        for (String path : mapping.getPatternsCondition().getPatterns()) {
            RequestMappingInfo cacheMapping = cache.get(path);

            if (!ObjectUtils.isEmpty(cacheMapping)) {
                Set<RequestMethod> methods = mapping.getMethodsCondition().getMethods();
                for (RequestMethod requestMethod : cacheMapping.getMethodsCondition().getMethods()) {
                    if (methods.contains(requestMethod)) {
                        LoggerFactory.COMMON_LOG.error(String.format("Mapping映射重复，重复类:%s,重复方法:%s", ProxyUtils.getUserClass(handler).getName(), method.getName()));
                        throw new IllegalStateException();
                    }
                }
            }

            cache.put(path, mapping);
        }
        super.registerHandlerMethod(handler, method, mapping);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("[class:%s][method:%s][url:%s]",
                    ProxyUtils.getUserClass(handler.getClass()).getCanonicalName(),
                    method.getName(),
                    String.join(",", mapping.getPatternsCondition().getPatterns())));
        }
    }
}
