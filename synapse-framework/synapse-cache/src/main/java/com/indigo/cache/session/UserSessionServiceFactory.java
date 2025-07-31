package com.indigo.cache.session;

import com.indigo.cache.core.CacheService;
import com.indigo.cache.manager.CacheKeyGenerator;
import com.indigo.cache.infrastructure.RedisService;
import com.indigo.cache.session.impl.DefaultSessionManager;
import com.indigo.cache.session.impl.DefaultPermissionManager;
import com.indigo.cache.session.impl.DefaultStatisticsManager;

/**
 * 用户会话服务工厂
 * 使用工厂模式创建和管理UserSessionService实例
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public class UserSessionServiceFactory {

    /**
     * 创建用户会话服务（默认实现）
     *
     * @param cacheService  缓存服务
     * @param keyGenerator  缓存键生成器
     * @param redisService  Redis服务
     * @return 用户会话服务实例
     */
    public static UserSessionService createUserSessionService(
            CacheService cacheService,
            CacheKeyGenerator keyGenerator,
            RedisService redisService) {
        
        // 创建管理器实例
        SessionManager sessionManager = new DefaultSessionManager(cacheService, keyGenerator);
        PermissionManager permissionManager = new DefaultPermissionManager(cacheService, keyGenerator);
        StatisticsManager statisticsManager = new DefaultStatisticsManager(
                cacheService, keyGenerator, redisService, sessionManager, permissionManager);
        
        // 创建用户会话服务
        return new UserSessionService(sessionManager, permissionManager, statisticsManager);
    }

    /**
     * 创建用户会话服务（自定义实现）
     *
     * @param sessionManager    会话管理器
     * @param permissionManager 权限管理器
     * @param statisticsManager 统计管理器
     * @return 用户会话服务实例
     */
    public static UserSessionService createUserSessionService(
            SessionManager sessionManager,
            PermissionManager permissionManager,
            StatisticsManager statisticsManager) {
        return new UserSessionService(sessionManager, permissionManager, statisticsManager);
    }

    /**
     * 创建会话管理器
     *
     * @param cacheService 缓存服务
     * @param keyGenerator 缓存键生成器
     * @return 会话管理器实例
     */
    public static SessionManager createSessionManager(
            CacheService cacheService,
            CacheKeyGenerator keyGenerator) {
        return new DefaultSessionManager(cacheService, keyGenerator);
    }

    /**
     * 创建权限管理器
     *
     * @param cacheService 缓存服务
     * @param keyGenerator 缓存键生成器
     * @return 权限管理器实例
     */
    public static PermissionManager createPermissionManager(
            CacheService cacheService,
            CacheKeyGenerator keyGenerator) {
        return new DefaultPermissionManager(cacheService, keyGenerator);
    }

    /**
     * 创建统计管理器
     *
     * @param cacheService      缓存服务
     * @param keyGenerator      缓存键生成器
     * @param redisService      Redis服务
     * @param sessionManager    会话管理器
     * @param permissionManager 权限管理器
     * @return 统计管理器实例
     */
    public static StatisticsManager createStatisticsManager(
            CacheService cacheService,
            CacheKeyGenerator keyGenerator,
            RedisService redisService,
            SessionManager sessionManager,
            PermissionManager permissionManager) {
        return new DefaultStatisticsManager(cacheService, keyGenerator, redisService, sessionManager, permissionManager);
    }
} 