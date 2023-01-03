package com.example.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @author 24668
 * @TableName tb_user
 */
@TableName(value ="tb_user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 生日
     */
    private Date birth;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 位置
     */
    private String location;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    /**
     * 积分
     */
    private Integer score;


    @TableField(exist = false)
    private String token;

    @TableField(exist = false)
    private static final long serialVersionUID = 2338282716883198667L;
}