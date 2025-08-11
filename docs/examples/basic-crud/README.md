# 基础 CRUD 示例

## 📋 示例说明

本示例展示了 Synapse Framework 的基础 CRUD 操作，包括实体定义、Repository 接口、Service 层和 Controller 层的完整实现。

## 🏗️ 项目结构

```
basic-crud/
├── README.md
├── pom.xml
├── src/main/java/
│   └── com/indigo/examples/basic/
│       ├── entity/
│       │   └── User.java
│       ├── mapper/
│       │   └── UserMapper.java
│       ├── repository/
│       │   └── UserRepository.java
│       ├── service/
│       │   └── UserService.java
│       ├── controller/
│       │   └── UserController.java
│       └── dto/
│           ├── UserQueryDTO.java
│           └── UserCreateDTO.java
└── src/main/resources/
    ├── application.yml
    └── schema.sql
```

## 🚀 快速开始

### 1. 创建实体类

```java
@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String username;
    private String email;
    private String phone;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

### 2. 创建 Mapper

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 基础 CRUD 由 MyBatis-Plus 提供
    // 可以添加自定义查询方法
}
```

### 3. 创建 Repository

```java
@AutoRepository
public interface UserRepository extends BaseRepository<User, UserMapper> {
    // 框架自动提供所有基础 CRUD 方法
    // 无需编写 ServiceImpl
}
```

### 4. 创建 DTO

```java
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageDTO {
    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String username;
    
    @QueryCondition(type = QueryCondition.QueryType.EQ)
    private String email;
    
    @QueryCondition(type = QueryCondition.QueryType.EQ)
    private Integer status;
    
    // 静态工厂方法
    public static UserQueryDTO byUsername(String username) {
        return UserQueryDTO.builder().username(username).build();
    }
    
    public static UserQueryDTO byStatus(Integer status) {
        return UserQueryDTO.builder().status(status).build();
    }
}
```

### 5. 创建 Service

```java
@Service
@Slf4j
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // 基础 CRUD 操作
    public User getUserById(String id) {
        return userRepository.getById(id);
    }
    
    public boolean saveUser(User user) {
        return userRepository.save(user);
    }
    
    public boolean updateUser(User user) {
        return userRepository.updateById(user);
    }
    
    public boolean deleteUser(String id) {
        return userRepository.removeById(id);
    }
    
    public List<User> getAllUsers() {
        return userRepository.list();
    }
    
    // 条件查询
    public List<User> findUsers(UserQueryDTO query) {
        return userRepository.listWithCondition(query);
    }
    
    public PageResult<User> findUsersWithPage(UserQueryDTO query) {
        return userRepository.pageWithCondition(query);
    }
    
    // 使用静态工厂方法
    public List<User> getUsersByStatus(Integer status) {
        UserQueryDTO query = UserQueryDTO.byStatus(status);
        return userRepository.listWithCondition(query);
    }
    
    public List<User> getUsersByUsername(String username) {
        UserQueryDTO query = UserQueryDTO.byUsername(username);
        return userRepository.listWithCondition(query);
    }
    
    // 批量操作
    public boolean saveBatch(List<User> users) {
        return userRepository.saveBatch(users);
    }
    
    public boolean updateBatch(List<User> users) {
        return userRepository.updateBatchById(users);
    }
    
    public boolean removeBatch(List<String> ids) {
        return userRepository.removeByIds(ids);
    }
}
```

### 6. 创建 Controller

```java
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // 基础 CRUD 接口
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }
    
    @PostMapping
    public Result<Boolean> createUser(@RequestBody User user) {
        boolean success = userService.saveUser(user);
        return Result.success(success);
    }
    
    @PutMapping("/{id}")
    public Result<Boolean> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        boolean success = userService.updateUser(user);
        return Result.success(success);
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable String id) {
        boolean success = userService.deleteUser(id);
        return Result.success(success);
    }
    
    @GetMapping
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }
    
    // 条件查询接口
    @PostMapping("/list")
    public Result<List<User>> findUsers(@RequestBody UserQueryDTO query) {
        List<User> users = userService.findUsers(query);
        return Result.success(users);
    }
    
    @PostMapping("/page")
    public Result<PageResult<User>> findUsersWithPage(@RequestBody UserQueryDTO query) {
        PageResult<User> result = userService.findUsersWithPage(query);
        return Result.success(result);
    }
    
    // 便捷查询接口
    @GetMapping("/status/{status}")
    public Result<List<User>> getUsersByStatus(@PathVariable Integer status) {
        List<User> users = userService.getUsersByStatus(status);
        return Result.success(users);
    }
    
    @GetMapping("/username/{username}")
    public Result<List<User>> getUsersByUsername(@PathVariable String username) {
        List<User> users = userService.getUsersByUsername(username);
        return Result.success(users);
    }
    
    // 批量操作接口
    @PostMapping("/batch")
    public Result<Boolean> createUsers(@RequestBody List<User> users) {
        boolean success = userService.saveBatch(users);
        return Result.success(success);
    }
    
    @PutMapping("/batch")
    public Result<Boolean> updateUsers(@RequestBody List<User> users) {
        boolean success = userService.updateBatch(users);
        return Result.success(success);
    }
    
    @DeleteMapping("/batch")
    public Result<Boolean> deleteUsers(@RequestBody List<String> ids) {
        boolean success = userService.removeBatch(ids);
        return Result.success(success);
    }
}
```

## 🧪 测试示例

### 1. 创建用户

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "status": 1
  }'
```

### 2. 查询用户

```bash
# 根据ID查询
curl -X GET http://localhost:8080/api/users/1

# 根据状态查询
curl -X GET http://localhost:8080/api/users/status/1

# 根据用户名查询
curl -X GET http://localhost:8080/api/users/username/testuser
```

### 3. 条件查询

```bash
curl -X POST http://localhost:8080/api/users/list \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "status": 1
  }'
```

### 4. 分页查询

```bash
curl -X POST http://localhost:8080/api/users/page \
  -H "Content-Type: application/json" \
  -d '{
    "current": 1,
    "size": 10,
    "username": "test",
    "orderByList": [
      {
        "field": "createTime",
        "direction": "DESC"
      }
    ]
  }'
```

### 5. 更新用户

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "email": "updated@example.com",
    "status": 0
  }'
```

### 6. 删除用户

```bash
curl -X DELETE http://localhost:8080/api/users/1
```

## 📊 数据库表结构

```sql
CREATE TABLE sys_user (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status INT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) COMMENT '用户表';
```

## 🎯 核心特性演示

### 1. 无 ServiceImpl 架构

传统方式需要编写 ServiceImpl：
```java
// ❌ 传统方式
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User getById(String id) {
        return userMapper.selectById(id);
    }
    // ... 更多样板代码
}
```

使用 Synapse 框架：
```java
// ✅ Synapse 方式
@AutoRepository
public interface UserRepository extends BaseRepository<User, UserMapper> {
    // 框架自动提供所有方法，无需编写 ServiceImpl
}
```

### 2. 优雅的查询条件构建

```java
// 方式1：静态工厂方法
UserQueryDTO query = UserQueryDTO.byStatus(1);
UserQueryDTO query = UserQueryDTO.byUsername("test");

// 方式2：Builder 模式
UserQueryDTO query = UserQueryDTO.builder()
    .username("test")
    .status(1)
    .build();

// 方式3：MyBatis-Plus 流式查询
List<User> users = userRepository.list(
    userRepository.query()
        .like(User::getUsername, "test")
        .eq(User::getStatus, 1)
        .orderByDesc(User::getCreateTime)
);
```

### 3. 统一分页查询

```java
// 分页查询 - 自动处理 count
PageResult<User> result = userRepository.pageWithCondition(queryDTO);

// 获取分页信息
long total = result.getTotal();
long current = result.getCurrent();
long size = result.getSize();
List<User> records = result.getRecords();
```

## 📝 注意事项

1. **实体类注解**: 确保正确使用 `@TableName`、`@TableId`、`@TableField` 注解
2. **Repository 注解**: 必须添加 `@AutoRepository` 注解
3. **DTO 继承**: 查询 DTO 需要继承 `PageDTO`
4. **字段映射**: 确保数据库字段名与实体类字段名正确映射
5. **索引优化**: 为常用查询字段添加数据库索引

## 🔧 配置说明

### 1. 数据库配置

```yaml
spring:
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/synapse_examples
          username: root
          password: password
          driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: ASSIGN_ID
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### 2. 日志配置

```yaml
logging:
  level:
    com.indigo.synapse: DEBUG
    com.indigo.examples: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

---

**示例特点**: 完整、清晰、可运行、有说明 