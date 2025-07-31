package com.indigo.security.core;

import com.indigo.security.model.TokenInfo;
import com.indigo.security.model.UserPrincipal;

/**
 * 令牌管理服务接口
 * 负责令牌的生成、验证、刷新和撤销
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public interface TokenService {

    /**
     * 生成访问令牌
     *
     * @param userPrincipal 用户主体信息
     * @param clientId 客户端ID（可选）
     * @param scope 授权范围（可选）
     * @return 令牌信息
     */
    TokenInfo generateToken(UserPrincipal userPrincipal, String clientId, String scope);

    /**
     * 验证令牌有效性
     *
     * @param token 访问令牌
     * @return 令牌信息，如果无效返回null
     */
    TokenInfo validateToken(String token);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    TokenInfo refreshToken(String refreshToken);

    /**
     * 撤销令牌
     *
     * @param token 访问令牌
     * @return 是否成功撤销
     */
    boolean revokeToken(String token);

    /**
     * 撤销用户的所有令牌
     *
     * @param userId 用户ID
     * @return 撤销的令牌数量
     */
    int revokeAllUserTokens(Long userId);

    /**
     * 从令牌中解析用户ID
     *
     * @param token 访问令牌
     * @return 用户ID，如果解析失败返回null
     */
    Long parseUserId(String token);

    /**
     * 检查令牌是否存在于黑名单
     *
     * @param token 访问令牌
     * @return 是否在黑名单中
     */
    boolean isTokenBlacklisted(String token);
} 