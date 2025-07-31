package com.indigo.security.core;

import com.indigo.core.entity.Result;
import com.indigo.security.model.AuthRequest;
import com.indigo.security.model.AuthResponse;

/**
 * 简化的认证服务接口
 * 提供基础的认证和Token续期功能
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public interface AuthenticationService {

    /**
     * 用户登录认证
     *
     * @param authRequest 认证请求
     * @return 认证结果
     */
    Result<AuthResponse> authenticate(AuthRequest authRequest);

    /**
     * 续期Token
     *
     * @param token Token值
     * @return 续期结果
     */
    Result<AuthResponse> renewToken(String token);
} 