package com.indigo.security.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户主体信息
 * 包含认证后的用户基本信息
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Data
@Builder
public class UserPrincipal {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 用户状态（启用/禁用）
     */
    private Boolean enabled;

    /**
     * 账号是否未过期
     */
    private Boolean accountNonExpired;

    /**
     * 账号是否未锁定
     */
    private Boolean accountNonLocked;

    /**
     * 凭证是否未过期
     */
    private Boolean credentialsNonExpired;

    /**
     * 用户角色列表
     */
    private List<String> roles;

    /**
     * 用户权限列表
     */
    private List<String> permissions;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 扩展属性
     */
    private Map<String, Object> attributes;

    /**
     * 检查用户是否可用
     */
    public boolean isActive() {
        return enabled != null && enabled 
               && accountNonExpired != null && accountNonExpired
               && accountNonLocked != null && accountNonLocked
               && credentialsNonExpired != null && credentialsNonExpired;
    }

    /**
     * 检查用户是否有指定角色
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * 检查用户是否有指定权限
     */
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
} 