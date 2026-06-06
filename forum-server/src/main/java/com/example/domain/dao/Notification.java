package com.example.domain.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 通知表（兼容系统公告 + 用户通知）
 * @TableName tb_notification
 */
@TableName(value ="tb_notification")
@Data
public class Notification implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 通知类型: COMMENT/REPLY/LIKE/FAVOUR/FOLLOW/SYSTEM
     */
    private String type;

    /**
     * 触发者用户ID
     */
    private String senderId;

    /**
     * 接收者用户ID (NULL表示全体公告)
     */
    private String receiverId;

    /**
     * 是否已读 0未读 1已读
     */
    private Integer isRead;

    /**
     * 关联资源类型: ARTICLE/COMMENT/SOURCE
     */
    private String resourceType;

    /**
     * 关联资源ID
     */
    private String resourceId;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 287984246061046552L;
}