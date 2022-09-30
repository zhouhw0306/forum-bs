package com.example.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.domain.User;
import com.example.service.UserService;
import com.example.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author zhw
 * 验证token
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
