package com.example.domain.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * 文章-标签关系表
 * @TableName tb_article_tag_relation
 */
@TableName(value ="tb_article_tag_relation")
@Builder
@Data
public class ArticleTagRelation implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 文章id
     */
    private String articleId;

    /**
     * 标签
     */
    private Integer tagId;

    @TableField(exist = false)
    private static final long serialVersionUID = -8655758938628832695L;

}