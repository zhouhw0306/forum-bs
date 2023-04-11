package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.domain.Article;
import com.example.domain.ArticleTagRelation;
import com.example.domain.Tag;
import com.example.mapper.ArticleTagRelationMapper;
import com.example.mapper.UserMapper;
import com.example.service.ArticleService;
import com.example.mapper.ArticleMapper;
import com.example.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
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
    UserMapper userMapper;

    @Resource
    ArticleTagRelationMapper articleTagRelationMapper;

    @Override
    @Transactional
    public String publishArticle(Article article) {
        //更新
        if(StringUtils.isNotBlank(article.getId())){
            articleMapper.updateById(article);
            List<Tag> tags = article.getTags();
            // 删除旧的文章-标签对应关系
            QueryWrapper<ArticleTagRelation> wrapper = new QueryWrapper<>();
            wrapper.eq("article_id",article.getId());
            articleTagRelationMapper.delete(wrapper);
            // 添加的文章-标签对应关系
            for (Tag tag : tags) {
                ArticleTagRelation atr = new ArticleTagRelation(article.getId(),tag.getId());
                articleTagRelationMapper.insert(atr);
            }
            return article.getId();
        }else{
            //添加
            article.setUserId(UserUtils.getCurrentUser());
            articleMapper.insert(article);
            String artId = article.getId();
            List<Tag> tags = article.getTags();
            for (Tag tag : tags) {
                ArticleTagRelation atr = new ArticleTagRelation(artId,tag.getId());
                articleTagRelationMapper.insert(atr);
            }
            userMapper.addScore(UserUtils.getCurrentUser(),2);
            return artId;
        }
    }

    @Override
    public Result addViewCount(String id) {
        articleMapper.addViewCount(id);
        return Result.success();
    }
}




