package com.example.controller;

import com.example.annotation.Authentication;
import com.example.annotation.RepeatSubmit;
import com.example.constant.AuthConstant;
import com.example.constant.Result;
import com.example.domain.dao.Comment;
import com.example.service.CommentService;
import com.example.utils.SensitiveFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhw
 */
@RestController
@RequestMapping("/comment")
@Api(tags = "评论接口")
public class CommentController {

    @Resource
    CommentService commentService;

    @Resource
    SensitiveFilter sensitiveFilter;

    @GetMapping("/getCommentsByArticle")
    @ApiOperation(value = "加载模块下所有评论")
    public Result getCommentsByArticle(String articleId) {
        return commentService.getCommentsByAid(articleId);
    }

    @PostMapping("/pushComment")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "添加评论")
    @RepeatSubmit
    public Result pushComment(Comment comment) {
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        return commentService.addComm(comment);
    }

    @Authentication
    @PostMapping("/getCommentAll")
    @ApiOperation(value = "获取系统所有评论")
    public Result getCommentAll() {
        return commentService.getCommentAll();
    }

    @Authentication
    @PostMapping("/delete")
    @ApiOperation(value = "删除评论")
    public Result getCommentAll(String id, String level) {
        return commentService.deleteComment(id, level);
    }
}
