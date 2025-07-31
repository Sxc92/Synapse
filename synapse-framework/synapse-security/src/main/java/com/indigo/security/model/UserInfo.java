package com.indigo.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统一的用户信息模型
 * 整合了TokenData.UserInfo和UserPrincipal的字段
 *
 * @author 史偕成
 * @date 2024/01/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    /**
     * 基本信息
     */
    private String userId;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;

    /**
     * 组织信息
     */
    private String tenantId;
    private String tenantName;
    private String deptId;
    private String deptName;

    /**
     * 安全信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecurityInfo {
        private List<String> roles;
        private List<String> permissions;
        private boolean enabled;
        private boolean accountNonExpired;
        private boolean credentialsNonExpired;
        private boolean accountNonLocked;
        private LocalDateTime passwordLastModified;
    }

    private SecurityInfo security;

    /**
     * 登录信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginInfo {
        private LocalDateTime lastLoginTime;
        private String lastLoginIp;
        private String lastLoginDevice;
        private Integer loginCount;
        private LocalDateTime currentLoginTime;
        private String currentLoginIp;
        private String currentLoginDevice;
    }

    private LoginInfo loginInfo;

    /**
     * 扩展信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtendInfo {
        private String language;
        private String timezone;
        private String theme;
        private LocalDateTime createdTime;
        private LocalDateTime updatedTime;
        private String remark;
    }

    private ExtendInfo extendInfo;

    /**
     * 快速构建方法
     */
    public static UserInfo from(UserPrincipal principal) {
        if (principal == null) {
            return null;
        }

        return UserInfo.builder()
                .userId(principal.getUserId())
                .username(principal.getUsername())
                .nickname(principal.getNickname())
                .email(principal.getEmail())
                .phone(principal.getPhone())
                .avatar(principal.getAvatar())
                .tenantId(principal.getTenantId())
                .tenantName(principal.getTenantName())
                .deptId(principal.getDeptId())
                .deptName(principal.getDeptName())
                .security(SecurityInfo.builder()
                        .roles(principal.getRoles())
                        .permissions(principal.getPermissions())
                        .enabled(principal.getEnabled() != null && principal.getEnabled())
                        .accountNonExpired(principal.getAccountNonExpired() != null && principal.getAccountNonExpired())
                        .credentialsNonExpired(principal.getCredentialsNonExpired() != null && principal.getCredentialsNonExpired())
                        .accountNonLocked(principal.getAccountNonLocked() != null && principal.getAccountNonLocked())
                        .build())
                .loginInfo(LoginInfo.builder()
                        .lastLoginTime(principal.getLastLoginTime())
                        .lastLoginIp(principal.getLastLoginIp())
                        .loginCount(principal.getLoginCount())
                        .build())
                .build();
    }

    /**
     * 创建UserPrincipal对象
     */
    public UserPrincipal toUserPrincipal() {
        return UserPrincipal.builder()
                .userId(this.userId)
                .username(this.username)
                .nickname(this.nickname)
                .email(this.email)
                .phone(this.phone)
                .avatar(this.avatar)
                .tenantId(this.tenantId)
                .tenantName(this.tenantName)
                .deptId(this.deptId)
                .deptName(this.deptName)
                .enabled(this.security != null && this.security.isEnabled())
                .accountNonExpired(this.security != null && this.security.isAccountNonExpired())
                .credentialsNonExpired(this.security != null && this.security.isCredentialsNonExpired())
                .accountNonLocked(this.security != null && this.security.isAccountNonLocked())
                .roles(this.security != null ? this.security.getRoles() : null)
                .permissions(this.security != null ? this.security.getPermissions() : null)
                .lastLoginTime(this.loginInfo != null ? this.loginInfo.getLastLoginTime() : null)
                .lastLoginIp(this.loginInfo != null ? this.loginInfo.getLastLoginIp() : null)
                .loginCount(this.loginInfo != null ? this.loginInfo.getLoginCount() : null)
                .attributes(null)
                .build();
    }
} 