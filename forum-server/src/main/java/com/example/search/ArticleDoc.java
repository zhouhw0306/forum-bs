package com.example.search;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 24668
 */
@Data
@Document(indexName = "article_index")
public class ArticleDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    /**文章ID*/
    @Id
    private String id;

    /**作者ID*/
    private String userId;

    /**标题*/
    private String title;

    /**分类*/
    private Integer categoryId;

    /**正文内容*/
    private String contentHtml;

    /**评论数量*/
    private Integer commentCount;

    /**阅读数量*/
    private Integer viewCount;

    /**创建时间*/
    private Date createTime;

    /**更新时间*/
    private Date updateTime;

    /**删除标识*/
    private String isDelete;


}
