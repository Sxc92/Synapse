# 示例代码

## 📁 目录结构

```
examples/
├── README.md                    # 本文件
├── basic-crud/                  # 基础 CRUD 示例
├── advanced-query/              # 高级查询示例
├── multi-table/                 # 多表关联示例
├── performance/                 # 性能优化示例
├── extension/                   # 扩展点示例
└── integration/                 # 集成示例
```

## 🎯 示例说明

本目录包含了 Synapse Framework 的各种使用示例，帮助开发者快速理解和掌握框架的使用方法。

## 📚 示例分类

### 1. 基础 CRUD 示例 (`basic-crud/`)

包含最基础的增删改查操作示例：

- 实体类定义
- Repository 接口
- Service 层实现
- Controller 层实现
- 基础查询操作

### 2. 高级查询示例 (`advanced-query/`)

展示复杂查询场景：

- 条件查询构建
- 分页查询
- 排序查询
- 聚合查询
- 动态查询

### 3. 多表关联示例 (`multi-table/`)

演示多表关联查询：

- 一对一关联
- 一对多关联
- 多对多关联
- 复杂关联查询
- 关联查询优化

### 4. 性能优化示例 (`performance/`)

展示性能优化技巧：

- 索引优化
- 查询优化
- 缓存使用
- 分页优化
- 批量操作

### 5. 扩展点示例 (`extension/`)

演示框架扩展能力：

- 自定义查询条件
- 结果处理器
- 查询拦截器
- 数据源扩展
- 缓存扩展

### 6. 集成示例 (`integration/`)

展示与其他框架的集成：

- Spring Boot 集成
- Spring Cloud 集成
- 缓存框架集成
- 消息队列集成
- 监控框架集成

## 🚀 快速开始

### 1. 查看基础示例

```bash
# 查看基础 CRUD 示例
cd examples/basic-crud
```

### 2. 运行示例

```bash
# 运行示例项目
mvn spring-boot:run
```

### 3. 测试接口

```bash
# 测试基础 CRUD 接口
curl -X GET http://localhost:8080/api/tenants/1
curl -X POST http://localhost:8080/api/tenants -H "Content-Type: application/json" -d '{"code":"T001","name":"测试租户"}'
```

## 📖 学习路径

### 初学者路径

1. **基础 CRUD** → 了解框架基本用法
2. **高级查询** → 掌握复杂查询技巧
3. **多表关联** → 学习关联查询方法
4. **性能优化** → 提升查询性能
5. **扩展点** → 了解框架扩展能力

### 进阶路径

1. **扩展点示例** → 深入理解框架扩展机制
2. **集成示例** → 学习与其他框架集成
3. **性能优化** → 掌握性能调优技巧
4. **多表关联** → 优化复杂查询场景

## 🔧 示例配置

### 数据库配置

```yaml
# application.yml
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

### 日志配置

```yaml
logging:
  level:
    com.indigo.synapse: DEBUG
    com.indigo.examples: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 📝 示例说明

每个示例都包含：

- **完整代码**: 可直接运行的完整示例
- **详细注释**: 代码中的详细说明
- **使用说明**: README 文件说明使用方法
- **测试用例**: 单元测试和集成测试
- **性能对比**: 不同实现方式的性能对比

## 🤝 贡献示例

欢迎贡献新的示例代码：

1. 在对应目录下创建新的示例项目
2. 提供完整的代码和文档
3. 包含测试用例和性能说明
4. 提交 Pull Request

## 📞 获取帮助

如果在运行示例时遇到问题：

1. 查看示例的 README 文件
2. 检查配置是否正确
3. 查看日志输出
4. 提交 Issue 到项目仓库

---

**示例代码原则**: 完整、清晰、可运行、有说明 