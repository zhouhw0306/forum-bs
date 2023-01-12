package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.domain.Comment;
import com.example.domain.User;
import com.example.mapper.ArticleMapper;
import com.example.mapper.UserMapper;
import com.example.service.CommentService;
import com.example.mapper.CommentMapper;
import com.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author 24668
* @description 针对表【tb_comment】的数据库操作Service实现
* @createDate 2022-09-29 14:20:56
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Resource
    CommentMapper commentMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    ArticleMapper articleMapper;

    @Override
    @Transactional
    public Result addComm(Comment comment) {
        commentMapper.insert(comment);
        //封装author对象
        Comment comment1 = commentMapper.selectById(comment.getId());
        User author = userMapper.selectById(comment1.getAuthorId());
        comment1.setUser(author);
        //封装toUser对象
        if ("2".equals(comment1.getLevel())){
            User toUser = userMapper.selectById(comment1.getToUid());
            comment1.setToUser(toUser);
        }
        articleMapper.updateCommCount(comment.getArticleId());
        return Result.success(comment1);
    }

    @Override
    @Transactional
    public Result deleteComment(String id, String level) {
        if ("0".equals(level)){
            remove(new QueryWrapper<Comment>().eq("parent_id", id));
            removeById(id);
        }
        if ("1".equals(level) || "2".equals(level)){
            removeById(id);
        }
        return Result.success();
    }
}




