package com.example.config;

import cn.hutool.core.util.StrUtil;
import com.example.domain.User;
import com.example.service.UserService;
import com.example.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhw
 * 验证token
 */
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        log.info("拦截请求{}",request.getRequestURI());
        String token = request.getHeader("token");
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)){
            return true;
        }

        //执行验证
        if (StrUtil.isBlank(token)){
            response.setStatus(401);
            return false;
        }
        //验证token合法
        String userId = JWTUtil.getUserId(token);
        if (userId==null){
            response.setStatus(401);
            return false;
        }

        // 根据token中的userid查询数据库
        User user = userService.getById(userId);
        if (user == null){
            response.setStatus(401);
            return false;
        }

        // 用户id + 密码 + token 验证
        boolean flag = JWTUtil.verify(token, userId, user.getPassword());

        return flag;
    }

}
