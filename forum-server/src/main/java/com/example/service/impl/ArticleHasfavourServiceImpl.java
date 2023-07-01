package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.ArticleHasfavour;
import com.example.domain.User;
import com.example.service.ArticleHasfavourService;
import com.example.mapper.ArticleHasfavourMapper;
import com.example.service.UserService;
import com.example.utils.UserUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import static com.example.utils.RedisConstants.FAVOUR_ART_KEY;

/**
* @author 24668
* @description 针对表【tb_article_hasfavour】的数据库操作Service实现
* @createDate 2023-04-04 23:01:09
*/
@Service
public class ArticleHasfavourServiceImpl extends ServiceImpl<ArticleHasfavourMapper, ArticleHasfavour>
    implements ArticleHasfavourService{

    @Resource
    private UserService userService;

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
}




