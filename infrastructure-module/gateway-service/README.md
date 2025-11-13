# Gateway Service

## 🎯 模块职责

Gateway服务专注于以下核心功能：

1. **Token 认证** - 处理用户认证 token 的传递和验证
2. **用户上下文传递** - 将用户信息传递给下游微服务
3. **网络限流** - 实现IP、用户、API等多维度的流量控制
4. **国际化支持** - 多语言请求头处理

**重要说明**：
- **网关职责**：只负责 Token 验证，确保请求携带有效的 token
- **权限检查**：由下游微服务通过 `@RequirePermission`、`@RequireRole` 等注解完成
- **架构优势**：网关轻量化，权限检查更灵活，支持细粒度控制

## 🏗️ 架构设计

### 模块职责划分

- **synapse-core**: 多语言国际化统一处理
- **synapse-cache**: 缓存处理，包含用户会话获取（支持二级缓存：Caffeine + Redis）
- **synapse-security**: Token 管理、权限服务、Gateway 签名
- **gateway**: Token 验证、用户上下文传递、网络限流

### 核心组件

```
gateway-service/
├── config/
│   ├── GatewayConfig.java          # 网关核心配置
│   ├── CorsConfiguration.java      # 跨域配置
│   └── FallbackRouterConfig.java   # 降级路由配置
├── filter/
│   ├── TokenAuthFilter.java        # Token 认证过滤器
│   ├── LocaleFilter.java           # 国际化过滤器
│   └── LoggingFilter.java          # 日志过滤器
└── handler/
    ├── GlobalErrorHandler.java     # 全局错误处理
    └── FallbackHandler.java        # 降级处理器
```

## 🔐 认证机制

### 认证流程

```
客户端请求
    ↓
TokenAuthFilter (Order: 100)
    ├─ 检查白名单 → 放行
    ├─ 提取 Token（Authorization Bearer / X-Auth-Token / 查询参数）
    ├─ 验证 Token (UserSessionService)
    ├─ 获取用户信息 (UserContext)
    ├─ 获取用户权限列表
    ├─ 编码用户上下文到请求头 (X-User-Context)
    ├─ 编码权限列表到请求头 (X-User-Permissions)
    └─ 生成 Gateway 签名 (X-Gateway-Signature)
    ↓
下游微服务
    ├─ UserContextInterceptor 解析请求头
    ├─ 验证 Gateway 签名
    ├─ 设置 UserContext 到 ThreadLocal
    └─ @RequirePermission / @RequireRole 注解进行权限检查
```

### Token 提取方式

支持三种方式传递 token：

```bash
# 方式1: Authorization Bearer（推荐）
curl -H "Authorization: Bearer your-token-here" http://localhost:8080/api/user/list

# 方式2: X-Auth-Token 头
curl -H "X-Auth-Token: your-token-here" http://localhost:8080/api/user/list

# 方式3: 查询参数
curl http://localhost:8080/api/user/list?token=your-token-here
```

### 用户上下文传递

Gateway 会将用户信息编码到请求头，传递给下游服务：

- `X-User-Context`: Base64 编码的用户上下文（UserContext JSON）
- `X-User-Permissions`: Base64 编码的用户权限列表
- `X-Gateway-Signature`: HMAC-SHA256 签名，防止请求被篡改
- `X-Gateway-Timestamp`: 时间戳，用于防重放攻击

下游服务的 `UserContextInterceptor` 会自动解析这些请求头，设置 `UserContext` 到 ThreadLocal。

### 权限验证（下游微服务）

权限检查由下游微服务通过注解完成：

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @RequirePermission("user:read")
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        // 需要 user:read 权限
        return Result.success(userService.list());
    }
    
    @RequireRole("admin")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable String id) {
        // 需要 admin 角色
        userService.delete(id);
        return Result.success();
    }
}
```

## 🔧 配置说明

### 基础配置

```yaml
synapse:
  security:
    # 是否启用安全模块
    enabled: true
    
    # 白名单路径配置（这些路径不需要认证）
    white-list:
      enabled: true
      paths:
        - /api/auth/**           # 认证相关接口
        - /api/iam/auth/**       # IAM 认证接口
        - /oauth2/**             # OAuth2相关接口
        - /actuator/**           # 监控端点
        - /swagger-ui/**         # Swagger UI
        - /v3/api-docs/**        # API文档
        - /webjars/**            # 静态资源
        - /favicon.ico           # 网站图标
        - /error                 # 错误页面
    
    # Token 配置
    token:
      prefix: "Bearer "             # Token 前缀（用于 Authorization 请求头）
      query-param: "token"           # Token 查询参数名
      header-name: "Authorization"  # Authorization 请求头名称
      x-auth-token-header: "X-Auth-Token"  # X-Auth-Token 请求头名称
    
    # Gateway 签名配置
    gateway-signature:
      enabled: true                    # 是否启用 Gateway 签名验证
      secret: "your-secret-key"        # Gateway 签名密钥（生产环境必须修改）
      validity-window: 300000          # 签名有效期窗口（毫秒），默认 5 分钟
      enable-context-passing: true     # 是否启用用户上下文传递
```

### 限流配置

```yaml
synapse:
  gateway:
    rate-limit:
      enabled: true
      default-limit: 100
      window-seconds: 60
      
      ip-rate-limit:
        enabled: true
        limit: 200
        window-seconds: 60
      
      user-rate-limit:
        enabled: true
        limit: 1000
        window-seconds: 60
```

### 第三方平台配置

```yaml
synapse:
  gateway:
    third-party:
      enabled: true
      paths:
        - /api/third-party/**
      api-key-header: X-API-Key
      timeout: 30
```

## 🚀 性能优化

### 混合方案（Gateway 上下文传递 + Caffeine 缓存）

Gateway 和微服务采用混合方案，实现性能和安全性的平衡：

1. **Gateway 端**：
   - 验证 Token（Redis）
   - 获取用户上下文和权限
   - 将用户上下文编码到请求头（`X-User-Context`）
   - 将权限列表编码到请求头（`X-User-Permissions`）
   - 生成并注入签名（`X-Gateway-Signature`、`X-Gateway-Timestamp`）

2. **微服务端**：
   - 优先从请求头解析用户上下文
   - 验证签名防篡改
   - 验签成功：直接使用上下文（无需 Redis 查询）
   - 验签失败/无上下文：降级到 Redis 查询
   - 结果写入 Caffeine 本地缓存（5分钟）

### 性能提升

- **减少 Redis 查询**：从每请求 4-5 次降到 0-1 次（命中 Header 时）
- **本地缓存命中**：Caffeine 缓存命中率可达 99%+，进一步减少 Redis 查询
- **总体性能提升**：响应时间减少 50-70%

## 🔄 路由配置

### 服务路由

- **IAM服务**: `/api/auth/**` → `lb://iam-service`
- **业务服务**: `/api/business/**` → `lb://business-service`
- **第三方API**: `/api/third-party/**` → `lb://third-party-service`

### 白名单

无需认证的路径：
- `/api/auth/**` - 认证相关接口
- `/api/iam/auth/**` - IAM 认证接口
- `/oauth2/**` - OAuth2认证
- `/actuator/**` - 监控端点
- `/swagger-ui/**` - API文档
- `/v3/api-docs/**` - OpenAPI文档

## 📝 响应格式

### 认证失败 (401 Unauthorized)

当 token 缺失或无效时，Gateway 返回：

```json
{
  "code": "SEC007",
  "message": "Token缺失",
  "data": null
}
```

或

```json
{
  "code": "SEC005",
  "message": "Token无效",
  "data": null
}
```

**注意**：权限不足的响应由下游微服务返回，Gateway 不进行权限检查。

## 🔍 日志说明

### TokenAuthFilter 日志

- `DEBUG`: 白名单路径跳过认证、用户上下文传递成功
- `INFO`: Token 验证成功、Gateway 签名生成成功
- `WARN`: Token 无效或用户会话不存在
- `ERROR`: Token 认证异常、签名生成失败

## ⚙️ 技术实现细节

### 1. Token 验证

使用 `UserSessionService.validateToken(token)` 验证 token：
- 如果 token 存在且有效，返回用户ID
- 如果 token 不存在或已过期，返回 null

### 2. 用户信息获取

使用 `UserSessionService.getUserSession(token)` 获取用户上下文：
- 返回 `UserContext` 对象，包含用户ID、用户名、租户ID、部门ID等信息
- 自动使用二级缓存（Caffeine + Redis）

### 3. 用户上下文传递

Gateway 使用 `UserContextCodec.encode()` 将 `UserContext` 编码为 Base64 字符串：
- 编码格式：`Base64(JSON(UserContext))`
- 请求头：`X-User-Context`

### 4. Gateway 签名

Gateway 使用 HMAC-SHA256 生成签名，防止请求被篡改：
- 签名内容：`HMAC-SHA256(secret, token + userId + timestamp)`
- 请求头：`X-Gateway-Signature`、`X-Gateway-Timestamp`
- 下游服务使用 `GatewaySignatureUtils.verifySignature()` 验证签名
- 时间戳验证：防止重放攻击（默认 5 分钟窗口）

## 🎯 注意事项

1. **缓存依赖**: 所有认证和权限信息都从 Redis 缓存中获取，确保 IAM 服务在登录时将用户信息存储到缓存中。

2. **白名单配置**: 登录接口等不需要认证的接口必须配置在白名单中。

3. **权限检查**: Gateway 不进行权限检查，权限检查由下游微服务通过注解完成。

4. **性能考虑**: 
   - Gateway 会将用户上下文传递到微服务，减少 Redis 查询
   - 微服务使用 Caffeine 本地缓存，进一步提升性能
   - 确保 Redis 连接池配置合理

5. **安全考虑**:
   - Gateway 签名密钥必须保密，生产环境必须修改
   - 签名有效期窗口防止重放攻击
   - 微服务验证签名，防止请求被篡改

6. **错误处理**: 认证失败返回 401，权限不足返回 403，确保客户端能够正确处理。

## 🔄 与 IAM 服务的集成

虽然 Gateway 不需要直接调用 IAM 服务，但需要确保：

1. **登录流程**: 用户在 IAM 服务登录后，IAM 服务需要：
   - 生成 token
   - 将用户信息存储到 Redis（通过 `UserSessionService`）
   - 将权限信息存储到 Redis（通过 `UserSessionService`）

2. **权限更新**: 如果用户权限发生变化，需要更新 Redis 中的权限信息。

3. **Token 过期**: Token 过期后，用户需要重新登录。

## 📊 监控和日志

### 监控端点

- `/actuator/health` - 健康检查
- `/actuator/metrics` - 指标监控
- `/actuator/gateway` - 网关状态
- `/actuator/prometheus` - Prometheus指标

### 日志配置

```yaml
logging:
  level:
    com.indigo.gateway: DEBUG
    org.springframework.cloud.gateway: DEBUG
```

## 🚀 部署配置

### 环境变量

- `REDIS_HOST`: Redis服务器地址
- `REDIS_PORT`: Redis端口
- `NACOS_SERVER_ADDR`: Nacos服务器地址
- `NACOS_NAMESPACE`: Nacos命名空间

### 依赖服务

- **Redis**: 用于限流和会话存储（支持二级缓存）
- **Nacos**: 用于服务发现和配置管理
- **各业务服务**: 需要注册到Nacos服务注册中心

## 📚 相关文档

- [synapse-security README](../../synapse-framework/synapse-security/README.md) - 安全模块文档
- [synapse-cache README](../../synapse-framework/synapse-cache/README.md) - 缓存模块文档（包含二级缓存说明）
- [国际化处理文档](./LOCALE_HANDLING.md) - 多语言请求头处理

## 🔧 维护说明

### 配置更新

1. 修改`application.yml`配置文件
2. 通过Nacos配置中心动态更新
3. 重启服务生效

### 性能优化

1. 调整限流配置以适应业务需求
2. 优化Redis连接池配置
3. 监控Gateway性能指标
4. 利用二级缓存（Caffeine + Redis）提升性能

### 故障排查

1. 检查日志文件中的错误信息
2. 验证Redis连接状态
3. 确认Nacos服务注册状态
4. 检查下游服务健康状态
5. 验证Gateway签名配置是否正确
