package com.indigo.core.config.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * 自定义序列化器配置
 * Custom serializers configuration
 *
 * @author IndigoByte
 * @date 2024/03/21
 */
@Configuration
public class CustomSerializers {

    /**
     * 注册自定义序列化器模块
     * Register custom serializer module
     *
     * @return SimpleModule 实例 / SimpleModule instance
     */
    @Bean
    public SimpleModule customSerializersModule() {
        SimpleModule module = new SimpleModule();
        
        // 配置 BigDecimal 序列化器（保持原始精度）
        // Configure BigDecimal serializer (keep original precision)
        module.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) 
                throws IOException {
                if (value != null) {
                    gen.writeString(value.toString());
                } else {
                    gen.writeNull();
                }
            }
        });
        
        // 配置 BigDecimal 反序列化器（保持原始精度）
        // Configure BigDecimal deserializer (keep original precision)
        module.addDeserializer(BigDecimal.class, new JsonDeserializer<BigDecimal>() {
            @Override
            public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) 
                throws IOException {
                String value = p.getValueAsString();
                return value != null ? new BigDecimal(value) : null;
            }
        });
        
        // 配置 BigInteger 序列化器（转换为字符串）
        // Configure BigInteger serializer (convert to string)
        module.addSerializer(BigInteger.class, new JsonSerializer<BigInteger>() {
            @Override
            public void serialize(BigInteger value, JsonGenerator gen, SerializerProvider provider) 
                throws IOException {
                if (value != null) {
                    gen.writeString(value.toString());
                } else {
                    gen.writeNull();
                }
            }
        });
        
        // 配置 BigInteger 反序列化器
        // Configure BigInteger deserializer
        module.addDeserializer(BigInteger.class, new JsonDeserializer<BigInteger>() {
            @Override
            public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) 
                throws IOException {
                String value = p.getValueAsString();
                return value != null ? new BigInteger(value) : null;
            }
        });
        
        // 配置 byte[] 序列化器（Base64编码）
        // Configure byte[] serializer (Base64 encoding)
        module.addSerializer(byte[].class, new JsonSerializer<byte[]>() {
            @Override
            public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) 
                throws IOException {
                if (value != null) {
                    gen.writeString(Base64.getEncoder().encodeToString(value));
                } else {
                    gen.writeNull();
                }
            }
        });
        
        // 配置 byte[] 反序列化器（Base64解码）
        // Configure byte[] deserializer (Base64 decoding)
        module.addDeserializer(byte[].class, new JsonDeserializer<byte[]>() {
            @Override
            public byte[] deserialize(JsonParser p, DeserializationContext ctxt) 
                throws IOException {
                String value = p.getValueAsString();
                return value != null ? Base64.getDecoder().decode(value) : null;
            }
        });

        // 配置 UUID 序列化器
        // Configure UUID serializer
        module.addSerializer(UUID.class, new JsonSerializer<UUID>() {
            @Override
            public void serialize(UUID value, JsonGenerator gen, SerializerProvider provider) 
                throws IOException {
                if (value != null) {
                    gen.writeString(value.toString());
                } else {
                    gen.writeNull();
                }
            }
        });

        // 配置 UUID 反序列化器
        // Configure UUID deserializer
        module.addDeserializer(UUID.class, new JsonDeserializer<UUID>() {
            @Override
            public UUID deserialize(JsonParser p, DeserializationContext ctxt) 
                throws IOException {
                String value = p.getValueAsString();
                return value != null ? UUID.fromString(value) : null;
            }
        });
        
        return module;
    }
} 