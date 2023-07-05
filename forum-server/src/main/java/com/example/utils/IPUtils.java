package com.example.utils;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {

    private static final String LOCAL_IP = "0:0:0:0:0:0:0:1";
    /**
     * 获取Ip
     *
     * @param request  请求
     */
    public static String getIpRequest(HttpServletRequest request) {
        String unknown = "unknown";

        String ip0 = request.getHeader("x-forwarded-for");
        String ip = request.getHeader("X-Real-IP");

        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (LOCAL_IP.equals(ip)) {
            ip = "local";
        }

        return ip;
    }
}
