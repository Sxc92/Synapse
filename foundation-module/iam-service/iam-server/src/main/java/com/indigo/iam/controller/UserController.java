package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.service.IamUserService;
import com.indigo.iam.sdk.dto.users.UsersDTO;
import com.indigo.iam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
