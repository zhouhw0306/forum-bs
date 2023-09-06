package com.example.service;

import com.example.constant.Result;
import com.example.domain.dao.Article;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;


/**
* @author zhw
* @description 针对表【tb_article(文章表)】的数据库操作Service
* @createDate 2022-09-23 19:32:15
*/
public interface ArticleService extends IService<Article> {

    Result<String> publishArticle(Article article);

    void addViewCount(String id);

    Article findById(String id);

    Map<String, Object> searchByKey(String word);

    List<Article> getRecommend();

    List<Article> listArticles(Integer pageNumber, Integer pageSize, Boolean isCareMe, String sort, String index);

    List<Article> getBrowsingHistory();
}
