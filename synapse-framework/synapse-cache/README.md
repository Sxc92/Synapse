# Synapse Cache 缓存框架

## 📖 概述

Synapse Cache 是一个基于 Spring Boot 的缓存框架，提供了多级缓存支持，包括本地缓存（Caffeine）和分布式缓存（Redis）。该框架支持注解驱动和编程式使用，为 SynapseMOM 平台提供高性能的缓存解决方案。

## ✨ 特性

- 🚀 **开箱即用**: 基于 Spring Boot 自动配置，无需复杂配置
- 🎯 **多级缓存**: 支持本地缓存 + Redis 分布式缓存
- 🔒 **分布式锁**: 提供基于 Redis 的分布式锁
- ⚡ **限流支持**: 内置限流功能，支持多种限流策略
- 📝 **注解驱动**: 通过注解轻松使用缓存功能
- 🎨 **用户会话**: 提供用户会话管理
- 🔄 **自动过期**: 支持缓存自动过期和清理
- 📊 **监控统计**: 提供缓存使用统计和监控

## 🏗️ 模块结构

```
synapse-cache/
├── annotation/       # 注解定义
│   ├── Cacheable.java    # 缓存注解
│   ├── CacheEvict.java   # 缓存清除注解
│   └── RateLimit.java    # 限流注解
├── aspect/          # AOP 切面
│   ├── CacheAspect.java      # 缓存切面
│   ├── RateLimitAspect.java  # 限流切面
│   └── AbstractRateLimitAspect.java
├── config/          # 配置类
│   ├── CacheAutoConfiguration.java  # 缓存自动配置
│   └── RedisConfiguration.java      # Redis 配置
├── core/            # 核心服务
│   ├── CacheService.java            # 缓存服务接口
│   └── TwoLevelCacheService.java    # 多级缓存服务
├── extension/       # 扩展功能
│   ├── DistributedLockService.java  # 分布式锁服务
│   └── RateLimitService.java        # 限流服务
├── infrastructure/  # 基础设施
│   ├── CaffeineCacheManager.java    # Caffeine 缓存管理器
│   └── RedisService.java            # Redis 服务
├── manager/         # 管理器
│   └── CacheKeyGenerator.java       # 缓存键生成器
├── model/           # 模型类
│   └── CacheObject.java             # 缓存对象
└── session/         # 会话管理
    ├── SessionManager.java          # 会话管理器
    ├── PermissionManager.java       # 权限管理器
    ├── StatisticsManager.java       # 统计管理器
    └── impl/                        # 实现类
```

## 🚀 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-cache</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 Redis

在 `application.yml` 中配置 Redis：

#### 单机模式配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    password: # 如果有密码则填写
    timeout: 2000ms
    lettuce:
      shutdown-timeout: 100ms
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
```

#### 集群模式配置

```yaml
spring:
  redis:
    cluster:
      nodes:
        - 127.0.0.1:7001
        - 127.0.0.1:7002
        - 127.0.0.1:7003
      max-redirects: 3
    password: # 如果有密码则填写
    timeout: 2000ms
```

#### 哨兵模式配置

```yaml
spring:
  redis:
    sentinel:
      master: mymaster
      nodes:
        - 127.0.0.1:26379
        - 127.0.0.1:26380
        - 127.0.0.1:26381
    password: # 如果有密码则填写
    timeout: 2000ms
```

### 3. 使用注解

#### @Cacheable - 缓存查询结果

```java
@Service
public class UserService {
    
    @Cacheable(key = "user:#{#userId}")
    public User getUserById(Long userId) {
        // 从数据库查询用户
        return userRepository.findById(userId);
    }
    
    @Cacheable(key = "users:list", condition = "#result != null")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
```

#### @CacheEvict - 清除缓存

```java
@Service
public class UserService {
    
    @CacheEvict(key = "user:#{#user.id}")
    public void updateUser(User user) {
        userRepository.save(user);
    }
    
    @CacheEvict(pattern = "user:*")
    public void clearAllUserCache() {
        // 清除所有用户缓存
    }
    
    @CacheEvict(key = "users:list")
    public void createUser(User user) {
        userRepository.save(user);
    }
}
```

#### @RateLimit - 限流

```java
@RestController
public class ApiController {
    
    @RateLimit(key = "api:#{#request.remoteAddr}", 
               permits = 10, 
               period = 60, 
               timeUnit = TimeUnit.SECONDS)
    @GetMapping("/api/data")
    public Result<String> getData(HttpServletRequest request) {
        return Result.success("data");
    }
    
    @RateLimit(key = "user:#{#userId}", 
               permits = 100, 
               period = 3600, 
               timeUnit = TimeUnit.SECONDS)
    @GetMapping("/api/user/{userId}/profile")
    public Result<User> getUserProfile(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }
}
```

## 📋 核心功能

### 编程式缓存使用

#### 使用 CacheService

```java
@Service
public class BusinessService {
    
    @Autowired
    private CacheService cacheService;
    
    public void example() {
        // 设置缓存
        cacheService.set("key", "value", 300); // 5分钟过期
        
        // 获取缓存
        String value = cacheService.get("key", String.class);
        
        // 删除缓存
        cacheService.delete("key");
        
        // 批量删除
        cacheService.deleteByPattern("user:*");
        
        // 检查缓存是否存在
        boolean exists = cacheService.exists("key");
        
        // 设置过期时间
        cacheService.expire("key", 600); // 10分钟过期
    }
}
```

#### 使用 TwoLevelCacheService（推荐）

```java
@Service
public class BusinessService {
    
    @Autowired
    private TwoLevelCacheService twoLevelCacheService;
    
    public User getUserById(Long userId) {
        String key = "user:" + userId;
        
        // 从多级缓存获取
        User user = twoLevelCacheService.get(key, User.class);
        if (user == null) {
            // 从数据库查询
            user = userRepository.findById(userId);
            if (user != null) {
                // 存入多级缓存
                twoLevelCacheService.put(key, user, 300); // 5分钟过期
            }
        }
        return user;
    }
    
    public void updateUser(User user) {
        // 更新数据库
        userRepository.save(user);
        
        // 清除缓存
        String key = "user:" + user.getId();
        twoLevelCacheService.delete(key);
    }
}
```

### 分布式锁

```java
@Service
public class BusinessService {
    
    @Autowired
    private DistributedLockService lockService;
    
    public void processOrder(Order order) {
        String lockKey = "order:process:" + order.getId();
        
        try {
            // 获取分布式锁
            boolean locked = lockService.tryLock(lockKey, 30, TimeUnit.SECONDS);
            if (locked) {
                try {
                    // 执行业务逻辑
                    processOrderLogic(order);
                } finally {
                    // 释放锁
                    lockService.unlock(lockKey);
                }
            } else {
                throw new BusinessException("订单正在处理中，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("处理被中断");
        }
    }
    
    public void batchProcess(List<Order> orders) {
        String lockKey = "batch:process:" + System.currentTimeMillis();
        
        if (lockService.tryLock(lockKey, 60, TimeUnit.SECONDS)) {
            try {
                for (Order order : orders) {
                    processOrderLogic(order);
                }
            } finally {
                lockService.unlock(lockKey);
            }
        }
    }
}
```

### 限流服务

```java
@Service
public class RateLimitService {
    
    @Autowired
    private RateLimitService rateLimitService;
    
    public boolean checkRateLimit(String key, int permits, int period) {
        return rateLimitService.tryAcquire(key, permits, period, TimeUnit.SECONDS);
    }
    
    public void processWithRateLimit(String userId, Runnable task) {
        String key = "user:limit:" + userId;
        
        if (rateLimitService.tryAcquire(key, 10, 60, TimeUnit.SECONDS)) {
            task.run();
        } else {
            throw new BusinessException("请求过于频繁，请稍后重试");
        }
    }
}
```

### 会话管理

```java
@Service
public class SessionService {
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private PermissionManager permissionManager;
    
    public void createSession(String sessionId, User user) {
        // 创建会话
        sessionManager.createSession(sessionId, user, 3600); // 1小时过期
        
        // 设置权限
        permissionManager.setPermissions(sessionId, user.getPermissions());
    }
    
    public User getCurrentUser(String sessionId) {
        return sessionManager.getUser(sessionId);
    }
    
    public boolean hasPermission(String sessionId, String permission) {
        return permissionManager.hasPermission(sessionId, permission);
    }
    
    public void invalidateSession(String sessionId) {
        sessionManager.invalidateSession(sessionId);
    }
}
```

## ⚙️ 配置选项

### 缓存配置

```yaml
synapse:
  cache:
    enabled: true
    local:
      max-size: 1000
      expire-after-write: 300
      expire-after-access: 60
    redis:
      key-prefix: "synapse:"
      default-ttl: 3600
    rate-limit:
      enabled: true
      default-permits: 100
      default-period: 60
```

### 分布式锁配置

```yaml
synapse:
  cache:
    distributed-lock:
      enabled: true
      default-timeout: 30
      retry-interval: 100
      max-retries: 3
```

## 📝 使用示例

### 完整的缓存服务示例

```java
@Service
@Slf4j
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private TwoLevelCacheService cacheService;
    
    @Autowired
    private DistributedLockService lockService;
    
    @Cacheable(key = "product:#{#productId}")
    public Product getProductById(Long productId) {
        log.info("从数据库查询产品: {}", productId);
        return productRepository.findById(productId);
    }
    
    @CacheEvict(key = "product:#{#product.id}")
    public Product updateProduct(Product product) {
        log.info("更新产品: {}", product.getId());
        
        String lockKey = "product:update:" + product.getId();
        
        try {
            if (lockService.tryLock(lockKey, 10, TimeUnit.SECONDS)) {
                try {
                    Product updatedProduct = productRepository.save(product);
                    log.info("产品更新成功: {}", updatedProduct.getId());
                    return updatedProduct;
                } finally {
                    lockService.unlock(lockKey);
                }
            } else {
                throw new BusinessException("产品正在更新中，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("更新被中断");
        }
    }
    
    @CacheEvict(pattern = "product:*")
    public void clearAllProductCache() {
        log.info("清除所有产品缓存");
    }
    
    public List<Product> getProductsByCategory(String category) {
        String cacheKey = "products:category:" + category;
        
        List<Product> products = cacheService.get(cacheKey, List.class);
        if (products == null) {
            products = productRepository.findByCategory(category);
            if (products != null) {
                cacheService.put(cacheKey, products, 1800); // 30分钟过期
            }
        }
        
        return products;
    }
}
```

### 限流控制器示例

```java
@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @RateLimit(key = "api:products:list", permits = 50, period = 60)
    @GetMapping
    public Result<List<Product>> getAllProducts() {
        return Result.success(productService.getAllProducts());
    }
    
    @RateLimit(key = "api:product:#{#productId}", permits = 100, period = 60)
    @GetMapping("/{productId}")
    public Result<Product> getProduct(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return Result.error("产品不存在");
        }
        return Result.success(product);
    }
    
    @RateLimit(key = "api:product:update:#{#product.id}", permits = 10, period = 60)
    @PutMapping("/{productId}")
    public Result<Product> updateProduct(@PathVariable Long productId, 
                                       @RequestBody Product product) {
        product.setId(productId);
        Product updatedProduct = productService.updateProduct(product);
        return Result.success(updatedProduct);
    }
}
```

### 缓存监控示例

```java
@Component
@Slf4j
public class CacheMonitor {
    
    @Autowired
    private CacheService cacheService;
    
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void logCacheStats() {
        Map<String, Object> stats = cacheService.getStats();
        
        log.info("缓存统计信息:");
        log.info("  - 总缓存数量: {}", stats.get("totalCount"));
        log.info("  - 命中率: {}%", stats.get("hitRate"));
        log.info("  - 内存使用: {}MB", stats.get("memoryUsage"));
        log.info("  - 过期数量: {}", stats.get("expiredCount"));
    }
}
```

## 🧪 测试

### 单元测试示例

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private TwoLevelCacheService cacheService;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    void getProductById_WithCache() {
        // Given
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("测试产品");
        
        when(cacheService.get("product:" + productId, Product.class))
            .thenReturn(product);
        
        // When
        Product result = productService.getProductById(productId);
        
        // Then
        assertEquals(product, result);
        verify(cacheService).get("product:" + productId, Product.class);
        verifyNoInteractions(productRepository);
    }
    
    @Test
    void getProductById_WithoutCache() {
        // Given
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("测试产品");
        
        when(cacheService.get("product:" + productId, Product.class))
            .thenReturn(null);
        when(productRepository.findById(productId)).thenReturn(product);
        
        // When
        Product result = productService.getProductById(productId);
        
        // Then
        assertEquals(product, result);
        verify(cacheService).get("product:" + productId, Product.class);
        verify(productRepository).findById(productId);
    }
}
```

## 📚 相关文档

- [Synapse Core](./synapse-core/README.md) - 核心框架
- [Synapse Events](./synapse-events/README.md) - 事件驱动框架
- [Synapse Security](./synapse-security/README.md) - 安全框架
- [Synapse Databases](./synapse-databases/README.md) - 数据库框架

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个框架。

## 📄 许可证

本项目采用 MIT 许可证。

---

**最后更新：** 2025-07-20  
**版本：** 1.0.0  
**维护者：** 史偕成 