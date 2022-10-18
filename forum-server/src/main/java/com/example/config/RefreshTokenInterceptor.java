package com.example.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import static com.example.utils.RedisConstants.LOGIN_TOKEN_KEY;
import static com.example.utils.RedisConstants.LOGIN_TOKEN_TTL;

/**
 * @author 24668
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)){
            return true;
        }
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY+token,LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        return true;
    }
}
