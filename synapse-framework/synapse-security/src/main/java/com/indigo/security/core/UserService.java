package com.indigo.security.core;

import com.indigo.security.model.UserPrincipal;

import java.util.List;

/**
 * 用户服务接口
 * 负责用户信息的查询和验证
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public interface UserService {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户主体信息
     */
    UserPrincipal findByUsername(String username);

    /**
     * 根据用户ID查找用户
     *
     * @param userId 用户ID
     * @return 用户主体信息
     */
    UserPrincipal findById(Long userId);

    /**
     * 验证用户密码
     *
     * @param username 用户名
     * @param password 密码
     * @return 验证成功返回用户信息，失败返回null
     */
    UserPrincipal validatePassword(String username, String password);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     * @param loginIp 登录IP
     */
    void updateLastLoginTime(Long userId, String loginIp);

    /**
     * 检查用户是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查用户是否被锁定
     *
     * @param userId 用户ID
     * @return 是否被锁定
     */
    boolean isUserLocked(Long userId);

    /**
     * 检查用户是否被禁用
     *
     * @param userId 用户ID
     * @return 是否被禁用
     */
    boolean isUserDisabled(Long userId);
} 