package com.indigo.cache.session.impl;

import com.indigo.cache.core.CacheService;
import com.indigo.cache.manager.CacheKeyGenerator;
import com.indigo.cache.session.SessionManager;
import com.indigo.core.context.UserContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认会话管理器实现
 * 基于缓存服务的会话管理
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
public class DefaultSessionManager implements SessionManager {

    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;

    public DefaultSessionManager(CacheService cacheService, CacheKeyGenerator keyGenerator) {
        this.cacheService = cacheService;
        this.keyGenerator = keyGenerator;
    }

    @Override
    public void storeUserSession(String token, UserContext userContext, long expiration) {
        String sessionKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", token);
        cacheService.setObject(sessionKey, userContext, expiration);
        log.debug("Stored user session for token: {}", token);
    }

    @Override
    public UserContext getUserSession(String token) {
        String sessionKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", token);
        UserContext userContext = cacheService.getObject(sessionKey, UserContext.class);
        if (userContext != null) {
            userContext.setToken(token);
            // 更新最后访问时间
            updateLastAccessTime(userContext);
        }
        return userContext;
    }

    @Override
    public boolean hasUserSession(String token) {
        String sessionKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", token);
        return cacheService.exists(sessionKey);
    }

    @Override
    public void removeUserSession(String token) {
        String sessionKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", token);
        cacheService.delete(sessionKey);
        log.debug("Removed user session for token: {}", token);
    }

    @Override
    public void extendUserSession(String token, long expiration) {
        String sessionKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", token);
        cacheService.resetExpiry(sessionKey, expiration);
        log.debug("Extended user session for token: {}", token);
    }

    @Override
    public long getTokenRemainingTime(String token) {
        String sessionKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", token);
        return cacheService.getTimeToLive(sessionKey);
    }

    @Override
    public boolean renewToken(String token, long duration) {
        try {
            String sessionKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", token);
            UserContext userContext = cacheService.getObject(sessionKey, UserContext.class);
            if (userContext != null) {
                cacheService.setObject(sessionKey, userContext, duration);
                log.debug("Renewed token: {}", token);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to renew token: {}", token, e);
            return false;
        }
    }

    /**
     * 更新用户最后访问时间
     *
     * @param userContext 用户上下文
     */
    private void updateLastAccessTime(UserContext userContext) {
        userContext.setLastAccessTime(System.currentTimeMillis());
        String sessionKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", userContext.getToken());
        // 获取当前过期时间
        long expireTime = cacheService.getTimeToLive(sessionKey);
        if (expireTime > 0) {
            cacheService.setObject(sessionKey, userContext, expireTime);
        }
    }
} 