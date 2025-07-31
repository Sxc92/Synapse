package com.indigo.cache.manager;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 缓存键生成器，统一管理缓存键的生成规则
 *
 * @author 史偕成
 * @date 2025/05/16 09:40
 */
public class CacheKeyGenerator {

    /**
     * 项目统一前缀
     */
    private static final String GLOBAL_PREFIX = "synapse";

    /**
     * 默认分隔符
     */
    private static final String DELIMITER = ":";

    /**
     * 生成缓存键
     * 格式：synapse:module:key
     *
     * @param module 模块名
     * @param key    键值
     * @return 完整的缓存键
     */
    public String generate(String module, String key) {
        return generate(module, (Object) key);
    }

    /**
     * 业务模块前缀定义
     */
    public static final class Module {
        /**
         * 用户相关
         */
        public static final String USER = "user";
        
        /**
         * 订单相关
         */
        public static final String ORDER = "order";
        
        /**
         * 产品相关
         */
        public static final String PRODUCT = "product";
        
        /**
         * 系统配置相关
         */
        public static final String CONFIG = "config";
        
        /**
         * 权限相关
         */
        public static final String AUTH = "auth";
        
        /**
         * 消息相关
         */
        public static final String MESSAGE = "message";
        
        /**
         * 日志相关
         */
        public static final String LOG = "log";
        
        /**
         * 任务相关
         */
        public static final String TASK = "task";
        
        /**
         * 锁相关
         */
        public static final String LOCK = "lock";
        
        /**
         * 限流相关
         */
        public static final String RATE_LIMIT = "rate_limit";
    }
    
    /**
     * 生成基本缓存键
     *
     * @param module  业务模块
     * @param keys    键值
     * @return 完整的缓存键
     */
    public String generate(String module, Object... keys) {
        // 处理空值
        if (keys == null || keys.length == 0) {
            return String.join(DELIMITER, GLOBAL_PREFIX, module);
        }

        // 转换并过滤所有键值
        String keyPart = Arrays.stream(keys)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(DELIMITER));

        return String.join(DELIMITER, GLOBAL_PREFIX, module, keyPart);
    }
} 