package com.example.domain.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName 资源-点赞关系表
 */
@TableName(value ="tb_source_hasthumb")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SourceHasthumb implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 点赞人
     */
    private String userId;

    /**
     * 资源id
     */
    private Integer sourceId;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}