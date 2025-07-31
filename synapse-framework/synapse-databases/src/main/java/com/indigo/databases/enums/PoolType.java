package com.indigo.databases.enums;

import lombok.Getter;

/**
 * 数据源连接池类型
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Getter
public enum PoolType {
    
    /**
     * HikariCP连接池
     */
    HIKARI("com.zaxxer.hikari.HikariDataSource"),
    
    /**
     * Druid连接池
     */
    DRUID("com.alibaba.druid.pool.DruidDataSource");
    
    /**
     * 数据源类名
     */
    private final String dataSourceClassName;
    
    PoolType(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
    }
} 