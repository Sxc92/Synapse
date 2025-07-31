package com.indigo.security.aspect;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据权限上下文
 * 用于存储当前线程的数据权限信息
 *
 * @author 史偕成
 * @date 2024/01/09
 */
@Slf4j
public class DataPermissionContext {

    private static final ThreadLocal<String> DATA_SCOPE = new ThreadLocal<>();

    /**
     * 设置数据范围
     *
     * @param dataScope 数据范围SQL条件
     */
    public static void setDataScope(String dataScope) {
        DATA_SCOPE.set(dataScope);
        log.debug("设置数据范围: {}", dataScope);
    }

    /**
     * 获取数据范围
     *
     * @return 数据范围SQL条件
     */
    public static String getDataScope() {
        String dataScope = DATA_SCOPE.get();
        log.debug("获取数据范围: {}", dataScope);
        return dataScope;
    }

    /**
     * 清除数据范围
     */
    public static void clear() {
        DATA_SCOPE.remove();
        log.debug("清除数据范围");
    }
} 