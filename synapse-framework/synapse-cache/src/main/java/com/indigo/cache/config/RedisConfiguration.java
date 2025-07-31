package com.indigo.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类，基于Spring Boot自动配置进行扩展
 * 业务模块只需要在yml中配置spring.redis相关属性即可
 * 
 * @author 史偕成
 * @date 2025/05/11 20:35
 */
@Slf4j
@Configuration
public class RedisConfiguration {
    
    /**
     * 配置RedisTemplate，使用JSON序列化
     * 只有当Spring Boot没有创建RedisTemplate时才创建自定义的
     */
    @Bean("redisTemplate")
    @Primary
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("创建自定义RedisTemplate Bean，RedisConnectionFactory: {}", 
                 redisConnectionFactory != null ? redisConnectionFactory.getClass().getSimpleName() : "null");
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        
        // 配置JSON序列化器
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper());
        
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value值
        template.setValueSerializer(jackson2JsonRedisSerializer);
        
        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        // Hash的value采用GenericJackson2JsonRedisSerializer的序列化方式
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        
        template.afterPropertiesSet();
        
        log.info("自定义RedisTemplate Bean 创建成功");
        return template;
    }
    
    /**
     * 配置StringRedisTemplate
     * 只有当Spring Boot没有创建StringRedisTemplate时才创建自定义的
     */
    @Bean("stringRedisTemplate")
    @ConditionalOnMissingBean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("创建自定义StringRedisTemplate Bean");
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        log.info("自定义StringRedisTemplate Bean 创建成功");
        return template;
    }
    
    /**
     * ObjectMapper配置，用于Redis序列化
     */
    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 注释掉默认类型信息配置，避免Redis存储二进制格式
        // mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        return mapper;
    }
}
