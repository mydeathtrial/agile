package com.agile.common.base;

import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 佟盟 on 2018/11/2
 */
public abstract class AbstractResponseFormat extends LinkedHashMap<String, Object> {
    /**
     * 获取返回信息
     *
     * @return RETURN
     */
    public abstract RETURN getReturn();

    /**
     * 获取结果信息
     *
     * @return result
     */
    public abstract Object getResult();

    /**
     * 构建返回数据
     *
     * @param head   头部信息
     * @param result 体
     * @return 格式化信息
     */
    public abstract Map<String, Object> buildResponseData(Head head, Object result);

    /**
     * 构建响应报文体
     *
     * @param head   头信息
     * @param result 体信息
     * @return 返回ModelAndView
     */
    public ModelAndView buildResponse(Head head, Object result) {
        ModelAndView modelAndView = new ModelAndView();
        if (result == null) {
            modelAndView.addAllObjects(buildResponseData(head, null));
            return modelAndView;
        }
        if (Map.class.isAssignableFrom(result.getClass())) {
            Map resultMap = ((Map) result);
            boolean isResult = ((Map) result).containsKey(Constant.ResponseAbout.RESULT);
            if (isResult) {
                modelAndView.addAllObjects(buildResponseData(head, resultMap.get(Constant.ResponseAbout.RESULT)));
                return modelAndView;
            }
        }
        modelAndView.addAllObjects(buildResponseData(head, result));
        return modelAndView;
    }
}
