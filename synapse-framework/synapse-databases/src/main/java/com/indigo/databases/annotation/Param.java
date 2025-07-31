package com.indigo.databases.annotation;

import java.lang.annotation.*;

/**
 * 参数绑定注解
 * 用于绑定方法参数到SQL中的参数
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    
    /**
     * 参数名称
     */
    String value();
} 