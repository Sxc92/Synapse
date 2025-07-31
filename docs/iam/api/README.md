# IAM 服务 API 文档

## API 概述

### 接口规范
1. **请求格式**
   - 使用 RESTful 风格
   - Content-Type: application/json
   - 字符编码：UTF-8

2. **响应格式**
```json
{
    "code": 200,           // 状态码
    "message": "success",  // 消息
    "data": {}            // 数据
}
```

3. **状态码**
   - 200：成功
   - 400：请求错误
   - 401：未认证
   - 403：无权限
   - 404：资源不存在
   - 500：服务器错误

4. **认证方式**
   - Bearer Token
   - Token 在 Header 中通过 Authorization 传递

### 环境信息
- 开发环境：http://dev-iam.synapsemom.com
- 测试环境：http://test-iam.synapsemom.com
- 生产环境：http://iam.synapsemom.com

## 认证接口

### 1. 用户登录
```http
POST /api/v1/auth/login
```

**请求参数**
```json
{
    "username": "admin",      // 用户名
    "password": "123456",     // 密码
    "tenantCode": "default"   // 租户编码
}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "expiresIn": 3600,
        "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
    }
}
```

### 2. 刷新Token
```http
POST /api/v1/auth/refresh
```

**请求参数**
```json
{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "expiresIn": 3600,
        "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
    }
}
```

### 3. 退出登录
```http
POST /api/v1/auth/logout
```

**响应结果**
```json
{
    "code": 200,
    "message": "success"
}
```

## 用户接口

### 1. 创建用户
```http
POST /api/v1/users
```

**请求参数**
```json
{
    "username": "zhangsan",
    "password": "123456",
    "nickname": "张三",
    "realName": "张三",
    "email": "zhangsan@example.com",
    "mobile": "13800138000",
    "gender": 1,
    "deptId": 1,
    "roleIds": [1, 2]
}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "zhangsan",
        "nickname": "张三",
        "email": "zhangsan@example.com",
        "mobile": "13800138000",
        "gender": 1,
        "status": 1,
        "deptId": 1,
        "createdAt": "2025-07-01 12:00:00"
    }
}
```

### 2. 更新用户
```http
PUT /api/v1/users/{id}
```

**请求参数**
```json
{
    "nickname": "张三",
    "realName": "张三",
    "email": "zhangsan@example.com",
    "mobile": "13800138000",
    "gender": 1,
    "deptId": 1,
    "roleIds": [1, 2]
}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "zhangsan",
        "nickname": "张三",
        "email": "zhangsan@example.com",
        "mobile": "13800138000",
        "gender": 1,
        "status": 1,
        "deptId": 1,
        "updatedAt": "2025-07-01 12:00:00"
    }
}
```

### 3. 删除用户
```http
DELETE /api/v1/users/{id}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success"
}
```

### 4. 查询用户列表
```http
GET /api/v1/users
```

**请求参数**
```
page: 1              // 页码
size: 10             // 每页大小
username: zhangsan   // 用户名
nickname: 张三       // 昵称
mobile: 13800138000  // 手机号
status: 1            // 状态
deptId: 1            // 部门ID
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "pages": 10,
        "list": [
            {
                "id": 1,
                "username": "zhangsan",
                "nickname": "张三",
                "email": "zhangsan@example.com",
                "mobile": "13800138000",
                "gender": 1,
                "status": 1,
                "deptId": 1,
                "createdAt": "2025-07-01 12:00:00"
            }
        ]
    }
}
```

## 角色接口

### 1. 创建角色
```http
POST /api/v1/roles
```

**请求参数**
```json
{
    "name": "管理员",
    "code": "ADMIN",
    "description": "系统管理员",
    "permissionIds": [1, 2, 3]
}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "管理员",
        "code": "ADMIN",
        "description": "系统管理员",
        "status": 1,
        "createdAt": "2025-07-01 12:00:00"
    }
}
```

### 2. 更新角色
```http
PUT /api/v1/roles/{id}
```

**请求参数**
```json
{
    "name": "管理员",
    "description": "系统管理员",
    "permissionIds": [1, 2, 3]
}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "管理员",
        "code": "ADMIN",
        "description": "系统管理员",
        "status": 1,
        "updatedAt": "2025-07-01 12:00:00"
    }
}
```

## 权限接口

### 1. 创建权限
```http
POST /api/v1/permissions
```

**请求参数**
```json
{
    "name": "用户管理",
    "code": "USER_MANAGE",
    "type": 1,
    "parentId": 0,
    "path": "/user",
    "component": "user/index",
    "icon": "user",
    "sortOrder": 1
}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "用户管理",
        "code": "USER_MANAGE",
        "type": 1,
        "parentId": 0,
        "path": "/user",
        "component": "user/index",
        "icon": "user",
        "sortOrder": 1,
        "status": 1,
        "createdAt": "2025-07-01 12:00:00"
    }
}
```

### 2. 查询权限树
```http
GET /api/v1/permissions/tree
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "系统管理",
            "code": "SYSTEM",
            "type": 1,
            "parentId": 0,
            "path": "/system",
            "component": "system/index",
            "icon": "setting",
            "sortOrder": 1,
            "children": [
                {
                    "id": 2,
                    "name": "用户管理",
                    "code": "USER_MANAGE",
                    "type": 1,
                    "parentId": 1,
                    "path": "/user",
                    "component": "user/index",
                    "icon": "user",
                    "sortOrder": 1
                }
            ]
        }
    ]
}
```

## 部门接口

### 1. 创建部门
```http
POST /api/v1/departments
```

**请求参数**
```json
{
    "name": "技术部",
    "code": "TECH",
    "parentId": 0,
    "leaderId": 1,
    "sortOrder": 1
}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "技术部",
        "code": "TECH",
        "parentId": 0,
        "leaderId": 1,
        "sortOrder": 1,
        "status": 1,
        "createdAt": "2025-07-01 12:00:00"
    }
}
```

### 2. 查询部门树
```http
GET /api/v1/departments/tree
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "总公司",
            "code": "HQ",
            "parentId": 0,
            "leaderId": 1,
            "sortOrder": 1,
            "children": [
                {
                    "id": 2,
                    "name": "技术部",
                    "code": "TECH",
                    "parentId": 1,
                    "leaderId": 2,
                    "sortOrder": 1
                }
            ]
        }
    ]
}
```

## 租户接口

### 1. 创建租户
```http
POST /api/v1/tenants
```

**请求参数**
```json
{
    "name": "示例公司",
    "code": "EXAMPLE",
    "domain": "example.com",
    "contactName": "张三",
    "contactPhone": "13800138000",
    "contactEmail": "zhangsan@example.com",
    "expireTime": "2026-12-31 23:59:59"
}
```

**响应结果**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "示例公司",
        "code": "EXAMPLE",
        "domain": "example.com",
        "status": 1,
        "createdAt": "2025-07-01 12:00:00"
    }
}
```

## 错误码说明

### 系统错误码
- 10000：系统内部错误
- 10001：参数验证失败
- 10002：数据不存在
- 10003：数据已存在
- 10004：操作失败

### 认证错误码
- 20001：用户名或密码错误
- 20002：账号已禁用
- 20003：账号已锁定
- 20004：Token已过期
- 20005：Token无效
- 20006：刷新Token无效

### 权限错误码
- 30001：无访问权限
- 30002：角色不存在
- 30003：权限不存在
- 30004：越权操作

### 业务错误码
- 40001：用户已存在
- 40002：角色已存在
- 40003：权限已存在
- 40004：部门已存在
- 40005：租户已存在 