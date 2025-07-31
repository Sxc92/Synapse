package com.indigo.events.core;

import com.indigo.cache.core.CacheService;
import com.indigo.events.core.impl.ReliableRocketMQEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.Set;

/**
 * 消费者管理器
 * 处理消费者生命周期管理和基础集群协调
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@Component
public class ConsumerManager {
    
    private final CacheService cacheService;
    private final EventConsumer eventConsumer;
    
    @Value("${spring.application.name:unknown-service}")
    private String applicationName;
    
    private final Map<String, ReliableRocketMQEventConsumer.ConsumerStatusInfo> clusterConsumers = new ConcurrentHashMap<>();
    
    @Autowired
    public ConsumerManager(CacheService cacheService, EventConsumer eventConsumer) {
        this.cacheService = cacheService;
        this.eventConsumer = eventConsumer;
    }
    
    /**
     * 更新本地消费者状态
     */
    public void updateLocalConsumerStatus() {
        if (eventConsumer instanceof ReliableRocketMQEventConsumer reliableConsumer) {
            reliableConsumer.updateConsumerStatus(eventConsumer.getStatus());
        }
    }
    
    /**
     * 扫描集群中的其他消费者
     */
    public void scanClusterConsumers() {
        try {
            // 获取所有消费者状态键
            String pattern = "consumer:status:*";
            // 使用RedisService的scan方法
            Set<String> statusKeys = cacheService.getRedisService().scan(pattern);
            
            clusterConsumers.clear();
            
            for (String statusKey : statusKeys) {
                ReliableRocketMQEventConsumer.ConsumerStatusInfo statusInfo = 
                    cacheService.getObject(statusKey, ReliableRocketMQEventConsumer.ConsumerStatusInfo.class);
                
                if (statusInfo != null) {
                    clusterConsumers.put(statusInfo.getInstanceId(), statusInfo);
                }
            }
            
        } catch (Exception e) {
            log.error("Error scanning cluster consumers", e);
        }
    }
    
    /**
     * 获取集群消费者列表
     */
    public List<ReliableRocketMQEventConsumer.ConsumerStatusInfo> getClusterConsumers() {
        return List.copyOf(clusterConsumers.values());
    }
    
    /**
     * 获取活跃消费者数量
     */
    public long getActiveConsumerCount() {
        return clusterConsumers.values().stream()
                .filter(consumer -> consumer.getStatus() == ConsumerStatus.RUNNING)
                .count();
    }
    
    /**
     * 获取集群统计信息
     */
    public Map<String, Object> getClusterStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalConsumers", clusterConsumers.size());
        stats.put("activeConsumers", getActiveConsumerCount());
        stats.put("applicationName", applicationName);
        return stats;
    }
    
    /**
     * 输出集群状态
     */
    public void logClusterStatus() {
        if (log.isDebugEnabled()) {
            log.debug("Cluster status - Total consumers: {}, Application: {}", 
                    clusterConsumers.size(), applicationName);
            
            clusterConsumers.values().forEach(consumer -> 
                log.debug("Consumer: {} - Status: {} - Last heartbeat: {}", 
                        consumer.getInstanceId(), consumer.getStatus(), consumer.getLastHeartbeat()));
        }
    }
} 