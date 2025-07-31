package com.indigo.security.annotation;

import com.indigo.security.model.DataPermissionRule;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 用于标记需要进行数据权限检查的方法
 *
 * @author 史偕成
 * @date 2024/01/09
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    /**
     * 资源类型
     */
    String resourceType();

    /**
     * 权限类型
     */
    DataPermissionRule.PermissionType permissionType() default DataPermissionRule.PermissionType.READ;

    /**
     * 是否启用数据范围过滤
     */
    boolean enableDataScope() default true;
} 