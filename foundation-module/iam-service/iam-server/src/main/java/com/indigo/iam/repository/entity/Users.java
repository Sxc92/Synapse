package com.indigo.iam.repository.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.indigo.databases.entity.AuditEntity;
import com.indigo.iam.sdk.enums.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @title 用户
 * @description
 * @create 2025-09-18 16:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@TableName(value = "iam_user")
public class Users extends AuditEntity<String> {

    /**
     * 用户名
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像 地址
     */
    private String avatar;


    /**
     * 用户类型
     * {@link UserTypeEnum}
     */
    private String type;

    /**
     * 锁定状态
     * 插入时跳过该字段，使用数据库默认值 0
     */
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Boolean locked;

    /**
     * 启用状态
     * 插入时跳过该字段，使用数据库默认值 1
     */
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Boolean enabled;

    /**
     * 密码过期状态
     * 插入时跳过该字段，使用数据库默认值 0
     */
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Boolean expired;
}
