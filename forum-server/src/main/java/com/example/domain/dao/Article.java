package com.example.domain.dao;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * 文章表
 * @TableName tb_article
 */
@TableName(value ="tb_article")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article implements Serializable {

    /**
     * 文章ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 作者ID
     */
    private String userId;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 分类
     */
    @NotNull(message = "类型不能为空")
    private Integer categoryId;

    /**
     * 原始的正文内容
     */
    @NotBlank(message = "正文不能为空")
    private String contentHtml;

    /**
     * 过滤渲染后的正文内容
     */
    @NotBlank(message = "正文不能为空")
    private String content;

    /**
     * 评论数量
     */
    @Null
    private Integer commentCount;

    /**
     * 阅读数量
     */
    @Null
    private Integer viewCount;

    /**
     * 创建时间
     */
    @Null
    private Date createTime;

    /**
     * 更新时间
     */
    @Null
    private Date updateTime;

    /**
     * 删除标识
     */
    @TableLogic(value = "0",delval = "1")
    private String isDelete;

    /**
     * 标签
     */
    @TableField(exist = false)
    private List<Tag> tags;

    @TableField(exist = false)
    private static final long serialVersionUID = 7919374261730108718L;

}