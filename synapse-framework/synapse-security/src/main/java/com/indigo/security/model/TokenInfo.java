package com.indigo.security.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 令牌信息模型
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Data
@Builder
public class TokenInfo {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 授权范围
     */
    private String scope;

    /**
     * 令牌创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 令牌过期时间
     */
    private LocalDateTime expiresAt;

    /**
     * 关联的用户ID
     */
    private Long userId;

    /**
     * 关联的客户端ID
     */
    private String clientId;

    /**
     * 检查令牌是否过期
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 获取剩余有效时间（秒）
     */
    public long getRemainSeconds() {
        if (expiresAt == null) {
            return -1;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) {
            return 0;
        }
        return java.time.Duration.between(now, expiresAt).getSeconds();
    }
} 