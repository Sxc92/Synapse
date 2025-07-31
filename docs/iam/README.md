# IAM (Identity and Access Management) 服务

## 概述

IAM（Identity and Access Management）服务是 SynapseMOM 平台的统一身份认证与访问控制核心模块，提供用户、组织、权限、认证、审计等全方位的身份安全能力。其设计遵循现代微服务三层架构，并通过事件机制实现业务解耦和扩展。

---

## ✨ 主要功能

- **用户身份管理**：注册、激活、禁用、注销、扩展属性、多租户隔离
- **多方式认证**：用户名密码、OAuth2、JWT、单点登录（SSO）
- **权限控制**：RBAC 权限模型、API/数据/功能级权限、动态策略、权限审计
- **组织架构管理**：部门、岗位、组织关系、用户-组织-角色关联
- **安全策略**：密码复杂度、登录失败处理、账户锁定、会话超时
- **审计与监控**：权限变更、访问日志、操作行为分析
- **事件驱动**：用户、权限、组织等变更事件解耦业务扩展

---

## 🏗️ 推荐包结构（分层+事件解耦+接口复用）

```
com.yourorg.iam
├── controller/    # 只做接口转发/参数校验，不写业务
├── service/       # 业务聚合/编排/领域逻辑，事件发布
├── repository/    # 数据访问层，MyBatis-Plus Mapper/IService/Entity
│   └── entity/    # 仅在 repository/service 层内部使用的数据库实体（DO/Entity）
├── config/        # Spring 配置类、属性类
├── event/         # 事件对象、发布器、监听器、处理器
│   ├── model/
│   ├── publisher/
│   ├── listener/
│   └── handler/
└── util/          # 工具类（如有）

# iam-api 模块（供其他模块依赖）
com.yourorg.iam.api
└── model/
    ├── dto/       # 数据传输对象 DTO
    ├── vo/        # 视图对象 VO
    └── query/     # 查询对象 Query
```

### 包职责说明
- **controller/**：REST/RPC 接口定义，参数校验，调用 service，不写业务逻辑。
- **service/**：业务聚合、事务、事件发布，依赖 repository，不直接操作数据库。
- **repository/entity/**：数据库实体（DO/Entity），只在 repository/service 层内部使用，不对外暴露。
- **config/**：Spring 配置、自动配置、属性类。
- **event/**：事件对象、发布器、监听器、处理器，解耦业务扩展。
- **util/**：通用工具类。
- **iam-api/model/dto|vo|query/**：DTO/VO/Query 只在 iam-api 层定义，供 controller 层参数/返回和其他模块依赖复用。

---

## ⚙️ 分层解耦与复用原则
- **Entity/DO 只在 repository/service 层内部使用**，不对外暴露。
- **DTO/VO/Query 只在 iam-api 层定义**，controller 层参数/返回类型全部用 iam-api 下的对象，service 层做对象转换。
- **其他模块跨服务调用时，只依赖 iam-api**，不依赖 iam-core。
- **核心业务逻辑只依赖 entity/DO，不直接依赖 DTO/VO/Query。**

---

## ⚙️ 异常处理
- 统一复用 `synapse-core` 的异常体系（如 `BusinessException`、`AssertException`），不在本模块重复定义。
- controller 层只捕获/抛出统一异常，service/repository 层只抛出。
- 全局异常处理由 synapse-core 的 `@ControllerAdvice` 负责。

---

## 📚 文档导航

- [架构设计](./architecture/README.md)
- [数据模型设计](./database/README.md)
- [API 接口设计](./api/README.md)

---

## 🚀 开发与运维指南

- **环境要求**：JDK 17+，Spring Boot 2.7+，MySQL 8+，Redis 6+，Nacos，Seata
- **服务注册/配置**：Nacos
- **分布式事务**：Seata
- **缓存/会话**：Redis
- **监控与告警**：暴露健康检查、认证统计、权限变更等指标

---

## 🧩 典型开发流程

1. **接口开发**：controller 层定义 API，参数/返回类型全部用 iam-api 下的 DTO/VO/Query
2. **业务实现**：service 层聚合业务逻辑，必要时发布事件，内部用 entity/DO
3. **数据访问**：repository 层操作数据库，集成 MyBatis-Plus，内部用 entity/DO
4. **事件扩展**：event 包发布/监听业务事件，实现解耦
5. **对象转换**：service 层负责 entity <-> DTO/VO/Query 的转换
6. **异常处理**：全程抛出统一异常，由 synapse-core 统一处理

---

## 🏷️ 版本规划

- **当前版本 (1.0.0)**：基础用户管理、RBAC 权限、组织架构、Sa-Token 认证
- **后续规划**：OAuth2 支持、ABAC 权限、多租户增强、审计日志分析

---

## 📝 最佳实践
- 只在 controller 层做参数校验和响应封装，参数/返回类型全部用 iam-api 下的对象
- 业务聚合、事件发布全部在 service 层，内部用 entity/DO
- 数据库操作全部在 repository 层，禁止 service 直接操作 Mapper
- 事件对象、发布、监听、处理分包，便于解耦和扩展
- 统一异常体系，便于全局日志和监控
- 跨模块调用只依赖 iam-api，避免核心实现泄漏

---

## 🧹 历史/无用文档清理
- 已删除空的 deployment 目录
- 其余文档均为有效文档，保留

---

**最后更新：2025-07-20**  
**维护者：史偕成** 