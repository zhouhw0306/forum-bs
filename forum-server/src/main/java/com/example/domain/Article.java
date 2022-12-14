package com.example.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章表
 * @TableName tb_article
 */
@TableName(value ="tb_article")
@Data
public class Article implements Serializable {

    /**
     * 文章ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类
     */
    private Integer categoryId;

    /**
     * 原始的正文内容
     */
    private String contentHtml;

    /**
     * 过滤渲染后的正文内容
     */
    private String content;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 阅读数量
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private List<Tag> tags;

    @TableField(exist = false)
    private static final long serialVersionUID = 7919374261730108718L;

}