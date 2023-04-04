package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.*;
import com.example.service.*;
import com.example.utils.UserUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zhw
 */
@RestController
@RequestMapping("hasArticle")
public class ArticleHasfavourController {

    @Resource
    private ArticleHasfavourService articleHasfavourService;

    @Resource
    private UserService userService;

    /**
     * 收藏操作
     */
    @PostMapping("favour")
    public Result favour(String targetId){
        QueryWrapper<ArticleHasfavour> queryWrapper = new QueryWrapper<>();
        String currentUser = UserUtils.getCurrentUser();
        if (currentUser == null ){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        User user = userService.getById(currentUser);
        if (user == null){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        queryWrapper.eq("user_id",currentUser);
        queryWrapper.eq("article_id",targetId);
        ArticleHasfavour one = articleHasfavourService.getOne(queryWrapper);;
        if (one == null){
            //添加收藏关系
            articleHasfavourService.save(new ArticleHasfavour(currentUser,targetId));
            return Result.success(1);
        }else {
            //删除收藏关系
            articleHasfavourService.remove(queryWrapper);
            return Result.success(-1);
        }
    }

    /**
     * 是否收藏
     */
    @GetMapping("isFavour/{id}")
    public Result isFavour(@PathVariable String id){
        QueryWrapper<ArticleHasfavour> queryWrapper = new QueryWrapper<>();
        String currentUser = UserUtils.getCurrentUser();
        if (currentUser == null ){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        queryWrapper.eq("user_id",currentUser);
        queryWrapper.eq("article_id",id);
        ArticleHasfavour one = articleHasfavourService.getOne(queryWrapper);;
        return Result.success(one != null);
    }
}
