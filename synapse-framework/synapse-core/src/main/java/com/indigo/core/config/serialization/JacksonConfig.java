package com.indigo.core.config.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Jackson 序列化配置
 * Jackson serialization configuration
 *
 * @author IndigoByte
 * @date 2024/03/21
 */
@Configuration
public class JacksonConfig {

    /**
     * 默认日期时间格式
     * Default date time format
     */
    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 默认日期格式
     * Default date format
     */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    
    /**
     * 默认时间格式
     * Default time format
     */
    private static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    /**
     * 默认时区
     * Default timezone
     */
    private static final String DEFAULT_TIMEZONE = "Asia/Shanghai";

    /**
     * 时区配置
     * Timezone configuration
     * 支持以下格式：
     * 1. 时区ID，如 "Asia/Shanghai"
     * 2. 时区偏移，如 "+08:00"
     * 3. 区域/城市，如 "GMT+8"
     */
    @Value("${spring.jackson.timezone:${spring.jackson.time-zone:${server.timezone:${server.time-zone:Asia/Shanghai}}}}")
    private String timezone;

    /**
     * 配置全局 ObjectMapper
     * Configure global ObjectMapper
     *
     * @return ObjectMapper 实例 / ObjectMapper instance
     */
    @Bean("synapseObjectMapper")
    @Primary
    public ObjectMapper synapseObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册 Java 8+ 时间模块
        // Register Java 8+ time module
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 配置日期时间格式化器
        // Configure date time formatters
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN);
        
        // 配置 LocalDateTime 序列化和反序列化
        // Configure LocalDateTime serialization and deserialization
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        
        // 配置 LocalDate 序列化和反序列化
        // Configure LocalDate serialization and deserialization
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        
        // 配置 LocalTime 序列化和反序列化
        // Configure LocalTime serialization and deserialization
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        
        // 注册时间模块
        // Register time module
        objectMapper.registerModule(javaTimeModule);
        
        // 设置时区
        // Set timezone
        try {
            // 尝试解析时区ID
            ZoneId zoneId = ZoneId.of(timezone);
            objectMapper.setTimeZone(TimeZone.getTimeZone(zoneId));
        } catch (Exception e) {
            // 如果解析失败，使用系统默认时区
            objectMapper.setTimeZone(TimeZone.getDefault());
        }
        
        // 配置序列化特性
        // Configure serialization features
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 注释掉默认类型信息配置，避免HTTP请求JSON格式问题
        // objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, 
        //     ObjectMapper.DefaultTyping.NON_FINAL);
        
        // 配置反序列化特性
        // Configure deserialization features
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        
        // 配置序列化特性
        // Configure serialization features
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        
        return objectMapper;
    }
} 