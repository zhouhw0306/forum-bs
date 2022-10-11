package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.domain.Subscribe;
import com.example.service.SubscribeService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author zhw
 */
@RestController
@RequestMapping("subScribe")
public class SubScribeController {

    @Resource
    SubscribeService subscribeService;

    //判断关注关系
    @PostMapping("/isFollowAuthor")
    public Result isFollow(String userId, String authorId){
        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subscribe",userId);
        queryWrapper.eq("be_subscribe",authorId);
        List<Subscribe> list = subscribeService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)){
            return Result.success(false);
        }
        return Result.success(true);
    }

    //关注
    @PostMapping("/addFollow")
    public Result addFollow(String userId, String authorId) {
        boolean flag = subscribeService.save(new Subscribe(authorId, userId));
        return flag ? Result.success() : Result.error();
    }
    //移除关注
    @PostMapping("/removeFollow")
    public Result removeFollow(String userId, String authorId) {
        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subscribe",userId);
        queryWrapper.eq("be_subscribe",authorId);
        boolean flag = subscribeService.remove(queryWrapper);
        return flag ? Result.success() : Result.error();
    }
}
