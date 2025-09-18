package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.service.IamUserService;
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


    private final IamUserService iamUserService;

    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody Users user) {
        return Result.success(iamUserService.save(user));
    }
}
