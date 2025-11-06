# Cursor Rules 更新日志

## 2025-01-XX 更新

### 新增规则

#### 1. DTO & VO 独立模块规范
- **要求**：DTO & VO 必须放在 `*-sdk` 模块中
- **项目结构示例**：
  ```
  foundation-module/
  ├── iam-service/              # IAM 服务模块
  │   ├── iam-sdk/              # IAM SDK 模块（存放 DTO & VO）
  │   │   ├── dto/
  │   │   └── vo/
  │   └── iam-server/           # IAM 核心业务模块
  │       ├── controller/
  │       ├── service/
  │       ├── repository/
  │       └── Application.java
  └── i18n-service/             # I18N 服务模块
      ├── i18n-sdk/
      └── i18n-server/
  ```
- **依赖方式**：`*-server` 模块通过 Maven 依赖对应的 `*-sdk` 模块
  ```xml
  <!-- iam-server/pom.xml -->
  <dependency>
      <groupId>com.indigo</groupId>
      <artifactId>iam-sdk</artifactId>
      <version>${project.version}</version>
  </dependency>
  ```

#### 2. 异常处理规范
- **要求**：使用 `Ex.java` 工具类处理异常
- **推荐用法**：
  ```java
  // ✅ 使用 Ex.throwEx()
  if (userId == null) {
      Ex.throwEx(StandardErrorCode.USER_ID_REQUIRED);
  }
  
  // ✅ 带原因的异常
  Ex.throwEx(StandardErrorCode.DATABASE_ERROR, e);
  
  // ✅ 自定义消息
  Ex.throwEx(StandardErrorCode.INVALID_AMOUNT, "转账金额必须大于0");
  ```
- **避免**：
  ```java
  // ❌ 不要直接使用 throw new SynapseException()
  throw new SynapseException(StandardErrorCode.USER_NOT_FOUND);
  ```

### 更新的文件

1. **`synapse-framework-practices.mdc`**
   - 更新了统一异常处理部分，添加 Ex 工具类使用示例
   - 更新了 VO 设计部分，添加 DTO & VO 存放规范
   - 更新了代码检查清单

2. **`java-jdk17-best-practices.mdc`**
   - 更新了异常处理部分，添加 Ex 工具类使用示例
   - 更新了代码检查清单

### 检查清单更新

#### 必须遵守（新增）
- ✅ 使用 Ex 工具类处理异常（优先使用 Ex.throwEx()）
- ✅ DTO & VO 放在 `*-sdk` 模块中（如 `iam-sdk`）
- ✅ 业务代码放在 `*-server` 模块中（如 `iam-server`）

#### 避免（新增）
- ❌ 直接使用 `throw new SynapseException()`（应使用 Ex 工具类）
- ❌ 将 DTO/VO 放在 `*-server` 模块中（应放在对应的 `*-sdk` 模块）
- ❌ 将业务代码（controller、service、repository）放在 `*-sdk` 模块中

## 使用方法

这些规则会自动应用到项目中，Cursor AI 会：
- 在编写代码时自动提醒使用 Ex 工具类
- 提醒将 DTO/VO 放在独立的 SDK 模块中
- 提供正确的代码示例和模式
- 在代码审查中检查这些规范

## 相关文档

- [Ex.java](Synapse-Framework/synapse-core/src/main/java/com/indigo/core/exception/Ex.java) - 异常工具类
- [Project Structure](Synapse-Framework/README.md) - 项目结构文档

