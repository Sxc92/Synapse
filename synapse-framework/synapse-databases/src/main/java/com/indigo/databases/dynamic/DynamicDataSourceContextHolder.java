package com.indigo.databases.dynamic;

import lombok.extern.slf4j.Slf4j;

/**
 * 动态数据源上下文持有者
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
public class DynamicDataSourceContextHolder {
    
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
    
    /**
     * 设置数据源
     *
     * @param dataSource 数据源名称
     */
    public static void setDataSource(String dataSource) {
        log.debug("Switch to DataSource: {}", dataSource);
        contextHolder.set(dataSource);
    }
    
    /**
     * 获取数据源
     *
     * @return 数据源名称
     */
    public static String getDataSource() {
        return contextHolder.get();
    }
    
    /**
     * 清除数据源
     */
    public static void clear() {
        contextHolder.remove();
    }
} 