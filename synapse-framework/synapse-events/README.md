# Synapse Events 事件驱动框架

## 📖 概述

Synapse Events 是一个基于 RocketMQ 的轻量级事件驱动框架，为 SynapseMOM 平台提供分布式事件处理能力。该框架支持事件发布、订阅、事务管理和自动回滚机制。

经过深度清理和重构，该模块现在专注于核心的事件处理功能，为独立的监控服务和回滚服务提供了理想的架构基础。

## ✨ 特性

- 🚀 **轻量级设计**: 基于 RocketMQ Client，避免重量级 Spring Cloud Stream
- 🔄 **异步处理**: 完全异步的事件发布和处理
- 🛡️ **事务支持**: 分布式事务状态跟踪和管理
- 🔄 **自动回滚**: 智能回滚机制，支持补偿操作
- 📊 **优先级管理**: 支持事件优先级，确保重要事件优先处理
- 🔧 **灵活配置**: 支持多种配置选项和自定义扩展
- 🎯 **职责单一**: 专注于事件处理，监控和回滚功能独立服务
- 🏗️ **模块化设计**: 清晰的职责分离，易于维护和扩展

## 🏗️ 架构设计

### 模块职责

```
synapse-events/
├── 核心功能：事件发布和消费
├── 配置管理：统一的 EventsProperties
├── 基础统计：ConsumerStats（仅基础统计）
├── 消费者管理：ConsumerManager（仅生命周期管理）
├── 可靠消费：ReliableRocketMQEventConsumer
└── 统一发布：UnifiedRocketMQEventPublisher
```

### 服务拆分策略

- **synapse-events**: 专注于事件处理核心功能
- **event-monitor-service**: 负责监控、健康检查、告警
- **rollback-coordinator-service**: 负责回滚协调和补偿

## 🚀 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-events</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 RocketMQ

在 `application.yml` 中配置：

```yaml
synapse:
  events:
    enabled: true
    rocketmq:
      name-server: localhost:9876
      producer-group: synapse-producer
      consumer-group: synapse-consumer
      topic-prefix: synapse-events
      send-timeout: 3000
    reliable:
      error-rate-threshold: 5.0
      latency-threshold: 1000
      queue-backlog-threshold: 1000
    duplicate-check:
      enabled: true
      expire-seconds: 300
```

### 3. 发布事件

```java
@Service
public class UserService {
    
    @Autowired
    private UnifiedRocketMQEventPublisher eventPublisher;
    
    public void createUser(User user) {
        // 业务逻辑
        userRepository.save(user);
        
        // 发布用户创建事件
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", user.getId());
        eventData.put("username", user.getUsername());
        
        // 自动生成 transactionId，使用 Redis 去重
        PublishResult result = eventPublisher.publish("user.created", "user-service", eventData);
        if (result.isSuccess()) {
            log.info("事件发布成功，transactionId: {}", result.getTransactionId());
        }
    }
}
```

### 4. 处理事件

```java
@Component
public class UserEventHandler implements EventHandler {
    
    @Override
    public EventResult handle(Event event) {
        String userId = (String) event.getEventData().get("userId");
        String username = (String) event.getEventData().get("username");
        
        // 处理用户创建事件
        log.info("处理用户创建事件: {} - {}", userId, username);
        
        return EventResult.success(event.getEventId(), event.getTransactionId());
    }
    
    @Override
    public String getEventType() {
        return "user.created";
    }
}
```

### 5. 事务跟踪

```java
@Service
public class OrderService {
    
    @Autowired
    private TransactionTracker transactionTracker;
    
    public void createOrder(Order order) {
        String transactionId = EventUtils.generateTransactionId();
        
        // 记录事务开始
        transactionTracker.recordTransactionStart(transactionId, "order.create", 300);
        
        // 执行业务逻辑...
        
        // 记录事务完成
        transactionTracker.recordTransactionComplete(transactionId);
    }
}
```

## 📋 核心概念

### 事件 (Event)

事件是系统中发生的重要业务动作的表示：

```java
Event event = Event.create(
    transactionId,    // 事务ID
    eventType,        // 事件类型
    sourceService,    // 源服务
    eventData         // 事件数据
);
```

### 事件优先级

支持 5 个优先级级别：

- `SYSTEM`: 系统级事件，最高优先级
- `URGENT`: 紧急事件，立即处理
- `HIGH`: 高优先级事件
- `NORMAL`: 普通优先级事件
- `LOW`: 低优先级事件，可延迟处理

```java
// 创建高优先级事件
Event urgentEvent = Event.create(transactionId, eventType, sourceService, eventData)
    .withPriority(EventPriority.URGENT);

// 创建系统事件
Event systemEvent = Event.create(transactionId, eventType, sourceService, eventData)
    .withPriority(EventPriority.SYSTEM);
```

### 事务状态管理

框架提供分布式事务状态跟踪：

```java
// 创建事务状态
TransactionStatus status = TransactionStatus.create(
    transactionId, 
    businessType, 
    timeoutSeconds
);

// 添加事件到事务
status.addEvent(eventId);

// 事件处理成功
status.eventSuccess();

// 事件处理失败
status.eventFailed();
```

### 事件发布结果

```java
PublishResult result = eventPublisher.publish(event);

if (result.isSuccess()) {
    log.info("Event published: {}", result.getMessageId());
} else {
    log.error("Failed to publish event: {} - {}", 
              result.getErrorCode(), result.getErrorMessage());
}
```

## 🔧 可靠发布器

### 解决消息丢失和重复问题

框架提供了 `UnifiedRocketMQEventPublisher` 来解决分布式消息系统的核心问题：

#### 1. 消息丢失防护
- **同步发送**: 确保消息发送到 MQ 后才返回结果
- **重试机制**: 自动重试失败的发送操作
- **超时控制**: 设置合理的发送超时时间

#### 2. 消息重复防护
- **消息去重**: 基于消息内容生成去重键，5分钟内相同消息自动跳过
- **幂等性**: 支持幂等操作，重复消息不会影响业务逻辑
- **缓存管理**: 自动清理过期的去重记录

#### 3. 自动生成 transactionId

```java
@Service
public class UserService {
    
    @Autowired
    private UnifiedRocketMQEventPublisher eventPublisher;
    
    public void createUser(User user) {
        // 业务逻辑
        userRepository.save(user);
        
        // 发布事件（自动生成 transactionId）
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", user.getId());
        eventData.put("username", user.getUsername());
        
        PublishResult result = eventPublisher.publish("user.created", "user-service", eventData);
        log.info("Event published with transactionId: {}", result.getTransactionId());
    }
}
```

## 📊 监控和统计

### 消费者统计

```java
@Component
public class MonitoringService {
    
    @Autowired
    private EventConsumer eventConsumer;
    
    public void logConsumerStats() {
        ConsumerStats stats = eventConsumer.getStats();
        
        log.info("Consumer Statistics:");
        log.info("  - Total Consumed: {}", stats.getTotalConsumed());
        log.info("  - Success Rate: {}%", stats.getSuccessRate() * 100);
        log.info("  - Average Process Time: {}ms", stats.getAverageProcessTime());
        log.info("  - Process Rate: {}/s", stats.getProcessRate());
    }
}
```

### 集群状态

```java
@Component
public class ClusterMonitoringService {
    
    @Autowired
    private ConsumerManager consumerManager;
    
    public void logClusterStatus() {
        long activeCount = consumerManager.getActiveConsumerCount();
        Map<String, Object> clusterStats = consumerManager.getClusterStats();
        
        log.info("Cluster Status:");
        log.info("  - Active Consumers: {}", activeCount);
        log.info("  - Total Consumers: {}", clusterStats.get("totalConsumers"));
    }
}
```

## 🧹 清理总结

### 清理目标

根据微服务架构的最佳实践，对 `synapse-events` 模块进行深度清理，消除重复和冗余代码，为后续创建独立的监控服务和回滚服务做准备。

### 已完成的清理工作

#### 1. **删除冗余的消费者实现**
- ❌ 删除了 `RocketMQEventConsumer.java`（基础消费者）
- ✅ 保留了 `ReliableRocketMQEventConsumer.java`（可靠消费者）

#### 2. **合并配置类**
- ❌ 删除了 `RocketMQProperties.java`
- ❌ 删除了 `ReliableConsumerProperties.java`
- ✅ 合并到统一的 `EventsProperties.java`

#### 3. **删除重复的测试示例**
- ❌ 删除了 `UnifiedPublisherExample.java`
- ✅ 保留了 `ReliableConsumerExample.java`

#### 4. **简化监控相关功能**
- ✅ 简化了 `ConsumerStats`，移除队列监控功能
- ✅ 简化了 `ConsumerManager`，移除健康检查和故障检测
- ❌ 删除了空的 `rollback` 目录

### 清理成果

- **删除文件：** 5 个
- **修改文件：** 7 个
- **删除代码行：** ~1000 行
- **净减少：** ~800 行
- **编译状态：** ✅ 成功
- **单元测试：** ✅ 17个测试全部通过

### 架构优势

1. **职责单一** - 每个组件都有明确的职责边界
2. **易于维护** - 代码更简洁，逻辑更清晰
3. **易于扩展** - 为独立的监控服务和回滚服务做好准备
4. **松耦合** - 服务间通过事件和缓存进行通信

## 🔮 后续计划

### 1. **监控服务设计**
- 创建独立的 `event-monitor-service`
- 负责事件数据收集、监控面板、告警
- 实现健康检查、故障检测、队列监控
- 与 `synapse-events` 解耦

### 2. **回滚服务设计**
- 创建独立的 `rollback-coordinator-service`
- 负责分布式回滚协调、补偿机制
- 与 `synapse-events` 解耦

### 3. **服务间通信**
- 使用 Redis 进行状态共享
- 使用 RocketMQ 进行事件通信
- 保持服务间的松耦合

## 📝 使用示例

### 完整的使用示例

```java
@SpringBootApplication
public class EventApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class, args);
    }
}

@Service
public class OrderService {
    
    @Autowired
    private UnifiedRocketMQEventPublisher eventPublisher;
    
    @Autowired
    private TransactionTracker transactionTracker;
    
    public void createOrder(Order order) {
        String transactionId = EventUtils.generateTransactionId();
        
        // 记录事务开始
        transactionTracker.recordTransactionStart(transactionId, "order.create", 300);
        
        try {
            // 业务逻辑
            orderRepository.save(order);
            
            // 发布订单创建事件
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", order.getId());
            eventData.put("amount", order.getAmount());
            eventData.put("userId", order.getUserId());
            
            PublishResult result = eventPublisher.publish("order.created", "order-service", eventData);
            
            if (result.isSuccess()) {
                log.info("Order created event published: {}", result.getTransactionId());
                transactionTracker.recordTransactionComplete(transactionId);
            } else {
                log.error("Failed to publish order event: {}", result.getErrorMessage());
                transactionTracker.recordTransactionFailed(transactionId, result.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("Error creating order", e);
            transactionTracker.recordTransactionFailed(transactionId, e.getMessage());
            throw e;
        }
    }
}

@Component
public class OrderEventHandler implements EventHandler {
    
    @Override
    public EventResult handle(Event event) {
        String orderId = (String) event.getEventData().get("orderId");
        Double amount = (Double) event.getEventData().get("amount");
        String userId = (String) event.getEventData().get("userId");
        
        log.info("Processing order created event: {} - {} - {}", orderId, amount, userId);
        
        // 处理订单创建事件
        // 例如：发送通知、更新库存等
        
        return EventResult.success(event.getEventId(), event.getTransactionId());
    }
    
    @Override
    public String getEventType() {
        return "order.created";
    }
}
```

## 📚 相关文档

- [清理总结](./CLEANUP_SUMMARY.md) - 详细的清理过程和成果
- [当前状态](./CURRENT_STATUS.md) - 模块的当前完成度
- [统一发布器解决方案](./UNIFIED_PUBLISHER_FINAL_SOLUTION.md) - 发布器的技术实现

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个框架。

## 📄 许可证

本项目采用 MIT 许可证。

---

**最后更新：** 2025-07-20  
**版本：** 1.0.0  
**维护者：** 史偕成 