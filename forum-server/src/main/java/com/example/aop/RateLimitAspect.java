package com.example.aop;

import cn.hutool.core.lang.UUID;
import com.example.annotation.RateLimiter;
import com.example.constant.RateLimiterType;
import com.example.exception.RateLimitException;
import com.example.utils.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 实现接口限流
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("lua/rollingRateLimiter.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Before("@annotation(rateLimiter)")
    public void before(JoinPoint jp, RateLimiter rateLimiter){
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        String key = getKey(jp, rateLimiter);
        // 当前时间
        String now = String.valueOf(System.currentTimeMillis());
        // 生成value值
        String uuid = UUID.fastUUID().toString();
        // 执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Arrays.asList(key, uuid, now),
                String.valueOf(time * 1000), String.valueOf(count)
        );
        if (result == null || result.intValue() != 1) {
            throw new RateLimitException("访问频繁，请稍后再试");
        }
    }

    /**
     * 生成key
     */
    private String getKey(JoinPoint jp, RateLimiter rateLimiter) {
        MethodSignature signature =(MethodSignature) jp.getSignature();
        Method method = signature.getMethod();

        StringBuilder key = new StringBuilder(rateLimiter.key());

        if(rateLimiter.limiterType() == RateLimiterType.IP){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            key.append(method.getDeclaringClass()).append(".").append(method.getName()).append(IPUtils.getIpRequest(request));
        }else {
            key.append(method.getDeclaringClass()).append(".").append(method.getName());
        }
        return key.toString();
    }

}
