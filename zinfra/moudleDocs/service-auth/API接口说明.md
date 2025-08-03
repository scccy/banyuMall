# service-auth API接口说明

> **文档位置**: `zinfra/moudleDocs/service-auth/API接口说明.md`
> **创建时间**: 2025-08-01
> **作者**: scccy

## 1. 接口概述

### 1.1 模块信息
- **模块名称**: service-auth（认证服务）
- **父模块**: service（服务层）
- **模块类型**: 认证授权服务
- **基础路径**: `/service/auth`

### 1.2 接口分类
- **用户认证接口**: 用户登录、登出等认证操作
- **密码管理接口**: 密码加密、验证等安全功能
- **基础设施接口**: 健康检查等系统功能

## 2. 用户认证接口

### 2.1 用户登录
- **接口路径**: `POST /service/auth/login`
- **功能描述**: 用户登录认证，返回JWT令牌和用户信息
- **是否需要Feign客户端**: 是

**请求参数**:
```json
{
  "username": "string",        // 用户名（必填）
  "password": "string"         // 密码（必填）
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "string",
    "username": "string",
    "nickname": "string",
    "avatar": "string",
    "roles": ["ADMIN", "USER"],
    "permissions": ["user:read", "user:write"],
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### 2.2 用户登出
- **接口路径**: `POST /service/auth/logout`
- **功能描述**: 用户登出，清除会话信息和令牌
- **是否需要Feign客户端**: 否

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应数据**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null
}
```

### 2.3 强制登出用户
- **接口路径**: `POST /service/auth/logout/force/{userId}`
- **功能描述**: 管理员强制登出指定用户
- **是否需要Feign客户端**: 否

**请求参数**:
```
userId: string    // 用户ID（路径参数）
```

**响应数据**:
```json
{
  "code": 200,
  "message": "强制登出成功",
  "data": null
}
```

### 2.4 认证服务健康检查
- **接口路径**: `GET /service/auth/test`
- **功能描述**: 认证服务健康检查
- **是否需要Feign客户端**: 否

**响应数据**:
```json
{
  "code": 200,
  "message": "认证服务运行正常",
  "data": "Auth Service is running"
}
```

## 3. 密码管理接口

### 3.1 密码加密
- **接口路径**: `POST /service/auth/password/encrypt`
- **功能描述**: 密码加密服务，供其他微服务调用
- **是否需要Feign客户端**: 是

**请求参数**:
```json
{
  "username": "string",        // 用户名（必填）
  "password": "string"         // 原始密码（必填）
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "密码加密成功",
  "data": {
    "username": "string",
    "encryptedPassword": "$2a$10$..."
  }
}
```

### 3.2 密码验证
- **接口路径**: `POST /service/auth/password/verify`
- **功能描述**: 密码验证服务，供其他微服务调用
- **是否需要Feign客户端**: 是

**请求参数**:
```json
{
  "username": "string",        // 用户名（必填）
  "password": "string"         // 待验证的密码（必填）
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "密码验证完成",
  "data": true                 // true-验证通过，false-验证失败
}
```

## 4. 权限验证接口

### 4.1 验证用户权限
- **接口路径**: `POST /service/auth/permission/verify`
- **功能描述**: 验证用户是否具有指定权限
- **是否需要Feign客户端**: 是

**请求参数**:
```json
{
  "userId": "string",          // 用户ID（必填）
  "permission": "string"       // 权限标识（必填）
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "权限验证完成",
  "data": {
    "hasPermission": true,     // true-有权限，false-无权限
    "userId": "string",
    "permission": "string"
  }
}
```

### 4.2 获取用户权限列表
- **接口路径**: `GET /service/auth/permission/user/{userId}`
- **功能描述**: 获取指定用户的所有权限列表
- **是否需要Feign客户端**: 是

**请求参数**:
```
userId: string    // 用户ID（路径参数）
```

**响应数据**:
```json
{
  "code": 200,
  "message": "获取用户权限成功",
  "data": {
    "userId": "string",
    "roles": ["ADMIN", "USER"],
    "permissions": ["user:read", "user:write", "task:create"]
  }
}
```

## 5. 令牌管理接口

### 5.1 验证令牌
- **接口路径**: `POST /service/auth/token/verify`
- **功能描述**: 验证JWT令牌的有效性
- **是否需要Feign客户端**: 是

**请求参数**:
```json
{
  "token": "string"            // JWT令牌（必填）
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "令牌验证完成",
  "data": {
    "valid": true,             // true-有效，false-无效
    "userId": "string",
    "username": "string",
    "expiresAt": "2025-08-01T12:00:00"
  }
}
```

### 5.2 刷新令牌
- **接口路径**: `POST /service/auth/token/refresh`
- **功能描述**: 刷新JWT令牌
- **是否需要Feign客户端**: 是

**请求参数**:
```json
{
  "token": "string"            // 当前JWT令牌（必填）
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "令牌刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

## 6. 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 200 | 成功 | 操作成功 |
| 400 | 请求参数错误 | 请求参数格式不正确 |
| 401 | 未授权 | 用户未登录或token无效 |
| 403 | 权限不足 | 用户权限不足 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器内部错误 | 服务器内部错误 |

## 7. 安全说明

### 7.1 认证机制
- 使用JWT（JSON Web Token）进行无状态认证
- 令牌有效期为1小时，支持自动刷新
- 支持令牌黑名单机制，确保登出后令牌失效

### 7.2 密码安全
- 使用BCrypt算法加密存储密码
- 密码强度要求：至少8位，包含字母和数字
- 支持密码验证失败次数限制

### 7.3 权限控制
- 基于角色的访问控制（RBAC）
- 支持细粒度的权限验证
- 权限信息缓存到Redis，提高验证效率

### 7.4 接口安全
- 所有敏感接口都需要JWT令牌验证
- 支持CORS跨域配置
- 防止SQL注入和XSS攻击

## 8. 注意事项

1. **接口路径规范**: 所有接口路径都遵循 `/service/auth/{具体功能}` 的格式
2. **参数验证**: 所有必填参数都会进行格式验证
3. **权限控制**: 敏感操作需要相应的权限验证
4. **错误处理**: 提供详细的错误信息和错误码
5. **性能优化**: 使用Redis缓存提高接口响应速度 