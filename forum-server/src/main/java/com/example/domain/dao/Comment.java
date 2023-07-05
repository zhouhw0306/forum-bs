package com.example.domain.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author zhou
 * @TableName tb_comment
 */
@TableName(value ="tb_comment")
@Data
public class Comment implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 所属帖子id
     */
    private String articleId;

    /**
     * 作者id
     */
    private String authorId;

    /**
     * 父级评论id(1,2级评论才有该字段)
     */
    private String parentId;

    /**
     * toUid(2级评论才有该字段)
     */
    private String toUid;

    /**
     * (0,1,2)[0级为最高级评论]
     */
    private String level;

    /**
     * 评论者信息
     */
    @TableField(exist = false)
    private User user;

    /**
     * 子评论
     */
    @TableField(exist = false)
    private List<Comment> childrens;

    /**
     * 评论的回复对象user
     */
    @TableField(exist = false)
    private User toUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 6663138067522613942L;
}