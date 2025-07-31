package com.indigo.core.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigo.core.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

/**
 * 用户上下文持有者
 * 用于在业务模块中获取网关传递的用户信息
 *
 * @author 史偕成
 * @date 2024/12/19
 * @deprecated 此类已迁移到 {@link com.indigo.security.utils.UserContextHolder}，
 * 请使用新的包路径。此类将在后续版本中移除。
 */
@Slf4j
@Deprecated(since = "1.0.0", forRemoval = true)
public class UserContextHolder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取当前用户上下文
     */
    public static UserContext getCurrentUser() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return null;
            }

            String userContextJson = request.getHeader("X-User-Context");
            if (userContextJson != null) {
                return objectMapper.readValue(userContextJson, UserContext.class);
            }

            // 如果没有完整的用户上下文，尝试从其他请求头构建
            return buildUserContextFromHeaders(request);

        } catch (Exception e) {
            log.error("获取当前用户上下文失败", e);
            return null;
        }
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return null;
            }

            String userId = request.getHeader("X-User-Id");
            return userId != null ? Long.valueOf(userId) : null;

        } catch (Exception e) {
            log.error("获取当前用户ID失败", e);
            return null;
        }
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return null;
            }

            return request.getHeader("X-Username");

        } catch (Exception e) {
            log.error("获取当前用户名失败", e);
            return null;
        }
    }

    /**
     * 获取当前租户ID
     */
    public static Long getCurrentTenantId() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return null;
            }

            String tenantId = request.getHeader("X-Tenant-Id");
            return tenantId != null ? Long.valueOf(tenantId) : null;

        } catch (Exception e) {
            log.error("获取当前租户ID失败", e);
            return null;
        }
    }

    /**
     * 获取当前部门ID
     */
    public static Long getCurrentDeptId() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return null;
            }

            String deptId = request.getHeader("X-Dept-Id");
            return deptId != null ? Long.valueOf(deptId) : null;

        } catch (Exception e) {
            log.error("获取当前部门ID失败", e);
            return null;
        }
    }

    /**
     * 获取当前用户角色列表
     */
    public static List<String> getCurrentUserRoles() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return List.of();
            }

            String roles = request.getHeader("X-User-Roles");
            if (roles != null && !roles.isEmpty()) {
                return Arrays.asList(roles.split(","));
            }

            return List.of();

        } catch (Exception e) {
            log.error("获取当前用户角色失败", e);
            return List.of();
        }
    }

    /**
     * 获取当前用户权限列表
     */
    public static List<String> getCurrentUserPermissions() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return List.of();
            }

            String permissions = request.getHeader("X-User-Permissions");
            if (permissions != null && !permissions.isEmpty()) {
                return Arrays.asList(permissions.split(","));
            }

            return List.of();

        } catch (Exception e) {
            log.error("获取当前用户权限失败", e);
            return List.of();
        }
    }

    /**
     * 检查当前用户是否有指定角色
     */
    public static boolean hasRole(String role) {
        List<String> roles = getCurrentUserRoles();
        return roles.contains(role);
    }

    /**
     * 检查当前用户是否有指定权限
     */
    public static boolean hasPermission(String permission) {
        List<String> permissions = getCurrentUserPermissions();
        return permissions.contains(permission);
    }

    /**
     * 检查当前用户是否有任一指定角色
     */
    public static boolean hasAnyRole(String... roles) {
        List<String> userRoles = getCurrentUserRoles();
        return Arrays.stream(roles).anyMatch(userRoles::contains);
    }

    /**
     * 检查当前用户是否有任一指定权限
     */
    public static boolean hasAnyPermission(String... permissions) {
        List<String> userPermissions = getCurrentUserPermissions();
        return Arrays.stream(permissions).anyMatch(userPermissions::contains);
    }

    /**
     * 检查当前用户是否有所有指定角色
     */
    public static boolean hasAllRoles(String... roles) {
        List<String> userRoles = getCurrentUserRoles();
        return Arrays.stream(roles).allMatch(userRoles::contains);
    }

    /**
     * 检查当前用户是否有所有指定权限
     */
    public static boolean hasAllPermissions(String... permissions) {
        List<String> userPermissions = getCurrentUserPermissions();
        return Arrays.stream(permissions).allMatch(userPermissions::contains);
    }

    /**
     * 获取当前请求
     */
    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            log.debug("获取当前请求失败", e);
            return null;
        }
    }

    /**
     * 从请求头构建用户上下文
     */
    private static UserContext buildUserContextFromHeaders(HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader("X-User-Id");
            String username = request.getHeader("X-Username");
            String tenantIdStr = request.getHeader("X-Tenant-Id");
            String deptIdStr = request.getHeader("X-Dept-Id");

            if (userIdStr == null || username == null) {
                return null;
            }

            return UserContext.builder()
                    .userId(userIdStr)
                    .username(username)
                    .tenantId(StrUtil.isNotBlank(tenantIdStr) ? tenantIdStr : null)
                    .deptId(StrUtil.isNotBlank(deptIdStr) ? deptIdStr : null)
                    .build();

        } catch (Exception e) {
            log.error("从请求头构建用户上下文失败", e);
            return null;
        }
    }
} 