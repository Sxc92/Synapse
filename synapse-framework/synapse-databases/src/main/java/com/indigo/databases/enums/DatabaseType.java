package com.indigo.databases.enums;

import lombok.Getter;

/**
 * 数据库类型枚举
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Getter
public enum DatabaseType {
    
    /**
     * MySQL数据库
     */
    MYSQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://"),
    
    /**
     * PostgreSQL数据库
     */
    POSTGRESQL("org.postgresql.Driver", "jdbc:postgresql://"),
    
    /**
     * Oracle数据库
     */
    ORACLE("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@"),
    
    /**
     * SQL Server数据库
     */
    SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://"),
    
    /**
     * H2数据库
     */
    H2("org.h2.Driver", "jdbc:h2:");
    
    /**
     * 驱动类名
     */
    private final String driverClassName;
    
    /**
     * JDBC URL前缀
     */
    private final String urlPrefix;
    
    DatabaseType(String driverClassName, String urlPrefix) {
        this.driverClassName = driverClassName;
        this.urlPrefix = urlPrefix;
    }
    
    /**
     * 根据驱动类名获取数据库类型
     */
    public static DatabaseType fromDriverClassName(String driverClassName) {
        for (DatabaseType type : values()) {
            if (type.getDriverClassName().equals(driverClassName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported driver class name: " + driverClassName);
    }
} 