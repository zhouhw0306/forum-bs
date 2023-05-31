package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.*;
import com.example.service.*;
import com.example.utils.UserUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.example.utils.RedisConstants.*;

/**
 * @author zhw
 */
@RestController
@RequestMapping("hasArticle")
public class ArticleHasfavourController {

    @Resource
    private ArticleHasfavourService articleHasfavourService;

    /**
     * 收藏操作
     */
    @PostMapping("favour")
    public Result favour(String targetId){
        return articleHasfavourService.favour(targetId);
    }

    /**
     * 是否收藏
     */
    @GetMapping("isFavour/{id}")
    public Result isFavour(@PathVariable String id){
        return articleHasfavourService.isFavour(id);
    }
}
