package com.example.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {

    /** 通知标题 */
    private String title;
    /** 通知内容 */
    private String content;
    /** 时间戳 */
    private Date timestamp;
    /** 展示样式: SUCCESS, ERROR, WARNING, INFO */
    private String type;
    /** 通知类别: COMMENT/REPLY/LIKE/FAVOUR/FOLLOW/SYSTEM */
    private String category;
    /** 关联资源ID */
    private Integer resourceId;
    /** 关联资源类型 */
    private String resourceType;

}
