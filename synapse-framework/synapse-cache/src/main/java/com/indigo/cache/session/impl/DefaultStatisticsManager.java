package com.indigo.cache.session.impl;

import com.indigo.cache.core.CacheService;
import com.indigo.cache.manager.CacheKeyGenerator;
import com.indigo.cache.infrastructure.RedisService;
import com.indigo.cache.session.PermissionManager;
import com.indigo.cache.session.SessionManager;
import com.indigo.cache.session.StatisticsManager;
import com.indigo.core.context.UserContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认统计管理器实现
 * 基于缓存服务的统计管理
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
public class DefaultStatisticsManager implements StatisticsManager {

    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;
    private final RedisService redisService;
    private final SessionManager sessionManager;
    private final PermissionManager permissionManager;

    public DefaultStatisticsManager(CacheService cacheService,
                                    CacheKeyGenerator keyGenerator,
                                    RedisService redisService,
                                    SessionManager sessionManager,
                                    PermissionManager permissionManager) {
        this.cacheService = cacheService;
        this.keyGenerator = keyGenerator;
        this.redisService = redisService;
        this.sessionManager = sessionManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public List<UserContext> getOnlineUsers() {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        return getUsersByPattern(pattern);
    }

    @Override
    public List<UserContext> getOnlineUsersByTenant(Long tenantId) {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);
        return allUsers.stream()
                .filter(user -> user.getTenantId() != null && user.getTenantId().equals(tenantId))
                .toList();
    }

    @Override
    public List<UserContext> getOnlineUsersByDept(Long deptId) {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);
        return allUsers.stream()
                .filter(user -> user.getDeptId() != null && user.getDeptId().equals(deptId))
                .toList();
    }

    @Override
    public List<UserContext> getOnlineUsersByRole(String role) {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);
        return allUsers.stream()
                .filter(user -> {
                    List<String> userRoles = permissionManager.getUserRoles(user.getToken());
                    return userRoles != null && userRoles.contains(role);
                })
                .toList();
    }

    @Override
    public long getOnlineUserCount() {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        return getUsersByPattern(pattern).size();
    }

    @Override
    public boolean isUserOnline(Long userId) {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);
        return allUsers.stream()
                .anyMatch(user -> user.getUserId() != null && user.getUserId().equals(userId));
    }

    @Override
    public boolean forceUserOffline(Long userId) {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);

        for (UserContext user : allUsers) {
            if (user.getUserId() != null && user.getUserId().equals(userId)) {
                sessionManager.removeUserSession(user.getToken());
                permissionManager.removeUserPermissions(user.getToken());
                log.info("强制用户下线: userId={}, username={}", userId, user.getUsername());
                return true;
            }
        }
        return false;
    }

    @Override
    public int forceUsersOffline(List<Long> userIds) {
        int count = 0;
        for (Long userId : userIds) {
            if (forceUserOffline(userId)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public UserSessionStats getUserSessionStats() {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);

        long totalUsers = allUsers.size();
        // 定义活跃用户为最近15分钟内有访问的用户
        long activeThreshold = System.currentTimeMillis() - (15 * 60 * 1000L);
        long activeUsers = allUsers.stream()
                .filter(user -> {
                    // 优先使用最后访问时间判断活跃性
                    if (user.getLastAccessTime() != null) {
                        return user.getLastAccessTime() >= activeThreshold;
                    }
                    // 如果没有最后访问时间，使用登录时间判断
                    return user.getLoginTime() != null && user.getLoginTime() >= activeThreshold;
                })
                .count();

        return new UserSessionStats(totalUsers, activeUsers);
    }

    @Override
    public List<UserContext> getActiveUsers(int minutes) {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);
        long threshold = System.currentTimeMillis() - (minutes * 60 * 1000L);

        return allUsers.stream()
                .filter(user -> {
                    if (user.getLastAccessTime() != null && user.getLastAccessTime() >= threshold) {
                        return true;
                    }
                    // 如果没有最后访问时间，但有登录时间，且登录时间在阈值内，也认为是活跃用户
                    return user.getLoginTime() != null && user.getLoginTime() >= threshold;
                })
                .toList();
    }

    @Override
    public LoginStats getLoginStats() {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);

        long currentTime = System.currentTimeMillis();
        long oneHourAgo = currentTime - (60 * 60 * 1000L);
        long oneDayAgo = currentTime - (24 * 60 * 60 * 1000L);

        long hourlyLogins = allUsers.stream()
                .filter(user -> user.getLoginTime() != null && user.getLoginTime() >= oneHourAgo)
                .count();

        long dailyLogins = allUsers.stream()
                .filter(user -> user.getLoginTime() != null && user.getLoginTime() >= oneDayAgo)
                .count();

        return new LoginStats(hourlyLogins, dailyLogins, allUsers.size());
    }

    @Override
    public Long getUserOnlineDuration(Long userId) {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);

        return allUsers.stream()
                .filter(user -> user.getUserId() != null && user.getUserId().equals(userId))
                .findFirst()
                .map(user -> {
                    if (user.getLoginTime() != null) {
                        return System.currentTimeMillis() - user.getLoginTime();
                    }
                    return 0L;
                })
                .orElse(0L);
    }

    @Override
    public Map<String, Long> getAllUsersOnlineDuration() {
        String pattern = keyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
        List<UserContext> allUsers = getUsersByPattern(pattern);

        return allUsers.stream()
                .filter(user -> user.getUserId() != null)
                .collect(Collectors.toMap(
                        UserContext::getUserId,
                        user -> {
                            if (user.getLoginTime() != null) {
                                return System.currentTimeMillis() - user.getLoginTime();
                            }
                            return 0L;
                        }
                ));
    }

    /**
     * 根据模式获取用户列表
     *
     * @param pattern 缓存键模式
     * @return 用户列表
     */
    private List<UserContext> getUsersByPattern(String pattern) {
        try {
            // 使用 RedisService 的 scan 方法查询匹配的键
            Set<String> keys = redisService.scan(pattern);
            return keys.stream()
                    .map(key -> {
                        try {
                            return cacheService.getObject(key, UserContext.class);
                        } catch (Exception e) {
                            log.warn("获取用户会话数据失败: key={}", key, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            log.error("查询在线用户失败: {}", pattern, e);
            return List.of();
        }
    }

    /**
     * 获取缓存服务（用于兼容性）
     * 注意：这是一个临时的兼容性方法
     *
     * @return 缓存服务
     */
    public CacheService getCacheService() {
        return cacheService;
    }
} 