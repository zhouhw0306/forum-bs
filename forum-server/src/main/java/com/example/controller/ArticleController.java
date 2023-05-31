package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.annotation.Authentication;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.Article;
import com.example.service.ArticleService;
import com.example.utils.SensitiveFilter;
import com.example.utils.UserUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 文章接口
 * @author 24668
 */
@RestController
@RequestMapping(value = "/articles")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Resource
    private SensitiveFilter sensitiveFilter;

    //id查询
    @GetMapping("/{id}")
    public Result findByKey(@PathVariable("id") String id) {
        return articleService.findById(id);
    }

    //微搜
    @GetMapping("/by/{word}")
    public Result byWord(@PathVariable("word") String word) {
        return articleService.searchByKey(word);
    }

    //添加或更新
    @PostMapping("/publish")
    @CacheEvict(value = "article", allEntries=true)
    public Result saveArticle(@RequestBody Article article) {
        if (!ObjectUtil.isEmpty(article.getUserId())){
            if (!ObjectUtil.equal(article.getUserId(),UserUtils.getCurrentUser())){
                Result.error(ResultCode.USER_NOT_LOGGED_IN);
            }
        }
        article.setContent(sensitiveFilter.filter(article.getContent()));
        String articleId = articleService.publishArticle(article);
        return Result.success(articleId);
    }

    //分页分类获取帖子
    @GetMapping("/getAll")
    @Cacheable(value = "article",keyGenerator = "keyGenerator")
    public Result listArticles(@RequestParam Integer pageNumber,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "false") Boolean isCareMe,
                               @RequestParam String sort,
                               @RequestParam(defaultValue = "-1") String index) {

        List<Article> articles = articleService.listArticles(pageNumber,pageSize,isCareMe,sort,index);
        return Result.success(articles);
    }

    //获取帖子数量
    @GetMapping("/getAllNOPage")
    public Result AllArticles() {
        return Result.success(articleService.count());
    }

    //添加文章阅读量
    @GetMapping("/addViewCount/{id}")
    public Result addViewCount(@PathVariable("id") String id){
        return articleService.addViewCount(id);
    }


    //获得热帖
    @GetMapping("/getHot")
    public Result getHot(){
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("order by comment_count desc,view_count desc limit 6");
        List<Article> list = articleService.list(queryWrapper);
        return Result.success(list);
    }

    //deleteById文章
    @Authentication
    @CacheEvict(value = "article", allEntries=true)
    @PostMapping("deleteById/{id}")
    public Result deleteById(@PathVariable String id){
        boolean flag = articleService.removeById(id);
        return flag ? Result.success() : Result.error();
    }

    /**
     * 基于用户的协同过滤算法
     */
    @GetMapping("getRecommend")
    public Result getRecommend(){
        List<Article> list = articleService.getRecommend();
        return Result.success(list);
    }
}
