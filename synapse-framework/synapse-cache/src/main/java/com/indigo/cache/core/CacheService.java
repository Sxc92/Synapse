package com.indigo.cache.core;

import com.indigo.cache.infrastructure.RedisService;
import com.indigo.cache.model.CacheObject;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 通用缓存服务类，封装RedisService，提供基于CacheObject的缓存操作
 *
 * @author 史偕成
 * @date 2025/05/16 09:25
 */
@Getter
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CacheService {

    /**
     * -- GETTER --
     *  直接访问底层RedisService
     *
     * @return RedisService
     */
    private final RedisService redisService;

    public CacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 生成缓存键
     *
     * @param prefix 前缀
     * @param keys   键值部分
     * @return 完整的缓存键
     */
    public String generateKey(String prefix, Object... keys) {
        StringBuilder sb = new StringBuilder(prefix);
        for (Object key : keys) {
            sb.append(":").append(key);
        }
        return sb.toString();
    }

    /**
     * 保存缓存对象
     *
     * @param cacheObject 缓存对象
     * @param <T>         数据类型
     */
    public <T> void save(CacheObject<T> cacheObject) {
        if (cacheObject.getExpireSeconds() > 0) {
            redisService.set(cacheObject.getKey(), cacheObject, cacheObject.getExpireSeconds());
        } else {
            redisService.set(cacheObject.getKey(), cacheObject);
        }
    }

    /**
     * 获取缓存对象
     *
     * @param key 缓存键
     * @param <T> 数据类型
     * @return 缓存对象
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<CacheObject<T>> get(String key) {
        Object obj = redisService.get(key);
        if (obj instanceof CacheObject<?> cacheObject) {

            // 检查是否过期（理论上Redis会自动处理过期，这里是双重检查）
            if (cacheObject.isExpired()) {
                redisService.delete(key);
                return Optional.empty();
            }
            
            return Optional.of((CacheObject<T>) cacheObject);
        }
        return Optional.empty();
    }

    /**
     * 获取缓存数据，如果缓存不存在或已过期，返回空
     *
     * @param key 缓存键
     * @param <T> 数据类型
     * @return 缓存数据
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getData(String key) {
        Optional<CacheObject<?>> optionalCache = (Optional<CacheObject<?>>) (Optional<?>) get(key);
        return optionalCache.map(cacheObject -> (T) cacheObject.getData());
    }

    /**
     * 获取缓存数据，如果缓存不存在或已过期，使用提供的函数加载数据并缓存
     *
     * @param key      缓存键
     * @param loader   数据加载函数
     * @param expireSeconds 过期时间（秒）
     * @param <T>      数据类型
     * @return 缓存数据
     */
    public <T> T getOrLoad(String key, Supplier<T> loader, long expireSeconds) {
        Optional<T> cachedData = getData(key);
        
        if (cachedData.isPresent()) {
            return cachedData.get();
        }
        
        // 加载数据
        T data = loader.get();
        
        // 缓存数据
        if (data != null) {
            save(new CacheObject<>(key, data, expireSeconds));
        }
        
        return data;
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     * @return 是否成功
     */
    public boolean delete(String key) {
        return redisService.delete(key);
    }

    /**
     * 批量删除缓存
     *
     * @param keys 缓存键集合
     * @return 成功删除的数量
     */
    public long deleteMulti(List<String> keys) {
        Long count = redisService.delete(keys);
        return count == null ? 0 : count;
    }

    /**
     * 删除指定前缀的所有缓存
     *
     * @param prefix 前缀
     * @return 删除的数量
     */
    public long deleteByPrefix(String prefix) {
        String pattern = prefix + "*";
        Set<Object> keys = redisService.setMembers(pattern);
        if (keys != null && !keys.isEmpty()) {
            List<String> keyList = keys.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            return deleteMulti(keyList);
        }
        return 0;
    }

    /**
     * 更新缓存数据，保留原有的过期时间
     *
     * @param key  缓存键
     * @param data 新数据
     * @param <T>  数据类型
     * @return 是否成功
     */
    @SuppressWarnings("unchecked")
    public <T> boolean update(String key, T data) {
        Optional<CacheObject<?>> optCacheObject = (Optional<CacheObject<?>>) (Optional<?>) get(key);
        if (optCacheObject.isPresent()) {
            CacheObject<?> cacheObject = optCacheObject.get();
            // 使用反射或其他机制更新数据 - 简化处理，创建新对象
            CacheObject<T> newCacheObject = new CacheObject<>(
                    key, 
                    data, 
                    cacheObject.getExpireSeconds()
            );
            save(newCacheObject);
            return true;
        }
        return false;
    }

    /**
     * 延长缓存过期时间
     *
     * @param key              缓存键
     * @param additionalSeconds 增加的秒数
     * @return 是否成功
     */
    public boolean extendExpiry(String key, long additionalSeconds) {
        Optional<CacheObject<?>> optCacheObject = (Optional<CacheObject<?>>) (Optional<?>) get(key);
        if (optCacheObject.isPresent()) {
            CacheObject<?> cacheObject = optCacheObject.get();
            cacheObject.extendExpiry(additionalSeconds);
            save((CacheObject) cacheObject);
            return true;
        }
        
        // 如果缓存对象不是CacheObject类型，尝试直接延长Redis的过期时间
        return Boolean.TRUE.equals(redisService.expire(key, 
                redisService.getExpire(key) + additionalSeconds));
    }

    /**
     * 重置缓存过期时间
     *
     * @param key          缓存键
     * @param expireSeconds 新的过期时间（秒）
     * @return 是否成功
     */
    public boolean resetExpiry(String key, long expireSeconds) {
        Optional<CacheObject<?>> optCacheObject = (Optional<CacheObject<?>>) (Optional<?>) get(key);
        if (optCacheObject.isPresent()) {
            CacheObject<?> cacheObject = optCacheObject.get();
            cacheObject.resetExpiry(expireSeconds);
            save((CacheObject) cacheObject);
            return true;
        }
        
        // 如果缓存对象不是CacheObject类型，尝试直接设置Redis的过期时间
        return Boolean.TRUE.equals(redisService.expire(key, expireSeconds));
    }

    /**
     * 获取缓存剩余有效时间
     *
     * @param key 缓存键
     * @return 剩余有效时间（秒），-1表示永不过期，-2表示不存在
     */
    public long getTimeToLive(String key) {
        Optional<CacheObject<?>> optCacheObject = (Optional<CacheObject<?>>) (Optional<?>) get(key);
        if (optCacheObject.isPresent()) {
            return optCacheObject.get().getRemainingTimeSeconds();
        }
        
        // 如果缓存对象不是CacheObject类型，尝试直接获取Redis的过期时间
        Long expire = redisService.getExpire(key);
        return expire == null ? -2L : expire;
    }

    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisService.hasKey(key));
    }

    /**
     * 保存哈希类型的缓存对象
     *
     * @param key        哈希键
     * @param field      字段
     * @param cacheObject 缓存对象
     * @param <T>        数据类型
     */
    public <T> void hashSave(String key, String field, CacheObject<T> cacheObject) {
        redisService.hashSet(key, field, cacheObject);
        
        // 如果有过期时间，设置整个哈希的过期时间
        if (cacheObject.getExpireSeconds() > 0) {
            redisService.expire(key, cacheObject.getExpireSeconds());
        }
    }

    /**
     * 获取哈希类型的缓存对象
     *
     * @param key   哈希键
     * @param field 字段
     * @param <T>   数据类型
     * @return 缓存对象
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<CacheObject<T>> hashGet(String key, String field) {
        Object obj = redisService.hashGet(key, field);
        if (obj instanceof CacheObject<?> cacheObject) {

            // 检查是否过期
            if (cacheObject.isExpired()) {
                redisService.hashDelete(key, field);
                return Optional.empty();
            }
            
            return Optional.of((CacheObject<T>) cacheObject);
        }
        return Optional.empty();
    }

    /**
     * 获取哈希类型的缓存数据
     *
     * @param key   哈希键
     * @param field 字段
     * @param <T>   数据类型
     * @return 缓存数据
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> hashGetData(String key, String field) {
        Optional<CacheObject<?>> optionalCache = (Optional<CacheObject<?>>) (Optional<?>) hashGet(key, field);
        return optionalCache.map(cacheObject -> (T) cacheObject.getData());
    }

    /**
     * 删除哈希类型的缓存
     *
     * @param key    哈希键
     * @param fields 字段列表
     * @return 删除的数量
     */
    public long hashDelete(String key, String... fields) {
        Long count = redisService.hashDelete(key, (Object[]) fields);
        return count == null ? 0 : count;
    }

    /**
     * 存储对象为 JSON 字符串
     */
    public <T> void setObject(String key, T value, long timeout) {
        redisService.setObject(key, value, timeout);
    }

    /**
     * 获取 JSON 字符串并反序列化为对象
     */
    public <T> T getObject(String key, Class<T> clazz) {
        return redisService.getObject(key, clazz);
    }

}