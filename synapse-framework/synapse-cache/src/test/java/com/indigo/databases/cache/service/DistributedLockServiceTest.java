//package com.indigo.databases.cache.service;
//
//import com.indigo.cache.manager.CacheKeyGenerator;
//import com.indigo.cache.extension.DistributedLockService;
//import com.indigo.core.utils.ThreadUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.script.RedisScript;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class DistributedLockServiceTest {
//
//    @Mock
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Mock
//    private CacheKeyGenerator keyGenerator;
//
//    private DistributedLockService lockService;
//
//    private static final String TEST_LOCK_NAME = "testLock";
//    private static final String TEST_KEY = "testKey";
//    private static final String TEST_LOCK_KEY = "lock:testLock:testKey";
//    private static final String TEST_LOCK_VALUE = "nodeId:threadId:uuid";
//    @Mock
//    private ThreadUtils threadUtils;
//    @BeforeEach
//    void setUp() {
//        when(keyGenerator.generate(any(), anyString(), anyString())).thenReturn(TEST_LOCK_KEY);
//        doAnswer(invocation -> true)
//            .when(redisTemplate)
//            .execute(any(org.springframework.data.redis.core.script.RedisScript.class), any(), any());
//        doAnswer(invocation -> true)
//            .when(redisTemplate)
//            .execute(any(org.springframework.data.redis.core.RedisCallback.class));
//        lockService = new DistributedLockService(redisTemplate, keyGenerator,threadUtils);
//    }
//
//    @Test
//    void testTryLock_Success() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> true);
//
//        // When
//        String lockValue = lockService.tryLock(TEST_LOCK_NAME, TEST_KEY);
//
//        // Then
//        assertNotNull(lockValue);
//        verify(redisTemplate).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testTryLock_Failure() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> false);
//
//        // When
//        String lockValue = lockService.tryLock(TEST_LOCK_NAME, TEST_KEY);
//
//        // Then
//        assertNull(lockValue);
//        verify(redisTemplate).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testReentrantLock() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> true);
//
//        // When
//        String lockValue1 = lockService.tryLock(TEST_LOCK_NAME, TEST_KEY);
//        String lockValue2 = lockService.tryLock(TEST_LOCK_NAME, TEST_KEY);
//
//        // Then
//        assertNotNull(lockValue1);
//        assertEquals(lockValue1, lockValue2);
//        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testUnlock_Success() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> true);
//        String lockValue = lockService.tryLock(TEST_LOCK_NAME, TEST_KEY);
//
//        // When
//        boolean result = lockService.unlock(TEST_LOCK_NAME, TEST_KEY, lockValue);
//
//        // Then
//        assertTrue(result);
//        verify(redisTemplate, times(2)).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testUnlock_Failure() {
//        // Given
//        // 没有加锁，直接解锁
//        // When
//        boolean result = lockService.unlock(TEST_LOCK_NAME, TEST_KEY, "wrongValue");
//
//        // Then
//        assertFalse(result);
//        // 不应断言redisTemplate.execute被调用
//    }
//
//    @Test
//    void testReentrantUnlock() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> true);
//        String lockValue = lockService.tryLock(TEST_LOCK_NAME, TEST_KEY);
//        lockService.tryLock(TEST_LOCK_NAME, TEST_KEY); // 重入
//
//        // When
//        boolean result1 = lockService.unlock(TEST_LOCK_NAME, TEST_KEY, lockValue);
//        boolean result2 = lockService.unlock(TEST_LOCK_NAME, TEST_KEY, lockValue);
//
//        // Then
//        assertTrue(result1);
//        assertTrue(result2);
//        // 1次加锁，1次解锁
//        verify(redisTemplate, times(2)).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testLockWithWait_Success() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(new org.mockito.stubbing.Answer<Boolean>() {
//            private int count = 0;
//            @Override
//            public Boolean answer(org.mockito.invocation.InvocationOnMock invocation) {
//                return count++ == 0 ? false : true;
//            }
//        });
//
//        // When
//        String lockValue = lockService.lock(TEST_LOCK_NAME, TEST_KEY, 10, 1);
//
//        // Then
//        assertNotNull(lockValue);
//        verify(redisTemplate, times(2)).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testLockWithWait_Timeout() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> false);
//
//        // When
//        String lockValue = lockService.lock(TEST_LOCK_NAME, TEST_KEY, 10, 1);
//
//        // Then
//        assertNull(lockValue);
//        verify(redisTemplate, atLeastOnce()).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testExecuteWithLock_Success() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> true);
//        AtomicBoolean executed = new AtomicBoolean(false);
//
//        // When
//        Boolean result = lockService.executeWithLock(TEST_LOCK_NAME, TEST_KEY, () -> {
//            executed.set(true);
//            return true;
//        });
//
//        // Then
//        assertTrue(result);
//        assertTrue(executed.get());
//        verify(redisTemplate, times(2)).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testExecuteWithLock_Failure() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> false);
//        AtomicBoolean executed = new AtomicBoolean(false);
//
//        // When
//        Boolean result = lockService.executeWithLock(TEST_LOCK_NAME, TEST_KEY, () -> {
//            executed.set(true);
//            return true;
//        });
//
//        // Then
//        assertNull(result);
//        assertFalse(executed.get());
//        verify(redisTemplate).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testExecuteWithLockAndWait_Success() {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(new org.mockito.stubbing.Answer<Boolean>() {
//            private int count = 0;
//            @Override
//            public Boolean answer(org.mockito.invocation.InvocationOnMock invocation) {
//                return count++ == 0 ? false : true;
//            }
//        });
//        AtomicBoolean executed = new AtomicBoolean(false);
//
//        // When
//        Boolean result = lockService.executeWithLockAndWait(TEST_LOCK_NAME, TEST_KEY, 10, 1, () -> {
//            executed.set(true);
//            return true;
//        });
//
//        // Then
//        assertTrue(result);
//        assertTrue(executed.get());
//        verify(redisTemplate, times(3)).execute(any(RedisCallback.class));
//    }
//
//    @Test
//    void testConcurrentLock() throws InterruptedException {
//        // Given
//        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> true);
//        int threadCount = 10;
//        CountDownLatch startLatch = new CountDownLatch(1);
//        CountDownLatch endLatch = new CountDownLatch(threadCount);
//        AtomicBoolean success = new AtomicBoolean(true);
//
//        // When
//        for (int i = 0; i < threadCount; i++) {
//            new Thread(() -> {
//                try {
//                    startLatch.await();
//                    String lockValue = lockService.tryLock(TEST_LOCK_NAME, TEST_KEY);
//                    if (lockValue != null) {
//                        Thread.sleep(100);
//                        lockService.unlock(TEST_LOCK_NAME, TEST_KEY, lockValue);
//                    } else {
//                        success.set(false);
//                    }
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    success.set(false);
//                } finally {
//                    endLatch.countDown();
//                }
//            }).start();
//        }
//        startLatch.countDown();
//        endLatch.await(5, TimeUnit.SECONDS);
//
//        // Then
//        assertTrue(success.get());
//        verify(redisTemplate, atLeast(threadCount * 2)).execute(any(RedisCallback.class));
//    }
//}