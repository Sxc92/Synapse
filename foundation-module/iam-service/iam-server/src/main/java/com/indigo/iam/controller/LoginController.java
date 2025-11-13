package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.iam.sdk.dto.auth.LoginDTO;
import com.indigo.iam.sdk.vo.auth.LoginResponseVO;
import com.indigo.iam.sdk.vo.resource.MenuVO;
import com.indigo.iam.sdk.vo.resource.ResourceVO;
import com.indigo.iam.sdk.vo.resource.SystemVO;
import com.indigo.iam.service.LoginService;
import com.indigo.security.annotation.RequireLogin;
import com.indigo.security.core.AuthenticationService;
import com.indigo.security.core.TokenService;
import com.indigo.security.utils.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
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
    private final TokenService tokenService;
    private final TokenExtractor tokenExtractor;

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
     * @param request HTTP 请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    @RequireLogin
    public Result<Void> logout(HttpServletRequest request) {
        log.info("用户登出请求");
        String token = getTokenFromRequest(request);
        if (token != null && tokenService != null) {
            // 撤销 token
            tokenService.revokeToken(token);
            log.info("用户登出成功: token={}", token);
        } else {
            log.warn("用户登出失败: token为空或TokenService未配置");
        }
        return Result.success();
    }

    /**
     * 获取用户系统列表
     * 根据 Token 从缓存中获取用户有权限的系统列表
     *
     * @param request HTTP 请求
     * @return 系统列表
     */
    @GetMapping("/system")
    @RequireLogin
    public Result<List<SystemVO>> getSystem(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        log.debug("获取用户系统列表: token={}", token);
        return authenticationService.getUserSystems(token, SystemVO.class);
    }

    /**
     * 获取用户菜单列表
     * 根据 Token 从缓存中获取用户有权限的菜单列表
     *
     * @param request HTTP 请求
     * @return 菜单列表
     */
    @GetMapping("/menu")
    @RequireLogin
    public Result<List<MenuVO>> getMenu(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        log.debug("获取用户菜单列表: token={}", token);
        return authenticationService.getUserMenus(token, MenuVO.class);
    }

    /**
     * 获取用户资源列表
     * 根据 Token 从缓存中获取用户有权限的资源列表
     *
     * @param request HTTP 请求
     * @return 资源列表
     */
    @GetMapping("/resource")
    @RequireLogin
    public Result<List<ResourceVO>> getResource(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        log.debug("获取用户资源列表: token={}", token);
        return authenticationService.getUserResources(token, ResourceVO.class);
    }

    /**
     * 从请求中获取 token
     * 使用 TokenExtractor 工具类统一提取
     *
     * @param request HTTP 请求
     * @return token 字符串，如果获取不到则返回 null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        return tokenExtractor.extractToken(request);
    }
}

