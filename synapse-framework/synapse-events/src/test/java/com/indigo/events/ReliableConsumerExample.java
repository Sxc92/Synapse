package com.indigo.events;

import com.indigo.events.core.*;
import com.indigo.events.core.impl.ReliableRocketMQEventConsumer;
import com.indigo.events.config.EventsProperties;
import com.indigo.events.utils.EventUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 可靠消费者使用示例
 * 展示消息去重、集群协调、错误处理等功能
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@Component
public class ReliableConsumerExample implements CommandLineRunner {
    
    @Autowired
    private EventPublisher eventPublisher;
    
    @Autowired
    private EventConsumer eventConsumer;
    
    @Autowired
    private ConsumerManager consumerManager;
    
    @Autowired
    private EventsProperties properties;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("=== 可靠消费者功能演示 ===");
        
        // 1. 基础消费功能演示
        demonstrateBasicConsumption();
        
        // 2. 消息去重功能演示
        demonstrateDeduplication();
        
        // 3. 集群协调功能演示
        demonstrateClusterCoordination();
        
        // 4. 错误处理功能演示
        demonstrateErrorHandling();
        
        // 5. 性能监控演示
        demonstratePerformanceMonitoring();
        
        log.info("=== 演示完成 ===");
    }
    
    /**
     * 演示基础消费功能
     */
    private void demonstrateBasicConsumption() throws Exception {
        log.info("1. 基础消费功能演示");
        
        // 创建测试事件
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", "user123");
        eventData.put("action", "login");
        eventData.put("timestamp", System.currentTimeMillis());
        
        Event event = Event.create(EventUtils.generateTransactionId(), "user.login", "user-service", eventData);
        PublishResult result = eventPublisher.publish(event);
        log.info("事件发布结果: {}", result);
        
        // 等待处理
        TimeUnit.SECONDS.sleep(2);
        
        // 检查处理结果
        ConsumerStats stats = eventConsumer.getStats();
        log.info("消费者统计: 成功={}, 失败={}, 平均处理时间={}ms", 
                stats.getSuccessConsumed(), stats.getFailedConsumed(), stats.getAverageProcessTime());
    }
    
    /**
     * 演示消息去重功能
     */
    private void demonstrateDeduplication() throws Exception {
        log.info("2. 消息去重功能演示");
        
        // 创建相同的事件多次
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", "order456");
        eventData.put("amount", 100.50);
        
        String eventType = "order.created";
        String sourceService = "order-service";
        
        // 第一次发布
        Event event1 = Event.create(EventUtils.generateTransactionId(), eventType, sourceService, eventData);
        PublishResult result1 = eventPublisher.publish(event1);
        log.info("第一次发布: {}", result1.getTransactionId());
        
        // 第二次发布（相同内容）
        Event event2 = Event.create(EventUtils.generateTransactionId(), eventType, sourceService, eventData);
        PublishResult result2 = eventPublisher.publish(event2);
        log.info("第二次发布: {}", result2.getTransactionId());
        
        // 等待处理
        TimeUnit.SECONDS.sleep(3);
        
        // 检查去重效果
        ConsumerStats stats = eventConsumer.getStats();
        log.info("去重后统计: 成功={}, 失败={}", stats.getSuccessConsumed(), stats.getFailedConsumed());
    }
    
    /**
     * 演示集群协调功能
     */
    private void demonstrateClusterCoordination() throws Exception {
        log.info("3. 集群协调功能演示");
        
        // 检查集群状态
        long activeCount = consumerManager.getActiveConsumerCount();
        
        log.info("集群状态: 活跃消费者: {}", activeCount);
        
        // 获取集群统计信息
        Map<String, Object> clusterStats = consumerManager.getClusterStats();
        log.info("集群统计: {}", clusterStats);
        
        // 模拟集群消费者列表
        var clusterConsumers = consumerManager.getClusterConsumers();
        log.info("集群消费者数量: {}", clusterConsumers.size());
        
        clusterConsumers.forEach(consumer -> 
            log.info("消费者: {} - 状态: {} - 最后心跳: {}", 
                    consumer.getInstanceId(), consumer.getStatus(), consumer.getLastHeartbeat()));
    }
    
    /**
     * 演示错误处理功能
     */
    private void demonstrateErrorHandling() throws Exception {
        log.info("4. 错误处理功能演示");
        
        // 发布一个会触发错误的事件
        Map<String, Object> errorEventData = new HashMap<>();
        errorEventData.put("testError", true);
        errorEventData.put("shouldFail", true);
        
        Event errorEvent = Event.create(EventUtils.generateTransactionId(), "test.error", "test-service", errorEventData);
        PublishResult result = eventPublisher.publish(errorEvent);
        log.info("错误测试事件发布: {}", result);
        
        // 等待处理
        TimeUnit.SECONDS.sleep(2);
        
        // 检查错误处理结果
        ConsumerStats stats = eventConsumer.getStats();
        log.info("错误处理统计: 成功={}, 失败={}, 错误率={}%", 
                stats.getSuccessConsumed(), stats.getFailedConsumed(), 
                stats.getFailureRate() * 100);
        
        // 检查最近的错误
        if (stats.getLastErrorMessage() != null) {
            log.info("最近错误: {}", stats.getLastErrorMessage());
        }
    }
    
    /**
     * 演示性能监控功能
     */
    private void demonstratePerformanceMonitoring() throws Exception {
        log.info("5. 性能监控功能演示");
        
        // 发布多个事件进行性能测试
        for (int i = 0; i < 10; i++) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("testId", "perf-" + i);
            eventData.put("sequence", i);
            
            Event perfEvent = Event.create(EventUtils.generateTransactionId(), "performance.test", "perf-service", eventData);
            CompletableFuture<PublishResult> future = eventPublisher.publishAsync(perfEvent);
            future.thenAccept(result -> log.debug("异步发布完成: {}", result.getTransactionId()));
        }
        
        // 等待处理完成
        TimeUnit.SECONDS.sleep(5);
        
        // 检查性能指标
        ConsumerStats stats = eventConsumer.getStats();
        log.info("性能统计:");
        log.info("  - 总处理数: {}", stats.getTotalConsumed());
        log.info("  - 平均处理时间: {}ms", stats.getAverageProcessTime());
        log.info("  - 最大处理时间: {}ms", stats.getMaxProcessTime());
        log.info("  - 最小处理时间: {}ms", stats.getMinProcessTime());
        log.info("  - 处理速率: {}/s", stats.getProcessRate());
        log.info("  - 错误率: {}%", stats.getFailureRate() * 100);
        
        // 检查配置的监控阈值
        log.info("监控阈值配置:");
        log.info("  - 错误率阈值: {}%", properties.getReliable().getErrorRateThreshold());
        log.info("  - 延迟阈值: {}ms", properties.getReliable().getLatencyThreshold());
        log.info("  - 队列积压阈值: {}", properties.getReliable().getQueueBacklogThreshold());
        
        // 检查是否超过阈值
        if (stats.getFailureRate() * 100 > properties.getReliable().getErrorRateThreshold()) {
            log.warn("⚠️ 错误率超过阈值: {}% > {}%", 
                    stats.getFailureRate() * 100, properties.getReliable().getErrorRateThreshold());
        }
        
        if (stats.getAverageProcessTime() > properties.getReliable().getLatencyThreshold()) {
            log.warn("⚠️ 平均处理时间超过阈值: {}ms > {}ms", 
                    stats.getAverageProcessTime(), properties.getReliable().getLatencyThreshold());
        }
    }
} 