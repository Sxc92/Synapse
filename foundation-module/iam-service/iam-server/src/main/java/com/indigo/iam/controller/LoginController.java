package com.indigo.iam.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.indigo.core.entity.Result;
import com.indigo.iam.sdk.dto.auth.LoginDTO;
import com.indigo.iam.sdk.vo.auth.LoginResponseVO;
import com.indigo.iam.sdk.vo.resource.MenuVO;
import com.indigo.iam.sdk.vo.resource.ResourceVO;
import com.indigo.iam.sdk.vo.resource.SystemVO;
import com.indigo.iam.service.LoginService;
import com.indigo.security.core.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录控制器
 * 提供登录、获取权限数据等接口
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final AuthenticationService authenticationService;

    /**
     * 用户登录
     *
     * @param dto 登录请求
     * @return 登录响应（包含 Token）
     */
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@RequestBody @Valid LoginDTO dto) {
        log.info("用户登录请求: username={}", dto.getUsername());
        return Result.success(loginService.login(dto));
    }

    /**
     * 用户登出
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    @SaCheckLogin
    public Result<Void> logout() {
        log.info("用户登出请求");
        return authenticationService.logout();
    }

    /**
     * 获取用户系统列表
     * 根据 Token 从缓存中获取用户有权限的系统列表
     *
     * @return 系统列表
     */
    @GetMapping("/system")
    @SaCheckLogin
    public Result<List<SystemVO>> getSystem() {
        String token = StpUtil.getTokenValue();
        log.debug("获取用户系统列表: token={}", token);
        return authenticationService.getUserSystems(token, SystemVO.class);
    }

    /**
     * 获取用户菜单列表
     * 根据 Token 从缓存中获取用户有权限的菜单列表
     *
     * @return 菜单列表
     */
    @GetMapping("/menu")
    @SaCheckLogin
    public Result<List<MenuVO>> getMenu() {
        String token = StpUtil.getTokenValue();
        log.debug("获取用户菜单列表: token={}", token);
        return authenticationService.getUserMenus(token, MenuVO.class);
    }

    /**
     * 获取用户资源列表
     * 根据 Token 从缓存中获取用户有权限的资源列表
     *
     * @return 资源列表
     */
    @GetMapping("/resource")
    @SaCheckLogin
    public Result<List<ResourceVO>> getResource() {
        String token = StpUtil.getTokenValue();
        log.debug("获取用户资源列表: token={}", token);
        return authenticationService.getUserResources(token, ResourceVO.class);
    }
}

