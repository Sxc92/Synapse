package com.indigo.events.utils;

import com.indigo.core.utils.JsonUtils;
import com.indigo.events.core.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 消息序列化工具类
 * 使用 synapse-core 的 JsonUtils 进行序列化
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@Component
public class MessageSerializer {
    
    private final JsonUtils jsonUtils;
    
    @Autowired
    public MessageSerializer(JsonUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }
    
    /**
     * 将事件序列化为 RocketMQ 消息
     *
     * @param event 事件对象
     * @param topic 消息主题
     * @return RocketMQ 消息
     */
    public Message serializeEvent(Event event, String topic) {
        try {
            // 序列化事件为 JSON
            String eventJson = jsonUtils.toJson(event);
            
            // 创建 RocketMQ 消息
            Message message = new Message(topic, eventJson.getBytes(StandardCharsets.UTF_8));
            
            // 设置消息属性
            message.putUserProperty("eventId", event.getEventId());
            message.putUserProperty("transactionId", event.getTransactionId());
            message.putUserProperty("eventType", event.getEventType());
            message.putUserProperty("sourceService", event.getSourceService());
            message.putUserProperty("priority", event.getPriority().name());
            message.putUserProperty("version", event.getVersion());
            
            // 设置消息标签（用于消息过滤）
            message.setTags(event.getEventType());
            
            // 设置消息键（用于消息去重）
            message.setKeys(event.getEventId());
            
            log.debug("Serialized event to message: eventId={}, topic={}", event.getEventId(), topic);
            return message;
            
        } catch (Exception e) {
            log.error("Failed to serialize event: eventId={}", event.getEventId(), e);
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
    
    /**
     * 将 RocketMQ 消息反序列化为事件
     *
     * @param message RocketMQ 消息
     * @return 事件对象
     */
    public Event deserializeEvent(Message message) {
        try {
            // 获取消息体
            String eventJson = new String(message.getBody(), StandardCharsets.UTF_8);
            
            // 反序列化为事件对象
            Event event = jsonUtils.fromJsonToObject(eventJson, Event.class);
            
            log.debug("Deserialized message to event: eventId={}, topic={}", 
                     event.getEventId(), message.getTopic());
            return event;
            
        } catch (Exception e) {
            log.error("Failed to deserialize message: topic={}", 
                     message.getTopic(), e);
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
    
    /**
     * 将 Map 数据序列化为 RocketMQ 消息
     *
     * @param data 数据 Map
     * @param topic 消息主题
     * @param tags 消息标签
     * @param keys 消息键
     * @return RocketMQ 消息
     */
    public Message serializeMap(Map<String, Object> data, String topic, String tags, String keys) {
        try {
            // 序列化为 JSON
            String json = jsonUtils.toJson(data);
            
            // 创建 RocketMQ 消息
            Message message = new Message(topic, json.getBytes(StandardCharsets.UTF_8));
            
            // 设置消息标签和键
            if (tags != null) {
                message.setTags(tags);
            }
            if (keys != null) {
                message.setKeys(keys);
            }
            
            log.debug("Serialized map to message: topic={}, tags={}, keys={}", topic, tags, keys);
            return message;
            
        } catch (Exception e) {
            log.error("Failed to serialize map: topic={}", topic, e);
            throw new RuntimeException("Failed to serialize map", e);
        }
    }
    
    /**
     * 将 RocketMQ 消息反序列化为 Map
     *
     * @param message RocketMQ 消息
     * @return Map 对象
     */
    public Map<String, Object> deserializeMap(Message message) {
        try {
            // 获取消息体
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            
            // 反序列化为 Map
            Map<String, Object> data = jsonUtils.fromJsonToObject(json, Map.class);
            
            log.debug("Deserialized message to map: topic={}", 
                     message.getTopic());
            return data;
            
        } catch (Exception e) {
            log.error("Failed to deserialize message to map: topic={}", 
                     message.getTopic(), e);
            throw new RuntimeException("Failed to deserialize message to map", e);
        }
    }
    
    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param obj 要序列化的对象
     * @return JSON 字符串
     */
    public String serializeObject(Object obj) {
        return jsonUtils.toJson(obj);
    }
    
    /**
     * 将 JSON 字符串反序列化为对象
     *
     * @param json JSON 字符串
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 反序列化的对象
     */
    public <T> T deserializeObject(String json, Class<T> clazz) {
        return jsonUtils.fromJsonToObject(json, clazz);
    }
    
    /**
     * 检查 JSON 字符串是否有效
     *
     * @param json JSON 字符串
     * @return 是否有效
     */
    public boolean isValidJson(String json) {
        return jsonUtils.isValidJson(json);
    }
} 