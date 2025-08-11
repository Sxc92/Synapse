# 扩展点指南

## 🎯 概述

Synapse Framework 提供了丰富的扩展点，允许开发者根据业务需求自定义和扩展框架功能。

## 🔧 核心扩展点

### 1. Repository 扩展

#### 自定义 Repository 方法

```java
@AutoRepository
public interface CustomTenantRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 自定义查询方法
    default List<IamTenant> findActiveTenants() {
        return list(new LambdaQueryWrapper<IamTenant>()
            .eq(IamTenant::getStatus, 1)
            .orderByDesc(IamTenant::getCreateTime));
    }
    
    // 自定义业务方法
    default boolean existsByCode(String code) {
        return count(new LambdaQueryWrapper<IamTenant>()
            .eq(IamTenant::getCode, code)) > 0;
    }
    
    // 调用 Mapper 的复杂查询
    default List<TenantWithCreatorDTO> findTenantsWithCreator(String status) {
        return getMapper().findTenantsWithCreator(status);
    }
}
```

#### 自定义 Mapper 方法

```java
@Mapper
public interface CustomTenantMapper extends BaseMapper<IamTenant> {
    
    // 自定义 SQL 查询
    @Select("SELECT * FROM iam_tenant WHERE status = #{status} AND create_time > #{startTime}")
    List<IamTenant> findRecentActiveTenants(@Param("status") Integer status, 
                                           @Param("startTime") LocalDateTime startTime);
    
    // 动态 SQL
    @Select("<script>" +
            "SELECT * FROM iam_tenant WHERE 1=1" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND code LIKE CONCAT('%', #{keyword}, '%')</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<IamTenant> findTenantsByCondition(@Param("status") Integer status, 
                                          @Param("keyword") String keyword);
}
```

### 2. 查询条件扩展

#### 自定义查询条件类型

```java
// 1. 定义新的查询类型
public enum CustomQueryType {
    IN_DATE_RANGE,    // 日期范围查询
    FUZZY_MATCH,      // 模糊匹配
    CUSTOM_LOGIC      // 自定义逻辑
}

// 2. 扩展 QueryCondition 注解
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomQueryCondition {
    CustomQueryType type();
    String field() default "";
    boolean ignoreNull() default true;
    boolean ignoreEmpty() default true;
}

// 3. 实现查询条件处理器
@Component
public class CustomQueryConditionHandler {
    
    public <T> void addCustomCondition(QueryWrapper<T> wrapper, Field field, Object value, CustomQueryCondition annotation) {
        switch (annotation.type()) {
            case IN_DATE_RANGE:
                handleDateRangeQuery(wrapper, field, value, annotation);
                break;
            case FUZZY_MATCH:
                handleFuzzyMatchQuery(wrapper, field, value, annotation);
                break;
            case CUSTOM_LOGIC:
                handleCustomLogicQuery(wrapper, field, value, annotation);
                break;
        }
    }
    
    private <T> void handleDateRangeQuery(QueryWrapper<T> wrapper, Field field, Object value, CustomQueryCondition annotation) {
        if (value instanceof LocalDateTime[] && ((LocalDateTime[]) value).length == 2) {
            LocalDateTime[] range = (LocalDateTime[]) value;
            String columnName = annotation.field().isEmpty() ? field.getName() : annotation.field();
            wrapper.between(columnName, range[0], range[1]);
        }
    }
}
```

### 3. 结果处理器扩展

#### 自定义结果处理器

```java
// 1. 定义结果处理器接口
public interface ResultProcessor<T> {
    T process(T result);
    int getOrder();
    boolean supports(Class<?> resultType);
}

// 2. 实现具体处理器
@Component
public class DataMaskingProcessor implements ResultProcessor<Object> {
    
    @Override
    public Object process(Object result) {
        if (result instanceof IamTenant) {
            IamTenant tenant = (IamTenant) result;
            // 数据脱敏处理
            if (tenant.getCode() != null) {
                tenant.setCode(maskCode(tenant.getCode()));
            }
        }
        return result;
    }
    
    @Override
    public int getOrder() {
        return 1; // 优先级
    }
    
    @Override
    public boolean supports(Class<?> resultType) {
        return IamTenant.class.isAssignableFrom(resultType);
    }
    
    private String maskCode(String code) {
        if (code.length() <= 2) return code;
        return code.substring(0, 2) + "***" + code.substring(code.length() - 2);
    }
}

// 3. 权限过滤处理器
@Component
public class PermissionFilterProcessor implements ResultProcessor<List<?>> {
    
    @Override
    public List<?> process(List<?> results) {
        return results.stream()
            .filter(this::hasPermission)
            .collect(Collectors.toList());
    }
    
    @Override
    public int getOrder() {
        return 2; // 在数据脱敏之后执行
    }
    
    @Override
    public boolean supports(Class<?> resultType) {
        return List.class.isAssignableFrom(resultType);
    }
    
    private boolean hasPermission(Object result) {
        // 权限检查逻辑
        return true;
    }
}
```

### 4. 查询拦截器扩展

#### 自定义查询拦截器

```java
// 1. 定义拦截器接口
public interface QueryInterceptor {
    void beforeQuery(QueryContext context);
    void afterQuery(QueryContext context, Object result);
    int getOrder();
}

// 2. 实现性能监控拦截器
@Component
public class PerformanceMonitorInterceptor implements QueryInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(PerformanceMonitorInterceptor.class);
    
    @Override
    public void beforeQuery(QueryContext context) {
        context.setStartTime(System.currentTimeMillis());
        log.debug("开始执行查询: {}", context.getMethodName());
    }
    
    @Override
    public void afterQuery(QueryContext context, Object result) {
        long duration = System.currentTimeMillis() - context.getStartTime();
        log.debug("查询执行完成: {}, 耗时: {}ms", context.getMethodName(), duration);
        
        // 慢查询告警
        if (duration > 1000) {
            log.warn("检测到慢查询: {}ms, 方法: {}", duration, context.getMethodName());
        }
    }
    
    @Override
    public int getOrder() {
        return 1;
    }
}

// 3. 实现 SQL 日志拦截器
@Component
public class SqlLogInterceptor implements QueryInterceptor {
    
    @Override
    public void beforeQuery(QueryContext context) {
        log.info("执行SQL: {}", context.getSql());
        log.info("参数: {}", context.getParameters());
    }
    
    @Override
    public void afterQuery(QueryContext context, Object result) {
        log.info("SQL执行结果: {}", result);
    }
    
    @Override
    public int getOrder() {
        return 2;
    }
}
```

### 5. 数据源扩展

#### 自定义数据源路由

```java
// 1. 实现数据源路由策略
@Component
public class CustomDataSourceRouter implements DataSourceRouter {
    
    @Override
    public String determineDataSource(Method method, Object[] args) {
        // 根据方法名或参数决定数据源
        if (method.getName().contains("Read")) {
            return "slave";
        }
        if (method.getName().contains("Write")) {
            return "master";
        }
        
        // 根据参数决定数据源
        if (args != null && args.length > 0) {
            if (args[0] instanceof String && ((String) args[0]).startsWith("TEMP")) {
                return "temp";
            }
        }
        
        return "master"; // 默认数据源
    }
}

// 2. 自定义数据源负载均衡
@Component
public class CustomLoadBalancer implements DataSourceLoadBalancer {
    
    private final AtomicInteger counter = new AtomicInteger(0);
    
    @Override
    public String selectDataSource(List<String> availableDataSources) {
        if (availableDataSources.isEmpty()) {
            throw new IllegalStateException("没有可用的数据源");
        }
        
        // 轮询策略
        int index = counter.getAndIncrement() % availableDataSources.size();
        return availableDataSources.get(index);
    }
}
```

### 6. 缓存扩展

#### 自定义缓存策略

```java
// 1. 自定义缓存键生成器
@Component
public class CustomCacheKeyGenerator implements CacheKeyGenerator {
    
    @Override
    public String generateKey(String methodName, Object... args) {
        StringBuilder keyBuilder = new StringBuilder(methodName);
        
        for (Object arg : args) {
            if (arg != null) {
                keyBuilder.append(":").append(arg.toString());
            }
        }
        
        return keyBuilder.toString();
    }
}

// 2. 自定义缓存过期策略
@Component
public class CustomExpirationPolicy implements CacheExpirationPolicy {
    
    @Override
    public Duration getExpiration(String cacheName, String key) {
        // 根据缓存名称和键决定过期时间
        if (cacheName.contains("user")) {
            return Duration.ofHours(1);
        }
        if (cacheName.contains("config")) {
            return Duration.ofDays(1);
        }
        return Duration.ofMinutes(30); // 默认30分钟
    }
}
```

### 7. 事件扩展

#### 自定义事件处理器

```java
// 1. 定义自定义事件
public class CustomBusinessEvent extends DomainEvent {
    private String businessType;
    private Map<String, Object> data;
    
    public CustomBusinessEvent(String aggregateId, String businessType, Map<String, Object> data) {
        super(aggregateId);
        this.businessType = businessType;
        this.data = data;
    }
}

// 2. 实现事件处理器
@Component
public class CustomBusinessEventHandler {
    
    @EventListener
    public void handleCustomBusinessEvent(CustomBusinessEvent event) {
        log.info("处理自定义业务事件: {}", event.getBusinessType());
        
        // 根据业务类型处理
        switch (event.getBusinessType()) {
            case "TENANT_CREATED":
                handleTenantCreated(event);
                break;
            case "USER_REGISTERED":
                handleUserRegistered(event);
                break;
            default:
                log.warn("未知的业务事件类型: {}", event.getBusinessType());
        }
    }
    
    private void handleTenantCreated(CustomBusinessEvent event) {
        // 处理租户创建事件
        log.info("处理租户创建事件: {}", event.getData());
    }
    
    private void handleUserRegistered(CustomBusinessEvent event) {
        // 处理用户注册事件
        log.info("处理用户注册事件: {}", event.getData());
    }
}
```

## 🔧 配置扩展

### 1. 自定义配置属性

```java
// 1. 定义配置属性类
@ConfigurationProperties(prefix = "synapse.custom")
@Data
public class CustomProperties {
    private String customFeature = "default";
    private int customTimeout = 5000;
    private List<String> customWhitelist = new ArrayList<>();
}

// 2. 自动配置类
@Configuration
@EnableConfigurationProperties(CustomProperties.class)
@ConditionalOnProperty(prefix = "synapse.custom", name = "enabled", havingValue = "true")
public class CustomAutoConfiguration {
    
    @Autowired
    private CustomProperties customProperties;
    
    @Bean
    @ConditionalOnMissingBean
    public CustomService customService() {
        return new CustomService(customProperties);
    }
}
```

### 2. 条件化配置

```java
@Configuration
@ConditionalOnClass(SomeClass.class)
@ConditionalOnProperty(prefix = "synapse.feature", name = "enabled", havingValue = "true")
public class FeatureAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public FeatureService featureService() {
        return new FeatureService();
    }
}
```

## 📝 最佳实践

### 1. 扩展点使用原则

- **单一职责**: 每个扩展点只负责一个功能
- **向后兼容**: 扩展不影响现有功能
- **性能考虑**: 扩展点不应显著影响性能
- **错误处理**: 扩展点要有完善的错误处理

### 2. 扩展点开发流程

1. **分析需求**: 确定需要扩展的功能点
2. **设计接口**: 定义清晰的扩展接口
3. **实现扩展**: 编写具体的扩展实现
4. **测试验证**: 确保扩展功能正确
5. **文档说明**: 编写使用文档

### 3. 扩展点调试

```java
// 启用调试日志
logging:
  level:
    com.indigo.synapse: DEBUG
    com.indigo.synapse.extension: DEBUG

// 使用调试工具
@Component
public class ExtensionDebugger {
    
    @EventListener
    public void debugExtension(ExtensionEvent event) {
        log.debug("扩展点执行: {}", event);
    }
}
```

---

**扩展点设计原则**: 简单、灵活、可组合、易维护 