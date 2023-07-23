package com.example.controller.article;

import com.example.constant.Result;
import com.example.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * @author zhw
 */
@RestController
@RequestMapping("hasArticle")
@Api(tags = "文章收藏操作接口")
public class ArticleHasfavourController {

    @Resource
    private ArticleHasfavourService articleHasfavourService;

    @PostMapping("favour")
    @ApiOperation(value = "收藏操作")
    public Result favour(String targetId){
        return articleHasfavourService.favour(targetId);
    }

    @GetMapping("isFavour/{id}")
    @ApiOperation(value = "判断登录用户是否收藏该文章")
    public Result isFavour(@PathVariable String id){
        return articleHasfavourService.isFavour(id);
    }

    @GetMapping("getHasFavour")
    @ApiOperation(value = "获得登录用户的所有收藏文章信息")
    public Result getHasFavour(){
        return articleHasfavourService.getHasFavour();
    }
}
