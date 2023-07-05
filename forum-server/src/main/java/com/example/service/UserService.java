package com.example.service;

import com.example.constant.Result;
import com.example.domain.dao.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 24668
* @description 针对表【tb_user】的数据库操作Service
* @createDate 2022-09-27 15:26:49
*/
public interface UserService extends IService<User> {

    /**
     * 登录
     */
    Result login(String username, String password);

    /**
     * 注册
     */
    Result signUp(HttpServletRequest req);
}
