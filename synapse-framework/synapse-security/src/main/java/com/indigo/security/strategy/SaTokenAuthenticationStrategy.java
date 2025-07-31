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
 * Sa-Token认证策略实现
 * 使用Sa-Token进行用户认证
 *
 * @author 史偕成
 * @date 2024/01/08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaTokenAuthenticationStrategy implements AuthenticationStrategy {

    private final TokenManager tokenManager;
    private final UserSessionService userSessionService;

    @Override
    public String getStrategyType() {
        return "satoken";
    }

    @Override
    public Result<AuthResponse> authenticate(AuthRequest request) {
        try {
            // 创建用户上下文
            UserContext userContext = UserContext.builder()
                .userId(request.getUserId())  // 将用户名作为用户ID
                .username(request.getUsername())
                .loginTime(System.currentTimeMillis())
                .roles(List.of("admin"))  // 这里可以根据实际情况设置角色
                .permissions(List.of("admin"))  // 这里可以根据实际情况设置权限
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
            
            // 构建认证响应
            AuthResponse response = AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(7200L) // 2小时
                .userPrincipal(userPrincipal)
                .roles(userContext.getRoles())
                .permissions(userContext.getPermissions())
                .tokenCreatedAt(LocalDateTime.now())
                .build();
                
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("Sa-Token认证失败", e);
            return Result.error("认证失败: " + e.getMessage());
        }
    }

    @Override
    public Result<AuthResponse> renewToken(String token) {
        try {
            // 验证当前token
            if (!tokenManager.validateToken(token)) {
                return Result.error("Token无效");
            }
            
            // 获取用户ID并重新登录
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
            
            // 构建认证响应
            AuthResponse response = AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(7200L)
                .userPrincipal(userPrincipal)
                .roles(existingContext.getRoles())
                .permissions(existingContext.getPermissions())
                .tokenCreatedAt(LocalDateTime.now())
                .build();
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("Token续期失败", e);
            return Result.error("Token续期失败: " + e.getMessage());
        }
    }
} 