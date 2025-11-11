package com.indigo.iam.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.exception.Ex;
import com.indigo.iam.repository.entity.RoleMenu;
import com.indigo.iam.repository.entity.RoleResource;
import com.indigo.iam.repository.entity.Roles;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.entity.UsersRole;
import com.indigo.iam.repository.service.IRoleMenuService;
import com.indigo.iam.repository.service.IRoleResourceService;
import com.indigo.iam.repository.service.IRoleService;
import com.indigo.iam.repository.service.IUsersRoleService;
import com.indigo.iam.repository.service.IUsersService;
import com.indigo.iam.sdk.dto.opera.AddOrModifyRoleDTO;
import com.indigo.iam.sdk.dto.opera.AddOrModifyUserDTO;
import com.indigo.iam.sdk.dto.associated.EmpowerDTO;
import com.indigo.iam.sdk.dto.associated.RoleMenuDTO;
import com.indigo.iam.sdk.dto.associated.RoleResourceDTO;
import com.indigo.security.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.indigo.iam.sdk.enums.IamError.*;

/**
 * @author 史偕成
 * @date 2025/07/23 07:58
 **/
public interface UserRoleService {

    /**
     * 添加或修改用户
     *
     * @param param 参数
     * @return
     */
    Boolean addOrModifyUser(AddOrModifyUserDTO param);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    Boolean deleteUser(String id);

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
     * 用户赋权（分配角色）
     *
     * @param param 赋权参数
     * @return 操作结果
     */
    boolean empower(EmpowerDTO param);

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
class UserServiceImpl implements UserRoleService {

    private final IUsersService iUsersService;

    private final IRoleService iRoleService;

    private final IUsersRoleService iUsersRoleService;

    private final IRoleMenuService iRoleMenuService;

    private final IRoleResourceService iRoleResourceService;

    @Override
    public Boolean addOrModifyUser(AddOrModifyUserDTO param) {
        if (iUsersService.checkKeyUniqueness(param, "account")) {
            Ex.throwEx(USER_ACCOUNT_EXIST);
        }
        if (StrUtil.isBlank(param.getId())) {
            // 新增场景：使用 saveFromDTO，保存前设置默认密码
            Users user = Users.builder()
                    .password(PasswordUtils.encode("123456"))
                    .build();
            return iUsersService.saveFromDTO(param, user);
        } else {
            // 更新场景：使用 updateFromDTO
            try {
                return iUsersService.updateFromDTO(param);
            } catch (IllegalArgumentException e) {
                Ex.throwEx(USER_NOT_EXIST);
                return false;
            }
        }
    }

    @Override
    public Boolean deleteUser(String id) {
        Users user = iUsersService.getById(id);
        if (user == null) {
            Ex.throwEx(USER_NOT_EXIST);
        }
        // 删除用户角色关联
        iUsersRoleService.remove(new LambdaQueryWrapper<UsersRole>()
                .eq(UsersRole::getUserId, id));
        // 删除用户
        return iUsersService.removeById(id);
    }


    @Override
    public Boolean addOrModifyRole(AddOrModifyRoleDTO param) {
        if (iRoleService.checkKeyUniqueness(param, "code")) {
            Ex.throwEx(ROLE_EXIST);
        }
        return iRoleService.saveOrUpdateFromDTO(param, Roles.class);
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
    public boolean empower(EmpowerDTO param) {
        Users user = iUsersService.getById(param.getId());
        if (user == null) {
            Ex.throwEx(USER_NOT_EXIST);
        }
        // 删除原有角色关联
        iUsersRoleService.remove(new LambdaQueryWrapper<UsersRole>()
                .eq(UsersRole::getUserId, param.getId()));
        // 如果角色列表为空，直接返回
        if (CollUtil.isEmpty(param.getRoleIds())) {
            return true;
        }
        // 批量保存新的角色关联
        List<UsersRole> usersRoles = new ArrayList<>();
        param.getRoleIds().forEach(roleId -> {
            usersRoles.add(UsersRole.builder()
                    .userId(param.getId())
                    .roleId(roleId)
                    .build());
        });
        return iUsersRoleService.saveBatch(usersRoles);
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
