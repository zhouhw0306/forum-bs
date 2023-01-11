package com.example.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 24668
 * @TableName 收藏关系表
 */
@TableName(value ="tb_source_hasfavour")
@Data
@AllArgsConstructor
public class SourceHasfavour implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 收藏人
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

    public SourceHasfavour(String currentUser, Integer targetId) {
        this.userId = currentUser;
        this.sourceId = targetId;
    }
}