package com.example.aop;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 打印日志
 *
 * @author zhw
 */
@Aspect
@Slf4j
@Component
public class LogAspect {

    @Pointcut("execution(* com.example.controller..*(..))")
    public void LogPointcut() {
    }

    @Around("LogPointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        javax.servlet.http.HttpServletRequest request = attributes.getRequest();
        // 获取方法的返回结果
        Long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        Long endTime = System.currentTimeMillis();
        // 记录下请求内容
        log.info("\n------------接口日志  开始-------------" +
                "\n请求URI : " + request.getRequestURI() +
                "\n请求方法 : " + request.getMethod() +
                "\n请求耗时 : " + (endTime - startTime) + "ms" +
                "\n传入参数 : " + JSONUtil.toJsonStr(request.getParameterMap()) +
                "\n返回数据 : " + JSONUtil.toJsonStr(result) +
                "\n------------接口日志  结束-------------");
        return result;
    }

    @AfterThrowing(value = "LogPointcut()", throwing = "e")
    public void doThrow(JoinPoint joinPoint, Exception e) {
        Signature signature = joinPoint.getSignature();
        String name = signature.getName();
        log.info("异常信息：{}:{}", name, e.getMessage());
    }
}
