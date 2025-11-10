package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.core.entity.result.PageResult;
import com.indigo.iam.repository.service.IRoleService;
import com.indigo.iam.sdk.dto.users.AddOrModifyRoleDTO;
import com.indigo.iam.sdk.dto.users.RoleDTO;
import com.indigo.iam.sdk.vo.users.RoleVO;
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
}
