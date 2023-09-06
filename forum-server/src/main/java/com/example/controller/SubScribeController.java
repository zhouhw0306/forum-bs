package com.example.controller;

import com.example.annotation.RepeatSubmit;
import com.example.constant.Result;
import com.example.domain.dao.Subscribe;
import com.example.service.SubscribeService;
import com.example.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhw
 */
@RestController
@RequestMapping("subScribe")
@Api(tags = "用户关注关系接口")
public class SubScribeController {

    @Resource
    SubscribeService subscribeService;

    @PostMapping("/isFollowAuthor")
    @ApiOperation(value = "判断与登录用户的关注关系")
    public Result<Boolean> isFollow(String authorId) {
        Subscribe list = subscribeService.lambdaQuery()
                .eq(Subscribe::getSubscribe, UserUtils.getCurrentUser())
                .eq(Subscribe::getBeSubscribe, authorId).one();
        return Result.success(list != null);
    }

    @PostMapping("/addFollow")
    @RepeatSubmit
    @CacheEvict(value = "article", allEntries = true)
    @ApiOperation(value = "关注指定用户")
    public Result<Void> addFollow(String authorId) {
        boolean flag = subscribeService.save(Subscribe.builder().beSubscribe(authorId).subscribe(UserUtils.getCurrentUser()).build());
        return flag ? Result.success() : Result.error();
    }

    @PostMapping("/removeFollow")
    @RepeatSubmit
    @CacheEvict(value = "article", allEntries = true)
    @ApiOperation(value = "移除关注")
    public Result<Void> removeFollow(String authorId) {
        boolean flag = subscribeService.lambdaUpdate()
                .eq(Subscribe::getSubscribe, UserUtils.getCurrentUser())
                .eq(Subscribe::getBeSubscribe, authorId)
                .remove();
        return flag ? Result.success() : Result.error();
    }

    @GetMapping("/getFans")
    @ApiOperation(value = "获取所有粉丝")
    public Result<List<Subscribe>> getFans() {
        List<Subscribe> list = subscribeService.lambdaQuery()
                .eq(Subscribe::getBeSubscribe, UserUtils.getCurrentUser())
                .list();
        return Result.success(list);
    }

    @GetMapping("/getFollows")
    @ApiOperation(value = "获取关注列表")
    public Result<List<Subscribe>> getFollows() {
        List<Subscribe> list = subscribeService.lambdaQuery()
                .eq(Subscribe::getSubscribe, UserUtils.getCurrentUser())
                .list();
        return Result.success(list);
    }
}
