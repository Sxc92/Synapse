package com.indigo.databases.annotation;

import java.lang.annotation.*;

/**
 * SQL更新注解
 * 用于在接口方法上直接定义SQL更新语句
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlUpdate {
    
    /**
     * SQL更新语句
     */
    String value();
    
    /**
     * 超时时间（秒）
     */
    int timeout() default 30;
    
    /**
     * 是否在事务中执行
     */
    boolean transactional() default true;
} 