> **文档位置**: infra/moudleDocs/{模块名称}/api-test.md

# Service-Common API测试文档

## 接口功能列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | 查看详情 |
|------|----------|----------|----------|----------|----------|----------|
| 1 | 统一响应格式测试 | POST | `/test/result-data` | 测试ResultData响应格式 | 数据结构 | [查看详情](#1-统一响应格式测试) |
| 2 | 业务异常测试 | POST | `/test/business-exception` | 测试BusinessException异常处理 | 异常处理 | [查看详情](#2-业务异常测试) |
| 3 | 参数验证测试 | POST | `/test/validation` | 测试参数验证功能 | 数据验证 | [查看详情](#3-参数验证测试) |
| 4 | 请求追踪测试 | POST | `/test/request-trace` | 测试RequestTrace追踪功能 | 链路追踪 | [查看详情](#4-请求追踪测试) |
| 5 | 错误码测试 | GET | `/test/error-codes` | 获取所有错误码定义 | 错误码管理 | [查看详情](#5-错误码测试) |

## 1. 统一响应格式测试

### 接口描述
测试ResultData统一响应格式的各种使用场景。

### 请求信息
- **请求方法**: POST
- **请求路径**: `/test/result-data`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

### 请求参数
```json
{
  "type": "success",
  "message": "测试成功",
  "data": {
    "id": "123",
    "name": "测试数据"
  }
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "测试成功",
  "data": {
    "id": "123",
    "name": "测试数据"
  },
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 成功响应测试
curl -X POST "http://localhost:8080/test/result-data" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "success",
    "message": "测试成功",
    "data": {
      "id": "123",
      "name": "测试数据"
    }
  }'

# 失败响应测试
curl -X POST "http://localhost:8080/test/result-data" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "fail",
    "message": "测试失败",
    "errorCode": "USER_NOT_FOUND"
  }'
```

## 2. 业务异常测试

### 接口描述
测试BusinessException异常的处理和响应格式。

### 请求信息
- **请求方法**: POST
- **请求路径**: `/test/business-exception`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

### 请求参数
```json
{
  "errorCode": "USER_NOT_FOUND",
  "message": "用户不存在"
}
```

### 响应示例
```json
{
  "code": 1001,
  "message": "用户不存在",
  "data": null,
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 业务异常测试
curl -X POST "http://localhost:8080/test/business-exception" \
  -H "Content-Type: application/json" \
  -d '{
    "errorCode": "USER_NOT_FOUND",
    "message": "用户不存在"
  }'

# 参数错误测试
curl -X POST "http://localhost:8080/test/business-exception" \
  -H "Content-Type: application/json" \
  -d '{
    "errorCode": "PARAM_ERROR",
    "message": "参数格式错误"
  }'
```

## 3. 参数验证测试

### 接口描述
测试Jakarta Validation参数验证功能。

### 请求信息
- **请求方法**: POST
- **请求路径**: `/test/validation`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

### 请求参数
```json
{
  "username": "",
  "email": "invalid-email",
  "age": -1
}
```

### 响应示例
```json
{
  "code": 400,
  "message": "用户名不能为空, 邮箱格式不正确, 年龄不能为负数",
  "data": null,
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 参数验证失败测试
curl -X POST "http://localhost:8080/test/validation" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "",
    "email": "invalid-email",
    "age": -1
  }'

# 参数验证成功测试
curl -X POST "http://localhost:8080/test/validation" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "age": 25
  }'
```

## 4. 请求追踪测试

### 接口描述
测试RequestTrace链路追踪功能。

### 请求信息
- **请求方法**: POST
- **请求路径**: `/test/request-trace`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
  X-User-ID: 1001
  X-Client-IP: 192.168.1.100
  User-Agent: curl/7.68.0
  ```

### 请求参数
```json
{
  "serviceName": "service-common",
  "requestPath": "/test/request-trace",
  "requestMethod": "POST",
  "requestParams": "{\"test\": \"data\"}"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "请求追踪成功",
  "data": {
    "requestId": "test-request-123",
    "userId": 1001,
    "clientIp": "192.168.1.100",
    "userAgent": "curl/7.68.0",
    "requestTimestamp": 1703123456789,
    "responseTimestamp": 1703123456890,
    "serviceName": "service-common",
    "requestPath": "/test/request-trace",
    "requestMethod": "POST",
    "requestParams": "{\"test\": \"data\"}",
    "responseStatus": 200,
    "duration": 101
  },
  "timestamp": 1703123456890
}
```

### 测试命令
```bash
# 请求追踪测试
curl -X POST "http://localhost:8080/test/request-trace" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-123" \
  -H "X-User-ID: 1001" \
  -H "X-Client-IP: 192.168.1.100" \
  -H "User-Agent: curl/7.68.0" \
  -d '{
    "serviceName": "service-common",
    "requestPath": "/test/request-trace",
    "requestMethod": "POST",
    "requestParams": "{\"test\": \"data\"}"
  }'
```

## 5. 错误码测试

### 接口描述
获取系统中定义的所有错误码信息。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/test/error-codes`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

### 响应示例
```json
{
  "code": 200,
  "message": "获取错误码成功",
  "data": [
    {
      "code": 200,
      "message": "操作成功"
    },
    {
      "code": 400,
      "message": "参数错误"
    },
    {
      "code": 401,
      "message": "未授权"
    },
    {
      "code": 403,
      "message": "禁止访问"
    },
    {
      "code": 404,
      "message": "资源不存在"
    },
    {
      "code": 500,
      "message": "服务器内部错误"
    },
    {
      "code": 1001,
      "message": "用户不存在"
    },
    {
      "code": 1002,
      "message": "用户已存在"
    },
    {
      "code": 1003,
      "message": "密码错误"
    },
    {
      "code": 1004,
      "message": "账号已被禁用"
    },
    {
      "code": 1005,
      "message": "令牌已过期"
    },
    {
      "code": 1006,
      "message": "令牌无效"
    }
  ],
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 获取所有错误码
curl -X GET "http://localhost:8080/test/error-codes" \
  -H "Content-Type: application/json"

# 获取特定错误码
curl -X GET "http://localhost:8080/test/error-codes?code=1001" \
  -H "Content-Type: application/json"
```

## 工具类测试

### ResultData工具方法测试
```bash
# 测试isSuccess方法
curl -X POST "http://localhost:8080/test/result-data-utils" \
  -H "Content-Type: application/json" \
  -d '{
    "testType": "isSuccess",
    "data": {
      "code": 200,
      "message": "成功"
    }
  }'

# 测试getData方法
curl -X POST "http://localhost:8080/test/result-data-utils" \
  -H "Content-Type: application/json" \
  -d '{
    "testType": "getData",
    "data": {
      "code": 200,
      "message": "成功",
      "data": {
        "id": "123",
        "name": "测试"
      }
    },
    "targetClass": "com.example.TestData"
  }'
```

### RequestTrace工具方法测试
```bash
# 测试create方法
curl -X POST "http://localhost:8080/test/request-trace-utils" \
  -H "Content-Type: application/json" \
  -d '{
    "testType": "create",
    "requestId": "test-123"
  }'

# 测试complete方法
curl -X POST "http://localhost:8080/test/request-trace-utils" \
  -H "Content-Type: application/json" \
  -d '{
    "testType": "complete",
    "requestId": "test-123",
    "responseStatus": 200
  }'

# 测试getSummary方法
curl -X POST "http://localhost:8080/test/request-trace-utils" \
  -H "Content-Type: application/json" \
  -d '{
    "testType": "getSummary",
    "requestId": "test-123"
  }'
```

## 配置说明

### 测试环境配置
```yaml
# application-test.yml
spring:
  profiles:
    active: test
  
logging:
  level:
    com.origin.common: DEBUG
    org.springframework.web: DEBUG

# 测试端点配置
test:
  endpoints:
    enabled: true
    base-path: /test
```

### 安全配置
```yaml
# 测试环境安全配置
security:
  test:
    enabled: false  # 测试环境关闭安全验证
```

## 注意事项

1. **测试数据**: 使用测试数据，避免影响生产环境
2. **异常处理**: 确保异常处理逻辑正确
3. **性能测试**: 测试工具类的性能表现
4. **兼容性**: 确保向后兼容性
5. **文档更新**: 及时更新测试文档 