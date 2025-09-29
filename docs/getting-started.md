# 快速开始指南

## 📖 概述

本指南将帮助您快速搭建和运行 SynapseMOM 制造运营管理平台，专注于 Synapse Framework 的最新特性。**重点推荐**：可以直接使用 `synapse-databases` 模块的 **无 ServiceImpl 架构**，3分钟内实现高级数据库操作！

## 🛠️ 环境准备

### 必需环境

| 组件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 17+ | 推荐使用 OpenJDK 17 或 Oracle JDK 17 |
| Maven | 3.8+ | 用于项目构建和依赖管理 |
| MySQL | 8.0+ | 主数据库，用于存储业务数据 |
| Redis | 6.0+ | 缓存和会话存储（可选，但推荐） |

### 可选环境

| 组件 | 版本要求 | 说明 |
|------|---------|------|
| Nacos | 2.0+ | 服务注册与配置中心 |
| Docker | 20.0+ | 容器化部署 |
| Git | 2.0+ | 版本控制 |

### 环境检查

```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version

# 检查 MySQL 版本
mysql --version

# 检查 Redis 版本
redis-server --version
```

## 🚀 快速部署

### 🎯 最速方案：使用 Synapse Framework

#### 方案A：直接使用 synapse-databases（推荐）

1. **📦 添加依赖**
```xml
<!-- 在你的项目 pom.xml 中添加 -->
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-databases</artifactId>
    <version>1.1.0</version>
</dependency>
```

2. **⚙️ 最少配置**
```yaml
# application.yml
synapse:
  datasource:
    primary: master1
    datasources:
      master1:
        type: MYSQL
        host: localhost
        port: 3306
        database: your_database
        username: root
        password: your_password
        pool-type: HIKARI
```

3. **🎯 定义 Repository（零代码）**
```java
@AutoRepository
public interface ICountryService extends BaseRepository<Country, CountryMapper> {
    // ✅ 自动获得所有 CRUD、分页、查询方法！
}

@Mapper
public interface CountryMapper extends BaseMapper<Country> {
    // ✅ MyBatis-Plus 自动提供基础 CRUD
}
```

4. **✨ 立即使用**
```java
@Service
public class CountryService {
    @Autowired private ICountryService countryRepo;
    
    // ✅ 基础操作
    public Country save(Country country) { return countryRepo.save(country); }
    
    // ✅ 唯一性验证（最新功能）
    public boolean isUnique(Country country) { 
        return !countryRepo.checkKeyUniqueness(country, "code"); 
    }
    
    // ✅ 增强查询
    public List<Country> findActive() {
        return countryRepo.enhancedQuery(Country.class)
            .eq(Country::getStatus, 1).orderByDesc(Country::getCreateTime).list();
    }
    
    // ✅ 多表查询
    public PageResult<CountryVO> pageWithRegion(PageDTO<Country> dto) {
        return countryRepo.enhancedQuery(Country.class)
            .leftJoin("region r", "c.region_id = r.id")
            .select("c.*", "r.name as region_name")
            .page(dto, CountryVO.class);
    }
}
```

5. **🚀 启动测试**
```bash
mvn spring-boot:run
curl http://localhost:8080/api/countries
```

---

### 方案B：完整平台部署

#### 1. 克隆项目

```bash
# 克隆项目到本地
git clone <repository-url>
cd SynapseMOM

# 查看项目结构
ls -la
```

#### 2. 数据库准备

#### 创建数据库

```sql
-- 连接到 MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE synapse_iam CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE synapse_metadata CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'synapse'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON synapse_iam.* TO 'synapse'@'localhost';
GRANT ALL PRIVILEGES ON synapse_metadata.* TO 'synapse'@'localhost';
FLUSH PRIVILEGES;
```

#### 初始化数据

```bash
# 进入 IAM 服务目录
cd foundation-module/iam-service/iam-core

# 运行数据库初始化脚本
mysql -u root -p synapse_iam < src/main/resources/schema.sql
mysql -u root -p synapse_iam < src/main/resources/data.sql
```

### 3. 配置修改

#### IAM 服务配置

编辑 `foundation-module/iam-service/iam-core/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    dynamic:
      primary: master1
      strict: false
      datasource:
        master1:
          type: MYSQL
          host: localhost
          port: 3306
          database: synapse_iam
          username: root  # 或您创建的用户
          password: your_password
          params:
            useUnicode: "true"
            characterEncoding: "utf8"
            useSSL: "false"
            serverTimezone: "Asia/Shanghai"
          hikari:
            minimumIdle: 5
            maximumPoolSize: 15
            idleTimeout: 30000
            maxLifetime: 1800000
            connectionTimeout: 30000
            connectionTestQuery: "SELECT 1"
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1
```

#### 网关服务配置

编辑 `infrastructure-module/gateway-service/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      routes:
        - id: iam-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/iam/**
          filters:
            - StripPrefix=1
        - id: metadata-service
          uri: http://localhost:8082
          predicates:
            - Path=/api/metadata/**
          filters:
            - StripPrefix=1
```

### 4. 启动服务

#### 启动 IAM 服务

```bash
# 进入 IAM 服务目录
cd foundation-module/iam-service/iam-core

# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

#### 启动元数据服务

```bash
# 新开终端，进入元数据服务目录
cd foundation-module/mdm-service/mdm-core

# 启动服务
mvn spring-boot:run
```

#### 启动网关服务

```bash
# 新开终端，进入网关服务目录
cd infrastructure-module/gateway-service

# 启动服务
mvn spring-boot:run
```

### 5. 验证部署

#### 检查服务状态

```bash
# 检查 IAM 服务
curl http://localhost:8081/actuator/health

# 检查元数据服务
curl http://localhost:8082/actuator/health

# 检查网关服务
curl http://localhost:8080/actuator/health
```

#### 测试 API 接口

```bash
# 测试用户列表接口
curl http://localhost:8080/api/iam/users

# 测试租户列表接口
curl http://localhost:8080/api/iam/tenants

# 测试角色列表接口
curl http://localhost:8080/api/iam/roles
```

## 🎯 功能演示

### 🔥 Synapse Framework 最新特性演示

#### 1. checkKeyUniqueness 唯一性验证

```java
@Service
public class UniqueValidationDemo {
    
    @Autowired private ICountryService countryRepo;
    
    // ✅ 单字段唯一性验证
    public boolean validateCountryCode(Country country) {
        return !countryRepo.checkKeyUniqueness(country, "code");
    }
    
    // ✅ 多字段联合唯一性验证
    public boolean validateCountryKeys(Country country) {
        return !countryRepo.checkKeyUniqueness(country, "code", "name");
    }
    
    // ✅ 使用 BaseDTO 进行验证
    public boolean validateCountryDTO(CountryDTO countryDto) {
        return !countryRepo.checkKeyUniqueness(countryDto, "code");
    }
    
    // ✅ 批量验证场景
    public Map<String, Boolean> validateBatchCountries(List<Country> countries) {
        return countries.stream()
            .collect(Collectors.toMap(
                Country::getCode,
                country -> !countryRepo.checkKeyUniqueness(country, "code")
            ));
    }
}
```

#### 2. EnhancedQueryBuilder 高性能查询

```java
@Service
public class EnhancedQueryDemo {
    
    @Autowired private ICountryService countryRepo;
    
    // ✅ 复杂条件查询
    public List<Country> findCountriesAdvanced(String keyword, Integer status) {
        return countryRepo.enhancedQuery(Country.class)
            .like(StringUtils.hasText(keyword), Country::getName, keyword)
            .or()
            .like(StringUtils.hasText(keyword), Country::getCode, keyword)
            .eq(Objects.nonNull(status), Country::getStatus, status)
            .between(Country::getCreateTime, getStartTime(), getEndTime())
            .orderByDesc(Country::getCreateTime)
            .monitorPerformance()  // 🚀 启用性能监控
            .list();
    }
    
    // ✅ 聚合统计查询
    public CountryStatisticsVO getStatistics() {
        return countryRepo.enhancedQuery(Country.class)
            .select(
                count().alias("total_count"),
                sum(Country::getPopulation).alias("total_population"),
                avg(Country::getArea).alias("avg_area"),
                max(Country::getCreateTime).alias("latest_create")
            )
            .eq(Country::getStatus, 1)
            .single(CountryStatisticsVO.class);
    }
    
    // ✅ 多表关联 + 分页
    public PageResult<CountryWithRegionVO> getCountriesWithRegion(int current, int size) {
        PageDTO<Country> pageDTO = PageDTO.<Country>builder()
            .current(current).size(size).build();
            
        return countryRepo.enhancedQuery(Country.class)
            .alias("c")
            .leftJoin("region r", "c.region_id = r.id")
            .leftJoin("continent ct", "r.continent_id = ct.id")
            .select(
                "c.id", "c.code", "c.name as country_name",
                "r.name as region_name",
                "ct.name as continent_name"
            )
            .eq("c.status", 1)
            .orderByDesc("c.create_time")
            .page(pageDTO, CountryWithRegionVO.class);
    }
    
    // ✅ 异步查询（实验性功能）
    public CompletableFuture<List<Country>> getCountriesAsync() {
        return countryRepo.enhancedQuery(Country.class)
            .eq(Country::getStatus, 1)
            .orderByDesc(Country::getCreateTime)
            .listAsync();
    }
}
```

#### 3. 智能 SqlMethodInterceptor 功能

```java
@Service
public class SqlInterceptorDemo {
    
    @Autowired private ICountryService countryRepo;
    
    // ✅ 自动处理复杂参数类型
    public boolean smartValidation(Object entity, String... fields) {
        // SqlMethodInterceptor 会自动处理类型转换和参数解析
        return !countryRepo.checkKeyUniqueness(entity, fields);
    }
    
    // ✅ 支持 JSON 字符串自动解析
    public Country parseAndValidate(String jsonCountry) {
        Country country = JSON.parseObject(jsonCountry, Country.class);
        if (countryRepo.checkKeyUniqueness(country, "code")) {
            throw new BusinessException("国家编码已存在");
        }
        return country;
    }
    
    // ✅ 批量操作验证
    public List<Tenant> batchValidate(List<Tenant> tenants) {
        return tenants.stream()
            .filter(tenant -> !countryRepo.checkKeyUniqueness(tenant, "code"))
            .collect(Collectors.toList());
    }
}
```

### 📚 传统功能演示

#### 1. 用户管理

#### 创建用户

```bash
curl -X POST http://localhost:8080/api/iam/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "email": "test@example.com",
    "realName": "测试用户",
    "status": 1
  }'
```

#### 查询用户

```bash
# 查询所有用户
curl http://localhost:8080/api/iam/users

# 查询指定用户
curl http://localhost:8080/api/iam/users/1

# 条件查询用户
curl "http://localhost:8080/api/iam/users?username=testuser&status=1"
```

#### 更新用户

```bash
curl -X PUT http://localhost:8080/api/iam/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "realName": "更新后的用户名",
    "email": "updated@example.com"
  }'
```

#### 删除用户

```bash
curl -X DELETE http://localhost:8080/api/iam/users/1
```

### 2. 租户管理

#### 创建租户

```bash
curl -X POST http://localhost:8080/api/iam/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "code": "T001",
    "name": "测试租户",
    "description": "这是一个测试租户",
    "status": 1
  }'
```

#### 查询租户

```bash
# 查询所有租户
curl http://localhost:8080/api/iam/tenants

# 查询指定租户
curl http://localhost:8080/api/iam/tenants/1
```

### 3. 角色权限管理

#### 创建角色

```bash
curl -X POST http://localhost:8080/api/iam/roles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试角色",
    "code": "TEST_ROLE",
    "description": "这是一个测试角色",
    "status": 1
  }'
```

#### 分配权限

```bash
curl -X POST http://localhost:8080/api/iam/roles/1/permissions \
  -H "Content-Type: application/json" \
  -d '{
    "permissionIds": [1, 2, 3]
  }'
```

## 🔧 开发环境配置

### IDE 配置

#### IntelliJ IDEA

1. **导入项目**
   - 打开 IDEA
   - 选择 "Open" 或 "Import Project"
   - 选择项目根目录
   - 选择 "Import project from external model" -> "Maven"

2. **配置 JDK**
   - File -> Project Structure -> Project
   - 设置 Project SDK 为 JDK 17
   - 设置 Project language level 为 17

3. **配置 Maven**
   - File -> Settings -> Build Tools -> Maven
   - 确保 Maven home path 正确
   - 设置 JDK for importer 为 JDK 17

#### Eclipse

1. **导入项目**
   - File -> Import -> Maven -> Existing Maven Projects
   - 选择项目根目录
   - 选择所有模块

2. **配置 JDK**
   - Window -> Preferences -> Java -> Installed JREs
   - 添加 JDK 17
   - 设置为默认 JRE

### 调试配置

#### 远程调试

```bash
# 启动服务时开启远程调试
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

#### 日志配置

编辑 `logback.xml` 或 `application.yml`：

```yaml
logging:
  level:
    root: INFO
    com.indigo: DEBUG
    com.indigo.security: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 🚨 常见问题

### 1. 启动失败

#### 端口占用

```bash
# 查看端口占用
lsof -i :8080
lsof -i :8081
lsof -i :8082

# 杀死占用进程
kill -9 <PID>
```

#### 数据库连接失败

```bash
# 检查 MySQL 服务状态
sudo systemctl status mysql

# 启动 MySQL 服务
sudo systemctl start mysql

# 检查数据库连接
mysql -u root -p -h localhost -P 3306
```

#### Redis 连接失败

```bash
# 检查 Redis 服务状态
sudo systemctl status redis

# 启动 Redis 服务
sudo systemctl start redis

# 测试 Redis 连接
redis-cli ping
```

### 2. 编译错误

#### 依赖下载失败

```bash
# 清理 Maven 缓存
mvn clean

# 强制更新依赖
mvn dependency:purge-local-repository

# 重新编译
mvn clean compile
```

#### 版本冲突

```bash
# 查看依赖树
mvn dependency:tree

# 排除冲突依赖
# 在 pom.xml 中添加 <exclusions> 标签
```

### 3. 运行时错误

#### 内存不足

```bash
# 增加 JVM 内存
export MAVEN_OPTS="-Xmx2g -Xms1g"
mvn spring-boot:run
```

#### 权限问题

```bash
# 检查文件权限
ls -la

# 修改权限
chmod +x mvnw
chmod +x mvnw.cmd
```

## 📚 下一步学习

### 1. 深入学习

- [架构设计](./architecture.md) - 了解平台的整体架构
- [扩展点指南](./extension-points.md) - 学习如何扩展框架功能
- [高级查询](./advanced-query.md) - 掌握复杂查询技巧

### 2. 实践项目

- [示例代码](./examples/) - 查看完整的使用示例
- [基础 CRUD 示例](./examples/basic-crud/) - 从基础开始
- [高级查询示例](./examples/advanced-query/) - 进阶学习

### 3. 部署运维

- [故障排除](./troubleshooting.md) - 解决常见问题
- 监控配置 - 配置应用监控
- 性能优化 - 提升系统性能

### 4. 社区支持

- [GitHub Issues](https://github.com/your-repo/issues) - 报告问题
- [GitHub Discussions](https://github.com/your-repo/discussions) - 参与讨论
- [邮件支持](mailto:support@indigo.com) - 获取技术支持

## 🎉 恭喜！

您已经成功完成了 SynapseMOM 平台的快速部署和基础功能体验。现在您可以：

1. **继续探索** - 尝试更多功能特性
2. **开始开发** - 基于平台开发您的业务应用
3. **参与贡献** - 为平台发展贡献力量
4. **分享经验** - 与其他开发者交流学习

祝您使用愉快！🚀 