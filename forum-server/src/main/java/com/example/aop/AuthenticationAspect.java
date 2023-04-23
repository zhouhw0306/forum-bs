package com.example.aop;

import com.example.annotation.Authentication;
import com.example.constant.AuthConstant;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.User;
import com.example.mapper.UserMapper;
import com.example.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 使用aop完成API请求时的认证和权限
 * made by Jason. Completed by mrruan
 * @author zhw
 */
@Slf4j
@Aspect
@Component
public class AuthenticationAspect {

    @Resource
    private UserMapper userMapper;

    @Pointcut(value = "@annotation(com.example.annotation.Authentication)")
    public void pointcut() {}


    @Around("pointcut() && @annotation(authentication)")
    public Object interceptor(ProceedingJoinPoint proceedingJoinPoint,Authentication authentication) {

        //是否开启验证
        boolean pass = authentication.pass();
        //需要的身份
        AuthConstant role = authentication.role();
        if (!pass || role == AuthConstant.ANON) {
            try {
                return proceedingJoinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return Result.error();
            }
        }
        //获得token
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (token == null){
            return Result.error(ResultCode.PERMISSION_NO_ACCESS);
        }
        AuthConstant realRole = authenticate(token);
        //要管理验证权限
        if (role == AuthConstant.ADMIN){
            if (realRole == AuthConstant.ADMIN){
                try {
                    return proceedingJoinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return Result.error();
                }
            }else {
                return Result.error(ResultCode.PERMISSION_NO_ACCESS);
            }
        }
        //要用户权限
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return Result.error();
        }
    }

    /**
     * 这个方法用于判断该token所属的到底是谁(管理员？ 用户？ 匿名？)
     * @param token
     */
    private AuthConstant authenticate(String token){
        String userId = JWTUtil.getUserId(token);
        if(userId == null){
            return AuthConstant.ANON;
        }
        User user = userMapper.selectById(userId);
        if (user == null || user.getRole()==null){
            return AuthConstant.ANON;
        }
        if (user.getRole().equals(AuthConstant.ADMIN.toString())){
            return AuthConstant.ADMIN;
        }else if (user.getRole().equals(AuthConstant.USER.toString())){
            return AuthConstant.USER;
        }
        return AuthConstant.ANON;
    }
}
