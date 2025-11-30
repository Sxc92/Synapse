package com.indigo.iam.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.constants.StandardErrorCode;
import com.indigo.core.exception.Ex;
import com.indigo.databases.utils.VoMapper;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.service.*;
import com.indigo.iam.sdk.dto.auth.LoginDTO;
import com.indigo.iam.sdk.dto.query.UserRoleQueryDTO;
import com.indigo.iam.sdk.vo.auth.LoginResponseVO;
import com.indigo.iam.sdk.vo.resource.*;
import com.indigo.iam.sdk.vo.users.UserRoleVO;
import com.indigo.security.core.AuthenticationService;
import com.indigo.security.model.AuthRequest;
import com.indigo.security.model.AuthResponse;
import com.indigo.security.model.auth.UsernamePasswordAuth;
import com.indigo.security.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.indigo.iam.sdk.enums.IamError.*;


public interface LoginService {

    /**
     * 用户登录
     *
     * @param dto 登录请求
     * @return 登录响应
     */
    LoginResponseVO login(LoginDTO dto);

}

/**
 * 登录服务
 * 负责用户登录认证和权限数据存储
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@Service
@RequiredArgsConstructor
class LoginServiceImpl implements LoginService {

    private final IUsersService iUsersService;
    private final AuthenticationService authenticationService;
    private final IUsersRoleService iUsersRoleService;
    private final IResourceService iResourceService;
    private final ISystemService iSystemService;
    private final IMenuService iMenuService;

    /**
     * 用户登录
     *
     * @param dto 登录请求
     * @return 登录响应
     */
    public LoginResponseVO login(LoginDTO dto) {
        // 1. 验证用户名密码
        Users user = validateUser(dto);

        // 2. 查询用户角色
        List<String> roles = getUserRoles(user.getId());
        if (CollUtil.isEmpty(roles)) {
            // 如果用户没有角色，不允许登录
            Ex.throwEx(StandardErrorCode.OPERATION_NOT_ALLOWED, "用户未分配角色，无法登录");
        }

        // 3. 查询用户的 Resource、System 和 System-Menu 树结构
        // 注意：菜单信息已包含在 systemMenuTree 中，不需要单独查询
        List<ResourceVO> resources = getUserResources(user.getId());
        List<SystemMenuTreeVO> systemMenuTree = getUserSystemMenuTree(user.getId());

        // 4. 从资源列表中提取权限编码（避免重复查询）
        // 注意：ResourceVO 已包含 permissions 字段，无需单独查询权限
        List<String> permissions = resources.stream()
                .map(ResourceVO::getPermissions)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        // 5. 构建权限数据（框架层会自动存储到缓存）
        // 只缓存 resources 和 systemMenuTree，不缓存 systems 和 permissionTree
        // 注意：菜单信息已包含在 systemMenuTree 中，不需要单独传递 menus
        AuthRequest.PermissionData permissionData = AuthRequest.PermissionData.builder()
                .systemMenuTree(systemMenuTree)
                .build();

        // 7. 构建认证请求（包含权限数据）
        AuthRequest authRequest = AuthRequest.builder()
                .authType(AuthRequest.AuthType.USERNAME_PASSWORD)
                .usernamePasswordAuth(UsernamePasswordAuth.builder()
                        .username(dto.getUsername())
                        .password(dto.getPassword())
                        .build())
                .userId(user.getId())
                .realName(user.getRealName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .avatar(user.getAvatar())
                .permissions(permissions)
                .permissionData(permissionData)
                .build();

        // 8. 调用认证服务（生成 Token，框架层会自动存储权限数据）
        AuthResponse authResponse = authenticationService.authenticate(authRequest);

        String token = authResponse.getAccessToken();
        long expiration = authResponse.getExpiresIn();

        log.info("用户登录成功: userId={}, username={}, token={}, systemMenuTreeSize={}", 
                user.getId(), user.getAccount(), token, systemMenuTree.size());
        // 9. 构建登录响应
        return LoginResponseVO.builder()
                .token(token)
                .expiresIn(expiration)
                .build();
//        return Result.success(response);
    }

    /**
     * 验证用户名密码
     *
     * @param dto 登录请求
     * @return 用户信息
     */
    private Users validateUser(LoginDTO dto) {
        // 1. 查询用户
        Users user = iUsersService.getOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getAccount, dto.getUsername())
        );

        if (user == null) {
            Ex.throwEx(USER_NOT_EXIST);
        }

        // 2. 检查用户状态
        if (Boolean.TRUE.equals(user.getLocked())) {
            Ex.throwEx(USER_LOCKED);
        }

        if (Boolean.FALSE.equals(user.getEnabled())) {
            Ex.throwEx(USER_DISABLED);
        }

        // 3. 验证密码
        if (!PasswordUtils.matches(dto.getPassword(), user.getPassword())) {
            Ex.throwEx(USER_PASSWORD_ERROR);
        }

        return user;
    }


    /**
     * 查询用户角色列表（返回角色编码）
     * 使用手写 SQL 进行多表联查，保证功能稳定
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    private List<String> getUserRoles(String userId) {
        // 使用手写 SQL 查询用户角色
        List<UserRoleVO> roleList = iUsersRoleService.getMapper().selectUserRoles(userId);

        if (CollUtil.isEmpty(roleList)) {
            return Collections.emptyList();
        }

        // 返回角色编码列表（去重）
        return roleList.stream()
                .map(UserRoleVO::getRoleCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 查询用户菜单列表
     * 使用 Mapper 层的 @Select SQL 查询，通过用户-角色-菜单关联查询
     *
     * @param userId 用户ID
     * @return 菜单VO列表
     */
    private List<MenuVO> getUserMenus(String userId) {
        // 使用 Mapper 层的 SQL 查询（已过滤状态和可见性）
        List<MenuVO> menus = iMenuService.getMapper().selectUserMenus(userId);

        if (CollUtil.isEmpty(menus)) {
            Ex.throwEx(USER_NOT_HAVE_MENU, "用户未分配菜单权限，无法登录");
        }
        
        return menus;
    }

    /**
     * 查询用户资源列表
     * 使用 Mapper 层的 @Select SQL 查询，通过用户-角色-资源关联查询
     *
     * @param userId 用户ID
     * @return 资源VO列表
     */
    private List<ResourceVO> getUserResources(String userId) {
        // 使用 Mapper 层的 SQL 查询
        List<ResourceVO> resources = iResourceService.getMapper().selectUserResources(userId);

        if (CollUtil.isEmpty(resources)) {
            Ex.throwEx(USER_NOT_HAVE_RESOURCE, "用户未分配资源权限，无法登录");
        }
        
        return resources;
    }

    /**
     * 查询用户系统列表
     * 使用 Mapper 层的 @Select SQL 查询，通过用户-角色-菜单-系统关联查询
     *
     * @param userId 用户ID
     * @return 系统VO列表
     */
    private List<SystemVO> getUserSystems(String userId) {
        // 使用 Mapper 层的 SQL 查询
        List<SystemVO> systems = iSystemService.getMapper().selectUserSystems(userId);

        if (CollUtil.isEmpty(systems)) {
            Ex.throwEx(USER_NOT_HAVE_SYSTEM, "用户未分配系统权限，无法登录");
        }
        
        return systems;
    }

    /**
     * 查询用户的 System-Menu 树结构
     * 以 System 为根节点，Menu 为子节点的树结构
     *
     * @param userId 用户ID
     * @return SystemMenuTreeVO 列表
     */
    private List<SystemMenuTreeVO> getUserSystemMenuTree(String userId) {
        // 1. 查询用户拥有的系统（如果查不到会抛出异常）
        List<SystemVO> systems = getUserSystems(userId);
        
        // 2. 查询用户拥有的菜单（已过滤状态和可见性，如果查不到会抛出异常）
        List<MenuVO> menus = getUserMenus(userId);
        
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
     * 支持多棵树的情况：多个根节点会生成多棵树，每个根节点及其子节点构成一棵独立的树
     *
     * @param menuList 平铺的菜单列表
     * @return 树结构的菜单列表（可能包含多棵树，每个根节点是一棵树的根）
     */
    private List<MenuTreeVO> buildMenuTree(List<MenuVO> menuList) {
        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }

        // 1. 转换为 MenuTreeVO 并构建 ID 映射
        var menuMap = menuList.stream()
                .map(menu -> VoMapper.mapToVo(menu, MenuTreeVO.class))
                .collect(Collectors.toMap(MenuTreeVO::getId, menu -> menu));

        // 2. 构建树结构并分离根节点
        var rootNodes = new ArrayList<MenuTreeVO>();
        menuMap.values().forEach(menu -> {
            var parentId = menu.getParentId();
            var parent = StrUtil.isNotBlank(parentId) ? menuMap.get(parentId) : null;
            if (parent != null) {
                // 有父节点，添加到父节点的 children
                parent.addChild(menu);
            } else {
                // 没有父节点或父节点不存在，作为根节点
                rootNodes.add(menu);
            }
        });

        return rootNodes;
    }

}

