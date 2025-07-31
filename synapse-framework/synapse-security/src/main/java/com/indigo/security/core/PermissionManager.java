package com.indigo.security.core;

import cn.dev33.satoken.stp.StpInterface;
import com.indigo.cache.session.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 统一的权限管理服务
 * 负责处理用户权限和角色的验证
 * 实现Sa-Token的StpInterface接口
 *
 * @author 史偕成
 * @date 2024/01/08
 */
@Slf4j
@Service
public class PermissionManager implements StpInterface {

    private final UserSessionService userSessionService;

    public PermissionManager(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
        log.info("权限管理服务初始化完成");
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        try {
            String token = resolveToken(loginId);
            if (token != null) {
                List<String> permissions = userSessionService.getUserPermissions(token);
                log.debug("获取用户权限列表: loginId={}, permissions={}", loginId, permissions);
                return permissions;
            }
        } catch (Exception e) {
            log.error("获取用户权限列表失败: loginId={}, loginType={}", loginId, loginType, e);
        }
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            String token = resolveToken(loginId);
            if (token != null) {
                List<String> roles = userSessionService.getUserRoles(token);
                log.debug("获取用户角色列表: loginId={}, roles={}", loginId, roles);
                return roles;
            }
        } catch (Exception e) {
            log.error("获取用户角色列表失败: loginId={}, loginType={}", loginId, loginType, e);
        }
        return List.of();
    }

    /**
     * 检查用户是否具有指定权限
     */
    public boolean hasPermission(String token, String permission) {
        try {
            List<String> permissions = userSessionService.getUserPermissions(token);
            boolean hasPermission = permissions != null && permissions.contains(permission);
            log.debug("检查用户权限: token={}, permission={}, result={}", token, permission, hasPermission);
            return hasPermission;
        } catch (Exception e) {
            log.error("检查用户权限失败: token={}, permission={}", token, permission, e);
            return false;
        }
    }

    /**
     * 检查用户是否具有指定角色
     */
    public boolean hasRole(String token, String role) {
        try {
            List<String> roles = userSessionService.getUserRoles(token);
            boolean hasRole = roles != null && roles.contains(role);
            log.debug("检查用户角色: token={}, role={}, result={}", token, role, hasRole);
            return hasRole;
        } catch (Exception e) {
            log.error("检查用户角色失败: token={}, role={}", token, role, e);
            return false;
        }
    }

    /**
     * 检查用户是否具有任意一个指定的权限
     */
    public boolean hasAnyPermission(String token, String... permissions) {
        try {
            List<String> userPermissions = userSessionService.getUserPermissions(token);
            if (userPermissions != null) {
                for (String permission : permissions) {
                    if (userPermissions.contains(permission)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("检查用户任意权限失败: token={}, permissions={}", token, permissions, e);
        }
        return false;
    }

    /**
     * 检查用户是否具有任意一个指定的角色
     */
    public boolean hasAnyRole(String token, String... roles) {
        try {
            List<String> userRoles = userSessionService.getUserRoles(token);
            if (userRoles != null) {
                for (String role : roles) {
                    if (userRoles.contains(role)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("检查用户任意角色失败: token={}, roles={}", token, roles, e);
        }
        return false;
    }

    /**
     * 检查用户是否具有所有指定的权限
     */
    public boolean hasAllPermissions(String token, String... permissions) {
        try {
            List<String> userPermissions = userSessionService.getUserPermissions(token);
            if (userPermissions != null) {
                for (String permission : permissions) {
                    if (!userPermissions.contains(permission)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            log.error("检查用户所有权限失败: token={}, permissions={}", token, permissions, e);
        }
        return false;
    }

    /**
     * 检查用户是否具有所有指定的角色
     */
    public boolean hasAllRoles(String token, String... roles) {
        try {
            List<String> userRoles = userSessionService.getUserRoles(token);
            if (userRoles != null) {
                for (String role : roles) {
                    if (!userRoles.contains(role)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            log.error("检查用户所有角色失败: token={}, roles={}", token, roles, e);
        }
        return false;
    }

    private String resolveToken(Object loginId) {
        // 这里需要根据你的业务逻辑实现如何通过loginId获取token
        // 例如可以通过StpUtil等工具类
        return loginId != null ? loginId.toString() : null;
    }
} 