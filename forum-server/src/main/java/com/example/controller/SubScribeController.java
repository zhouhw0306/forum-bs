package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.domain.Subscribe;
import com.example.service.SubscribeService;
import com.example.utils.UserUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

/**
 * 关注接口
 * @author zhw
 */
@RestController
@RequestMapping("subScribe")
public class SubScribeController {

    @Resource
    SubscribeService subscribeService;

    /**
     * 判断关注关系
     */
    @PostMapping("/isFollowAuthor")
    public Result isFollow(String authorId){
        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subscribe",UserUtils.getCurrentUser());
        queryWrapper.eq("be_subscribe",authorId);
        List<Subscribe> list = subscribeService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)){
            return Result.success(false);
        }
        return Result.success(true);
    }

    /**
     * 关注
     */
    @PostMapping("/addFollow")
    @CacheEvict(value = "article", allEntries=true)
    public Result addFollow(String authorId) {
        boolean flag = subscribeService.save(new Subscribe(authorId, UserUtils.getCurrentUser()));
        return flag ? Result.success() : Result.error();
    }

    /**
     * 移除关注
     */
    @PostMapping("/removeFollow")
    @CacheEvict(value = "article", allEntries=true)
    public Result removeFollow(String authorId) {
        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subscribe",UserUtils.getCurrentUser());
        queryWrapper.eq("be_subscribe",authorId);
        boolean flag = subscribeService.remove(queryWrapper);
        return flag ? Result.success() : Result.error();
    }

    /**
     * 获取所有粉丝
     */
    @GetMapping("/getFans")
    public Result getFans() {
        String id = UserUtils.getCurrentUser();
        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("be_subscribe",id);
        List<Subscribe> list = subscribeService.list(queryWrapper);
        return Result.success(list);
    }

    /**
     * 获取所有关注
     */
    @GetMapping("/getFollows")
    public Result getFollows() {
        String id = UserUtils.getCurrentUser();
        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subscribe",id);
        List<Subscribe> list = subscribeService.list(queryWrapper);
        return Result.success(list);
    }
}
