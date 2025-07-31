package com.indigo.databases.annotation;

import java.lang.annotation.*;

/**
 * 分页参数注解
 * 用于标识分页参数
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PageParam {
    
    /**
     * 页码参数名
     */
    String pageNum() default "pageNum";
    
    /**
     * 页大小参数名
     */
    String pageSize() default "pageSize";
    
    /**
     * 排序参数名
     */
    String orderBy() default "orderBy";
} 