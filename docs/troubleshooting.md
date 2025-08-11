# 故障排除指南

## 🚨 常见问题

### 1. 启动问题

#### 问题：应用启动失败

**错误信息**：
```
Error creating bean with name 'xxxRepository': Invocation of init method failed
```

**可能原因**：
- 缺少必要的依赖
- 数据库连接失败
- 配置错误

**解决方案**：

1. **检查依赖**：
```xml
<!-- 确保添加了必要的依赖 -->
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-databases</artifactId>
</dependency>
```

2. **检查数据库配置**：
```yaml
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/your_database
          username: your_username
          password: your_password
          driver-class-name: com.mysql.cj.jdbc.Driver
```

3. **检查数据库连接**：
```bash
# 测试数据库连接
mysql -h localhost -u your_username -p your_database
```

#### 问题：Repository Bean 创建失败

**错误信息**：
```
No qualifying bean of type 'xxxRepository' available
```

**可能原因**：
- 缺少 `@AutoRepository` 注解
- 包扫描配置错误
- 接口定义错误

**解决方案**：

1. **检查注解**：
```java
@AutoRepository  // 确保添加此注解
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // ...
}
```

2. **检查包扫描**：
```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.indigo", "your.package"})  // 确保扫描到框架包
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

3. **检查接口定义**：
```java
// 确保正确继承 BaseRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    // 泛型参数：实体类型, Mapper类型
}
```

### 2. 查询问题

#### 问题：查询条件不生效

**错误信息**：
```
查询结果不符合预期，条件没有生效
```

**可能原因**：
- `@QueryCondition` 注解配置错误
- 字段名与数据库列名不匹配
- 数据类型不匹配

**解决方案**：

1. **检查注解配置**：
```java
@Data
public class TenantQueryDTO extends PageDTO {
    @QueryCondition(type = QueryCondition.QueryType.EQ)  // 确保类型正确
    private Integer status;
    
    @QueryCondition(type = QueryCondition.QueryType.LIKE, field = "tenant_code")  // 指定列名
    private String code;
}
```

2. **检查字段映射**：
```java
@Data
@TableName("iam_tenant")  // 确保表名正确
public class IamTenant {
    @TableField("tenant_code")  // 确保字段映射正确
    private String code;
}
```

3. **启用 SQL 日志**：
```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

#### 问题：分页查询异常

**错误信息**：
```
分页查询返回结果不正确
```

**可能原因**：
- 分页参数错误
- 排序字段不存在
- 数据库方言不支持

**解决方案**：

1. **检查分页参数**：
```java
// 确保分页参数正确
TenantQueryDTO query = TenantQueryDTO.builder()
    .current(1)  // 从1开始
    .size(10)    // 每页大小
    .build();
```

2. **检查排序字段**：
```java
// 确保排序字段存在
query.addOrderBy("create_time", "DESC");  // 字段名要与数据库列名一致
```

3. **配置分页插件**：
```java
@Configuration
public class MybatisPlusConfig {
    
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### 3. 性能问题

#### 问题：查询性能慢

**错误信息**：
```
查询执行时间过长
```

**可能原因**：
- 缺少索引
- SQL 语句优化不足
- 数据量过大

**解决方案**：

1. **添加索引**：
```sql
-- 为常用查询字段添加索引
CREATE INDEX idx_tenant_status ON iam_tenant(status);
CREATE INDEX idx_tenant_code ON iam_tenant(code);
CREATE INDEX idx_tenant_create_time ON iam_tenant(create_time);
```

2. **优化查询**：
```java
// 使用分页查询避免全表扫描
PageResult<IamTenant> result = tenantsRepository.pageWithCondition(query);

// 使用流式查询优化复杂条件
List<IamTenant> tenants = tenantsRepository.list(
    tenantsRepository.query()
        .eq(IamTenant::getStatus, 1)
        .orderByDesc(IamTenant::getCreateTime)
);
```

3. **使用缓存**：
```java
@Cacheable(value = "tenant", key = "#id")
public IamTenant getTenantById(String id) {
    return tenantsRepository.getById(id);
}
```

#### 问题：内存使用过高

**错误信息**：
```
内存使用率过高，可能导致 OOM
```

**可能原因**：
- 查询结果集过大
- 缓存配置不当
- 连接池配置错误

**解决方案**：

1. **限制查询结果**：
```java
// 使用分页查询限制结果集大小
PageResult<IamTenant> result = tenantsRepository.pageWithCondition(query);

// 使用流式查询处理大数据量
tenantsRepository.listMaps(
    tenantsRepository.queryWrapper()
        .select("id", "code", "name")  // 只查询需要的字段
);
```

2. **优化缓存配置**：
```yaml
spring:
  cache:
    redis:
      time-to-live: 600000  # 设置合理的过期时间
      cache-null-values: false  # 不缓存空值
```

3. **优化连接池**：
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10  # 限制连接池大小
      minimum-idle: 5
      connection-timeout: 30000
```

### 4. 配置问题

#### 问题：配置不生效

**错误信息**：
```
配置修改后不生效
```

**可能原因**：
- 配置文件位置错误
- 配置项名称错误
- 配置优先级问题

**解决方案**：

1. **检查配置文件位置**：
```
src/main/resources/
├── application.yml          # 主配置文件
├── application-dev.yml      # 开发环境配置
├── application-prod.yml     # 生产环境配置
└── bootstrap.yml           # 启动配置
```

2. **检查配置项名称**：
```yaml
# 确保配置项名称正确
synapse:
  databases:
    enabled: true
  cache:
    enabled: true
  security:
    enabled: true
```

3. **使用配置优先级**：
```yaml
# 命令行参数 > 环境变量 > 配置文件
spring:
  profiles:
    active: dev  # 激活开发环境配置
```

#### 问题：多数据源配置错误

**错误信息**：
```
数据源切换失败
```

**可能原因**：
- 数据源配置错误
- 路由策略错误
- 注解使用错误

**解决方案**：

1. **检查数据源配置**：
```yaml
spring:
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/master_db
          username: root
          password: password
        slave:
          url: jdbc:mysql://localhost:3306/slave_db
          username: root
          password: password
```

2. **检查路由策略**：
```java
@Component
public class CustomDataSourceRouter implements DataSourceRouter {
    
    @Override
    public String determineDataSource(Method method, Object[] args) {
        // 实现数据源路由逻辑
        if (method.getName().contains("Read")) {
            return "slave";
        }
        return "master";
    }
}
```

3. **使用数据源注解**：
```java
@DataSource("slave")
public List<IamTenant> getTenantsFromSlave() {
    return tenantsRepository.list();
}
```

### 5. 扩展问题

#### 问题：自定义扩展不生效

**错误信息**：
```
自定义扩展功能不生效
```

**可能原因**：
- 扩展点实现错误
- 注册方式错误
- 优先级配置错误

**解决方案**：

1. **检查扩展点实现**：
```java
@Component  // 确保添加 @Component 注解
public class CustomResultProcessor implements ResultProcessor<Object> {
    
    @Override
    public Object process(Object result) {
        // 实现处理逻辑
        return result;
    }
    
    @Override
    public int getOrder() {
        return 1;  // 设置优先级
    }
    
    @Override
    public boolean supports(Class<?> resultType) {
        return true;  // 支持的类型
    }
}
```

2. **检查注册方式**：
```java
@Configuration
public class CustomConfiguration {
    
    @Bean
    public CustomService customService() {
        return new CustomService();
    }
}
```

3. **检查优先级配置**：
```java
@Component
@Order(1)  // 使用 @Order 注解设置优先级
public class HighPriorityProcessor implements ResultProcessor<Object> {
    // ...
}
```

## 🔧 调试技巧

### 1. 启用调试日志

```yaml
logging:
  level:
    com.indigo.synapse: DEBUG
    com.indigo.synapse.databases: DEBUG
    com.indigo.synapse.cache: DEBUG
    com.indigo.synapse.security: DEBUG
    com.indigo.synapse.events: DEBUG
```

### 2. 使用调试工具

```java
@Component
public class DebugInterceptor implements QueryInterceptor {
    
    @Override
    public void beforeQuery(QueryContext context) {
        log.debug("查询开始: {}", context.getMethodName());
        log.debug("SQL: {}", context.getSql());
        log.debug("参数: {}", context.getParameters());
    }
    
    @Override
    public void afterQuery(QueryContext context, Object result) {
        log.debug("查询完成: {}, 结果: {}", context.getMethodName(), result);
    }
    
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;  // 最高优先级
    }
}
```

### 3. 性能监控

```java
@Aspect
@Component
public class PerformanceMonitor {
    
    @Around("@annotation(QueryMonitor)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("方法: {}, 耗时: {}ms", methodName, duration);
            
            if (duration > 1000) {
                log.warn("慢查询: {}ms, 方法: {}", duration, methodName);
            }
            
            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("异常: {}ms, 方法: {}, 错误: {}", duration, methodName, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
```

## 📞 获取帮助

### 1. 查看日志

```bash
# 查看应用日志
tail -f logs/application.log

# 查看错误日志
grep "ERROR" logs/application.log
```

### 2. 检查健康状态

```bash
# 检查应用健康状态
curl http://localhost:8080/actuator/health

# 检查数据库连接
curl http://localhost:8080/actuator/health/db
```

### 3. 提交 Issue

如果问题仍然存在，请提交 Issue 到项目仓库，包含以下信息：

- 错误信息
- 环境信息
- 复现步骤
- 相关配置
- 日志文件

---

**故障排除原则**: 系统分析、逐步排查、记录日志、及时反馈 