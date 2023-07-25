package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.domain.dao.ArticleTagRelation;
import com.example.domain.dao.Tag;
import com.example.service.ArticleTagRelationService;
import com.example.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhw
 */
@RestController
@RequestMapping(value = "/tag")
@Api(tags = "标签接口")
public class TagController {

    @Autowired
    private TagService tagService;

    @Resource
    private ArticleTagRelationService articleTagRelationService;

    @GetMapping("/getTagAll")
    @ApiOperation(value = "获取所有标签")
    public Result listTags() {
        List<Tag> tags = tagService.list();
        return Result.success(tags);
    }

    @GetMapping("/getByArticleId")
    @ApiOperation(value = "获取指定文章的标签")
    public Result getByArticleId(String articleId){

        List<ArticleTagRelation> list = articleTagRelationService.lambdaQuery()
                .eq(ArticleTagRelation::getArticleId,articleId)
                .list();

        List<Tag> tagList = list.stream().map(
                atr -> tagService.getById(atr.getTagId())
        ).collect(Collectors.toList());

        return Result.success(tagList);
    }
}