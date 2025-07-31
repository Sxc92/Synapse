# Synapse Framework 框架集合

## 📖 概述

Synapse Framework 是 SynapseMOM 制造运营管理平台的核心框架集合，提供了一套完整的微服务开发基础设施。该框架集合包含核心工具、缓存、安全、事件驱动、数据库等模块，为业务服务提供开箱即用的技术能力。

## 🏗️ 架构设计

```
SynapseMOM Platform
├── Business Modules (业务模块)
│   ├── User Service (用户服务)
│   ├── Order Service (订单服务)
│   ├── Product Service (产品服务)
│   └── ... (其他业务服务)
├── Infrastructure Modules (基础设施模块)
│   ├── Gateway Service (网关服务)
│   ├── Monitor Service (监控服务)
│   └── ... (其他基础设施服务)
└── Synapse Framework (框架集合) ← 本项目
    ├── synapse-core (核心框架)
    ├── synapse-cache (缓存框架)
    ├── synapse-security (安全框架)
    ├── synapse-events (事件框架)
    ├── synapse-databases (数据库框架)
    └── synapse-bom (依赖管理)
```

## 📦 模块介绍

### 🛠️ [Synapse Core](./synapse-core/README.md) - 核心框架

提供基础工具类、异常处理、配置管理、国际化支持等核心功能。

**主要特性：**
- 丰富的工具类集合（断言、集合、日期时间、字符串等）
- 统一的异常处理机制
- 多语言国际化支持
- 用户上下文管理
- 树形结构支持

### 🎯 [Synapse Cache](./synapse-cache/README.md) - 缓存框架

提供多级缓存、分布式锁、限流等缓存相关功能。

**主要特性：**
- 多级缓存（本地缓存 + Redis）
- 分布式锁服务
- 限流功能
- 注解驱动使用
- 用户会话管理

### 🔐 [Synapse Security](./synapse-security/README.md) - 安全框架

提供认证、授权、权限管理等安全功能。

**主要特性：**
- 多种认证策略（Sa-Token、OAuth2.0、JWT）
- 基于角色的访问控制
- 用户上下文管理
- 分布式会话管理
- 自动配置支持

### 🔄 [Synapse Events](./synapse-events/README.md) - 事件框架

提供事件驱动架构支持，基于 RocketMQ 实现。

**主要特性：**
- 事件发布和订阅
- 可靠消息传递
- 事务状态管理
- 事件优先级
- 自动回滚机制

### 🗄️ [Synapse Databases](./synapse-databases/README.md) - 数据库框架

提供动态数据源、SQL注解框架、统一分页查询等数据库功能。

**主要特性：**
- **🆕 无ServiceImpl**: 接口+注解即可使用，大幅提升开发效率
- **🆕 统一分页查询**: 基于PageDTO的统一分页查询，支持自动查询条件构建
- **🆕 SQL注解框架**: 支持复杂多表查询和聚合查询
- **🆕 类型安全**: 编译时类型检查，避免运行时错误
- 动态数据源切换
- 多数据库支持
- 负载均衡策略
- 健康检查
- 故障转移

### 📦 [Synapse BOM](./synapse-bom/README.md) - 依赖管理

统一管理所有模块的版本依赖，确保版本兼容性。

**主要特性：**
- 统一版本管理
- 依赖传递
- 版本兼容性保证
- 简化配置

## 🚀 快速开始

### 1. 添加 BOM 依赖

在父项目的 `pom.xml` 中添加：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.indigo</groupId>
            <artifactId>synapse-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 选择需要的模块

在子模块的 `pom.xml` 中添加需要的依赖：

```xml
<dependencies>
    <!-- 核心框架（必需） -->
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-core</artifactId>
    </dependency>
    
    <!-- 缓存框架（可选） -->
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-cache</artifactId>
    </dependency>
    
    <!-- 安全框架（可选） -->
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-security</artifactId>
    </dependency>
    
    <!-- 事件框架（可选） -->
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-events</artifactId>
    </dependency>
    
    <!-- 数据库框架（推荐） -->
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-databases</artifactId>
    </dependency>
</dependencies>
```

### 3. 配置示例

#### 基础配置

```yaml
# application.yml
spring:
  application:
    name: your-service-name
  profiles:
    active: dev

# Synapse 框架配置
synapse:
  core:
    enabled: true
  cache:
    enabled: true
  security:
    enabled: true
  events:
    enabled: true
  databases:
    enabled: true
```

#### Nacos 配置

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_ADDR:localhost:8848}
        namespace: ${NACOS_NAMESPACE:}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
        username: ${NACOS_USERNAME:nacos}
        password: ${NACOS_PASSWORD:123456}
      config:
        server-addr: ${NACOS_ADDR:localhost:8848}
        namespace: ${NACOS_NAMESPACE:}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
        username: ${NACOS_USERNAME:nacos}
        password: ${NACOS_PASSWORD:123456}
```

#### Redis 配置

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      database: 0
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1
```

#### 数据库配置

```yaml
spring:
  datasource:
    dynamic:
      primary: master1
      strict: false
      datasource:
        master1:
          type: MYSQL
          host: localhost
          port: 3306
          database: synapse_iam
          username: root
          password: password
          params:
            useUnicode: "true"
            characterEncoding: "utf8"
            useSSL: "false"
            serverTimezone: "Asia/Shanghai"
          hikari:
            minimumIdle: 5
            maximumPoolSize: 15
            idleTimeout: 30000
            maxLifetime: 1800000
            connectionTimeout: 30000
            connectionTestQuery: "SELECT 1"
```

#### Seata 分布式事务配置

```yaml
seata:
  application-id: ${spring.application.name}
  tx-service-group: default_tx_group
  data-source-proxy-mode: AT
  service:
    vgroup-mapping:
      default_tx_group: default
    grouplist:
      default: 127.0.0.1:8091
  registry:
    type: file
  config:
    type: file
  enable-auto-data-source-proxy: false
```

## 📋 使用示例

### 🆕 数据库框架使用示例

#### 1. 定义Repository接口（无需实现类）

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 框架自动提供所有MyBatis-Plus方法
    // 无需手写任何实现代码
}
```

#### 2. 使用统一分页查询

```java
@Service
public class TenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    // 一行代码完成分页查询
    public PageResult<IamTenant> getTenantsPage(TenantsPageDTO params) {
        return tenantsRepository.pageWithCondition(params);
    }
}
```

#### 3. 使用SQL注解

```java
@AutoRepository
public interface UserRepository extends BaseRepository<User, UserMapper> {
    
    // 自定义SQL查询
    @SqlQuery("SELECT * FROM iam_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    // 复杂多表查询
    @SqlQuery("""
        SELECT u.*, r.role_name 
        FROM iam_user u 
        LEFT JOIN iam_user_role ur ON u.id = ur.user_id 
        LEFT JOIN iam_role r ON ur.role_id = r.id 
        WHERE u.id = #{userId}
    """)
    UserWithRoleDTO findUserWithRoles(@Param("userId") Long userId);
}
```

### 完整的服务示例

```java
@SpringBootApplication
public class UserServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

@Service
@Slf4j
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TwoLevelCacheService cacheService;
    
    @Autowired
    private UnifiedRocketMQEventPublisher eventPublisher;
    
    @Cacheable(key = "user:#{#userId}")
    public User getUserById(Long userId) {
        log.info("查询用户: {}", userId);
        return userRepository.findById(userId);
    }
    
    @CacheEvict(key = "user:#{#user.id}")
    public User createUser(User user) {
        // 参数验证
        AssertUtils.notNull(user, "用户信息不能为空");
        AssertUtils.hasText(user.getUsername(), "用户名不能为空");
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 发布用户创建事件
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", savedUser.getId());
        eventData.put("username", savedUser.getUsername());
        
        PublishResult result = eventPublisher.publish("user.created", "user-service", eventData);
        if (result.isSuccess()) {
            log.info("用户创建事件发布成功: {}", result.getTransactionId());
        }
        
        return savedUser;
    }
}

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{userId}")
    @SaCheckPermission("user:read")
    public Result<User> getUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户失败", e);
            return Result.error("获取用户失败");
        }
    }
    
    @PostMapping
    @SaCheckPermission("user:create")
    public Result<User> createUser(@RequestBody @Valid User user) {
        try {
            User createdUser = userService.createUser(user);
            return Result.success(createdUser);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return Result.error("创建用户失败");
        }
    }
}
```

## 🔧 配置选项

### 全局配置

```yaml
synapse:
  # 核心框架配置
  core:
    enabled: true
    thread-pool:
      core-size: 10
      max-size: 20
      queue-capacity: 100
  
  # 缓存框架配置
  cache:
    enabled: true
    local:
      max-size: 1000
      expire-after-write: 300
    redis:
      key-prefix: "synapse:"
      default-ttl: 3600
  
  # 安全框架配置
  security:
    enabled: true
    authentication:
      strategy: sa-token
      default-timeout: 3600
    authorization:
      enabled: true
      cache-permissions: true
  
  # 事件框架配置
  events:
    enabled: true
    rocketmq:
      name-server: localhost:9876
      producer-group: synapse-producer
      consumer-group: synapse-consumer
    reliable:
      error-rate-threshold: 5.0
      latency-threshold: 1000
  
  # 数据库框架配置
  databases:
    enabled: true
    dynamic:
      enabled: true
      default-data-source: primary
    health-check:
      enabled: true
      interval: 30000
    # 统一分页查询配置
    pagination:
      default-page-size: 10
      max-page-size: 100
    # SQL注解框架配置
    sql-annotation:
      enabled: true
      cache-enabled: true
```

## 🧪 测试

### 单元测试

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private TwoLevelCacheService cacheService;
    
    @Mock
    private UnifiedRocketMQEventPublisher eventPublisher;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void createUser_Success() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        
        PublishResult publishResult = PublishResult.success("msg-123", "txn-456");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(eventPublisher.publish(anyString(), anyString(), anyMap()))
            .thenReturn(publishResult);
        
        // When
        User result = userService.createUser(user);
        
        // Then
        assertEquals(savedUser, result);
        verify(userRepository).save(user);
        verify(eventPublisher).publish("user.created", "user-service", anyMap());
    }
}
```

### 集成测试

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "synapse.core.enabled=true",
    "synapse.cache.enabled=true",
    "synapse.security.enabled=true",
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void createAndGetUser_Integration() {
        // Given
        User user = new User();
        user.setUsername("integrationtest");
        user.setEmail("integration@example.com");
        
        // When
        User createdUser = userService.createUser(user);
        User retrievedUser = userService.getUserById(createdUser.getId());
        
        // Then
        assertNotNull(createdUser.getId());
        assertEquals("integrationtest", createdUser.getUsername());
        assertEquals(createdUser.getId(), retrievedUser.getId());
    }
}
```

## 📚 文档导航

- [Synapse Core](./synapse-core/README.md) - 核心框架文档
- [Synapse Cache](./synapse-cache/README.md) - 缓存框架文档
- [Synapse Security](./synapse-security/README.md) - 安全框架文档
- [Synapse Events](./synapse-events/README.md) - 事件框架文档
- [Synapse Databases](./synapse-databases/README.md) - 数据库框架文档
- [Synapse BOM](./synapse-bom/README.md) - 依赖管理文档

## 🔄 版本兼容性

| 版本 | Spring Boot | Spring Cloud | Java | 说明 |
|------|-------------|--------------|------|------|
| 1.0.0 | 3.2.3 | 2023.0.0 | 17+ | **🆕 数据库框架重大改进** |

### 🆕 v1.0.0 主要改进 (2025-07-31)

- **统一分页查询**: 基于PageDTO的统一分页查询，支持自动查询条件构建
- **SQL注解框架**: 无ServiceImpl，接口+注解即可使用，支持复杂多表查询
- **类型安全**: 编译时类型检查，避免运行时错误
- **性能优化**: 数据库层面排序，避免内存分页问题
- **技术栈升级**: 升级到Spring Boot 3.2.3 + Spring Cloud 2023.0.0

## 🚀 最佳实践

### 1. 模块选择

- **所有服务**：必须引入 `synapse-core`
- **需要缓存**：引入 `synapse-cache`
- **需要认证**：引入 `synapse-security`
- **需要事件**：引入 `synapse-events`
- **需要数据库操作**：**强烈推荐**引入 `synapse-databases`
  - 无ServiceImpl，大幅提升开发效率
  - 统一分页查询，简化分页逻辑
  - SQL注解框架，支持复杂查询

### 2. 配置管理

- 使用 `synapse-bom` 统一管理版本
- 按需启用模块功能
- 合理配置缓存和连接池参数

### 3. 异常处理

- 使用 `Result<T>` 统一返回格式
- 合理使用业务异常和系统异常
- 记录详细的错误日志

### 4. 性能优化

- 合理使用缓存
- 配置合适的连接池大小
- 监控关键指标

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个框架。

### 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

### 开发环境

- **JDK**: 17+
- **Maven**: 3.6+
- **Spring Boot**: 3.2.3
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Alibaba**: 2023.0.1.0
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Nacos**: 2.0+
- **Seata**: 2.0+

## 📄 许可证

本项目采用 MIT 许可证。

## 📞 联系方式

- **维护者**：史偕成
- **邮箱**：shixiecheng@indigobyte.com
- **项目地址**：https://github.com/indigobyte/synapse-framework

---

**最后更新：** 2025-07-31  
**版本：** 1.0.0  
**维护者：** 史偕成

### 📝 更新日志

#### v1.0.0 (2025-07-31)
- 🎉 **数据库框架重大改进**: 统一分页查询、SQL注解框架
- ✅ **无ServiceImpl**: 接口+注解即可使用，大幅提升开发效率
- 🔧 **性能优化**: 数据库层面排序，避免内存分页问题
- 📚 **文档完善**: 提供完整的使用指南和最佳实践
- 🚀 **技术栈升级**: Spring Boot 3.2.3 + Spring Cloud 2023.0.0
- 🔧 **中间件支持**: Nacos 2.0+ + Seata 2.0+ + Redis 6.0+ 