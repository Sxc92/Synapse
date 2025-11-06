# Synapse Databases 多表联查使用指南

## 📋 概述

Synapse Databases 模块提供了两种多表联查方式：

1. **@VoMapping 注解方式**（推荐）✅ - 声明式配置，代码简洁，易于维护
2. **EnhancedQueryBuilder 链式API方式** - 编程式构建，灵活性高

## 🎯 架构设计

### 方案对比

| 特性 | @VoMapping 注解 | EnhancedQueryBuilder |
|------|----------------|---------------------|
| **配置方式** | 声明式（注解） | 编程式（链式API） |
| **代码简洁度** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| **可维护性** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **灵活性** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **性能** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **适用场景** | 固定多表关联 | 动态多表关联 |
| **推荐度** | ✅ 推荐 | ✅ 灵活场景 |

### 推荐使用场景

- **@VoMapping**：固定的多表关联查询（如：用户+部门、订单+商品+用户）
- **EnhancedQueryBuilder**：动态条件、复杂业务逻辑、需要运行时决定关联表

---

## 🚀 方式一：@VoMapping 注解方式（推荐）

### 1. 创建多表关联VO

在 `*-sdk` 模块中创建VO类，使用 `@VoMapping` 注解配置多表关联：

```java
package com.indigo.iam.vo;

import com.indigo.core.entity.vo.BaseVO;
import com.indigo.core.annotation.VoMapping;
import lombok.Data;

/**
 * 用户与部门关联查询VO
 * 
 * @author 史偕成
 */
@Data
@VoMapping(
    // 主表配置
    table = "iam_users",
    alias = "u",
    
    // 关联表配置
    joins = {
        @VoMapping.Join(
            table = "iam_department",
            alias = "d",
            type = VoMapping.JoinType.LEFT,
            on = "u.department_id = d.id"
        ),
        @VoMapping.Join(
            table = "iam_tenant",
            alias = "t",
            type = VoMapping.JoinType.LEFT,
            on = "u.tenant_id = t.id"
        )
    },
    
    // 字段映射配置
    fields = {
        // 用户表字段
        @VoMapping.Field(source = "u.id", target = "userId"),
        @VoMapping.Field(source = "u.account", target = "account"),
        @VoMapping.Field(source = "u.type", target = "userType"),
        @VoMapping.Field(source = "u.enabled", target = "enabled"),
        @VoMapping.Field(source = "u.create_time", target = "createTime"),
        
        // 部门表字段
        @VoMapping.Field(source = "d.id", target = "departmentId"),
        @VoMapping.Field(source = "d.name", target = "departmentName"),
        @VoMapping.Field(source = "d.code", target = "departmentCode"),
        
        // 租户表字段
        @VoMapping.Field(source = "t.id", target = "tenantId"),
        @VoMapping.Field(source = "t.name", target = "tenantName"),
        @VoMapping.Field(source = "t.code", target = "tenantCode"),
        
        // 计算字段示例
        @VoMapping.Field(
            source = "CONCAT(u.account, '@', t.code)",
            target = "fullAccount",
            type = VoMapping.FieldType.EXPRESSION
        )
    }
)
public class UserWithDeptAndTenantVO extends BaseVO<String> {
    
    // 用户字段
    private String userId;
    private String account;
    private String userType;
    private Boolean enabled;
    
    // 部门字段
    private String departmentId;
    private String departmentName;
    private String departmentCode;
    
    // 租户字段
    private String tenantId;
    private String tenantName;
    private String tenantCode;
    
    // 计算字段
    private String fullAccount;
}
```

### 2. 创建查询DTO

```java
package com.indigo.iam.dto;

import com.indigo.core.entity.dto.PageDTO;
import com.indigo.core.entity.dto.QueryCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户多表查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserMultiTablePageDTO extends PageDTO<Users> {
    
    @QueryCondition(condition = "=")
    private String account;
    
    @QueryCondition(condition = "=", field = "u.enabled")
    private Boolean enabled;
    
    @QueryCondition(condition = "=", field = "d.id")
    private String departmentId;
    
    @QueryCondition(condition = "=", field = "t.id")
    private String tenantId;
    
    @QueryCondition(condition = "LIKE", field = "d.name")
    private String departmentName;
}
```

### 3. Repository 接口定义

```java
package com.indigo.iam.repository;

import com.indigo.core.entity.dto.PageDTO;
import com.indigo.core.entity.dto.PageResult;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.dto.UserMultiTablePageDTO;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.vo.UserWithDeptAndTenantVO;

/**
 * 用户Repository
 */
@Repository
public interface UserRepository extends BaseRepository<Users> {
    
    /**
     * 多表关联分页查询 - 使用@VoMapping注解
     * 框架会自动检测VO类上的@VoMapping注解，自动构建多表关联SQL
     */
    default PageResult<UserWithDeptAndTenantVO> pageUsersWithDeptAndTenant(
            UserMultiTablePageDTO pageDTO) {
        // 方式1：使用 pageWithVoMapping 方法（推荐）
        return pageWithVoMapping(pageDTO, UserWithDeptAndTenantVO.class);
        
        // 方式2：使用 quickPage 方法（自动检测多表）
        // return quickPage(pageDTO, UserWithDeptAndTenantVO.class);
        
        // 方式3：使用 pageWithDTO 方法（自动检测多表）
        // return pageWithDTO(pageDTO, UserWithDeptAndTenantVO.class);
    }
    
    /**
     * 多表关联列表查询
     */
    default List<UserWithDeptAndTenantVO> listUsersWithDeptAndTenant(
            UserMultiTableQueryDTO queryDTO) {
        return listWithDTO(queryDTO, UserWithDeptAndTenantVO.class);
    }
}
```

### 4. Service 层使用

```java
package com.indigo.iam.service;

import com.indigo.core.entity.dto.PageResult;
import com.indigo.iam.dto.UserMultiTablePageDTO;
import com.indigo.iam.repository.UserRepository;
import com.indigo.iam.vo.UserWithDeptAndTenantVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Slf4j
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 分页查询用户（包含部门和租户信息）
     */
    public PageResult<UserWithDeptAndTenantVO> getUsersWithDeptAndTenant(
            UserMultiTablePageDTO pageDTO) {
        log.info("查询用户列表，条件：{}", pageDTO);
        return userRepository.pageUsersWithDeptAndTenant(pageDTO);
    }
}
```

### 5. Controller 层使用

```java
package com.indigo.iam.controller;

import com.indigo.core.entity.dto.PageResult;
import com.indigo.core.entity.dto.Result;
import com.indigo.iam.dto.UserMultiTablePageDTO;
import com.indigo.iam.service.UserService;
import com.indigo.iam.vo.UserWithDeptAndTenantVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询用户（多表关联）
     */
    @GetMapping("/page")
    public Result<PageResult<UserWithDeptAndTenantVO>> getUsersPage(
            UserMultiTablePageDTO pageDTO) {
        PageResult<UserWithDeptAndTenantVO> result = 
            userService.getUsersWithDeptAndTenant(pageDTO);
        return Result.success(result);
    }
}
```

---

## 🔧 方式二：EnhancedQueryBuilder 链式API方式

### 1. Repository 接口定义

```java
@Repository
public interface UserRepository extends BaseRepository<Users> {
    
    /**
     * 使用 EnhancedQueryBuilder 进行多表关联查询
     */
    default PageResult<UserWithDeptAndTenantVO> pageUsersWithDeptDynamic(
            UserMultiTablePageDTO pageDTO) {
        return enhancedQuery(Users.class)
            .alias("u")
            .leftJoin("iam_department", "d", "u.department_id = d.id")
            .leftJoin("iam_tenant", "t", "u.tenant_id = t.id")
            .select(
                "u.id as userId",
                "u.account",
                "u.type as userType",
                "u.enabled",
                "d.id as departmentId",
                "d.name as departmentName",
                "t.id as tenantId",
                "t.name as tenantName"
            )
            .eq(StringUtils.hasText(pageDTO.getAccount()), "u.account", pageDTO.getAccount())
            .eq(pageDTO.getEnabled() != null, "u.enabled", pageDTO.getEnabled())
            .eq(StringUtils.hasText(pageDTO.getDepartmentId()), "d.id", pageDTO.getDepartmentId())
            .orderByDesc("u.create_time")
            .page(pageDTO, UserWithDeptAndTenantVO.class);
    }
    
    /**
     * 复杂多表关联查询
     */
    default List<UserWithDeptAndTenantVO> findUsersWithComplexCondition(
            String keyword, String departmentId) {
        return enhancedQuery(Users.class)
            .alias("u")
            .innerJoin("iam_department", "d", "u.department_id = d.id")
            .leftJoin("iam_tenant", "t", "u.tenant_id = t.id")
            .leftJoin("iam_role", "r", "u.id = r.user_id")
            .select(
                "u.*",
                "d.name as departmentName",
                "t.name as tenantName",
                "GROUP_CONCAT(r.name) as roleNames"
            )
            .and(wrapper -> wrapper
                .like(StringUtils.hasText(keyword), "u.account", keyword)
                .or()
                .like(StringUtils.hasText(keyword), "d.name", keyword)
            )
            .eq(StringUtils.hasText(departmentId), "d.id", departmentId)
            .eq("u.enabled", true)
            .groupBy("u.id")
            .orderByDesc("u.create_time")
            .list(UserWithDeptAndTenantVO.class);
    }
}
```

---

## 📝 @VoMapping 注解详细说明

### Join 配置

```java
@VoMapping.Join(
    table = "关联表名",           // 必填：关联的表名
    alias = "表别名",             // 必填：表的别名
    type = VoMapping.JoinType.LEFT,  // 可选：关联类型（默认LEFT）
    on = "关联条件"              // 必填：ON条件，如 "u.department_id = d.id"
)
```

### JoinType 枚举

- `INNER` - INNER JOIN（内连接）
- `LEFT` - LEFT JOIN（左连接，默认）
- `RIGHT` - RIGHT JOIN（右连接）
- `FULL` - FULL JOIN（全连接）

### Field 配置

```java
@VoMapping.Field(
    source = "数据库字段或表达式",  // 必填：如 "u.id" 或 "CONCAT(u.account, '@', t.code)"
    target = "VO字段名",          // 可选：VO中的字段名（默认与source相同）
    type = VoMapping.FieldType.DIRECT  // 可选：字段类型（默认DIRECT）
)
```

### FieldType 枚举

- `DIRECT` - 直接映射（默认）
- `EXPRESSION` - SQL表达式（如聚合函数、计算字段）
- `ALIAS` - 别名映射（自动添加 AS 子句）

---

## 🎨 完整示例

### 示例1：用户+部门+租户三表关联

```java
// VO定义
@Data
@VoMapping(
    table = "iam_users",
    alias = "u",
    joins = {
        @VoMapping.Join(table = "iam_department", alias = "d", 
                       type = VoMapping.JoinType.LEFT,
                       on = "u.department_id = d.id"),
        @VoMapping.Join(table = "iam_tenant", alias = "t",
                       type = VoMapping.JoinType.LEFT,
                       on = "u.tenant_id = t.id")
    },
    fields = {
        @VoMapping.Field(source = "u.id", target = "userId"),
        @VoMapping.Field(source = "u.account", target = "account"),
        @VoMapping.Field(source = "d.name", target = "departmentName"),
        @VoMapping.Field(source = "t.name", target = "tenantName")
    }
)
public class UserDetailVO extends BaseVO<String> {
    private String userId;
    private String account;
    private String departmentName;
    private String tenantName;
}

// Repository使用
@Repository
public interface UserRepository extends BaseRepository<Users> {
    default PageResult<UserDetailVO> pageUserDetails(PageDTO<?> pageDTO) {
        return pageWithVoMapping(pageDTO, UserDetailVO.class);
    }
}
```

### 示例2：订单+商品+用户多表关联

```java
@Data
@VoMapping(
    table = "orders",
    alias = "o",
    joins = {
        @VoMapping.Join(table = "products", alias = "p",
                       type = VoMapping.JoinType.INNER,
                       on = "o.product_id = p.id"),
        @VoMapping.Join(table = "users", alias = "u",
                       type = VoMapping.JoinType.LEFT,
                       on = "o.user_id = u.id")
    },
    fields = {
        @VoMapping.Field(source = "o.id", target = "orderId"),
        @VoMapping.Field(source = "o.order_no", target = "orderNo"),
        @VoMapping.Field(source = "p.name", target = "productName"),
        @VoMapping.Field(source = "p.price", target = "productPrice"),
        @VoMapping.Field(source = "u.account", target = "userAccount"),
        @VoMapping.Field(source = "o.quantity * p.price", 
                        target = "totalAmount",
                        type = VoMapping.FieldType.EXPRESSION)
    }
)
public class OrderDetailVO extends BaseVO<Long> {
    private Long orderId;
    private String orderNo;
    private String productName;
    private BigDecimal productPrice;
    private String userAccount;
    private BigDecimal totalAmount;
}
```

---

## ⚠️ 注意事项

### 1. VO 必须放在 `*-sdk` 模块中

```java
// ✅ 正确位置
foundation-module/iam-service/iam-sdk/src/main/java/com/indigo/iam/vo/UserWithDeptVO.java

// ❌ 错误位置
foundation-module/iam-service/iam-server/src/main/java/com/indigo/iam/vo/UserWithDeptVO.java
```

### 2. 字段映射必须使用表别名

```java
// ✅ 正确：使用表别名
@VoMapping.Field(source = "u.id", target = "userId")
@VoMapping.Field(source = "d.name", target = "departmentName")

// ❌ 错误：不使用表别名（多表查询会报错）
@VoMapping.Field(source = "id", target = "userId")
```

### 3. JOIN 条件必须完整

```java
// ✅ 正确：完整的关联条件
on = "u.department_id = d.id"

// ❌ 错误：缺少表别名
on = "department_id = id"
```

### 4. 查询条件字段需要指定表别名

```java
// DTO中的查询条件
@QueryCondition(condition = "=", field = "u.enabled")  // ✅ 指定表别名
private Boolean enabled;

@QueryCondition(condition = "=")  // ❌ 不指定表别名（多表查询可能出错）
private Boolean enabled;
```

---

## 🔍 调试技巧

### 1. 启用SQL日志

```yaml
# application.yml
synapse:
  datasource:
    mybatis-plus:
      configuration:
        log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 2. 查看生成的SQL

框架会自动生成类似如下的SQL：

```sql
SELECT 
    u.id AS userId,
    u.account,
    d.name AS departmentName,
    t.name AS tenantName
FROM iam_users u
LEFT JOIN iam_department d ON u.department_id = d.id
LEFT JOIN iam_tenant t ON u.tenant_id = t.id
WHERE u.enabled = 1
ORDER BY u.create_time DESC
LIMIT 0, 10
```

---

## 📚 相关文档

- [BaseRepository API文档](../../synapse-framework/synapse-databases/README.md)
- [EnhancedQueryBuilder 使用指南](../../docs/advanced-query.md)
- [VO映射最佳实践](../../synapse-framework/synapse-core/docs/BASEVO_USAGE_GUIDE.md)

---

## 🔗 一对多关联查询

### 场景说明

当主表与关联表是一对多关系时（如：用户-角色），查询结果会返回多条记录（每个用户-角色组合一条记录）。需要在应用层进行分组组装。

### 实现步骤

#### 1. 创建中间VO（用于接收JOIN查询结果）

```java
// 位置：iam-sdk/src/main/java/com/indigo/iam/sdk/vo/users/UserWithRoleVO.java
@Data
@VoMapping(
    table = "iam_users",
    alias = "u",
    joins = {
        @VoMapping.Join(
            table = "iam_users_role",
            alias = "ur",
            type = VoMapping.JoinType.LEFT,
            on = "u.id = ur.user_id"
        ),
        @VoMapping.Join(
            table = "iam_roles",
            alias = "r",
            type = VoMapping.JoinType.LEFT,
            on = "ur.role_id = r.id"
        )
    },
    fields = {
        // 用户字段
        @VoMapping.Field(source = "u.id", target = "userId"),
        @VoMapping.Field(source = "u.account", target = "account"),
        // 角色字段（一对多，会返回多条记录）
        @VoMapping.Field(source = "r.id", target = "roleId"),
        @VoMapping.Field(source = "r.code", target = "roleCode"),
        @VoMapping.Field(source = "r.description", target = "roleDesc")
    }
)
public class UserWithRoleVO extends BaseVO<String> {
    private String userId;
    private String account;
    private String roleId;      // 注意：一对多关系，会返回多条记录
    private String roleCode;
    private String roleDesc;
}
```

#### 2. Repository 层查询方法

```java
@Repository
public interface IUsersService extends BaseRepository<Users, UsersMapper> {
    
    /**
     * 查询用户及其角色（一对多关联查询）- 列表查询
     * 使用 listWithVoMapping 方法，自动根据 @VoMapping 注解进行多表关联查询
     * 注意：返回扁平化结果，需要在Service层组装
     */
    default List<UserWithRoleVO> listUsersWithRoles(QueryDTO<?> queryDTO) {
        return listWithVoMapping(queryDTO, UserWithRoleVO.class);
    }
    
    /**
     * 查询用户及其角色（一对多关联查询）- 分页查询
     * 使用 pageWithVoMapping 方法，自动根据 @VoMapping 注解进行多表关联查询
     */
    default PageResult<UserWithRoleVO> pageUsersWithRoles(PageDTO<?> pageDTO) {
        return pageWithVoMapping(pageDTO, UserWithRoleVO.class);
    }
    
    /**
     * 查询用户及其角色（一对多关联查询）- 单个查询
     * 使用 getOneWithVoMapping 方法，自动根据 @VoMapping 注解进行多表关联查询
     */
    default UserWithRoleVO getUserWithRole(QueryDTO<?> queryDTO) {
        return getOneWithVoMapping(queryDTO, UserWithRoleVO.class);
    }
}
```

#### 3. Service 层组装逻辑

```java
@Service
public class UserService {
    
    @Autowired
    private IUsersService usersService;
    
    /**
     * 查询用户详情（包含角色列表）
     */
    public List<UserDetailVO> listUserDetails(QueryDTO<?> queryDTO) {
        // 1. 查询扁平化结果（一对多，返回多条记录）
        List<UserWithRoleVO> userWithRoleList = usersService.listUsersWithRoles(queryDTO);
        
        // 2. 组装数据：将扁平化结果转换为 UserDetailVO（包含 List<RoleDetailVO>）
        return assembleUserDetails(userWithRoleList);
    }
    
    /**
     * 组装用户详情数据
     * 将扁平化的 UserWithRoleVO 列表转换为 UserDetailVO 列表（包含角色列表）
     */
    private List<UserDetailVO> assembleUserDetails(List<UserWithRoleVO> userWithRoleList) {
        if (userWithRoleList == null || userWithRoleList.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 按用户ID分组
        Map<String, List<UserWithRoleVO>> userGroupMap = userWithRoleList.stream()
            .collect(Collectors.groupingBy(
                UserWithRoleVO::getUserId,
                LinkedHashMap::new,  // 保持顺序
                Collectors.toList()
            ));
        
        // 组装数据
        List<UserDetailVO> userDetailList = new ArrayList<>();
        for (Map.Entry<String, List<UserWithRoleVO>> entry : userGroupMap.entrySet()) {
            List<UserWithRoleVO> userRoleList = entry.getValue();
            if (userRoleList.isEmpty()) {
                continue;
            }
            
            // 获取第一条记录作为用户基础信息
            UserWithRoleVO firstRecord = userRoleList.get(0);
            
            // 构建用户详情VO
            UserDetailVO userDetail = UserDetailVO.builder()
                .userId(firstRecord.getUserId())
                .account(firstRecord.getAccount())
                .password(null)  // 不返回密码
                .build();
            
            // 设置基础字段
            userDetail.setId(firstRecord.getId());
            userDetail.setCreateTime(firstRecord.getCreateTime());
            userDetail.setModifyTime(firstRecord.getModifyTime());
            
            // 组装角色列表
            List<RoleDetailVO> roleList = userRoleList.stream()
                .filter(record -> StringUtils.hasText(record.getRoleId()))  // 过滤掉没有角色的记录
                .map(record -> {
                    RoleDetailVO roleDetail = RoleDetailVO.builder()
                        .roleCode(record.getRoleCode())
                        .roleDesc(record.getRoleDesc())
                        .build();
                    roleDetail.setId(record.getRoleId());
                    return roleDetail;
                })
                .distinct()  // 去重
                .collect(Collectors.toList());
            
            userDetail.setRoles(roleList);
            userDetailList.add(userDetail);
        }
        
        return userDetailList;
    }
}
```

#### 4. Controller 层使用

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询用户详情（包含角色列表）
     */
    @GetMapping("/page")
    public Result<PageResult<UserDetailVO>> getUserDetailsPage(
            UsersPageDTO pageDTO) {
        PageResult<UserDetailVO> result = userService.pageUserDetails(pageDTO);
        return Result.success(result);
    }
    
    /**
     * 根据用户ID查询用户详情（包含角色列表）
     */
    @GetMapping("/{userId}")
    public Result<UserDetailVO> getUserDetail(@PathVariable String userId) {
        UserDetailVO userDetail = userService.getUserDetailById(userId);
        return Result.success(userDetail);
    }
}
```

### 一对多查询要点

1. **JOIN 查询返回扁平化结果**：每个用户-角色组合一条记录
2. **需要在应用层分组组装**：按用户ID分组，组装成包含角色列表的VO
3. **使用 LEFT JOIN**：确保没有角色的用户也能查询出来
4. **去重处理**：使用 `distinct()` 避免重复角色

### 查询结果示例

**JOIN 查询返回的扁平化结果：**
```
userId | account | roleId | roleCode | roleDesc
-------|---------|--------|----------|----------
user1  | admin   | role1  | admin    | 管理员
user1  | admin   | role2  | user     | 普通用户
user2  | test    | role2  | user     | 普通用户
```

**组装后的结果：**
```json
[
  {
    "userId": "user1",
    "account": "admin",
    "roles": [
      {"roleId": "role1", "roleCode": "admin", "roleDesc": "管理员"},
      {"roleId": "role2", "roleCode": "user", "roleDesc": "普通用户"}
    ]
  },
  {
    "userId": "user2",
    "account": "test",
    "roles": [
      {"roleId": "role2", "roleCode": "user", "roleDesc": "普通用户"}
    ]
  }
]
```

---

## 📋 BaseRepository 多表查询方法总结

### 推荐使用的多表查询方法

| 方法名 | 用途 | 返回类型 | 说明 |
|--------|------|---------|------|
| `pageWithVoMapping` | 分页查询 | `PageResult<V>` | 自动根据 @VoMapping 注解进行多表关联查询 |
| `listWithVoMapping` | 列表查询 | `List<V>` | 自动根据 @VoMapping 注解进行多表关联查询 |
| `getOneWithVoMapping` | 单个查询 | `V` | 自动根据 @VoMapping 注解进行多表关联查询 |

### 使用示例

```java
@Repository
public interface UserRepository extends BaseRepository<Users, UsersMapper> {
    
    // ✅ 分页查询（推荐）
    default PageResult<UserWithRoleVO> pageUsers(PageDTO<?> pageDTO) {
        return pageWithVoMapping(pageDTO, UserWithRoleVO.class);
    }
    
    // ✅ 列表查询（推荐）
    default List<UserWithRoleVO> listUsers(QueryDTO<?> queryDTO) {
        return listWithVoMapping(queryDTO, UserWithRoleVO.class);
    }
    
    // ✅ 单个查询（推荐）
    default UserWithRoleVO getUser(QueryDTO<?> queryDTO) {
        return getOneWithVoMapping(queryDTO, UserWithRoleVO.class);
    }
}
```

### 方法对比

| 方法 | 是否支持多表 | 是否自动检测@VoMapping | 推荐度 |
|------|------------|---------------------|--------|
| `pageWithVoMapping` | ✅ | ✅ | ⭐⭐⭐⭐⭐ |
| `listWithVoMapping` | ✅ | ✅ | ⭐⭐⭐⭐⭐ |
| `getOneWithVoMapping` | ✅ | ✅ | ⭐⭐⭐⭐⭐ |
| `pageWithDTO` | ✅ | ✅ | ⭐⭐⭐⭐ |
| `listWithDTO` | ✅ | ✅ | ⭐⭐⭐⭐ |
| `getOneWithDTO` | ✅ | ✅ | ⭐⭐⭐⭐ |

**注意**：`*WithVoMapping` 方法名更明确，推荐使用。

---

## ✅ 总结

1. **推荐使用 @VoMapping 注解方式**，代码简洁、易于维护
2. **推荐使用 `*WithVoMapping` 方法**（`pageWithVoMapping`、`listWithVoMapping`、`getOneWithVoMapping`）
3. **VO 必须放在 `*-sdk` 模块中**
4. **字段映射必须使用表别名**（如 `u.account`）
5. **查询条件字段建议指定表别名**（如 `@QueryCondition(field = "u.account")`）
6. **一对多查询需要在应用层组装数据**
7. **启用SQL日志便于调试**

如有问题，请查看框架日志或联系开发团队。

