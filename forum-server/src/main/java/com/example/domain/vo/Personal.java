package com.example.domain.vo;

import lombok.Data;

/**
 * 用户数据实体
 * @author zhw
 */
@Data
public class Personal {

    /**
     * 追随数
     */
    private Integer followers;
    /**
     * 关注数
     */
    private Integer idol;
    /**
     * 发表文章数
     */
    private Integer writeNum;
    /**
     * 积分
     */
    private Integer score;
}
