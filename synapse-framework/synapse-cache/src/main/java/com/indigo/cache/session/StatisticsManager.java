package com.indigo.cache.session;

import com.indigo.core.context.UserContext;

import java.util.List;
import java.util.Map;

/**
 * 统计管理器接口
 * 定义用户会话统计相关的操作
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public interface StatisticsManager {

    /**
     * 获取所有在线用户列表
     *
     * @return 在线用户列表
     */
    List<UserContext> getOnlineUsers();

    /**
     * 获取指定租户的在线用户列表
     *
     * @param tenantId 租户ID
     * @return 在线用户列表
     */
    List<UserContext> getOnlineUsersByTenant(Long tenantId);

    /**
     * 获取指定部门的在线用户列表
     *
     * @param deptId 部门ID
     * @return 在线用户列表
     */
    List<UserContext> getOnlineUsersByDept(Long deptId);

    /**
     * 获取指定角色的在线用户列表
     *
     * @param role 角色标识
     * @return 在线用户列表
     */
    List<UserContext> getOnlineUsersByRole(String role);

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    long getOnlineUserCount();

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    boolean isUserOnline(Long userId);

    /**
     * 强制用户下线
     *
     * @param userId 用户ID
     * @return 是否成功下线
     */
    boolean forceUserOffline(Long userId);

    /**
     * 批量强制用户下线
     *
     * @param userIds 用户ID列表
     * @return 成功下线的用户数量
     */
    int forceUsersOffline(List<Long> userIds);

    /**
     * 获取用户会话统计信息
     *
     * @return 会话统计信息
     */
    UserSessionStats getUserSessionStats();

    /**
     * 获取指定时间范围内的活跃用户
     *
     * @param minutes 时间范围（分钟）
     * @return 活跃用户列表
     */
    List<UserContext> getActiveUsers(int minutes);

    /**
     * 获取用户登录统计信息
     *
     * @return 登录统计信息
     */
    LoginStats getLoginStats();

    /**
     * 获取用户在线时长
     *
     * @param userId 用户ID
     * @return 在线时长（毫秒）
     */
    Long getUserOnlineDuration(Long userId);

    /**
     * 获取所有在线用户的在线时长
     *
     * @return 用户在线时长映射
     */
    Map<String, Long> getAllUsersOnlineDuration();

    /**
     * 用户会话统计信息
     */
    class UserSessionStats {
        private final long totalUsers;
        private final long activeUsers;

        public UserSessionStats(long totalUsers, long activeUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }
    }

    /**
     * 登录统计信息
     */
    class LoginStats {
        private final long hourlyLogins;
        private final long dailyLogins;
        private final long totalOnlineUsers;

        public LoginStats(long hourlyLogins, long dailyLogins, long totalOnlineUsers) {
            this.hourlyLogins = hourlyLogins;
            this.dailyLogins = dailyLogins;
            this.totalOnlineUsers = totalOnlineUsers;
        }

        public long getHourlyLogins() {
            return hourlyLogins;
        }

        public long getDailyLogins() {
            return dailyLogins;
        }

        public long getTotalOnlineUsers() {
            return totalOnlineUsers;
        }
    }
} 