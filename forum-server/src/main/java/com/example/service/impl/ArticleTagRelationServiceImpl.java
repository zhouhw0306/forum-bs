package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.dao.ArticleTagRelation;
import com.example.service.ArticleTagRelationService;
import com.example.mapper.ArticleTagRelationMapper;
import org.springframework.stereotype.Service;

/**
* @author zhw
* @description 针对表【tb_article_tag_relation】的数据库操作Service实现
* @createDate 2022-09-28 14:45:19
*/
@Service
public class ArticleTagRelationServiceImpl extends ServiceImpl<ArticleTagRelationMapper, ArticleTagRelation>
    implements ArticleTagRelationService{

}




