package com.example.controller;

import com.example.annotation.Authentication;
import com.example.constant.Result;
import com.example.domain.Comment;
import com.example.service.CommentService;
import com.example.utils.SensitiveFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 评论接口
 *
 * @author zhou
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    CommentService commentService;

    @Resource
    SensitiveFilter sensitiveFilter;

    /**
     * 加载文章评论
     */
    @GetMapping("/getCommentsByArticle")
    public Result getCommentsByArticle(String articleId) {
        return commentService.getCommentsByAid(articleId);
    }

    /**
     * 添加评论
     */
    @PostMapping("/pushComment")
    public Result pushComment(Comment comment) {
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        return commentService.addComm(comment);
    }

    /**
     * 获取所有评论
     */
    @Authentication
    @PostMapping("/getCommentAll")
    public Result getCommentAll() {
        return commentService.getCommentAll();
    }

    /**
     * 删除评论
     */
    @Authentication
    @PostMapping("/delete")
    public Result getCommentAll(String id, String level) {
        return commentService.deleteComment(id, level);
    }
}
