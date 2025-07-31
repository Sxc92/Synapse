package com.indigo.cache.session;

import java.util.List;

/**
 * 权限管理器接口
 * 定义用户权限和角色管理的核心操作
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public interface PermissionManager {

    /**
     * 存储用户权限信息
     *
     * @param token       访问令牌
     * @param permissions 权限列表
     * @param expiration  过期时间（秒）
     */
    void storeUserPermissions(String token, List<String> permissions, long expiration);

    /**
     * 存储用户角色信息
     *
     * @param token      访问令牌
     * @param roles      角色列表
     * @param expiration 过期时间（秒）
     */
    void storeUserRoles(String token, List<String> roles, long expiration);

    /**
     * 获取用户权限列表
     *
     * @param token 访问令牌
     * @return 权限列表
     */
    List<String> getUserPermissions(String token);

    /**
     * 获取用户角色列表
     *
     * @param token 访问令牌
     * @return 角色列表
     */
    List<String> getUserRoles(String token);

    /**
     * 检查用户是否有指定权限
     *
     * @param token      访问令牌
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean hasPermission(String token, String permission);

    /**
     * 检查用户是否有指定角色
     *
     * @param token 访问令牌
     * @param role  角色标识
     * @return 是否有角色
     */
    boolean hasRole(String token, String role);

    /**
     * 删除用户权限和角色信息
     *
     * @param token 访问令牌
     */
    void removeUserPermissions(String token);

    /**
     * 延长用户权限和角色过期时间
     *
     * @param token      访问令牌
     * @param expiration 新的过期时间（秒）
     */
    void extendUserPermissions(String token, long expiration);
} 