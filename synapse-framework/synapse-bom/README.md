# Synapse BOM 依赖管理

## 📖 概述

Synapse BOM (Bill of Materials) 是 SynapseMOM 平台的依赖管理模块，统一管理所有 Synapse 框架模块的版本依赖。该模块确保整个平台使用一致的依赖版本，避免版本冲突和兼容性问题。

## ✨ 特性

- 📦 **统一版本管理**: 集中管理所有 Synapse 模块的版本
- 🔄 **依赖传递**: 自动传递版本信息到子模块
- 🛡️ **版本兼容性**: 确保各模块间的版本兼容性
- 🚀 **简化配置**: 简化子模块的依赖配置
- 📊 **依赖分析**: 提供依赖关系分析和报告
- 🔧 **灵活配置**: 支持不同环境的依赖配置

## 🏗️ 模块结构

```
synapse-bom/
├── pom.xml                    # BOM 主配置文件
├── dependency-management/     # 依赖管理配置
│   ├── synapse-dependencies.xml    # Synapse 模块依赖
│   ├── spring-dependencies.xml     # Spring 相关依赖
│   ├── database-dependencies.xml   # 数据库相关依赖
│   ├── cache-dependencies.xml      # 缓存相关依赖
│   ├── security-dependencies.xml   # 安全相关依赖
│   └── test-dependencies.xml       # 测试相关依赖
└── README.md                 # 本文档
```

## 🚀 快速开始

### 1. 在父项目中引入 BOM

在父项目的 `pom.xml` 中添加：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.indigo</groupId>
            <artifactId>synapse-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 在子模块中使用依赖

在子模块的 `pom.xml` 中直接使用依赖，无需指定版本：

```xml
<dependencies>
    <!-- Synapse 模块依赖 -->
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-core</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-cache</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-security</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-events</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-databases</artifactId>
    </dependency>
    
    <!-- Spring Boot 依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- 数据库依赖 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    
    <!-- 缓存依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- 安全依赖 -->
    <dependency>
        <groupId>cn.dev33</groupId>
        <artifactId>sa-token-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 📋 依赖管理

### Synapse 模块依赖

| 模块 | GroupId | ArtifactId | 版本 | 说明 |
|------|---------|------------|------|------|
| 核心框架 | com.indigo | synapse-core | 1.0.0 | 核心工具类和基础功能 |
| 缓存框架 | com.indigo | synapse-cache | 1.0.0 | 多级缓存和分布式锁 |
| 安全框架 | com.indigo | synapse-security | 1.0.0 | 认证授权和权限管理 |
| 事件框架 | com.indigo | synapse-events | 1.0.0 | 事件驱动和消息队列 |
| 数据库框架 | com.indigo | synapse-databases | 1.0.0 | 动态数据源和负载均衡 |

### Spring 相关依赖

| 依赖 | GroupId | ArtifactId | 版本 | 说明 |
|------|---------|------------|------|------|
| Spring Boot | org.springframework.boot | spring-boot-starter-web | 2.7.0 | Web 应用启动器 |
| Spring Boot | org.springframework.boot | spring-boot-starter-data-jpa | 2.7.0 | JPA 数据访问 |
| Spring Boot | org.springframework.boot | spring-boot-starter-data-redis | 2.7.0 | Redis 数据访问 |
| Spring Boot | org.springframework.boot | spring-boot-starter-validation | 2.7.0 | 数据验证 |
| Spring Boot | org.springframework.boot | spring-boot-starter-actuator | 2.7.0 | 监控管理 |
| Spring Boot | org.springframework.boot | spring-boot-starter-test | 2.7.0 | 测试启动器 |

### 数据库相关依赖

| 依赖 | GroupId | ArtifactId | 版本 | 说明 |
|------|---------|------------|------|------|
| MySQL | mysql | mysql-connector-java | 8.0.28 | MySQL 驱动 |
| PostgreSQL | org.postgresql | postgresql | 42.3.3 | PostgreSQL 驱动 |
| Oracle | com.oracle.database.jdbc | ojdbc8 | 21.5.0.0 | Oracle 驱动 |
| MyBatis Plus | com.baomidou | mybatis-plus-boot-starter | 3.5.1 | MyBatis Plus |
| Druid | com.alibaba | druid-spring-boot-starter | 1.2.8 | 数据库连接池 |

### 缓存相关依赖

| 依赖 | GroupId | ArtifactId | 版本 | 说明 |
|------|---------|------------|------|------|
| Redis | org.springframework.boot | spring-boot-starter-data-redis | 2.7.0 | Redis 支持 |
| Caffeine | com.github.ben-manes.caffeine | caffeine | 3.1.0 | 本地缓存 |
| Redisson | org.redisson | redisson-spring-boot-starter | 3.17.0 | Redis 客户端 |

### 安全相关依赖

| 依赖 | GroupId | ArtifactId | 版本 | 说明 |
|------|---------|------------|------|------|
| Sa-Token | cn.dev33 | sa-token-spring-boot-starter | 1.32.0 | 认证授权框架 |
| Sa-Token | cn.dev33 | sa-token-redis-jackson | 1.32.0 | Redis 存储 |
| JWT | io.jsonwebtoken | jjwt-api | 0.11.5 | JWT 支持 |
| JWT | io.jsonwebtoken | jjwt-impl | 0.11.5 | JWT 实现 |
| JWT | io.jsonwebtoken | jjwt-jackson | 0.11.5 | JWT Jackson |

### 消息队列依赖

| 依赖 | GroupId | ArtifactId | 版本 | 说明 |
|------|---------|------------|------|------|
| RocketMQ | org.apache.rocketmq | rocketmq-spring-boot-starter | 2.2.2 | RocketMQ 支持 |
| RocketMQ | org.apache.rocketmq | rocketmq-client | 4.9.3 | RocketMQ 客户端 |

### 工具类依赖

| 依赖 | GroupId | ArtifactId | 版本 | 说明 |
|------|---------|------------|------|------|
| Lombok | org.projectlombok | lombok | 1.18.24 | 代码生成工具 |
| Apache Commons | org.apache.commons | commons-lang3 | 3.12.0 | 通用工具类 |
| Apache Commons | org.apache.commons | commons-collections4 | 4.4 | 集合工具类 |
| Guava | com.google.guava | guava | 31.1-jre | Google 工具类 |
| Hutool | cn.hutool | hutool-all | 5.8.0 | 国产工具类 |

### 测试相关依赖

| 依赖 | GroupId | ArtifactId | 版本 | 说明 |
|------|---------|------------|------|------|
| JUnit | org.junit.jupiter | junit-jupiter | 5.8.2 | JUnit 5 |
| Mockito | org.mockito | mockito-core | 4.5.1 | Mock 框架 |
| Mockito | org.mockito | mockito-junit-jupiter | 4.5.1 | Mock JUnit 5 |
| Testcontainers | org.testcontainers | testcontainers | 1.17.2 | 容器测试 |
| Testcontainers | org.testcontainers | junit-jupiter | 1.17.2 | Testcontainers JUnit |

## ⚙️ 配置选项

### 版本管理配置

```xml
<properties>
    <!-- Java 版本 -->
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    
    <!-- Spring Boot 版本 -->
    <spring-boot.version>2.7.0</spring-boot.version>
    
    <!-- Synapse 版本 -->
    <synapse.version>1.0.0</synapse.version>
    
    <!-- 编码设置 -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

### 依赖管理配置

```xml
<dependencyManagement>
    <dependencies>
        <!-- Spring Boot 依赖管理 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        
        <!-- Synapse BOM -->
        <dependency>
            <groupId>com.indigo</groupId>
            <artifactId>synapse-bom</artifactId>
            <version>${synapse.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 📝 使用示例

### 完整的项目配置示例

#### 父项目 pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.indigo</groupId>
    <artifactId>synapse-mom-platform</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <name>SynapseMOM Platform</name>
    <description>SynapseMOM 制造运营管理平台</description>
    
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        
        <spring-boot.version>2.7.0</spring-boot.version>
        <synapse.version>1.0.0</synapse.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot 依赖管理 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            
            <!-- Synapse BOM -->
            <dependency>
                <groupId>com.indigo</groupId>
                <artifactId>synapse-bom</artifactId>
                <version>${synapse.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <modules>
        <module>business-module</module>
        <module>foundation-module</module>
        <module>infrastructure-module</module>
        <module>synapse-framework</module>
    </modules>
</project>
```

#### 子模块 pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.indigo</groupId>
        <artifactId>synapse-mom-platform</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>user-service</artifactId>
    <name>User Service</name>
    <description>用户管理服务</description>
    
    <dependencies>
        <!-- Synapse 框架依赖 -->
        <dependency>
            <groupId>com.indigo</groupId>
            <artifactId>synapse-core</artifactId>
        </dependency>
        
        <dependency>
            <groupId>com.indigo</groupId>
            <artifactId>synapse-cache</artifactId>
        </dependency>
        
        <dependency>
            <groupId>com.indigo</groupId>
            <artifactId>synapse-security</artifactId>
        </dependency>
        
        <dependency>
            <groupId>com.indigo</groupId>
            <artifactId>synapse-databases</artifactId>
        </dependency>
        
        <!-- Spring Boot 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- 数据库依赖 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>
        
        <!-- 工具类依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- 测试依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 多环境配置示例

#### 开发环境

```xml
<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
            <synapse.cache.enabled>true</synapse.cache.enabled>
            <synapse.security.enabled>true</synapse.security.enabled>
        </properties>
    </profile>
</profiles>
```

#### 生产环境

```xml
<profiles>
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
            <synapse.cache.enabled>true</synapse.cache.enabled>
            <synapse.security.enabled>true</synapse.security.enabled>
            <synapse.monitoring.enabled>true</synapse.monitoring.enabled>
        </properties>
    </profile>
</profiles>
```

## 🔧 版本升级

### 升级步骤

1. **更新 BOM 版本**
   ```xml
   <dependency>
       <groupId>com.indigo</groupId>
       <artifactId>synapse-bom</artifactId>
       <version>1.1.0</version>
       <type>pom</type>
       <scope>import</scope>
   </dependency>
   ```

2. **检查兼容性**
   - 查看版本变更日志
   - 运行测试套件
   - 检查 API 变更

3. **更新依赖**
   - 清理并重新编译项目
   - 解决版本冲突
   - 更新配置

### 版本兼容性

| Synapse 版本 | Spring Boot 版本 | Java 版本 | 说明 |
|-------------|-----------------|-----------|------|
| 1.0.0 | 2.7.x | 17+ | 初始版本 |
| 1.1.0 | 2.7.x | 17+ | 功能增强 |
| 2.0.0 | 3.0.x | 17+ | 重大升级 |

## 📊 依赖分析

### 生成依赖报告

```bash
# 生成依赖树
mvn dependency:tree

# 生成依赖分析报告
mvn dependency:analyze

# 检查依赖冲突
mvn dependency:analyze-duplicate
```

### 依赖冲突解决

```xml
<dependency>
    <groupId>com.conflicting</groupId>
    <artifactId>library</artifactId>
    <version>1.0.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.conflicting</groupId>
            <artifactId>dependency</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 🧪 测试

### BOM 测试

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.3.0</version>
    <executions>
        <execution>
            <id>analyze</id>
            <goals>
                <goal>analyze</goal>
            </goals>
            <configuration>
                <failOnWarning>false</failOnWarning>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 📚 相关文档

- [Synapse Core](./synapse-core/README.md) - 核心框架
- [Synapse Cache](./synapse-cache/README.md) - 缓存框架
- [Synapse Security](./synapse-security/README.md) - 安全框架
- [Synapse Events](./synapse-events/README.md) - 事件驱动框架
- [Synapse Databases](./synapse-databases/README.md) - 数据库框架

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个框架。

## 📄 许可证

本项目采用 MIT 许可证。

---

**最后更新：** 2025-07-20  
**版本：** 1.0.0  
**维护者：** 史偕成 