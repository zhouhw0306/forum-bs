package com.example.service;

import com.example.constant.Result;
import com.example.domain.Article;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
* @author 24668
* @description 针对表【tb_article(文章表)】的数据库操作Service
* @createDate 2022-09-23 19:32:15
*/
public interface ArticleService extends IService<Article> {

    String publishArticle(Article article);

    Result addViewCount(String id);

    Result findById(String id);

    Result searchByKey(String word);

    List<Article> getRecommend();

    List<Article> listArticles(Integer pageNumber, Integer pageSize, Boolean isCareMe, String sort, String index);
}
