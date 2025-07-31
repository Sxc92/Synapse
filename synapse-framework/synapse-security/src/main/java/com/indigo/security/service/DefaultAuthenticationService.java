package com.indigo.security.service;

import com.indigo.core.entity.Result;
import com.indigo.security.core.AuthenticationService;
import com.indigo.security.factory.AuthenticationStrategyFactory;
import com.indigo.security.model.AuthRequest;
import com.indigo.security.model.AuthResponse;
import com.indigo.security.strategy.AuthenticationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 简化的认证服务实现
 * 使用策略模式处理不同类型的认证请求
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {

    private final AuthenticationStrategyFactory strategyFactory;

    @Override
    public Result<AuthResponse> authenticate(AuthRequest request) {
        try {
            if (request == null || !StringUtils.hasText(request.getUsername())) {
                return Result.error("用户名不能为空");
            }

            // 默认使用Sa-Token策略
            String strategyType = "satoken";
            AuthenticationStrategy strategy = strategyFactory.getStrategy(strategyType);
            
            return strategy.authenticate(request);
            
        } catch (Exception e) {
            log.error("认证失败", e);
            return Result.error("认证失败: " + e.getMessage());
        }
    }

    @Override
    public Result<AuthResponse> renewToken(String token) {
        try {
            if (!StringUtils.hasText(token)) {
                return Result.error("Token不能为空");
            }

            // 默认使用Sa-Token策略
            String strategyType = "satoken";
            AuthenticationStrategy strategy = strategyFactory.getStrategy(strategyType);
            
            return strategy.renewToken(token);
            
        } catch (Exception e) {
            log.error("Token续期失败", e);
            return Result.error("续期失败: " + e.getMessage());
        }
    }
} 