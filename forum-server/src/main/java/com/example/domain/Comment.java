package com.example.domain;

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
     * 
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private String articleId;

    /**
     * 
     */
    private String authorId;

    /**
     * 
     */
    private String parentId;

    /**
     * 
     */
    private String toUid;

    /**
     * 
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