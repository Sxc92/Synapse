package com.indigo.iam.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.exception.Ex;
import com.indigo.iam.repository.entity.*;
import com.indigo.iam.repository.service.*;
import com.indigo.iam.sdk.dto.associated.EmpowerDTO;
import com.indigo.iam.sdk.dto.opera.AddOrModifyUserDTO;
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
public interface UserService {

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
     * 用户赋权（分配角色）
     *
     * @param param 赋权参数
     * @return 操作结果
     */
    boolean empower(EmpowerDTO param);


    /**
     * 用户状态修改
     *
     * @param params
     * @return
     */
    Boolean modify(AddOrModifyUserDTO params);

}

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final IUsersService iUsersService;

    private final IRoleService iRoleService;

    private final IUsersRoleService iUsersRoleService;

    private final IRoleMenuService iRoleMenuService;

    private final IRoleResourceService iRoleResourceService;

    // private final IRoleSystemService iRoleSystemService; // 已注释：暂时取消 iam_role_system 表，通过菜单推导系统

    private final ISystemService iSystemService;

    private final IMenuService iMenuService;

    private final IResourceService iResourceService;

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
    public Boolean modify(AddOrModifyUserDTO params) {
        Users user = iUsersService.getById(params.getId());
        if (user == null) {
            Ex.throwEx(USER_NOT_EXIST);
        }
        if (params.getEnabled() != null) {
            user.setEnabled(!user.getEnabled());
        }
        return iUsersService.updateById(user);
    }
}
