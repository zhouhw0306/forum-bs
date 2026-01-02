package com.example.service;

import com.example.domain.dao.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhw
 */
public interface CommentService extends IService<Comment> {

    Comment addComm(Comment comment);

    void deleteComment(String id, String level);

    List<Comment> getCommentsByAid(String articleId);

    List<Comment> getCommentAll();

    Integer likeComment(String commentId, String type);

    List<String> allLikeCommonId();
}
