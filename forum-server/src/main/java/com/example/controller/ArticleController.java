package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.Article;
import com.example.service.ArticleService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;


/**
 * @author 24668
 */
@RestController
@RequestMapping(value = "/articles")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    //id查询
    @GetMapping("/{id}")
    public Result findByKey(@PathVariable("id") String id) {
        Result r = new Result();

        if (null == id) {
            r.setResultCode(ResultCode.PARAM_IS_BLANK);
            return r;
        }

        Article article = articleService.getById(id);

        r.setResultCode(ResultCode.SUCCESS);
        r.setData(article);
        return r;
    }

    //添加或更新
    @PostMapping("/publish")
    public Result saveArticle(@RequestBody Article article) {

        String articleId = articleService.publishArticle(article);

        Result r = Result.success();
        r.simple().put("articleId", articleId);
        return r;
    }
    //获得全部
    @GetMapping("/getAll")
    public Result listArticles(@RequestParam Integer pageNumber,
                               @RequestParam Integer pageSize,
                               @RequestParam String sort) {

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        Integer skipTotal = (pageNumber-1)*pageSize;
        queryWrapper.last("order by create_Time "+sort + " limit " + skipTotal +","+pageSize);
        //IPage<Article> page = new Page<>(pageNumber,pageSize);
        //IPage<Article> iPage = articleService.page(page,queryWrapper);
        //System.out.println("记录数"+iPage.getTotal());
        //List<Article> articles = iPage.getRecords();
        List<Article> articles = articleService.list(queryWrapper);

        return Result.success(articles);
    }
}
