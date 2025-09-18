package com.indigo.iam.repository.entity;

import com.indigo.databases.entity.AuditEntity;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author 史偕成
 * @title
 * @description
 * @create 2025-09-18 16:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public class Users extends AuditEntity<String> {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 锁定状态
     */
    private Boolean locked;

    /**
     * 启用状态
     */
    private Boolean enabled;

    /**
     * 密码过期状态
     */
    private Boolean expired;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

}
