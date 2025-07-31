package com.indigo.security.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 认证响应模型
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Data
@Builder
public class AuthResponse {

    /**
     * 认证是否成功
     */
    private Boolean success;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型（通常是 Bearer）
     */
    private String tokenType;

    /**
     * 令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 授权范围
     */
    private String scope;

    /**
     * 用户信息
     */
    private UserPrincipal userPrincipal;

    /**
     * 用户角色列表
     */
    private List<String> roles;

    /**
     * 用户权限列表
     */
    private List<String> permissions;

    /**
     * 令牌创建时间
     */
    private LocalDateTime tokenCreatedAt;

    /**
     * 令牌过期时间
     */
    private LocalDateTime tokenExpiresAt;

    /**
     * 扩展信息
     */
    private Map<String, Object> extraInfo;

    /**
     * 创建成功响应
     */
    public static AuthResponse success(String accessToken, String refreshToken, UserPrincipal userPrincipal) {
        return AuthResponse.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userPrincipal(userPrincipal)
                .tokenCreatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败响应
     */
    public static AuthResponse failure() {
        return AuthResponse.builder()
                .success(false)
                .build();
    }
} 