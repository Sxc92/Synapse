package com.indigo.cache.extension;

import com.indigo.cache.infrastructure.RedisService;
import com.indigo.cache.manager.CacheKeyGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 分布式限流服务
 * 支持滑动窗口、令牌桶、固定窗口等多种限流算法
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
public class RateLimitService {

    private final RedisService redisService;
    private final CacheKeyGenerator keyGenerator;

    public RateLimitService(RedisService redisService, CacheKeyGenerator keyGenerator) {
        this.redisService = redisService;
        this.keyGenerator = keyGenerator;
    }

    /**
     * 滑动窗口限流
     *
     * @param key          限流键
     * @param timeWindow   时间窗口（秒）
     * @param maxRequests  最大请求数
     * @return 是否允许请求
     */
    public boolean slidingWindowLimit(String key, long timeWindow, long maxRequests) {
        String rateLimitKey = keyGenerator.generate(CacheKeyGenerator.Module.RATE_LIMIT, "sliding", key);
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - (timeWindow * 1000);

        // 使用 Redis 的 ZREMRANGEBYSCORE 和 ZCARD 实现滑动窗口
        String script = """
            local key = KEYS[1]
            local current_time = tonumber(ARGV[1])
            local window_start = tonumber(ARGV[2])
            local max_requests = tonumber(ARGV[3])
            
            -- 移除窗口外的请求记录
            redis.call('ZREMRANGEBYSCORE', key, 0, window_start)
            
            -- 获取当前窗口内的请求数
            local current_requests = redis.call('ZCARD', key)
            
            if current_requests < max_requests then
                -- 添加当前请求记录
                redis.call('ZADD', key, current_time, current_time .. ':' .. math.random())
                redis.call('EXPIRE', key, 60)
                return 1
            else
                return 0
            end
            """;

        try {
            Long result = redisService.executeScript(script, rateLimitKey, 
                String.valueOf(currentTime), String.valueOf(windowStart), String.valueOf(maxRequests));
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("滑动窗口限流执行失败: {}", key, e);
            return true; // 限流失败时默认放行
        }
    }

    /**
     * 令牌桶限流
     *
     * @param key          限流键
     * @param capacity     桶容量
     * @param rate         令牌产生速率（个/秒）
     * @return 是否允许请求
     */
    public boolean tokenBucketLimit(String key, long capacity, double rate) {
        String rateLimitKey = keyGenerator.generate(CacheKeyGenerator.Module.RATE_LIMIT, "token", key);
        long currentTime = System.currentTimeMillis();

        String script = """
            local key = KEYS[1]
            local current_time = tonumber(ARGV[1])
            local capacity = tonumber(ARGV[2])
            local rate = tonumber(ARGV[3])
            
            -- 获取当前令牌数和上次更新时间
            local bucket_info = redis.call('HMGET', key, 'tokens', 'last_update')
            local current_tokens = tonumber(bucket_info[1]) or capacity
            local last_update = tonumber(bucket_info[2]) or current_time
            
            -- 计算需要补充的令牌数
            local time_passed = (current_time - last_update) / 1000
            local tokens_to_add = math.floor(time_passed * rate)
            current_tokens = math.min(capacity, current_tokens + tokens_to_add)
            
            if current_tokens >= 1 then
                -- 消耗一个令牌
                current_tokens = current_tokens - 1
                redis.call('HMSET', key, 'tokens', current_tokens, 'last_update', current_time)
                redis.call('EXPIRE', key, 60)
                return 1
            else
                return 0
            end
            """;

        try {
            Long result = redisService.executeScript(script, rateLimitKey,
                String.valueOf(currentTime), String.valueOf(capacity), String.valueOf(rate));
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("令牌桶限流执行失败: {}", key, e);
            return true; // 限流失败时默认放行
        }
    }

    /**
     * 固定窗口限流
     *
     * @param key          限流键
     * @param timeWindow   时间窗口（秒）
     * @param maxRequests  最大请求数
     * @return 是否允许请求
     */
    public boolean fixedWindowLimit(String key, long timeWindow, long maxRequests) {
        String rateLimitKey = keyGenerator.generate(CacheKeyGenerator.Module.RATE_LIMIT, "fixed", key);
        long windowStart = System.currentTimeMillis() / (timeWindow * 1000) * (timeWindow * 1000);

        String script = """
            local key = KEYS[1]
            local window_start = tonumber(ARGV[1])
            local max_requests = tonumber(ARGV[2])
            
            -- 获取当前窗口的请求数
            local current_requests = redis.call('GET', key)
            if not current_requests then
                current_requests = 0
            else
                current_requests = tonumber(current_requests)
            end
            
            if current_requests < max_requests then
                -- 增加请求计数
                redis.call('INCR', key)
                redis.call('EXPIRE', key, 60)
                return 1
            else
                return 0
            end
            """;

        try {
            Long result = redisService.executeScript(script, rateLimitKey,
                String.valueOf(windowStart), String.valueOf(maxRequests));
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("固定窗口限流执行失败: {}", key, e);
            return true; // 限流失败时默认放行
        }
    }

    /**
     * 通用限流方法
     *
     * @param key          限流键
     * @param algorithm    限流算法
     * @param timeWindow   时间窗口（秒）
     * @param maxRequests  最大请求数
     * @return 是否允许请求
     */
    public boolean isAllowed(String key, String algorithm, long timeWindow, long maxRequests) {
        switch (algorithm.toUpperCase()) {
            case "SLIDING_WINDOW":
                return slidingWindowLimit(key, timeWindow, maxRequests);
            case "TOKEN_BUCKET":
                return tokenBucketLimit(key, maxRequests, (double) maxRequests / timeWindow);
            case "FIXED_WINDOW":
                return fixedWindowLimit(key, timeWindow, maxRequests);
            default:
                return slidingWindowLimit(key, timeWindow, maxRequests);
        }
    }

    /**
     * 获取限流信息
     *
     * @param key          限流键
     * @param algorithm    限流算法
     * @return 限流信息
     */
    public RateLimitInfo getRateLimitInfo(String key, String algorithm) {
        String rateLimitKey = keyGenerator.generate(CacheKeyGenerator.Module.RATE_LIMIT, 
            algorithm.toLowerCase(), key);
        
        // 这里可以根据不同算法获取具体的限流信息
        // 比如当前请求数、剩余令牌数等
        return new RateLimitInfo(key, algorithm, rateLimitKey);
    }

    /**
     * 重置限流计数器
     *
     * @param key          限流键
     * @param algorithm    限流算法
     */
    public void reset(String key, String algorithm) {
        String rateLimitKey = keyGenerator.generate(CacheKeyGenerator.Module.RATE_LIMIT, 
            algorithm.toLowerCase(), key);
        redisService.delete(rateLimitKey);
        log.debug("重置限流计数器: {}", rateLimitKey);
    }

    /**
     * 限流信息
     */
    public static class RateLimitInfo {
        private final String key;
        private final String algorithm;
        private final String redisKey;

        public RateLimitInfo(String key, String algorithm, String redisKey) {
            this.key = key;
            this.algorithm = algorithm;
            this.redisKey = redisKey;
        }

        // Getters
        public String getKey() { return key; }
        public String getAlgorithm() { return algorithm; }
        public String getRedisKey() { return redisKey; }
    }
} 