package com.indigo.cache.infrastructure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.indigo.cache.model.CacheObject;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 基于Caffeine的本地缓存管理器
 * 作为Redis缓存的补充，用于存储使用频率高但变化不频繁的数据
 *
 * @author 史偕成
 * @date 2025/05/16 09:50
 */
@Component
public class CaffeineCacheManager {
    
    /**
     * 存储多个缓存实例，按照业务分类
     */
    private final Map<String, Cache<String, Object>> cacheMap = new ConcurrentHashMap<>();
    
    /**
     * 默认缓存过期时间（秒）
     */
    private static final int DEFAULT_EXPIRE_SECONDS = 3600;
    
    /**
     * 默认最大缓存条目数
     */
    private static final int DEFAULT_MAX_SIZE = 1000;

    /**
     * 获取指定名称的缓存，如果不存在则创建
     *
     * @param cacheName  缓存名称
     * @return 缓存实例
     */
    public Cache<String, Object> getCache(String cacheName) {
        return cacheMap.computeIfAbsent(cacheName, name -> 
            createCache(DEFAULT_EXPIRE_SECONDS, DEFAULT_MAX_SIZE)
        );
    }
    
    /**
     * 获取指定名称的缓存，并自定义过期时间和大小
     *
     * @param cacheName    缓存名称
     * @param expireSeconds 过期时间（秒）
     * @param maxSize      最大缓存条目数
     * @return 缓存实例
     */
    public Cache<String, Object> getCache(String cacheName, int expireSeconds, int maxSize) {
        return cacheMap.computeIfAbsent(cacheName, name -> 
            createCache(expireSeconds, maxSize)
        );
    }
    
    /**
     * 创建一个新的缓存实例
     *
     * @param expireSeconds 过期时间（秒）
     * @param maxSize      最大缓存条目数
     * @return 新的缓存实例
     */
    private Cache<String, Object> createCache(int expireSeconds, int maxSize) {
        return Caffeine.newBuilder()
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .recordStats() // 记录统计信息
                .build();
    }
    
    /**
     * 从指定缓存中获取数据
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param <T>       数据类型
     * @return 缓存数据
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String cacheName, String key) {
        Cache<String, Object> cache = getCache(cacheName);
        Object value = cache.getIfPresent(key);
        
        if (value == null) {
            return Optional.empty();
        }
        
        // 如果值是CacheObject类型，则处理过期逻辑
        if (value instanceof CacheObject<?> cacheObject) {

            // 检查是否过期
            if (cacheObject.isExpired()) {
                cache.invalidate(key);
                return Optional.empty();
            }
            
            return Optional.of((T) cacheObject.getData());
        }
        
        // 直接存储的值
        return Optional.of((T) value);
    }
    
    /**
     * 从指定缓存中获取数据，如果不存在则使用加载函数加载
     *
     * @param cacheName    缓存名称
     * @param key          缓存键
     * @param loader       数据加载函数
     * @param expireSeconds 过期时间（秒）
     * @param <T>          数据类型
     * @return 缓存数据
     */
    public <T> T getOrLoad(String cacheName, String key, java.util.function.Supplier<T> loader, int expireSeconds) {
        Optional<T> cachedValue = get(cacheName, key);
        
        if (cachedValue.isPresent()) {
            return cachedValue.get();
        }
        
        // 加载数据
        T value = loader.get();
        
        // 缓存数据
        if (value != null) {
            put(cacheName, key, value, expireSeconds);
        }
        
        return value;
    }
    
    /**
     * 将数据放入指定缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param value     缓存值
     * @param <T>       数据类型
     */
    public <T> void put(String cacheName, String key, T value) {
        put(cacheName, key, value, DEFAULT_EXPIRE_SECONDS);
    }
    
    /**
     * 将数据放入指定缓存并设置过期时间
     *
     * @param cacheName    缓存名称
     * @param key          缓存键
     * @param value        缓存值
     * @param expireSeconds 过期时间（秒）
     * @param <T>          数据类型
     */
    public <T> void put(String cacheName, String key, T value, int expireSeconds) {
        Cache<String, Object> cache = getCache(cacheName);
        
        // 使用CacheObject包装以支持自定义过期时间
        CacheObject<T> cacheObject = new CacheObject<>(key, value, expireSeconds);
        cache.put(key, cacheObject);
    }
    
    /**
     * 从指定缓存中删除数据
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     */
    public void remove(String cacheName, String key) {
        Cache<String, Object> cache = getCache(cacheName);
        cache.invalidate(key);
    }
    
    /**
     * 清空指定缓存
     *
     * @param cacheName 缓存名称
     */
    public void clear(String cacheName) {
        Cache<String, Object> cache = getCache(cacheName);
        cache.invalidateAll();
    }
    
    /**
     * 清空所有缓存
     */
    public void clearAll() {
        cacheMap.values().forEach(Cache::invalidateAll);
    }
    
    /**
     * 获取指定缓存的统计信息
     *
     * @param cacheName 缓存名称
     * @return 统计信息
     */
    public String getStats(String cacheName) {
        Cache<String, Object> cache = getCache(cacheName);
        return cache.stats().toString();
    }
    
    /**
     * 移除指定的缓存实例
     *
     * @param cacheName 缓存名称
     */
    public void removeCache(String cacheName) {
        Cache<String, Object> cache = cacheMap.remove(cacheName);
        if (cache != null) {
            cache.invalidateAll();
        }
    }
} 