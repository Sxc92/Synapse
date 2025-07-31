# Gateway Service

## 🎯 模块职责

Gateway服务专注于以下核心功能：

1. **JWT Token传递** - 处理用户认证token的传递和验证
2. **权限认证** - 支持内部用户和第三方平台的权限认证
3. **网络限流** - 实现IP、用户、API等多维度的流量控制

## 🏗️ 架构设计

### 模块职责划分

- **synapse-core**: 多语言国际化统一处理
- **synapse-cache**: 缓存处理，包含用户会话获取
- **synapse-security**: 权限处理和Token续期
- **gateway**: JWT token传递、权限认证、网络限流

### 核心组件

```
gateway-service/
├── config/
│   ├── GatewayConfig.java          # 网关核心配置
│   ├── CorsConfiguration.java      # 跨域配置
│   ├── JwtConfig.java              # JWT配置
│   └── RateLimiterConfig.java      # 限流配置
├── filter/
│   ├── SaTokenAuthFilter.java      # 统一认证过滤器
│   ├── GlobalExceptionFilter.java  # 全局异常处理
│   └── LoggingFilter.java          # 日志过滤器
└── handler/
    ├── GlobalErrorHandler.java     # 全局错误处理
    └── SaTokenInterface.java       # Sa-Token接口实现
```

## 🔐 认证机制

### 内部用户认证

1. **Token提取**: 从`Authorization`头、`satoken`头或查询参数中提取token
2. **Token验证**: 使用synapse-security模块验证token有效性
3. **Token续期**: 根据配置自动续期即将过期的token
4. **用户信息**: 从synapse-cache模块获取用户会话信息
5. **权限检查**: 基于用户权限进行路径访问控制
6. **信息传递**: 将用户信息通过请求头传递给下游服务

### 第三方平台认证

1. **API Key验证**: 通过`X-API-Key`头进行第三方平台身份验证
2. **平台标识**: 为第三方请求添加`X-Auth-Type: third-party`标识
3. **限流控制**: 独立的第三方平台限流策略

## 🚦 限流策略

### 多维度限流

1. **IP限流**: 基于客户端IP地址的访问频率控制
2. **用户限流**: 基于已认证用户的访问频率控制
3. **API限流**: 基于API路径的访问频率控制

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

## 🔄 路由配置

### 服务路由

- **IAM服务**: `/api/auth/**` → `lb://iam-service`
- **业务服务**: `/api/business/**` → `lb://business-service`
- **第三方API**: `/api/third-party/**` → `lb://third-party-service`

### 白名单

无需认证的路径：
- `/api/auth/**` - 认证相关接口
- `/oauth2/**` - OAuth2认证
- `/actuator/**` - 监控端点
- `/swagger-ui/**` - API文档
- `/v3/api-docs/**` - OpenAPI文档

## 🛡️ 安全配置

### JWT Token处理

```yaml
synapse:
  gateway:
    token-renewal:
      enabled: true
      threshold-seconds: 1800  # 30分钟内自动续期
      renewal-seconds: 3600    # 续期1小时
```

### 第三方平台

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
    cn.dev33.satoken: DEBUG
```

## 🚀 部署配置

### 环境变量

- `REDIS_HOST`: Redis服务器地址
- `REDIS_PORT`: Redis端口
- `NACOS_SERVER_ADDR`: Nacos服务器地址
- `NACOS_NAMESPACE`: Nacos命名空间

### 依赖服务

- **Redis**: 用于限流和会话存储
- **Nacos**: 用于服务发现和配置管理
- **各业务服务**: 需要注册到Nacos服务注册中心

## 📝 使用说明

### 客户端请求

#### 内部用户请求

```bash
curl -H "Authorization: Bearer <jwt_token>" \
     -H "Content-Type: application/json" \
     http://gateway:8080/api/business/users
```

#### 第三方平台请求

```bash
curl -H "X-API-Key: <api_key>" \
     -H "Content-Type: application/json" \
     http://gateway:8080/api/third-party/data
```

### 请求头传递

Gateway会自动为下游服务添加以下请求头：

- `X-Auth-Type`: 认证类型（`internal`或`third-party`）
- `X-User-Id`: 用户ID（内部用户）
- `X-Username`: 用户名（内部用户）
- `X-Tenant-Id`: 租户ID（内部用户）
- `X-User-Roles`: 用户角色（内部用户）
- `X-User-Permissions`: 用户权限（内部用户）
- `X-API-Key`: API密钥（第三方平台）

## 🔧 维护说明

### 配置更新

1. 修改`application.yml`配置文件
2. 通过Nacos配置中心动态更新
3. 重启服务生效

### 性能优化

1. 调整限流配置以适应业务需求
2. 优化Redis连接池配置
3. 监控Gateway性能指标

### 故障排查

1. 检查日志文件中的错误信息
2. 验证Redis连接状态
3. 确认Nacos服务注册状态
4. 检查下游服务健康状态 