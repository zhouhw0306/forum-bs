package com.example.mapper;

import com.example.domain.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 24668
* @description 针对表【tb_article(文章表)】的数据库操作Mapper
* @createDate 2022-09-23 19:32:15
* @Entity com.example.domain.Article
*/
public interface ArticleMapper extends BaseMapper<Article> {

    void updateCommCount(String articleId);

    void addViewCount(String articleId);
}




