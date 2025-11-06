package com.indigo.iam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

//    private final AuthService authService;

//    /**
//     * 用户登录
//     *
//     * @param loginRequest 登录请求
//     * @return 登录结果
//     */
//    @PostMapping("/login")
//    public Result<AuthResponse> login(@RequestBody AuthRequest loginRequest) {
//        log.info("用户登录请求: username={}", loginRequest.getUsername());
//        return authService.login(loginRequest);
//    }
//
//    /**
//     * 用户登出
//     *
//     * @param token 访问令牌
//     * @return 登出结果
//     */
//    @PostMapping("/logout")
//    public Result<Void> logout(@RequestParam String token) {
//        log.info("用户登出请求: token={}", token);
//        return authService.logout(token);
//    }
} 