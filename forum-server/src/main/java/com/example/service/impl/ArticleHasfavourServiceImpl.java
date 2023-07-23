package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.dao.Article;
import com.example.domain.dao.ArticleHasfavour;
import com.example.domain.dao.User;
import com.example.service.ArticleHasfavourService;
import com.example.mapper.ArticleHasfavourMapper;
import com.example.service.ArticleService;
import com.example.service.UserService;
import com.example.utils.UserUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.utils.RedisConstants.FAVOUR_ART_KEY;

/**
* @author zhw
* @description 针对表【tb_article_hasfavour】的数据库操作Service实现
* @createDate 2023-04-04 23:01:09
*/
@Service
public class ArticleHasfavourServiceImpl extends ServiceImpl<ArticleHasfavourMapper, ArticleHasfavour>
    implements ArticleHasfavourService{

    @Resource
    private UserService userService;

    @Resource
    private ArticleService articleService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 收藏操作
     */
    @Override
    public Result favour(String targetId) {
        QueryWrapper<ArticleHasfavour> queryWrapper = new QueryWrapper<>();
        String currentUser = UserUtils.getCurrentUser();
        if (currentUser == null ){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        User user = userService.getById(currentUser);
        if (user == null){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        queryWrapper.eq("user_id",currentUser);
        queryWrapper.eq("article_id",targetId);
        ArticleHasfavour one = getOne(queryWrapper);;
        if (one == null){
            //添加收藏关系
            save(ArticleHasfavour.builder().userId(currentUser).articleId(targetId).build());
            stringRedisTemplate.opsForSet().add(FAVOUR_ART_KEY + currentUser, targetId);
            return Result.success(1);
        }else {
            //删除收藏关系
            remove(queryWrapper);
            stringRedisTemplate.opsForSet().remove(FAVOUR_ART_KEY + currentUser, targetId);
            return Result.success(-1);
        }
    }

    @Override
    public Result isFavour(String id) {
        QueryWrapper<ArticleHasfavour> queryWrapper = new QueryWrapper<>();
        String currentUser = UserUtils.getCurrentUser();
        if (currentUser == null ){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        queryWrapper.eq("user_id",currentUser);
        queryWrapper.eq("article_id",id);
        ArticleHasfavour one = getOne(queryWrapper);;
        return Result.success(one != null);
    }

    @Override
    public Result getHasFavour() {
        String userId = UserUtils.getCurrentUser();
        if (userId == null){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        List<ArticleHasfavour> favourList = lambdaQuery().eq(ArticleHasfavour::getUserId, userId).list();
        List<Article> articleList = favourList.stream().map(articleHasfavour ->
                articleService.lambdaQuery()
                        .select(Article::getId, Article::getTitle)
                        .eq(Article::getId, articleHasfavour.getArticleId())
                        .one()
        ).collect(Collectors.toList());
        return Result.success(articleList);
    }
}




