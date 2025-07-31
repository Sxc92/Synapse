package com.indigo.databases.cache.manager;

import com.indigo.cache.manager.CacheKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheKeyGenerator单元测试
 */
public class CacheKeyGeneratorTest {

    private CacheKeyGenerator keyGenerator;
    
    @BeforeEach
    public void setup() {
        keyGenerator = new CacheKeyGenerator();
    }
    
    @Test
    public void testGenerate() {
        // 测试基本的模块和键组合
        String key = keyGenerator.generate("user", "123");
        assertEquals("synapse:user:123", key);
        
        // 测试含有特殊字符的键
        key = keyGenerator.generate("order", "A-001:B");
        assertEquals("synapse:order:A-001:B", key);
    }
    
    @Test
    public void testGenerateWithMultipleKeys() {
        // 测试多级键
        String key = keyGenerator.generate("user", "profile", "123");
        assertEquals("synapse:user:profile:123", key);
        
        key = keyGenerator.generate("product", "category", "electronics", "phone");
        assertEquals("synapse:product:category:electronics:phone", key);
    }
    
    @Test
    public void testGenerateWithEmptyParts() {
        // 测试空参数
        String key = keyGenerator.generate("user");
        assertEquals("synapse:user", key);
        
        // 测试null值
        key = keyGenerator.generate("user", (Object)null);
        assertEquals("synapse:user", key);
    }
    
    @Test
    public void testGenerateWithObjects() {
        // 测试对象转换为字符串
        String key = keyGenerator.generate("user", 123);
        assertEquals("synapse:user:123", key);
        
        key = keyGenerator.generate("product", true);
        assertEquals("synapse:product:true", key);
    }
    
    @Test
    public void testModuleConstants() {
        // 测试模块常量
        assertEquals("user", CacheKeyGenerator.Module.USER);
        assertEquals("order", CacheKeyGenerator.Module.ORDER);
        assertEquals("product", CacheKeyGenerator.Module.PRODUCT);
        assertEquals("config", CacheKeyGenerator.Module.CONFIG);
        assertEquals("auth", CacheKeyGenerator.Module.AUTH);
        assertEquals("message", CacheKeyGenerator.Module.MESSAGE);
        assertEquals("log", CacheKeyGenerator.Module.LOG);
        assertEquals("task", CacheKeyGenerator.Module.TASK);
        assertEquals("lock", CacheKeyGenerator.Module.LOCK);
        assertEquals("rate_limit", CacheKeyGenerator.Module.RATE_LIMIT);
    }
    
    @Test
    public void testGenerateWithModuleConstants() {
        // 测试使用模块常量生成键
        String key = keyGenerator.generate(CacheKeyGenerator.Module.USER, "123");
        assertEquals("synapse:user:123", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.ORDER, "ORDER-001");
        assertEquals("synapse:order:ORDER-001", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.PRODUCT, "P001");
        assertEquals("synapse:product:P001", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.CONFIG, "system");
        assertEquals("synapse:config:system", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.LOCK, "user_update");
        assertEquals("synapse:lock:user_update", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.RATE_LIMIT, "api");
        assertEquals("synapse:rate_limit:api", key);
    }
    
    @Test
    public void testGenerateWithModuleConstantsAndMultipleKeys() {
        // 测试使用模块常量生成多级键
        String key = keyGenerator.generate(CacheKeyGenerator.Module.USER, "123", "profile");
        assertEquals("synapse:user:123:profile", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.ORDER, "ORDER-001", "items");
        assertEquals("synapse:order:ORDER-001:items", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.PRODUCT, "P001", "specs");
        assertEquals("synapse:product:P001:specs", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.CONFIG, "system", "performance");
        assertEquals("synapse:config:system:performance", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.LOCK, "order_process", "12345");
        assertEquals("synapse:lock:order_process:12345", key);
        
        key = keyGenerator.generate(CacheKeyGenerator.Module.RATE_LIMIT, "api", "user", "get");
        assertEquals("synapse:rate_limit:api:user:get", key);
    }
} 