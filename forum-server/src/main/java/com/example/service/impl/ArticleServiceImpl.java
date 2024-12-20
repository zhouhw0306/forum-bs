package com.example.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.dao.*;
import com.example.mapper.ArticleTagRelationMapper;
import com.example.mapper.UserMapper;
import com.example.search.ArticleSearchService;
import com.example.service.ArticleService;
import com.example.mapper.ArticleMapper;
import com.example.service.SourceService;
import com.example.service.SubscribeService;
import com.example.service.UserService;
import com.example.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.*;

import static com.example.utils.RedisConstants.FAVOUR_ART_KEY;
import static com.example.utils.RedisConstants.VIEW_ART_KEY;

/**
* @author zhw
* @description 针对表【tb_article(文章表)】的数据库操作Service实现
* @createDate 2022-09-23 19:32:15
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private SourceService sourceService;

    @Resource
    private ArticleTagRelationMapper articleTagRelationMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SubscribeService subscribeService;

    @Resource
    @Lazy
    ArticleSearchService articleSearchService;

    @Override
    @Transactional
    public Result<String> publishArticle(Article article) {
        //更新
        if (StringUtils.isNotBlank(article.getId())) {
            Article article1 = articleMapper.selectOne(Wrappers.lambdaQuery(Article.class).eq(Article::getId, article.getId()));
            if (ObjectUtil.isEmpty(article1)) {
                return Result.error(ResultCode.ERROR, "文章不存在");
            }
            if (!Objects.equals(UserUtils.getCurrentUser(), article1.getUserId())) {
                return Result.error(ResultCode.ERROR, "非法修改");
            }
            articleMapper.updateById(article);
            List<Tag> tags = article.getTags();
            // 删除旧的文章-标签对应关系
            QueryWrapper<ArticleTagRelation> wrapper = new QueryWrapper<>();
            wrapper.eq("article_id", article.getId());
            articleTagRelationMapper.delete(wrapper);
            // 添加的文章-标签对应关系
            for (Tag tag : tags) {
                ArticleTagRelation atr = ArticleTagRelation.builder().articleId(article.getId()).tagId(tag.getId()).build();
                articleTagRelationMapper.insert(atr);
            }
            return Result.success(article.getId());
        }else{
            //添加
            article.setUserId(UserUtils.getCurrentUser());
            articleMapper.insert(article);
            String artId = article.getId();
            List<Tag> tags = article.getTags();
            for (Tag tag : tags) {
                ArticleTagRelation atr = ArticleTagRelation.builder().articleId(artId).tagId(tag.getId()).build();
                articleTagRelationMapper.insert(atr);
            }
            userMapper.addScore(UserUtils.getCurrentUser(),2);
            return Result.success(artId);
        }
    }

    @Override
    public void addViewCount(String id) {
        articleMapper.addViewCount(id);
        // 如登录加入到该用户的浏览历史表
        if (StringUtils.isNotBlank(UserUtils.getCurrentUser())) {
            stringRedisTemplate.opsForSet().add(VIEW_ART_KEY + UserUtils.getCurrentUser(), id);
        }
    }

    @Override
    public Article findById(String id) {
        return getById(id);
    }

    @Override
    public Map<String, Object> searchByKey(String word) {
        List<Article> list1 = articleSearchService.searchByKey(word);
        List<User> list2 = userService.query().select("id", "username", "avatar", "introduction").like("username", word).list();
        List<Source> list3 = sourceService.query().like("title", word).list();
        list3.forEach(v -> {
            String title = v.getTitle();
            String replace = title.replace(word, "<em>" + word + "</em>");
            v.setTitle(replace);
        });
        Map<String, Object> simple = new HashMap<>(3);
        simple.put("articleData", list1);
        simple.put("userData", list2);
        simple.put("sourceData", list3);
        return simple;
    }

    @Override
    public List<Article> getRecommend() {
        String id = UserUtils.getCurrentUser();
        // 未登录推默认
        if (ObjectUtil.isEmpty(id)) {
            List<Article> list = query().list();
            return list.subList(0, Math.min(list.size(), 5));
        }
        Set<String> set = new HashSet<>();
        // 查询所有关注者article集合
        List<Subscribe> subscribes = subscribeService.list(new QueryWrapper<Subscribe>().eq("subscribe", id));
        for (Subscribe subscribe : subscribes) {
            String beSubscribe = subscribe.getBeSubscribe();
            Set<String> members = stringRedisTemplate.opsForSet().members(FAVOUR_ART_KEY + beSubscribe);
            if (members != null){
                set.addAll(members);
            }
        }
        // 过滤掉自己所浏览过的
        Set<String> me = stringRedisTemplate.opsForSet().members(VIEW_ART_KEY + id);
        for (String artId : me) {
            set.remove(artId);
        }
        if (CollectionUtils.isEmpty(set)){
            List<Article> list = query().notIn(!CollectionUtils.isEmpty(me),"id",me).list();
            return list.subList(0, Math.min(list.size(), 5));
        }
        return query().in("id", set).list().subList(0, Math.min(set.size(), 5));
    }

    @Override
    public List<Article> listArticles(Integer pageNumber, Integer pageSize, Boolean isCareMe, String sort, String index) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        //查关注的
        if (isCareMe!=null && isCareMe){
            //获得所有关注者
            String userId = UserUtils.getCurrentUser();
            List<Subscribe> list = subscribeService.query().eq("subscribe",userId).list();
            if (CollectionUtils.isEmpty(list)){
                return null;
            }
            String[] beSubscribe = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                beSubscribe[i] = list.get(i).getBeSubscribe();
            }
            queryWrapper.in("user_id", beSubscribe);
        }
        if (!"-1".equals(index)){
            queryWrapper.eq("category_id",index);
        }
        if (0==pageNumber){
            queryWrapper.last("order by create_Time " + sort);
        }else {
            int skipTotal = (pageNumber-1)*pageSize;
            queryWrapper.last("order by create_Time " + sort + " limit " + skipTotal +","+pageSize);
        }
        List<Article> articles = list(queryWrapper);
        return articles;
    }

    @Override
    public List<Article> getBrowsingHistory() {
        Set<String> articles = stringRedisTemplate.opsForSet().members(VIEW_ART_KEY + UserUtils.getCurrentUser());
        if (CollectionUtils.isEmpty(articles)) {
            return new ArrayList<>();
        }
        return lambdaQuery().in(Article::getId, articles).list();
    }
}




