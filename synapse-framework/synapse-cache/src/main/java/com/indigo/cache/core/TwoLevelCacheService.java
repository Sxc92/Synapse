package com.indigo.cache.core;

import com.indigo.cache.infrastructure.CaffeineCacheManager;
import com.indigo.cache.infrastructure.RedisService;
import com.indigo.cache.manager.CacheKeyGenerator;
import com.indigo.cache.model.CacheObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 二级缓存服务，整合本地Caffeine缓存和Redis缓存
 * 实现了快速本地读取和分布式一致性
 *
 * @author 史偕成
 * @date 2025/05/16 10:00
 */
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class TwoLevelCacheService {

    private final CaffeineCacheManager localCache;
    private final CacheService redisCache;
    private final CacheKeyGenerator keyGenerator;

    // 缓存策略
    public enum CacheStrategy {
        /**
         * 只使用本地缓存，适用于频繁读取但更新不频繁的数据
         */
        LOCAL_ONLY,
        
        /**
         * 只使用Redis缓存，适用于分布式一致性要求高的数据
         */
        REDIS_ONLY,
        
        /**
         * 同时使用本地缓存和Redis缓存，本地缓存优先，适用于读取频繁但偶尔更新的数据
         */
        LOCAL_AND_REDIS,
        
        /**
         * Redis同步到本地，Redis变更后本地自动同步，适用于较长有效期的数据
         */
        REDIS_SYNC_TO_LOCAL
    }

    /**
     * 构造函数
     *
     * @param localCache   本地缓存管理器
     * @param redisCache   Redis缓存服务
     * @param keyGenerator 缓存键生成器
     */
    public TwoLevelCacheService(CaffeineCacheManager localCache, CacheService redisCache, CacheKeyGenerator keyGenerator) {
        this.localCache = localCache;
        this.redisCache = redisCache;
        this.keyGenerator = keyGenerator;
    }

    /**
     * 获取缓存数据
     *
     * @param module    模块名
     * @param key       缓存键
     * @param strategy  缓存策略
     * @param <T>       数据类型
     * @return 缓存数据
     */
    public <T> Optional<T> get(String module, String key, CacheStrategy strategy) {
        String cacheKey = keyGenerator.generate(module, key);
        
        switch (strategy) {
            case LOCAL_ONLY:
                return localCache.get(module, cacheKey);
                
            case REDIS_ONLY:
                return redisCache.getData(cacheKey);
                
            case LOCAL_AND_REDIS:
            case REDIS_SYNC_TO_LOCAL:
                // 先从本地缓存获取
                Optional<T> localResult = localCache.get(module, cacheKey);
                if (localResult.isPresent()) {
                    return localResult;
                }
                
                // 本地未命中，从Redis获取
                Optional<T> redisResult = redisCache.getData(cacheKey);
                
                // 如果Redis命中且是同步策略，则同步到本地缓存
                if (redisResult.isPresent() && strategy == CacheStrategy.REDIS_SYNC_TO_LOCAL) {
                    T data = redisResult.get();
                    long ttl = redisCache.getTimeToLive(cacheKey);
                    // 只有当TTL有效时才缓存到本地
                    if (ttl > 0) {
                        localCache.put(module, cacheKey, data, (int) ttl);
                    }
                }
                
                return redisResult;
                
            default:
                return Optional.empty();
        }
    }

    /**
     * 获取缓存数据，如果不存在则加载并缓存
     *
     * @param module         模块名
     * @param key            缓存键
     * @param loader         数据加载函数
     * @param expireSeconds  过期时间（秒）
     * @param strategy       缓存策略
     * @param <T>            数据类型
     * @return 缓存数据
     */
    public <T> T getOrLoad(String module, String key, Supplier<T> loader, long expireSeconds, CacheStrategy strategy) {
        String cacheKey = keyGenerator.generate(module, key);
        
        switch (strategy) {
            case LOCAL_ONLY:
                return localCache.getOrLoad(module, cacheKey, loader, (int) expireSeconds);
                
            case REDIS_ONLY:
                return redisCache.getOrLoad(cacheKey, loader, expireSeconds);
                
            case LOCAL_AND_REDIS:
                // 先从本地缓存获取
                Optional<T> localResult = localCache.get(module, cacheKey);
                if (localResult.isPresent()) {
                    return localResult.get();
                }
                
                // 本地未命中，从Redis获取
                Optional<T> redisResult = redisCache.getData(cacheKey);
                if (redisResult.isPresent()) {
                    // Redis命中，同步到本地缓存
                    T data = redisResult.get();
                    localCache.put(module, cacheKey, data, (int) expireSeconds);
                    return data;
                }
                
                // 都未命中，加载数据
                T data = loader.get();
                if (data != null) {
                    // 同时存入本地和Redis缓存
                    save(module, key, data, expireSeconds, strategy);
                }
                
                return data;
                
            case REDIS_SYNC_TO_LOCAL:
                // 从Redis获取或加载
                T redisData = redisCache.getOrLoad(cacheKey, loader, expireSeconds);
                
                // 写入本地缓存（本地缓存有效期比Redis短一些，避免一致性问题）
                int localTtl = expireSeconds > 300 ? (int)(expireSeconds * 0.8) : (int)expireSeconds;
                localCache.put(module, cacheKey, redisData, localTtl);
                
                return redisData;
                
            default:
                return loader.get();
        }
    }

    /**
     * 保存缓存数据
     *
     * @param module         模块名
     * @param key            缓存键
     * @param value          缓存值
     * @param expireSeconds  过期时间（秒）
     * @param strategy       缓存策略
     * @param <T>            数据类型
     */
    public <T> void save(String module, String key, T value, long expireSeconds, CacheStrategy strategy) {
        String cacheKey = keyGenerator.generate(module, key);
        
        switch (strategy) {
            case LOCAL_ONLY:
                localCache.put(module, cacheKey, value, (int) expireSeconds);
                break;
                
            case REDIS_ONLY:
                redisCache.save(new CacheObject<>(cacheKey, value, expireSeconds));
                break;
                
            case LOCAL_AND_REDIS:
                // 同时存入本地和Redis缓存
                localCache.put(module, cacheKey, value, (int) expireSeconds);
                redisCache.save(new CacheObject<>(cacheKey, value, expireSeconds));
                break;
                
            case REDIS_SYNC_TO_LOCAL:
                // 先存入Redis
                redisCache.save(new CacheObject<>(cacheKey, value, expireSeconds));
                
                // 再存入本地（有效期比Redis短一些）
                int localTtl = expireSeconds > 300 ? (int)(expireSeconds * 0.8) : (int)expireSeconds;
                localCache.put(module, cacheKey, value, localTtl);
                break;
                
            default:
                break;
        }
    }

    /**
     * 删除缓存
     *
     * @param module    模块名
     * @param key       缓存键
     * @param strategy  缓存策略
     */
    public void delete(String module, String key, CacheStrategy strategy) {
        String cacheKey = keyGenerator.generate(module, key);
        
        switch (strategy) {
            case LOCAL_ONLY:
                localCache.remove(module, cacheKey);
                break;
                
            case REDIS_ONLY:
                redisCache.delete(cacheKey);
                break;
                
            case LOCAL_AND_REDIS:
            case REDIS_SYNC_TO_LOCAL:
                // 同时删除本地和Redis缓存
                localCache.remove(module, cacheKey);
                redisCache.delete(cacheKey);
                break;
                
            default:
                break;
        }
    }

    /**
     * 获取本地缓存管理器
     *
     * @return 本地缓存管理器
     */
    public CaffeineCacheManager getLocalCache() {
        return localCache;
    }

    /**
     * 获取Redis缓存服务
     *
     * @return Redis缓存服务
     */
    public CacheService getRedisCache() {
        return redisCache;
    }

    /**
     * 获取缓存键生成器
     *
     * @return 缓存键生成器
     */
    public CacheKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }
}