package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.domain.dao.Subscribe;
import com.example.service.SubscribeService;
import com.example.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.util.CollectionUtils;
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

    @PostMapping("/addFollow")
    @CacheEvict(value = "article", allEntries=true)
    @ApiOperation(value = "关注指定用户")
    public Result addFollow(String authorId) {
        boolean flag = subscribeService.save(Subscribe.builder().beSubscribe(authorId).subscribe(UserUtils.getCurrentUser()).build());
        return flag ? Result.success() : Result.error();
    }

    @PostMapping("/removeFollow")
    @CacheEvict(value = "article", allEntries=true)
    @ApiOperation(value = "移除关注")
    public Result removeFollow(String authorId) {
        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subscribe",UserUtils.getCurrentUser());
        queryWrapper.eq("be_subscribe",authorId);
        boolean flag = subscribeService.remove(queryWrapper);
        return flag ? Result.success() : Result.error();
    }

    @GetMapping("/getFans")
    @ApiOperation(value = "获取所有粉丝")
    public Result getFans() {
        String id = UserUtils.getCurrentUser();
        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("be_subscribe",id);
        List<Subscribe> list = subscribeService.list(queryWrapper);
        return Result.success(list);
    }

    @GetMapping("/getFollows")
    @ApiOperation(value = "获取关注列表")
    public Result getFollows() {
        String id = UserUtils.getCurrentUser();
        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subscribe",id);
        List<Subscribe> list = subscribeService.list(queryWrapper);
        return Result.success(list);
    }
}
