# Gateway语言环境处理方案

## 📖 概述

为Spring Gateway服务添加了完整的语言环境处理功能，包括：

1. **LocaleFilter** - 语言环境过滤器
2. **GatewayConfig.LocaleConfig** - 语言环境配置
3. **TestController** - 语言环境测试接口

## 🎯 核心功能

### 1. LocaleFilter过滤器
- **多层级获取**：URL参数 > 自定义请求头 > Accept-Language头 > 默认语言环境
- **配置驱动**：支持通过配置文件自定义行为
- **异常安全**：完善的异常处理机制
- **请求头传递**：自动将语言环境传递给下游服务

### 2. GatewayConfig.LocaleConfig配置
- **开关控制**：可启用/禁用语言环境处理
- **默认语言环境**：可配置默认语言环境
- **支持列表**：可配置支持的语言环境列表
- **参数配置**：可配置URL参数名和请求头名

### 3. TestController测试接口
- **配置查看**：查看当前语言环境配置
- **功能测试**：测试语言环境检测功能
- **调试支持**：便于调试和验证

## 🚀 使用方式

### 1. 客户端设置语言环境

```http
# 方式1：URL参数（最高优先级）
GET /api/users?lang=zh_CN

# 方式2：自定义请求头
X-Locale: zh_CN

# 方式3：标准Accept-Language头
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
```

### 2. Gateway处理流程

```
客户端请求 → LocaleFilter → 检测语言环境 → 添加请求头 → 下游服务
     ↓              ↓           ↓           ↓         ↓
设置语言环境 → 解析优先级 → 验证支持 → X-Locale → 业务处理
```

### 3. 下游服务获取语言环境

```java
// 下游服务可以通过请求头获取语言环境
@RequestHeader("X-Locale") String locale
@RequestHeader("X-Language") String language

// 或者使用LocaleContextHolder
Locale currentLocale = LocaleContextHolder.getCurrentLocale();
```

## 🔧 配置说明

### 1. application.yml配置

```yaml
synapse:
  gateway:
    locale:
      enabled: true                    # 是否启用语言环境处理
      default-locale: zh_CN           # 默认语言环境
      supported-locales:              # 支持的语言环境列表
        - zh_CN                       # 简体中文
        - zh_TW                       # 繁体中文
        - en                          # 英文
        - en_US                       # 美式英文
      param-name: lang                # URL参数名
      log-locale: false               # 是否记录语言环境日志
      header-name: X-Locale           # 语言环境请求头名称
```

### 2. 配置类结构

```java
@Data
public static class LocaleConfig {
    private boolean enabled = true;                    // 是否启用
    private String defaultLocale = "zh_CN";           // 默认语言环境
    private List<String> supportedLocales = List.of(  // 支持的语言环境
        "zh_CN", "zh_TW", "en", "en_US"
    );
    private String paramName = "lang";                // URL参数名
    private boolean logLocale = false;                // 是否记录日志
    private String headerName = "X-Locale";           // 请求头名称
}
```

## 📊 语言环境获取优先级

```
1. URL参数 (最高优先级)
   GET /api/users?lang=zh_CN
   ↓
2. 自定义请求头
   X-Locale: zh_CN
   ↓
3. Accept-Language请求头
   Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
   ↓
4. 默认语言环境 (最低优先级)
   zh_CN
```

## 🌐 测试接口

### 1. 查看配置

```http
GET /test/whitelist
```

响应示例：
```json
{
  "locale": {
    "enabled": true,
    "defaultLocale": "zh_CN",
    "supportedLocales": ["zh_CN", "zh_TW", "en", "en_US"],
    "paramName": "lang",
    "logLocale": false,
    "headerName": "X-Locale"
  }
}
```

### 2. 测试语言环境检测

```http
# 测试URL参数
GET /test/locale?lang=en_US

# 测试请求头
GET /test/locale
X-Locale: zh_CN

# 测试Accept-Language
GET /test/locale
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
```

响应示例：
```json
{
  "config": {
    "enabled": true,
    "defaultLocale": "zh_CN",
    "supportedLocales": ["zh_CN", "zh_TW", "en", "en_US"],
    "paramName": "lang",
    "headerName": "X-Locale"
  },
  "request": {
    "lang": "en_US",
    "xLocale": null,
    "acceptLanguage": null
  },
  "detectedLocale": "en_US"
}
```

## 🔒 安全考虑

### 1. 语言环境验证
- **支持检查**：只允许配置中支持的语言环境
- **格式验证**：验证语言环境格式是否正确
- **异常处理**：解析失败时使用默认语言环境

### 2. 性能优化
- **配置缓存**：配置信息在启动时加载
- **异常安全**：即使出错也不影响请求处理
- **日志控制**：可配置是否记录详细日志

## 📝 最佳实践

### 1. 配置建议
- **默认语言环境**：设置为主要用户群体的语言
- **支持列表**：根据实际需求配置支持的语言环境
- **日志控制**：生产环境建议关闭详细日志

### 2. 使用建议
- **URL参数**：用于临时语言切换
- **请求头**：用于客户端持久化语言偏好
- **Accept-Language**：用于浏览器自动语言检测

### 3. 错误处理
- **优雅降级**：语言环境检测失败时使用默认语言环境
- **日志记录**：记录语言环境处理过程中的异常
- **监控告警**：监控语言环境处理异常

## 🐛 常见问题

### 1. 语言环境不生效
**问题**：设置的语言环境没有生效
**解决方案**：
- 检查配置是否启用：`synapse.gateway.locale.enabled=true`
- 检查语言环境是否在支持列表中
- 检查请求头格式是否正确

### 2. 下游服务获取不到语言环境
**问题**：下游服务无法获取语言环境
**解决方案**：
- 检查Gateway是否正确添加了X-Locale请求头
- 检查下游服务是否正确读取请求头
- 使用测试接口验证Gateway处理结果

### 3. 性能问题
**问题**：语言环境处理影响性能
**解决方案**：
- 关闭详细日志：`synapse.gateway.locale.log-locale=false`
- 减少支持的语言环境列表
- 检查过滤器执行顺序

## 📚 相关文档

- [Synapse Core 语言环境处理](Synapse-Framework/docs/modules/synapse-core/LOCALE_HANDLING.md)
- [Spring Cloud Gateway 过滤器](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#gatewayfilter-factories)
- [Spring Boot 配置属性](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties)

---

*最后更新时间：2025年01月27日*
