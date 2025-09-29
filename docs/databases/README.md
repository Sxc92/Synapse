# Synapse Databases 数据库框架

## 概述

Synapse Databases 是 SynapseMOM 平台的数据库操作框架，提供无 ServiceImpl 的数据库操作，支持动态数据源、统一分页查询、优雅的查询条件构建等功能。

## 核心特性

### 🎯 无 ServiceImpl 架构
- 基于 `@AutoRepository` 注解自动生成代理
- 继承 MyBatis-Plus `IService` 接口
- 消除传统 ServiceImpl 样板代码

### 🚀 优雅的查询条件构建
- **Builder 模式**: 使用 Lombok `@Builder` 注解
- **查询条件对象**: 支持链式调用
- **静态工厂方法**: 简化常用查询
- **@QueryCondition 注解**: 自动构建查询条件

### 📊 统一分页查询
- 基于 `PageDTO` 和 `PageResult`
- 数据库层面排序，避免内存分页
- 支持复杂查询条件

### 🔄 动态数据源
- 支持多数据源切换
- 负载均衡策略
- 健康检查和故障转移

### 🛡️ 类型安全
- 移除 Map 类型参数
- 编译时类型检查
- 避免运行时错误

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-databases</artifactId>
</dependency>
```

### 2. 定义 Repository 接口

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 自动继承 IService 的所有方法
    // 无需编写 ServiceImpl
}
```

### 3. 定义 Mapper 接口

```java
@Mapper
public interface TenantMapper extends BaseMapper<IamTenant> {
    // 复杂查询使用 @Select 注解
    @Select("SELECT t.*, u.username as creator_name FROM iam_tenant t " +
            "LEFT JOIN iam_user u ON t.creator_id = u.id " +
            "WHERE t.status = #{status}")
    List<TenantWithCreatorDTO> findTenantsWithCreator(@Param("status") String status);
}
```

### 4. 定义 DTO

```java
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class TenantQueryDTO extends PageDTO {
    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String code;
    
    @QueryCondition(type = QueryCondition.QueryType.EQ)
    private Integer status;
    
    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "create_time")
    private LocalDateTime[] createTime;
    
    // 静态工厂方法
    public static TenantQueryDTO byStatus(Integer status) {
        return TenantQueryDTO.builder().status(status).build();
    }
    
    public static TenantQueryDTO byKeyword(String keyword) {
        return TenantQueryDTO.builder().code(keyword).build();
    }
}
```

### 5. 使用示例

```java
@Service
public class TenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    // 基础 CRUD 操作
    public IamTenant getTenantById(String id) {
        return tenantsRepository.getById(id);
    }
    
    public boolean saveTenant(IamTenant tenant) {
        return tenantsRepository.save(tenant);
        }
    
    // 查询条件构建
    public List<IamTenant> findTenants(TenantQueryDTO query) {
        return tenantsRepository.listWithCondition(query);
    }
    
    public PageResult<IamTenant> findTenantsWithPage(TenantQueryDTO query) {
        return tenantsRepository.pageWithCondition(query);
    }
    
    // 使用静态工厂方法
    public List<IamTenant> getTenantsByStatus(Integer status) {
        TenantQueryDTO query = TenantQueryDTO.byStatus(status);
        return tenantsRepository.listWithCondition(query);
    }
    
    public List<IamTenant> searchTenantsByKeyword(String keyword) {
        TenantQueryDTO query = TenantQueryDTO.byKeyword(keyword);
        return tenantsRepository.listWithCondition(query);
    }
    
    // 复杂查询
    public List<TenantWithCreatorDTO> getTenantsWithCreator(String status) {
        return tenantsRepository.getMapper().findTenantsWithCreator(status);
    }
}
```

## 核心注解

### @AutoRepository

自动生成 Repository 代理，无需编写实现类。

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 框架自动提供所有 MyBatis-Plus 方法
}
```

### @QueryCondition

自动构建查询条件，支持多种查询类型。

```java
public class TenantQueryDTO extends PageDTO {
    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String code;
    
    @QueryCondition(type = QueryCondition.QueryType.EQ)
    private Integer status;
    
    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "create_time")
    private LocalDateTime[] createTime;
    }
```

**支持的查询类型**:
- `EQ`: 等于
- `NE`: 不等于
- `LIKE`: 模糊查询
- `LIKE_LEFT`: 左模糊查询
- `LIKE_RIGHT`: 右模糊查询
- `GT`: 大于
- `GE`: 大于等于
- `LT`: 小于
- `LE`: 小于等于
- `IN`: 包含
- `NOT_IN`: 不包含
- `BETWEEN`: 范围查询
- `IS_NULL`: 为空
- `IS_NOT_NULL`: 不为空

### @DataSource

数据源切换注解。

```java
@DataSource("slave")
public List<IamTenant> getTenantsFromSlave() {
    return tenantsRepository.list();
}
```

## 查询条件构建

### 1. Builder 模式

```java
// 使用 Lombok @Builder
TenantQueryDTO query = TenantQueryDTO.builder()
    .status(1)
    .code("T001")
    .description("测试")
    .build();
```

### 2. 静态工厂方法

```java
// 简单查询
TenantQueryDTO query = TenantQueryDTO.byStatus(1);
TenantQueryDTO query = TenantQueryDTO.byKeyword("test");
TenantQueryDTO query = TenantQueryDTO.byCode("T001");

// 时间范围查询
TenantQueryDTO query = TenantQueryDTO.byCreateTimeRange(start, end);
```

### 3. 链式调用

```java
TenantQueryCondition condition = TenantQueryCondition.builder()
    .status(1)
    .description("测试")
    .build();

condition.setCurrent(1);
condition.setSize(10);
condition.addOrderBy("create_time", "DESC");
```

## 分页查询

### 统一分页参数

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantQueryDTO extends PageDTO {
    // 继承 PageDTO 获得分页能力
    // current: 当前页
    // size: 每页大小
    // orderByList: 排序条件
}
```

### 分页查询方法

```java
// 分页查询
PageResult<IamTenant> result = tenantsRepository.pageWithCondition(queryDTO);
        
// 获取分页信息
long total = result.getTotal();
long current = result.getCurrent();
long size = result.getSize();
List<IamTenant> records = result.getRecords();
```

## 复杂查询

### 使用 @Select 注解

```java
@Mapper
public interface TenantMapper extends BaseMapper<IamTenant> {
    
    // 简单查询
    @Select("SELECT * FROM iam_tenant WHERE status = #{status}")
    List<IamTenant> findByStatus(@Param("status") Integer status);
    
    // 多表关联查询
    @Select("""
        SELECT t.*, u.username as creator_name, u.email as creator_email
        FROM iam_tenant t
        LEFT JOIN iam_user u ON t.creator_id = u.id
        WHERE t.status = #{status}
        ORDER BY t.create_time DESC
    """)
    List<TenantWithCreatorDTO> findTenantsWithCreator(@Param("status") String status);
    
    // 分页查询
    @Select("""
        SELECT t.*, u.username as creator_name
        FROM iam_tenant t
        LEFT JOIN iam_user u ON t.creator_id = u.id
        WHERE t.status = #{status}
        ORDER BY t.create_time DESC
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<TenantWithCreatorDTO> findTenantsWithCreatorPage(
        @Param("status") String status,
        @Param("pageSize") int pageSize,
        @Param("offset") int offset
    );
}
```

### 在 Repository 中使用

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 调用 Mapper 的复杂查询方法
    default List<TenantWithCreatorDTO> findTenantsWithCreator(String status) {
        return this.getMapper().findTenantsWithCreator(status);
    }
    
    default PageResult<TenantWithCreatorDTO> findTenantsWithCreatorPage(String status, int page, int size) {
        int offset = (page - 1) * size;
        List<TenantWithCreatorDTO> records = this.getMapper().findTenantsWithCreatorPage(status, size, offset);
        
        // 构建分页结果
        PageResult<TenantWithCreatorDTO> result = new PageResult<>();
        result.setCurrent(page);
        result.setSize(size);
        result.setRecords(records);
        
        // 获取总数
        long total = this.getMapper().countTenantsWithCreator(status);
        result.setTotal(total);
        
        return result;
    }
}
```

## 配置说明

### 基础配置

```yaml
# 启用数据库框架
synapse:
  databases:
    enabled: true
```

### 动态数据源配置

```yaml
spring:
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/synapse_mom
        username: root
        password: password
        driver-class-name: com.mysql.cj.jdbc.Driver
        slave:
          url: jdbc:mysql://localhost:3306/synapse_mom_slave
        username: root
        password: password
        driver-class-name: com.mysql.cj.jdbc.Driver
```

### MyBatis-Plus 配置

```yaml
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

## 最佳实践

### 1. 模块化设计

- **DTO 统一放在 `api` 模块的 `model/dto` 目录**
- 业务逻辑放在 `core` 模块
- 客户端代码放在 `client` 模块

### 2. 命名规范

- 实体类: `IamTenant`
- DTO: `TenantQueryDTO`, `TenantQueryCondition`
- Repository: `TenantsRepository`
- Mapper: `TenantMapper`
- Service: `TenantService`
- Controller: `TenantController`

### 3. 查询条件构建

- 简单查询使用静态工厂方法：`TenantQueryDTO.byStatus(1)`
- 复杂查询使用 Builder 模式：`TenantQueryDTO.builder().status(1).code("T001").build()`
- 动态查询使用 `@QueryCondition` 注解

### 4. SQL 注解使用

- 使用文本块（"""）编写多行 SQL，提高可读性
- 合理使用参数绑定，避免 SQL 注入
- 复杂查询添加适当的注释
- 使用 `@Param` 注解明确参数名称

### 5. 性能优化

- 合理使用分页查询，避免全表扫描
- 使用索引优化查询性能
- 避免 N+1 查询问题
- 数据库层面排序，避免内存分页

## 常见问题

### Q: 如何添加自定义查询方法？

A: 在 Mapper 接口中使用 `@Select` 注解，然后在 Repository 中添加 `default` 方法调用。

### Q: 如何实现动态数据源切换？

A: 使用 `@DataSource` 注解或在代码中调用 `DataSourceContextHolder.setDataSource("slave")`。

### Q: 如何处理复杂的分页查询？

A: 在 Mapper 中使用 `LIMIT` 和 `OFFSET`，然后在 Repository 中构建 `PageResult`。

### Q: 如何优化查询性能？

A: 使用索引、合理分页、避免 N+1 查询、使用缓存等。

## 故障排除指南

### 🔍 checkKeyUniqueness 方法问题

**问题描述**：调用 `checkKeyUniqueness` 时出现 `NoSuchFieldException` 或参数类型错误。

**常见错误**：
```
java.lang.NoSuchFieldException: [Ljava.lang.String;@xxxxx
Failed to access field [Ljava.lang.String;@xxxxx for uniqueness check
```

**解决方案**：
1. **确保使用正确的调用方式**：
   ```java
   // ✅ 正确
   countryService.checkKeyUniqueness(param, "fieldName");
   
   // ❌ 错误 - 传递数组
   countryService.checkKeyUniqueness(param, new String[]{"fieldName"});
   ```

2. **检查 BaseDTO 参数**：确保第一个参数正确继承了 `BaseDTO`
   ```java
   @Data
   public class CountryDTO extends BaseDTO<String> {
       private String code;
       private String name;
   }
   ```

3. **调试模式启用**：检查日志中的详细调试信息
   ```
   DEBUG [SqlMethodInterceptor] - checkKeyUniqueness - args.length: 2
   DEBUG [SqlMethodInterceptor] - args[1]: type=[Ljava.lang.String;, value=[code]
   ```

### 🔍 Mapper 未找到问题

**问题描述**：`NoSuchBeanDefinitionException: No qualifying bean of type 'xxxMapper'`

**解决方案**：
1. **检查 Mapper 注解**：
   ```java
   @Mapper  // ✅ 确保有 @Mapper 注解
   public interface CountryMapper extends BaseMapper<Country> {
   }
   ```

2. **确认扫描路径**：框架已配置全局扫描 `com.indigo.**.repository.mapper`

3. **检查包路径结构**：
   ```
   src/main/java/
   └── com/indigo/
       └── mdm/
           └── repository/
               └── mapper/     # ✅ Mapper 放在这里
                   └── CountryMapper.java
   ```

### 🔍 代理调用问题

**问题描述**：动态代理方法调用失败或死循环

**解决方案**：
1. **检查方法默认性**：确保调用的是 `default` 方法
2. **验证 Spring AOP 配置**：确保 Spring 代理正常工作
3. **调试日志**：查看 `SqlMethodInterceptor` 的详细日志

### 🔍 参数类型问题

**问题描述**：参数类型在传递过程中被污染或转换错误

**调试步骤**：
1. 启用调试日志：
   ```yaml
   logging:
     level:
       com.indigo.databases.proxy.SqlMethodInterceptor: DEBUG
   ```

2. 查看关键调试信息：
   ```
   Processing fieldNameParam: class=java.lang.String, toString=code
   Parsed fieldNames: [code]
   Final actualFieldName: code
   ```

### 📊 最佳调试实践

1. **启用详细日志**：
   ```yaml
   logging:
     level:
       com.indigo.databases: DEBUG
       org.springframework.aop: DEBUG
   ```

2. **关键日志标识**：
   - `Handling BaseRepository method:` - 方法拦截
   - `Processing fieldNameParam:` - 参数处理
   - `Parsed fieldNames:` - 字段解析
   - `Final actualFieldName:` - 最终字段名

3. **常见模式匹配**：
   - 参数类型：`[Ljava.lang.String;` = String[]
   - 正常字段名：`java.lang.String`
   - 类型污染：异常的对象类型标识

## 核心架构实现

### 🔧 SqlMethodInterceptor 核心技术

Synapse Databases 的核心是 `SqlMethodInterceptor` 动态代理，它实现了：

#### **智能方法拦截**
```java
@Override
 public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
     // 1. 检查是否是 BaseRepository 的方法
     if (isBaseRepositoryMethod(method)) {
         return handleBaseRepositoryMethod(proxy, method, args);
     }
     // 2. 检查是否是 Mapper 的方法  
     if (isMapperMethod(method)) {
         return callMapperMethod(method, args);
     }
     // 3. 处理默认方法
     if (method.isDefault()) {
         return executeDefaultMethod(proxy, method, args);
     }
 }
```

#### **Spring Context 集成**
- 使用 `ApplicationContext` 进行依赖注入
- 动态创建 `ServiceImpl` 实例
- 支持 Spring AOP 代理的深层次调用

#### **BaseRepository 默认方法支持**
框架完美支持 `BaseRepository` 的所有默认方法：
- ✅ `checkKeyUniqueness(T entity, String... keyFields)`
- ✅ `checkKeyUniqueness(BaseDTO<?> dto, String... keyFields)`  
- ✅ `pageWithCondition(PageDTO<?> queryDTO)`
- ✅ `listWithCondition(QueryDTO<?> queryDTO)`
- ✅ 所有 MyBatis-Plus IService 方法

### 🚀 高级特性

#### **全局 Mapper 扫描**
在框架级别配置，无需在每个模块重复：
```java
@Configuration
@MapperScan("com.indigo.**.repository.mapper")
public class MybatisPlusConfig {
    // 框架级别统一扫描所有 Mapper
}
```

#### **智能参数处理**
框架能智能处理各种参数类型混淆问题：
```java
// 支持 BaseDTO 类型的 checkKeyUniqueness 调用
countryService.checkKeyUniqueness(param, "code")  
// param: CountryDTO extends BaseDTO<String>
// "code": String 可变参数
```

#### **多层安全检查机制**
针对 Spring AOP 和反射调用中的类型问题，实现了多层防护：
1. **参数类型检测**：智能识别 `String[]`, `String`, `List` 等类型
2. **字段名解析**：自动处理嵌套数组和类型转换问题  
3. **反射安全检查**：确保字段访问的类型正确性
4. **异常容错处理**：详细的调试日志和错误处理

## 更新日志

### 2025-09-29
- 🔧 **修复 SqlMethodInterceptor 核心问题**
  - ✅ 解决 `checkKeyUniqueness` BaseDTO 类型调用问题
  - ✅ 修复 Spring AOP 可变参数类型污染问题  
  - ✅ 实现智能参数类型检测和转换
  - ✅ 添加多层安全检查机制
  - ✅ 修复 `NoSuchFieldException` 反射问题
- 🚀 **优化架构设计**
  - ✅ 全局 Mapper 扫描配置
  - ✅ 增强 BaseRepository 默认方法支持
  - ✅ 完善异常处理和调试日志
  - ✅ 提升框架稳定性和容错性

### 2024-12-19
- ✅ 完成 DTO 模块化重构
- ✅ 实现 Lombok Builder 模式
- ✅ 优化查询条件构建
- ✅ 移除 Map 类型参数，提升类型安全
- ✅ 统一分页返回类型为 `PageResult`

### 2024-12-18
- ✅ 实现无 ServiceImpl 架构
- ✅ 集成 MyBatis-Plus 原生 `@Select` 注解
- ✅ 支持复杂多表查询
- ✅ 实现动态数据源切换

## 技术支持

- **维护者**: 史偕成
- **邮箱**: [christ.sxc@gmail.com]
- **项目地址**: [https://github.com/christ-sxc/SynapseMOM]

---

**最后更新**: 2025-09-29  
**版本**: 0.0.1  
**维护者**: 史偕成