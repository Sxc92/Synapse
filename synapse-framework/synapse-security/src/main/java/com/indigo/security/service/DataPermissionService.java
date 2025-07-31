package com.indigo.security.service;

import com.indigo.security.model.DataPermissionRule;
import com.indigo.security.model.UserPrincipal;

import java.util.List;
import java.util.Map;

/**
 * 数据权限服务接口
 * 提供数据权限规则的管理和验证功能
 *
 * @author 史偕成
 * @date 2024/01/09
 */
public interface DataPermissionService {

    /**
     * 添加数据权限规则
     *
     * @param rule 权限规则
     * @return 规则ID
     */
    Long addRule(DataPermissionRule rule);

    /**
     * 更新数据权限规则
     *
     * @param rule 权限规则
     */
    void updateRule(DataPermissionRule rule);

    /**
     * 删除数据权限规则
     *
     * @param ruleId 规则ID
     */
    void deleteRule(String ruleId);

    /**
     * 获取数据权限规则
     *
     * @param ruleId 规则ID
     * @return 权限规则
     */
    DataPermissionRule getRule(String ruleId);

    /**
     * 获取用户的所有数据权限规则
     *
     * @param userId 用户ID
     * @return 权限规则列表
     */
    List<DataPermissionRule> getUserRules(String userId);

    /**
     * 获取角色的所有数据权限规则
     *
     * @param roleId 角色ID
     * @return 权限规则列表
     */
    List<DataPermissionRule> getRoleRules(String roleId);

    /**
     * 获取部门的所有数据权限规则
     *
     * @param deptId 部门ID
     * @return 权限规则列表
     */
    List<DataPermissionRule> getDepartmentRules(String deptId);

    /**
     * 检查用户是否有权限访问指定资源
     *
     * @param user 用户信息
     * @param resourceType 资源类型
     * @param permissionType 权限类型
     * @return 是否有权限
     */
    boolean hasPermission(UserPrincipal user, String resourceType, DataPermissionRule.PermissionType permissionType);

    /**
     * 获取用户对指定资源的数据范围
     *
     * @param user 用户信息
     * @param resourceType 资源类型
     * @return 数据范围条件
     */
    String getDataScope(UserPrincipal user, String resourceType);

    /**
     * 获取用户对指定资源的自定义数据范围条件
     *
     * @param user 用户信息
     * @param resourceType 资源类型
     * @return 自定义数据范围条件
     */
    Map<String, Object> getCustomDataScope(UserPrincipal user, String resourceType);

    /**
     * 批量验证用户对多个资源的访问权限
     *
     * @param user 用户信息
     * @param resourceTypes 资源类型列表
     * @param permissionType 权限类型
     * @return 资源类型到权限结果的映射
     */
    Map<String, Boolean> batchCheckPermissions(UserPrincipal user, List<String> resourceTypes, 
                                             DataPermissionRule.PermissionType permissionType);
} 