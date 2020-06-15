package com.agile.common.param;

import com.agile.common.exception.NoSignInException;
import com.agile.common.properties.SimulationProperties;
import com.agile.common.security.CustomerUserDetails;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.ParamUtil;
import com.agile.common.util.clazz.TypeReference;
import com.agile.common.util.object.ObjectUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/6/1 14:20
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class AgileParam {
    private final Map<String, Object> params;

    public AgileParam(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * 获取当前用户信息
     */
    public CustomerUserDetails getUser() {
        // 判断模拟配置
        SimulationProperties simulation = FactoryUtil.getBean(SimulationProperties.class);
        if (simulation != null && simulation.isEnable()) {
            return ObjectUtil.to(simulation.getUser(),
                    new com.agile.common.util.clazz.TypeReference<>(simulation.getUserClass()));
        }
        // 非模拟情况
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new NoSignInException("账号尚未登录，服务中无法获取登录信息");
        } else {
            return (CustomerUserDetails) authentication.getDetails();
        }
    }

    public Map<String, Object> getInParam() {
        return params;
    }

    public boolean containsKey(String key) {
        return ParamUtil.containsKey(getInParam(), key);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public Object getInParam(String key) {
        return ParamUtil.getInParam(getInParam(), key);
    }


    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz 参数映射类型
     * @return 入参映射对象
     */
    public <T> T getInParam(Class<T> clazz) {
        return ParamUtil.getInParam(getInParam(), clazz);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @return 入参映射对象
     */
    public <T> T getInParamByPrefix(Class<T> clazz, String prefix) {
        return ObjectUtil.getObjectFromMap(clazz, this.getInParam(), prefix);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @param suffix 筛选参数后缀
     * @return 入参映射对象
     */
    public <T> T getInParamByPrefixAndSuffix(Class<T> clazz, String prefix, String suffix) {
        return ObjectUtil.getObjectFromMap(clazz, this.getInParam(), prefix, suffix);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public String getInParam(String key, String defaultValue) {
        return ParamUtil.getInParam(getInParam(), key, defaultValue);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public <T> T getInParam(String key, Class<T> clazz) {
        return ParamUtil.getInParam(getInParam(), key, clazz);
    }

    /**
     * 取path下入参，转换为指定泛型
     *
     * @param key       参数path
     * @param reference 泛型
     * @param <T>       泛型
     * @return 转换后的入参
     */
    public <T> T getInParam(String key, TypeReference<T> reference) {
        return ParamUtil.getInParam(getInParam(), key, reference);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public <T> T getInParam(String key, Class<T> clazz, T defaultValue) {
        return ParamUtil.getInParam(getInParam(), key, clazz, defaultValue);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    public MultipartFile getInParamOfFile(String key) {
        return ParamUtil.getInParamOfFile(getInParam(), key);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    public List<MultipartFile> getInParamOfFiles(String key) {
        return ParamUtil.getInParamOfFiles(getInParam(), key);
    }

    /**
     * 服务中调用该方法获取字符串数组入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public List<String> getInParamOfArray(String key) {
        return getInParamOfArray(key, String.class);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public <T> List<T> getInParamOfArray(String key, Class<T> clazz) {
        return ParamUtil.getInParamOfArray(getInParam(), key, clazz);
    }
}