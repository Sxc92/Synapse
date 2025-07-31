package com.indigo.events.core.impl;

import com.indigo.cache.core.CacheService;
import com.indigo.events.config.EventsProperties;
import com.indigo.events.core.*;
import com.indigo.events.utils.EventUtils;
import com.indigo.events.utils.MessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 统一的 RocketMQ 事件发布器
 * 集成所有高级功能：去重、延迟、顺序、优先级等
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@Component
public class UnifiedRocketMQEventPublisher implements EventPublisher {
    
    private final EventsProperties properties;
    private final MessageSerializer messageSerializer;
    private final CacheService cacheService;
    
    private DefaultMQProducer producer;
    private final AtomicLong messageCounter = new AtomicLong(0);
    private final ConcurrentHashMap<String, Long> localDuplicateCache = new ConcurrentHashMap<>();
    
    @Autowired
    public UnifiedRocketMQEventPublisher(EventsProperties properties,
                                        MessageSerializer messageSerializer,
                                        CacheService cacheService) {
        this.properties = properties;
        this.messageSerializer = messageSerializer;
        this.cacheService = cacheService;
    }
    
    @PostConstruct
    public void init() {
        try {
            // 创建生产者
            producer = new DefaultMQProducer(properties.getRocketmq().getProducerGroup());
            producer.setNamesrvAddr(properties.getRocketmq().getNameServer());
            producer.setSendMsgTimeout(properties.getRocketmq().getSendTimeout());
            
            // 设置重试次数
            producer.setRetryTimesWhenSendFailed(properties.getRocketmq().getMaxRetryTimes());
            producer.setRetryTimesWhenSendAsyncFailed(properties.getRocketmq().getMaxRetryTimes());
            
            // 启动生产者
            producer.start();
            
            log.info("Unified RocketMQ producer started: group={}, nameServer={}", 
                    properties.getRocketmq().getProducerGroup(), properties.getRocketmq().getNameServer());
            log.info("Duplicate check enabled: {}, use Redis: {}", 
                    properties.getDuplicateCheck().isEnabled(), properties.getDuplicateCheck().isUseRedis());
                    
        } catch (Exception e) {
            log.error("Failed to initialize unified RocketMQ producer", e);
            throw new RuntimeException("Failed to initialize unified RocketMQ producer", e);
        }
    }
    
    @PreDestroy
    public void destroy() {
        if (producer != null) {
            producer.shutdown();
            log.info("Unified RocketMQ producer shutdown");
        }
    }
    
    // ==================== 基础发布方法（EventPublisher 接口实现） ====================
    
    @Override
    public PublishResult publish(Event event) {
        try {
            // 1. 消息去重检查
            if (properties.getDuplicateCheck().isEnabled() && isDuplicateMessage(event)) {
                log.warn("Duplicate message detected, skipping: {}", event.getEventId());
                return PublishResult.success(event.getEventId(), event.getTransactionId(), "duplicate-skipped");
            }
            
            // 2. 序列化消息
            String topic = properties.getRocketmq().getTopicPrefix() + "-" + event.getEventType();
            Message message = messageSerializer.serializeEvent(event, topic);
            
            // 3. 发送消息
            SendResult sendResult = producer.send(message);
            
            // 4. 记录消息发送
            recordMessageSent(event);
            
            log.debug("Event published successfully: eventId={}, msgId={}, transactionId={}", 
                    event.getEventId(), sendResult.getMsgId(), event.getTransactionId());
            
            return PublishResult.success(event.getEventId(), event.getTransactionId(), sendResult.getMsgId());
            
        } catch (Exception e) {
            log.error("Failed to publish event: {}", event.getEventId(), e);
            return PublishResult.failure(event.getEventId(), event.getTransactionId(), "PUBLISH_ERROR", e.getMessage());
        }
    }
    
    @Override
    public CompletableFuture<PublishResult> publishAsync(Event event) {
        CompletableFuture<PublishResult> future = new CompletableFuture<>();
        
        try {
            // 1. 消息去重检查
            if (properties.getDuplicateCheck().isEnabled() && isDuplicateMessage(event)) {
                log.warn("Duplicate message detected, skipping: {}", event.getEventId());
                future.complete(PublishResult.success(event.getEventId(), event.getTransactionId(), "duplicate-skipped"));
                return future;
            }
            
            // 2. 序列化消息
            String topic = properties.getRocketmq().getTopicPrefix() + "-" + event.getEventType();
            Message message = messageSerializer.serializeEvent(event, topic);
            
            // 3. 异步发送消息
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    recordMessageSent(event);
                    log.debug("Async event published successfully: eventId={}, msgId={}", 
                            event.getEventId(), sendResult.getMsgId());
                    future.complete(PublishResult.success(event.getEventId(), event.getTransactionId(), sendResult.getMsgId()));
                }
                
                @Override
                public void onException(Throwable e) {
                    log.error("Failed to publish async event: {}", event.getEventId(), e);
                    future.complete(PublishResult.failure(event.getEventId(), event.getTransactionId(), "PUBLISH_ERROR", e.getMessage()));
                }
            });
            
        } catch (Exception e) {
            log.error("Failed to publish async event: {}", event.getEventId(), e);
            future.complete(PublishResult.failure(event.getEventId(), event.getTransactionId(), "PUBLISH_ERROR", e.getMessage()));
        }
        
        return future;
    }
    
    // ==================== 简化 API（自动生成 transactionId） ====================
    
    /**
     * 简化 API：发布事件（自动生成 transactionId）
     */
    public PublishResult publish(String eventType, String sourceService, java.util.Map<String, Object> eventData) {
        if (!properties.isAutoGenerateTransactionId()) {
            throw new IllegalStateException("Auto generate transactionId is disabled");
        }
        
        String transactionId = EventUtils.generateTransactionId();
        Event event = Event.create(transactionId, eventType, sourceService, eventData);
        return publish(event);
    }
    
    /**
     * 简化 API：异步发布事件（自动生成 transactionId）
     */
    public CompletableFuture<PublishResult> publishAsync(String eventType, String sourceService, java.util.Map<String, Object> eventData) {
        if (!properties.isAutoGenerateTransactionId()) {
            throw new IllegalStateException("Auto generate transactionId is disabled");
        }
        
        String transactionId = EventUtils.generateTransactionId();
        Event event = Event.create(transactionId, eventType, sourceService, eventData);
        return publishAsync(event);
    }
    
    /**
     * 简化 API：发布紧急事件（自动生成 transactionId）
     */
    public PublishResult publishUrgent(String eventType, String sourceService, java.util.Map<String, Object> eventData) {
        if (!properties.isAutoGenerateTransactionId()) {
            throw new IllegalStateException("Auto generate transactionId is disabled");
        }
        
        String transactionId = EventUtils.generateTransactionId();
        Event event = Event.create(transactionId, eventType, sourceService, eventData);
        event.setPriority(EventPriority.URGENT);
        return publish(event);
    }
    
    /**
     * 简化 API：发布系统事件（自动生成 transactionId）
     */
    public PublishResult publishSystem(String eventType, String sourceService, java.util.Map<String, Object> eventData) {
        if (!properties.isAutoGenerateTransactionId()) {
            throw new IllegalStateException("Auto generate transactionId is disabled");
        }
        
        String transactionId = EventUtils.generateTransactionId();
        Event event = Event.create(transactionId, eventType, sourceService, eventData);
        event.setPriority(EventPriority.SYSTEM);
        return publish(event);
    }
    
    // ==================== 高级功能 ====================
    
    /**
     * 发布延迟消息
     */
    public PublishResult publishWithDelay(String eventType, String sourceService, 
                                        java.util.Map<String, Object> eventData, int delayLevel) {
        if (!properties.isAutoGenerateTransactionId()) {
            throw new IllegalStateException("Auto generate transactionId is disabled");
        }
        
        String transactionId = EventUtils.generateTransactionId();
        Event event = Event.create(transactionId, eventType, sourceService, eventData);
        
        try {
            // 去重检查
            if (properties.getDuplicateCheck().isEnabled() && isDuplicateMessage(event)) {
                log.warn("Duplicate delayed message detected, skipping: {}", event.getEventId());
                return PublishResult.success(event.getEventId(), event.getTransactionId(), "duplicate-skipped");
            }
            
            // 序列化消息
            String topic = properties.getRocketmq().getTopicPrefix() + "-" + eventType;
            Message message = messageSerializer.serializeEvent(event, topic);
            
            // 设置延迟级别
            message.setDelayTimeLevel(delayLevel);
            
            // 发送消息
            SendResult sendResult = producer.send(message);
            
            // 记录消息发送
            recordMessageSent(event);
            
            log.debug("Delayed message sent successfully: msgId={}, transactionId={}, delayLevel={}", 
                    sendResult.getMsgId(), event.getTransactionId(), delayLevel);
            
            return PublishResult.success(event.getEventId(), event.getTransactionId(), sendResult.getMsgId());
            
        } catch (Exception e) {
            log.error("Failed to send delayed message: {}", event.getEventId(), e);
            return PublishResult.failure(event.getEventId(), event.getTransactionId(), "SEND_ERROR", e.getMessage());
        }
    }
    
    /**
     * 发布顺序消息
     */
    public PublishResult publishOrderly(String eventType, String sourceService, 
                                      java.util.Map<String, Object> eventData, String orderKey) {
        if (!properties.isAutoGenerateTransactionId()) {
            throw new IllegalStateException("Auto generate transactionId is disabled");
        }
        
        String transactionId = EventUtils.generateTransactionId();
        Event event = Event.create(transactionId, eventType, sourceService, eventData);
        
        try {
            // 去重检查
            if (properties.getDuplicateCheck().isEnabled() && isDuplicateMessage(event)) {
                log.warn("Duplicate orderly message detected, skipping: {}", event.getEventId());
                return PublishResult.success(event.getEventId(), event.getTransactionId(), "duplicate-skipped");
            }
            
            // 序列化消息
            String topic = properties.getRocketmq().getTopicPrefix() + "-" + eventType;
            Message message = messageSerializer.serializeEvent(event, topic);
            
            // 发送顺序消息
            SendResult sendResult = producer.send(message, (mqs, msg, arg) -> {
                int index = Math.abs(arg.hashCode()) % mqs.size();
                return mqs.get(index);
            }, orderKey);
            
            // 记录消息发送
            recordMessageSent(event);
            
            log.debug("Orderly message sent successfully: msgId={}, transactionId={}, orderKey={}", 
                    sendResult.getMsgId(), event.getTransactionId(), orderKey);
            
            return PublishResult.success(event.getEventId(), event.getTransactionId(), sendResult.getMsgId());
            
        } catch (Exception e) {
            log.error("Failed to send orderly message: {}", event.getEventId(), e);
            return PublishResult.failure(event.getEventId(), event.getTransactionId(), "SEND_ERROR", e.getMessage());
        }
    }
    
    // ==================== 核心发布逻辑 ====================
    
    /**
     * 构建主题名称
     */
    private String buildTopic(Event event) {
        return properties.getRocketmq().getTopicPrefix() + "-" + event.getEventType();
    }
    
    /**
     * 设置消息属性
     */
    private void setMessageProperties(Message message, Event event, boolean autoGeneratedTransactionId) {
        // 设置消息属性
        message.putUserProperty("autoGeneratedTransactionId", String.valueOf(autoGeneratedTransactionId));
        message.putUserProperty("publishTime", String.valueOf(System.currentTimeMillis()));
        
        // 设置消息标签（用于消息过滤）
        message.setTags(event.getEventType());
        
        // 设置消息键（用于消息去重）
        message.setKeys(event.getEventId());
    }
    
    // ==================== 消息去重逻辑（使用 RedisService） ====================
    
    /**
     * 生成消息去重键
     */
    private String generateMessageKey(Event event) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(event.getEventType())
                  .append(":")
                  .append(event.getSourceService())
                  .append(":");
        
        // 根据事件类型提取关键字段
        java.util.Map<String, Object> eventData = event.getEventData();
        if (eventData != null) {
            String[] keyFields = {"userId", "orderId", "productId", "taskId", "id"};
            for (String field : keyFields) {
                if (eventData.containsKey(field)) {
                    keyBuilder.append(field).append("=").append(eventData.get(field));
                    break;
                }
            }
        }
        
        // 如果没有找到关键字段，使用事件ID
        if (keyBuilder.toString().endsWith(":")) {
            keyBuilder.append("eventId=").append(event.getEventId());
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 检查是否为重复消息
     */
    private boolean isDuplicateMessage(Event event) {
        String messageKey = event.getEventId() + ":" + event.getEventType() + ":" + event.getSourceService();
        
        // 1. 先检查本地缓存（快速路径）
        Long lastSendTime = localDuplicateCache.get(messageKey);
        if (lastSendTime != null) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastSendTime) < properties.getDuplicateCheck().getExpireMinutes() * 60 * 1000L) {
                return true;
            }
        }
        
        // 2. 如果启用 Redis，检查分布式缓存
        if (properties.getDuplicateCheck().isUseRedis()) {
            try {
                String redisKey = properties.getDuplicateCheck().getRedisKeyPrefix() + messageKey;
                Object cachedTime = cacheService.getObject(redisKey, String.class);
                if (cachedTime != null) {
                    // 在本地缓存中也记录一下，提高后续查询性能
                    localDuplicateCache.put(messageKey, System.currentTimeMillis());
                    return true;
                }
            } catch (Exception e) {
                log.warn("Failed to check Redis for duplicate message: {}", messageKey, e);
            }
        }
        
        return false;
    }
    
    /**
     * 记录消息已发送（用于去重）
     */
    private void recordMessageSent(Event event) {
        String messageKey = event.getEventId() + ":" + event.getEventType() + ":" + event.getSourceService();
        long currentTime = System.currentTimeMillis();
        
        // 1. 更新本地缓存
        localDuplicateCache.put(messageKey, currentTime);
        
        // 2. 如果启用 Redis，更新分布式缓存
        if (properties.getDuplicateCheck().isUseRedis()) {
            try {
                String redisKey = properties.getDuplicateCheck().getRedisKeyPrefix() + messageKey;
                // 使用 CacheService 设置带过期时间的缓存
                cacheService.setObject(redisKey, String.valueOf(currentTime), properties.getDuplicateCheck().getExpireMinutes() * 60);
            } catch (Exception e) {
                log.warn("Failed to record message in Redis: {}", messageKey, e);
            }
        }
        
        // 3. 更新计数器
        messageCounter.incrementAndGet();
        
        // 4. 定期清理本地缓存
        if (messageCounter.get() % 100 == 0) {
            cleanupLocalCache();
        }
    }
    
    /**
     * 清理过期的本地缓存
     */
    private void cleanupLocalCache() {
        long currentTime = System.currentTimeMillis();
        long expireTime = properties.getDuplicateCheck().getExpireMinutes() * 60 * 1000L;
        
        localDuplicateCache.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) > expireTime
        );
        
        log.debug("Cleaned up local cache, current size: {}", localDuplicateCache.size());
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 获取消息缓存统计信息
     */
    public java.util.Map<String, Object> getMessageCacheStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("localCacheSize", localDuplicateCache.size());
        stats.put("messageCounter", messageCounter.get());
        stats.put("duplicateCheckEnabled", properties.getDuplicateCheck().isEnabled());
        stats.put("useRedisForDuplicateCheck", properties.getDuplicateCheck().isUseRedis());
        stats.put("autoGenerateTransactionId", properties.isAutoGenerateTransactionId());
        stats.put("redisKeyPrefix", properties.getDuplicateCheck().getRedisKeyPrefix());
        
        // Redis 统计信息
        if (properties.getDuplicateCheck().isUseRedis()) {
            try {
                // 使用 CacheService 的 deleteByPrefix 方法获取键数量
                String pattern = properties.getDuplicateCheck().getRedisKeyPrefix() + "*";
                long keyCount = cacheService.deleteByPrefix(properties.getDuplicateCheck().getRedisKeyPrefix());
                stats.put("redisKeyCount", keyCount);
            } catch (Exception e) {
                log.warn("Failed to get Redis stats", e);
                stats.put("redisKeyCount", -1);
            }
        }
        
        return stats;
    }
    
    /**
     * 清理所有缓存
     */
    public void clearAllCaches() {
        localDuplicateCache.clear();
        messageCounter.set(0);
        
        if (properties.getDuplicateCheck().isUseRedis()) {
            try {
                // 使用 CacheService 的 deleteByPrefix 方法清理 Redis 缓存
                long deletedCount = cacheService.deleteByPrefix(properties.getDuplicateCheck().getRedisKeyPrefix());
                if (deletedCount > 0) {
                    log.info("Cleared {} Redis duplicate check keys", deletedCount);
                }
            } catch (Exception e) {
                log.warn("Failed to clear Redis cache", e);
            }
        }
        
        log.info("All caches cleared");
    }
} 