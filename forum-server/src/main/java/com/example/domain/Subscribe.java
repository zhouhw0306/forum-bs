package com.example.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

/**
 * 用户关注关系表
 * @author 24668
 * @TableName tb_subscribe
 */
@TableName(value ="tb_subscribe")
@Builder
@Data
public class Subscribe implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 被关注者id
     */
    private String beSubscribe;

    /**
     * 关注者id
     */
    private String subscribe;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = -5259840089586023608L;

}