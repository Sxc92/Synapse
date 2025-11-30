package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.core.entity.result.PageResult;
import com.indigo.iam.repository.service.IRoleService;
import com.indigo.iam.sdk.dto.associated.RoleMenuDTO;
import com.indigo.iam.sdk.dto.associated.RolePermissionDTO;
import com.indigo.iam.sdk.dto.associated.RoleResourceDTO;
import com.indigo.iam.sdk.dto.opera.AddOrModifyRoleDTO;
import com.indigo.iam.sdk.dto.query.RoleDTO;
import com.indigo.iam.sdk.vo.resource.RolePermissionVO;
import com.indigo.iam.sdk.vo.users.RoleVO;
import com.indigo.iam.service.RoleService;
import com.indigo.iam.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/07 15:54
 **/
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {


    private final UserRoleService userRoleService;
    private final IRoleService iRoleService;
    private final RoleService roleService;

    /**
     * 保存/修改用户
     *
     * @param param
     * @return
     */
    @PostMapping("/addOrModify")
    public Result<Boolean> save(@RequestBody AddOrModifyRoleDTO param) {
        return Result.success(userRoleService.addOrModifyRole(param));
    }

    /**
     * 分页查询用户
     *
     * @param params
     * @return
     */
    @PostMapping("/page")
    public Result<PageResult<RoleVO>> pageUsers(@RequestBody RoleDTO params) {
        return Result.success(iRoleService.pageWithCondition(params, RoleVO.class));
    }

    /**
     * 获取用户详情
     *
     * @param params
     * @return
     */
    @PostMapping("/detail")
    public Result<RoleVO> detailByKey(@RequestBody RoleDTO params) {
        return Result.success(iRoleService.getOneWithDTO(params, RoleVO.class));
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteUser(@PathVariable String id) {
        return Result.success(userRoleService.deleteRole(id));
    }

    /**
     * 获取用户列表
     *
     * @param param
     * @return
     */
    @PostMapping("/list")
    public Result<List<RoleVO>> getUserList(@RequestBody RoleDTO param) {
        return Result.success(iRoleService.listWithDTO(param, RoleVO.class));
    }


    /**
     * 角色分配菜单
     *
     * @param param 角色菜单关联参数
     * @return 操作结果
     */
    @PostMapping("/assignMenusToRole")
    public Result<Boolean> assignMenusToRole(@RequestBody RoleMenuDTO param) {
        return Result.success(userRoleService.assignMenusToRole(param));
    }

    /**
     * 角色分配资源
     *
     * @param param 角色资源关联参数
     * @return 操作结果
     */
    @PostMapping("/assignResourcesToRole")
    public Result<Boolean> assignResourcesToRole(@RequestBody RoleResourceDTO param) {
        return Result.success(userRoleService.assignResourcesToRole(param));
    }

    /**
     * 角色分配系统（已暂时注释）
     * 暂时取消 iam_role_system 表，通过菜单推导系统关系
     * 如果需要分配系统，可以通过分配菜单来实现（菜单属于系统）
     *
     * @param param 角色系统关联参数
     * @return 操作结果
     */
    @PostMapping("/assignPermissions/{roleId}")
    public Result<Boolean> assignSystemsToRole(@PathVariable String roleId, @RequestBody RolePermissionDTO param) {
        return Result.success(roleService.assignPermissions(roleId, param));
    }

    /**
     * 根据角色ID获取权限树形结构（系统-菜单-资源）
     *
     * @param roleId 角色ID
     * @return 权限树形结构列表
     */
    @GetMapping("/{roleId}/permissionIds")
    public Result<RolePermissionVO> getRolePermissionTree(@PathVariable String roleId) {
        return Result.success(roleService.getRolePermissionTree(roleId));
    }
}
