package com.example.service;

import com.example.constant.Result;
import com.example.domain.dao.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 24668
* @description 针对表【tb_comment】的数据库操作Service
* @createDate 2022-09-29 14:20:56
*/
public interface CommentService extends IService<Comment> {

    Result addComm(Comment comment);

    Result deleteComment(String id, String level);

    Result getCommentsByAid(String articleId);

    Result getCommentAll();
}
