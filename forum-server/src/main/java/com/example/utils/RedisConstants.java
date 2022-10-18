package com.example.utils;

/**
 * @author zhw
 * redis key常量前缀
 */
public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 1L;

    public static final String LOGIN_TOKEN_KEY = "login:token:";
    public static final Long LOGIN_TOKEN_TTL = 30L;
}
