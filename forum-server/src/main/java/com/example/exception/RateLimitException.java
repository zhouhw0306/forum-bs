package com.example.exception;

/**
 * 接口被限流异常
 * @author zhw
 */
public class RateLimitException extends RuntimeException{

    private static final long serialVersionUID = -641613032938409190L;

    public RateLimitException(String message) {
        super(message);
    }
}

