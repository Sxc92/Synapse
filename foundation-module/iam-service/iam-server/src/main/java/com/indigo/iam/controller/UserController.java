package com.indigo.iam.controller;

import com.indigo.core.context.UserContext;
import com.indigo.core.entity.Result;
import com.indigo.core.entity.result.PageResult;
import com.indigo.iam.repository.service.IUsersService;
import com.indigo.iam.sdk.dto.associated.EmpowerDTO;
import com.indigo.iam.sdk.dto.opera.AddOrModifyUserDTO;
import com.indigo.iam.sdk.dto.query.UsersDTO;
import com.indigo.iam.sdk.vo.users.UserInfoVO;
import com.indigo.iam.sdk.vo.users.UserVO;
import com.indigo.iam.service.UserRoleService;
import com.indigo.security.annotation.RequireLogin;
import com.indigo.security.core.AuthenticationService;
import com.indigo.security.utils.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 用户管理控制器
 *
 * @author 史偕成
 * @date 2025/07/22 17:59
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRoleService userRoleService;
    private final IUsersService iUsersService;
    private final AuthenticationService authenticationService;
    private final TokenExtractor tokenExtractor;
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
    public Result<UserVO> detail(@RequestBody UsersDTO params) {
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

    /**
     * 获取当前登录用户信息
     * 包含用户基本信息和权限数据（从缓存中获取）
     *
     * @param request HTTP 请求
     * @return 用户信息
     */
    @GetMapping("/info")
    @RequireLogin
    public Result<UserInfoVO> getUserInfo(HttpServletRequest request) {
        // 从请求中获取 token
        String token = tokenExtractor.extractToken(request);
        if (token == null || token.trim().isEmpty()) {
            return Result.error("Token 不能为空");
        }

        // 调用 AuthenticationService 获取用户信息
        return authenticationService.getUserInfo(token, (userContext, permissions, systemMenuTree) -> {
            // 安全地转换系统菜单树类型
            @SuppressWarnings("unchecked")
            List<com.indigo.iam.sdk.vo.resource.SystemMenuTreeVO> menuTree = 
                    systemMenuTree != null ? (List<com.indigo.iam.sdk.vo.resource.SystemMenuTreeVO>) systemMenuTree : Collections.emptyList();
            
            // 构建 UserInfoVO
            UserInfoVO userInfo = UserInfoVO.builder()
                    .id(userContext.getUserId())
                    .account(userContext.getAccount())
                    .realName(userContext.getRealName())
                    .email(userContext.getEmail())
                    .mobile(userContext.getMobile())
                    .avatar(userContext.getAvatar())
                    .permissions(permissions)
                    .systemMenuTree(menuTree)
                    .build();
            return userInfo;
        });
    }

}
