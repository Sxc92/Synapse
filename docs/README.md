# SynapseMOM 制造运营管理平台

## 📖 平台概述

SynapseMOM 是一个基于 Spring Boot 3.2.3 的现代化制造运营管理平台，提供完整的微服务架构解决方案。平台集成了身份认证、数据管理、事件驱动、缓存、安全等核心功能模块，为企业级应用提供开箱即用的技术能力。

## 🏗️ 平台架构

```
SynapseMOM Platform
├── Business Modules (业务模块)
│   ├── User Service (用户服务)
│   ├── Order Service (订单服务)
│   ├── Product Service (产品服务)
│   └── ... (其他业务服务)
├── Foundation Modules (基础模块)
│   ├── IAM Service (身份认证服务)
│   ├── Meta Data Service (元数据服务)
│   └── ... (其他基础服务)
├── Infrastructure Modules (基础设施模块)
│   ├── Gateway Service (网关服务)
│   ├── Monitor Service (监控服务)
│   ├── Audit Service (审计服务)
│   ├── Notification Service (通知服务)
│   ├── Schedule Service (调度服务)
│   ├── Workflow Service (工作流服务)
│   ├── Integration Service (集成服务)
│   └── License Service (许可证服务)
└── Synapse Framework (框架集合)
    ├── synapse-core (核心框架)
    ├── synapse-cache (缓存框架)
    ├── synapse-security (安全框架)
    ├── synapse-events (事件框架)
    ├── synapse-databases (数据库框架)
    └── synapse-bom (依赖管理)
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+ (可选)
- **Nacos**: 2.0+ (可选)

### 5分钟快速体验

1. **克隆项目**
```bash
git clone <repository-url>
cd SynapseMOM
```

2. **配置数据库**
```yaml
# application.yml
spring:
  datasource:
    dynamic:
      primary: master1
      datasource:
        master1:
          type: MYSQL
          host: localhost
          port: 3306
          database: synapse_iam
          username: root
          password: your_password
```

3. **启动服务**
```bash
# 启动 IAM 服务
cd foundation-module/iam-service/iam-core
mvn spring-boot:run

# 启动网关服务
cd infrastructure-module/gateway-service
mvn spring-boot:run
```

4. **访问服务**
```bash
# IAM 服务
http://localhost:8081

# 网关服务
http://localhost:8080
```

详细步骤请参考：[快速开始指南](./getting-started.md)

## 📚 文档导航

### 🎯 [快速开始](./getting-started.md)
- 环境准备
- 项目配置
- 服务启动
- 基础功能演示
- 下一步学习

### 🏗️ [架构设计](./architecture.md)
- 整体架构
- 核心设计理念
- 模块架构
- 技术实现
- 扩展机制
- 性能设计
- 安全设计
- 部署架构

### 🔧 [扩展点指南](./extension-points.md)
- Repository 扩展
- 查询条件扩展
- 结果处理器扩展
- 查询拦截器扩展
- 数据源扩展
- 缓存扩展
- 事件扩展
- 配置扩展
- 最佳实践

### 🔍 [高级查询](./advanced-query.md)
- 查询方式对比
- 注解驱动查询
- 流式查询
- 多表关联查询
- 性能优化
- 查询调试
- 最佳实践

### 📖 [示例代码](./examples/)
- [基础 CRUD 示例](./examples/basic-crud/)
- 高级查询示例
- 多表关联示例
- 性能优化示例
- 扩展点示例
- 集成示例

### 🚨 [故障排除](./troubleshooting.md)
- 启动问题
- 查询问题
- 配置问题
- 性能问题
- 常见错误

## 🛠️ 核心模块

### 🔐 [身份认证 (IAM)](./iam/README.md)
- 用户管理
- 角色权限
- 组织架构
- 认证授权
- 单点登录

### 🗄️ [数据库框架](./databases/README.md)
- 无 ServiceImpl 设计
- 动态数据源
- 多级缓存
- 查询优化
- 事务管理

### 🎯 [缓存框架](./cache/README.md)
- 多级缓存
- 分布式锁
- 限流控制
- 会话管理
- 性能优化

### 🔐 [安全框架](./security/README.md)
- 多种认证策略
- 权限控制
- 数据权限
- 安全审计
- 加密解密

### 📡 [事件框架](./events/README.md)
- 事件发布订阅
- 事务事件
- 可靠投递
- 事件溯源
- 异步处理

### 🛠️ [核心框架](./core/README.md)
- 工具类集合
- 异常处理
- 国际化支持
- 上下文管理
- 配置管理

## 🚀 开发指南

### 模块选择指南

| 需求场景 | 推荐模块 | 说明 |
|---------|---------|------|
| 基础 CRUD 操作 | synapse-databases | 提供无 ServiceImpl 的数据库操作 |
| 缓存需求 | synapse-cache | 多级缓存、分布式锁、限流 |
| 安全认证 | synapse-security | 多种认证策略、权限控制 |
| 事件驱动 | synapse-events | 事件发布订阅、事务事件 |
| 工具支持 | synapse-core | 丰富的工具类、异常处理 |

### 配置说明

#### 1. 数据库配置
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
          database: your_database
          username: your_username
          password: your_password
          hikari:
            minimumIdle: 5
            maximumPoolSize: 15
            idleTimeout: 30000
            maxLifetime: 1800000
            connectionTimeout: 30000
```

#### 2. 缓存配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1
```

#### 3. 安全配置
```yaml
synapse:
  security:
    auth:
      default-strategy: satoken
      allow-concurrent-login: true
      share-token: false
    token:
      name: user_token
      timeout: 7200
      active-timeout: 1800
```

### 测试指南

#### 1. 单元测试
```java
@SpringBootTest
@AutoConfigureTestDatabase
class UserServiceTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindUser() {
        User user = userRepository.getById(1L);
        assertNotNull(user);
    }
}
```

#### 2. 集成测试
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testGetUser() {
        ResponseEntity<User> response = restTemplate.getForEntity("/api/users/1", User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

## 📊 监控和运维

### 健康检查
```bash
# 应用健康状态
curl http://localhost:8081/actuator/health

# 数据库健康状态
curl http://localhost:8081/actuator/health/db

# 缓存健康状态
curl http://localhost:8081/actuator/health/redis
```

### 性能监控
```bash
# 应用指标
curl http://localhost:8081/actuator/metrics

# 数据库连接池指标
curl http://localhost:8081/actuator/metrics/hikaricp.connections

# 缓存命中率
curl http://localhost:8081/actuator/metrics/cache.gets
```

### 日志管理
```yaml
logging:
  level:
    root: INFO
    com.indigo: DEBUG
    com.indigo.security: INFO
  file:
    name: logs/synapse-iam.log
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
```

## 🎯 最佳实践

### 1. 代码组织
```
src/main/java/com/indigo/
├── config/          # 配置类
├── controller/      # 控制器
├── service/         # 服务层
├── repository/      # 数据访问层
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── enums/           # 枚举类
├── utils/           # 工具类
└── exception/       # 异常类
```

### 2. 命名规范
- **类名**: 使用 PascalCase，如 `UserService`
- **方法名**: 使用 camelCase，如 `findUserById`
- **常量**: 使用 UPPER_SNAKE_CASE，如 `MAX_RETRY_COUNT`
- **包名**: 使用小写，如 `com.indigo.user`

### 3. 异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }
}
```

### 4. 数据验证
```java
@Validated
@RestController
public class UserController {
    
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateRequest request) {
        // 处理请求
    }
}
```

### 5. 缓存使用
```java
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.getById(id);
    }
    
    @CacheEvict(value = "users", key = "#user.id")
    public void updateUser(User user) {
        userRepository.updateById(user);
    }
}
```

## 🤝 贡献指南

### 开发流程
1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 创建 Pull Request
5. 代码审查
6. 合并代码

### 代码规范
- 遵循 Java 编码规范
- 添加必要的注释
- 编写单元测试
- 更新相关文档

### 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

## 📄 许可证

本项目采用 [MIT 许可证](LICENSE)。

## 🆘 获取帮助

- 📖 [文档中心](./README.md)
- 🐛 [问题反馈](https://github.com/your-repo/issues)
- 💬 [讨论区](https://github.com/your-repo/discussions)
- 📧 [邮件支持](mailto:support@indigo.com)

---

**SynapseMOM** - 让制造运营管理更简单、更高效、更智能！ 