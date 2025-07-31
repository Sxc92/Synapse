package com.indigo.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import lombok.experimental.Accessors;

/**
 * 登录请求对象
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LoginRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 租户ID（多租户场景）
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
     * 设备类型
     */
    private String deviceType;

    /**
     * 记住我
     */
    private Boolean rememberMe;
} 