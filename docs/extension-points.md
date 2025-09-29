# 扩展点指南

## 🎯 概述

Synapse Framework 提供了丰富的扩展点，允许开发者根据业务需求自定义和扩展框架功能。

## 🔧 核心扩展点

### 1. Repository 扩展

#### 自定义 Repository 方法

```java
@AutoRepository
public interface CustomTenantRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // ✅ 使用增强查询构建器
    default List<IamTenant> findActiveTenants() {
        return enhancedQuery(IamTenant.class)
            .eq(IamTenant::getStatus, 1)
            .orderByDesc(IamTenant::getCreateTime)
            .list();
    }
    
    // ✅ 使用checkKeyUniqueness方法
    default boolean isCodeUnique(IamTenant tenant, String... additionalFields) {
        return !checkKeyUniqueness(tenant, "code", additionalFields);
    }
    
    // ✅ 分页查询支持VO映射
    default PageResult<TenantVO> pageTenants(PageDTO<IamTenant> pageDTO) {
        return enhancedQuery(IamTenant.class)
            .page(pageDTO, TenantVO.class);
    }
    
    // ✅ 多表关联查询
    default PageResult<TenantWithCreatorVO> pageTenantsWithCreator(PageDTO<IamTenant> pageDTO) {
        return enhancedQuery(IamTenant.class)
            .leftJoin("iam_user", "u", "t.creator_id = u.id")
            .select("t.*", "u.username as creator_name")
            .page(pageDTO, TenantWithCreatorVO.class);
    }
    
    // ✅ 聚合查询
    default TenantStatisticsVO getTenantStatistics() {
        return enhancedQuery(IamTenant.class)
            .select(
                count(), 
                sum(IamTenant::getStatus), 
                avg("create_time")
            )
            .single(TenantStatisticsVO.class);
    }
    
    // ✅ 异步查询（实验性功能）
    default CompletableFuture<List<IamTenant>> findActiveTenantsAsync() {
        return enhancedQuery(IamTenant.class)
            .eq(IamTenant::getStatus, 1)
            .listAsync();
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

### 2. EnhancedQueryBuilder 扩展

#### 自定义查询构建器扩展

```java
// 1. 扩展 EnhancedQueryBuilder
public class CustomQueryBuilder<T> extends EnhancedQueryBuilder<T> {
    
    // ✅ 自定义地理位置查询
    public CustomQueryBuilder<T> withinRadius(String latField, String lngField, 
                                             double lat, double lng, double radiusKm) {
        // HAVERSINE公式实现地理围栏查询
        String sqlFunction = String.format(
            "6371 * acos(cos(radians(%f)) * cos(radians(%s)) * cos(radians(%s) - radians(%f)) + sin(radians(%f)) * sin(radians(%s)))",
            lat, latField, lngField, lng, lat, latField
        );
        return having(sqlFunction + " <= " + radiusKm);
    }
    
    // ✅ 自定义文本搜索（全文索引）
    public CustomQueryBuilder<T> fullTextSearch(String field, String keywords, boolean naturalLanguage) {
        String fulltextFunction = naturalLanguage 
            ? String.format("MATCH (%s) AGAINST ('%s' IN NATURAL LANGUAGE MODE)", field, keywords)
            : String.format("MATCH (%s) AGAINST ('%s' IN BOOLEAN MODE)", field, keywords);
        return where(fulltextFunction);
    }
    
    // ✅ 自定义聚合统计
    public CustomQueryBuilder<T> advancedStatistics(String groupByField) {
        return select(
            count(), 
            sum("value"), 
            avg("value"), 
            max("value"), 
            min("value"),
            variance("value"),
            stddev("value")
        ).groupBy(groupByField);
    }
    
    // ✅ 自定义分页性能优化
    public CustomQueryBuilder<T> optimizePagination() {
        // 添加查询优化提示
        return where("/*+ USE_INDEX(primary) */").orderBy("id");
    }
}

// 2. 自定义 Repository 使用扩展查询构建器
@AutoRepository
public interface LocationAwareRepository<T> extends BaseRepository<T, BaseMapper<T>> {
    
    default List<T> findNearby(String latField, String lngField, 
                              double lat, double lng, double radiusKm) {
        return new CustomQueryBuilder<>(getEntityClass())
            .withinRadius(latField, lngField, lat, lng, radiusKm)
            .orderBy(sqlFunction("6371 * acos(...)")) // 按距离排序
            .list();
    }
    
    default List<T> searchByFullText(String field, String keywords) {
        return new CustomQueryBuilder<>(getEntityClass())
            .fullTextSearch(field, keywords, true)
            .eq("status", 1) // 只搜索激活的记录
            .list();
    }
}

// 3. 自定义查询条件注解扩展
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeoQueryCondition {
    String latField() default "latitude";
    String lngField() default "longitude";
    double radiusKm() default 10.0;
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FullTextSearchCondition {
    String field();
    boolean naturalLanguage() default true;
}
```
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

### 7. SqlMethodInterceptor 扩展

#### 自定义方法拦截器

```java
// ✅ 1. 扩展 SqlMethodInterceptor
@Component
public class CustomSqlMethodInterceptor extends SqlMethodInterceptor {
    
    // 🎯 自定义默认方法处理
    @Override
    protected Object executeCustomDefaultMethod(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        
        return switch (methodName) {
            case "findByCustomLogic" -> handleCustomLogicMethod(proxy, args);
            case "validateBusinessRule" -> handleBusinessValidationMethod(proxy, args);
            case "processComplexData" -> handleComplexDataProcessingMethod(proxy, args);
            default -> super.executeDefaultMethod(proxy, method, args);
        };
    }
    
    private Object handleCustomLogicMethod(Object proxy, Object[] args) {
        // 🧠 自定义业务逻辑查询
        var entityClass = getEntityClass(proxy);
        Object condition = args[0];
        
        return enhancedQuery(entityClass)
            .applyCustomLogic(condition)
            .list();
    }
    
    private Object handleBusinessValidationMethod(Object proxy, Object[] args) {
        // ✅ 自定义业务规则验证
        Object entity = args[0];
        String ruleType = (String) args[1];
        
        return switch (ruleType) {
            case "UNIQUE_CONSTRAINT" -> validateUniqueConstraint(entity);
            case "BUSINESS_RULE" -> validateBusinessRule(entity);
            case "DATA_INTEGRITY" -> validateDataIntegrity(entity);
            default -> throw new UnsupportedOperationException("Unknown validation rule: " + ruleType);
        };
    }
    
    // 🎯 参数预处理扩展
    @Override
    protected Object[] preprocessArguments(Method method, Object[] args) {
        // 智能参数类型检测和转换
        Object[] processedArgs = super.preprocessArguments(method, args);
        
        for (int i = 0; i < processedArgs.length; i++) {
            // 🌐 支持 JSON 字符串自动解析
            if (processedArgs[i] instanceof String str && isJsonString(str)) {
                processedArgs[i] = parseJsonToTypedObject(str, method.getParameterTypes()[i]);
            }
            
            // 📊 支持分页参数智能转换
            if (processedArgs[i] instanceof Map map && isPageParam(map)) {
                processedArgs[i] = convertMapToPageDTO(map);
            }
        }
        
        return processedArgs;
    }
}

// ✅ 2. 自定义 Repository 使用扩展拦截器
@AutoRepository
@InterceptWith(CustomSqlMethodInterceptor.class)
public interface CustomBusinessRepository<T> extends BaseRepository<T, BaseMapper<T>> {
    
    // 🎯 自定义方法被拦截器处理
    default List<T> findByCustomLogic(CustomQueryCondition condition) {
        // SqlMethodInterceptor 会拦截此方法调用
        return null; // 实际上会被拦截器替换实现
    }
    
    default boolean validateBusinessRule(Object entity, String ruleType) {
        // 业务规则验证
        return true; // 会被拦截器处理
    }
    
    default PageResult<T> processComplexData(Object... params) {
        // 复杂数据处理
        return null; // 会被拦截器处理
    }
}
```

### 8. 多语言国际化扩展

#### 自定义国际化处理器

```java
// ✅ 1. 扩展国际化消息解析器
@Component
public class CustomI18nMessageResolver extends I18nMessageResolver {
    
    // 🌍 支持动态语言包加载
    @Override
    public String resolveMessage(String key, Object[] args, Locale locale) {
        // 尝试从缓存加载
        String cached = getFromCache(key, locale);
        if (cached != null) {
            return formatMessage(cached, args);
        }
        
        // 动态从数据库加载
        String message = loadFromDatabase(key, locale);
        if (message != null) {
            putToCache(key, locale, message);
            return formatMessage(message, args);
        }
        
        return super.resolveMessage(key, args, locale);
    }
    
    // 🌐 支持多租户语言包
    public String resolveMessageForTenant(String key, Object[] args, Locale locale, String tenantId) {
        String tenantSpecificKey = tenantId + ":" + key;
        return resolveMessage(tenantSpecificKey, args, locale);
    }
    
    // 📊 支持智能语言检测
    public Locale detectLanguage(String text) {
        // 基于内容的语言检测算法
        return detectLanguageFromContent(text);
    }
}

// ✅ 2. 自定义多语言 Repository
@AutoRepository
public interface MultilingualRepository<T> extends BaseRepository<T, BaseMapper<T>> {
    
    // 🌍 根据语言环境查询数据
    default List<T> findByLanguage(String languageCode, QueryCondition condition) {
        return enhancedQuery(getEntityClass())
            .eq("language_code", languageCode)
            .apply(condition)
            .list();
    }
    
    // 🔄 多语言数据同步
    default CompletableFuture<Void> syncMultilingualData(List<T> entities, List<String> languages) {
        return CompletableFuture.runAsync(() -> {
            languages.parallelStream().forEach(lang -> {
                entities.forEach(entity -> {
                    T translated = translateEntity(entity, lang);
                    saveOrUpdate(translated);
                });
            });
        });
    }
}
```

### 9. 事件扩展

#### 自定义事件处理器

```java
// ✅ 1. 定义自定义领域事件
public class CustomBusinessEvent extends DomainEvent {
    private String businessType;
    private Map<String, Object> businessData;
    private String correlationId; // 关联ID用于追踪
    
    public CustomBusinessEvent(String aggregateId, String businessType, 
                              Map<String, Object> businessData, String correlationId) {
        super(aggregateId);
        this.businessType = businessType;
        this.businessData = businessData;
        this.correlationId = correlationId;
    }
    
    // 🎯 事件版本控制
    @Override
    public int getVersion() {
        return 2; // 当前事件版本
    }
}

// ✅ 2. 智能事件处理器
@Component
public class SmartBusinessEventHandler {
    
    @EventListener
    @Async("businessEventExecutor")
    public CompletableFuture<Void> handleBusinessEvent(CustomBusinessEvent event) {
        log.info("处理业务事件: {} (版本: {})", 
                event.getBusinessType(), event.getVersion());
        
        // 🎯 版本兼容处理
        return switch (event.getVersion()) {
            case 1 -> handleLegacyEvent(event);
            case 2 -> handleCurrentEvent(event);
            default -> handleUnsupportedVersion(event);
        };
    }
    
    // 🔄 事件重试和补偿
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CompletableFuture<Void> handleCurrentEvent(CustomBusinessEvent event) {
        try {
            // 业务处理逻辑
            processBusinessLogic(event);
            
            // 📊 事件处理完成统计
            recordEventProcessingMetrics(event);
            
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("事件处理失败: {}", event.getBusinessType(), e);
            throw e; // 触发重试
        }
    }
    
    // 🕰️ 延迟事件处理
    @Scheduled(fixedDelay = 5000)
    public void processDelayedEvents() {
        List<CustomBusinessEvent> delayedEvents = eventRepository.findDelayedEvents();
        
        delayedEvents.forEach(event -> {
            if (shouldProcessNow(event)) {
                eventPublisher.publishEvent(event);
            }
        });
    }

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