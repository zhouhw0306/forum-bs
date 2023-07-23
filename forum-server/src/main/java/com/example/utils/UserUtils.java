package com.example.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhw
 */
public class UserUtils {

    /**
     * 获得当前用户
     * @return 用户id
     */
    public static String getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return JWTUtil.getUserId(token);
    }

}
