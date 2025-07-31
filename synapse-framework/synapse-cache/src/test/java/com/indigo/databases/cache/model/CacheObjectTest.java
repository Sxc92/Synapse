package com.indigo.databases.cache.model;

import com.indigo.cache.model.CacheObject;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheObject单元测试
 */
public class CacheObjectTest {

    @Test
    public void testCreateCacheObject() {
        // 测试创建永不过期的缓存对象
        String key = "testKey";
        String data = "testData";
        CacheObject<String> cacheObject = new CacheObject<>(key, data);
        
        assertEquals(key, cacheObject.getKey());
        assertEquals(data, cacheObject.getData());
        assertEquals(-1, cacheObject.getExpireSeconds());
        assertNotNull(cacheObject.getCreateTime());
        assertFalse(cacheObject.isExpired());
        
        // 测试创建有过期时间的缓存对象
        long expireSeconds = 3600;
        CacheObject<String> expirableCacheObject = new CacheObject<>(key, data, expireSeconds);
        
        assertEquals(key, expirableCacheObject.getKey());
        assertEquals(data, expirableCacheObject.getData());
        assertEquals(expireSeconds, expirableCacheObject.getExpireSeconds());
        assertNotNull(expirableCacheObject.getCreateTime());
        assertFalse(expirableCacheObject.isExpired());
    }

    @Test
    public void testExpiration() throws InterruptedException {
        // 测试过期判断
        String key = "expiryKey";
        String data = "expiryData";
        long expireSeconds = 1; // 1秒后过期
        
        CacheObject<String> cacheObject = new CacheObject<>(key, data, expireSeconds);
        assertFalse(cacheObject.isExpired()); // 刚创建，不应该过期
        
        // 等待超过过期时间
        TimeUnit.SECONDS.sleep(2);
        
        assertTrue(cacheObject.isExpired()); // 应该已过期
        assertEquals(0, cacheObject.getRemainingTimeSeconds()); // 剩余时间应为0
    }

    @Test
    public void testRemainingTime() {
        String key = "timeKey";
        String data = "timeData";
        long expireSeconds = 60; // 60秒后过期
        
        CacheObject<String> cacheObject = new CacheObject<>(key, data, expireSeconds);
        
        // 剩余时间应该接近但小于等于设置的过期时间
        long remainingTime = cacheObject.getRemainingTimeSeconds();
        assertTrue(remainingTime <= expireSeconds && remainingTime > 50);
        
        // 测试永不过期的情况
        CacheObject<String> neverExpire = new CacheObject<>(key, data);
        assertEquals(-1, neverExpire.getRemainingTimeSeconds());
    }

    @Test
    public void testUpdateData() {
        String key = "updateKey";
        String initialData = "initialData";
        String updatedData = "updatedData";
        
        CacheObject<String> cacheObject = new CacheObject<>(key, initialData);
        LocalDateTime initialCreateTime = cacheObject.getCreateTime();
        long initialVersion = cacheObject.getVersion();
        
        // 等待一小段时间确保时间戳变化
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            // 忽略
        }
        
        // 更新数据
        cacheObject.updateData(updatedData);
        
        assertEquals(updatedData, cacheObject.getData());
        assertNotEquals(initialCreateTime, cacheObject.getCreateTime());
        assertNotEquals(initialVersion, cacheObject.getVersion());
    }

    @Test
    public void testResetExpiry() {
        String key = "resetKey";
        String data = "resetData";
        long initialExpire = 3600;
        long newExpire = 7200;
        
        CacheObject<String> cacheObject = new CacheObject<>(key, data, initialExpire);
        LocalDateTime initialCreateTime = cacheObject.getCreateTime();
        
        // 等待一小段时间确保时间戳变化
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            // 忽略
        }
        
        // 重置过期时间
        cacheObject.resetExpiry(newExpire);
        
        assertEquals(newExpire, cacheObject.getExpireSeconds());
        assertNotEquals(initialCreateTime, cacheObject.getCreateTime());
        
        // 剩余时间应接近新设置的过期时间
        long remainingTime = cacheObject.getRemainingTimeSeconds();
        assertTrue(remainingTime <= newExpire && remainingTime > newExpire - 10);
    }

    @Test
    public void testExtendExpiry() {
        String key = "extendKey";
        String data = "extendData";
        long initialExpire = 3600;
        long additionalTime = 1800;
        
        CacheObject<String> cacheObject = new CacheObject<>(key, data, initialExpire);
        
        // 延长过期时间
        cacheObject.extendExpiry(additionalTime);
        
        assertEquals(initialExpire + additionalTime, cacheObject.getExpireSeconds());
        
        // 测试永不过期对象的延长
        CacheObject<String> neverExpire = new CacheObject<>(key, data);
        neverExpire.extendExpiry(additionalTime);
        assertEquals(-1, neverExpire.getExpireSeconds()); // 仍应为永不过期
    }

    @Test
    public void testEqualsAndHashCode() {
        String key = "equalityKey";
        String data1 = "data1";
        String data2 = "data2";
        
        CacheObject<String> cache1 = new CacheObject<>(key, data1);
        
        // 相同键和版本的对象应该相等
        CacheObject<String> cache2 = new CacheObject<>(key, data2);
        cache2.setVersion(cache1.getVersion());
        
        assertEquals(cache1, cache2);
        assertEquals(cache1.hashCode(), cache2.hashCode());
        
        // 不同键的对象不应该相等
        CacheObject<String> cache3 = new CacheObject<>("differentKey", data1);
        cache3.setVersion(cache1.getVersion());
        
        assertNotEquals(cache1, cache3);
        
        // 不同版本的对象不应该相等
        CacheObject<String> cache4 = new CacheObject<>(key, data1);
        cache4.setVersion(cache1.getVersion() + 1);
        
        assertNotEquals(cache1, cache4);
    }

    @Test
    public void testToString() {
        String key = "toStringKey";
        String data = "toStringData";
        
        CacheObject<String> cacheObject = new CacheObject<>(key, data);
        
        String stringRepresentation = cacheObject.toString();
        
        assertTrue(stringRepresentation.contains(key));
        assertTrue(stringRepresentation.contains("isExpired=false"));
    }
} 