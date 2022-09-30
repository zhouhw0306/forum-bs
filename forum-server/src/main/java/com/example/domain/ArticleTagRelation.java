package com.example.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @author 24668
 * @TableName tb_article_tag_relation
 */
@TableName(value ="tb_article_tag_relation")
@Data
public class ArticleTagRelation implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 
     */
    private String articleId;

    /**
     * 
     */
    private Integer tagId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public ArticleTagRelation(String articleId, Integer tagId) {
        this.articleId = articleId;
        this.tagId = tagId;
    }

    public ArticleTagRelation(String id, String articleId, Integer tagId) {
        this.id = id;
        this.articleId = articleId;
        this.tagId = tagId;
    }
}