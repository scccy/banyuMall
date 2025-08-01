# service-user API接口说明

> **文档位置**: `infra/moudleDocs/service-user/API接口说明.md`
> **创建时间**: 2025-08-01
> **作者**: scccy

## 接口概述

### 基础信息
- **服务名称**: service-user
- **服务端口**: 8081
- **基础路径**: `/service/user`
- **API文档地址**: http://localhost:8081/doc.html

### 通用响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 1. 用户管理接口

### 1.1 用户注册

**接口地址**: `POST /service/user/register`

**接口描述**: 用户注册

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "registerTime": "2025-01-27T10:00:00"
  }
}
```

**测试示例**:
```bash
curl -X POST "http://localhost:8081/service/user/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "phone": "13800138000"
  }'
```

### 1.2 用户登录

**接口地址**: `POST /service/user/login`

**接口描述**: 用户登录

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "testuser",
    "expireTime": "2025-01-27T18:00:00"
  }
}
```

**测试示例**:
```bash
curl -X POST "http://localhost:8081/service/user/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 1.3 获取用户信息

**接口地址**: `GET /service/user/info/{userId}`

**接口描述**: 获取用户详细信息

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "nickname": "测试用户",
    "avatar": "https://example.com/avatar.jpg",
    "status": "ACTIVE",
    "createTime": "2025-01-27T10:00:00",
    "updateTime": "2025-01-27T10:00:00"
  }
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8081/service/user/info/1"
```

### 1.4 更新用户信息

**接口地址**: `PUT /service/user/info/{userId}`

**接口描述**: 更新用户信息

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nickname | String | 否 | 昵称 |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |
| avatar | String | 否 | 头像URL |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "nickname": "新昵称",
    "email": "newemail@example.com",
    "phone": "13900139000",
    "avatar": "https://example.com/new-avatar.jpg",
    "updateTime": "2025-01-27T11:00:00"
  }
}
```

**测试示例**:
```bash
curl -X PUT "http://localhost:8081/service/user/info/1" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "新昵称",
    "email": "newemail@example.com",
    "phone": "13900139000"
  }'
```

### 1.5 修改密码

**接口地址**: `PUT /service/user/password/{userId}`

**接口描述**: 修改用户密码

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | String | 是 | 旧密码 |
| newPassword | String | 是 | 新密码 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**测试示例**:
```bash
curl -X PUT "http://localhost:8081/service/user/password/1" \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "password123",
    "newPassword": "newpassword123"
  }'
```

## 2. 用户档案接口

### 2.1 获取用户档案

**接口地址**: `GET /service/user/profile/{userId}`

**接口描述**: 获取用户档案信息

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "realName": "张三",
    "gender": "MALE",
    "birthday": "1990-01-01",
    "idCard": "110101199001011234",
    "address": "北京市朝阳区",
    "education": "本科",
    "occupation": "软件工程师",
    "income": "10000-20000",
    "interests": ["技术", "阅读", "旅行"],
    "createTime": "2025-01-27T10:00:00",
    "updateTime": "2025-01-27T10:00:00"
  }
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8081/service/user/profile/1"
```

### 2.2 更新用户档案

**接口地址**: `PUT /service/user/profile/{userId}`

**接口描述**: 更新用户档案信息

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| realName | String | 否 | 真实姓名 |
| gender | String | 否 | 性别 |
| birthday | String | 否 | 生日 |
| address | String | 否 | 地址 |
| education | String | 否 | 学历 |
| occupation | String | 否 | 职业 |
| income | String | 否 | 收入范围 |
| interests | List<String> | 否 | 兴趣爱好 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "realName": "张三",
    "gender": "MALE",
    "birthday": "1990-01-01",
    "address": "北京市朝阳区",
    "education": "本科",
    "occupation": "软件工程师",
    "income": "10000-20000",
    "interests": ["技术", "阅读", "旅行"],
    "updateTime": "2025-01-27T11:00:00"
  }
}
```

**测试示例**:
```bash
curl -X PUT "http://localhost:8081/service/user/profile/1" \
  -H "Content-Type: application/json" \
  -d '{
    "realName": "张三",
    "gender": "MALE",
    "birthday": "1990-01-01",
    "address": "北京市朝阳区",
    "education": "本科",
    "occupation": "软件工程师",
    "income": "10000-20000",
    "interests": ["技术", "阅读", "旅行"]
  }'
```

## 3. 用户状态接口

### 3.1 获取用户状态

**接口地址**: `GET /service/user/status/{userId}`

**接口描述**: 获取用户状态信息

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "status": "ACTIVE",
    "lastLoginTime": "2025-01-27T10:00:00",
    "loginCount": 100,
    "onlineStatus": "ONLINE",
    "createTime": "2025-01-27T10:00:00"
  }
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8081/service/user/status/1"
```

### 3.2 更新用户状态

**接口地址**: `PUT /service/user/status/{userId}`

**接口描述**: 更新用户状态

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | String | 是 | 用户状态 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**测试示例**:
```bash
curl -X PUT "http://localhost:8081/service/user/status/1" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "INACTIVE"
  }'
```

## 4. 用户查询接口

### 4.1 用户列表查询

**接口地址**: `GET /service/user/list`

**接口描述**: 分页查询用户列表

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 页大小，默认10 |
| username | String | 否 | 用户名 |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |
| status | String | 否 | 用户状态 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10,
    "records": [
      {
        "userId": 1,
        "username": "testuser",
        "email": "test@example.com",
        "phone": "13800138000",
        "nickname": "测试用户",
        "status": "ACTIVE",
        "createTime": "2025-01-27T10:00:00"
      }
    ]
  }
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8081/service/user/list?pageNum=1&pageSize=10&status=ACTIVE"
```

### 4.2 用户搜索

**接口地址**: `GET /service/user/search`

**接口描述**: 搜索用户

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 是 | 搜索关键词 |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 页大小，默认10 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "pages": 1,
    "current": 1,
    "size": 10,
    "records": [
      {
        "userId": 1,
        "username": "testuser",
        "nickname": "测试用户",
        "email": "test@example.com"
      }
    ]
  }
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8081/service/user/search?keyword=test&pageNum=1&pageSize=10"
```

## 5. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 用户不存在 |
| 409 | 用户名已存在 |
| 500 | 服务器内部错误 |
| 1001 | 用户名或密码错误 |
| 1002 | 用户已被禁用 |
| 1003 | 密码强度不足 |
| 1004 | 邮箱格式错误 |
| 1005 | 手机号格式错误 |

## 6. 注意事项

### 6.1 安全要求
- 所有接口需要JWT Token认证（除注册和登录接口）
- 密码传输需要加密
- 敏感信息需要脱敏处理

### 6.2 性能要求
- 接口响应时间 < 200ms
- 支持并发访问
- 数据缓存优化

### 6.3 数据验证
- 用户名长度：3-20个字符
- 密码长度：6-20个字符
- 邮箱格式验证
- 手机号格式验证

### 6.4 业务规则
- 用户名唯一性检查
- 邮箱唯一性检查
- 手机号唯一性检查
- 用户状态管理 