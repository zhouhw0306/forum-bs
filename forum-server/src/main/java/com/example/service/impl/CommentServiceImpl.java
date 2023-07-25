package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.domain.dao.Comment;
import com.example.domain.dao.User;
import com.example.mapper.ArticleMapper;
import com.example.mapper.UserMapper;
import com.example.service.CommentService;
import com.example.mapper.CommentMapper;
import com.example.service.UserService;
import com.example.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhw
 * @description 针对表【tb_comment】的数据库操作Service实现
 * @createDate 2022-09-29 14:20:56
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Resource
    CommentMapper commentMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    UserService userService;

    @Resource
    ArticleMapper articleMapper;

    @Override
    @Transactional
    public Result addComm(Comment comment) {
        String authorId = UserUtils.getCurrentUser();
        comment.setAuthorId(authorId);
        commentMapper.insert(comment);
        //封装author对象
        Comment comment1 = commentMapper.selectById(comment.getId());
        User author = userMapper.selectById(authorId);
        author.setPassword("it's a secret");
        comment1.setUser(author);
        //封装toUser对象
        if ("2".equals(comment1.getLevel())) {
            User toUser = userMapper.selectById(comment1.getToUid());
            comment1.setToUser(toUser);
        }
        articleMapper.addCommCount(comment.getArticleId());
        return Result.success(comment1);
    }

    @Override
    @Transactional
    public Result deleteComment(String id, String level) {
        if ("0".equals(level)) {
            remove(new QueryWrapper<Comment>().eq("parent_id", id));
            removeById(id);
        }
        if ("1".equals(level) || "2".equals(level)) {
            removeById(id);
        }
        return Result.success();
    }

    @Override
    public Result getCommentsByAid(String articleId) {
        List<Comment> list = query().eq("article_id", articleId).eq("level", "0").orderByDesc("create_time").list();
        // 封装评论的user信息
        for (Comment comm : list) {
            // 封装评论作者信息
            List<User> list0 = userService.query().select("avatar", "username").eq("id", comm.getAuthorId()).list();
            if (list0 != null && list0.size() > 0) {
                comm.setUser(list0.get(0));
            }
            // 封装子评论信息
            List<Comment> childrens = pushChildrenComment(comm);
            comm.setChildrens(childrens);
        }

        return Result.success(list);
    }

    @Override
    public Result getCommentAll() {
        List<Comment> list = query().eq("level", 0).orderByDesc("create_time").list();
        // 封装评论的user信息
        for (Comment comm : list) {
            // 封装评论作者信息
            List<User> list0 = userService.query().select("avatar", "username").eq("id", comm.getAuthorId()).list();
            if (list0 != null && list0.size() > 0) {
                comm.setUser(list0.get(0));
            }
            // 封装子评论信息
            List<Comment> childrens = pushChildrenComment(comm);
            comm.setChildrens(childrens);
        }
        return Result.success(list);
    }


    /**
     * 封装子评论
     */
    public List<Comment> pushChildrenComment(Comment comm) {
        // 封装子评论
        List<Comment> childrens = query().eq("parent_id", comm.getId()).orderByDesc("create_time").list();
        // 封装子评论的user信息
        for (Comment children : childrens) {
            List<User> list1 = userService.query().select("avatar", "username").eq("id", children.getAuthorId()).list();
            if (list1 != null && list1.size() > 0) {
                children.setUser(list1.get(0));
            }
            if (children.getLevel().equals("2")) {
                List<User> list2 = userService.query().select("avatar", "username").eq("id", children.getToUid()).list();
                if (list2 != null && list2.size() > 0) {
                    children.setToUser(list2.get(0));
                }
            }
        }
        return childrens;
    }
}




