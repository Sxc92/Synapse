# Synapse Framework 框架集合

## 概述

Synapse Framework 是 SynapseMOM 平台的核心框架集合，提供了一套完整的微服务开发基础设施。框架采用模块化设计，支持按需引入，大幅提升开发效率。

## 框架模块

### 🎯 synapse-core (核心框架)

**功能**: 提供基础工具类、异常处理、配置管理等核心功能

**主要特性**:
- 统一异常处理机制
- 通用工具类集合
- 国际化支持
- 配置管理工具

**使用示例**:
```java
// 统一返回结果
Result.success(data);
Result.error("操作失败");

// 异常处理
throw new BusinessException("业务异常");
```

---

### 🗄️ synapse-databases (数据库框架) - **🆕 重大改进**

**功能**: 提供无 ServiceImpl 的数据库操作框架，支持动态数据源、统一分页查询

**主要特性**:
- **无 ServiceImpl 架构**: 基于 `@AutoRepository` 注解自动生成代理
- **优雅的查询条件构建**: 支持 Builder 模式和链式调用
- **统一分页查询**: 基于 `PageDTO` 和 `PageResult`
- **复杂查询支持**: 集成 MyBatis-Plus `@Select` 注解
- **动态数据源**: 支持多数据源切换
- **类型安全**: 移除 Map 类型参数，提升编译时检查

**核心注解**:
- `@AutoRepository`: 自动生成 Repository 代理
- `@QueryCondition`: 自动构建查询条件
- `@DataSource`: 数据源切换

**使用示例**:

```java
// 1. 定义 Repository 接口（无需实现类）
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 自动继承 IService 的所有方法
}

// 2. 查询条件构建
// 方式1: Builder 模式
TenantQueryDTO query = TenantQueryDTO.byStatus(1);
TenantQueryDTO query = TenantQueryDTO.byKeyword("test");

// 方式2: 链式调用
TenantQueryCondition condition = TenantQueryCondition.builder()
    .status(1)
    .description("测试")
    .build();

// 3. 复杂查询
@Mapper
public interface TenantMapper extends BaseMapper<IamTenant> {
    @Select("SELECT t.*, u.username as creator_name FROM iam_tenant t " +
            "LEFT JOIN iam_user u ON t.creator_id = u.id " +
            "WHERE t.status = #{status}")
    List<TenantWithCreatorDTO> findTenantsWithCreator(@Param("status") String status);
}
```

**DTO 设计**:
```java
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class TenantQueryDTO extends PageDTO {
    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String code;
    
    @QueryCondition(type = QueryCondition.QueryType.EQ)
    private Integer status;
    
    // 静态工厂方法
    public static TenantQueryDTO byStatus(Integer status) {
        return TenantQueryDTO.builder().status(status).build();
    }
}
```

---

### 🚀 synapse-cache (缓存框架)

**功能**: 提供多级缓存、分布式锁、限流等功能

**主要特性**:
- 多级缓存支持 (本地缓存 + Redis)
- 分布式锁实现
- 限流器
- 缓存注解支持

**使用示例**:
```java
@Cacheable(value = "user", key = "#id")
public User getUserById(Long id) {
    return userMapper.selectById(id);
}

@DistributedLock(key = "order:lock:#{#orderId}")
public void processOrder(String orderId) {
    // 业务逻辑
}
```

---

### 🔐 synapse-security (安全框架)

**功能**: 提供认证、授权、权限管理等安全功能

**主要特性**:
- JWT 令牌管理
- 多种认证策略
- 权限控制
- 数据权限

**使用示例**:
```java
@RequiresPermissions("user:read")
public List<User> getUsers() {
    return userService.list();
}

@RequiresRoles("admin")
public void deleteUser(Long id) {
    userService.removeById(id);
}
```

---

### 🔄 synapse-events (事件框架)

**功能**: 提供事件驱动架构支持

**主要特性**:
- 事件发布和订阅
- 可靠消息传递
- 事务状态管理
- 事件溯源

**使用示例**:
```java
@EventListener
public void handleUserCreated(UserCreatedEvent event) {
    // 处理用户创建事件
}

@TransactionalEventListener
public void handleOrderPaid(OrderPaidEvent event) {
    // 处理订单支付事件
}
```

## 快速集成

### 1. 添加依赖管理

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

在子模块的 `pom.xml` 中添加：

```xml
<dependencies>
    <!-- 核心框架（必需） -->
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-core</artifactId>
    </dependency>
    
    <!-- 数据库框架（强烈推荐） -->
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-databases</artifactId>
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
</dependencies>
```

## 配置说明

### 基础配置

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

### 数据库框架配置

```yaml
# 动态数据源配置
spring:
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/synapse_mom
          username: root
          password: password
          driver-class-name: com.mysql.cj.jdbc.Driver

# MyBatis-Plus 配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: ASSIGN_ID
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

## 最佳实践

### 1. 模块化设计

- **API 模块**: 对外暴露的接口和 DTO
- **Core 模块**: 核心业务实现
- **Client 模块**: 客户端调用代码

### 2. 数据库操作最佳实践

#### Repository 层设计
- 使用 `@AutoRepository` 自动生成实现，避免手写 ServiceImpl
- 单表操作使用 MyBatis-Plus 方法，复杂查询使用 SQL 注解
- 统一使用 `PageDTO` 作为分页参数，`PageResult` 作为返回类型

#### 查询条件构建
- 简单查询使用静态工厂方法：`TenantQueryDTO.byStatus(1)`
- 复杂查询使用 Builder 模式：`TenantQueryDTO.builder().status(1).code("T001").build()`
- 动态查询使用 `@QueryCondition` 注解

#### SQL 注解使用
- 使用文本块（"""）编写多行 SQL，提高可读性
- 合理使用参数绑定，避免 SQL 注入
- 复杂查询添加适当的注释

### 3. 性能优化

- 合理使用分页查询，避免全表扫描
- 使用索引优化查询性能
- 避免 N+1 查询问题
- 数据库层面排序，避免内存分页

### 4. 异常处理

- 使用 `Result<T>` 统一返回格式
- 合理使用业务异常和系统异常
- 记录详细的错误日志

## 版本兼容性

| 版本 | Spring Boot | Spring Cloud | Java | 说明 |
|------|-------------|--------------|------|------|
| 1.0.0 | 3.2.3 | 2023.0.0 | 17+ | **🆕 数据库框架重大改进** |

### 🆕 v1.0.0 主要改进 (2024-12-19)

- **无 ServiceImpl 架构**: 基于 `@AutoRepository` 注解自动生成代理
- **优雅的查询条件构建**: 支持 Builder 模式和链式调用
- **DTO 模块化重构**: 统一放在 `api` 模块的 `model/dto` 目录
- **类型安全优化**: 移除 Map 类型参数，提升编译时检查
- **统一分页查询**: 基于 `PageDTO` 和 `PageResult`
- **Lombok Builder 集成**: 简化对象构建过程

## 更新日志

### 2024-12-19
- ✅ 完成 DTO 模块化重构
- ✅ 实现 Lombok Builder 模式
- ✅ 优化查询条件构建
- ✅ 移除 Map 类型参数，提升类型安全
- ✅ 统一分页返回类型为 `PageResult`

### 2024-12-18
- ✅ 实现无 ServiceImpl 架构
- ✅ 集成 MyBatis-Plus 原生 `@Select` 注解
- ✅ 支持复杂多表查询
- ✅ 实现动态数据源切换

## 技术支持

- **维护者**: 史偕成
- **邮箱**: [your-email@example.com]
- **项目地址**: [https://github.com/your-username/SynapseMOM]

---

**最后更新**: 2024-12-19  
**版本**: 1.0.0  
**维护者**: 史偕成 