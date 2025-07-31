package com.indigo.cache.session;

import com.indigo.core.context.UserContext;

/**
 * 会话管理器接口
 * 定义用户会话管理的核心操作
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public interface SessionManager {

    /**
     * 存储用户会话信息
     *
     * @param token       访问令牌
     * @param userContext 用户上下文
     * @param expiration  过期时间（秒）
     */
    void storeUserSession(String token, UserContext userContext, long expiration);

    /**
     * 获取用户会话信息
     *
     * @param token 访问令牌
     * @return 用户上下文
     */
    UserContext getUserSession(String token);

    /**
     * 检查用户会话是否存在
     *
     * @param token 访问令牌
     * @return 是否存在
     */
    boolean hasUserSession(String token);

    /**
     * 删除用户会话
     *
     * @param token 访问令牌
     */
    void removeUserSession(String token);

    /**
     * 延长用户会话过期时间
     *
     * @param token      访问令牌
     * @param expiration 新的过期时间（秒）
     */
    void extendUserSession(String token, long expiration);

    /**
     * 获取token剩余时间
     *
     * @param token 访问令牌
     * @return 剩余时间（秒），如果token不存在返回-1
     */
    long getTokenRemainingTime(String token);

    /**
     * 续期token
     *
     * @param token    访问令牌
     * @param duration 续期时间（秒）
     * @return 是否续期成功
     */
    boolean renewToken(String token, long duration);
} 