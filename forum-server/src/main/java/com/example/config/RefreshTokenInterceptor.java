package com.example.config;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import static com.example.utils.RedisConstants.LOGIN_TOKEN_KEY;
import static com.example.utils.RedisConstants.LOGIN_TOKEN_TTL;

/**
 * @author zhw
 */
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("进入token拦截器刷新token: {}",request.getRequestURI());
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)){
            return true;
        }
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY+token,LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        return true;
    }
}
