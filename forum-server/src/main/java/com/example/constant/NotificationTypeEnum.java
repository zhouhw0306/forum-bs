package com.example.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知类型枚举
 * @author zhw
 */
@Getter
@AllArgsConstructor
public enum NotificationTypeEnum {

    /** 评论 */
    COMMENT("COMMENT", "评论了你的文章"),
    /** 回复 */
    REPLY("REPLY", "回复了你的评论"),
    /** 点赞 */
    LIKE("LIKE", "赞了你的评论"),
    /** 收藏 */
    FAVOUR("FAVOUR", "收藏了你的文章"),
    /** 关注 */
    FOLLOW("FOLLOW", "关注了你"),
    /** 系统通知 */
    SYSTEM("SYSTEM", "系统通知");

    private final String code;
    private final String desc;

    public static NotificationTypeEnum fromCode(String code) {
        for (NotificationTypeEnum type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return SYSTEM;
    }
}
