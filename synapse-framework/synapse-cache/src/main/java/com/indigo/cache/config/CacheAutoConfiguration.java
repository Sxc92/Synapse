package com.indigo.cache.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigo.cache.aspect.CacheAspect;
import com.indigo.cache.manager.CacheKeyGenerator;
import com.indigo.cache.core.CacheService;
import com.indigo.cache.infrastructure.CaffeineCacheManager;
import com.indigo.cache.infrastructure.RedisService;
import com.indigo.cache.core.TwoLevelCacheService;
import com.indigo.cache.extension.DistributedLockService;
import com.indigo.cache.extension.RateLimitService;
import com.indigo.cache.session.UserSessionService;
import com.indigo.cache.session.UserSessionServiceFactory;
import com.indigo.core.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 缓存自动配置类，用于自动注册缓存服务
 * 基于 Spring Boot 的 Redis 自动配置进行扩展
 * 
 * 注意：该配置类在RedisAutoConfiguration之后运行，确保RedisTemplate等依赖已经可用
 *
 * @author 史偕成
 * @date 2025/05/16 10:30
 */
@Slf4j
@AutoConfiguration(after = RedisAutoConfiguration.class)
@Import({RedisConfiguration.class})
@ComponentScan(basePackages = {"com.indigo.cache.aspect", "com.indigo.core"})
public class CacheAutoConfiguration {

    /**
     * 注册缓存键生成器
     */
    @Bean("synapseCacheKeyGenerator")
    @ConditionalOnMissingBean
    public CacheKeyGenerator synapseCacheKeyGenerator() {
        return new CacheKeyGenerator();
    }

    /**
     * 注册Caffeine缓存管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public CaffeineCacheManager caffeineCacheManager() {
        return new CaffeineCacheManager();
    }

    /**
     * 注册Redis缓存服务
     * 使用@Qualifier指定具体的RedisTemplate bean
     */
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("unchecked")
    public RedisService redisService(@Qualifier("redisTemplate") @SuppressWarnings("rawtypes") RedisTemplate redisTemplate,
                                   @Qualifier("stringRedisTemplate") StringRedisTemplate stringRedisTemplate,
                                   @Qualifier("synapseObjectMapper") ObjectMapper objectMapper) {
        log.info("创建RedisService Bean，RedisTemplate: {}, StringRedisTemplate: {}", 
                 redisTemplate != null ? redisTemplate.getClass().getSimpleName() : "null",
                 stringRedisTemplate != null ? stringRedisTemplate.getClass().getSimpleName() : "null");
        
        // 类型转换为我们需要的泛型类型
        RedisTemplate<String, Object> typedRedisTemplate = (RedisTemplate<String, Object>) redisTemplate;
        return new RedisService(typedRedisTemplate, stringRedisTemplate, objectMapper);
    }

    /**
     * 注册缓存服务
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheService cacheService(RedisService redisService) {
        return new CacheService(redisService);
    }

    /**
     * 注册二级缓存服务
     */
    @Bean
    @ConditionalOnMissingBean
    public TwoLevelCacheService twoLevelCacheService(
            CaffeineCacheManager caffeineCacheManager,
            CacheService cacheService,
            @Qualifier("synapseCacheKeyGenerator") CacheKeyGenerator cacheKeyGenerator) {
        return new TwoLevelCacheService(caffeineCacheManager, cacheService, cacheKeyGenerator);
    }

    /**
     * 注册缓存切面
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheAspect cacheAspect(TwoLevelCacheService twoLevelCacheService) {
        return new CacheAspect(twoLevelCacheService);
    }

    /**
     * 注册用户会话服务
     * 使用工厂模式创建，完全依赖接口
     */
    @Bean
    @ConditionalOnMissingBean
    public UserSessionService userSessionService(
            CacheService cacheService,
            @Qualifier("synapseCacheKeyGenerator") CacheKeyGenerator cacheKeyGenerator,
            RedisService redisService) {
        log.info("创建UserSessionService Bean - 使用新的session包架构");
        return UserSessionServiceFactory.createUserSessionService(cacheService, cacheKeyGenerator, redisService);
    }

    /**
     * 注册限流服务
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimitService rateLimitService(
            RedisService redisService,
            @Qualifier("synapseCacheKeyGenerator") CacheKeyGenerator cacheKeyGenerator) {
        log.info("创建RateLimitService Bean");
        return new RateLimitService(redisService, cacheKeyGenerator);
    }

    /**
     * 注册分布式锁服务
     * 依赖于ThreadUtils，只有在ThreadUtils bean存在时才创建
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(ThreadUtils.class)
    public DistributedLockService distributedLockService(
            RedisService redisService,
            @Qualifier("synapseCacheKeyGenerator") CacheKeyGenerator cacheKeyGenerator,
            ThreadUtils threadUtils) {
        log.info("创建DistributedLockService Bean");
        return new DistributedLockService(redisService, cacheKeyGenerator, threadUtils);
    }


} 