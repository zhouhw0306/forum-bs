package com.example.annotation;

import com.example.constant.AuthConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhw
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Authentication {

    /**
     *  true为启用验证
     *  false为跳过验证
     * @return
     */
    boolean pass() default true;

    AuthConstant role() default AuthConstant.ADMIN;

}
