package com.example.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.domain.dao.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author zhw
* @description 针对表【tb_article(文章表)】的数据库操作Mapper
* @createDate 2022-09-23 19:32:15
* @Entity com.example.domain.dao.Article
*/
public interface ArticleMapper extends BaseMapper<Article> {

    @Update("UPDATE tb_article SET comment_count = comment_count + 1 WHERE id=#{articleId}")
    void addCommCount(String articleId);

    @Update("UPDATE tb_article SET view_count = view_count + 1 WHERE id=#{articleId}")
    void addViewCount(String articleId);

    List<Article> selectAll(IPage page);
}




