package com.example.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhw
 */

@Getter
@AllArgsConstructor
public enum SourceEnum {

    /**
     * 类型
     */
    TOOL("tool", "工具"),
    SITE("site", "网站"),
    PROJECT("project", "项目"),
    COURSE("course","教程");

    /**
     * code
     */
    public final String code;
    /**
     * name
     */
    public final String name;

    /**
     * 根据code查找
     * @param code 枚举code
     * @return 枚举对象
     */
    public static String findEnumByCode(String code) {
        for (SourceEnum statusEnum : SourceEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum.name;
            }
        }
        throw new IllegalArgumentException("code is invalid");
    }

    /**
     * 根据name查找
     * @param name 枚举name
     * @return 枚举对象
     */
    public static String findEnumByName(String name) {
        for (SourceEnum statusEnum : SourceEnum.values()) {
            if (statusEnum.getName().equals(name)) {
                return statusEnum.code;
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }
}
