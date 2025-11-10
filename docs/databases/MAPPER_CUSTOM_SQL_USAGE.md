# Mapper 自定义 SQL 方法使用指南

## 📋 概述

在 Synapse Framework 中，自定义 SQL 方法统一在 **Mapper 接口**中定义，然后在业务层通过 Repository 的 `getMapper()` 方法调用。

**⚠️ 重要提示**：由于 JVM 限制，框架**不再支持**在 Repository 接口中定义 `default` 方法，以避免 JVM 断言失败。所有自定义 SQL 方法必须在 Mapper 中定义。

## 🎯 推荐使用方式

### 步骤1：在 Mapper 接口中定义自定义 SQL

```java
@Mapper
public interface UsersMapper extends BaseMapper<Users>, EnhancedVoMapper<Users, UsersVO> {
    
    /**
     * 根据ID查询用户（自定义SQL）
     */
    @Select("SELECT * FROM iam_user WHERE id = #{id}")
    Users getUserByIdCustom(String id);
    
    /**
     * 带多个参数的查询
     */
    @Select("SELECT * FROM iam_user WHERE account = #{account} AND enabled = #{enabled}")
    Users findByAccountAndEnabled(@Param("account") String account, @Param("enabled") Boolean enabled);
    
    /**
     * 动态 SQL 查询
     */
    @Select("<script>" +
            "SELECT * FROM iam_user WHERE 1=1" +
            "<if test='account != null and account != \"\"'> AND account = #{account}</if>" +
            "<if test='enabled != null'> AND enabled = #{enabled}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<Users> findUsersByCondition(@Param("account") String account, 
                                     @Param("enabled") Boolean enabled);
    
    /**
     * 返回列表的查询
     */
    @Select("SELECT * FROM iam_user WHERE status = #{status}")
    List<Users> findByStatus(@Param("status") String status);
}
```

### 步骤2：在 Repository 接口中保持简洁

```java
@AutoRepository
@IdeFriendlyRepository("iUsersService")
public interface IUsersService extends BaseRepository<Users, UsersMapper> {
    // Repository 接口保持简洁，不需要定义任何方法
    // 所有自定义SQL方法都在 Mapper 中定义
}
```

### 步骤3：在 Service 层通过 getMapper() 调用

```java
@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    
    private final IUsersService iUsersService;
    
    /**
     * 通过 getMapper() 调用 Mapper 中的自定义方法
     */
    public Users getUserById(String id) {
        // 使用 getMapper() 获取 Mapper 实例，然后调用自定义方法
        return iUsersService.getMapper().getUserByIdCustom(id);
    }
    
    /**
     * 调用多个参数的方法
     */
    public Users getUserByAccountAndEnabled(String account, Boolean enabled) {
        return iUsersService.getMapper().findByAccountAndEnabled(account, enabled);
    }
    
    /**
     * 调用返回列表的方法
     */
    public List<Users> searchUsers(String account, Boolean enabled) {
        return iUsersService.getMapper().findUsersByCondition(account, enabled);
    }
    
    /**
     * 调用其他自定义方法
     */
    public List<Users> getUsersByStatus(String status) {
        return iUsersService.getMapper().findByStatus(status);
    }
}
```

## 🔍 为什么不再支持 default 方法？

1. **JVM 限制**：在代理对象上调用 `default` 方法会导致 JVM 断言失败
2. **架构清晰**：统一在 Mapper 中定义 SQL，职责更清晰
3. **易于维护**：所有 SQL 集中管理，便于维护和优化

## 📝 完整示例

### Mapper 定义

```java
package com.indigo.iam.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.sdk.vo.UsersVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UsersMapper extends BaseMapper<Users>, EnhancedVoMapper<Users, UsersVO> {

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM iam_user WHERE id = #{id}")
    Users getUserByIdCustom(String id);
    
    /**
     * 根据账号和启用状态查询
     */
    @Select("SELECT * FROM iam_user WHERE account = #{account} AND enabled = #{enabled}")
    Users findByAccountAndEnabled(@Param("account") String account, @Param("enabled") Boolean enabled);
}
```

### Repository 定义

```java
package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.mapper.UsersMapper;

@AutoRepository
@IdeFriendlyRepository("iUsersService")
public interface IUsersService extends BaseRepository<Users, UsersMapper> {
    // 保持简洁，不需要定义任何方法
}
```

### Service 使用

```java
package com.indigo.iam.service;

import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.service.IUsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final IUsersService iUsersService;
    
    public Users getUserById(String id) {
        // 通过 getMapper() 调用 Mapper 中的自定义方法
        return iUsersService.getMapper().getUserByIdCustom(id);
    }
}
```

## 🚫 不推荐的方式

### ❌ 方式1：在 Repository 中使用 default 方法

```java
// ❌ 不支持：会导致 JVM 断言失败
public interface IUsersService extends BaseRepository<Users, UsersMapper> {
    default Users getUserByIdCustom(String id) {
        return getMapper().getUserByIdCustom(id);
    }
}
```

### ❌ 方式2：直接注入 Mapper（不推荐）

```java
// ❌ 不推荐：绕过了 Repository 层
@Service
class UserServiceImpl {
    private final UsersMapper usersMapper;  // 直接注入 Mapper
    
    public Users getUserById(String id) {
        return usersMapper.getUserByIdCustom(id);
    }
}
```

**为什么不推荐**：
- 绕过了 Repository 层，无法利用框架的统一处理
- 不利于统一管理和维护
- 无法利用框架的统一异常处理和日志记录

## 📚 更多示例

### 动态 SQL 查询

```java
@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    
    /**
     * 动态SQL查询示例
     */
    @Select("<script>" +
            "SELECT * FROM iam_user WHERE 1=1" +
            "<if test='account != null and account != \"\"'> AND account LIKE CONCAT('%', #{account}, '%')</if>" +
            "<if test='enabled != null'> AND enabled = #{enabled}</if>" +
            "<if test='locked != null'> AND locked = #{locked}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<Users> findUsersByCondition(@Param("account") String account,
                                     @Param("enabled") Boolean enabled,
                                     @Param("locked") Boolean locked);
}
```

### Service 层调用示例

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final IUsersService iUsersService;
    
    /**
     * 根据ID查询用户
     */
    public Users getUserById(String id) {
        log.info("查询用户，ID: {}", id);
        // 通过 getMapper() 调用 Mapper 中的自定义方法
        return iUsersService.getMapper().getUserByIdCustom(id);
    }
    
    /**
     * 根据账号和状态查询用户
     */
    public Users getUserByAccountAndStatus(String account, Boolean enabled) {
        log.info("查询用户，账号: {}, 状态: {}", account, enabled);
        return iUsersService.getMapper().findByAccountAndEnabled(account, enabled);
    }
    
    /**
     * 条件查询用户列表
     */
    public List<Users> searchUsers(String account, Boolean enabled, Boolean locked) {
        log.info("条件查询用户，账号: {}, 启用: {}, 锁定: {}", account, enabled, locked);
        return iUsersService.getMapper().findUsersByCondition(account, enabled, locked);
    }
}
```

## 🔍 注意事项

### 1. 方法名冲突

如果 Mapper 中的方法名与 `BaseRepository` 或 `IService` 的方法名冲突，需要在 Mapper 中使用不同的方法名：

```java
// ❌ 错误：方法名冲突
@Select("SELECT * FROM iam_user WHERE id = #{id}")
Users getOne(String id);  // 与 IService.getOne() 冲突

// ✅ 正确：使用不同的方法名
@Select("SELECT * FROM iam_user WHERE id = #{id}")
Users getUserByIdCustom(String id);
```

### 2. 参数绑定

使用 `@Param` 注解明确指定参数名，避免参数绑定错误：

```java
// ✅ 正确：使用 @Param 注解
@Select("SELECT * FROM iam_user WHERE account = #{account} AND enabled = #{enabled}")
Users findByAccountAndEnabled(@Param("account") String account, 
                              @Param("enabled") Boolean enabled);

// ❌ 错误：多个参数时可能绑定错误
@Select("SELECT * FROM iam_user WHERE account = #{account} AND enabled = #{enabled}")
Users findByAccountAndEnabled(String account, Boolean enabled);
```

### 3. 动态 SQL

使用 `<script>` 标签支持动态 SQL：

```java
@Select("<script>" +
        "SELECT * FROM iam_user WHERE 1=1" +
        "<if test='account != null'> AND account = #{account}</if>" +
        "<if test='enabled != null'> AND enabled = #{enabled}</if>" +
        "</script>")
List<Users> findUsers(@Param("account") String account, 
                     @Param("enabled") Boolean enabled);
```

### 4. SQL 注入防护

使用 `#{}` 占位符而不是 `${}` 字符串拼接，防止 SQL 注入：

```java
// ✅ 正确：使用 #{} 占位符
@Select("SELECT * FROM iam_user WHERE account = #{account}")

// ❌ 错误：使用 ${} 可能导致 SQL 注入
@Select("SELECT * FROM iam_user WHERE account = '${account}'")
```

## 🎯 最佳实践

1. **推荐在 Repository 中添加 default 方法**：统一数据访问入口
2. **使用有意义的方法名**：避免与框架方法冲突
3. **添加必要的注释**：说明方法用途和参数
4. **使用 @Param 注解**：明确参数绑定
5. **优先使用框架提供的方法**：只有在需要复杂 SQL 时才使用自定义方法

## 📚 相关文档

- [BaseRepository 使用指南](./README.md)
- [多表关联查询指南](./MULTI_TABLE_JOIN_GUIDE.md)
- [扩展点指南](../extension-points.md)

