//package com.indigo.databases.cache.service;
//
//import com.indigo.cache.manager.CacheKeyGenerator;
//import com.indigo.cache.service.CacheService;
//import com.indigo.cache.service.CaffeineCacheManager;
//import com.indigo.cache.service.TwoLevelCacheService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Optional;
//import java.util.function.Supplier;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * TwoLevelCacheService单元测试
// */
//public class TwoLevelCacheServiceTest {
//
//    private TwoLevelCacheService cacheService;
//
//    @Mock
//    private CaffeineCacheManager localCache;
//
//    @Mock
//    private CacheService redisCache;
//
//    @Mock
//    private CacheKeyGenerator keyGenerator;
//
//    private final String MODULE = "testModule";
//    private final String KEY = "testKey";
//    private final String GENERATED_KEY = "synapse:testModule:testKey";
//    private final String TEST_VALUE = "测试数据";
//    private final int EXPIRE_SECONDS = 3600;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        // 修改：明确指定使用 generate(String, Object...) 方法
//        when(keyGenerator.generate(eq(MODULE), (Object[]) any())).thenReturn(GENERATED_KEY);
//
//        cacheService = new TwoLevelCacheService(localCache, redisCache, keyGenerator);
//    }
//
//    /**
//     * 重置mock并重新设置基本行为
//     */
//    private void resetAndSetupMocks() {
//        reset(localCache, redisCache);
//        when(keyGenerator.generate(eq(MODULE), (Object[]) any())).thenReturn(GENERATED_KEY);
//    }
//
//    @Test
//    public void testGet_LocalOnly() {
//        // Given
//        when(localCache.get(eq(MODULE), eq(GENERATED_KEY))).thenReturn(Optional.of(TEST_VALUE));
//
//        // When
//        Optional<String> result = cacheService.get(MODULE, KEY, TwoLevelCacheService.CacheStrategy.LOCAL_ONLY);
//
//        // Then
//        assertTrue(result.isPresent());
//        assertEquals(TEST_VALUE, result.get());
//        verify(localCache).get(eq(MODULE), eq(GENERATED_KEY));
//        verify(redisCache, never()).getData(anyString());
//    }
//
//    @Test
//    public void testGet_RedisOnly() {
//        // Given
//        when(redisCache.getData(eq(GENERATED_KEY))).thenReturn(Optional.of(TEST_VALUE));
//
//        // When
//        Optional<String> result = cacheService.get(MODULE, KEY, TwoLevelCacheService.CacheStrategy.REDIS_ONLY);
//
//        // Then
//        assertTrue(result.isPresent());
//        assertEquals(TEST_VALUE, result.get());
//        verify(redisCache).getData(eq(GENERATED_KEY));
//        verify(localCache, never()).get(anyString(), anyString());
//    }
//
//    @Test
//    public void testGet_LocalAndRedis_LocalHit() {
//        // Given
//        when(localCache.get(eq(MODULE), eq(GENERATED_KEY))).thenReturn(Optional.of(TEST_VALUE));
//
//        // When
//        Optional<String> result = cacheService.get(MODULE, KEY, TwoLevelCacheService.CacheStrategy.LOCAL_AND_REDIS);
//
//        // Then
//        assertTrue(result.isPresent());
//        assertEquals(TEST_VALUE, result.get());
//        verify(localCache).get(eq(MODULE), eq(GENERATED_KEY));
//        verify(redisCache, never()).getData(anyString());
//    }
//
//    @Test
//    public void testGet_LocalAndRedis_RedisHit() {
//        // Given
//        when(localCache.get(eq(MODULE), eq(GENERATED_KEY))).thenReturn(Optional.empty());
//        when(redisCache.getData(eq(GENERATED_KEY))).thenReturn(Optional.of(TEST_VALUE));
//
//        // When
//        Optional<String> result = cacheService.get(MODULE, KEY, TwoLevelCacheService.CacheStrategy.LOCAL_AND_REDIS);
//
//        // Then
//        assertTrue(result.isPresent());
//        assertEquals(TEST_VALUE, result.get());
//        verify(localCache).get(eq(MODULE), eq(GENERATED_KEY));
//        verify(redisCache).getData(eq(GENERATED_KEY));
//    }
//
//    @Test
//    public void testGetOrLoad_LocalOnly() {
//        // Given
//        Supplier<String> loader = () -> TEST_VALUE;
//        when(localCache.getOrLoad(eq(MODULE), eq(GENERATED_KEY), any(), eq(EXPIRE_SECONDS)))
//            .thenReturn(TEST_VALUE);
//
//        // When
//        String result = cacheService.getOrLoad(MODULE, KEY, loader, EXPIRE_SECONDS,
//            TwoLevelCacheService.CacheStrategy.LOCAL_ONLY);
//
//        // Then
//        assertEquals(TEST_VALUE, result);
//        verify(localCache).getOrLoad(eq(MODULE), eq(GENERATED_KEY), any(), eq(EXPIRE_SECONDS));
//        verify(redisCache, never()).getOrLoad(anyString(), any(), anyLong());
//    }
//
//    @Test
//    public void testGetOrLoad_RedisOnly() {
//        // Given
//        Supplier<String> loader = () -> TEST_VALUE;
//        when(redisCache.getOrLoad(eq(GENERATED_KEY), any(), eq((long)EXPIRE_SECONDS)))
//            .thenReturn(TEST_VALUE);
//
//        // When
//        String result = cacheService.getOrLoad(MODULE, KEY, loader, EXPIRE_SECONDS,
//            TwoLevelCacheService.CacheStrategy.REDIS_ONLY);
//
//        // Then
//        assertEquals(TEST_VALUE, result);
//        verify(redisCache).getOrLoad(eq(GENERATED_KEY), any(), eq((long)EXPIRE_SECONDS));
//        verify(localCache, never()).getOrLoad(anyString(), anyString(), any(), anyInt());
//    }
//
//    @Test
//    public void testSave_LocalOnly() {
//        // Given
//        resetAndSetupMocks();
//
//        // When
//        cacheService.save(MODULE, KEY, TEST_VALUE, EXPIRE_SECONDS,
//            TwoLevelCacheService.CacheStrategy.LOCAL_ONLY);
//
//        // Then
//        verify(localCache).put(eq(MODULE), eq(GENERATED_KEY), eq(TEST_VALUE), eq(EXPIRE_SECONDS));
//        verify(redisCache, never()).save(any());
//    }
//
//    @Test
//    public void testSave_RedisOnly() {
//        // Given
//        resetAndSetupMocks();
//
//        // When
//        cacheService.save(MODULE, KEY, TEST_VALUE, EXPIRE_SECONDS,
//            TwoLevelCacheService.CacheStrategy.REDIS_ONLY);
//
//        // Then
//        verify(redisCache).save(argThat(cacheObject ->
//            GENERATED_KEY.equals(cacheObject.getKey()) &&
//            TEST_VALUE.equals(cacheObject.getData()) &&
//            cacheObject.getExpireSeconds() == EXPIRE_SECONDS
//        ));
//        verify(localCache, never()).put(anyString(), anyString(), any(), anyInt());
//    }
//
//    @Test
//    public void testSave_LocalAndRedis() {
//        // Given
//        resetAndSetupMocks();
//
//        // When
//        cacheService.save(MODULE, KEY, TEST_VALUE, EXPIRE_SECONDS,
//            TwoLevelCacheService.CacheStrategy.LOCAL_AND_REDIS);
//
//        // Then
//        verify(localCache).put(eq(MODULE), eq(GENERATED_KEY), eq(TEST_VALUE), eq(EXPIRE_SECONDS));
//        verify(redisCache).save(argThat(cacheObject ->
//            GENERATED_KEY.equals(cacheObject.getKey()) &&
//            TEST_VALUE.equals(cacheObject.getData()) &&
//            cacheObject.getExpireSeconds() == EXPIRE_SECONDS
//        ));
//    }
//
//    @Test
//    public void testDelete_LocalOnly() {
//        // Given
//        resetAndSetupMocks();
//
//        // When
//        cacheService.delete(MODULE, KEY, TwoLevelCacheService.CacheStrategy.LOCAL_ONLY);
//
//        // Then
//        verify(localCache).remove(MODULE, GENERATED_KEY);
//        verify(redisCache, never()).delete(anyString());
//    }
//
//    @Test
//    public void testDelete_RedisOnly() {
//        // Given
//        resetAndSetupMocks();
//
//        // When
//        cacheService.delete(MODULE, KEY, TwoLevelCacheService.CacheStrategy.REDIS_ONLY);
//
//        // Then
//        verify(redisCache).delete(GENERATED_KEY);
//        verify(localCache, never()).remove(anyString(), anyString());
//    }
//
//    @Test
//    public void testDelete_LocalAndRedis() {
//        // Given
//        resetAndSetupMocks();
//
//        // When
//        cacheService.delete(MODULE, KEY, TwoLevelCacheService.CacheStrategy.LOCAL_AND_REDIS);
//
//        // Then
//        verify(localCache).remove(MODULE, GENERATED_KEY);
//        verify(redisCache).delete(GENERATED_KEY);
//    }
//
//    @Test
//    public void testRedisSyncToLocalStrategy() {
//        // 配置Mock对象行为 - Redis命中
//        when(localCache.get(eq(MODULE), eq(GENERATED_KEY))).thenReturn(Optional.empty());
//        when(redisCache.getData(eq(GENERATED_KEY))).thenReturn(Optional.of(TEST_VALUE));
//        when(redisCache.getTimeToLive(eq(GENERATED_KEY))).thenReturn(3000L); // 剩余3000秒
//
//        // 测试读取 - REDIS_SYNC_TO_LOCAL策略
//        Optional<String> result = cacheService.get(MODULE, KEY, TwoLevelCacheService.CacheStrategy.REDIS_SYNC_TO_LOCAL);
//
//        // 验证结果和调用
//        assertTrue(result.isPresent());
//        assertEquals(TEST_VALUE, result.get());
//        verify(localCache).get(eq(MODULE), eq(GENERATED_KEY));
//        verify(redisCache).getData(eq(GENERATED_KEY));
//        verify(redisCache).getTimeToLive(eq(GENERATED_KEY));
//        verify(localCache).put(eq(MODULE), eq(GENERATED_KEY), eq(TEST_VALUE), eq(3000)); // 同步到本地缓存
//    }
//
//    @Test
//    public void testRedisSyncToLocalStrategy_WithLoader() {
//        // 配置Mock对象行为
//        when(redisCache.getOrLoad(eq(GENERATED_KEY), any(), eq(EXPIRE_SECONDS))).thenReturn(TEST_VALUE);
//
//        // 定义加载函数
//        Supplier<String> loader = () -> "加载的数据";
//
//        // 测试getOrLoad方法
//        String result = cacheService.getOrLoad(MODULE, KEY, loader, EXPIRE_SECONDS, TwoLevelCacheService.CacheStrategy.REDIS_SYNC_TO_LOCAL);
//
//        // 验证结果和调用
//        assertEquals(TEST_VALUE, result);
//        verify(redisCache).getOrLoad(eq(GENERATED_KEY), any(), eq(EXPIRE_SECONDS));
//        verify(localCache).put(eq(MODULE), eq(GENERATED_KEY), eq(TEST_VALUE), anyInt()); // 同步到本地缓存
//    }
//
//    @Test
//    public void testAccessors() {
//        // 测试获取器方法
//        assertSame(localCache, cacheService.getLocalCache());
//        assertSame(redisCache, cacheService.getRedisCache());
//        assertSame(keyGenerator, cacheService.getKeyGenerator());
//    }
//}