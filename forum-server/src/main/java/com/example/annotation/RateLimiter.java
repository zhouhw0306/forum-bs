package com.example.annotation;

import com.example.constant.RateLimiterType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author zhw
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiter {

    /**
     * 限流前缀
     */
    String key() default "redis_limiter:";

    /**
     * 限流时间窗
     */
    int time() default 10;

    /**
     * 时间窗内的限流次数
     */
    int count() default 100;

    /**
     * 限流类型
     */
    RateLimiterType limiterType() default RateLimiterType.DEFAULT;

}

