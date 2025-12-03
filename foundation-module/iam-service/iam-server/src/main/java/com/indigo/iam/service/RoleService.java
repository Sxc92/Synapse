package com.indigo.iam.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.cache.infrastructure.RedisService;
import com.indigo.cache.manager.CacheKeyGenerator;
import com.indigo.cache.session.UserSessionService;
import com.indigo.core.context.UserContext;
import com.indigo.core.exception.Ex;
import com.indigo.iam.repository.entity.*;
import com.indigo.iam.repository.service.*;
import com.indigo.iam.sdk.dto.associated.RoleMenuDTO;
import com.indigo.iam.sdk.dto.associated.RolePermissionDTO;
import com.indigo.iam.sdk.dto.associated.RoleResourceDTO;
import com.indigo.iam.sdk.dto.opera.AddOrModifyRoleDTO;
import com.indigo.iam.sdk.vo.resource.*;
import com.indigo.security.core.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.indigo.iam.sdk.enums.IamError.*;
import static com.indigo.iam.sdk.enums.IamError.RESOURCE_NOT_IN_ROLE_SYSTEM;

/**
 * 角色服务接口
 * 提供角色相关的业务逻辑
 *
 * @author 史偕成
 * @date 2025/11/27
 */
public interface RoleService {


    /**
     * 添加或修改用户
     *
     * @param param 参数
     * @return
     */
    Boolean addOrModifyRole(AddOrModifyRoleDTO param);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    Boolean deleteRole(String id);

    /**
     * 修改状态
     * @param param
     * @return
     */
    Boolean modify(AddOrModifyRoleDTO param);
    /**
     * 根据角色ID获取权限树形结构（系统-菜单-资源）
     *
     * @param roleId 角色ID
     * @return 权限树形结构列表
     */
    RolePermissionVO getRolePermissionTree(String roleId);

    /**
     * 角色分配权限
     *
     * @param roleId 角色Id
     * @param param  参数
     * @return
     */
    Boolean assignPermissions(String roleId, RolePermissionDTO param);


    /**
     * 角色分配菜单
     *
     * @param param 角色菜单关联参数
     * @return 操作结果
     */
    boolean assignMenusToRole(RoleMenuDTO param);

    /**
     * 角色分配资源
     *
     * @param param 角色资源关联参数
     * @return 操作结果
     */
    boolean assignResourcesToRole(RoleResourceDTO param);
}

@Slf4j
@Service
@RequiredArgsConstructor
class RoleServiceImpl implements RoleService {

    private final IRoleMenuService iRoleMenuService;
    private final IRoleResourceService iRoleResourceService;
    private final IRoleSystemService iRoleSystemService;
    private final IUsersRoleService iUsersRoleService;
    private final IResourceService iResourceService;
    private final ISystemService iSystemService;
    private final IMenuService iMenuService;
    private final AuthenticationService authenticationService;
    private final UserSessionService userSessionService;
    private final RedisService redisService;
    private final CacheKeyGenerator cacheKeyGenerator;
    private final IRoleService iRoleService;
    @Override
    public RolePermissionVO getRolePermissionTree(String roleId) {
        return RolePermissionVO.builder()
                .systemIds(iRoleSystemService.getMapper().listRoleSystems(roleId))
                .menuIds(iRoleMenuService.getMapper().listRoleMenu(roleId))
                .resourceIds(iRoleResourceService.getMapper().listRoleResources(roleId))
                .build();
    }

    @Override
    public Boolean deleteRole(String id) {
        Roles roles = iRoleService.getById(id);
        if (roles == null) {
            Ex.throwEx(ROLE_NOT_EXIST);
        }
        // 删除用户角色关联
        iUsersRoleService.remove(new LambdaQueryWrapper<UsersRole>()
                .eq(UsersRole::getRoleId, id));
        // 删除角色系统关联（已注释：暂时取消 iam_role_system 表，通过菜单推导系统）
        // iRoleSystemService.remove(new LambdaQueryWrapper<RoleSystem>()
        //         .eq(RoleSystem::getRoleId, id));
        // 删除角色菜单关联
        iRoleMenuService.remove(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, id));
        // 删除角色资源关联
        iRoleResourceService.remove(new LambdaQueryWrapper<RoleResource>()
                .eq(RoleResource::getRoleId, id));
        // 删除角色
        return iRoleService.removeById(id);
    }

    @Override
    public Boolean modify(AddOrModifyRoleDTO param) {
        Roles roles = iRoleService.getById(param.getId());
        if (roles == null) {
            Ex.throwEx(ROLE_NOT_EXIST);
        }
        if (param.getStatus() != null) {
            roles.setStatus(!roles.getStatus());
        }
        return iRoleService.updateById(roles);
    }

    @Override
    public Boolean assignPermissions(String roleId, RolePermissionDTO param) {
        // 删除原有关联关系
        removeRolePermissions(roleId);
        
        // 批量保存新的关联关系
        saveRolePermissions(roleId, param);
        
        // 更新拥有该角色的用户的权限缓存
        updateUsersPermissionCache(roleId);
        
        return true;
    }

    @Override
    public Boolean addOrModifyRole(AddOrModifyRoleDTO param) {
        if (iRoleService.checkKeyUniqueness(param, "code")) {
            Ex.throwEx(ROLE_EXIST);
        }
        return iRoleService.saveOrUpdateFromDTO(param, Roles.class);
    }

    /**
     * 删除角色所有权限关联
     *
     * @param roleId 角色ID
     */
    private void removeRolePermissions(String roleId) {
        iRoleSystemService.remove(new LambdaQueryWrapper<RoleSystem>()
                .eq(RoleSystem::getRoleId, roleId));
        iRoleMenuService.remove(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId));
        iRoleResourceService.remove(new LambdaQueryWrapper<RoleResource>()
                .eq(RoleResource::getRoleId, roleId));
    }

    /**
     * 批量保存角色权限关联
     *
     * @param roleId 角色ID
     * @param param  权限参数
     */
    private void saveRolePermissions(String roleId, RolePermissionDTO param) {
        // 批量保存系统关联
        List<RoleSystem> roleSystems = param.getSystemIds().stream()
                .map(systemId -> RoleSystem.builder()
                        .roleId(roleId)
                        .systemId(systemId)
                        .build())
                .collect(Collectors.toList());
        iRoleSystemService.saveBatch(roleSystems);

        // 批量保存菜单关联
        List<RoleMenu> roleMenus = param.getMenuIds().stream()
                .map(menuId -> RoleMenu.builder()
                        .roleId(roleId)
                        .menuId(menuId)
                        .build())
                .collect(Collectors.toList());
        iRoleMenuService.saveBatch(roleMenus);

        // 批量保存资源关联
        List<RoleResource> roleResources = param.getResourceIds().stream()
                .map(resourceId -> RoleResource.builder()
                        .roleId(roleId)
                        .resourceId(resourceId)
                        .build())
                .collect(Collectors.toList());
        iRoleResourceService.saveBatch(roleResources);
    }

    /**
     * 更新拥有该角色的用户的权限缓存
     * 不更换token，只更新缓存中的权限信息
     *
     * @param roleId 角色ID
     */
    private void updateUsersPermissionCache(String roleId) {
        // 1. 查找拥有该角色的所有用户
        List<UsersRole> usersRoles = iUsersRoleService.list(
                new LambdaQueryWrapper<UsersRole>()
                        .eq(UsersRole::getRoleId, roleId)
        );

        if (CollUtil.isEmpty(usersRoles)) {
            log.debug("角色 {} 没有关联用户，跳过权限缓存更新", roleId);
            return;
        }

        // 2. 获取用户ID列表（去重）
        List<String> userIds = usersRoles.stream()
                .map(UsersRole::getUserId)
                .distinct()
                .toList();

        log.info("开始更新角色 {} 的 {} 个用户的权限缓存", roleId, userIds.size());

        // 3. 遍历每个用户，更新其权限缓存
        for (String userId : userIds) {
            try {
                updateUserPermissionCache(userId);
            } catch (Exception e) {
                log.error("更新用户 {} 的权限缓存失败", userId, e);
                // 继续处理其他用户，不中断整个流程
            }
        }

        log.info("完成更新角色 {} 的用户权限缓存", roleId);
    }

    /**
     * 更新单个用户的权限缓存
     * 通过扫描 Redis 中的 session key，找到匹配的 userId，然后更新其权限缓存
     *
     * @param userId 用户ID
     */
    private void updateUserPermissionCache(String userId) {
        // 1. 重新计算用户的权限数据（类似登录时的逻辑）
        List<ResourceVO> resources = getUserResources(userId);
        List<SystemMenuTreeVO> systemMenuTree = getUserSystemMenuTree(userId);

        // 2. 从资源列表中提取权限编码
        List<String> permissions = resources.stream()
                .map(ResourceVO::getPermissions)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        // 3. 更新用户所有会话的权限缓存
        // 通过扫描 Redis 中的 session key，找到匹配的 userId，然后更新其权限缓存
        updateUserSessionsPermissionCache(userId, resources, systemMenuTree, permissions);
    }

    /**
     * 更新用户所有会话的权限缓存
     * 通过扫描 Redis 中的 session key，找到匹配的 userId，然后更新其权限缓存
     *
     * @param userId         用户ID
     * @param resources      资源列表
     * @param systemMenuTree 系统菜单树
     * @param permissions    权限列表
     */
    private void updateUserSessionsPermissionCache(String userId,
                                                    List<ResourceVO> resources,
                                                    List<SystemMenuTreeVO> systemMenuTree,
                                                    List<String> permissions) {
        // 获取 token 的剩余过期时间（用于设置新的过期时间）
        // 这里使用默认的 2 小时（7200 秒）
        long expiration = 2 * 60 * 60L;

        try {
            // 1. 扫描所有 session key
            String pattern = cacheKeyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "*");
            Set<String> keys = redisService.scan(pattern);

            if (CollUtil.isEmpty(keys)) {
                log.debug("未找到任何在线会话，跳过用户 {} 的权限缓存更新", userId);
                return;
            }

            // 2. 遍历所有 session key，找到匹配的 userId
            int updatedCount = 0;
            for (String key : keys) {
                try {
                    // 从 key 中提取 token
                    String token = extractTokenFromKey(key);
                    if (token == null) {
                        continue;
                    }

                    // 获取用户会话信息
                    UserContext userContext = userSessionService.getUserSession(token);
                    if (userContext == null) {
                        continue;
                    }

                    // 检查 userId 是否匹配
                    if (!userId.equals(userContext.getUserId())) {
                        continue;
                    }

                    // 3. 更新该 token 的权限缓存
                    // 使用 AuthenticationService 的方法更新权限缓存
                    authenticationService.storeUserPermissionData(
                            token,
                            null, // 不再单独存储菜单，菜单信息在 systemMenuTree 中
                            resources,
                            null, // 不缓存 systems
                            expiration
                    );

                    // 存储系统菜单树
                    authenticationService.storeUserSystemMenuTree(token, systemMenuTree, expiration);

                    // 更新权限列表
                    userSessionService.storeUserPermissions(token, permissions, expiration);

                    updatedCount++;
                    log.debug("已更新用户 {} 的 token {} 的权限缓存", userId, token);

                } catch (Exception e) {
                    log.warn("更新用户 {} 的权限缓存失败: key={}", userId, key, e);
                    // 继续处理其他 token，不中断整个流程
                }
            }

            if (updatedCount > 0) {
                log.info("成功更新用户 {} 的 {} 个会话的权限缓存", userId, updatedCount);
            } else {
                log.debug("用户 {} 没有在线会话，跳过权限缓存更新", userId);
            }

        } catch (Exception e) {
            log.error("更新用户 {} 的权限缓存失败", userId, e);
        }
    }

    /**
     * 从 session key 中提取 token
     * key 格式：user:session:{token}
     *
     * @param key session key
     * @return token，如果无法提取则返回 null
     */
    private String extractTokenFromKey(String key) {
        try {
            // key 格式：user:session:{token}
            String prefix = cacheKeyGenerator.generate(CacheKeyGenerator.Module.USER, "session", "");
            if (key.startsWith(prefix)) {
                return key.substring(prefix.length());
            }
            // 如果格式不匹配，尝试从最后一个冒号或斜杠后提取
            int lastIndex = key.lastIndexOf(":");
            if (lastIndex > 0 && lastIndex < key.length() - 1) {
                return key.substring(lastIndex + 1);
            }
            return null;
        } catch (Exception e) {
            log.warn("从 key 中提取 token 失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 查询用户资源列表
     * 复用 LoginService 的逻辑
     *
     * @param userId 用户ID
     * @return 资源VO列表
     */
    private List<ResourceVO> getUserResources(String userId) {
        List<ResourceVO> resources = iResourceService.getMapper().selectUserResources(userId);
        if (CollUtil.isEmpty(resources)) {
            return Collections.emptyList();
        }
        return resources;
    }

    /**
     * 查询用户系统列表
     * 复用 LoginService 的逻辑
     *
     * @param userId 用户ID
     * @return 系统VO列表
     */
    private List<SystemVO> getUserSystems(String userId) {
        List<SystemVO> systems = iSystemService.getMapper().selectUserSystems(userId);
        if (CollUtil.isEmpty(systems)) {
            return Collections.emptyList();
        }
        return systems;
    }

    /**
     * 查询用户菜单列表
     * 复用 LoginService 的逻辑
     *
     * @param userId 用户ID
     * @return 菜单VO列表
     */
    private List<MenuVO> getUserMenus(String userId) {
        List<MenuVO> menus = iMenuService.getMapper().selectUserMenus(userId);
        if (CollUtil.isEmpty(menus)) {
            return Collections.emptyList();
        }
        return menus;
    }

    /**
     * 查询用户的 System-Menu 树结构
     * 复用 LoginService 的逻辑
     *
     * @param userId 用户ID
     * @return SystemMenuTreeVO 列表
     */
    private List<SystemMenuTreeVO> getUserSystemMenuTree(String userId) {
        // 1. 查询用户拥有的系统
        List<SystemVO> systems = getUserSystems(userId);
        if (CollUtil.isEmpty(systems)) {
            return Collections.emptyList();
        }

        // 2. 查询用户拥有的菜单
        List<MenuVO> menus = getUserMenus(userId);
        if (CollUtil.isEmpty(menus)) {
            return Collections.emptyList();
        }

        // 3. 构建菜单树结构
        List<MenuTreeVO> menuTreeList = buildMenuTree(menus);

        // 4. 构建 System-Menu 树结构
        List<SystemMenuTreeVO> systemMenuTreeList = new ArrayList<>();
        for (SystemVO system : systems) {
            SystemMenuTreeVO systemMenuTree = new SystemMenuTreeVO();
            systemMenuTree.setId(system.getId());
            systemMenuTree.setName(system.getName());
            systemMenuTree.setLogo(system.getLogo());
            systemMenuTree.setCreateTime(system.getCreateTime());
            systemMenuTree.setModifyTime(system.getModifyTime());

            // 过滤出属于当前系统的菜单树
            List<MenuTreeVO> systemMenus = menuTreeList.stream()
                    .filter(menu -> system.getId().equals(menu.getSystemId()))
                    .collect(Collectors.toList());

            systemMenuTree.setMenus(systemMenus);
            systemMenuTreeList.add(systemMenuTree);
        }

        return systemMenuTreeList;
    }

    /**
     * 构建菜单树结构
     * 复用 LoginService 的逻辑
     *
     * @param menuList 平铺的菜单列表
     * @return 树结构的菜单列表
     */
    private List<MenuTreeVO> buildMenuTree(List<MenuVO> menuList) {
        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }

        // 1. 转换为 MenuTreeVO 并构建 ID 映射
        var menuMap = menuList.stream()
                .map(menu -> {
                    MenuTreeVO treeVO = new MenuTreeVO();
                    treeVO.setId(menu.getId());
                    treeVO.setName(menu.getName());
                    treeVO.setRouter(menu.getRouter());
                    treeVO.setIcon(menu.getIcon());
                    treeVO.setComponent(menu.getComponent());
                    treeVO.setParentId(menu.getParentId());
                    treeVO.setSystemId(menu.getSystemId());
                    treeVO.setVisible(menu.getVisible());
                    treeVO.setStatus(menu.getStatus());
                    return treeVO;
                })
                .collect(Collectors.toMap(MenuTreeVO::getId, menu -> menu));

        // 2. 构建树结构并分离根节点
        var rootNodes = new ArrayList<MenuTreeVO>();
        menuMap.values().forEach(menu -> {
            var parentId = menu.getParentId();
            var parent = StrUtil.isNotBlank(parentId) ? menuMap.get(parentId) : null;
            if (parent != null) {
                // 有父节点，添加到父节点的 children
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(menu);
            } else {
                // 没有父节点或父节点不存在，作为根节点
                rootNodes.add(menu);
            }
        });

        return rootNodes;
    }

    @Override
    public boolean assignMenusToRole(RoleMenuDTO param) {
        Roles role = iRoleService.getById(param.getId());
        if (role == null) {
            Ex.throwEx(ROLE_NOT_EXIST);
        }
        // 删除原有菜单关联
        iRoleMenuService.remove(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, param.getId()));
        // 如果菜单列表为空，直接返回
        if (CollUtil.isEmpty(param.getMenuIds())) {
            return true;
        }
        // 批量保存新的菜单关联
        List<RoleMenu> roleMenus = new ArrayList<>();
        param.getMenuIds().forEach(menuId -> {
            roleMenus.add(RoleMenu.builder()
                    .roleId(param.getId())
                    .menuId(menuId)
                    .build());
        });
        return iRoleMenuService.saveBatch(roleMenus);
    }

    @Override
    public boolean assignResourcesToRole(RoleResourceDTO param) {
        Roles role = iRoleService.getById(param.getId());
        if (role == null) {
            Ex.throwEx(ROLE_NOT_EXIST);
        }

        // 已注释：暂时取消 iam_role_system 表，通过菜单推导系统关系
        // 不再需要校验角色是否拥有资源所属的系统权限，改为通过角色菜单推导系统
        if (CollUtil.isNotEmpty(param.getResourceIds())) {
            // 1. 通过角色菜单推导角色拥有的系统ID列表
            List<RoleMenu> roleMenus = iRoleMenuService.list(
                    new LambdaQueryWrapper<RoleMenu>()
                            .eq(RoleMenu::getRoleId, param.getId()));
            if (CollUtil.isEmpty(roleMenus)) {
                Ex.throwEx(ROLE_NOT_HAVE_SYSTEM);
            }
            List<String> menuIds = roleMenus.stream()
                    .map(RoleMenu::getMenuId)
                    .distinct()
                    .toList();
            List<Menu> menus = iMenuService.listByIds(menuIds);
            List<String> roleSystemIds = menus.stream()
                    .map(Menu::getSystemId)
                    .filter(StrUtil::isNotBlank)
                    .distinct()
                    .toList();

            // 2. 查询所有资源，通过资源关联的菜单推导系统，校验资源所属的系统是否在角色已拥有的系统列表中
            List<IamResource> resources = iResourceService.listByIds(param.getResourceIds());
            for (IamResource resource : resources) {
                if (StrUtil.isBlank(resource.getMenuId())) {
                    Ex.throwEx(RESOURCE_NOT_IN_ROLE_SYSTEM,
                            "资源[" + resource.getName() + "]未关联菜单");
                }
                // 通过菜单查询系统ID
                Menu menu = iMenuService.getById(resource.getMenuId());
                if (menu == null || StrUtil.isBlank(menu.getSystemId())) {
                    Ex.throwEx(RESOURCE_NOT_IN_ROLE_SYSTEM,
                            "资源[" + resource.getName() + "]关联的菜单不存在或未关联系统");
                }
                if (!roleSystemIds.contains(menu.getSystemId())) {
                    Ex.throwEx(RESOURCE_NOT_IN_ROLE_SYSTEM,
                            "资源[" + resource.getName() + "]不属于角色已拥有的系统");
                }
            }
        }

        // 删除原有资源关联
        iRoleResourceService.remove(new LambdaQueryWrapper<RoleResource>()
                .eq(RoleResource::getRoleId, param.getId()));
        // 如果资源列表为空，直接返回
        if (CollUtil.isEmpty(param.getResourceIds())) {
            return true;
        }
        // 批量保存新的资源关联
        List<RoleResource> roleResources = new ArrayList<>();
        param.getResourceIds().forEach(resourceId -> {
            roleResources.add(RoleResource.builder()
                    .roleId(param.getId())
                    .resourceId(resourceId)
                    .build());
        });
        return iRoleResourceService.saveBatch(roleResources);
    }
}
