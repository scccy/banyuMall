# 半语积分商城 API 文档

## 文档概述

本文档详细描述了半语积分商城微服务架构中的所有API接口，包括接口路径、HTTP方法、所属微服务、请求参数、响应格式等信息。

### 文档版本
- **版本**: v1.0.0
- **更新时间**: 2025-01-27
- **维护者**: 项目开发团队

### 服务架构
```
半语积分商城微服务架构
├── service-gateway (8080)     - API网关服务
├── service-auth (8081)        - 认证授权服务
├── service-user (8082)        - 用户管理服务
├── service-base               - 基础支撑服务
└── 其他服务 (待开发)...
```

## 通用规范

### 1. 请求格式
- **Content-Type**: `application/json`
- **字符编码**: UTF-8
- **认证方式**: Bearer Token (JWT)

### 2. 响应格式
所有接口统一使用 `ResultData<T>` 响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "requestId": "req_123456",
  "userId": 12345,
  "clientIp": "192.168.1.1",
  "userAgent": "Mozilla/5.0...",
  "timestamp": 1706347800000
}
```

### 3. HTTP状态码
- `200`: 成功
- `400`: 请求参数错误
- `401`: 未授权
- `403`: 禁止访问
- `404`: 资源不存在
- `500`: 服务器内部错误

### 4. 分页格式
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

## 服务路由配置

### 网关路由规则
| 服务名称 | 服务端口 | 网关路径 | 内部路径 | 说明 |
|---------|---------|---------|---------|------|
| service-auth | 8081 | `/auth/**` | `/auth/**` | 认证服务 |
| service-user | 8082 | `/user/**` | `/user/**` | 用户服务 |
| service-gateway | 8080 | `/` | - | API网关 |

## 接口数据流说明

### 数据流图例
```mermaid
graph LR
    A[客户端] --> B[API网关]
    B --> C[微服务]
    C --> D[数据库/其他服务]
```

### 典型数据流示例
1. **用户登录流程**:
   - 客户端 → 网关(/auth/login) → Auth服务 → 数据库验证 → 返回JWT
   - JWT用于后续接口认证

2. **用户信息获取流程**:
   - 客户端(带JWT) → 网关(/user/profile) → User服务 → 数据库查询 → 返回用户数据

3. **跨服务调用流程**:
   - 客户端 → 网关 → 服务A → 服务B(通过Feign) → 返回组合数据

## API 接口详情

### 1. 认证服务 (service-auth)

#### 1.1 用户认证接口

##### 1.1.1 用户登录
- **接口路径**: `/auth/login`
- **HTTP方法**: `POST`
- **所属服务**: service-auth
- **接口类型**: Controller
- **微服务端口**: 8081
- **网关路径**: `/auth/login`

**请求参数**:
```json
{
  "username": "string",        // 用户名 (必填)
  "password": "string",        // 密码 (必填)
  "captcha": "string",         // 验证码 (可选)
  "captchaKey": "string",      // 验证码KEY (可选)
  "rememberMe": false          // 记住我 (可选，默认false)
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "string",        // 用户ID
    "username": "string",      // 用户名
    "nickname": "string",      // 昵称
    "avatar": "string",        // 头像URL
    "roles": ["string"],       // 角色列表
    "permissions": ["string"], // 权限列表
    "token": "string",         // 访问令牌
    "tokenType": "Bearer",     // 令牌类型
    "expiresIn": 3600          // 过期时间(秒)
  }
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "用户名或密码错误",
  "data": null
}
```

##### 1.1.2 用户登出
- **接口路径**: `/auth/logout`
- **HTTP方法**: `POST`
- **所属服务**: service-auth
- **接口类型**: Controller
- **微服务端口**: 8081
- **网关路径**: `/auth/logout`
- **认证要求**: 需要Bearer Token

**请求头**:
```
Authorization: Bearer <token>
```

**响应数据**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null
}
```

#### 1.2 测试接口

##### 1.2.1 健康检查
- **接口路径**: `/auth/test/health`
- **HTTP方法**: `GET`
- **所属服务**: service-auth
- **接口类型**: Controller
- **微服务端口**: 8081
- **网关路径**: `/auth/test/health`

**响应数据**:
```json
{
  "code": 200,
  "message": "Auth Service is running!",
  "data": null
}
```

##### 1.2.2 Knife4j测试
- **接口路径**: `/auth/test/knife4j`
- **HTTP方法**: `GET`
- **所属服务**: service-auth
- **接口类型**: Controller
- **微服务端口**: 8081
- **网关路径**: `/auth/test/knife4j`

**响应数据**:
```json
{
  "code": 200,
  "message": "Knife4j configuration is working!",
  "data": null
}
```

### 2. 用户服务 (service-user)

#### 2.1 用户扩展信息管理

##### 2.1.1 获取用户扩展信息
- **接口路径**: `/user/profile/{userId}`
- **HTTP方法**: `GET`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/profile/{userId}`
- **认证要求**: 需要Bearer Token

**路径参数**:
- `userId`: 用户ID (必填)

**请求头**:
```
Authorization: Bearer <token>
```

**响应数据**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": "string",
    "userId": "string",
    "realName": "string",      // 真实姓名
    "companyName": "string",   // 公司名称
    "companyAddress": "string", // 公司地址
    "contactPerson": "string", // 联系人
    "contactPhone": "string",  // 联系电话
    "industry": "string",      // 所属行业
    "description": "string",   // 个人简介
    "createTime": "2025-01-27T14:30:00.000Z",
    "updateTime": "2025-01-27T14:30:00.000Z"
  }
}
```

##### 2.1.2 创建或更新用户扩展信息
- **接口路径**: `/user/profile/{userId}`
- **HTTP方法**: `POST`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/profile/{userId}`
- **认证要求**: 需要Bearer Token

**路径参数**:
- `userId`: 用户ID (必填)

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求参数**:
```json
{
  "nickname": "string",        // 昵称
  "avatar": "string",          // 头像URL
  "email": "string",           // 邮箱
  "phone": "string",           // 手机号
  "gender": 1,                 // 性别：0-未知，1-男，2-女
  "birthday": "2025-01-27",    // 生日
  "realName": "string",        // 真实姓名
  "companyName": "string",     // 公司名称
  "companyAddress": "string",  // 公司地址
  "contactPerson": "string",   // 联系人
  "contactPhone": "string",    // 联系电话
  "industry": "string",        // 所属行业
  "description": "string"      // 个人简介
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

#### 2.2 用户配置管理

##### 2.2.1 获取用户所有配置
- **接口路径**: `/user/config/{userId}`
- **HTTP方法**: `GET`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/config/{userId}`

**路径参数**:
- `userId`: 用户ID (必填)

**响应数据**:
```json
{
  "code": 200,
  "message": "1",
  "data": [
    {
      "id": "string",
      "userId": "string",
      "configKey": "string",
      "configValue": "string",
      "configType": "string",
      "createTime": "2025-01-27T14:30:00.000Z",
      "updateTime": "2025-01-27T14:30:00.000Z"
    }
  ]
}
```

##### 2.2.2 获取用户配置Map
- **接口路径**: `/user/config/{userId}/map`
- **HTTP方法**: `GET`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/config/{userId}/map`

**路径参数**:
- `userId`: 用户ID (必填)

**响应数据**:
```json
{
  "code": 200,
  "message": "1",
  "data": {
    "configKey1": "configValue1",
    "configKey2": "configValue2"
  }
}
```

##### 2.2.3 获取用户指定配置
- **接口路径**: `/user/config/{userId}/{configKey}`
- **HTTP方法**: `GET`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/config/{userId}/{configKey}`

**路径参数**:
- `userId`: 用户ID (必填)
- `configKey`: 配置键 (必填)

**响应数据**:
```json
{
  "code": 200,
  "message": "configValue",
  "data": null
}
```

##### 2.2.4 设置用户配置
- **接口路径**: `/user/config/{userId}/{configKey}`
- **HTTP方法**: `POST`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/config/{userId}/{configKey}`

**路径参数**:
- `userId`: 用户ID (必填)
- `configKey`: 配置键 (必填)

**查询参数**:
- `configValue`: 配置值 (必填)
- `configType`: 配置类型 (可选，默认"string")

**响应数据**:
```json
{
  "code": 200,
  "message": "true",
  "data": null
}
```

##### 2.2.5 批量设置用户配置
- **接口路径**: `/user/config/{userId}/batch`
- **HTTP方法**: `POST`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/config/{userId}/batch`

**路径参数**:
- `userId`: 用户ID (必填)

**请求参数**:
```json
{
  "configKey1": "configValue1",
  "configKey2": "configValue2"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "true",
  "data": null
}
```

##### 2.2.6 删除用户配置
- **接口路径**: `/user/config/{userId}/{configKey}`
- **HTTP方法**: `DELETE`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/config/{userId}/{configKey}`

**路径参数**:
- `userId`: 用户ID (必填)
- `configKey`: 配置键 (必填)

**响应数据**:
```json
{
  "code": 200,
  "message": "true",
  "data": null
}
```

#### 2.3 测试接口

##### 2.3.1 健康检查
- **接口路径**: `/user/test/health`
- **HTTP方法**: `GET`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/test/health`

**响应数据**:
```json
{
  "code": 200,
  "message": "User Service is running!",
  "data": null
}
```

##### 2.3.2 Knife4j测试
- **接口路径**: `/user/test/knife4j`
- **HTTP方法**: `GET`
- **所属服务**: service-user
- **接口类型**: Controller
- **微服务端口**: 8082
- **网关路径**: `/user/test/knife4j`

**响应数据**:
```json
{
  "code": 200,
  "message": "Knife4j configuration is working!",
  "data": null
}
```

### 3. 服务间调用接口 (Feign Client)

#### 3.1 认证服务Feign客户端

##### 3.1.1 用户登录 (Feign)
- **接口路径**: `/auth/login`
- **HTTP方法**: `POST`
- **所属服务**: service-auth
- **接口类型**: Feign Client
- **调用方**: 其他微服务
- **服务名**: service-auth

**请求参数**:
```json
{
  "username": "string",
  "password": "string",
  "captcha": "string",
  "captchaKey": "string",
  "rememberMe": false
}
```

**响应数据**: 同Controller接口

##### 3.1.2 用户登出 (Feign)
- **接口路径**: `/auth/logout`
- **HTTP方法**: `POST`
- **所属服务**: service-auth
- **接口类型**: Feign Client
- **调用方**: 其他微服务
- **服务名**: service-auth

**请求头**:
```
Authorization: Bearer <token>
```

**响应数据**: 同Controller接口

#### 3.2 用户服务Feign客户端

##### 3.2.1 获取用户基础信息
- **接口路径**: `/auth/user/{userId}`
- **HTTP方法**: `GET`
- **所属服务**: service-auth
- **接口类型**: Feign Client
- **调用方**: 其他微服务
- **服务名**: service-auth

**路径参数**:
- `userId`: 用户ID (必填)

**请求头**:
```
Authorization: Bearer <token>
```

**响应数据**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "userId": "string",
    "username": "string",
    "nickname": "string",
    "status": 1
  }
}
```

##### 3.2.2 验证用户是否存在
- **接口路径**: `/auth/user/{userId}/exists`
- **HTTP方法**: `GET`
- **所属服务**: service-auth
- **接口类型**: Feign Client
- **调用方**: 其他微服务
- **服务名**: service-auth

**路径参数**:
- `userId`: 用户ID (必填)

**请求头**:
```
Authorization: Bearer <token>
```

**响应数据**:
```json
{
  "code": 200,
  "message": "验证成功",
  "data": true
}
```

##### 3.2.3 获取用户状态
- **接口路径**: `/auth/user/{userId}/status`
- **HTTP方法**: `GET`
- **所属服务**: service-auth
- **接口类型**: Feign Client
- **调用方**: 其他微服务
- **服务名**: service-auth

**路径参数**:
- `userId`: 用户ID (必填)

**请求头**:
```
Authorization: Bearer <token>
```

**响应数据**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": 1
}
```

## 错误码说明

### 通用错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 请求参数错误 | 检查请求参数格式和必填项 |
| 401 | 未授权 | 检查认证token是否有效 |
| 403 | 禁止访问 | 检查用户权限 |
| 404 | 资源不存在 | 检查请求的资源是否存在 |
| 500 | 服务器内部错误 | 联系技术支持 |

### 业务错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 1001 | 用户名或密码错误 | 检查用户名和密码 |
| 1002 | 用户不存在 | 检查用户ID是否正确 |
| 1003 | 用户已被禁用 | 联系管理员 |
| 1004 | 验证码错误 | 重新获取验证码 |
| 2001 | 配置不存在 | 检查配置键是否正确 |
| 2002 | 配置值格式错误 | 检查配置值格式 |

## 接口调用示例

### 1. 用户登录流程

```bash
# 1. 用户登录
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "12345",
    "username": "testuser",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  }
}
```

### 2. 获取用户信息流程

```bash
# 1. 获取用户扩展信息
curl -X GET http://localhost:8080/user/profile/12345 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 响应
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "userId": "12345",
    "realName": "张三",
    "companyName": "测试公司"
  }
}
```

### 3. 用户配置管理流程

```bash
# 1. 设置用户配置
curl -X POST "http://localhost:8080/user/config/12345/theme?configValue=dark" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 2. 获取用户配置
curl -X GET http://localhost:8080/user/config/12345/theme \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 开发环境配置

### 本地开发环境
```bash
# 启动认证服务
cd service/service-auth
mvn spring-boot:run

# 启动用户服务
cd service/service-user
mvn spring-boot:run

# 启动网关服务
cd service/service-gateway
mvn spring-boot:run
```

### 服务端口配置
| 服务名称 | 端口 | 上下文路径 |
|---------|------|-----------|
| service-gateway | 8080 | / |
| service-auth | 8081 | /auth |
| service-user | 8082 | /user |

### 数据库配置
- **MySQL**: localhost:3306
- **Redis**: localhost:6379
- **Nacos**: localhost:8848

## 文档维护

### 更新规则
1. 新增接口时，必须更新本文档
2. 修改接口时，必须更新本文档
3. 删除接口时，必须更新本文档
4. 版本更新时，必须更新版本号

### 文档结构
- 按服务模块分组
- 每个接口包含完整的请求响应信息
- 提供调用示例和错误码说明
- 包含环境配置信息

### 联系方式
- 文档维护者：项目开发团队
- 问题反馈：通过项目Issue系统
- 技术支持：联系项目负责人