package com.agile.common.container;

import com.agile.common.param.AgileParam;
import com.agile.common.util.ParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/6/1 21:48
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CustomHandlerInterceptor implements HandlerInterceptor {
    private Logger log = LoggerFactory.getLogger(CustomHandlerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AgileParam.clear();
    }
}
