package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.Article;
import com.example.domain.Subscribe;
import com.example.domain.User;
import com.example.service.ArticleService;
import com.example.service.SubscribeService;
import com.example.utils.SensitiveFilter;
import com.example.utils.UserUtils;
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

    //添加或更新
    @PostMapping("/publish")
    public Result saveArticle(@RequestBody Article article) {
        article.setContent(sensitiveFilter.filter(article.getContent()));
        String articleId = articleService.publishArticle(article);

        Result r = Result.success();
        r.simple().put("articleId", articleId);
        return r;
    }
    //获得全部根据创建时间排序
    @GetMapping("/getAll")
    public Result listArticles(@RequestParam Integer pageNumber,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "false") Boolean isCareMe,
                               @RequestParam String sort) {

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        Integer skipTotal = (pageNumber-1)*pageSize;
        //查关注的
        System.out.println(subscribeService);
        if (isCareMe!=null && isCareMe){
            //获得所有关注者
            String userId = UserUtils.getCurrentUser();
            System.out.println(userId+"***");
            QueryWrapper<Subscribe> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("subscribe",userId);
            List<Subscribe> list = subscribeService.list(queryWrapper1);
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
        //IPage<Article> page = new Page<>(pageNumber,pageSize);
        //IPage<Article> iPage = articleService.page(page,queryWrapper);
        //System.out.println("记录数"+iPage.getTotal());
        //List<Article> articles = iPage.getRecords();
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
}
