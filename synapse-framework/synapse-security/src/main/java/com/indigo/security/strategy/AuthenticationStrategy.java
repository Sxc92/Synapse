package com.indigo.security.strategy;

import com.indigo.core.entity.Result;
import com.indigo.security.model.AuthRequest;
import com.indigo.security.model.AuthResponse;

/**
 * 认证策略接口
 * 定义不同认证方式的统一接口
 *
 * @author 史偕成
 * @date 2024/01/08
 */
public interface AuthenticationStrategy {

    /**
     * 获取策略类型
     *
     * @return 策略类型标识
     */
    String getStrategyType();

    /**
     * 执行认证
     *
     * @param request 认证请求
     * @return 认证结果
     */
    Result<AuthResponse> authenticate(AuthRequest request);

    /**
     * 续期Token
     *
     * @param token Token值
     * @return 续期结果
     */
    Result<AuthResponse> renewToken(String token);
} 