# SynapseMOM Platform

## 项目概述

SynapseMOM Platform 是一个基于 Spring Boot 3.2.3 和 Spring Cloud 2023.0.0 的现代化微服务架构平台，专注于提供高性能、可扩展的企业级解决方案。

## 技术栈

### 核心框架
- **Spring Boot**: 3.2.3
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Alibaba**: 2023.0.1.0
- **Java**: 17+

### 数据库与缓存
- **MySQL**: 8.0+
- **PostgreSQL**: 12+
- **Redis**: 6.0+
- **MyBatis-Plus**: 3.5.4+

### 消息队列
- **RocketMQ**: 4.9+

### 服务治理
- **Nacos**: 2.0+ (服务发现与配置中心)
- **Seata**: 2.0+ (分布式事务)

### 开发工具
- **Lombok**: 代码简化
- **MapStruct**: 对象映射
- **Logback**: 日志框架

## 项目结构

```
SynapseMOM/
├── business-module/          # 业务模块
├── foundation-module/        # 基础服务模块
│   ├── iam-service/         # 身份认证与授权服务
│   │   ├── iam-api/         # API接口和DTO定义
│   │   ├── iam-core/        # 核心业务实现
│   │   └── iam-client/      # 客户端调用代码
│   └── meta-data-service/   # 元数据服务
├── infrastructure-module/   # 基础设施模块
│   ├── gateway-service/     # 网关服务
│   ├── audit-service/       # 审计服务
│   ├── notification-service/ # 通知服务
│   ├── workflow-service/    # 工作流服务
│   ├── schedule-service/    # 调度服务
│   ├── integration-service/ # 集成服务
│   └── license-service/     # 许可证服务
├── synapse-framework/       # 核心框架
│   ├── synapse-core/        # 核心工具类
│   ├── synapse-databases/   # 数据库框架
│   ├── synapse-cache/       # 缓存框架
│   ├── synapse-events/      # 事件框架
│   └── synapse-security/    # 安全框架
└── docs/                    # 项目文档
```

## 核心特性

### 1. 无 ServiceImpl 架构
- 基于 `@AutoRepository` 注解的自动代理生成
- 继承 MyBatis-Plus `IService` 接口
- 消除传统 ServiceImpl 样板代码

### 2. 优雅的查询条件构建
- **Builder 模式**: 使用 Lombok `@Builder` 注解
- **查询条件对象**: 支持链式调用
- **静态工厂方法**: 简化常用查询
- **@QueryCondition 注解**: 自动构建查询条件

### 3. 模块化设计
- **API 模块**: 对外暴露的接口和 DTO
- **Core 模块**: 核心业务实现
- **Client 模块**: 客户端调用代码

### 4. 数据库框架
- **动态数据源**: 支持多数据源切换
- **自动分页**: 统一的分页处理
- **复杂查询**: 支持 `@Select` 注解
- **事务管理**: 集成 Seata 分布式事务

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.0+

### 启动步骤

1. **启动基础设施服务**
   ```bash
   # 启动 Nacos
   # 启动 MySQL
   # 启动 Redis
   ```

2. **启动网关服务**
   ```bash
   cd infrastructure-module/gateway-service
   mvn spring-boot:run
   ```

3. **启动基础服务**
   ```bash
   cd foundation-module/iam-service/iam-core
   mvn spring-boot:run
   ```

## 使用示例

### 1. 基础 CRUD 操作

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 自动继承 IService 的所有方法
    // 无需编写 ServiceImpl
}
```

### 2. 查询条件构建

```java
// 方式1: Builder 模式
TenantQueryDTO query = TenantQueryDTO.byStatus(1);
TenantQueryDTO query = TenantQueryDTO.byKeyword("test");

// 方式2: 链式调用
TenantQueryCondition condition = TenantQueryCondition.builder()
    .status(1)
    .description("测试")
    .build();

// 方式3: 前端传参
findTenants(frontendQueryDTO);
```

### 3. 复杂查询

```java
@Mapper
public interface TenantMapper extends BaseMapper<IamTenant> {
    @Select("SELECT t.*, u.username as creator_name FROM iam_tenant t " +
            "LEFT JOIN iam_user u ON t.creator_id = u.id " +
            "WHERE t.status = #{status}")
    List<TenantWithCreatorDTO> findTenantsWithCreator(@Param("status") String status);
}
```

## 配置说明

### 数据库配置
```yaml
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
```

### MyBatis-Plus 配置
```yaml
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

## 开发规范

### 1. 模块化原则
- DTO 统一放在 `api` 模块的 `model/dto` 目录
- 业务逻辑放在 `core` 模块
- 客户端代码放在 `client` 模块

### 2. 命名规范
- 实体类: `IamTenant`
- DTO: `TenantQueryDTO`, `TenantQueryCondition`
- Repository: `TenantsRepository`
- Mapper: `TenantMapper`
- Service: `TenantService`
- Controller: `TenantController`

### 3. 注解使用
- `@AutoRepository`: 自动生成 Repository 代理
- `@QueryCondition`: 自动构建查询条件
- `@Builder`: 使用 Lombok Builder 模式
- `@Select`: 复杂 SQL 查询

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

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目维护者: 史偕成
- 邮箱: [your-email@example.com]
- 项目地址: [https://github.com/your-username/SynapseMOM] 