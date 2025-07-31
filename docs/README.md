# SynapseMOM 平台文档中心

## 📖 概述

SynapseMOM 是一个现代化的制造运营管理平台，采用微服务架构设计，提供完整的制造企业数字化解决方案。本文档中心包含了平台各个模块的详细技术文档和使用指南。

## 🏗️ 平台架构

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
└── Synapse Framework (框架集合)
    ├── synapse-core (核心框架)
    ├── synapse-cache (缓存框架)
    ├── synapse-security (安全框架)
    ├── synapse-events (事件框架)
    ├── synapse-databases (数据库框架)
    └── synapse-bom (依赖管理)
```

## 📚 文档目录

### 🛠️ [Synapse Framework 框架集合](./synapse-framework.md)

SynapseMOM 平台的核心框架集合，提供了一套完整的微服务开发基础设施。

**包含模块：**
- [核心框架](./core/README.md) - 基础工具类、异常处理、配置管理等
- [缓存框架](./cache/README.md) - 多级缓存、分布式锁、限流等
- [安全框架](./security/README.md) - 认证、授权、权限管理等
- [事件框架](./events/README.md) - 事件驱动架构支持
- [数据库框架](./databases/README.md) - 动态数据源、负载均衡等
- [依赖管理](./bom/README.md) - 统一版本管理和依赖管理

### 🔐 [身份认证与访问管理](./iam/README.md)

平台的用户认证、授权和权限管理系统。

**主要内容：**
- [架构设计](./iam/architecture/README.md) - IAM 系统架构设计
- [API 文档](./iam/api/README.md) - 认证授权 API 接口
- [数据库设计](./iam/database/README.md) - 用户权限数据库设计

### 🔄 [事务管理](./transaction/README.md)

分布式事务管理和协调机制。

**主要内容：**
- 事务状态跟踪
- 分布式事务协调
- 补偿机制
- 事务监控

### 🔐 [安全模块](./security/README.md)

平台安全相关的详细文档。

**主要内容：**
- [认证机制](./security/authentication/README.md) - 多种认证方式
- [JWT 集成](./security/authentication/jwt.md) - JWT 令牌管理
- [数据权限](./security/DATA_PERMISSION.md) - 数据权限控制

### 🔄 [事件驱动架构](./events/README.md)

基于事件驱动的微服务通信机制。

**主要内容：**
- [架构详情](./events/ARCHITECTURE_DETAILS.md) - 事件架构详细设计
- 事件发布和订阅
- 可靠消息传递
- 事务状态管理

## 🚀 快速开始

### 1. 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **Spring Boot**: 2.7+
- **Redis**: 6.0+
- **RocketMQ**: 4.9+
- **MySQL**: 8.0+

### 2. 项目结构

```
SynapseMOM/
├── business-module/          # 业务模块
├── foundation-module/        # 基础模块
├── infrastructure-module/    # 基础设施模块
├── synapse-framework/        # 框架集合
└── docs/                    # 文档中心
```

### 3. 快速启动

#### 添加依赖管理

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

#### 选择需要的模块

在子模块的 `pom.xml` 中添加：

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
    
    <!-- 数据库框架（可选） -->
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-databases</artifactId>
    </dependency>
</dependencies>
```

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

## 📋 模块选择指南

### 必需模块

- **synapse-core**: 所有服务都必须引入，提供基础工具类和异常处理

### 可选模块

- **synapse-cache**: 需要缓存功能时引入
- **synapse-security**: 需要认证授权时引入
- **synapse-events**: 需要事件驱动时引入
- **synapse-databases**: 需要多数据源时引入

## 🔧 配置说明

### Redis 配置

```yaml
spring:
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
```

### RocketMQ 配置

```yaml
synapse:
  events:
    rocketmq:
      name-server: localhost:9876
      producer-group: synapse-producer
      consumer-group: synapse-consumer
      topic-prefix: synapse-events
```

### 数据库配置

```yaml
synapse:
  databases:
    primary:
      url: jdbc:mysql://localhost:3306/primary_db
      username: root
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
    secondary:
      url: jdbc:mysql://localhost:3306/secondary_db
      username: root
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
```

## 🧪 测试指南

### 单元测试

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @Test
    void testMethod() {
        // 测试逻辑
    }
}
```

### 集成测试

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IntegrationTest {
    
    @Autowired
    private Service service;
    
    @Test
    void testIntegration() {
        // 集成测试逻辑
    }
}
```

## 📊 监控和运维

### 健康检查

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### 日志配置

```yaml
logging:
  level:
    com.indigo: DEBUG
    org.springframework: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 🔄 版本兼容性

| 版本 | Spring Boot | Java | 说明 |
|------|-------------|------|------|
| 1.0.0 | 2.7.x | 17+ | 初始版本 |
| 1.1.0 | 2.7.x | 17+ | 功能增强 |
| 2.0.0 | 3.0.x | 17+ | 重大升级 |

## 🚀 最佳实践

### 1. 项目结构

- 遵循微服务架构设计原则
- 合理划分模块职责
- 使用统一的命名规范

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

## 🤝 贡献指南

我们欢迎任何形式的贡献，包括但不限于：

- 提交问题和建议
- 改进文档
- 提交代码修改
- 分享使用经验

### 贡献流程

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

### 开发环境

- JDK 17+
- Maven 3.6+
- Spring Boot 2.7+
- Redis 6.0+
- RocketMQ 4.9+

## 📞 联系方式

- **维护者**: 史偕成
- **邮箱**: shixiecheng@indigobyte.com
- **项目地址**: https://github.com/indigobyte/synapse-mom

## 📄 许可证

本项目采用 MIT 许可证。

---

**最后更新**: 2025-07-20  
**版本**: 1.0.0  
**维护者**: 史偕成 