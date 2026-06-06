package com.example.domain.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 文章类型表
 * @TableName tb_category
 */
@TableName(value ="tb_category")
@Data
public class Category implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 描述
     */
    private String description;

    @TableField(exist = false)
    private static final long serialVersionUID = -7049964225082687735L;
}