# SaToken 安全模块重构方案

## 问题分析

您提出的问题非常有意义：

> "这里是不是应该放在框架内。每个业务模块都有可能需要调用 还是单独起一个项目存放吧 我发现 cache 里面引用了core"

经过对项目结构的分析，我发现：

### 1. 当前的问题
- **重复配置**: SaToken 配置散布在多个模块中
- **依赖混乱**: IAM服务、网关服务都各自实现了SaToken配置
- **维护困难**: 相同的认证逻辑在不同地方重复实现
- **框架层级**: cache依赖core，但安全配置却在业务层

### 2. 使用范围分析
根据代码搜索结果，SaToken在以下模块中都有使用：
- `foundation-module/iam-service` - 核心认证配置
- `infrastructure-module/gateway-service` - 网关认证过滤器
- `synapse-framework/synapse-core` - 已引入sa-token-core
- `synapse-framework/synapse-databases` - 使用StpUtil

## 解决方案

### ✅ 推荐方案：创建 `synapse-security` 框架模块

```
synapse-framework/
├── synapse-core/          # 基础核心功能
├── synapse-cache/         # 缓存功能 (依赖 core)
├── synapse-databases/     # 数据库功能
└── synapse-security/      # 🆕 统一安全认证模块 (依赖 core + cache)
```

### 依赖关系优化

```
业务模块 (iam-service, gateway-service)
    ↓
synapse-security (统一安全配置)
    ↓
synapse-cache (用户会话管理)
    ↓
synapse-core (基础工具类)
```

## 实施方案

### 1. 新建框架模块

已创建 `synapse-framework/synapse-security/` 模块，包含：

- **自动配置类**: `SaTokenAutoConfiguration`
- **Redis DAO**: `SaTokenRedisDao` 
- **权限服务**: `SaTokenPermissionService`
- **自动装配**: Spring Boot AutoConfiguration

### 2. 核心特性

```java
@AutoConfiguration
@ConditionalOnClass({SaTokenDao.class, UserSessionService.class})
public class SaTokenAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public SaOAuth2Config saOAuth2Config() {
        // 提供默认OAuth2.0配置，业务模块可覆盖
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public SaTokenDao saTokenDao(UserSessionService userSessionService, RedisService redisService) {
        // 统一的Redis存储实现
    }
    
    @Bean
    @ConditionalOnMissingBean(StpInterface.class)
    public StpInterface stpInterface(UserSessionService userSessionService) {
        // 统一的权限验证逻辑
    }
}
```

### 3. 使用方式

#### 对于业务模块

```xml
<!-- 替换原有的多个SaToken依赖 -->
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-security</artifactId>
</dependency>
```

#### 自动配置生效

```yaml
# application.yml - 只需要配置参数，无需额外代码
sa-token:
  token-name: satoken
  timeout: 2592000
  activity-timeout: 1800
  is-concurrent: true
```

#### 业务代码简化

```java
// 原来需要自己实现的认证逻辑
@RestController
public class UserController {
    
    @GetMapping("/user/info")
    @SaCheckLogin  // 🎯 直接使用注解，无需额外配置
    public UserInfo getUserInfo() {
        return userService.getCurrentUser();
    }
    
    @PostMapping("/admin/users")
    @SaCheckRole("admin")  // 🎯 角色检查自动生效
    public void deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
    }
}
```

## 优势对比

### ❌ 原有方案的问题
- 每个模块都要实现SaTokenConfiguration
- 依赖关系混乱，维护困难
- 配置重复，容易出现不一致

### ✅ 新方案的优势
- **统一管理**: 所有安全配置集中在框架层
- **开箱即用**: 业务模块只需引入依赖即可
- **灵活扩展**: 支持业务模块覆盖默认配置
- **依赖清晰**: 框架层次分明，职责明确

## 迁移步骤

### 1. 框架层
- ✅ 创建 `synapse-security` 模块
- ✅ 实现自动配置类
- ✅ 迁移通用的SaToken逻辑
- ✅ 迁移UserContextHolder工具类
- ✅ 创建使用示例和文档

### 2. 业务层
- ✅ 更新IAM服务依赖
- ✅ 更新网关服务依赖
- ✅ 迁移UserContextHolder到security模块
- ⏳ 删除重复的配置类
- ⏳ 测试功能完整性

### 3. 验证测试
- ⏳ 用户登录认证
- ⏳ 权限注解验证
- ⏳ OAuth2.0流程
- ⏳ 网关统一认证

## 最佳实践建议

### 1. 不要单独创建项目

**建议放在框架内** 而不是单独项目，因为：
- 与现有缓存和核心模块紧密集成
- 便于版本管理和依赖控制
- 符合框架模块化设计原则

### 2. 保持扩展性

```java
// 业务模块可以覆盖默认配置
@Configuration
public class CustomSecurityConfig {
    
    @Bean
    @Primary
    public SaOAuth2Config customOAuth2Config() {
        // 自定义登录逻辑
        return new CustomOAuth2Config();
    }
}
```

### 3. 遵循框架分层

```
synapse-core (基础)
    ↓
synapse-cache (缓存)
    ↓
synapse-security (安全) 
    ↓
synapse-databases (数据库，可选)
```

## 总结

您的观察非常准确！将SaToken配置放在框架内是最佳选择，这样可以：

1. **避免重复**: 各业务模块无需重复实现认证逻辑
2. **统一管理**: 框架层提供标准的安全能力
3. **依赖清晰**: 遵循 core → cache → security 的分层架构
4. **易于维护**: 安全策略的升级只需修改框架层

这个重构方案既解决了代码重复问题，又保持了良好的架构设计，是一个很好的优化方向！ 