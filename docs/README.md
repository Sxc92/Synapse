# SynapseMOM 制造运营管理平台

## 📖 平台概述

SynapseMOM 是一个基于 Spring Boot 3.x 的现代化制造运营管理平台，提供完整的微服务架构解决方案。平台集成了身份认证、数据管理、事件驱动、缓存、安全、国际化等核心功能模块，为企业级应用提供开箱即用的技术能力。

**核心优势**：
- 🚀 **模块化架构**：清晰的模块划分，按需引入
- 🔐 **统一认证**：基于自研 TokenService 的认证机制，支持滑动过期、自动续期
- 🗄️ **数据访问**：强大的 BaseRepository，支持 VO 映射、多表关联查询
- ⚡ **二级缓存**：Caffeine 本地缓存 + Redis 分布式缓存，自动降级
- 🌍 **国际化**：完整的 i18n 支持，错误消息多语言
- 🛡️ **权限控制**：基于注解的细粒度权限管理

## 🏗️ 平台架构

```
SynapseMOM Platform
├── Business Modules (业务模块)
│   └── (待开发业务服务)
│
├── Foundation Modules (基础模块)
│   ├── IAM Service (身份认证服务) ✅
│   │   ├── 用户管理、角色权限、菜单资源
│   │   ├── RBAC 权限模型、Token 认证
│   │   └── [文档](../foundation-module/iam-service/README.md)
│   ├── MDM Service (元数据服务) ✅
│   │   ├── 国家管理、语言管理
│   │   ├── 国际化内容管理
│   │   └── 基础数据维护
│   └── I18N Service (国际化服务) ✅
│       ├── 多语言支持
│       ├── 消息资源管理
│       └── 动态语言切换
│
├── Infrastructure Modules (基础设施模块)
│   ├── Gateway Service (网关服务) ✅
│   │   ├── Token 认证、用户上下文传递
│   │   ├── 网络限流、国际化支持
│   │   └── [文档](../infrastructure-module/gateway-service/README.md)
│   ├── Audit Service (审计服务) 🚧
│   ├── Notification Service (通知服务) 🚧
│   ├── Schedule Service (调度服务) 🚧
│   ├── Workflow Service (工作流服务) 🚧
│   ├── Integration Service (集成服务) 🚧
│   └── License Service (许可证服务) 🚧
│
└── Synapse Framework (框架集合)
    ├── synapse-core (核心框架)
    ├── synapse-cache (缓存框架)
    ├── synapse-security (安全框架)
    ├── synapse-events (事件框架)
    ├── synapse-databases (数据库框架)
    ├── synapse-i18n (国际化框架)
    └── synapse-bom (依赖管理)
```

**图例说明**：
- ✅ 已实现并可用
- 🚧 规划中或开发中

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+ (可选)
- **Nacos**: 2.0+ (可选)

### ⚡ 最速体验 (3分钟)

**🔥 推荐**: 直接使用 `synapse-databases` 框架，体验强大的 BaseRepository 功能！

```xml
<!-- 1. 在你的项目中添加依赖 -->
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

<dependencies>
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-databases</artifactId>
    </dependency>
</dependencies>
```

```yaml
# 2. 最少配置 (application.yml)
synapse:
  datasource:
    dynamic-data-source:
      primary: master
      datasource:
        master:
          type: MYSQL
          host: localhost
          port: 3306
          database: your_database
          username: root
          password: your_password
          pool-type: HIKARI
```

```java
// 3. 定义 Repository
@Repository
public interface CountryRepository extends BaseRepository<Country> {
    // ✅ 使用 @QueryCondition 自动构建查询条件
    @QueryCondition
    List<CountryVO> findByCode(String code);
    
    // ✅ 分页查询，自动映射到 VO
    PageResult<CountryVO> pageCountries(CountryPageDTO pageDTO);
}

// 4. 立即使用强大功能
@Service
public class CountryService {
    @Autowired private CountryRepository countryRepository;
    
    // ✅ 唯一性验证
    public boolean isUnique(Country country) { 
        return !countryRepository.exists(new LambdaQueryWrapper<Country>()
            .eq(Country::getCode, country.getCode())
            .ne(country.getId() != null, Country::getId, country.getId())); 
    }
    
    // ✅ 条件查询，自动映射到 VO
    public List<CountryVO> findActive() {
        CountryQueryDTO query = CountryQueryDTO.builder()
            .status(1)
            .build();
        return countryRepository.listWithDTO(query, CountryVO.class);
    }
    
    // ✅ 分页查询，自动映射到 VO
    public PageResult<CountryVO> getCountries(CountryPageDTO pageDTO) {
        return countryRepository.pageWithDTO(pageDTO, CountryVO.class);
    }
}
```

### 🌟 Synapse Framework v1.0.0 特性

| 模块 | 版本 | 亮点特性 | 状态 |
|------|------|----------|------|
| **synapse-databases** | v1.0.0 | ✅ BaseRepository、VO 映射、动态数据源、查询构建器 | 🚀 生产可用 |
| **synapse-cache** | v1.0.0 | ✅ 二级缓存（Caffeine + Redis）、分布式锁、会话管理 | 🚀 生产可用 |
| **synapse-security** | v1.0.0 | ✅ 自研 TokenService、权限控制、滑动过期、自动续期 | 🚀 生产可用 |
| **synapse-events** | v1.0.0 | ✅ 异步事件处理、事务事件、可靠性投递 | 🚀 生产可用 |
| **synapse-i18n** | v1.0.0 | ✅ 国际化支持、多语言环境、动态切换、错误消息国际化 | 🚀 生产可用 |
| **synapse-core** | v1.0.0 | ✅ 统一响应、异常处理（Ex.throwEx）、工具类 | 🚀 生产可用 |

### 🔧 完整平台部署

如需部署完整平台，请参考：[完整部署指南](./getting-started.md#方案b完整平台部署)

详细步骤请参考：[快速开始指南](./getting-started.md) | [故障排除指南](./databases/README.md#故障排除指南)

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

### 🔐 [身份认证 (IAM)](../foundation-module/iam-service/README.md)
- ✅ 用户管理：用户新增、修改、删除、查询
- ✅ 角色管理：角色配置、角色权限分配
- ✅ 菜单管理：菜单树形结构、菜单权限控制
- ✅ 资源管理：资源权限编码、细粒度权限控制
- ✅ 系统管理：多系统支持、系统权限隔离
- ✅ RBAC 权限模型：基于角色的访问控制
- ✅ Token 认证：基于自研 TokenService 的认证机制
- ✅ 权限缓存：Redis 缓存用户会话和权限数据

### 🌍 [元数据服务 (MDM)](../foundation-module/mdm-service/)
- ✅ 国家管理：国家信息维护
- ✅ 语言管理：语言信息维护
- ✅ 国际化内容：多语言内容管理
- ✅ 基础数据：系统基础数据维护

### 🌐 [国际化服务 (I18N)](../foundation-module/i18n-service/)
- ✅ 多语言支持：动态语言切换
- ✅ 消息资源管理：国际化消息资源
- ✅ 错误消息国际化：异常消息多语言

### 🚪 [网关服务 (Gateway)](../infrastructure-module/gateway-service/README.md)
- ✅ Token 认证：统一 Token 验证
- ✅ 用户上下文传递：将用户信息传递给下游服务
- ✅ 网络限流：IP、用户、API 等多维度限流
- ✅ 国际化支持：多语言请求头处理
- ✅ Gateway 签名：防止请求被篡改

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
synapse:
  datasource:
    dynamic-data-source:
      primary: master
      datasource:
        master:
          type: MYSQL
          host: localhost
          port: 3306
          database: your_database
          username: your_username
          password: your_password
          pool-type: HIKARI
```

#### 2. 缓存配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password
      database: 0
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0

synapse:
  cache:
    enabled: true
    default-strategy: "LOCAL_AND_REDIS"
    two-level:
      enabled: true
      local:
        enabled: true
        maximum-size: 1000
      redis:
        enabled: true
        default-ttl: 3600
```

#### 3. 安全配置
```yaml
synapse:
  security:
    enabled: true
    mode: STRICT  # STRICT(严格)、PERMISSIVE(宽松)、DISABLED(关闭)
    token:
      timeout: 7200  # Token 过期时间（秒），默认 2 小时
      enable-sliding-expiration: true  # 启用滑动过期（自动刷新）
      refresh-threshold: 600  # 刷新阈值（秒），当 token 剩余时间少于 10 分钟时自动续期
      renewal-duration: 7200  # 续期时长（秒），刷新时将过期时间延长到 2 小时
    white-list:
      enabled: true
      paths:
        - /api/auth/login
        - /actuator/**
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
@Service
public class UserService {
    
    public UserVO getUser(String id) {
        if (id == null || id.isEmpty()) {
            // 使用 Ex.throwEx() 统一异常处理
            Ex.throwEx(StandardErrorCode.USER_ID_REQUIRED, "用户ID不能为空");
        }
        
        User user = userRepository.getById(id);
        if (user == null) {
            Ex.throwEx(StandardErrorCode.USER_NOT_FOUND, id);
        }
        
        return VoMapper.toVO(user, UserVO.class);
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
    
    @Autowired
    private TwoLevelCacheService cacheService;
    
    public UserVO getUser(String id) {
        String cacheKey = "user:" + id;
        
        return cacheService.get(
            cacheKey,
            () -> {
                User user = userRepository.getById(id);
                return VoMapper.toVO(user, UserVO.class);
            },
            UserVO.class,
            3600 // TTL: 1小时
        );
    }
    
    public void updateUser(String id, UpdateUserDTO dto) {
        User user = userRepository.getById(id);
        user.setUsername(dto.getUsername());
        userRepository.updateById(user);
        
        // 删除缓存
        cacheService.delete("user:" + id);
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

---

**🎉 最新更新**: 2025-01-14 v1.0.0  
**🚀 Synapse Framework** - 让制造运营管理更简单、更高效、更智能！  
**🔧 BaseRepository** - 强大的 Repository 接口，支持 VO 映射、多表关联查询！  
**⚡ TokenService** - 自研 Token 认证，支持滑动过期、自动续期！  
**🌍 I18N** - 完整的国际化支持，错误消息多语言！

---

📖 **完整文档**: [Synapse Databases 框架](./databases/README.md)  
🐛 **问题反馈**: [GitHub Issues](https://github.com/your-repo/issues)  
💬 **技术讨论**: [GitHub Discussions](https://github.com/your-repo/discussions) 