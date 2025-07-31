package com.indigo.cache.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

/**
 * Redis服务类，提供常用的Redis操作方法
 * 注意：该类不使用@Service注解，而是通过CacheAutoConfiguration创建bean
 * 这样可以确保RedisTemplate等依赖已经正确初始化
 *  TODO 1、增加常用封装方法（用于 token 校验场景） 未做
 * @author 史偕成
 * @date 2025/05/16 08:50
 */
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate, 
                       StringRedisTemplate stringRedisTemplate, 
                       @Qualifier("synapseObjectMapper") ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存并设置过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return 是否成功
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     *
     * @param keys 键集合
     * @return 成功删除的数量
     */
    public Long delete(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间（秒）
     * @return 是否成功
     */
    public Boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间（秒）
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 增加的值
     * @return 增加后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 减少的值
     * @return 减少后的值
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // Hash 操作

    /**
     * 设置Hash缓存
     *
     * @param key     键
     * @param hashKey hash键
     * @param value   值
     */
    public void hashSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取Hash缓存
     *
     * @param key     键
     * @param hashKey hash键
     * @return 值
     */
    public Object hashGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取Hash的所有键值
     *
     * @param key 键
     * @return 所有键值
     */
    public Map<Object, Object> hashGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 批量设置Hash缓存
     *
     * @param key  键
     * @param map  对应多个键值
     */
    public void hashPutAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 删除Hash缓存
     *
     * @param key      键
     * @param hashKeys hash键
     * @return 删除的数量
     */
    public Long hashDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key     键
     * @param hashKey hash键
     * @return 是否存在
     */
    public Boolean hashHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    // List 操作

    /**
     * 获取List缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0到-1代表所有值
     * @return List
     */
    public List<Object> listRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取List缓存的长度
     *
     * @param key 键
     * @return 长度
     */
    public Long listSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 根据索引获取List中的值
     *
     * @param key   键
     * @param index 索引 index>=0时，0表头，1第二个元素，依次类推；index<0时，-1表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    public Object listIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 将List放入缓存
     *
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public Long listRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将List放入缓存
     *
     * @param key   键
     * @param value 值
     * @param timeout 过期时间(秒)
     * @return 是否成功
     */
    public Long listRightPush(String key, Object value, long timeout) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        expire(key, timeout);
        return count;
    }

    /**
     * 将List放入缓存
     *
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public Long listRightPushAll(String key, List<Object> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 将List放入缓存并设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param timeout 过期时间(秒)
     * @return 是否成功
     */
    public Long listRightPushAll(String key, List<Object> value, long timeout) {
        Long count = redisTemplate.opsForList().rightPushAll(key, value);
        expire(key, timeout);
        return count;
    }

    /**
     * 根据索引修改List中的值
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    public void listSet(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移除N个值为value的项
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public Long listRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    // Set 操作

    /**
     * 将数据放入Set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long setAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取Set缓存的长度
     *
     * @param key 键
     * @return 长度
     */
    public Long setSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 判断Set是否包含value
     *
     * @param key   键
     * @param value 值
     * @return 是否包含
     */
    public Boolean setIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取Set缓存的所有值
     *
     * @param key 键
     * @return 所有值
     */
    public Set<Object> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public Long setRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    // ZSet 操作

    /**
     * 添加ZSet元素
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public Boolean zSetAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取ZSet指定范围的元素
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 值
     */
    public Set<Object> zSetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取ZSet的大小
     *
     * @param key 键
     * @return 大小
     */
    public Long zSetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 移除ZSet中的元素
     *
     * @param key    键
     * @param values 值
     * @return 移除的数量
     */
    public Long zSetRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }


    /**
     * 检查 token 是否有效（存在并未过期）
     */
    public boolean isTokenValid(String tokenKey) {
        return this.hasKey(tokenKey) && Boolean.TRUE.equals(this.getExpire(tokenKey) > 0);
    }

    /**
     * 自动续期，如果 TTL 小于阈值则刷新
     */
    public void tryRenewToken(String key, long ttlThreshold, long newTtl) {
        Long ttl = this.getExpire(key);
        if (ttl != null && ttl > 0 && ttl < ttlThreshold) {
            this.expire(key, newTtl);
        }
    }

    /**
     * 存储对象为 JSON 字符串
     */
    public <T> void setObject(String key, T value, long timeout) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object for redis", e);
        }
    }

    /**
     * 获取 JSON 字符串并反序列化为对象
     */
    public <T> T getObject(String key, Class<T> clazz) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null) return null;
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize object from redis", e);
        }
    }

    /**
     * 设置字符串值
     */
    public void setString(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取字符串值
     */
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 执行Lua脚本
     *
     * @param script Lua脚本
     * @param key    键
     * @param args   参数
     * @return 执行结果
     */
    public Long executeScript(String script, String key, String... args) {
        // 使用现代的 RedisScript 方式
        DefaultRedisScript<Long> redisScript =
            new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        
        // 准备键和参数
        java.util.List<String> keys = java.util.Collections.singletonList(key);

        try {
            return redisTemplate.execute(redisScript, keys, (Object[]) args);
        } catch (Exception e) {
            log.error("执行Lua脚本失败: {}", e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 根据模式扫描键
     *
     * @param pattern 模式
     * @return 匹配的键集合
     */
    public Set<String> scan(String pattern) {
        Set<String> keys = new HashSet<>();
        try {
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();
            Cursor<String> cursor = redisTemplate.scan(options);
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
            cursor.close();
        } catch (Exception e) {
            log.error("扫描键失败: pattern={}, error={}", pattern, e.getMessage(), e);
        }
        return keys;
    }

    /**
     * 根据模式扫描键（带限制）
     *
     * @param pattern 模式
     * @param count   每次扫描的数量
     * @return 匹配的键集合
     */
    public Set<String> scan(String pattern, int count) {
        Set<String> keys = new HashSet<>();
        try {
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(count).build();
            Cursor<String> cursor = redisTemplate.scan(options);
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
            cursor.close();
        } catch (Exception e) {
            log.error("扫描键失败: pattern={}, count={}, error={}", pattern, count, e.getMessage(), e);
        }
        return keys;
    }
} 