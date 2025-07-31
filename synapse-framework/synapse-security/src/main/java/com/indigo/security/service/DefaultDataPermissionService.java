package com.indigo.security.service;

import com.indigo.cache.core.CacheService;
import com.indigo.security.model.DataPermissionRule;
import com.indigo.security.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限服务默认实现
 *
 * @author 史偕成
 * @date 2024/01/09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDataPermissionService implements DataPermissionService {

    private static final String RULE_CACHE_PREFIX = "data_permission_rule:";
    private static final String USER_RULES_CACHE_PREFIX = "user_data_rules:";
    private static final String ROLE_RULES_CACHE_PREFIX = "role_data_rules:";
    private static final String DEPT_RULES_CACHE_PREFIX = "dept_data_rules:";
    private static final long CACHE_TIMEOUT = 3600; // 1小时

    private final CacheService cacheService;

    @Override
    public Long addRule(DataPermissionRule rule) {
        // 这里应该调用数据库服务保存规则
        // 为演示，我们使用缓存模拟
        rule.setRuleId(System.currentTimeMillis()); // 模拟生成ID
        String cacheKey = RULE_CACHE_PREFIX + rule.getRuleId();
        cacheService.getRedisService().setObject(cacheKey, rule, CACHE_TIMEOUT);

        // 更新相关缓存
        updateSubjectRulesCache(rule);

        return rule.getRuleId();
    }

    @Override
    public void updateRule(DataPermissionRule rule) {
        if (rule.getRuleId() == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }

        String cacheKey = RULE_CACHE_PREFIX + rule.getRuleId();
        cacheService.getRedisService().setObject(cacheKey, rule, CACHE_TIMEOUT);

        // 更新相关缓存
        updateSubjectRulesCache(rule);
    }

    @Override
    public void deleteRule(String ruleId) {
        String cacheKey = RULE_CACHE_PREFIX + ruleId;
        DataPermissionRule rule = getRule(ruleId);
        if (rule != null) {
            // 删除规则缓存
            cacheService.getRedisService().delete(cacheKey);
            // 清除相关主体的规则缓存
            clearSubjectRulesCache(rule);
        }
    }

    @Override
    public DataPermissionRule getRule(String ruleId) {
        String cacheKey = RULE_CACHE_PREFIX + ruleId;
        return cacheService.getRedisService().getObject(cacheKey, DataPermissionRule.class);
    }

    @Override
    public List<DataPermissionRule> getUserRules(String userId) {
        String cacheKey = USER_RULES_CACHE_PREFIX + userId;
        List<DataPermissionRule> rules = cacheService.getRedisService().getObject(cacheKey, List.class);
        return rules != null ? rules : new ArrayList<>();
    }

    @Override
    public List<DataPermissionRule> getRoleRules(String roleId) {
        String cacheKey = ROLE_RULES_CACHE_PREFIX + roleId;
        List<DataPermissionRule> rules = cacheService.getRedisService().getObject(cacheKey, List.class);
        return rules != null ? rules : new ArrayList<>();
    }

    @Override
    public List<DataPermissionRule> getDepartmentRules(String deptId) {
        String cacheKey = DEPT_RULES_CACHE_PREFIX + deptId;
        List<DataPermissionRule> rules = cacheService.getRedisService().getObject(cacheKey, List.class);
        return rules != null ? rules : new ArrayList<>();
    }

    @Override
    public boolean hasPermission(UserPrincipal user, String resourceType, DataPermissionRule.PermissionType permissionType) {
        // 获取用户所有适用的规则
        List<DataPermissionRule> allRules = getAllApplicableRules(user);

        // 按优先级排序并过滤出匹配的规则
        Optional<DataPermissionRule> matchingRule = allRules.stream()
                .filter(rule -> rule.getEnabled())
                .filter(rule -> rule.getResourceType().equals(resourceType))
                .filter(rule -> rule.getPermissionTypes().contains(permissionType))
                .min(Comparator.comparing(DataPermissionRule::getPriority));

        // 如果找到匹配的规则，根据规则类型返回结果
        return matchingRule.map(rule -> rule.getRuleType() == DataPermissionRule.RuleType.ALLOW)
                .orElse(false); // 默认拒绝
    }

    @Override
    public String getDataScope(UserPrincipal user, String resourceType) {
        // 获取用户所有适用的规则
        List<DataPermissionRule> allRules = getAllApplicableRules(user);

        // 获取优先级最高的数据范围规则
        Optional<DataPermissionRule> highestPriorityRule = allRules.stream()
                .filter(rule -> rule.getEnabled())
                .filter(rule -> rule.getResourceType().equals(resourceType))
                .filter(rule -> rule.getRuleType() == DataPermissionRule.RuleType.ALLOW)
                .min(Comparator.comparing(DataPermissionRule::getPriority));

        if (highestPriorityRule.isPresent()) {
            DataPermissionRule rule = highestPriorityRule.get();
            switch (rule.getDataScopeType()) {
                case ALL:
                    return "1=1"; // 无限制
                case DEPARTMENT:
                    return "dept_id = " + user.getDeptId();
                case DEPARTMENT_AND_BELOW:
                    return "dept_id IN (SELECT id FROM department WHERE path LIKE '" +
                            getDepartmentPath(user.getDeptId()) + "%')";
                case PERSONAL:
                    return "create_user_id = " + user.getUserId();
                case CUSTOM:
                    return rule.getCustomScope();
                default:
                    return "1=2"; // 默认无权限
            }
        }

        return "1=2"; // 默认无权限
    }

    @Override
    public Map<String, Object> getCustomDataScope(UserPrincipal user, String resourceType) {
        // 获取用户所有适用的规则
        List<DataPermissionRule> allRules = getAllApplicableRules(user);

        // 获取优先级最高的自定义数据范围规则
        Optional<DataPermissionRule> customRule = allRules.stream()
                .filter(rule -> rule.getEnabled())
                .filter(rule -> rule.getResourceType().equals(resourceType))
                .filter(rule -> rule.getRuleType() == DataPermissionRule.RuleType.ALLOW)
                .filter(rule -> rule.getDataScopeType() == DataPermissionRule.DataScopeType.CUSTOM)
                .min(Comparator.comparing(DataPermissionRule::getPriority));

        return customRule.map(DataPermissionRule::getConditions)
                .orElse(Collections.emptyMap());
    }

    @Override
    public Map<String, Boolean> batchCheckPermissions(UserPrincipal user, List<String> resourceTypes,
                                                      DataPermissionRule.PermissionType permissionType) {
        return resourceTypes.stream()
                .collect(Collectors.toMap(
                        resourceType -> resourceType,
                        resourceType -> hasPermission(user, resourceType, permissionType)
                ));
    }

    // 私有辅助方法

    private List<DataPermissionRule> getAllApplicableRules(UserPrincipal user) {

        // 添加用户特定规则
        List<DataPermissionRule> allRules = new ArrayList<>(getUserRules(user.getUserId()));

        // 添加角色规则
        if (user.getRoles() != null) {
            for (String role : user.getRoles()) {
                try {
                    allRules.addAll(getRoleRules(role));
                } catch (NumberFormatException e) {
                    log.warn("Invalid role ID format: {}", role);
                }
            }
        }

        // 添加部门规则
        if (user.getDeptId() != null) {
            allRules.addAll(getDepartmentRules(user.getDeptId()));
        }

        return allRules;
    }

    private void updateSubjectRulesCache(DataPermissionRule rule) {
        String cacheKey = null;
        switch (rule.getSubjectType()) {
            case USER:
                cacheKey = USER_RULES_CACHE_PREFIX + rule.getSubjectId();
                break;
            case ROLE:
                cacheKey = ROLE_RULES_CACHE_PREFIX + rule.getSubjectId();
                break;
            case DEPARTMENT:
                cacheKey = DEPT_RULES_CACHE_PREFIX + rule.getSubjectId();
                break;
        }

        if (cacheKey != null) {
            List<DataPermissionRule> rules = new ArrayList<>();
            List<DataPermissionRule> existingRules = cacheService.getRedisService().getObject(cacheKey, List.class);
            if (existingRules != null) {
                rules.addAll(existingRules);
            }
            rules.add(rule);
            cacheService.getRedisService().setObject(cacheKey, rules, CACHE_TIMEOUT);
        }
    }

    private void clearSubjectRulesCache(DataPermissionRule rule) {
        String cacheKey = null;
        switch (rule.getSubjectType()) {
            case USER:
                cacheKey = USER_RULES_CACHE_PREFIX + rule.getSubjectId();
                break;
            case ROLE:
                cacheKey = ROLE_RULES_CACHE_PREFIX + rule.getSubjectId();
                break;
            case DEPARTMENT:
                cacheKey = DEPT_RULES_CACHE_PREFIX + rule.getSubjectId();
                break;
        }

        if (cacheKey != null) {
            cacheService.getRedisService().delete(cacheKey);
        }
    }

    private String getDepartmentPath(String deptId) {
        // 这里应该调用组织架构服务获取部门路径
        // 为演示返回模拟值
        return String.valueOf(deptId);
    }
} 