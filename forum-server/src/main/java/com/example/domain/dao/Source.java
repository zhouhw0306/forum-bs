package com.example.domain.dao;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

/**
 * @author zhw
 * @TableName 资源表
 */
@TableName(value ="tb_source")
@Data
public class Source implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 描述信息
     */
    @NotBlank(message = "描述信息不能为空")
    private String description;

    /**
     * 类型
     */
    @NotBlank(message = "类型不能为空")
    private String category;

    /**
     * 内容（外链）
     */
    @URL(message = "url非法")
    private String content;

    /**
     * 封面
     */
    @NotBlank(message = "封面不能为空")
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