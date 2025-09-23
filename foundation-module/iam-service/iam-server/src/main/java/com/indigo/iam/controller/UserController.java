package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.core.entity.result.PageResult;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.service.IamUserService;
import com.indigo.iam.sdk.dto.users.UserConditionDTO;
import com.indigo.iam.sdk.dto.users.UsersDTO;
import com.indigo.iam.sdk.dto.users.UsersPageDTO;
import com.indigo.iam.sdk.vo.UsersCVO;
import com.indigo.iam.sdk.vo.UsersVO;
import com.indigo.iam.service.UserService;
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


    private final UserService userService;

    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody UsersDTO param) {
        return Result.success(userService.addUser(param));
    }


    @PostMapping("/listUsers")
    public Result<List<UsersCVO>> listUsers(@RequestBody UserConditionDTO params) {
        return Result.success(userService.listUsers(params));
    }

    @PostMapping("/page")
    public Result<PageResult<UsersCVO>> pageUsers(@RequestBody UsersPageDTO params) {
        return Result.success(userService.pageUsers(params));
    }
}
