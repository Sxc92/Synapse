package com.indigo.databases.annotation;

import java.lang.annotation.*;

/**
 * 查询条件注解
 * 用于标记实体类中的查询条件字段
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryCondition {

    /**
     * 查询类型
     */
    QueryType type() default QueryType.EQ;

    /**
     * 数据库字段名（如果不指定，则使用实体字段名）
     */
    String field() default "";

    /**
     * 是否忽略空值
     */
    boolean ignoreNull() default true;

    /**
     * 是否忽略空字符串
     */
    boolean ignoreEmpty() default true;

    /**
     * 查询类型枚举
     */
    enum QueryType {
        EQ,        // 等于
        NE,        // 不等于
        LIKE,      // 模糊查询
        LIKE_LEFT, // 左模糊
        LIKE_RIGHT,// 右模糊
        GT,        // 大于
        GE,        // 大于等于
        LT,        // 小于
        LE,        // 小于等于
        IN,        // IN查询
        NOT_IN,    // NOT IN查询
        BETWEEN,   // 范围查询
        IS_NULL,   // IS NULL
        IS_NOT_NULL // IS NOT NULL
    }
} 