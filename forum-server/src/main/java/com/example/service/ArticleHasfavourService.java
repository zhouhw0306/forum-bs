package com.example.service;

import com.example.constant.Result;
import com.example.domain.dao.Article;
import com.example.domain.dao.ArticleHasfavour;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhw
 * @description 针对表【tb_article_hasfavour】的数据库操作Service
 * @createDate 2023-04-04 23:01:09
 */
public interface ArticleHasfavourService extends IService<ArticleHasfavour> {

    Result<Integer> favour(String targetId);

    Result<Boolean> isFavour(String id);

    Result<List<Article>> getHasFavour();
}
