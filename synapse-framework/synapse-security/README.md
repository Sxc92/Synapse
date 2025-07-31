# Synapse Security 安全框架

## 📖 概述

Synapse Security 是 SynapseMOM 平台的安全认证框架，基于 Sa-Token 提供统一的认证、授权和会话管理功能。该框架支持多种认证策略、OAuth2.0、JWT 等安全机制，为微服务架构提供完整的安全解决方案。

## ✨ 特性

- 🔐 **统一认证**: 基于 Sa-Token 的统一认证机制
- 🎯 **多种策略**: 支持 Sa-Token、OAuth2.0、JWT 等多种认证策略
- 🔒 **权限管理**: 基于角色和权限的访问控制
- 🌐 **OAuth2.0**: 完整的 OAuth2.0 授权码模式支持
- 🎨 **用户上下文**: 便捷的用户上下文管理
- 🔄 **会话管理**: 分布式会话管理和自动续期
- ⚙️ **自动配置**: 基于 Spring Boot 的自动配置
- 🛡️ **安全防护**: 内置安全防护机制

## 🏗️ 模块结构

```
synapse-security/
├── config/           # 配置类
│   ├── SecurityAutoConfiguration.java    # 安全自动配置
│   └── JWTSaTokenConfiguration.java      # JWT Sa-Token 配置
├── core/             # 核心服务
│   ├── AuthenticationService.java        # 认证服务接口
│   ├── JWTStpLogic.java                  # JWT StpLogic 实现
│   ├── PermissionManager.java            # 权限管理器
│   └── ...                               # 其他核心服务
├── factory/          # 工厂类
│   └── AuthenticationStrategyFactory.java # 认证策略工厂
├── model/            # 模型类
│   ├── AuthRequest.java                  # 认证请求
│   ├── AuthResponse.java                 # 认证响应
│   ├── LoginRequest.java                 # 登录请求
│   └── ...                               # 其他模型
├── service/          # 服务实现
│   ├── DefaultAuthenticationService.java # 默认认证服务
│   └── TokenRenewalService.java          # Token 续期服务
├── strategy/         # 策略类
│   ├── AuthenticationStrategy.java       # 认证策略接口
│   ├── OAuth2AuthenticationStrategy.java # OAuth2 认证策略
│   └── SaTokenAuthenticationStrategy.java # Sa-Token 认证策略
├── utils/            # 工具类
└── view/             # 视图处理
    └── OAuth2ViewHandler.java            # OAuth2 视图处理器
```

## 🚀 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-security</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 Sa-Token

在 `application.yml` 中配置：

```yaml
sa-token:
  token-name: satoken
  timeout: 2592000              # token有效期（秒）
  activity-timeout: 1800        # token活跃有效期（秒）
  is-concurrent: true           # 是否允许并发登录
  is-share: false              # 是否共享token
  token-style: uuid            # token风格
  is-log: true                 # 是否输出日志
  is-read-cookie: false        # 是否从cookie读取token
  is-read-header: true         # 是否从header读取token
  is-read-body: false          # 是否从body读取token
```

### 3. 配置 OAuth2.0

```yaml
synapse:
  security:
    oauth2:
      enabled: true
      client-id: your-client-id
      client-secret: your-client-secret
      redirect-uri: http://localhost:8080/oauth2/callback
      authorization-server: http://localhost:8080/oauth2/authorize
      token-server: http://localhost:8080/oauth2/token
```

### 4. 使用注解式权限检查

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/info")
    @SaCheckLogin  // 要求登录
    public Result<UserInfo> getUserInfo() {
        UserInfo userInfo = userService.getCurrentUser();
        return Result.success(userInfo);
    }
    
    @PostMapping("/delete")
    @SaCheckRole("admin")  // 要求管理员角色
    public Result<Void> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return Result.success();
    }
    
    @GetMapping("/list")  
    @SaCheckPermission("user:read")  // 要求用户读权限
    public Result<List<User>> getUserList() {
        List<User> users = userService.getUserList();
        return Result.success(users);
    }
    
    @PostMapping("/create")
    @SaCheckPermission("user:create")
    public Result<User> createUser(@RequestBody @Valid User user) {
        User createdUser = userService.createUser(user);
        return Result.success(createdUser);
    }
}
```

### 5. 使用编程式权限检查

```java
@Service
public class BusinessService {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    public void processData(String token) {
        // 获取用户信息
        UserContext user = authenticationService.getUserContext(token);
        
        // 检查权限
        if (!authenticationService.hasPermission(token, "data:process")) {
            throw new SecurityException("权限不足");
        }
        
        // 检查角色
        if (authenticationService.hasRole(token, "admin")) {
            // 管理员逻辑
            processAdminData();
        } else {
            // 普通用户逻辑
            processUserData();
        }
    }
}
```

## 📋 核心功能

### 认证策略

#### Sa-Token 认证

```java
@Service
public class SaTokenAuthService {
    
    @Autowired
    private SaTokenAuthenticationStrategy saTokenStrategy;
    
    public AuthResponse login(LoginRequest request) {
        // 验证用户名密码
        User user = userService.authenticate(request.getUsername(), request.getPassword());
        
        // 生成 token
        String token = saTokenStrategy.generateToken(user);
        
        return AuthResponse.builder()
            .token(token)
            .user(user)
            .expiresIn(3600)
            .build();
    }
    
    public void logout(String token) {
        saTokenStrategy.invalidateToken(token);
    }
    
    public UserContext getUserContext(String token) {
        return saTokenStrategy.getUserContext(token);
    }
}
```

#### OAuth2.0 认证

```java
@Service
public class OAuth2AuthService {
    
    @Autowired
    private OAuth2AuthenticationStrategy oauth2Strategy;
    
    public String getAuthorizationUrl() {
        return oauth2Strategy.getAuthorizationUrl();
    }
    
    public AuthResponse handleCallback(String code) {
        // 处理授权码回调
        return oauth2Strategy.handleAuthorizationCode(code);
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        return oauth2Strategy.refreshToken(refreshToken);
    }
}
```

#### JWT 认证

```java
@Service
public class JWTAuthService {
    
    @Autowired
    private JWTStpLogic jwtStpLogic;
    
    public AuthResponse login(LoginRequest request) {
        User user = userService.authenticate(request.getUsername(), request.getPassword());
        
        // 生成 JWT token
        String token = jwtStpLogic.createToken(user.getId(), user.getUsername());
        
        return AuthResponse.builder()
            .token(token)
            .user(user)
            .expiresIn(3600)
            .build();
    }
    
    public UserContext getUserContext(String token) {
        return jwtStpLogic.getUserContext(token);
    }
}
```

### 用户上下文管理

```java
@Service
public class UserContextService {
    
    public void processWithUserContext() {
        // 获取当前用户信息
        UserContext user = UserContextHolder.getCurrentUser();
        Long userId = UserContextHolder.getCurrentUserId();
        String username = UserContextHolder.getCurrentUsername();
        
        // 权限检查
        if (!UserContextHolder.hasPermission("data:process")) {
            throw new SecurityException("权限不足");
        }
        
        // 角色检查
        if (UserContextHolder.hasRole("admin")) {
            // 管理员逻辑
            processAdminData();
        } else if (UserContextHolder.hasRole("user")) {
            // 普通用户逻辑
            processUserData();
        }
        
        // 检查多个权限
        if (UserContextHolder.hasAnyPermission("read", "write")) {
            // 有读或写权限
            processReadWriteData();
        }
        
        // 检查所有权限
        if (UserContextHolder.hasAllPermissions("read", "write", "delete")) {
            // 有读、写、删除权限
            processFullData();
        }
    }
}
```

### 权限管理

```java
@Service
public class PermissionService {
    
    @Autowired
    private PermissionManager permissionManager;
    
    public void assignRoleToUser(Long userId, String role) {
        permissionManager.assignRole(userId, role);
    }
    
    public void assignPermissionToUser(Long userId, String permission) {
        permissionManager.assignPermission(userId, permission);
    }
    
    public void assignPermissionToRole(String role, String permission) {
        permissionManager.assignPermissionToRole(role, permission);
    }
    
    public List<String> getUserRoles(Long userId) {
        return permissionManager.getUserRoles(userId);
    }
    
    public List<String> getUserPermissions(Long userId) {
        return permissionManager.getUserPermissions(userId);
    }
    
    public boolean hasPermission(Long userId, String permission) {
        return permissionManager.hasPermission(userId, permission);
    }
    
    public boolean hasRole(Long userId, String role) {
        return permissionManager.hasRole(userId, role);
    }
}
```

### Token 续期

```java
@Service
public class TokenService {
    
    @Autowired
    private TokenRenewalService tokenRenewalService;
    
    public String renewToken(String currentToken) {
        return tokenRenewalService.renewToken(currentToken);
    }
    
    public boolean isTokenExpiringSoon(String token) {
        return tokenRenewalService.isTokenExpiringSoon(token);
    }
    
    public long getTokenExpirationTime(String token) {
        return tokenRenewalService.getTokenExpirationTime(token);
    }
}
```

## ⚙️ 配置选项

### 安全配置

```yaml
synapse:
  security:
    enabled: true
    authentication:
      strategy: sa-token  # sa-token, oauth2, jwt
      default-timeout: 3600
      auto-renewal: true
    authorization:
      enabled: true
      cache-permissions: true
      permission-cache-ttl: 300
    session:
      enabled: true
      distributed: true
      timeout: 1800
```

### OAuth2 配置

```yaml
synapse:
  security:
    oauth2:
      enabled: true
      client-id: your-client-id
      client-secret: your-client-secret
      redirect-uri: http://localhost:8080/oauth2/callback
      authorization-server: http://localhost:8080/oauth2/authorize
      token-server: http://localhost:8080/oauth2/token
      user-info-server: http://localhost:8080/oauth2/userinfo
      scope: read write
      state: random-state
```

### JWT 配置

```yaml
synapse:
  security:
    jwt:
      enabled: true
      secret: your-jwt-secret
      issuer: synapse-mom
      audience: synapse-users
      expiration: 3600
      refresh-expiration: 86400
```

## 📝 使用示例

### 完整的认证控制器

```java
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private OAuth2AuthenticationStrategy oauth2Strategy;
    
    @PostMapping("/login")
    public Result<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        try {
            AuthResponse response = authenticationService.login(request);
            return Result.success(response);
        } catch (AuthenticationException e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.error("登录失败，请稍后重试");
        }
    }
    
    @PostMapping("/logout")
    @SaCheckLogin
    public Result<Void> logout() {
        try {
            String token = StpUtil.getTokenValue();
            authenticationService.logout(token);
            return Result.success();
        } catch (Exception e) {
            log.error("登出异常", e);
            return Result.error("登出失败");
        }
    }
    
    @GetMapping("/oauth2/authorize")
    public Result<String> getOAuth2AuthorizationUrl() {
        try {
            String url = oauth2Strategy.getAuthorizationUrl();
            return Result.success(url);
        } catch (Exception e) {
            log.error("获取OAuth2授权URL失败", e);
            return Result.error("获取授权URL失败");
        }
    }
    
    @GetMapping("/oauth2/callback")
    public Result<AuthResponse> handleOAuth2Callback(@RequestParam String code) {
        try {
            AuthResponse response = oauth2Strategy.handleAuthorizationCode(code);
            return Result.success(response);
        } catch (Exception e) {
            log.error("OAuth2回调处理失败", e);
            return Result.error("授权失败");
        }
    }
    
    @PostMapping("/refresh")
    public Result<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        try {
            AuthResponse response = authenticationService.refreshToken(refreshToken);
            return Result.success(response);
        } catch (Exception e) {
            log.error("Token刷新失败", e);
            return Result.error("Token刷新失败");
        }
    }
    
    @GetMapping("/user/info")
    @SaCheckLogin
    public Result<UserContext> getUserInfo() {
        try {
            UserContext userContext = UserContextHolder.getCurrentUser();
            return Result.success(userContext);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }
}
```

### 权限管理控制器

```java
@RestController
@RequestMapping("/api/permissions")
@Slf4j
public class PermissionController {
    
    @Autowired
    private PermissionManager permissionManager;
    
    @PostMapping("/users/{userId}/roles")
    @SaCheckRole("admin")
    public Result<Void> assignRoleToUser(@PathVariable Long userId, 
                                        @RequestParam String role) {
        try {
            permissionManager.assignRole(userId, role);
            return Result.success();
        } catch (Exception e) {
            log.error("分配角色失败", e);
            return Result.error("分配角色失败");
        }
    }
    
    @PostMapping("/users/{userId}/permissions")
    @SaCheckRole("admin")
    public Result<Void> assignPermissionToUser(@PathVariable Long userId, 
                                              @RequestParam String permission) {
        try {
            permissionManager.assignPermission(userId, permission);
            return Result.success();
        } catch (Exception e) {
            log.error("分配权限失败", e);
            return Result.error("分配权限失败");
        }
    }
    
    @GetMapping("/users/{userId}/roles")
    @SaCheckPermission("permission:read")
    public Result<List<String>> getUserRoles(@PathVariable Long userId) {
        try {
            List<String> roles = permissionManager.getUserRoles(userId);
            return Result.success(roles);
        } catch (Exception e) {
            log.error("获取用户角色失败", e);
            return Result.error("获取用户角色失败");
        }
    }
    
    @GetMapping("/users/{userId}/permissions")
    @SaCheckPermission("permission:read")
    public Result<List<String>> getUserPermissions(@PathVariable Long userId) {
        try {
            List<String> permissions = permissionManager.getUserPermissions(userId);
            return Result.success(permissions);
        } catch (Exception e) {
            log.error("获取用户权限失败", e);
            return Result.error("获取用户权限失败");
        }
    }
}
```

## 🧪 测试

### 单元测试示例

```java
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    
    @Mock
    private UserService userService;
    
    @Mock
    private PermissionManager permissionManager;
    
    @InjectMocks
    private DefaultAuthenticationService authenticationService;
    
    @Test
    void login_Success() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        when(userService.authenticate("testuser", "password")).thenReturn(user);
        
        // When
        AuthResponse response = authenticationService.login(request);
        
        // Then
        assertNotNull(response.getToken());
        assertEquals(user, response.getUser());
        verify(userService).authenticate("testuser", "password");
    }
    
    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");
        
        when(userService.authenticate("testuser", "wrongpassword"))
            .thenThrow(new AuthenticationException("Invalid credentials"));
        
        // When & Then
        assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(request);
        });
    }
}
```

## 📚 相关文档

- [Synapse Core](./synapse-core/README.md) - 核心框架
- [Synapse Cache](./synapse-cache/README.md) - 缓存框架
- [Synapse Events](./synapse-events/README.md) - 事件驱动框架
- [Synapse Databases](./synapse-databases/README.md) - 数据库框架

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个框架。

## 📄 许可证

本项目采用 MIT 许可证。

---

**最后更新：** 2025-07-20  
**版本：** 1.0.0  
**维护者：** 史偕成 