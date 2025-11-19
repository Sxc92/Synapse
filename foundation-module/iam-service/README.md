# IAM 服务 - 身份认证与权限管理系统

## 概述

IAM（Identity and Access Management）服务是 Synapse 平台的核心基础服务，提供完整的用户身份认证、授权和权限管理功能。基于 RBAC（基于角色的访问控制）模型，实现了用户、角色、菜单、资源和系统的统一管理。

## 核心特性

- ✅ **统一认证**: 基于 Synapse Security 框架的用户登录认证
- ✅ **Token 管理**: 支持 Token 生成、验证和撤销
- ✅ **RBAC 权限模型**: 基于角色的访问控制，支持用户-角色-权限的灵活配置
- ✅ **多系统支持**: 支持多系统管理，用户可访问多个业务系统
- ✅ **菜单权限**: 支持菜单级别的权限控制，动态生成用户菜单树
- ✅ **资源权限**: 支持细粒度的资源权限控制（API、按钮等）
- ✅ **Redis 缓存**: 用户会话和权限信息存储在 Redis 中，提升性能
- ✅ **网关集成**: 与网关服务集成，实现统一的权限验证

## 系统架构

```
前端应用 
    ↓ (携带 token)
网关服务 (统一认证拦截)
    ↓ (验证 token，添加用户信息到请求头)
IAM 服务 (权限验证与数据管理)
    ↓ (读取用户会话和权限)
Redis 缓存 (存储用户会话和权限数据)
    ↓
数据库 (存储用户、角色、权限等基础数据)
```

## 功能模块

### 1. 认证管理

#### 用户登录
- 支持用户名密码登录
- 登录时自动查询用户角色、权限、菜单、资源和系统信息
- 生成 Token 并存储用户会话信息到 Redis
- 支持用户状态检查（锁定、禁用等）

#### 用户登出
- 支持 Token 撤销
- 清理 Redis 中的用户会话数据

#### 权限数据获取
- 获取当前用户有权限的系统列表
- 获取当前用户有权限的菜单列表（树形结构）
- 获取当前用户有权限的资源列表
- 获取当前用户信息

### 2. 用户管理

#### 用户基础管理
- 用户新增/修改：支持用户信息的创建和更新
- 用户删除：删除用户及其关联的角色关系
- 用户查询：支持分页查询、列表查询和详情查询
- 用户状态管理：支持用户启用/禁用、锁定/解锁

#### 用户角色授权
- 用户角色分配：为用户分配一个或多个角色
- 支持批量角色授权
- 删除用户时自动清理角色关联关系

### 3. 角色管理

#### 角色基础管理
- 角色新增/修改：支持角色的创建和更新
- 角色删除：删除角色及其关联的用户、菜单、资源关系
- 角色查询：支持分页查询、列表查询和详情查询

#### 角色权限配置
- 角色菜单分配：为角色分配可访问的菜单
- 角色资源分配：为角色分配可访问的资源（API、按钮等）
- 支持批量菜单/资源授权

### 4. 菜单管理

#### 菜单基础管理
- 菜单新增/修改：支持菜单的创建和更新
- 菜单删除：支持菜单的删除操作
- 菜单查询：支持分页查询、列表查询和详情查询
- 菜单树形结构：支持多级菜单的树形展示

#### 菜单属性
- 菜单编码、名称、路由、组件等基础信息
- 菜单图标、排序、状态、可见性等配置
- 支持菜单与系统的关联

### 5. 资源管理

#### 资源基础管理
- 资源新增/修改：支持资源的创建和更新
- 资源删除：支持资源的删除操作
- 资源查询：支持分页查询、列表查询和详情查询

#### 资源类型
- 支持多种资源类型（API、按钮、数据等）
- 资源权限编码管理
- 资源与菜单、系统的关联

#### 权限查询
- 获取当前用户的资源权限编码列表
- 用于前端按钮权限控制

### 6. 系统管理

#### 系统基础管理
- 系统新增/修改：支持业务系统的创建和更新
- 系统删除：支持系统的删除操作
- 系统查询：支持分页查询、列表查询和详情查询

#### 多系统支持
- 支持多个业务系统的统一管理
- 用户可访问多个系统，系统间权限隔离
- 菜单和资源按系统进行组织

## 权限模型

### RBAC 模型结构

```
用户 (Users)
  ↓ (多对多)
用户-角色关联 (UsersRole)
  ↓
角色 (Roles)
  ↓ (多对多)
角色-菜单关联 (RoleMenu)  角色-资源关联 (RoleResource)
  ↓                        ↓
菜单 (Menu)               资源 (Resource)
  ↓                        ↓
系统 (System)             系统 (System)
```

### 权限继承关系

1. **用户 → 角色**: 用户通过分配角色获得权限
2. **角色 → 菜单**: 角色通过分配菜单获得菜单访问权限
3. **角色 → 资源**: 角色通过分配资源获得资源操作权限
4. **菜单/资源 → 系统**: 菜单和资源归属于特定系统

### 权限计算流程

1. 用户登录时，系统查询用户的所有角色
2. 根据角色查询关联的菜单和资源
3. 根据菜单和资源查询关联的系统
4. 将所有权限数据存储到 Redis 缓存
5. 前端请求时，从缓存中获取用户权限进行验证

## 在业务模块中使用

### 1. 获取当前用户信息

```java
@RestController
@RequestMapping("/api/business")
public class BusinessController {

    @GetMapping("/user-info")
    public Result<UserContext> getUserInfo() {
        // 获取当前用户完整信息
        UserContext currentUser = UserContext.getCurrentUser();
        
        // 获取用户基本信息
        String userId = UserContext.getCurrentUserId();
        String username = UserContext.getCurrentUsername();
        
        // 获取用户权限和角色
        List<String> roles = UserContext.getCurrentRoles();
        List<String> permissions = UserContext.getCurrentPermissions();
        
        return Result.success(currentUser);
    }
}
```

### 2. 使用注解进行权限控制

```java
@RestController
@RequestMapping("/api/business")
public class BusinessController {

    // 需要登录
    @RequireLogin
    @GetMapping("/data")
    public Result<?> getData() {
        return Result.success("数据");
    }

    // 需要特定权限
    @RequirePermission("user:read")
    @GetMapping("/users")
    public Result<?> getUsers() {
        return Result.success("用户列表");
    }

    // 需要多个权限之一
    @RequirePermission(value = {"user:read", "user:write"}, logical = Logical.OR)
    @GetMapping("/user-operations")
    public Result<?> userOperations() {
        return Result.success("用户操作");
    }
}
```

### 3. 手动权限检查

```java
@Service
public class BusinessService {

    public void processData() {
        // 检查权限
        if (!UserContext.hasPermission("data:process")) {
            Ex.throwEx(StandardErrorCode.PERMISSION_DENIED);
        }

        // 检查角色
        if (UserContext.hasRole("admin")) {
            // 管理员逻辑
        } else {
            // 普通用户逻辑
        }
    }
}
```

## 数据存储

### Redis 数据结构

#### 用户会话数据
```
Key: synapse:user:session:{token}
Value: UserContext JSON
TTL: token 过期时间
```

#### 用户角色数据
```
Key: synapse:user:roles:{token}
Value: ["admin", "user"]
TTL: token 过期时间
```

#### 用户权限数据
```
Key: synapse:user:permissions:{token}
Value: ["user:read", "user:write", "system:config"]
TTL: token 过期时间
```

#### 用户菜单数据
```
Key: synapse:user:menus:{token}
Value: MenuVO List JSON
TTL: token 过期时间
```

#### 用户资源数据
```
Key: synapse:user:resources:{token}
Value: ResourceVO List JSON
TTL: token 过期时间
```

#### 用户系统数据
```
Key: synapse:user:systems:{token}
Value: SystemVO List JSON
TTL: token 过期时间
```

## 模块结构

```
iam-service/
├── iam-sdk/          # SDK 模块（DTO、VO、枚举等）
│   ├── dto/          # 数据传输对象
│   │   ├── auth/     # 认证相关 DTO
│   │   ├── opera/    # 操作相关 DTO
│   │   ├── query/    # 查询相关 DTO
│   │   └── associated/ # 关联关系 DTO
│   ├── vo/           # 视图对象
│   │   ├── auth/     # 认证相关 VO
│   │   ├── resource/ # 资源相关 VO
│   │   └── users/    # 用户相关 VO
│   └── enums/        # 枚举定义
└── iam-server/       # 服务端模块（业务逻辑实现）
    ├── controller/   # 控制器层
    ├── service/      # 服务层
    ├── repository/   # 数据访问层
    │   ├── entity/   # 实体类
    │   └── mapper/   # Mapper 接口
    └── resources/    # 配置文件
```

## 配置说明

### 安全配置

IAM 服务依赖 Synapse Security 模块进行认证和授权，相关配置请参考 `synapse-security` 模块文档。

### 数据库配置

IAM 服务需要连接数据库存储用户、角色、权限等基础数据，数据库初始化脚本位于 `sql/synapse_iam.sql`。

### Redis 配置

IAM 服务使用 Redis 存储用户会话和权限数据，需要配置 Redis 连接信息。

## 注意事项

1. **Token 安全**: 请妥善保管 Token，避免泄露，建议使用 HTTPS 传输
2. **Redis 可用性**: 系统依赖 Redis，请确保 Redis 服务的高可用性
3. **权限设计**: 建议采用 RBAC 模型设计权限体系，合理划分角色和权限
4. **性能优化**: 权限数据已缓存到 Redis，登录时一次性加载，减少数据库查询
5. **数据一致性**: 修改用户角色、角色权限后，需要用户重新登录才能生效
6. **多系统隔离**: 不同系统的菜单和资源相互隔离，确保权限边界清晰

## 故障排查

### 常见问题

1. **登录失败**: 检查用户名密码、用户状态（锁定、禁用）
2. **Token 验证失败**: 检查 Redis 连接和 Token 有效性
3. **权限不足**: 检查用户角色分配和角色权限配置
4. **菜单不显示**: 检查菜单状态、可见性设置和角色菜单分配
5. **资源权限无效**: 检查角色资源分配和资源权限编码

### 调试日志

开启调试日志可以帮助排查问题：

```yaml
logging:
  level:
    com.indigo.iam: DEBUG
    com.indigo.security: DEBUG
```

## 扩展功能

### 1. 会话管理

可以通过 `AuthenticationService` 进行会话管理：
- 获取在线用户列表
- 强制用户下线
- 刷新用户权限数据

### 2. 权限扩展

可以在业务模块中扩展权限验证逻辑：
- 基于数据权限的访问控制
- 基于时间段的权限控制
- 基于 IP 地址的权限控制

### 3. 多租户支持

IAM 服务支持多租户场景，可以通过 `UserContext.getCurrentTenantId()` 获取当前租户信息。
