package com.indigo.events.config;

import com.indigo.cache.core.CacheService;
import com.indigo.cache.extension.DistributedLockService;
import com.indigo.core.utils.JsonUtils;
import com.indigo.events.core.EventConsumer;
import com.indigo.events.core.EventPublisher;
import com.indigo.events.core.EventHandlerRegistry;
import com.indigo.events.core.impl.ReliableRocketMQEventConsumer;
import com.indigo.events.core.impl.UnifiedRocketMQEventPublisher;
import com.indigo.events.utils.MessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 事件框架自动配置
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(EventsProperties.class)
@ConditionalOnProperty(prefix = "synapse.events", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EventsAutoConfiguration {
    
    @Bean
    public MessageSerializer messageSerializer(JsonUtils jsonUtils) {
        return new MessageSerializer(jsonUtils);
    }
    
    @Bean
    public EventHandlerRegistry eventHandlerRegistry() {
        return new EventHandlerRegistry();
    }
    
    @Bean
    public EventPublisher eventPublisher(EventsProperties properties,
                                        MessageSerializer messageSerializer,
                                        CacheService cacheService) {
        log.info("Initializing UnifiedRocketMQEventPublisher with properties: {}", properties);
        return new UnifiedRocketMQEventPublisher(properties, messageSerializer, cacheService);
    }
    
    @Bean
    public EventConsumer eventConsumer(EventsProperties properties,
                                      MessageSerializer messageSerializer,
                                      EventHandlerRegistry eventHandlerRegistry,
                                      CacheService cacheService,
                                      DistributedLockService lockService) {
        log.info("Initializing ReliableRocketMQEventConsumer with properties: {}", properties);
        return new ReliableRocketMQEventConsumer(properties, messageSerializer, eventHandlerRegistry, cacheService, lockService);
    }
} 