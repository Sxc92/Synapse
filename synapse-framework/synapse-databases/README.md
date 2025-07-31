# Synapse Databases 数据库框架

## 📖 概述

Synapse Databases 是 SynapseMOM 平台的数据库框架，提供了动态数据源、多数据库支持、负载均衡、健康检查等功能。

## 📚 详细文档

详细的文档请查看：[docs/databases/README.md](../../../docs/databases/README.md)

## 🚀 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>com.indigo</groupId>
    <artifactId>synapse-databases</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 核心功能

- **@AutoRepository**: 自动生成Repository实现，无需手写ServiceImpl
- **SQL注解框架**: 通过注解直接定义SQL，支持复杂查询
- **统一分页查询**: 基于PageDTO的统一分页查询
- **动态数据源**: 支持多数据源和读写分离
- **负载均衡**: 提供多种负载均衡策略
- **健康检查**: 自动检测数据源健康状态

## 📝 更新日志

最新更新请查看：[docs/databases/README.md#更新日志](../../../docs/databases/README.md#更新日志) 