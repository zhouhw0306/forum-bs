package com.example.domain.dao;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 24668
 * @TableName 资源表
 */
@TableName(value ="tb_source")
@Data
public class Source implements Serializable {

    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 信息
     */
    private String description;

    /**
     * 类型
     */
    private String category;

    /**
     * 内容
     */
    private String content;

    /**
     * 封面
     */
    private String cover;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 状态 默认0审核中 1上线
     */
    private Integer state;

    /**
     * 删除标识
     */
    @TableLogic(value = "0",delval = "1")
    private String isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * userId
     */
    private String userId;

    /**
     * 资源名称
     */
    private String fileName;

    /**
     * 资源路径
     */
    private String fileUrl;

    /**
     * 点赞标识
     */
    @TableField(exist = false)
    private boolean hasThumb;

    /**
     * 收藏标识
     */
    @TableField(exist = false)
    private boolean hasFavour;

    /**
     * 作者信息
     */
    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}