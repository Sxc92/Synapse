# IAM 服务数据库设计

## 数据库概述

### 设计原则
1. **数据完整性**：确保数据的准确性和一致性
2. **安全性**：敏感数据加密存储
3. **性能优化**：合理的索引设计
4. **可扩展性**：支持水平扩展
5. **多租户**：租户数据隔离

### 命名规范
- 表名：小写字母，下划线分隔，模块前缀
- 字段名：小写字母，下划线分隔
- 主键：id
- 外键：关联表名_id
- 创建时间：created_at
- 更新时间：updated_at
- 创建人：created_by
- 更新人：updated_by
- 删除标记：is_deleted
- 版本号：version

## 表结构设计

### 1. 用户表 (iam_user)
```sql
CREATE TABLE iam_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username        VARCHAR(50)     NOT NULL COMMENT '用户名',
    password        VARCHAR(100)    NOT NULL COMMENT '密码',
    nickname        VARCHAR(50)     COMMENT '昵称',
    real_name       VARCHAR(50)     COMMENT '真实姓名',
    email           VARCHAR(100)    COMMENT '邮箱',
    mobile          VARCHAR(20)     COMMENT '手机号',
    avatar          VARCHAR(255)    COMMENT '头像URL',
    gender          TINYINT        DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    status          TINYINT        DEFAULT 1 COMMENT '状态：0-禁用，1-启用，2-锁定',
    tenant_id       BIGINT         NOT NULL COMMENT '租户ID',
    dept_id         BIGINT         COMMENT '部门ID',
    last_login_ip   VARCHAR(50)     COMMENT '最后登录IP',
    last_login_time DATETIME        COMMENT '最后登录时间',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT         COMMENT '创建人ID',
    updated_by      BIGINT         COMMENT '更新人ID',
    is_deleted      TINYINT        NOT NULL DEFAULT 0 COMMENT '是否删除',
    version         INT            NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username_tenant (username, tenant_id, is_deleted),
    KEY idx_email (email),
    KEY idx_mobile (mobile),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2. 角色表 (iam_role)
```sql
CREATE TABLE iam_role (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name            VARCHAR(50)     NOT NULL COMMENT '角色名称',
    code            VARCHAR(50)     NOT NULL COMMENT '角色编码',
    description     VARCHAR(255)    COMMENT '角色描述',
    status          TINYINT        DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_id       BIGINT         NOT NULL COMMENT '租户ID',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT         COMMENT '创建人ID',
    updated_by      BIGINT         COMMENT '更新人ID',
    is_deleted      TINYINT        NOT NULL DEFAULT 0 COMMENT '是否删除',
    version         INT            NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code_tenant (code, tenant_id, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
```

### 3. 权限表 (iam_permission)
```sql
CREATE TABLE iam_permission (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name            VARCHAR(50)     NOT NULL COMMENT '权限名称',
    code            VARCHAR(50)     NOT NULL COMMENT '权限编码',
    type            TINYINT        NOT NULL COMMENT '类型：1-菜单，2-按钮，3-接口',
    parent_id       BIGINT         DEFAULT 0 COMMENT '父级ID',
    path            VARCHAR(255)    COMMENT '路径',
    component       VARCHAR(255)    COMMENT '前端组件',
    icon            VARCHAR(100)    COMMENT '图标',
    sort_order      INT            DEFAULT 0 COMMENT '排序',
    status          TINYINT        DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_id       BIGINT         NOT NULL COMMENT '租户ID',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT         COMMENT '创建人ID',
    updated_by      BIGINT         COMMENT '更新人ID',
    is_deleted      TINYINT        NOT NULL DEFAULT 0 COMMENT '是否删除',
    version         INT            NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code_tenant (code, tenant_id, is_deleted),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';
```

### 4. 用户角色关系表 (iam_user_role)
```sql
CREATE TABLE iam_user_role (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT         NOT NULL COMMENT '用户ID',
    role_id         BIGINT         NOT NULL COMMENT '角色ID',
    tenant_id       BIGINT         NOT NULL COMMENT '租户ID',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT         COMMENT '创建人ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系表';
```

### 5. 角色权限关系表 (iam_role_permission)
```sql
CREATE TABLE iam_role_permission (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id         BIGINT         NOT NULL COMMENT '角色ID',
    permission_id   BIGINT         NOT NULL COMMENT '权限ID',
    tenant_id       BIGINT         NOT NULL COMMENT '租户ID',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT         COMMENT '创建人ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关系表';
```

### 6. 部门表 (iam_department)
```sql
CREATE TABLE iam_department (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name            VARCHAR(50)     NOT NULL COMMENT '部门名称',
    code            VARCHAR(50)     NOT NULL COMMENT '部门编码',
    parent_id       BIGINT         DEFAULT 0 COMMENT '父级ID',
    path            VARCHAR(255)    COMMENT '路径',
    sort_order      INT            DEFAULT 0 COMMENT '排序',
    leader_id       BIGINT         COMMENT '负责人ID',
    status          TINYINT        DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_id       BIGINT         NOT NULL COMMENT '租户ID',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT         COMMENT '创建人ID',
    updated_by      BIGINT         COMMENT '更新人ID',
    is_deleted      TINYINT        NOT NULL DEFAULT 0 COMMENT '是否删除',
    version         INT            NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code_tenant (code, tenant_id, is_deleted),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';
```

### 7. 租户表 (iam_tenant)
```sql
CREATE TABLE iam_tenant (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name            VARCHAR(50)     NOT NULL COMMENT '租户名称',
    code            VARCHAR(50)     NOT NULL COMMENT '租户编码',
    domain          VARCHAR(100)    COMMENT '域名',
    contact_name    VARCHAR(50)     COMMENT '联系人',
    contact_phone   VARCHAR(20)     COMMENT '联系电话',
    contact_email   VARCHAR(100)    COMMENT '联系邮箱',
    status          TINYINT        DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    expire_time     DATETIME        COMMENT '过期时间',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT         COMMENT '创建人ID',
    updated_by      BIGINT         COMMENT '更新人ID',
    is_deleted      TINYINT        NOT NULL DEFAULT 0 COMMENT '是否删除',
    version         INT            NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';
```

## 缓存设计

### Redis 缓存结构

1. **用户信息缓存**
```
Key: iam:user:{userId}
Type: Hash
Fields:
    - id: 用户ID
    - username: 用户名
    - nickname: 昵称
    - status: 状态
    - tenantId: 租户ID
TTL: 1小时
```

2. **用户权限缓存**
```
Key: iam:user:perms:{userId}
Type: Set
Members: 权限编码列表
TTL: 1小时
```

3. **用户角色缓存**
```
Key: iam:user:roles:{userId}
Type: Set
Members: 角色编码列表
TTL: 1小时
```

4. **角色权限缓存**
```
Key: iam:role:perms:{roleId}
Type: Set
Members: 权限编码列表
TTL: 1小时
```

5. **Token 缓存**
```
Key: iam:token:{token}
Type: String
Value: 用户ID
TTL: 与token有效期一致
```

6. **权限资源缓存**
```
Key: iam:resource:perms
Type: Hash
Fields: 资源路径 -> 所需权限
TTL: 1小时
```

## 索引设计

### 索引策略
1. **主键索引**：所有表使用自增ID作为主键
2. **唯一索引**：用于保证业务唯一性
3. **普通索引**：用于提升查询性能
4. **组合索引**：多字段联合查询优化

### 重要索引
1. **用户表**
   - 用户名+租户ID唯一索引
   - 邮箱索引
   - 手机号索引
   - 部门ID索引

2. **角色表**
   - 角色编码+租户ID唯一索引

3. **权限表**
   - 权限编码+租户ID唯一索引
   - 父级ID索引

4. **部门表**
   - 部门编码+租户ID唯一索引
   - 父级ID索引

## 分库分表设计

### 分库策略
- 按租户ID分库
- 每个租户独立数据库
- 数据库命名规则：iam_{tenant_code}

### 分表策略
1. **水平分表**
   - 用户表按用户ID分表
   - 命名规则：表名_分片号
   - 分片算法：用户ID取模

2. **垂直分表**
   - 将大字段拆分到独立表
   - 例如：用户扩展信息表

## 数据迁移

### 迁移工具
- Flyway 数据库版本控制
- 迁移脚本规范
- 版本号命名规则

### 迁移策略
1. **版本控制**
   - 使用版本号管理
   - 增量迁移
   - 回滚机制

2. **数据一致性**
   - 事务控制
   - 数据校验
   - 失败处理 