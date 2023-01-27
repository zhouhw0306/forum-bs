package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.annotation.Authentication;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.Article;
import com.example.domain.Subscribe;
import com.example.domain.User;
import com.example.service.ArticleService;
import com.example.service.SubscribeService;
import com.example.utils.SensitiveFilter;
import com.example.utils.UserUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;
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
    private SubscribeService subscribeService;

    @Resource
    private SensitiveFilter sensitiveFilter;

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

    //模糊查询
    @GetMapping("/by/{word}")
    public Result byWord(@PathVariable("word") String word) {
        if (ObjectUtil.isEmpty(word)){
            return Result.success();
        }
        List<Article> list = articleService.query().like("title",word).list();
        return Result.success(list);
    }

    //添加或更新
    @PostMapping("/publish")
    @CacheEvict(value = "article", allEntries=true)
    public Result saveArticle(@RequestBody Article article) {
        article.setContent(sensitiveFilter.filter(article.getContent()));
        String articleId = articleService.publishArticle(article);
        return Result.success(articleId);
    }

    //获得全部根据创建时间排序
    @GetMapping("/getAll")
    @Cacheable(value = "article",keyGenerator = "keyGenerator")
    public Result listArticles(@RequestParam Integer pageNumber,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "false") Boolean isCareMe,
                               @RequestParam String sort) {

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        Integer skipTotal = (pageNumber-1)*pageSize;
        //查关注的
        if (isCareMe!=null && isCareMe){
            //获得所有关注者
            String userId = UserUtils.getCurrentUser();
            List<Subscribe> list = subscribeService.query().eq("subscribe",userId).list();
            if (CollectionUtils.isEmpty(list)){
                return Result.success();
            }
            String[] beSubscribe = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                beSubscribe[i] = list.get(i).getBeSubscribe();
            }
            queryWrapper.in("user_id",beSubscribe);
        }
        queryWrapper.last("order by create_Time "+sort + " limit " + skipTotal +","+pageSize);
        List<Article> articles = articleService.list(queryWrapper);
        return Result.success(articles);
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
}
