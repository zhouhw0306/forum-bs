package com.example.controller.article;

import com.example.annotation.Authentication;
import com.example.annotation.RepeatSubmit;
import com.example.constant.AuthConstant;
import com.example.constant.Result;
import com.example.domain.dao.Article;
import com.example.service.ArticleHasfavourService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    @RepeatSubmit
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "收藏操作")
    public Result<Integer> favour(String targetId) {
        return articleHasfavourService.favour(targetId);
    }

    @GetMapping("isFavour/{id}")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "判断登录用户是否收藏该文章")
    public Result<Boolean> isFavour(@PathVariable String id) {
        return articleHasfavourService.isFavour(id);
    }

    @GetMapping("getArticleHasFavour")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "获得登录用户的所有收藏文章信息")
    public Result<List<Article>> getHasFavour() {
        return articleHasfavourService.getHasFavour();
    }
}
