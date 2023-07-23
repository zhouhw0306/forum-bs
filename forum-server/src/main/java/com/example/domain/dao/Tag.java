package com.example.domain.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 标签表
 * @TableName tb_tag
 */
@TableName(value ="tb_tag")
@Data
public class Tag implements Serializable {
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
    private String tagName;

    @TableField(exist = false)
    private static final long serialVersionUID = -66078278895318126L;
}