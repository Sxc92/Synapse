package com.indigo.events.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 事件框架统一配置属性
 * 整合事件、RocketMQ和可靠消费者的所有配置
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Data
@ConfigurationProperties(prefix = "synapse.events")
public class EventsProperties {
    
    /**
     * 是否启用事件框架
     */
    private boolean enabled = true;
    
    /**
     * 是否自动生成 transactionId
     */
    private boolean autoGenerateTransactionId = true;
    
    /**
     * 消息去重配置
     */
    private DuplicateCheck duplicateCheck = new DuplicateCheck();
    
    /**
     * RocketMQ 配置
     */
    private RocketMQ rocketmq = new RocketMQ();
    
    /**
     * 可靠消费者配置
     */
    private ReliableConsumer reliable = new ReliableConsumer();
    
    /**
     * 监控配置
     */
    private Monitoring monitoring = new Monitoring();
    
    @Data
    public static class DuplicateCheck {
        
        /**
         * 是否启用消息去重
         */
        private boolean enabled = true;
        
        /**
         * 是否使用 Redis 进行去重检查
         */
        private boolean useRedis = true;
        
        /**
         * 本地缓存大小
         */
        private int localCacheSize = 1000;
        
        /**
         * 去重过期时间（分钟）
         */
        private int expireMinutes = 5;
        
        /**
         * Redis 键前缀
         */
        private String redisKeyPrefix = "synapse:events:duplicate:";
    }
    
    @Data
    public static class RocketMQ {
        
        /**
         * 名称服务器地址
         */
        private String nameServer = "localhost:9876";
        
        /**
         * 生产者组名
         */
        private String producerGroup = "synapse-events-producer";
        
        /**
         * 消费者组名
         */
        private String consumerGroup = "synapse-events-consumer";
        
        /**
         * 主题前缀
         */
        private String topicPrefix = "synapse-events";
        
        /**
         * 发送超时时间（毫秒）
         */
        private int sendTimeout = 3000;
        
        /**
         * 消费者拉取超时时间（毫秒）
         */
        private int pullTimeout = 3000;
        
        /**
         * 消费者批量拉取消息数量
         */
        private int pullBatchSize = 32;
        
        /**
         * 消费者线程池大小
         */
        private int consumerThreadPoolSize = 20;
        
        /**
         * 是否启用消息重试
         */
        private boolean enableRetry = true;
        
        /**
         * 最大重试次数
         */
        private int maxRetryTimes = 3;
        
        /**
         * 重试间隔（毫秒）
         */
        private int retryInterval = 1000;
        
        /**
         * 是否启用消息压缩
         */
        private boolean enableCompression = true;
        
        /**
         * 压缩级别 (1-9)
         */
        private int compressionLevel = 5;
    }
    
    @Data
    public static class ReliableConsumer {
        
        /**
         * 心跳间隔（秒）
         */
        private int heartbeatInterval = 30;
        
        /**
         * 故障超时时间（秒）
         */
        private int failureTimeout = 90;
        
        /**
         * 去重缓存过期时间（秒）
         */
        private int dedupExpireSeconds = 3600;
        
        /**
         * 分布式锁超时时间（秒）
         */
        private int lockTimeout = 30;
        
        /**
         * 批量处理大小
         */
        private int batchSize = 10;
        
        /**
         * 并发处理线程数
         */
        private int concurrency = 4;
        
        /**
         * 重试间隔（毫秒）
         */
        private long retryInterval = 1000;
        
        /**
         * 最大重试次数
         */
        private int maxRetryCount = 3;
        
        /**
         * 监控阈值 - 错误率告警（百分比）
         */
        private double errorRateThreshold = 5.0;
        
        /**
         * 监控阈值 - 处理延迟告警（毫秒）
         */
        private long latencyThreshold = 5000;
        
        /**
         * 监控阈值 - 队列积压告警
         */
        private int queueBacklogThreshold = 1000;
    }
    
    @Data
    public static class Monitoring {
        
        /**
         * 是否启用监控
         */
        private boolean enabled = true;
        
        /**
         * 监控数据收集间隔（秒）
         */
        private int metricsInterval = 60;
        
        /**
         * 是否启用详细日志
         */
        private boolean detailedLogging = false;
        
        /**
         * 监控数据保留时间（小时）
         */
        private int dataRetentionHours = 24;
        
        /**
         * 告警配置
         */
        private Alert alert = new Alert();
        
        @Data
        public static class Alert {
            
            /**
             * 是否启用告警
             */
            private boolean enabled = true;
            
            /**
             * 告警通知方式
             */
            private String notificationType = "log"; // log, email, webhook
            
            /**
             * 告警阈值配置
             */
            private Thresholds thresholds = new Thresholds();
            
            @Data
            public static class Thresholds {
                
                /**
                 * 错误率阈值（百分比）
                 */
                private double errorRate = 5.0;
                
                /**
                 * 延迟阈值（毫秒）
                 */
                private long latency = 5000;
                
                /**
                 * 队列积压阈值
                 */
                private int queueBacklog = 1000;
            }
        }
    }
} 