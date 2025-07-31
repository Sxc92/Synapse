package com.indigo.events.core.impl;

import com.indigo.cache.core.CacheService;
import com.indigo.cache.extension.DistributedLockService;
import com.indigo.events.config.EventsProperties;
import com.indigo.events.core.*;
import com.indigo.events.utils.MessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 可靠的 RocketMQ 事件消费者
 * 解决消息重复消费、消息丢失和集群稳定性问题
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@Component
public class ReliableRocketMQEventConsumer implements EventConsumer {
    
    private final EventsProperties properties;
    private final MessageSerializer messageSerializer;
    private final EventHandlerRegistry eventHandlerRegistry;
    private final CacheService cacheService;
    private final DistributedLockService lockService;
    
    @Value("${spring.application.name:unknown-service}")
    private String applicationName;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    private DefaultMQPushConsumer consumer;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ConsumerStats stats = ConsumerStats.create();
    private final ConcurrentHashMap<String, AtomicLong> eventTypeCounters = new ConcurrentHashMap<>();
    
    // 消费者实例标识
    private String consumerInstanceId;
    
    // 去重缓存键前缀
    private static final String DEDUP_KEY_PREFIX = "event:dedup:";
    private static final String CONSUMER_STATUS_KEY_PREFIX = "consumer:status:";
    private static final String CONSUMER_LOCK_KEY_PREFIX = "consumer:lock:";
    
    // 去重缓存过期时间（秒）
    private static final int DEDUP_EXPIRE_SECONDS = 3600; // 1小时
    
    @Autowired
    public ReliableRocketMQEventConsumer(EventsProperties properties, 
                                        MessageSerializer messageSerializer,
                                        EventHandlerRegistry eventHandlerRegistry,
                                        CacheService cacheService,
                                        DistributedLockService lockService) {
        this.properties = properties;
        this.messageSerializer = messageSerializer;
        this.eventHandlerRegistry = eventHandlerRegistry;
        this.cacheService = cacheService;
        this.lockService = lockService;
    }
    
    @PostConstruct
    public void init() {
        try {
            // 生成消费者实例ID
            this.consumerInstanceId = generateConsumerInstanceId();
            
            // 创建消费者
            consumer = new DefaultMQPushConsumer(properties.getRocketmq().getConsumerGroup());
            consumer.setNamesrvAddr(properties.getRocketmq().getNameServer());
            consumer.setInstanceName(consumerInstanceId);
            
            // 设置批量消费
            consumer.setConsumeMessageBatchMaxSize(properties.getReliable().getBatchSize());
            consumer.setPullBatchSize(properties.getRocketmq().getPullBatchSize());
            
            // 设置消费超时时间
            consumer.setConsumeTimeout(15); // 15分钟
            
            // 订阅主题
            String topicPattern = properties.getRocketmq().getTopicPrefix() + "-*";
            consumer.subscribe(topicPattern, "*");
            
            // 设置消息监听器
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, 
                                                               ConsumeConcurrentlyContext context) {
                    return processMessagesReliably(msgs, context);
                }
            });
            
            // 注册消费者状态
            registerConsumerStatus();
            
            log.info("Reliable RocketMQ consumer initialized: instance={}, group={}, nameServer={}, topicPattern={}", 
                    consumerInstanceId, properties.getRocketmq().getConsumerGroup(), properties.getRocketmq().getNameServer(), topicPattern);
                    
        } catch (Exception e) {
            log.error("Failed to initialize reliable RocketMQ consumer", e);
            throw new RuntimeException("Failed to initialize reliable RocketMQ consumer", e);
        }
    }
    
    @Override
    public EventResult consume(Event event) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 验证事件
            if (!isValidEvent(event)) {
                return EventResult.failure(event.getEventId(), event.getTransactionId(), "INVALID_EVENT", "Invalid event");
            }
            
            // 分布式去重检查
            if (isDuplicateEvent(event)) {
                log.debug("Duplicate event skipped: {}", event.getEventId());
                return EventResult.success(event.getEventId(), event.getTransactionId());
            }
            
            // 获取分布式锁，确保同一事件不会被多个消费者同时处理
            String lockKey = CONSUMER_LOCK_KEY_PREFIX + event.getEventId();
            String lockValue = lockService.tryLock("event-consumer", event.getEventId(), 30); // 30秒锁超时
            
            if (lockValue == null) {
                log.warn("Failed to acquire lock for event: {}, skipping", event.getEventId());
                return EventResult.failure(event.getEventId(), event.getTransactionId(), "LOCK_FAILED", "Failed to acquire distributed lock");
            }
            
            try {
                // 再次检查去重（双重检查）
                if (isDuplicateEvent(event)) {
                    log.debug("Duplicate event detected after lock acquisition: {}", event.getEventId());
                    return EventResult.success(event.getEventId(), event.getTransactionId());
                }
                
                // 查找事件处理器
                List<EventHandler> handlers = eventHandlerRegistry.getHandlers(event.getEventType());
                if (handlers.isEmpty()) {
                    log.warn("No handler found for event type: {}", event.getEventType());
                    return EventResult.success(event.getEventId(), event.getTransactionId());
                }
                
                // 执行事件处理
                EventResult result = executeEventHandlers(event, handlers);
                
                // 如果处理成功，标记为已处理（去重）
                if (result.isSuccess()) {
                    markEventAsProcessed(event);
                }
                
                // 记录统计信息
                long processTime = System.currentTimeMillis() - startTime;
                if (result.isSuccess()) {
                    stats.recordSuccess(processTime);
                } else {
                    stats.recordFailure(processTime, result.getErrorMessage());
                }
                
                // 更新事件类型计数器
                updateEventTypeCounter(event.getEventType());
                
                return result;
                
            } finally {
                // 释放分布式锁
                lockService.unlock("event-consumer", event.getEventId(), lockValue);
            }
            
        } catch (Exception e) {
            long processTime = System.currentTimeMillis() - startTime;
            stats.recordFailure(processTime, e.getMessage());
            log.error("Failed to consume event: {}", event.getEventId(), e);
            return EventResult.failure(event.getEventId(), event.getTransactionId(), "CONSUME_ERROR", e.getMessage());
        }
    }
    
    @Override
    public CompletableFuture<EventResult> consumeAsync(Event event) {
        return CompletableFuture.supplyAsync(() -> consume(event));
    }
    
    @Override
    public EventResult consumeBatch(List<Event> events) {
        EventResult batchResult = EventResult.success("batch", "BATCH_PROCESSED");
        
        for (Event event : events) {
            EventResult result = consume(event);
            if (!result.isSuccess()) {
                log.error("Batch processing failed for event: {} - {}", event.getEventId(), result.getErrorMessage());
                // 继续处理其他事件，但记录失败
            }
        }
        
        return batchResult;
    }
    
    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            try {
                consumer.start();
                updateConsumerStatus(ConsumerStatus.RUNNING);
                log.info("Reliable RocketMQ consumer started: {}", consumerInstanceId);
            } catch (MQClientException e) {
                running.set(false);
                updateConsumerStatus(ConsumerStatus.ERROR);
                log.error("Failed to start reliable RocketMQ consumer", e);
                throw new RuntimeException("Failed to start reliable RocketMQ consumer", e);
            }
        }
    }
    
    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            if (consumer != null) {
                consumer.shutdown();
                updateConsumerStatus(ConsumerStatus.STOPPED);
                log.info("Reliable RocketMQ consumer stopped: {}", consumerInstanceId);
            }
        }
    }
    
    @Override
    public boolean isRunning() {
        return running.get();
    }
    
    @Override
    public ConsumerStatus getStatus() {
        if (!running.get()) {
            return ConsumerStatus.STOPPED;
        }
        if (consumer != null && consumer.getDefaultMQPushConsumerImpl().getServiceState().name().equals("RUNNING")) {
            return ConsumerStatus.RUNNING;
        }
        return ConsumerStatus.ERROR;
    }
    
    @Override
    public ConsumerStats getStats() {
        return stats;
    }
    
    /**
     * 可靠的消息处理
     */
    private ConsumeConcurrentlyStatus processMessagesReliably(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            for (MessageExt msg : msgs) {
                // 反序列化事件
                Event event = messageSerializer.deserializeEvent(msg);
                if (event == null) {
                    log.warn("Failed to deserialize message: {}", msg.getMsgId());
                    continue;
                }
                
                // 消费事件
                EventResult result = consume(event);
                if (!result.isSuccess()) {
                    log.error("Failed to process event: {} - {}", event.getEventId(), result.getErrorMessage());
                    
                    // 根据错误类型决定是否重试
                    if (shouldRetry(result.getErrorCode())) {
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    } else {
                        // 不重试的错误，标记为已处理
                        markEventAsProcessed(event);
                        log.warn("Event marked as processed despite failure: {}", event.getEventId());
                    }
                }
            }
            
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            
        } catch (Exception e) {
            log.error("Error processing messages", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    /**
     * 验证事件
     */
    private boolean isValidEvent(Event event) {
        return event != null && 
               event.getEventId() != null && 
               event.getEventType() != null && 
               event.getSourceService() != null;
    }
    
    /**
     * 分布式去重检查
     */
    private boolean isDuplicateEvent(Event event) {
        String dedupKey = DEDUP_KEY_PREFIX + event.getEventId();
        return cacheService.exists(dedupKey);
    }
    
    /**
     * 标记事件为已处理
     */
    private void markEventAsProcessed(Event event) {
        String dedupKey = DEDUP_KEY_PREFIX + event.getEventId();
        cacheService.setObject(dedupKey, "processed", DEDUP_EXPIRE_SECONDS);
    }
    
    /**
     * 判断是否应该重试
     */
    private boolean shouldRetry(String errorCode) {
        // 定义不需要重试的错误码
        return !"LOCK_FAILED".equals(errorCode) && 
               !"DUPLICATE_SKIPPED".equals(errorCode) && 
               !"NO_HANDLER".equals(errorCode);
    }
    
    /**
     * 执行事件处理器
     */
    private EventResult executeEventHandlers(Event event, List<EventHandler> handlers) {
        EventResult finalResult = EventResult.success(event.getEventId(), event.getTransactionId());
        
        for (EventHandler handler : handlers) {
            try {
                EventResult result = handler.handle(event);
                if (!result.isSuccess()) {
                    log.error("Handler failed for event: {} - {}", event.getEventId(), result.getErrorMessage());
                    finalResult = result;
                    break;
                }
            } catch (Exception e) {
                log.error("Handler exception for event: {}", event.getEventId(), e);
                finalResult = EventResult.failure(event.getEventId(), event.getTransactionId(), "HANDLER_ERROR", e.getMessage());
                break;
            }
        }
        
        return finalResult;
    }
    
    /**
     * 更新事件类型计数器
     */
    private void updateEventTypeCounter(String eventType) {
        eventTypeCounters.computeIfAbsent(eventType, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * 生成消费者实例ID
     */
    private String generateConsumerInstanceId() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return String.format("%s-%s-%s-%d", applicationName, hostname, serverPort, System.currentTimeMillis());
        } catch (Exception e) {
            return String.format("%s-unknown-%d", applicationName, System.currentTimeMillis());
        }
    }
    
    /**
     * 注册消费者状态
     */
    private void registerConsumerStatus() {
        String statusKey = CONSUMER_STATUS_KEY_PREFIX + consumerInstanceId;
        ConsumerStatusInfo statusInfo = ConsumerStatusInfo.builder()
                .instanceId(consumerInstanceId)
                .applicationName(applicationName)
                .status(ConsumerStatus.INITIALIZED)
                .startTime(LocalDateTime.now())
                .lastHeartbeat(LocalDateTime.now())
                .build();
        
        cacheService.setObject(statusKey, statusInfo, 300); // 5分钟过期
    }
    
    /**
     * 更新消费者状态（公开方法供ConsumerManager调用）
     */
    public void updateConsumerStatus(ConsumerStatus status) {
        String statusKey = CONSUMER_STATUS_KEY_PREFIX + consumerInstanceId;
        ConsumerStatusInfo statusInfo = ConsumerStatusInfo.builder()
                .instanceId(consumerInstanceId)
                .applicationName(applicationName)
                .status(status)
                .startTime(LocalDateTime.now())
                .lastHeartbeat(LocalDateTime.now())
                .build();
        
        cacheService.setObject(statusKey, statusInfo, 300); // 5分钟过期
    }
    
    @PreDestroy
    public void destroy() {
        stop();
        // 清理消费者状态
        String statusKey = CONSUMER_STATUS_KEY_PREFIX + consumerInstanceId;
        cacheService.delete(statusKey);
    }
    
    /**
     * 消费者状态信息
     */
    @lombok.Data
    @lombok.Builder
    public static class ConsumerStatusInfo {
        private String instanceId;
        private String applicationName;
        private ConsumerStatus status;
        private LocalDateTime startTime;
        private LocalDateTime lastHeartbeat;
    }
} 