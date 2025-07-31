# IAM 服务 - Sa-Token + OAuth2.0 + Redis 权限管理系统

## 概述

本服务基于 Sa-Token 框架，整合 OAuth2.0 和 Redis 缓存，实现了完整的用户认证和权限管理系统。

## 核心特性

- ✅ **Sa-Token 集成**: 基于 Sa-Token 的认证授权框架
- ✅ **OAuth2.0 支持**: 完整的 OAuth2.0 授权码模式实现  
- ✅ **Redis 缓存**: 用户会话和权限信息存储在 Redis 中
- ✅ **网关统一认证**: 在网关层进行统一的权限验证
- ✅ **用户信息传递**: 网关将用户信息通过请求头传递给业务服务
- ✅ **最小引入原则**: 复用现有缓存模块，避免重复依赖

## 系统架构

```
前端应用 
    ↓ (携带 token)
网关服务 (SaTokenAuthFilter)
    ↓ (验证 token，添加用户信息到请求头)
业务服务 (UserContextHolder 获取用户信息)
    ↓ (读取用户会话和权限)
Redis 缓存 (UserSessionService)
```

## 快速开始

### 1. 用户登录

```bash
# 用户登录
curl -X POST http://localhost:8080/api/iam/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'

# 响应示例
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "uuid-token-string",
    "userInfo": {
      "userId": 10001,
      "username": "admin",
      "tenantId": 1,
      "deptId": 1
    },
    "roles": ["admin", "user"],
    "permissions": ["user:read", "user:write", "system:config"]
  }
}
```

### 2. 使用 Token 访问受保护的资源

```bash
# 访问需要权限的接口
curl -X GET http://localhost:8080/api/iam/api/example/user-list \
  -H "Authorization: Bearer your-token-here"

# 访问管理员专用接口
curl -X GET http://localhost:8080/api/iam/api/example/admin-only \
  -H "Authorization: Bearer your-token-here"
```

### 3. OAuth2.0 授权流程

```bash
# 1. 获取授权码
curl -X GET "http://localhost:8080/api/iam/api/auth/oauth2/authorize?clientId=test_client&redirectUri=http://localhost:3000/callback&scope=read"

# 2. 使用授权码获取访问令牌
curl -X POST http://localhost:8080/api/iam/api/auth/oauth2/token \
  -d "code=authorization-code&clientId=test_client&clientSecret=test_secret"

# 3. 使用访问令牌获取用户信息
curl -X GET http://localhost:8080/api/iam/api/auth/userinfo \
  -H "Authorization: Bearer access-token"
```

## 在业务模块中使用

### 1. 获取当前用户信息

```java
@RestController
@RequestMapping("/api/business")
public class BusinessController {

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        // 获取当前用户完整信息
        UserContext currentUser = UserContextHolder.getCurrentUser();
        
        // 获取用户基本信息
        Long userId = UserContextHolder.getCurrentUserId();
        String username = UserContextHolder.getCurrentUsername();
        Long tenantId = UserContextHolder.getCurrentTenantId();
        
        // 获取用户权限和角色
        List<String> roles = UserContextHolder.getCurrentUserRoles();
        List<String> permissions = UserContextHolder.getCurrentUserPermissions();
        
        return ResponseEntity.ok(Map.of(
            "user", currentUser,
            "roles", roles,
            "permissions", permissions
        ));
    }
}
```

### 2. 使用注解进行权限控制

```java
@RestController
@RequestMapping("/api/business")
public class BusinessController {

    // 需要管理员角色
    @SaCheckRole("admin")
    @GetMapping("/admin-data")
    public ResponseEntity<?> getAdminData() {
        return ResponseEntity.ok("管理员数据");
    }

    // 需要特定权限
    @SaCheckPermission("user:read")
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok("用户列表");
    }

    // 需要多个权限之一
    @SaCheckPermission(value = {"user:read", "user:write"}, mode = SaMode.OR)
    @GetMapping("/user-operations")
    public ResponseEntity<?> userOperations() {
        return ResponseEntity.ok("用户操作");
    }
}
```

### 3. 手动权限检查

```java
@Service
public class BusinessService {

    public void processData() {
        // 检查权限
        if (!UserContextHolder.hasPermission("data:process")) {
            throw new RuntimeException("权限不足");
        }

        // 检查角色
        if (UserContextHolder.hasRole("admin")) {
            // 管理员逻辑
        } else {
            // 普通用户逻辑
        }

        // 检查多个权限
        if (UserContextHolder.hasAnyPermission("read", "write")) {
            // 有读或写权限
        }

        if (UserContextHolder.hasAllPermissions("read", "write", "delete")) {
            // 有全部权限
        }
    }
}
```

## 网关配置

网关会自动进行以下处理：

1. **白名单检查**: 登录、OAuth2.0 等接口无需认证
2. **Token 验证**: 验证 token 的有效性
3. **用户信息获取**: 从 Redis 获取用户会话信息
4. **权限验证**: 根据路径进行权限检查
5. **请求头添加**: 将用户信息添加到请求头传递给下游服务

### 请求头说明

网关会在请求头中添加以下信息：

```
X-User-Id: 用户ID
X-Username: 用户名
X-Tenant-Id: 租户ID
X-Dept-Id: 部门ID
X-User-Roles: 角色列表（逗号分隔）
X-User-Permissions: 权限列表（逗号分隔）
X-User-Context: 完整用户上下文（JSON格式）
```

## Redis 数据结构

### 用户会话数据

```
Key: synapse:user:session:{token}
Value: UserContext JSON
TTL: token 过期时间
```

### 用户角色数据

```
Key: synapse:user:roles:{token}
Value: ["admin", "user"]
TTL: token 过期时间
```

### 用户权限数据

```
Key: synapse:user:permissions:{token}
Value: ["user:read", "user:write", "system:config"]
TTL: token 过期时间
```

## 配置说明

### Sa-Token 配置

```yaml
sa-token:
  token-name: satoken                    # token名称
  timeout: 2592000                       # token有效期(秒)
  activity-timeout: 1800                 # token活跃有效期(秒)
  is-concurrent: true                    # 是否允许并发登录
  is-share: false                        # 是否共享token
  token-style: uuid                      # token风格
  is-log: true                          # 是否输出日志
```

### OAuth2.0 配置

```yaml
oauth2:
  clients:
    - client-id: test_client
      client-secret: test_secret
      redirect-uris:
        - http://localhost:3000/callback
      scopes:
        - read
        - write
      grant-types:
        - authorization_code
        - refresh_token
```

## API 接口

### 认证接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/logout` | POST | 用户登出 |
| `/api/auth/userinfo` | GET | 获取用户信息 |

### OAuth2.0 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/oauth2/authorize` | GET | 获取授权码 |
| `/api/auth/oauth2/token` | POST | 获取访问令牌 |

### 示例接口

| 接口 | 方法 | 权限要求 | 说明 |
|------|------|----------|------|
| `/api/example/current-user` | GET | 无 | 获取当前用户信息 |
| `/api/example/admin-only` | GET | admin 角色 | 管理员专用接口 |
| `/api/example/user-list` | GET | user:read 权限 | 用户列表 |
| `/api/example/user-create` | GET | user:write 权限 | 创建用户 |
| `/api/example/system-config` | GET | system:config 权限 | 系统配置 |

## 扩展功能

### 1. 会话管理

```java
@Autowired
private UserSessionService userSessionService;

// 获取在线用户列表
List<UserContext> onlineUsers = userSessionService.getOnlineUsers();

// 强制用户下线
userSessionService.forceUserOffline(userId);

// 获取用户会话统计
UserSessionService.UserSessionStats stats = userSessionService.getUserSessionStats();
```

### 2. 权限扩展

可以在网关的 `hasPermission` 方法中实现更复杂的权限匹配逻辑：

```java
private boolean hasPermission(String path, List<String> permissions) {
    // 基于路径的权限映射
    Map<String, String> pathPermissionMap = Map.of(
        "/api/user/**", "user:read",
        "/api/admin/**", "admin",
        "/api/system/**", "system:config"
    );
    
    // 实现路径匹配和权限检查逻辑
    // ...
}
```

## 注意事项

1. **Token 安全**: 请妥善保管 token，避免泄露
2. **Redis 可用性**: 系统依赖 Redis，请确保 Redis 服务的高可用性
3. **权限设计**: 建议采用 RBAC 模型设计权限体系
4. **性能优化**: 可以考虑在本地缓存权限信息以提高性能
5. **日志审计**: 重要操作建议记录审计日志

## 故障排查

### 常见问题

1. **Token 验证失败**: 检查 Redis 连接和 token 有效性
2. **权限不足**: 检查用户角色和权限配置
3. **网关认证失败**: 检查网关过滤器配置和白名单设置
4. **用户信息获取失败**: 检查请求头传递和 UserContextHolder 使用

### 调试日志

开启调试日志可以帮助排查问题：

```yaml
logging:
  level:
    com.indigo: DEBUG
    cn.dev33.satoken: DEBUG
``` 