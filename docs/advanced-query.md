# 高级查询指南

## 🎯 概述

Synapse Framework 提供了强大的查询能力，支持从简单查询到复杂多表关联查询的各种场景。

## 🔍 查询方式对比

| 查询方式 | 适用场景 | 性能 | 复杂度 | 推荐度 |
|---------|---------|------|--------|--------|
| 注解驱动 | 简单条件查询 | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| 流式查询 | 复杂条件查询 | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| 原生SQL | 多表关联查询 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |

## 📊 注解驱动查询

### 1. 基础查询条件

```java
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class TenantQueryDTO extends PageDTO {
    
    @QueryCondition(type = QueryCondition.QueryType.EQ)
    private Integer status;
    
    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String code;
    
    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String name;
    
    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "create_time")
    private LocalDateTime[] createTime;
    
    @QueryCondition(type = QueryCondition.QueryType.IN)
    private List<Integer> statusList;
    
    @QueryCondition(type = QueryCondition.QueryType.IS_NOT_NULL)
    private Boolean hasDescription;
}
```

### 2. 高级查询条件

```java
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class AdvancedQueryDTO extends PageDTO {
    
    // 精确匹配
    @QueryCondition(type = QueryCondition.QueryType.EQ)
    private String exactCode;
    
    // 模糊匹配
    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String fuzzyName;
    
    // 左模糊匹配
    @QueryCondition(type = QueryCondition.QueryType.LIKE_LEFT)
    private String leftFuzzyCode;
    
    // 右模糊匹配
    @QueryCondition(type = QueryCondition.QueryType.LIKE_RIGHT)
    private String rightFuzzyCode;
    
    // 范围查询
    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "create_time")
    private LocalDateTime[] createTimeRange;
    
    // 大于等于
    @QueryCondition(type = QueryCondition.QueryType.GE, field = "update_time")
    private LocalDateTime updateTimeFrom;
    
    // 小于等于
    @QueryCondition(type = QueryCondition.QueryType.LE, field = "update_time")
    private LocalDateTime updateTimeTo;
    
    // 包含查询
    @QueryCondition(type = QueryCondition.QueryType.IN)
    private List<String> codeList;
    
    // 不包含查询
    @QueryCondition(type = QueryCondition.QueryType.NOT_IN)
    private List<String> excludeCodeList;
    
    // 为空查询
    @QueryCondition(type = QueryCondition.QueryType.IS_NULL, field = "description")
    private Boolean descriptionIsNull;
    
    // 不为空查询
    @QueryCondition(type = QueryCondition.QueryType.IS_NOT_NULL, field = "description")
    private Boolean descriptionIsNotNull;
}
```

### 3. 使用示例

```java
@Service
public class TenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    // 简单查询
    public List<IamTenant> findActiveTenants() {
        TenantQueryDTO query = TenantQueryDTO.builder()
            .status(1)
            .build();
        return tenantsRepository.listWithCondition(query);
    }
    
    // 复杂查询
    public PageResult<IamTenant> findTenantsWithConditions(AdvancedQueryDTO query) {
        return tenantsRepository.pageWithCondition(query);
    }
    
    // 时间范围查询
    public List<IamTenant> findTenantsByTimeRange(LocalDateTime start, LocalDateTime end) {
        TenantQueryDTO query = TenantQueryDTO.builder()
            .createTime(new LocalDateTime[]{start, end})
            .build();
        return tenantsRepository.listWithCondition(query);
    }
}
```

## 🚀 流式查询

### 1. LambdaQueryWrapper 流式查询

```java
@Service
public class TenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    // 基础流式查询
    public List<IamTenant> findTenantsByCondition(String keyword, Integer status) {
        return tenantsRepository.list(
            tenantsRepository.query()
                .like(StringUtils.isNotBlank(keyword), IamTenant::getCode, keyword)
                .eq(status != null, IamTenant::getStatus, status)
                .orderByDesc(IamTenant::getCreateTime)
        );
    }
    
    // 复杂条件查询
    public List<IamTenant> findTenantsWithComplexCondition(String keyword, Integer status, 
                                                          LocalDateTime startTime, LocalDateTime endTime) {
        return tenantsRepository.list(
            tenantsRepository.query()
                .like(StringUtils.isNotBlank(keyword), IamTenant::getCode, keyword)
                .or()
                .like(StringUtils.isNotBlank(keyword), IamTenant::getName, keyword)
                .eq(status != null, IamTenant::getStatus, status)
                .between(startTime != null && endTime != null, 
                        IamTenant::getCreateTime, startTime, endTime)
                .isNotNull(IamTenant::getDescription)
                .orderByDesc(IamTenant::getCreateTime)
        );
    }
    
    // 分页查询
    public PageResult<IamTenant> findTenantsWithPage(String keyword, Integer status, int page, int size) {
        Page<IamTenant> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<IamTenant> wrapper = tenantsRepository.query()
            .like(StringUtils.isNotBlank(keyword), IamTenant::getCode, keyword)
            .eq(status != null, IamTenant::getStatus, status)
            .orderByDesc(IamTenant::getCreateTime);
        
        IPage<IamTenant> result = tenantsRepository.page(pageParam, wrapper);
        return PageResult.from(result);
    }
}
```

### 2. QueryWrapper 流式查询

```java
@Service
public class TenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    // 使用 QueryWrapper
    public List<IamTenant> findTenantsWithQueryWrapper(String keyword, Integer status) {
        QueryWrapper<IamTenant> wrapper = tenantsRepository.queryWrapper()
            .like(StringUtils.isNotBlank(keyword), "code", keyword)
            .eq(status != null, "status", status)
            .orderByDesc("create_time");
        
        return tenantsRepository.list(wrapper);
    }
    
    // 复杂 SQL 条件
    public List<IamTenant> findTenantsWithCustomCondition(String keyword, Integer status) {
        QueryWrapper<IamTenant> wrapper = tenantsRepository.queryWrapper()
            .and(w -> w.like("code", keyword).or().like("name", keyword))
            .eq(status != null, "status", status)
            .apply("create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)")
            .orderByDesc("create_time");
        
        return tenantsRepository.list(wrapper);
    }
}
```

## 🔗 多表关联查询

### 1. 使用 @Select 注解

```java
@Mapper
public interface TenantMapper extends BaseMapper<IamTenant> {
    
    // 简单关联查询
    @Select("SELECT t.*, u.username as creator_name FROM iam_tenant t " +
            "LEFT JOIN iam_user u ON t.creator_id = u.id " +
            "WHERE t.status = #{status}")
    List<TenantWithCreatorDTO> findTenantsWithCreator(@Param("status") String status);
    
    // 复杂关联查询
    @Select("""
        SELECT t.*, u.username as creator_name, u.email as creator_email,
               d.name as department_name, d.code as department_code
        FROM iam_tenant t
        LEFT JOIN iam_user u ON t.creator_id = u.id
        LEFT JOIN iam_department d ON u.department_id = d.id
        WHERE t.status = #{status}
        AND (#{keyword} IS NULL OR t.code LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY t.create_time DESC
    """)
    List<TenantWithCreatorAndDeptDTO> findTenantsWithCreatorAndDept(
        @Param("status") String status, 
        @Param("keyword") String keyword
    );
    
    // 分页关联查询
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
    
    // 统计查询
    @Select("SELECT COUNT(*) FROM iam_tenant t " +
            "LEFT JOIN iam_user u ON t.creator_id = u.id " +
            "WHERE t.status = #{status}")
    long countTenantsWithCreator(@Param("status") String status);
}
```

### 2. 在 Repository 中使用

```java
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // 调用 Mapper 的关联查询
    default List<TenantWithCreatorDTO> findTenantsWithCreator(String status) {
        return getMapper().findTenantsWithCreator(status);
    }
    
    // 分页关联查询
    default PageResult<TenantWithCreatorDTO> findTenantsWithCreatorPage(String status, int page, int size) {
        int offset = (page - 1) * size;
        
        // 查询数据
        List<TenantWithCreatorDTO> records = getMapper().findTenantsWithCreatorPage(status, size, offset);
        
        // 查询总数
        long total = getMapper().countTenantsWithCreator(status);
        
        // 构建分页结果
        PageResult<TenantWithCreatorDTO> result = new PageResult<>();
        result.setCurrent(page);
        result.setSize(size);
        result.setTotal(total);
        result.setRecords(records);
        
        return result;
    }
}
```

## ⚡ 性能优化

### 1. 索引优化

```sql
-- 为常用查询字段创建索引
CREATE INDEX idx_tenant_status ON iam_tenant(status);
CREATE INDEX idx_tenant_code ON iam_tenant(code);
CREATE INDEX idx_tenant_create_time ON iam_tenant(create_time);
CREATE INDEX idx_tenant_creator_id ON iam_tenant(creator_id);

-- 复合索引
CREATE INDEX idx_tenant_status_create_time ON iam_tenant(status, create_time);
CREATE INDEX idx_tenant_code_status ON iam_tenant(code, status);
```

### 2. 查询优化

```java
@Service
public class OptimizedTenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    // 1. 避免 SELECT *
    @Mapper
    public interface OptimizedTenantMapper extends BaseMapper<IamTenant> {
        @Select("SELECT id, code, name, status, create_time FROM iam_tenant WHERE status = #{status}")
        List<IamTenant> findActiveTenantsOptimized(@Param("status") Integer status);
    }
    
    // 2. 使用分页避免全表扫描
    public List<IamTenant> findTenantsWithPagination(String keyword, int pageSize) {
        Page<IamTenant> page = new Page<>(1, pageSize);
        
        LambdaQueryWrapper<IamTenant> wrapper = tenantsRepository.query()
            .like(StringUtils.isNotBlank(keyword), IamTenant::getCode, keyword)
            .orderByDesc(IamTenant::getCreateTime);
        
        IPage<IamTenant> result = tenantsRepository.page(page, wrapper);
        return result.getRecords();
    }
    
    // 3. 使用 EXISTS 代替 IN
    @Mapper
    public interface OptimizedTenantMapper extends BaseMapper<IamTenant> {
        @Select("SELECT * FROM iam_tenant t WHERE EXISTS (" +
                "SELECT 1 FROM iam_user u WHERE u.id = t.creator_id AND u.status = 1" +
                ") AND t.status = #{status}")
        List<IamTenant> findTenantsWithActiveCreator(@Param("status") Integer status);
    }
}
```

### 3. 缓存优化

```java
@Service
public class CachedTenantService {
    
    @Autowired
    private TenantsRepository tenantsRepository;
    
    @Autowired
    private TwoLevelCacheService cacheService;
    
    // 使用缓存优化查询
    @Cacheable(value = "tenant", key = "#id")
    public IamTenant getTenantById(String id) {
        return tenantsRepository.getById(id);
    }
    
    @Cacheable(value = "tenant", key = "'active'")
    public List<IamTenant> getActiveTenants() {
        TenantQueryDTO query = TenantQueryDTO.builder()
            .status(1)
            .build();
        return tenantsRepository.listWithCondition(query);
    }
    
    @CacheEvict(value = "tenant", allEntries = true)
    public boolean saveTenant(IamTenant tenant) {
        return tenantsRepository.save(tenant);
    }
}
```

## 🔍 查询调试

### 1. 启用 SQL 日志

```yaml
# application.yml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    log-prefix: "[SQL] "
```

### 2. 性能监控

```java
@Aspect
@Component
public class QueryPerformanceAspect {
    
    private static final Logger log = LoggerFactory.getLogger(QueryPerformanceAspect.class);
    
    @Around("@annotation(QueryMonitor)")
    public Object monitorQuery(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("查询方法: {}, 耗时: {}ms", methodName, duration);
            
            // 慢查询告警
            if (duration > 1000) {
                log.warn("检测到慢查询: {}ms, 方法: {}", duration, methodName);
            }
            
            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("查询异常: {}ms, 方法: {}, 错误: {}", duration, methodName, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
```

### 3. 查询分析

```java
@Component
public class QueryAnalyzer {
    
    public void analyzeQuery(String sql, Object[] parameters) {
        log.info("SQL: {}", sql);
        log.info("参数: {}", Arrays.toString(parameters));
        
        // 分析 SQL 执行计划
        analyzeExecutionPlan(sql);
        
        // 检查索引使用情况
        checkIndexUsage(sql);
    }
    
    private void analyzeExecutionPlan(String sql) {
        // 使用 EXPLAIN 分析执行计划
        log.info("执行计划分析: {}", sql);
    }
    
    private void checkIndexUsage(String sql) {
        // 检查索引使用情况
        log.info("索引使用检查: {}", sql);
    }
}
```

## 📊 查询最佳实践

### 1. 查询方式选择

```java
// ✅ 推荐：简单查询使用注解驱动
TenantQueryDTO query = TenantQueryDTO.byStatus(1);
List<IamTenant> tenants = tenantsRepository.listWithCondition(query);

// ✅ 推荐：复杂查询使用流式查询
List<IamTenant> tenants = tenantsRepository.list(
    tenantsRepository.query()
        .like(IamTenant::getCode, keyword)
        .eq(IamTenant::getStatus, status)
        .between(IamTenant::getCreateTime, start, end)
        .orderByDesc(IamTenant::getCreateTime)
);

// ✅ 推荐：多表关联使用原生 SQL
List<TenantWithCreatorDTO> tenants = tenantsRepository.getMapper().findTenantsWithCreator(status);
```

### 2. 性能优化原则

- **索引优先**: 为常用查询字段创建合适的索引
- **分页查询**: 避免全表扫描，使用分页查询
- **缓存策略**: 对热点数据使用缓存
- **SQL 优化**: 避免 SELECT *，使用具体的字段名
- **连接优化**: 使用 EXISTS 代替 IN，使用 JOIN 代替子查询

### 3. 查询安全

```java
// ✅ 安全：使用参数绑定
@Select("SELECT * FROM iam_tenant WHERE code = #{code}")
IamTenant findByCode(@Param("code") String code);

// ❌ 危险：字符串拼接
@Select("SELECT * FROM iam_tenant WHERE code = '" + code + "'")
IamTenant findByCode(@Param("code") String code);
```

---

**查询优化原则**: 简单、高效、安全、可维护 