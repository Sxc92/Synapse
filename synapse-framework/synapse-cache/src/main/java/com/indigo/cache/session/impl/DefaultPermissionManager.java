package com.indigo.cache.session.impl;

import com.indigo.cache.core.CacheService;
import com.indigo.cache.manager.CacheKeyGenerator;
import com.indigo.cache.session.PermissionManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 默认权限管理器实现
 * 基于缓存服务的权限管理
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
public class DefaultPermissionManager implements PermissionManager {

    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;

    public DefaultPermissionManager(CacheService cacheService, CacheKeyGenerator keyGenerator) {
        this.cacheService = cacheService;
        this.keyGenerator = keyGenerator;
    }

    @Override
    public void storeUserPermissions(String token, List<String> permissions, long expiration) {
        String permissionsKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "permissions", token);
        cacheService.setObject(permissionsKey, permissions, expiration);
        log.debug("Stored user permissions for token: {}", token);
    }

    @Override
    public void storeUserRoles(String token, List<String> roles, long expiration) {
        String rolesKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "roles", token);
        cacheService.setObject(rolesKey, roles, expiration);
        log.debug("Stored user roles for token: {}", token);
    }

    @Override
    public List<String> getUserPermissions(String token) {
        String permissionsKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "permissions", token);
        return cacheService.getObject(permissionsKey, List.class);
    }

    @Override
    public List<String> getUserRoles(String token) {
        String rolesKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "roles", token);
        return cacheService.getObject(rolesKey, List.class);
    }

    @Override
    public boolean hasPermission(String token, String permission) {
        List<String> permissions = getUserPermissions(token);
        return permissions != null && permissions.contains(permission);
    }

    @Override
    public boolean hasRole(String token, String role) {
        List<String> roles = getUserRoles(token);
        return roles != null && roles.contains(role);
    }

    @Override
    public void removeUserPermissions(String token) {
        String permissionsKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "permissions", token);
        String rolesKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "roles", token);
        cacheService.delete(permissionsKey);
        cacheService.delete(rolesKey);
        log.debug("Removed user permissions and roles for token: {}", token);
    }

    @Override
    public void extendUserPermissions(String token, long expiration) {
        String permissionsKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "permissions", token);
        String rolesKey = keyGenerator.generate(CacheKeyGenerator.Module.USER, "roles", token);
        cacheService.resetExpiry(permissionsKey, expiration);
        cacheService.resetExpiry(rolesKey, expiration);
        log.debug("Extended user permissions and roles for token: {}", token);
    }
} 