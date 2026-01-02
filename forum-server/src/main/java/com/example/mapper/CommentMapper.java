package com.example.mapper;

import com.example.domain.dao.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zhw
*/
public interface CommentMapper extends BaseMapper<Comment> {

    int likeComment(String commentId);

    int disLikeComment(String commentId);
}




