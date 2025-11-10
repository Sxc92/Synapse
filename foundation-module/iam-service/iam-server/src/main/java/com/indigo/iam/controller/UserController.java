package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.core.entity.result.PageResult;
import com.indigo.iam.repository.service.IUsersService;
import com.indigo.iam.sdk.dto.users.AddOrModifyUserDTO;
import com.indigo.iam.sdk.dto.users.EmpowerDTO;
import com.indigo.iam.sdk.dto.users.UsersDTO;
import com.indigo.iam.sdk.vo.users.UserVO;
import com.indigo.iam.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/07/22 17:59
 **/
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {


    private final UserRoleService userRoleService;

    private final IUsersService iUsersService;

    /**
     * 保存/修改用户
     *
     * @param param
     * @return
     */
    @PostMapping("/addOrModify")
    public Result<Boolean> save(@RequestBody AddOrModifyUserDTO param) {
        return Result.success(userRoleService.addOrModifyUser(param));
    }

    /**
     * 分页查询用户
     *
     * @param params
     * @return
     */
    @PostMapping("/page")
    public Result<PageResult<UserVO>> pageUsers(@RequestBody UsersDTO params) {
        return Result.success(iUsersService.pageWithCondition(params, UserVO.class));
    }

    /**
     * 获取用户详情
     *
     * @param params
     * @return
     */
    @PostMapping("/detail")
    public Result<UserVO> detailByKey(@RequestBody UsersDTO params) {
        return Result.success(iUsersService.getOneWithDTO(params, UserVO.class));
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteUser(@PathVariable String id) {
        return Result.success(userRoleService.deleteUser(id));
    }

    /**
     * 获取用户列表
     *
     * @param param
     * @return
     */
    @PostMapping("/list")
    public Result<List<UserVO>> getUserList(@RequestBody UsersDTO param) {
        return Result.success(iUsersService.listWithDTO(param, UserVO.class));
    }


    /**
     * 给用户添加角色
     *
     * @param param
     * @return
     */
    @PostMapping("/empower")
    public Result<Boolean> empower(@RequestBody EmpowerDTO param) {
        return Result.success(userRoleService.empower(param));
    }
}
