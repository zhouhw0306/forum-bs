package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.Article;
import com.example.domain.ArticleTagRelation;
import com.example.domain.Tag;
import com.example.mapper.ArticleTagRelationMapper;
import com.example.mapper.TagMapper;
import com.example.service.ArticleService;
import com.example.mapper.ArticleMapper;
import com.example.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 24668
* @description 针对表【tb_article(文章表)】的数据库操作Service实现
* @createDate 2022-09-23 19:32:15
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

    @Resource
    ArticleMapper articleMapper;

    @Resource
    ArticleTagRelationMapper articleTagRelationMapper;

    @Override
    @Transactional
    public String publishArticle(Article article) {
        //更新
        if(StringUtils.isNotBlank(article.getId())){
            articleMapper.updateById(article);
            return article.getId();
        }else{
            //添加
            article.setUserId(UserUtils.getCurrentUser());
            articleMapper.insert(article);
            String artId = article.getId();
            List<Tag> tags = article.getTags();
            for (Tag tag : tags) {
                ArticleTagRelation atr = new ArticleTagRelation(artId+"",tag.getId());
                articleTagRelationMapper.insert(atr);
            }
            return artId;
        }
    }
}




