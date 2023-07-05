package com.example.service;

import com.example.constant.Result;
import com.example.domain.dao.ArticleHasfavour;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 24668
* @description 针对表【tb_article_hasfavour】的数据库操作Service
* @createDate 2023-04-04 23:01:09
*/
public interface ArticleHasfavourService extends IService<ArticleHasfavour> {

    Result favour(String targetId);

    Result isFavour(String id);
}
