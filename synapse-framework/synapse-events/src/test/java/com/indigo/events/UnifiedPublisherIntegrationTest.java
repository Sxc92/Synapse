package com.indigo.events;

import com.indigo.events.core.Event;
import com.indigo.events.core.EventPublisher;
import com.indigo.events.core.PublishResult;
import com.indigo.events.utils.EventUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统一发布器集成测试
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class UnifiedPublisherIntegrationTest {
    
    @Autowired
    private EventPublisher eventPublisher;
    
    @Test
    public void testBasicPublish() throws Exception {
        log.info("=== 测试基础发布功能 ===");
        
        // 创建测试事件
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", "test-user-001");
        eventData.put("action", "login");
        eventData.put("timestamp", System.currentTimeMillis());
        
        Event event = Event.create(EventUtils.generateTransactionId(), "user.login", "test-service", eventData);
        
        // 发布事件
        PublishResult result = eventPublisher.publish(event);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getEventId());
        assertNotNull(result.getTransactionId());
        
        log.info("发布结果: {}", result);
    }
    
    @Test
    public void testAsyncPublish() throws Exception {
        log.info("=== 测试异步发布功能 ===");
        
        // 创建测试事件
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", "test-order-001");
        eventData.put("amount", 100.50);
        eventData.put("timestamp", System.currentTimeMillis());
        
        Event event = Event.create(EventUtils.generateTransactionId(), "order.created", "test-service", eventData);
        
        // 异步发布事件
        CompletableFuture<PublishResult> future = eventPublisher.publishAsync(event);
        
        // 等待结果
        PublishResult result = future.get(5, TimeUnit.SECONDS);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getEventId());
        assertNotNull(result.getTransactionId());
        
        log.info("异步发布结果: {}", result);
    }
    
    @Test
    public void testSimplifiedApi() throws Exception {
        log.info("=== 测试简化 API ===");
        
        // 使用简化 API 发布事件
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("productId", "test-product-001");
        eventData.put("price", 99.99);
        eventData.put("timestamp", System.currentTimeMillis());
        
        Event event = Event.create(EventUtils.generateTransactionId(), "product.created", "test-service", eventData);
        PublishResult result = eventPublisher.publish(event);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getEventId());
        assertNotNull(result.getTransactionId());
        
        log.info("简化 API 发布结果: {}", result);
    }
    
    @Test
    public void testUrgentPublish() throws Exception {
        log.info("=== 测试紧急事件发布 ===");
        
        // 发布紧急事件
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("alertType", "system_error");
        eventData.put("severity", "high");
        eventData.put("timestamp", System.currentTimeMillis());
        
        Event event = Event.create(EventUtils.generateTransactionId(), "system.alert", "test-service", eventData);
        event.setPriority(com.indigo.events.core.EventPriority.URGENT);
        PublishResult result = eventPublisher.publish(event);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getEventId());
        assertNotNull(result.getTransactionId());
        
        log.info("紧急事件发布结果: {}", result);
    }
    
    @Test
    public void testSystemPublish() throws Exception {
        log.info("=== 测试系统事件发布 ===");
        
        // 发布系统事件
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("operation", "backup");
        eventData.put("status", "completed");
        eventData.put("timestamp", System.currentTimeMillis());
        
        Event event = Event.create(EventUtils.generateTransactionId(), "system.backup", "test-service", eventData);
        event.setPriority(com.indigo.events.core.EventPriority.SYSTEM);
        PublishResult result = eventPublisher.publish(event);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getEventId());
        assertNotNull(result.getTransactionId());
        
        log.info("系统事件发布结果: {}", result);
    }
} 