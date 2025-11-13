package com.indigo.iam.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.exception.Ex;
import com.indigo.databases.utils.VoMapper;
import com.indigo.iam.repository.entity.IamSystem;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.service.ISystemService;
import com.indigo.iam.repository.service.IUsersRoleService;
import com.indigo.iam.repository.service.IUsersService;
import com.indigo.iam.sdk.dto.auth.LoginDTO;
import com.indigo.iam.sdk.dto.query.UserMenuQueryDTO;
import com.indigo.iam.sdk.dto.query.UserPermissionQueryDTO;
import com.indigo.iam.sdk.dto.query.UserResourceQueryDTO;
import com.indigo.iam.sdk.dto.query.UserRoleQueryDTO;
import com.indigo.iam.sdk.vo.auth.LoginResponseVO;
import com.indigo.iam.sdk.vo.resource.MenuVO;
import com.indigo.iam.sdk.vo.resource.ResourceVO;
import com.indigo.iam.sdk.vo.resource.SystemVO;
import com.indigo.iam.sdk.vo.users.UserMenuVO;
import com.indigo.iam.sdk.vo.users.UserPermissionVO;
import com.indigo.iam.sdk.vo.users.UserResourceVO;
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
import java.util.Set;
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
class LoginServiceImpl implements LoginService{

    private final IUsersService iUsersService;
    private final AuthenticationService authenticationService;
    private final IUsersRoleService iUsersRoleService;
    private final ISystemService iSystemService;
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
            // 如果用户没有角色，返回空列表而不是 null
            roles = Collections.emptyList();
        }

        // 3. 查询用户权限
        List<String> permissions = getUserPermissions(user.getId());
        if (CollUtil.isEmpty(permissions)) {
            // 如果用户没有权限，返回空列表而不是 null
            permissions = Collections.emptyList();
        }

        // 4. 查询用户的 Menu、Resource、System
        List<MenuVO> menus = getUserMenus(user.getId());
        List<ResourceVO> resources = getUserResources(user.getId());
        List<SystemVO> systems = getUserSystems(user.getId());

        // 5. 构建认证请求
        AuthRequest authRequest = AuthRequest.builder()
                .authType(AuthRequest.AuthType.USERNAME_PASSWORD)
                .usernamePasswordAuth(UsernamePasswordAuth.builder()
                        .username(dto.getUsername())
                        .password(dto.getPassword())
                        .build())
                .userId(user.getId())
                .roles(roles)
                .permissions(permissions)
                .build();

        // 6. 调用认证服务（生成 Token）
        AuthResponse authResponse = authenticationService.authenticate(authRequest);

        String token = authResponse.getAccessToken();
        long expiration = authResponse.getExpiresIn();

        // 7. 存储 Menu、Resource、System 到缓存
        authenticationService.storeUserPermissionData(
                token,
                menus,
                resources,
                systems,
                expiration
        );
        log.info("用户登录成功: userId={}, username={}, token={}", user.getId(), user.getAccount(), token);
        // 8. 构建登录响应
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
     * 使用多表查询，一次查询完成用户角色关联查询
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    private List<String> getUserRoles(String userId) {
        // 构建查询条件
        UserRoleQueryDTO queryDTO = UserRoleQueryDTO.builder()
                .userId(userId)
                .build();

        // 使用多表查询，一次查询完成
        List<UserRoleVO> roleList = iUsersRoleService.listWithVoMapping(queryDTO, UserRoleVO.class);

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
     * 查询用户权限列表（返回权限编码）
     * 使用多表查询，一次查询完成用户-角色-资源关联查询
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    private List<String> getUserPermissions(String userId) {
        // 构建查询条件
        UserPermissionQueryDTO queryDTO = UserPermissionQueryDTO.builder()
                .userId(userId)
                .build();

        // 使用多表查询，一次查询完成
        List<UserPermissionVO> permissionList = iUsersRoleService.listWithVoMapping(queryDTO, UserPermissionVO.class);

        if (CollUtil.isEmpty(permissionList)) {
            return Collections.emptyList();
        }

        // 返回权限编码列表（去重）
        return permissionList.stream()
                .map(UserPermissionVO::getPermissions)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 查询用户菜单列表
     * 使用多表查询，一次查询完成用户-角色-菜单关联查询
     *
     * @param userId 用户ID
     * @return 菜单VO列表
     */
    private List<MenuVO> getUserMenus(String userId) {
        // 构建查询条件
        UserMenuQueryDTO queryDTO = UserMenuQueryDTO.builder()
                .userId(userId)
                .build();

        // 使用多表查询，一次查询完成
        List<UserMenuVO> menuList = iUsersRoleService.listWithVoMapping(queryDTO, UserMenuVO.class);

        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }

        // 过滤：只返回启用且可见的菜单，并去重
        List<MenuVO> result = menuList.stream()
                .filter(menu -> menu.getMenuId() != null
                        && Boolean.TRUE.equals(menu.getStatus())
                        && Boolean.TRUE.equals(menu.getVisible()))
                .map(menu -> {
                    // 转换为 MenuVO
                    MenuVO menuVO = new MenuVO();
                    menuVO.setId(menu.getMenuId());
                    menuVO.setSystemId(menu.getSystemId());
                    menuVO.setParentId(menu.getParentId());
                    menuVO.setCode(menu.getCode());
                    menuVO.setName(menu.getName());
                    menuVO.setRouter(menu.getRouter());
                    menuVO.setComponent(menu.getComponent());
                    menuVO.setIcon(menu.getIcon());
                    menuVO.setStatus(menu.getStatus());
                    menuVO.setVisible(menu.getVisible());
                    menuVO.setCreateTime(menu.getCreateTime());
                    menuVO.setModifyTime(menu.getModifyTime());
                    return menuVO;
                })
                .distinct()
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 查询用户资源列表
     * 使用多表查询，一次查询完成用户-角色-资源关联查询
     *
     * @param userId 用户ID
     * @return 资源VO列表
     */
    private List<ResourceVO> getUserResources(String userId) {
        // 构建查询条件
        UserResourceQueryDTO queryDTO = UserResourceQueryDTO.builder()
                .userId(userId)
                .build();

        // 使用多表查询，一次查询完成
        List<UserResourceVO> resourceList = iUsersRoleService.listWithVoMapping(queryDTO, UserResourceVO.class);

        if (CollUtil.isEmpty(resourceList)) {
            return Collections.emptyList();
        }

        // 转换为 ResourceVO 并去重
        return resourceList.stream()
                .filter(resource -> resource.getResourceId() != null)
                .map(resource -> {
                    ResourceVO resourceVO = new ResourceVO();
                    resourceVO.setId(resource.getResourceId());
                    resourceVO.setSystemId(resource.getSystemId());
                    resourceVO.setMenuId(resource.getMenuId());
                    resourceVO.setCode(resource.getCode());
                    resourceVO.setName(resource.getName());
                    resourceVO.setType(resource.getType());
                    resourceVO.setDescription(resource.getDescription());
                    resourceVO.setPermissions(resource.getPermissions());
                    resourceVO.setCreateTime(resource.getCreateTime());
                    resourceVO.setModifyTime(resource.getModifyTime());
                    return resourceVO;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 查询用户系统列表
     * 通过用户的菜单和资源获取系统列表
     *
     * @param userId 用户ID
     * @return 系统VO列表
     */
    private List<SystemVO> getUserSystems(String userId) {
        // 1. 查询用户菜单
        List<MenuVO> menus = getUserMenus(userId);
        // 2. 查询用户资源
        List<ResourceVO> resources = getUserResources(userId);

        // 3. 收集系统ID
        Set<String> systemIds = menus.stream()
                .map(MenuVO::getSystemId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

        resources.stream()
                .map(ResourceVO::getSystemId)
                .filter(StrUtil::isNotBlank)
                .forEach(systemIds::add);

        if (CollUtil.isEmpty(systemIds)) {
            return Collections.emptyList();
        }

        // 4. 查询系统信息
        List<IamSystem> systems = iSystemService.listByIds(new ArrayList<>(systemIds));

        // 5. 过滤：只返回启用的系统
        systems = systems.stream()
                .filter(system -> Boolean.TRUE.equals(system.getStatus()))
                .collect(Collectors.toList());

        // 6. 转换为VO
        return VoMapper.mapToVoList(systems, SystemVO.class);
    }
}

