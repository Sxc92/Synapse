//package com.indigo.databases.cache.service;
//
//import com.indigo.cache.extension.CaffeineCacheManager;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * CaffeineCacheManager单元测试
// */
//public class CaffeineCacheManagerTest {
//
//    @Test
//    public void testGetCache() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//
//        // 获取默认配置的缓存
//        assertNotNull(cacheManager.getCache("testCache"));
//
//        // 获取自定义配置的缓存
//        assertNotNull(cacheManager.getCache("customCache", 100, 500));
//
//        // 再次获取同名缓存应该返回同一个实例
//        assertSame(
//            cacheManager.getCache("testCache"),
//            cacheManager.getCache("testCache")
//        );
//    }
//
//    @Test
//    public void testPutAndGet() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//        String cacheName = "userCache";
//        String key = "user:1";
//        String value = "张三";
//
//        // 放入缓存
//        cacheManager.put(cacheName, key, value);
//
//        // 读取缓存
//        Optional<String> cachedValue = cacheManager.get(cacheName, key);
//
//        assertTrue(cachedValue.isPresent());
//        assertEquals(value, cachedValue.get());
//    }
//
//    @Test
//    public void testGetOrLoad() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//        String cacheName = "productCache";
//        String key = "product:1";
//        String value = "笔记本电脑";
//
//        // 首次加载
//        String loadedValue = cacheManager.getOrLoad(
//            cacheName,
//            key,
//            () -> value,
//            60
//        );
//
//        assertEquals(value, loadedValue);
//
//        // 再次获取应该从缓存返回
//        String cachedValue = cacheManager.getOrLoad(
//            cacheName,
//            key,
//            () -> "不同的值", // 如果从加载函数获取，会返回不同的值
//            60
//        );
//
//        assertEquals(value, cachedValue); // 应该返回缓存中的原值
//    }
//
//    @Test
//    public void testExpiration() throws InterruptedException {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//        String cacheName = "shortCache";
//        String key = "expiring:1";
//        String value = "短期数据";
//
//        // 放入缓存，设置1秒过期
//        cacheManager.put(cacheName, key, value, 1);
//
//        // 立即获取应该能够获取到
//        Optional<String> immediateValue = cacheManager.get(cacheName, key);
//        assertTrue(immediateValue.isPresent());
//        assertEquals(value, immediateValue.get());
//
//        // 等待超过过期时间
//        TimeUnit.SECONDS.sleep(2);
//
//        // 过期后应该获取不到
//        Optional<String> expiredValue = cacheManager.get(cacheName, key);
//        assertFalse(expiredValue.isPresent());
//    }
//
//    @Test
//    public void testRemoveAndClear() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//        String cacheName = "removalCache";
//
//        // 添加三个键值对
//        cacheManager.put(cacheName, "key1", "value1");
//        cacheManager.put(cacheName, "key2", "value2");
//        cacheManager.put(cacheName, "key3", "value3");
//
//        // 验证添加成功
//        assertTrue(cacheManager.get(cacheName, "key1").isPresent());
//        assertTrue(cacheManager.get(cacheName, "key2").isPresent());
//        assertTrue(cacheManager.get(cacheName, "key3").isPresent());
//
//        // 删除单个键
//        cacheManager.remove(cacheName, "key2");
//
//        assertTrue(cacheManager.get(cacheName, "key1").isPresent());
//        assertFalse(cacheManager.get(cacheName, "key2").isPresent());
//        assertTrue(cacheManager.get(cacheName, "key3").isPresent());
//
//        // 清空整个缓存
//        cacheManager.clear(cacheName);
//
//        assertFalse(cacheManager.get(cacheName, "key1").isPresent());
//        assertFalse(cacheManager.get(cacheName, "key3").isPresent());
//    }
//
//    @Test
//    public void testRemoveCache() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//        String cacheName = "temporaryCache";
//
//        // 添加数据到缓存
//        cacheManager.put(cacheName, "tempKey", "tempValue");
//
//        // 验证数据存在
//        assertTrue(cacheManager.get(cacheName, "tempKey").isPresent());
//
//        // 移除整个缓存实例
//        cacheManager.removeCache(cacheName);
//
//        // 重新获取缓存应该是一个新的空实例
//        assertFalse(cacheManager.get(cacheName, "tempKey").isPresent());
//    }
//
//    @Test
//    public void testGetStats() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//        String cacheName = "statsCache";
//
//        // 添加数据并进行一些操作
//        cacheManager.put(cacheName, "statsKey1", "value1");
//        cacheManager.get(cacheName, "statsKey1");
//        cacheManager.get(cacheName, "nonExistentKey");
//
//        // 获取统计信息，应该返回非空字符串
//        String stats = cacheManager.getStats(cacheName);
//        assertNotNull(stats);
//        assertFalse(stats.isEmpty());
//    }
//
//    @Test
//    public void testClearAll() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//
//        // 添加数据到多个缓存
//        cacheManager.put("cache1", "key1", "value1");
//        cacheManager.put("cache2", "key2", "value2");
//
//        // 验证数据存在
//        assertTrue(cacheManager.get("cache1", "key1").isPresent());
//        assertTrue(cacheManager.get("cache2", "key2").isPresent());
//
//        // 清空所有缓存
//        cacheManager.clearAll();
//
//        // 验证数据已被清空
//        assertFalse(cacheManager.get("cache1", "key1").isPresent());
//        assertFalse(cacheManager.get("cache2", "key2").isPresent());
//    }
//}