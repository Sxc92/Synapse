# Synapse Databases 数据库框架

## 📖 概述

Synapse Databases 是 SynapseMOM 平台的数据库框架，提供了动态数据源、多数据库支持、负载均衡、健康检查等功能。该框架支持 MySQL、PostgreSQL、Oracle 等多种数据库，为微服务架构提供灵活的数据库解决方案。

## ✨ 特性

- 🔄 **动态数据源**: 支持运行时动态切换数据源
- 🎯 **多数据库支持**: 支持 MySQL、PostgreSQL、Oracle 等主流数据库
- ⚖️ **负载均衡**: 提供多种负载均衡策略
- 🏥 **健康检查**: 自动检测数据源健康状态
- 🔧 **自动配置**: 基于 Spring Boot 的自动配置
- 📊 **监控统计**: 提供数据源使用统计和监控
- 🛡️ **故障转移**: 支持数据源故障自动切换
- 🎨 **灵活配置**: 支持多种配置方式和策略
- 🚀 **@AutoRepository**: 自动生成Repository实现，无需手写ServiceImpl
- 🎯 **MyBatis-Plus集成**: 完美集成MyBatis-Plus，获得完整CRUD功能
- 🔧 **零样板代码**: 大幅减少重复代码，提升开发效率
- 📄 **统一分页查询**: 基于PageDTO的统一分页查询，支持自动查询条件构建
- 🎯 **类型安全**: 编译时类型检查，避免运行时错误

## 🏗️ 模块结构

```
synapse-databases/
├── annotation/       # 注解类
│   ├── AutoRepository.java                      # 自动Repository注解
│   ├── SqlQuery.java                            # SQL查询注解
│   ├── SqlUpdate.java                           # SQL更新注解
│   └── SqlPage.java                             # SQL分页注解
├── config/          # 配置类
│   ├── DynamicDataSourceAutoConfiguration.java  # 动态数据源自动配置
│   ├── DynamicDataSourceProperties.java         # 动态数据源属性配置
│   ├── MybatisPlusConfig.java                   # MyBatis Plus 配置
│   └── SqlAnnotationAutoConfiguration.java      # SQL注解自动配置
├── dynamic/         # 动态数据源
│   ├── DynamicDataSourceContextHolder.java      # 动态数据源上下文
│   └── DynamicRoutingDataSource.java            # 动态路由数据源
├── enums/           # 枚举类
│   ├── DatabaseType.java                        # 数据库类型枚举
│   ├── DataSourceType.java                      # 数据源类型枚举
│   └── PoolType.java                            # 连接池类型枚举
├── health/          # 健康检查
│   └── DataSourceHealthChecker.java             # 数据源健康检查器
├── interceptor/     # 拦截器
│   └── AutoDataSourceInterceptor.java           # 自动数据源拦截器
├── loadbalance/     # 负载均衡
│   └── DataSourceLoadBalancer.java              # 数据源负载均衡器
├── proxy/           # 动态代理
│   └── SqlMethodInterceptor.java                # SQL方法拦截器
├── repository/      # Repository基类
│   └── BaseRepository.java                      # 基础Repository接口
└── executor/        # SQL执行器
    └── SqlMethodExecutor.java                   # SQL方法执行器
```

## 🚀 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-databases</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置数据源

在 `application.yml` 中配置：

```yaml
synapse:
  databases:
    enabled: true
    primary:
      url: jdbc:mysql://localhost:3306/primary_db
      username: root
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
      type: mysql
      pool-type: hikari
    secondary:
      url: jdbc:mysql://localhost:3306/secondary_db
      username: root
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
      type: mysql
      pool-type: hikari
    dynamic:
      enabled: true
      default-data-source: primary
      health-check:
        enabled: true
        interval: 30000
      load-balance:
        strategy: round-robin
        enabled: true
```

### 3. 使用 @AutoRepository（推荐）

#### 定义Repository接口

```java
// 1. 定义实体类
@Entity
@Table(name = "iam_tenant")
public class IamTenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String code;
    private String description;
    private String status;
    private LocalDateTime expireTime;
    
    // getters and setters...
}

// 2. 定义Mapper接口
@Mapper
public interface TenantMapper extends BaseMapper<IamTenant> {
    // 可以添加自定义的SQL方法
}

// 3. 定义Repository接口（无需实现类）
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 框架自动提供所有BaseRepository方法
    // 无需手写任何实现代码
}

// 4. 在Service中使用
@Service
public class TenantServiceImpl {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    public boolean addOrModify(IamTenant tenant) {
        // 直接调用，框架自动处理
        return tenantsRepository.save(tenant);
    }
    
    public IamTenant getById(Long id) {
        return tenantsRepository.getById(id);
    }
    
    public List<IamTenant> getAllTenants() {
        return tenantsRepository.list();
    }
    
    public boolean deleteTenant(Long id) {
        return tenantsRepository.removeById(id);
    }
}
```

### 3. 使用@AutoRepository（新功能）

#### 创建Repository接口

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 无需手写实现类，框架自动生成代理
    // 继承BaseRepository获得完整的CRUD功能
}
```

#### 在Service中使用

```java
@Service
public class TenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository; // 直接注入，无需实现类
    
    public boolean createTenant(IamTenant tenant) {
        // 直接调用save方法，框架自动处理
        return tenantsRepository.save(tenant);
    }
    
    public IamTenant getTenantById(Long id) {
        // 直接调用getById方法
        return tenantsRepository.getById(id);
    }
    
    public boolean updateTenant(IamTenant tenant) {
        // 直接调用updateById方法
        return tenantsRepository.updateById(tenant);
    }
    
    public boolean deleteTenant(Long id) {
        // 直接调用removeById方法
        return tenantsRepository.removeById(id);
    }
}
```

#### 支持的BaseRepository方法

```java
// 所有BaseRepository方法都支持，包括：
tenantsRepository.save(entity);           // 保存实体
tenantsRepository.getById(id);            // 根据ID查询
tenantsRepository.updateById(entity);     // 根据ID更新
tenantsRepository.removeById(id);         // 根据ID删除
tenantsRepository.list();                 // 查询所有
tenantsRepository.page(page);             // 分页查询
tenantsRepository.count();                // 统计数量
// ... 更多MyBatis-Plus ServiceImpl方法
```

### 4. 使用动态数据源

#### 注解方式

```java
@Service
public class UserService {
    
    @DataSource("primary")
    public User getUserFromPrimary(Long userId) {
        return userRepository.findById(userId);
    }
    
    @DataSource("secondary")
    public User getUserFromSecondary(Long userId) {
        return userRepository.findById(userId);
    }
    
    @DataSource(DatabaseType.MYSQL)
    public List<User> getUsersFromMySQL() {
        return userRepository.findAll();
    }
    
    @DataSource(DatabaseType.POSTGRESQL)
    public List<User> getUsersFromPostgreSQL() {
        return userRepository.findAll();
    }
}
```

#### 编程方式

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    public Order getOrder(Long orderId) {
        // 从主库查询
        DynamicDataSourceContextHolder.setDataSource("primary");
        try {
            return orderRepository.findById(orderId);
        } finally {
            DynamicDataSourceContextHolder.clearDataSource();
        }
    }
    
    public void createOrder(Order order) {
        // 写入主库
        DynamicDataSourceContextHolder.setDataSource("primary");
        try {
            orderRepository.save(order);
        } finally {
            DynamicDataSourceContextHolder.clearDataSource();
        }
    }
    
    public List<Order> getReadOnlyOrders() {
        // 从从库查询
        DynamicDataSourceContextHolder.setDataSource("secondary");
        try {
            return orderRepository.findByStatus("active");
        } finally {
            DynamicDataSourceContextHolder.clearDataSource();
        }
    }
}
```

## 📋 核心功能

### @AutoRepository 自动Repository生成

#### 功能概述

`@AutoRepository` 是 Synapse Databases 框架的核心功能之一，它能够自动为标记的接口生成实现，无需手写 `ServiceImpl` 类，大幅减少样板代码。

### SQL注解框架

#### 概述

SQL注解框架是一个基于MyBatis-Plus的扩展框架，通过注解的方式直接在接口方法上定义SQL语句，无需编写实现类，大大简化了开发工作。

#### 核心特性

- ✅ **无ServiceImpl** - 接口 + 注解即可使用
- ✅ **兼容MyBatis-Plus** - 继承所有基础CRUD功能
- ✅ **注解SQL** - 直接在方法上定义SQL语句
- ✅ **动态代理** - 自动生成实现，无需手动编写
- ✅ **类型安全** - 支持参数绑定和类型转换
- ✅ **统一分页** - 基于PageDTO的统一分页查询
- ✅ **自动查询条件** - 支持@QueryCondition注解自动构建查询条件
- ✅ **多表关联** - 支持复杂的多表查询
- ✅ **多数据源支持** - 自动读写分离，从库轮询负载均衡

#### 注解说明

##### @AutoRepository
标记需要自动生成实现的Repository接口。

```java
@AutoRepository
public interface UserRepository extends BaseRepository<User, UserMapper> {
    // 接口方法
}
```

##### @AutoService
标记需要自动生成实现的Service接口。

```java
@AutoService
public interface UserService extends IService<User> {
    // 接口方法
}
```

##### @SqlQuery
用于定义查询SQL语句。

```java
@SqlQuery("SELECT * FROM iam_user WHERE id = #{id}")
User findById(@Param("id") Long id);

@SqlQuery("SELECT * FROM iam_user WHERE username LIKE #{username}")
List<User> findByUsernameLike(@Param("username") String username);
```

##### @SqlUpdate
用于定义更新SQL语句。

```java
@SqlUpdate("UPDATE iam_user SET status = #{status} WHERE id = #{id}")
int updateStatus(@Param("id") Long id, @Param("status") Integer status);

@SqlUpdate("INSERT INTO iam_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
int assignRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
```

##### @SqlPage
用于定义分页查询SQL语句。

```java
@SqlPage(
    countSql = "SELECT COUNT(*) FROM iam_user WHERE tenant_id = #{tenantId}",
    dataSql = "SELECT * FROM iam_user WHERE tenant_id = #{tenantId} ORDER BY create_time DESC"
)
PageResult<User> findPageByTenant(@Param("tenantId") Long tenantId, @PageParam PageDTO pageDTO);
```

##### @Param
用于绑定方法参数到SQL中的参数。

```java
@SqlQuery("SELECT * FROM iam_user WHERE username = #{username} AND status = #{status}")
User findByUsernameAndStatus(@Param("username") String username, @Param("status") Integer status);
```

##### @PageParam
用于标识分页参数。

```java
@SqlPage(countSql = "...", dataSql = "...")
PageResult<User> findPage(@Param("tenantId") Long tenantId, @PageParam PageDTO pageDTO);
```

##### @QueryCondition
用于标记实体类字段的查询条件，支持自动查询条件构建。

```java
public class IamTenant {
    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String code;
    
    @QueryCondition(type = QueryCondition.QueryType.EQ)
    private Integer status;
    
    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "expire_time")
    private LocalDateTime expireTime;
}
```

###### 查询类型支持
- `EQ`: 等于
- `NE`: 不等于
- `LIKE`: 模糊查询
- `LIKE_LEFT`: 左模糊
- `LIKE_RIGHT`: 右模糊
- `GT`: 大于
- `GE`: 大于等于
- `LT`: 小于
- `LE`: 小于等于
- `IN`: IN查询
- `NOT_IN`: NOT IN查询
- `BETWEEN`: 范围查询
- `IS_NULL`: IS NULL
- `IS_NOT_NULL`: IS NOT NULL

#### 使用示例

##### 基础SQL查询

```java
@AutoRepository
public interface UserRepository extends BaseRepository<User, UserMapper> {
    
    // 继承MyBatis-Plus的所有方法：
    // save(), update(), remove(), list(), page()等
    
    // 自定义SQL查询
    @SqlQuery("SELECT * FROM iam_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    // 多表关联查询
    @SqlQuery("""
        SELECT u.*, r.role_name 
        FROM iam_user u 
        LEFT JOIN iam_user_role ur ON u.id = ur.user_id 
        LEFT JOIN iam_role r ON ur.role_id = r.id 
        WHERE u.id = #{userId}
    """)
    UserWithRoleDTO findUserWithRoles(@Param("userId") Long userId);
    
    // 分页查询
    @SqlPage(
        countSql = "SELECT COUNT(*) FROM iam_user WHERE tenant_id = #{tenantId}",
        dataSql = "SELECT * FROM iam_user WHERE tenant_id = #{tenantId} ORDER BY create_time DESC"
    )
    PageResult<User> findPageByTenant(@Param("tenantId") Long tenantId, @PageParam PageDTO pageDTO);
}
```

##### 复杂多表查询

```java
@SqlQuery("""
    SELECT u.*, r.role_name, p.permission_name
    FROM iam_user u 
    LEFT JOIN iam_user_role ur ON u.id = ur.user_id 
    LEFT JOIN iam_role r ON ur.role_id = r.id 
    LEFT JOIN iam_role_permission rp ON r.id = rp.role_id 
    LEFT JOIN iam_permission p ON rp.permission_id = p.id 
    WHERE u.tenant_id = #{tenantId}
    AND u.status = #{status}
    ORDER BY u.create_time DESC
""")
List<UserWithRoleAndPermissionDTO> findUsersWithRoleAndPermission(
    @Param("tenantId") Long tenantId, 
    @Param("status") Integer status
);
```

##### 聚合查询

```java
@SqlQuery("""
    SELECT 
        COUNT(*) as total_users,
        COUNT(CASE WHEN status = 1 THEN 1 END) as active_users,
        AVG(permission_count) as avg_permissions
    FROM (
        SELECT u.id, u.status, COUNT(p.id) as permission_count
        FROM iam_user u 
        LEFT JOIN iam_user_role ur ON u.id = ur.user_id 
        LEFT JOIN iam_role_permission rp ON ur.role_id = rp.role_id 
        LEFT JOIN iam_permission p ON rp.permission_id = p.id 
        WHERE u.tenant_id = #{tenantId}
        GROUP BY u.id, u.status
    ) user_stats
""")
UserStatisticsDTO getUserStatistics(@Param("tenantId") Long tenantId);
```

##### 混合使用MyBatis-Plus和注解SQL

```java
@AutoService
public interface UserService extends IService<User> {
    
    // MyBatis-Plus方法（继承）
    // save(), update(), remove(), list(), page()等
    
    // 注解SQL方法
    @SqlQuery("SELECT * FROM iam_user WHERE id = #{id}")
    UserDTO findById(@Param("id") Long id);
    
    // 业务方法：先保存，再查询
    default UserDTO createAndGetUser(User user) {
        // 使用MyBatis-Plus保存
        save(user);
        
        // 使用注解SQL查询
        return findById(user.getId());
    }
}
```

#### 最佳实践

##### 1. 分层使用
- **Repository层**：单表操作使用MyBatis-Plus，复杂查询使用注解SQL
- **Service层**：业务逻辑聚合，复杂查询使用注解SQL
- **Controller层**：直接调用Service方法

##### 2. SQL编写规范
- 使用文本块（"""）编写多行SQL
- 合理使用参数绑定，避免SQL注入
- 复杂查询添加适当的注释

##### 3. 性能优化
- 合理使用分页查询
- 避免N+1查询问题
- 使用索引优化查询性能

##### 4. 错误处理
- SQL执行异常会自动抛出RuntimeException
- 建议在Controller层统一处理异常

#### 注意事项

1. **参数绑定**：必须使用@Param注解绑定参数
2. **返回类型**：确保SQL查询结果与返回类型匹配
3. **分页参数**：分页查询必须使用@PageParam注解
4. **事务处理**：更新操作默认在事务中执行
5. **缓存使用**：可以通过注解配置缓存策略

#### 工作原理

1. **自动扫描**: 框架启动时自动扫描所有带有 `@AutoRepository` 注解的接口
2. **动态代理**: 为每个接口生成动态代理对象
3. **方法映射**: 将 Repository 方法调用映射到 MyBatis-Plus 的 ServiceImpl 方法
4. **Bean注册**: 自动将生成的代理注册为 Spring Bean

#### 使用示例

```java
// 1. 定义实体类
@Entity
@Table(name = "iam_tenant")
public class IamTenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String code;
    private String description;
    private String status;
    private LocalDateTime expireTime;
    
    // getters and setters...
}

// 2. 定义Mapper接口
@Mapper
public interface TenantMapper extends BaseMapper<IamTenant> {
    // 可以添加自定义的SQL方法
}

// 3. 定义Repository接口（无需实现类）
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 框架自动提供所有BaseRepository方法
    // 无需手写任何实现代码
}

// 4. 在Service中使用
@Service
public class TenantServiceImpl {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    public boolean addOrModify(IamTenant tenant) {
        // 直接调用，框架自动处理
        return tenantsRepository.save(tenant);
    }
    
    public IamTenant getById(Long id) {
        return tenantsRepository.getById(id);
    }
    
    public List<IamTenant> getAllTenants() {
        return tenantsRepository.list();
    }
    
    public boolean deleteTenant(Long id) {
        return tenantsRepository.removeById(id);
    }
}
```

#### 支持的方法

```java
// 基础CRUD操作
repository.save(entity);                    // 保存实体
repository.getById(id);                     // 根据ID查询
repository.updateById(entity);              // 根据ID更新
repository.removeById(id);                  // 根据ID删除

// 批量操作
repository.saveBatch(entityList);           // 批量保存
repository.updateBatchById(entityList);     // 批量更新
repository.removeByIds(idList);             // 批量删除

// 查询操作
repository.list();                          // 查询所有
repository.list(queryWrapper);              // 条件查询
repository.page(page);                      // 分页查询
repository.count();                         // 统计数量
repository.count(queryWrapper);             // 条件统计

// 链式操作
repository.lambdaQuery()                    // Lambda查询
repository.lambdaUpdate()                   // Lambda更新
```

#### 高级功能

##### 1. 统一分页查询（最新功能）

框架提供了统一的分页查询功能，基于 `PageDTO` 和 `PageResult`，支持自动查询条件构建。

###### 设计理念
- **统一返回类型**：所有分页方法返回 `PageResult<T>` 而不是 `IPage<T>`
- **统一参数约定**：所有分页请求参数都要继承 `PageDTO`
- **接口简化**：只保留一个 `pageWithCondition(D queryDTO)` 方法

###### 使用示例

```java
// 1. 定义分页DTO（继承PageDTO）
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TenantsPageDTO extends PageDTO {

    @QueryCondition(type = QueryCondition.QueryType.IN)
    private List<Integer> status;

    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String code;

    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String description;

    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "expireTime")
    private LocalDateTime expireTimeStart;

    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "expireTime")
    private LocalDateTime expireTimeEnd;
}

// 2. Repository层（自动获得分页方法）
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 继承BaseRepository后，自动获得：
    // - pageWithCondition(D queryDTO) 返回 PageResult<T>
}

// 3. Service层
@Service
public class TenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    // 统一的分页查询方法
    public PageResult<IamTenant> getTenantsPage(TenantsPageDTO params) {
        return tenantsRepository.pageWithCondition(params);
    }
}

// 4. Controller层
@RestController
public class TenantController {
    
    @PostMapping("/tenant/page")
    public Result<PageResult<IamTenant>> getTenantsPage(@RequestBody TenantsPageDTO request) {
        PageResult<IamTenant> result = tenantService.getTenantsPage(request);
        return Result.success(result);
    }
}
```

###### 前端请求示例

```json
{
  "pageNo": 1,
  "pageSize": 10,
  "code": "tenant",
  "status": [1, 2],
  "description": "测试",
  "orderByList": [
    {
      "field": "createTime",
      "direction": "DESC"
    },
    {
      "field": "code",
      "direction": "ASC"
    }
  ]
}
```

###### 返回结果示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "code": "tenant001",
        "description": "测试租户",
        "status": 1,
        "createTime": "2024-12-19T10:00:00"
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

###### 优势
1. **接口简洁**：只有一个分页方法，使用简单
2. **类型统一**：所有分页都使用 `PageDTO` 和 `PageResult`
3. **功能完整**：支持分页、排序、查询条件等所有功能
4. **扩展性好**：未来可以在 `PageDTO` 中添加更多功能
5. **约定明确**：所有分页请求参数都要继承 `PageDTO`

##### 2. 自定义SQL查询 (@SqlQuery)

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 基础查询
    @SqlQuery("SELECT * FROM iam_tenant WHERE code = #{code}")
    IamTenant findByCode(@Param("code") String code);
    
    // 条件查询
    @SqlQuery("SELECT * FROM iam_tenant WHERE status = #{status}")
    List<IamTenant> findByStatus(@Param("status") String status);
    
    // 复杂查询
    @SqlQuery("SELECT * FROM iam_tenant WHERE create_time >= #{startTime} AND create_time <= #{endTime}")
    List<IamTenant> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
    
    // 带缓存的查询
    @SqlQuery(value = "SELECT * FROM iam_tenant WHERE id = #{id}", 
              useCache = true, cacheTimeout = 600)
    IamTenant findByIdWithCache(@Param("id") Long id);
    
    // 自定义返回类型
    @SqlQuery(value = "SELECT COUNT(*) as count FROM iam_tenant WHERE status = #{status}", 
              resultType = Long.class)
    Long countByStatus(@Param("status") String status);
    
    // 关联查询
    @SqlQuery("""
        SELECT t.*, u.username as creator_name 
        FROM iam_tenant t 
        LEFT JOIN iam_user u ON t.creator_id = u.id 
        WHERE t.status = #{status}
        """)
    List<Map<String, Object>> findTenantsWithCreator(@Param("status") String status);
}
```

##### 2. SQL更新操作 (@SqlUpdate)

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 基础更新
    @SqlUpdate("UPDATE iam_tenant SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // 批量更新
    @SqlUpdate("UPDATE iam_tenant SET description = #{description} WHERE code IN (${codes})")
    int updateDescriptionByCodes(@Param("description") String description, 
                                @Param("codes") List<String> codes);
    
    // 条件更新
    @SqlUpdate("UPDATE iam_tenant SET expire_time = #{expireTime} WHERE status = #{status}")
    int updateExpireTimeByStatus(@Param("expireTime") LocalDateTime expireTime, 
                                @Param("status") String status);
    
    // 非事务更新
    @SqlUpdate(value = "UPDATE iam_tenant SET last_access_time = NOW() WHERE id = #{id}", 
               transactional = false)
    int updateLastAccessTime(@Param("id") Long id);
    
    // 复杂更新
    @SqlUpdate("""
        UPDATE iam_tenant 
        SET status = #{newStatus}, 
            update_time = NOW(), 
            update_user = #{updateUser} 
        WHERE id = #{id} AND status = #{oldStatus}
        """)
    int updateStatusWithCondition(@Param("id") Long id, 
                                 @Param("oldStatus") String oldStatus,
                                 @Param("newStatus") String newStatus,
                                 @Param("updateUser") String updateUser);
}
```

##### 3. 分页查询 (@SqlPage)

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 基础分页查询
    @SqlPage(
        countSql = "SELECT COUNT(*) FROM iam_tenant WHERE status = #{status}",
        dataSql = "SELECT * FROM iam_tenant WHERE status = #{status} ORDER BY create_time DESC"
    )
    PageResult<IamTenant> findPageByStatus(@Param("status") String status, @PageParam PageDTO page);
    
    // 复杂分页查询
    @SqlPage(
        countSql = """
            SELECT COUNT(*) FROM iam_tenant t 
            LEFT JOIN iam_user u ON t.creator_id = u.id 
            WHERE t.status = #{status} AND u.username LIKE CONCAT('%', #{keyword}, '%')
            """,
        dataSql = """
            SELECT t.*, u.username as creator_name 
            FROM iam_tenant t 
            LEFT JOIN iam_user u ON t.creator_id = u.id 
            WHERE t.status = #{status} AND u.username LIKE CONCAT('%', #{keyword}, '%')
            ORDER BY t.create_time DESC
            """
    )
    PageResult<Map<String, Object>> findPageWithCreator(@Param("status") String status,
                                                       @Param("keyword") String keyword,
                                                       @PageParam PageDTO page);
    
    // 带缓存的分页查询
    @SqlPage(
        countSql = "SELECT COUNT(*) FROM iam_tenant WHERE status = #{status}",
        dataSql = "SELECT * FROM iam_tenant WHERE status = #{status} ORDER BY create_time DESC",
        useCache = true,
        cacheTimeout = 300
    )
    PageResult<IamTenant> findPageByStatusWithCache(@Param("status") String status, 
                                                   @PageParam PageDTO page);
}
```

##### 4. 参数绑定注解 (@Param)

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 基础参数绑定
    @SqlQuery("SELECT * FROM iam_tenant WHERE code = #{code} AND status = #{status}")
    IamTenant findByCodeAndStatus(@Param("code") String code, @Param("status") String status);
    
    // 集合参数绑定
    @SqlQuery("SELECT * FROM iam_tenant WHERE id IN (${ids})")
    List<IamTenant> findByIds(@Param("ids") List<Long> ids);
    
    // 对象参数绑定
    @SqlQuery("SELECT * FROM iam_tenant WHERE code = #{tenant.code} AND status = #{tenant.status}")
    IamTenant findByTenant(@Param("tenant") IamTenant tenant);
    
    // 可选参数绑定
    @SqlQuery("""
        SELECT * FROM iam_tenant 
        WHERE 1=1 
        <if test="code != null">AND code = #{code}</if>
        <if test="status != null">AND status = #{status}</if>
        """)
    List<IamTenant> findByCondition(@Param("code") String code, @Param("status") String status);
}
```

##### 5. 分页参数注解 (@PageParam)

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 基础分页参数
    @SqlPage(
        countSql = "SELECT COUNT(*) FROM iam_tenant",
        dataSql = "SELECT * FROM iam_tenant ORDER BY create_time DESC"
    )
    PageResult<IamTenant> findAll(@PageParam PageDTO page);
    
    // 自定义分页参数名
    @SqlPage(
        countSql = "SELECT COUNT(*) FROM iam_tenant WHERE status = #{status}",
        dataSql = "SELECT * FROM iam_tenant WHERE status = #{status} ORDER BY create_time DESC"
    )
    PageResult<IamTenant> findPageByStatus(@Param("status") String status, 
                                          @PageParam(pageNum = "current", pageSize = "size") PageDTO page);
    
    // 带排序的分页
    @SqlPage(
        countSql = "SELECT COUNT(*) FROM iam_tenant",
        dataSql = "SELECT * FROM iam_tenant ORDER BY ${orderBy}"
    )
    PageResult<IamTenant> findAllWithOrder(@PageParam(orderBy = "orderBy") PageDTO page);
}
```

##### 6. 多数据源支持

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    @DataSource("primary")
    IamTenant getFromPrimary(Long id);
    
    @DataSource("secondary")
    IamTenant getFromSecondary(Long id);
    
    @DataSource("readonly")
    List<IamTenant> getAllFromReadonly();
    
    @DataSource("primary")
    @SqlUpdate("UPDATE iam_tenant SET status = #{status} WHERE id = #{id}")
    int updateStatusInPrimary(@Param("id") Long id, @Param("status") String status);
}
```

##### 7. Lambda查询注解 (@LambdaQuery)

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 基础Lambda查询
    @LambdaQuery
    List<IamTenant> findByStatus(String status);
    
    // 带分页的Lambda查询
    @LambdaQuery(enablePage = true, defaultOrderBy = "createTime")
    PageResult<IamTenant> findPageByStatus(String status, @PageParam PageDTO page);
    
    // 复杂Lambda查询
    @LambdaQuery(enablePage = true, defaultOrderBy = "createTime", defaultOrderDirection = "DESC")
    PageResult<IamTenant> searchTenants(String keyword, String status, 
                                       LocalDateTime startTime, LocalDateTime endTime, 
                                       @PageParam PageDTO page);
    
    // 使用Consumer参数的Lambda查询
    @LambdaQuery
    List<IamTenant> findByCondition(Consumer<LambdaQueryWrapper<IamTenant>> condition);
    
    // 带缓存的Lambda查询
    @LambdaQuery(useCache = true, cacheTimeout = 600)
    List<IamTenant> findActiveTenantsWithCache();
}
```

**注意**: `@LambdaQuery`注解目前主要用于标记方法，实际查询逻辑需要在Service层使用`LambdaQueryBuilder`来实现。

##### 8. Lambda查询构建器使用

```java
@Service
public class TenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    public List<IamTenant> findTenantsByCondition(String keyword, String status) {
        // 使用Lambda查询构建器
        return LambdaQueryBuilder.of(tenantsRepository.getBaseMapper())
            .like(IamTenant::getCode, keyword)
            .or()
            .like(IamTenant::getDescription, keyword)
            .eq(IamTenant::getStatus, status)
            .orderByDesc(IamTenant::getCreateTime)
            .list();
    }
    
    public PageResult<IamTenant> findTenantsPage(String keyword, String status, PageDTO page) {
        return LambdaQueryBuilder.of(tenantsRepository.getBaseMapper())
            .where(wrapper -> wrapper
                .like(IamTenant::getCode, keyword)
                .or()
                .like(IamTenant::getDescription, keyword)
            )
            .eq(IamTenant::getStatus, status)
            .orderByDesc(IamTenant::getCreateTime)
            .page(page);
    }
    
    public List<IamTenant> findTenantsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return LambdaQueryBuilder.of(tenantsRepository.getBaseMapper())
            .ge(IamTenant::getCreateTime, startTime)
            .le(IamTenant::getCreateTime, endTime)
            .orderByDesc(IamTenant::getCreateTime)
            .list();
    }
    
    public List<IamTenant> findTenantsByIds(List<Long> ids) {
        return LambdaQueryBuilder.of(tenantsRepository.getBaseMapper())
            .in(IamTenant::getId, ids)
            .orderByAsc(IamTenant::getCode)
            .list();
    }
}
```

##### 9. 自动Service注解 (@AutoService)

```java
// 定义Service接口
@AutoService
public interface TenantBusinessService {
    
    // 业务方法定义
    boolean createTenantWithValidation(IamTenant tenant);
    
    List<IamTenant> getActiveTenants();
    
    void processTenantExpiration();
    
    @SqlQuery("SELECT * FROM iam_tenant WHERE status = 'ACTIVE'")
    List<IamTenant> getActiveTenantsDirect();
}

// 在Controller中使用
@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    
    @Autowired
    private TenantBusinessService tenantBusinessService; // 自动注入，无需实现类
    
    @PostMapping("/business/create")
    public Result<Boolean> createTenantBusiness(@RequestBody IamTenant tenant) {
        boolean success = tenantBusinessService.createTenantWithValidation(tenant);
        return Result.success(success);
    }
}
```

#### 注解参考表

| 注解 | 用途 | 属性 | 示例 |
|------|------|------|------|
| `@AutoRepository` | 自动生成Repository实现 | `value` (可选) | `@AutoRepository` |
| `@AutoService` | 自动生成Service实现 | `value`, `enableCache`, `cachePrefix` | `@AutoService(enableCache = true)` |
| `@SqlQuery` | 定义SQL查询语句 | `value`, `resultType`, `useCache`, `cacheTimeout`, `timeout` | `@SqlQuery("SELECT * FROM table")` |
| `@SqlUpdate` | 定义SQL更新语句 | `value`, `timeout`, `transactional` | `@SqlUpdate("UPDATE table SET col = #{val}")` |
| `@SqlPage` | 定义分页查询语句 | `countSql`, `dataSql`, `resultType`, `useCache`, `cacheTimeout`, `timeout` | `@SqlPage(countSql = "...", dataSql = "...")` |
| `@Param` | 参数绑定 | `value` | `@Param("id") Long id` |
| `@PageParam` | 分页参数 | `pageNum`, `pageSize`, `orderBy` | `@PageParam(pageNum = "current")` |
| `@DataSource` | 数据源切换 | `value` | `@DataSource("primary")` |
| `@LambdaQuery` | Lambda查询 | `enablePage`, `defaultOrderBy`, `defaultOrderDirection`, `useCache`, `cacheTimeout`, `timeout` | `@LambdaQuery(enablePage = true)` |

#### 注解最佳实践

##### 1. 性能优化

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 使用缓存提高查询性能
    @SqlQuery(value = "SELECT * FROM iam_tenant WHERE id = #{id}", 
              useCache = true, cacheTimeout = 600)
    IamTenant findByIdWithCache(@Param("id") Long id);
    
    // 设置合理的超时时间
    @SqlQuery(value = "SELECT * FROM iam_tenant WHERE status = #{status}", 
              timeout = 10)
    List<IamTenant> findByStatus(@Param("status") String status);
    
    // 非事务操作提高性能
    @SqlUpdate(value = "UPDATE iam_tenant SET last_access_time = NOW() WHERE id = #{id}", 
               transactional = false)
    int updateLastAccessTime(@Param("id") Long id);
}
```

##### 2. 安全性考虑

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 使用参数绑定防止SQL注入
    @SqlQuery("SELECT * FROM iam_tenant WHERE code = #{code}")
    IamTenant findByCode(@Param("code") String code);
    
    // 避免使用字符串拼接
    // ❌ 错误示例
    // @SqlQuery("SELECT * FROM iam_tenant WHERE code = '" + code + "'")
    
    // ✅ 正确示例
    @SqlQuery("SELECT * FROM iam_tenant WHERE code = #{code}")
    IamTenant findByCode(@Param("code") String code);
}
```

##### 3. 事务管理

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 默认启用事务
    @SqlUpdate("UPDATE iam_tenant SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // 明确指定事务
    @SqlUpdate(value = "UPDATE iam_tenant SET status = #{status} WHERE id = #{id}", 
               transactional = true)
    int updateStatusWithTransaction(@Param("id") Long id, @Param("status") String status);
    
    // 非事务操作
    @SqlUpdate(value = "UPDATE iam_tenant SET last_access_time = NOW() WHERE id = #{id}", 
               transactional = false)
    int updateLastAccessTime(@Param("id") Long id);
}
```

##### 4. 错误处理

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 设置合理的超时时间
    @SqlQuery(value = "SELECT * FROM iam_tenant WHERE status = #{status}", 
              timeout = 30)
    List<IamTenant> findByStatus(@Param("status") String status);
    
    // 使用缓存减少数据库压力
    @SqlQuery(value = "SELECT COUNT(*) FROM iam_tenant WHERE status = #{status}", 
              useCache = true, cacheTimeout = 300)
    Long countByStatus(@Param("status") String status);
}
```

#### 配置说明

```yaml
synapse:
  databases:
    auto-repository:
      enabled: true                    # 启用@AutoRepository功能
      scan-packages:                   # 扫描的包路径
        - com.indigo.iam.repository
        - com.indigo.business.repository
      auto-register: true              # 自动注册为Spring Bean
      enable-sql-log: true             # 启用SQL日志
    
    sql-annotations:
      enabled: true                    # 启用SQL注解功能
      cache:
        enabled: true                  # 启用缓存功能
        default-timeout: 300           # 默认缓存时间（秒）
        max-size: 1000                 # 最大缓存条目数
      timeout:
        default: 30                    # 默认超时时间（秒）
        max: 300                       # 最大超时时间（秒）
      transaction:
        default-enabled: true          # 默认启用事务
        rollback-on-exception: true    # 异常时回滚
    
    auto-service:
      enabled: true                    # 启用@AutoService功能
      scan-packages:                   # 扫描的包路径
        - com.indigo.iam.service
        - com.indigo.business.service
      cache:
        enabled: false                 # 默认不启用缓存
        prefix: "service:"             # 缓存前缀
```

### 动态数据源切换

#### 基于注解的切换

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{userId}")
    @DataSource("primary")
    public Result<User> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return Result.success(user);
    }
    
    @GetMapping("/readonly/{userId}")
    @DataSource("secondary")
    public Result<User> getUserReadOnly(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return Result.success(user);
    }
}
```

#### 基于方法的切换

```java
@Service
public class DataService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    public void processData() {
        // 从主库读取用户数据
        DynamicDataSourceContextHolder.setDataSource("primary");
        List<User> users = userRepository.findAll();
        
        // 切换到从库读取订单数据
        DynamicDataSourceContextHolder.setDataSource("secondary");
        List<Order> orders = orderRepository.findAll();
        
        // 处理数据...
        
        // 清理数据源上下文
        DynamicDataSourceContextHolder.clearDataSource();
    }
}
```

### 负载均衡

```java
@Service
public class LoadBalancedService {
    
    @Autowired
    private DataSourceLoadBalancer loadBalancer;
    
    public User getUserWithLoadBalance(Long userId) {
        // 使用负载均衡选择数据源
        String dataSource = loadBalancer.selectDataSource("user-read");
        
        DynamicDataSourceContextHolder.setDataSource(dataSource);
        try {
            return userRepository.findById(userId);
        } finally {
            DynamicDataSourceContextHolder.clearDataSource();
        }
    }
}
```

### 健康检查

```java
@Component
@Slf4j
public class DatabaseHealthMonitor {
    
    @Autowired
    private DataSourceHealthChecker healthChecker;
    
    @Scheduled(fixedRate = 30000) // 每30秒检查一次
    public void checkDatabaseHealth() {
        Map<String, Boolean> healthStatus = healthChecker.checkAllDataSources();
        
        for (Map.Entry<String, Boolean> entry : healthStatus.entrySet()) {
            String dataSource = entry.getKey();
            Boolean isHealthy = entry.getValue();
            
            if (!isHealthy) {
                log.warn("数据源 {} 健康检查失败", dataSource);
                // 可以触发告警或自动切换
            } else {
                log.debug("数据源 {} 健康检查通过", dataSource);
            }
        }
    }
}
```

### 多数据库支持

#### MySQL 配置

```yaml
synapse:
  databases:
    mysql-primary:
      url: jdbc:mysql://localhost:3306/primary_db?useSSL=false&serverTimezone=UTC
      username: root
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
      type: mysql
      pool-type: hikari
      hikari:
        maximum-pool-size: 20
        minimum-idle: 5
        connection-timeout: 30000
```

#### PostgreSQL 配置

```yaml
synapse:
  databases:
    postgres-primary:
      url: jdbc:postgresql://localhost:5432/primary_db
      username: postgres
      password: password
      driver-class-name: org.postgresql.Driver
      type: postgresql
      pool-type: hikari
      hikari:
        maximum-pool-size: 20
        minimum-idle: 5
        connection-timeout: 30000
```

#### Oracle 配置

```yaml
synapse:
  databases:
    oracle-primary:
      url: jdbc:oracle:thin:@localhost:1521:orcl
      username: system
      password: password
      driver-class-name: oracle.jdbc.OracleDriver
      type: oracle
      pool-type: hikari
      hikari:
        maximum-pool-size: 20
        minimum-idle: 5
        connection-timeout: 30000
```

## ⚙️ 配置选项

### 基础配置

```yaml
synapse:
  databases:
    enabled: true
    auto-configuration: true
    default-data-source: primary
    show-sql: false
    format-sql: true
```

### 数据源配置

```yaml
synapse:
  databases:
    data-sources:
      primary:
        url: jdbc:mysql://localhost:3306/primary_db
        username: root
        password: password
        driver-class-name: com.mysql.cj.jdbc.Driver
        type: mysql
        pool-type: hikari
        weight: 100
        enabled: true
      secondary:
        url: jdbc:mysql://localhost:3306/secondary_db
        username: root
        password: password
        driver-class-name: com.mysql.cj.jdbc.Driver
        type: mysql
        pool-type: hikari
        weight: 50
        enabled: true
```

### 健康检查配置

```yaml
synapse:
  databases:
    health-check:
      enabled: true
      interval: 30000
      timeout: 5000
      query: SELECT 1
      retry-count: 3
      retry-interval: 1000
```

### 负载均衡配置

```yaml
synapse:
  databases:
    load-balance:
      enabled: true
      strategy: round-robin
      weight-based: true
      health-check-enabled: true
      failover-enabled: true
```

## 📝 使用示例

### 完整的服务示例

#### 使用@AutoRepository的完整示例

##### 1. 实体类 (IamTenant.java)

```java
@Data
@TableName("iam_tenant")
public class IamTenant {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String code;
    
    private String description;
    
    private String status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Long creatorId;
    
    private String creatorName;
}
```

##### 2. Mapper接口 (TenantMapper.java)

```java
@Mapper
public interface TenantMapper extends BaseMapper<IamTenant> {
    // 可以添加自定义的复杂查询方法
}
```

##### 3. Repository接口 (TenantsRepository.java)

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // ==================== Lambda查询示例 ====================
    
    /**
     * 基础Lambda查询 - 根据状态查询
     */
    @LambdaQuery
    List<IamTenant> findByStatus(String status);
    
    /**
     * 带分页的Lambda查询
     */
    @LambdaQuery(enablePage = true, defaultOrderBy = "createTime", defaultOrderDirection = "DESC")
    PageResult<IamTenant> findPageByStatus(String status, @PageParam PageDTO page);
    
    /**
     * 复杂Lambda查询 - 多条件搜索
     */
    @LambdaQuery(enablePage = true, defaultOrderBy = "createTime", defaultOrderDirection = "DESC")
    PageResult<IamTenant> searchTenants(String keyword, String status, 
                                       LocalDateTime startTime, LocalDateTime endTime, 
                                       @PageParam PageDTO page);
    
    /**
     * 使用Consumer参数的Lambda查询 - 灵活的条件构建
     */
    @LambdaQuery
    List<IamTenant> findByCondition(Consumer<LambdaQueryWrapper<IamTenant>> condition);
    
    /**
     * 带缓存的Lambda查询
     */
    @LambdaQuery(useCache = true, cacheTimeout = 600)
    List<IamTenant> findActiveTenantsWithCache();
    
    /**
     * 根据代码查询
     */
    @LambdaQuery
    IamTenant findByCode(String code);
    
    /**
     * 根据ID列表查询
     */
    @LambdaQuery
    List<IamTenant> findByIds(List<Long> ids);
    
    // ==================== SQL查询示例 ====================
    
    /**
     * 传统SQL查询 - 关联查询
     */
    @SqlQuery("""
        SELECT t.*, u.username as creator_name 
        FROM iam_tenant t 
        LEFT JOIN iam_user u ON t.creator_id = u.id 
        WHERE t.status = #{status}
        """)
    List<IamTenant> findTenantsWithCreator(String status);
    
    /**
     * SQL更新示例
     */
    @SqlUpdate("UPDATE iam_tenant SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * SQL分页查询示例
     */
    @SqlPage(
        countSql = "SELECT COUNT(*) FROM iam_tenant WHERE status = #{status}",
        dataSql = "SELECT * FROM iam_tenant WHERE status = #{status} ORDER BY create_time DESC"
    )
    PageResult<IamTenant> findPageByStatusSql(String status, @PageParam PageDTO page);
}
```

##### 4. Service类 (TenantService.java)

```java
@Service
public class TenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 创建租户
     */
    public boolean createTenant(IamTenant tenant) {
        return tenantsRepository.save(tenant);
    }
    
    /**
     * 根据ID查询租户
     */
    public IamTenant getTenantById(Long id) {
        return tenantsRepository.getById(id);
    }
    
    /**
     * 更新租户
     */
    public boolean updateTenant(IamTenant tenant) {
        return tenantsRepository.updateById(tenant);
    }
    
    /**
     * 删除租户
     */
    public boolean deleteTenant(Long id) {
        return tenantsRepository.removeById(id);
    }
    
    // ==================== Lambda查询使用 ====================
    
    /**
     * 使用Lambda查询 - 根据状态查询
     */
    public List<IamTenant> getTenantsByStatus(String status) {
        return tenantsRepository.findByStatus(status);
    }
    
    /**
     * 使用Lambda查询 - 分页查询
     */
    public PageResult<IamTenant> getTenantsPage(String status, PageDTO page) {
        return tenantsRepository.findPageByStatus(status, page);
    }
    
    /**
     * 使用Lambda查询 - 复杂搜索
     */
    public PageResult<IamTenant> searchTenants(String keyword, String status, 
                                              LocalDateTime startTime, LocalDateTime endTime, 
                                              PageDTO page) {
        return tenantsRepository.searchTenants(keyword, status, startTime, endTime, page);
    }
    
    /**
     * 使用Lambda查询 - 灵活条件查询
     */
    public List<IamTenant> findTenantsByCondition(String keyword, String status) {
        return tenantsRepository.findByCondition(wrapper -> wrapper
            .like(IamTenant::getCode, keyword)
            .or()
            .like(IamTenant::getDescription, keyword)
            .eq(IamTenant::getStatus, status)
            .orderByDesc(IamTenant::getCreateTime)
        );
    }
    
    /**
     * 使用Lambda查询构建器 - 更灵活的查询
     */
    public List<IamTenant> findTenantsByBuilder(String keyword, String status) {
        return LambdaQueryBuilder.of(tenantsRepository.getBaseMapper())
            .like(IamTenant::getCode, keyword)
            .or()
            .like(IamTenant::getDescription, keyword)
            .eq(IamTenant::getStatus, status)
            .orderByDesc(IamTenant::getCreateTime)
            .list();
    }
    
    /**
     * 使用Lambda查询构建器 - 分页查询
     */
    public PageResult<IamTenant> findTenantsPageByBuilder(String keyword, String status, PageDTO page) {
        return LambdaQueryBuilder.of(tenantsRepository.getBaseMapper())
            .where(wrapper -> wrapper
                .like(IamTenant::getCode, keyword)
                .or()
                .like(IamTenant::getDescription, keyword)
            )
            .eq(IamTenant::getStatus, status)
            .orderByDesc(IamTenant::getCreateTime)
            .page(page);
    }
    
    // ==================== SQL查询使用 ====================
    
    /**
     * 使用SQL查询 - 关联查询
     */
    public List<IamTenant> getTenantsWithCreator(String status) {
        return tenantsRepository.findTenantsWithCreator(status);
    }
    
    /**
     * 使用SQL更新
     */
    public int updateTenantStatus(Long id, String status) {
        return tenantsRepository.updateStatus(id, status);
    }
    
    /**
     * 使用SQL分页查询
     */
    public PageResult<IamTenant> getTenantsPageBySql(String status, PageDTO page) {
        return tenantsRepository.findPageByStatusSql(status, page);
    }
    
    // ==================== 批量操作 ====================
    
    /**
     * 批量查询
     */
    public List<IamTenant> getTenantsByIds(List<Long> ids) {
        return tenantsRepository.findByIds(ids);
    }
    
    /**
     * 批量保存
     */
    public boolean saveBatch(List<IamTenant> tenants) {
        return tenantsRepository.saveBatch(tenants);
    }
    
    /**
     * 批量更新
     */
    public boolean updateBatchById(List<IamTenant> tenants) {
        return tenantsRepository.updateBatchById(tenants);
    }
    
    /**
     * 批量删除
     */
    public boolean removeByIds(List<Long> ids) {
        return tenantsRepository.removeByIds(ids);
    }
}
```

#### 传统方式的对比示例

```java
@Service
@Slf4j
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DataSourceHealthChecker healthChecker;
    
    @DataSource("primary")
    public User createUser(User user) {
        log.info("在主库创建用户: {}", user.getUsername());
        
        // 验证数据源健康状态
        if (!healthChecker.isHealthy("primary")) {
            throw new BusinessException("主库不可用");
        }
        
        return userRepository.save(user);
    }
    
    @DataSource("secondary")
    public User getUserById(Long userId) {
        log.info("从从库查询用户: {}", userId);
        
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        return user;
    }
    
    public User getUserWithFallback(Long userId) {
        // 优先从从库查询
        try {
            DynamicDataSourceContextHolder.setDataSource("secondary");
            return userRepository.findById(userId);
        } catch (Exception e) {
            log.warn("从库查询失败，切换到主库: {}", e.getMessage());
            
            // 从库失败，切换到主库
            DynamicDataSourceContextHolder.setDataSource("primary");
            return userRepository.findById(userId);
        } finally {
            DynamicDataSourceContextHolder.clearDataSource();
        }
    }
    
    @DataSource("primary")
    public void updateUser(User user) {
        log.info("在主库更新用户: {}", user.getId());
        userRepository.save(user);
    }
    
    @DataSource("secondary")
    public List<User> getAllUsers() {
        log.info("从从库查询所有用户");
        return userRepository.findAll();
    }
}
```

### 控制器示例

#### 使用@AutoRepository的控制器示例

```java
@RestController
@RequestMapping("/api/tenants")
@Slf4j
public class TenantController {
    
    @Autowired
    private TenantService tenantService;
    
    @PostMapping
    @DataSource("primary")
    public Result<Boolean> createTenant(@RequestBody @Valid IamTenant tenant) {
        try {
            boolean success = tenantService.createTenant(tenant);
            return Result.success(success);
        } catch (Exception e) {
            log.error("创建租户失败", e);
            return Result.error("创建租户失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @DataSource("secondary")
    public Result<IamTenant> getTenant(@PathVariable Long id) {
        try {
            IamTenant tenant = tenantService.getTenantById(id);
            return Result.success(tenant);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询租户失败", e);
            return Result.error("查询租户失败");
        }
    }
    
    @GetMapping("/code/{code}")
    @DataSource("secondary")
    public Result<IamTenant> getTenantByCode(@PathVariable String code) {
        try {
            IamTenant tenant = tenantService.getTenantByCode(code);
            return Result.success(tenant);
        } catch (Exception e) {
            log.error("根据代码查询租户失败", e);
            return Result.error("查询租户失败");
        }
    }
    
    @GetMapping("/status/{status}")
    @DataSource("secondary")
    public Result<List<IamTenant>> getTenantsByStatus(@PathVariable String status) {
        try {
            List<IamTenant> tenants = tenantService.getTenantsByStatus(status);
            return Result.success(tenants);
        } catch (Exception e) {
            log.error("根据状态查询租户失败", e);
            return Result.error("查询租户失败");
        }
    }
    
    @GetMapping("/search")
    @DataSource("secondary")
    public Result<PageResult<IamTenant>> searchTenants(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            PageDTO page = new PageDTO(pageNum, pageSize);
            PageResult<IamTenant> result = tenantService.searchTenants(keyword, page);
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索租户失败", e);
            return Result.error("搜索租户失败");
        }
    }
    
    @PutMapping("/{id}")
    @DataSource("primary")
    public Result<Boolean> updateTenant(@PathVariable Long id, 
                                       @RequestBody @Valid IamTenant tenant) {
        try {
            tenant.setId(id);
            boolean success = tenantService.updateTenant(tenant);
            return Result.success(success);
        } catch (Exception e) {
            log.error("更新租户失败", e);
            return Result.error("更新租户失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @DataSource("primary")
    public Result<Boolean> deleteTenant(@PathVariable Long id) {
        try {
            boolean success = tenantService.deleteTenant(id);
            return Result.success(success);
        } catch (Exception e) {
            log.error("删除租户失败", e);
            return Result.error("删除租户失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    @DataSource("secondary")
    public Result<List<IamTenant>> getAllTenants() {
        try {
            List<IamTenant> tenants = tenantService.getAllTenants();
            return Result.success(tenants);
        } catch (Exception e) {
            log.error("查询所有租户失败", e);
            return Result.error("查询所有租户失败");
        }
    }
    
    @GetMapping("/count")
    @DataSource("secondary")
    public Result<Long> getTenantCount() {
        try {
            long count = tenantService.getTenantCount();
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计租户数量失败", e);
            return Result.error("统计失败");
        }
    }
}
```

#### 传统方式的控制器示例

```java
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @DataSource("primary")
    public Result<User> createUser(@RequestBody @Valid User user) {
        try {
            User createdUser = userService.createUser(user);
            return Result.success(createdUser);
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return Result.error("创建用户失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{userId}")
    @DataSource("secondary")
    public Result<User> getUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return Result.success(user);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询用户失败", e);
            return Result.error("查询用户失败");
        }
    }
    
    @GetMapping("/{userId}/fallback")
    public Result<User> getUserWithFallback(@PathVariable Long userId) {
        try {
            User user = userService.getUserWithFallback(userId);
            return Result.success(user);
        } catch (Exception e) {
            log.error("查询用户失败", e);
            return Result.error("查询用户失败");
        }
    }
    
    @PutMapping("/{userId}")
    @DataSource("primary")
    public Result<User> updateUser(@PathVariable Long userId, 
                                  @RequestBody @Valid User user) {
        try {
            user.setId(userId);
            userService.updateUser(user);
            return Result.success(user);
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    @DataSource("secondary")
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Result.success(users);
        } catch (Exception e) {
            log.error("查询所有用户失败", e);
            return Result.error("查询所有用户失败");
        }
    }
}
```

### 监控示例

```java
@Component
@Slf4j
public class DatabaseMonitor {
    
    @Autowired
    private DataSourceHealthChecker healthChecker;
    
    @Autowired
    private DataSourceLoadBalancer loadBalancer;
    
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void monitorDatabaseStatus() {
        log.info("=== 数据库状态监控 ===");
        
        // 健康检查
        Map<String, Boolean> healthStatus = healthChecker.checkAllDataSources();
        for (Map.Entry<String, Boolean> entry : healthStatus.entrySet()) {
            String dataSource = entry.getKey();
            Boolean isHealthy = entry.getValue();
            
            if (isHealthy) {
                log.info("数据源 {}: ✅ 健康", dataSource);
            } else {
                log.error("数据源 {}: ❌ 不健康", dataSource);
            }
        }
        
        // 负载均衡统计
        Map<String, Object> loadBalanceStats = loadBalancer.getStatistics();
        log.info("负载均衡统计: {}", loadBalanceStats);
    }
}
```

## 🧪 测试

### 单元测试示例

#### @AutoRepository测试示例

```java
@ExtendWith(MockitoExtension.class)
class TenantServiceTest {
    
    @Mock
    private TenantsRepository tenantsRepository;
    
    @Mock
    private DataSourceHealthChecker healthChecker;
    
    @InjectMocks
    private TenantService tenantService;
    
    @Test
    void createTenant_Success() {
        // Given
        IamTenant tenant = new IamTenant();
        tenant.setCode("test");
        tenant.setDescription("test tenant");
        
        when(healthChecker.isHealthy("primary")).thenReturn(true);
        when(tenantsRepository.save(any(IamTenant.class))).thenReturn(true);
        
        // When
        boolean result = tenantService.createTenant(tenant);
        
        // Then
        assertTrue(result);
        verify(healthChecker).isHealthy("primary");
        verify(tenantsRepository).save(tenant);
    }
    
    @Test
    void getTenantById_Success() {
        // Given
        Long id = 1L;
        IamTenant tenant = new IamTenant();
        tenant.setId(id);
        tenant.setCode("test");
        
        when(tenantsRepository.getById(id)).thenReturn(tenant);
        
        // When
        IamTenant result = tenantService.getTenantById(id);
        
        // Then
        assertEquals(tenant, result);
        verify(tenantsRepository).getById(id);
    }
    
```

## 📝 更新日志

### v1.0.0 (2024-12-19)

#### 🎉 重大改进：统一分页查询

##### 新增功能
- ✅ **统一返回类型**：所有分页方法现在返回 `PageResult<T>` 而不是 `IPage<T>`
- ✅ **接口简化**：只保留一个 `pageWithCondition(D queryDTO)` 方法
- ✅ **统一参数约定**：所有分页请求参数都要继承 `PageDTO`
- ✅ **自动查询条件**：支持 `@QueryCondition` 注解自动构建查询条件
- ✅ **排序支持**：支持通过 `orderByList` 进行多字段排序

##### 解决的问题
- 🔧 **修复分页问题**：解决了 MyBatis-Plus 分页插件在代理对象中的调用问题
- 🔧 **避免内存问题**：不再使用手动分页，避免大数据量时的内存爆炸
- 🔧 **提升性能**：排序在数据库层面进行，性能更好
- 🔧 **代码简化**：减少了复杂的反射调用和类型转换

##### 技术实现
- 使用 `LambdaQueryBuilder` 静态方法绕过代理对象问题
- 修改 `SqlMethodInterceptor` 直接调用 `LambdaQueryBuilder`
- 统一使用 `PageDTO` 作为分页参数，`PageResult` 作为返回类型

##### 迁移指南
```java
// 旧版本
public IPage<IamTenant> getTenantsPage(TenantsPageDTO params) {
    return tenantsRepository.pageWithCondition(params);
}

// 新版本
public PageResult<IamTenant> getTenantsPage(TenantsPageDTO params) {
    return tenantsRepository.pageWithCondition(params);
}
```

#### 🚀 其他改进
- ✅ **文档更新**：完善了使用指南和最佳实践
- ✅ **示例代码**：提供了完整的前后端调用示例
- ✅ **类型安全**：改进了泛型类型处理
- ✅ **SQL注解框架**：新增完整的SQL注解框架支持
- ✅ **多数据源支持**：完善了动态数据源和负载均衡功能

---

## 🎯 总结

Synapse Databases 框架通过以下方式简化了数据库操作：

### 核心价值
1. **开发效率**：无ServiceImpl，接口+注解即可使用
2. **代码质量**：类型安全，编译时检查
3. **性能优化**：自动读写分离，数据库层面排序
4. **维护性**：统一的分页查询，简洁的接口设计
5. **扩展性**：支持多种数据库和负载均衡策略

### 主要功能
- **@AutoRepository**：自动生成Repository实现，无需手写ServiceImpl
- **SQL注解框架**：通过注解直接定义SQL，支持复杂查询
- **统一分页查询**：基于PageDTO的统一分页查询
- **动态数据源**：支持多数据源和读写分离
- **负载均衡**：提供多种负载均衡策略
- **健康检查**：自动检测数据源健康状态

### 技术特色
- 完美集成MyBatis-Plus，获得完整CRUD功能
- 支持复杂的多表关联查询和聚合查询
- 自动读写分离，从库轮询负载均衡
- 类型安全的参数绑定和查询条件构建
- 统一的错误处理和监控机制
    @Test
    void findByCode_Success() {
        // Given
        String code = "test";
        IamTenant tenant = new IamTenant();
        tenant.setCode(code);
        
        when(tenantsRepository.findByCode(code)).thenReturn(tenant);
        
        // When
        IamTenant result = tenantService.getTenantByCode(code);
        
        // Then
        assertEquals(tenant, result);
        verify(tenantsRepository).findByCode(code);
    }
}
```

#### SQL注解测试示例

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "synapse.databases.enabled=true",
    "synapse.databases.auto-repository.enabled=true",
    "synapse.databases.sql-annotations.enabled=true"
})
class SqlAnnotationTest {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    @Test
    void testSqlQuery() {
        // Given
        String code = "test";
        
        // When
        IamTenant tenant = tenantsRepository.findByCode(code);
        
        // Then
        assertNotNull(tenant);
        assertEquals(code, tenant.getCode());
    }
    
    @Test
    void testSqlUpdate() {
        // Given
        Long id = 1L;
        String newStatus = "ACTIVE";
        
        // When
        int result = tenantsRepository.updateStatus(id, newStatus);
        
        // Then
        assertEquals(1, result);
    }
    
    @Test
    void testSqlPage() {
        // Given
        String status = "ACTIVE";
        PageDTO page = new PageDTO(1, 10);
        
        // When
        PageResult<IamTenant> result = tenantsRepository.findPageByStatus(status, page);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getTotal() >= 0);
    }
}
```

#### 传统方式测试示例

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private DataSourceHealthChecker healthChecker;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void createUser_Success() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        
        when(healthChecker.isHealthy("primary")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        User result = userService.createUser(user);
        
        // Then
        assertEquals(savedUser, result);
        verify(healthChecker).isHealthy("primary");
        verify(userRepository).save(user);
    }
    
    @Test
    void createUser_UnhealthyDataSource_ThrowsException() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        
        when(healthChecker.isHealthy("primary")).thenReturn(false);
        
        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.createUser(user);
        });
        
        verify(healthChecker).isHealthy("primary");
        verifyNoInteractions(userRepository);
    }
}
```

### 集成测试示例

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "synapse.databases.enabled=true",
    "synapse.databases.primary.url=jdbc:h2:mem:testdb",
    "synapse.databases.primary.driver-class-name=org.h2.Driver",
    "synapse.databases.primary.username=sa",
    "synapse.databases.primary.password="
})
class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void createAndGetUser_Integration() {
        // Given
        User user = new User();
        user.setUsername("integrationtest");
        user.setEmail("integration@example.com");
        
        // When
        User createdUser = userService.createUser(user);
        User retrievedUser = userService.getUserById(createdUser.getId());
        
        // Then
        assertNotNull(createdUser.getId());
        assertEquals("integrationtest", createdUser.getUsername());
        assertEquals(createdUser.getId(), retrievedUser.getId());
    }
}
```

## 📚 相关文档

- [Synapse Core](./synapse-core/README.md) - 核心框架
- [Synapse Cache](./synapse-cache/README.md) - 缓存框架
- [Synapse Events](./synapse-events/README.md) - 事件驱动框架
- [Synapse Security](./synapse-security/README.md) - 安全框架

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个框架。

## 📄 许可证

本项目采用 MIT 许可证。

---

**最后更新：** 2025-07-30  
**版本：** 1.1.0  
**维护者：** 史偕成 

## 🆕 更新日志

### v1.1.0 (2025-07-30)
- ✨ 新增 `@AutoRepository` 功能，自动生成Repository实现
- 🎯 完美集成MyBatis-Plus，获得完整CRUD功能
- 🔧 大幅减少样板代码，提升开发效率
- 📝 新增SQL注解支持：`@SqlQuery`、`@SqlUpdate`、`@SqlPage`
- 🏗️ 新增动态代理机制，自动注册Spring Bean
- 📚 完善文档和示例代码

### v1.0.0 (2025-07-20)
- 🚀 初始版本发布
- 🔄 支持动态数据源切换
- 🎯 支持多数据库类型
- ⚖️ 支持负载均衡
- 🏥 支持健康检查 