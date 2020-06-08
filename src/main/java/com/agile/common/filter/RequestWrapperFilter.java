package com.agile.common.filter;

import com.agile.common.base.RequestWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 佟盟
 * 日期 2020/6/4 15:25
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class RequestWrapperFilter extends OncePerRequestFilter implements Filter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(new RequestWrapper(httpServletRequest), httpServletResponse);
    }
}
