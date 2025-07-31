package com.indigo.security.strategy;

import com.indigo.cache.session.UserSessionService;
import com.indigo.core.context.UserContext;
import com.indigo.core.entity.Result;
import com.indigo.security.core.TokenManager;
import com.indigo.security.model.AuthRequest;
import com.indigo.security.model.AuthResponse;
import com.indigo.security.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OAuth2认证策略实现
 * 使用Sa-Token的OAuth2模块进行认证
 *
 * @author 史偕成
 * @date 2024/01/08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationStrategy implements AuthenticationStrategy {

    private final TokenManager tokenManager;
    private final UserSessionService userSessionService;

    @Override
    public String getStrategyType() {
        return "oauth2";
    }

    @Override
    public Result<AuthResponse> authenticate(AuthRequest request) {
        try {
            // 简化的OAuth2认证逻辑
            log.info("开始OAuth2认证: username={}", request.getUsername());
            
            // 创建用户上下文
            UserContext userContext = UserContext.builder()
                .userId(request.getUserId())  // 将String转换为Long
                .username(request.getUsername())
                .loginTime(System.currentTimeMillis())  // 使用当前时间戳
                .roles(List.of("user"))  // 这里可以根据实际情况设置角色
                .permissions(List.of("user:read"))  // 这里可以根据实际情况设置权限
                .build();
            
            // 使用TokenManager进行登录，同时存储用户上下文
            String token = tokenManager.login(request.getUsername(), userContext);
            
            // 转换为UserPrincipal
            UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(userContext.getUserId())
                .username(userContext.getUsername())
                .roles(userContext.getRoles())
                .permissions(userContext.getPermissions())
                .build();
            
            AuthResponse response = AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(7200L)
                .userPrincipal(userPrincipal)
                .roles(userContext.getRoles())
                .permissions(userContext.getPermissions())
                .tokenCreatedAt(LocalDateTime.now())
                .build();
                
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("OAuth2认证失败", e);
            return Result.error("认证失败: " + e.getMessage());
        }
    }

    @Override
    public Result<AuthResponse> renewToken(String token) {
        try {
            // OAuth2的token续期逻辑
            if (!tokenManager.validateToken(token)) {
                return Result.error("Token无效");
            }
            
            String userId = tokenManager.getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 获取现有的用户上下文
            UserContext existingContext = userSessionService.getUserSession(token);
            if (existingContext == null) {
                return Result.error("用户会话已失效");
            }
            
            // 转换为UserPrincipal
            UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(existingContext.getUserId())
                .username(existingContext.getUsername())
                .roles(existingContext.getRoles())
                .permissions(existingContext.getPermissions())
                .build();
            
            // 重新登录，保持原有的用户上下文
            String newToken = tokenManager.login(userId, existingContext);
            
            AuthResponse response = AuthResponse.builder()
                .accessToken(newToken)
                .tokenType("Bearer")
                .expiresIn(7200L)
                .userPrincipal(userPrincipal)
                .roles(existingContext.getRoles())
                .permissions(existingContext.getPermissions())
                .tokenCreatedAt(LocalDateTime.now())
                .build();
                
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("OAuth2 Token续期失败", e);
            return Result.error("续期失败: " + e.getMessage());
        }
    }
} 