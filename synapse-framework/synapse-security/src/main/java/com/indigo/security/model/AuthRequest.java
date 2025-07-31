package com.indigo.security.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 认证请求模型
 * 支持多种认证方式的统一请求格式
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Data
@Builder
public class AuthRequest {

    /**
     * 认证类型
     */
    private AuthType authType;

    private String userId;

    /**
     * 用户名（用于用户名密码认证）
     */
    private String username;

    /**
     * 密码（用于用户名密码认证）
     */
    private String password;

    /**
     * 访问令牌（用于Token验证）
     */
    private String token;

    /**
     * 客户端ID（用于OAuth2.0）
     */
    private String clientId;

    /**
     * 客户端密钥（用于OAuth2.0）
     */
    private String clientSecret;

    /**
     * 授权码（用于OAuth2.0）
     */
    private String code;

    /**
     * 重定向URI（用于OAuth2.0）
     */
    private String redirectUri;

    /**
     * 授权范围（用于OAuth2.0）
     */
    private String scope;

    /**
     * 租户ID（多租户支持）
     */
    private Long tenantId;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 扩展参数
     */
    private Map<String, Object> extraParams;

    /**
     * 认证类型枚举
     */
    public enum AuthType {
        /**
         * 用户名密码认证
         */
        USERNAME_PASSWORD,

        /**
         * Token验证
         */
        TOKEN_VALIDATION,

        /**
         * OAuth2.0 授权码模式
         */
        OAUTH2_AUTHORIZATION_CODE,

        /**
         * OAuth2.0 客户端凭证模式
         */
        OAUTH2_CLIENT_CREDENTIALS,

        /**
         * 刷新Token
         */
        REFRESH_TOKEN
    }
} 