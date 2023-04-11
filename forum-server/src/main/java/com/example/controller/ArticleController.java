package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.annotation.Authentication;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.Article;
import com.example.domain.Source;
import com.example.domain.Subscribe;
import com.example.domain.User;
import com.example.service.ArticleService;
import com.example.service.SourceService;
import com.example.service.SubscribeService;
import com.example.service.UserService;
import com.example.utils.SensitiveFilter;
import com.example.utils.UserUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


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
    private UserService userService;

    @Resource
    private SourceService sourceService;

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

    //微搜
    @GetMapping("/by/{word}")
    public Result byWord(@PathVariable("word") String word) {
        if (ObjectUtil.isEmpty(word)){
            return Result.success();
        }
        List<Article> list1 = articleService.query().select("id","title","content_html","comment_count","view_count","create_time").like("title",word).list();
        List<User> list2 = userService.query().select("id","username","avatar","introduction").like("username",word).list();
        List<Source> list3 = sourceService.query().like("title",word).list();
        Result result = new Result();
        Map<String, Object> simple = result.simple();
        simple.put("articleData",list1);
        simple.put("userData",list2);
        simple.put("sourceData",list3);
        return result;
    }

    //添加或更新
    @PostMapping("/publish")
    @CacheEvict(value = "article", allEntries=true)
    public Result saveArticle(@RequestBody Article article) {
        if (ObjectUtil.isEmpty(UserUtils.getCurrentUser())){
            Result.error(ResultCode.INTERFACE_FORBID_VISIT);
        }
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

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

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
            queryWrapper.in("user_id", beSubscribe);
        }
        if (!"-1".equals(index)){
            queryWrapper.eq("category_id",index);
        }
        if (0==pageNumber){
            queryWrapper.last("order by create_Time " + sort);
        }else {
            Integer skipTotal = (pageNumber-1)*pageSize;
            queryWrapper.last("order by create_Time " + sort + " limit " + skipTotal +","+pageSize);
        }
        List<Article> articles = articleService.list(queryWrapper);
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
}
