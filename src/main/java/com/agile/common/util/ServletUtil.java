package com.agile.common.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author 佟盟 on 2017/2/23
 */
public class ServletUtil {
    public static String getCurrentRequestIP() {
        HttpServletRequest request = getCurrentRequest();
        return getRequestIP(request);
    }

    public static String getCurrentRequestUrl() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getRequestURI();
        }
        return null;
    }

    /**
     * 获取http请求的真实IP地址
     *
     * @param request 请求对象
     * @return 返回IP地址
     */
    public static String getRequestIP(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return localhostFormat(ip);
    }

    /**
     * 本地地址格式化
     *
     * @param ip IP地址
     * @return 格式化后的IP地址
     */
    public static String localhostFormat(String ip) {
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException unknownhostexception) {
                ip = "未知IP地址";
            }
        }
        return ip;
    }

    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     */
    private static String getLinuxLocalIp() {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /**
     * 获取本地Host名称
     */
    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断操作系统是否是Windows
     */
    private static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    /**
     * 获取本地IP地址
     */
    public static String getLocalIP() {
        if (isWindowsOS()) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                return "未知";
            }
        } else {
            return getLinuxLocalIp();
        }
    }

    /**
     * 处理body参数
     *
     * @param request 请求request
     */
    public static String getBody(HttpServletRequest request) {

        try {
            BufferedReader br = request.getReader();

            String temp;
            StringBuilder jsonStr = new StringBuilder();
            while ((temp = br.readLine()) != null) {
                jsonStr.append(temp);
            }
            if (jsonStr.length() > 0) {
                return jsonStr.toString();
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    /**
     * 获取当前request中的请求地址
     *
     * @return url
     */
    public static String getCurrentUrl(HttpServletRequest request) {
        if (request != null) {
            return request.getRequestURL().toString();
        }
        return null;
    }

    /**
     * 请求中获取header或cookies中的信息
     *
     * @param request 请求 请求
     * @param key     索引
     * @return 信息
     */
    public static String getInfo(HttpServletRequest request, String key) {
        String token = request.getHeader(key);

        if (StringUtil.isBlank(token)) {
            token = getCookie(request, key);

            if (token == null) {
                Map<String, Object> map = ParamUtil.handleInParam(request);
                Object tokenValue = map.get(key);
                if (tokenValue != null) {
                    return tokenValue.toString();
                }
            }
        }
        return token;
    }

    /**
     * 获取cookies信息
     *
     * @param request 请求
     * @param key     索引
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 获取当前请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    /**
     * 获取当前请求
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getCurrentResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getResponse();
        }
        return null;
    }

    /**
     * 获取当前请求
     *
     * @return SessionId
     */
    public static String getCurrentSessionId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return requestAttributes.getSessionId();
        }
        return null;
    }
}
