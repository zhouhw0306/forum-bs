package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.domain.Comment;
import com.example.domain.User;
import com.example.service.CommentService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhou
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    CommentService commentService;

    @Resource
    UserService userService;

    /**
     *  加载文章评论
     */
    @GetMapping("/getCommentsByArticle")
    public Result getCommentsByArticle(String articleId){

        QueryWrapper<Comment> queryWrapper = new QueryWrapper();
        queryWrapper.eq("article_id",articleId);
        queryWrapper.eq("level","0");
        queryWrapper.last("order by create_Time desc");
        List<Comment> list = commentService.list(queryWrapper);
        // 封装评论的user信息
        for (Comment comm : list) {
            User user = userService.getById(comm.getAuthorId());
            comm.setUser(user);
            // 封装子评论
            QueryWrapper<Comment> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("parent_id",comm.getId());
            queryWrapper1.last("order by create_Time desc");
            List<Comment> childrens = commentService.list(queryWrapper1);
            // 封装子评论的user信息
            for (Comment children : childrens) {
                User user1 = userService.getById(children.getAuthorId());
                children.setUser(user1);
                if (children.getLevel().equals("2")){
                    User user2 = userService.getById(children.getToUid());
                    children.setToUser(user2);
                }
            }
            comm.setChildrens(childrens);
        }

        return Result.success(list);
    }

    /**
     * 添加评论
     */
    @PostMapping("/pushComment")
    public Result pushComment(Comment comment){

        return commentService.addComm(comment);
    }
}
