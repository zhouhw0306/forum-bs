package com.example.controller.article;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.annotation.Authentication;
import com.example.annotation.RepeatSubmit;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.dao.Article;
import com.example.service.ArticleService;
import com.example.utils.SensitiveFilter;
import com.example.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 文章接口
 *
 * @author zhw
 */
@RestController
@RequestMapping(value = "/articles")
@Api(tags = "文章接口")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询文章")
    public Result<Article> findByKey(@PathVariable("id") String id) {
        return Result.success(articleService.findById(id));
    }

    @GetMapping("/by/{word}")
    @ApiOperation(value = "根据关键词搜索文章")
    public Result<Map<String, Object>> byWord(@PathVariable("word") String word) {
        if (ObjectUtil.isEmpty(word)) {
            return Result.success();
        }
        return Result.success(articleService.searchByKey(word));
    }

    @RepeatSubmit
    @PostMapping("/publish")
    @ApiOperation(value = "文章的新增或更新")
    @CacheEvict(value = "article", allEntries = true)
    public Result<String> saveArticle(@RequestBody @Valid Article article) {
        if (!ObjectUtil.equal(article.getUserId(), UserUtils.getCurrentUser())) {
            Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        article.setContent(sensitiveFilter.filter(article.getContent()));
        return articleService.publishArticle(article);
    }

    @GetMapping("/getAll")
    @ApiOperation(value = "分页分类获取文章")
    @Cacheable(value = "article", keyGenerator = "keyGenerator")
    public Result<List<Article>> listArticles(@RequestParam Integer pageNumber,
                                              @RequestParam Integer pageSize,
                                              @RequestParam(defaultValue = "false") Boolean isCareMe,
                                              @RequestParam String sort,
                                              @RequestParam(defaultValue = "-1") String index) {

        List<Article> articles = articleService.listArticles(pageNumber, pageSize, isCareMe, sort, index);
        return Result.success(articles);
    }

    @GetMapping("/getAllNOPage")
    @ApiOperation(value = "获取文章数量")
    public Result<Integer> AllArticles() {
        return Result.success(articleService.count());
    }

    @GetMapping("/addViewCount/{id}")
    @ApiOperation(value = "添加文章阅读量")
    public Result<Void> addViewCount(@PathVariable("id") String id) {
        articleService.addViewCount(id);
        return Result.success();
    }

    @GetMapping("/getHot")
    @ApiOperation(value = "获得热门文章")
    public Result<List<Article>> getHot() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("order by comment_count desc,view_count desc limit 6");
        List<Article> list = articleService.list(queryWrapper);
        return Result.success(list);
    }

    @Authentication
    @CacheEvict(value = "article", allEntries = true)
    @PostMapping("deleteById/{id}")
    @ApiOperation(value = "根据id删除文章")
    public Result<Void> deleteById(@PathVariable String id) {
        boolean flag = articleService.removeById(id);
        return flag ? Result.success() : Result.error();
    }

    @GetMapping("getRecommend")
    @ApiOperation(value = "基于用户的协同过滤算法，推荐文章")
    public Result<List<Article>> getRecommend() {
        List<Article> list = articleService.getRecommend();
        return Result.success(list);
    }

    @GetMapping("getBrowsingHistory")
    @ApiOperation(value = "获取浏览历史")
    public Result<List<Article>> getBrowsingHistory() {
        return Result.success(articleService.getBrowsingHistory());
    }
}
