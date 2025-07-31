package com.indigo.security.aspect;

import com.indigo.core.context.UserContext;
import com.indigo.core.exception.SecurityException;
import com.indigo.security.annotation.DataPermission;
import com.indigo.security.model.DataPermissionRule;
import com.indigo.security.model.UserPrincipal;
import com.indigo.security.service.DataPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 数据权限切面
 * 用于自动处理数据权限检查
 *
 * @author 史偕成
 * @date 2024/01/09
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DataPermissionAspect {

    private final DataPermissionService dataPermissionService;

    /**
     * 在方法执行前检查数据权限
     */
    @Before("@annotation(dataPermission)")
    public void checkDataPermission(JoinPoint joinPoint, DataPermission dataPermission) {
        try {
            // 获取当前用户信息
            UserContext userContext = UserContext.getCurrentUser();
            if (userContext == null) {
                throw new SecurityException("未获取到用户上下文");
            }

            // 转换为UserPrincipal
            UserPrincipal user = convertToUserPrincipal(userContext);

            // 获取方法签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getMethod().getName();
            String className = signature.getDeclaringType().getSimpleName();

            // 检查权限
            String resourceType = dataPermission.resourceType();
            DataPermissionRule.PermissionType permissionType = dataPermission.permissionType();

            log.debug("检查数据权限: user={}, resource={}, permission={}, method={}.{}",
                user.getUsername(), resourceType, permissionType, className, methodName);

            boolean hasPermission = dataPermissionService.hasPermission(user, resourceType, permissionType);
            if (!hasPermission) {
                throw new SecurityException("没有访问权限: " + resourceType);
            }

            // 获取数据范围条件
            String dataScope = dataPermissionService.getDataScope(user, resourceType);
            // 将数据范围条件设置到ThreadLocal中，供后续SQL拦截器使用
            DataPermissionContext.setDataScope(dataScope);

            log.debug("数据权限检查通过: user={}, resource={}, dataScope={}",
                user.getUsername(), resourceType, dataScope);

        } catch (SecurityException e) {
            log.error("数据权限检查失败", e);
            throw e;
        } catch (Exception e) {
            log.error("数据权限检查异常", e);
            throw new SecurityException("数据权限检查异常: " + e.getMessage());
        }
    }

    /**
     * 将UserContext转换为UserPrincipal
     */
    private UserPrincipal convertToUserPrincipal(UserContext userContext) {
        return UserPrincipal.builder()
            .userId(userContext.getUserId())
            .username(userContext.getUsername())
            .deptId(userContext.getDeptId())
            .roles(userContext.getRoles())
            .permissions(userContext.getPermissions())
            .build();
    }
} 