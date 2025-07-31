> **文档位置**: infra/moudleDocs/{模块名称}/api-test.md

# Service-Auth API测试文档

## 接口功能列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | Feign调用 | 查看详情 |
|------|----------|----------|----------|----------|----------|-----------|----------|
| 1 | 用户登录 | POST | `/auth/login` | 用户登录认证 | 用户认证 | 否 | [查看详情](#1-用户登录) |
| 2 | 用户登出 | POST | `/auth/logout` | 用户登出，清除会话 | 会话管理 | 否 | [查看详情](#2-用户登出) |
| 3 | 强制登出 | POST | `/auth/logout/force/{userId}` | 管理员强制登出用户 | 会话管理 | 否 | [查看详情](#3-强制登出) |
| 4 | 健康检查 | GET | `/auth/test` | 认证服务健康检查 | 基础设施 | 否 | [查看详情](#4-健康检查) |
| 5 | 令牌验证 | POST | `/auth/validate` | 验证JWT令牌有效性 | 令牌管理 | 是 | [查看详情](#5-令牌验证) |
| 6 | 令牌刷新 | POST | `/auth/refresh` | 刷新JWT令牌 | 令牌管理 | 是 | [查看详情](#6-令牌刷新) |
| 7 | 获取用户信息 | GET | `/auth/user/info` | 获取当前用户信息 | 用户信息 | 是 | [查看详情](#7-获取用户信息) |
| 8 | 获取用户权限 | GET | `/auth/user/permissions` | 获取用户权限列表 | 权限管理 | 是 | [查看详情](#8-获取用户权限) |

## 1. 用户登录

### 接口描述
用户登录认证，验证用户名密码，返回JWT令牌和用户信息。

### 请求信息
- **请求方法**: POST
- **请求路径**: `/auth/login`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
  ```

### 请求参数
```json
{
  "username": "admin",
  "password": "123456",
  "captcha": "1234",
  "captchaKey": "captcha_key",
  "rememberMe": true
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "1234567890",
    "username": "admin",
    "nickname": "管理员",
    "avatar": "https://example.com/avatar.jpg",
    "roles": ["ADMIN", "USER"],
    "permissions": ["user:read", "user:write", "admin:all"],
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NTY3ODkwIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGVzIjoiWyJBRE1JTiIsIlVTRVIiXSIsInBlcm1pc3Npb25zIjoiWyJ1c2VyOnJlYWQiLCJ1c2VyOndyaXRlIiwiYWRtaW46YWxsIl0iLCJpYXQiOjE3MDMxMjM0NTYsImV4cCI6MTcwMzEyNzA1Nn0.signature",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 正常登录测试
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-123" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'

# 带验证码登录测试
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-124" \
  -d '{
    "username": "admin",
    "password": "123456",
    "captcha": "1234",
    "captchaKey": "captcha_key",
    "rememberMe": true
  }'

# 错误密码测试
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-125" \
  -d '{
    "username": "admin",
    "password": "wrong_password"
  }'

# 不存在的用户测试
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-126" \
  -d '{
    "username": "nonexistent",
    "password": "123456"
  }'
```

## 2. 用户登出

### 接口描述
用户登出，清除会话信息和令牌，将令牌加入黑名单。

### 请求信息
- **请求方法**: POST
- **请求路径**: `/auth/logout`
- **请求头**: 
  ```
  Content-Type: application/json
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  X-Request-ID: test-request-127
  ```

### 响应示例
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 正常登出测试
curl -X POST "http://localhost:8081/auth/logout" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Request-ID: test-request-127"

# 缺少Token测试
curl -X POST "http://localhost:8081/auth/logout" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-128"

# 无效Token测试
curl -X POST "http://localhost:8081/auth/logout" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid_token" \
  -H "X-Request-ID: test-request-129"
```

## 3. 强制登出

### 接口描述
管理员强制登出指定用户，清除该用户的所有会话。

### 请求信息
- **请求方法**: POST
- **请求路径**: `/auth/logout/force/{userId}`
- **请求头**: 
  ```
  Content-Type: application/json
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  X-Request-ID: test-request-130
  ```

### 路径参数
- `userId`: 用户ID

### 响应示例
```json
{
  "code": 200,
  "message": "强制登出成功",
  "data": null,
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 强制登出测试
curl -X POST "http://localhost:8081/auth/logout/force/1234567890" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Request-ID: test-request-130"

# 不存在的用户测试
curl -X POST "http://localhost:8081/auth/logout/force/nonexistent" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Request-ID: test-request-131"
```

## 4. 健康检查

### 接口描述
认证服务健康检查，用于验证服务是否正常运行。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/auth/test`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-132
  ```

### 响应示例
```json
{
  "code": 200,
  "message": "Auth Service is running!",
  "data": null,
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 健康检查测试
curl -X GET "http://localhost:8081/auth/test" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-132"
```

## 5. 令牌验证

### 接口描述
验证JWT令牌的有效性，检查令牌是否过期或被加入黑名单。

### 请求信息
- **请求方法**: POST
- **请求路径**: `/auth/validate`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-133
  ```

### 请求参数
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "令牌验证成功",
  "data": {
    "valid": true,
    "userId": "1234567890",
    "username": "admin",
    "expiresIn": 1800
  },
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 有效令牌验证
curl -X POST "http://localhost:8081/auth/validate" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-133" \
  -d '{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'

# 无效令牌验证
curl -X POST "http://localhost:8081/auth/validate" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-134" \
  -d '{
    "token": "invalid_token"
  }'

# 过期令牌验证
curl -X POST "http://localhost:8081/auth/validate" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-135" \
  -d '{
    "token": "expired_token_here"
  }'
```

## 6. 令牌刷新

### 接口描述
刷新JWT令牌，生成新的访问令牌。

### 请求信息
- **请求方法**: POST
- **请求路径**: `/auth/refresh`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-136
  ```

### 请求参数
```json
{
  "refreshToken": "refresh_token_here"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "令牌刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "new_refresh_token_here",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 令牌刷新测试
curl -X POST "http://localhost:8081/auth/refresh" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-136" \
  -d '{
    "refreshToken": "refresh_token_here"
  }'

# 无效刷新令牌测试
curl -X POST "http://localhost:8081/auth/refresh" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-137" \
  -d '{
    "refreshToken": "invalid_refresh_token"
  }'
```

## 7. 获取用户信息

### 接口描述
获取当前用户的详细信息。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/auth/user/info`
- **请求头**: 
  ```
  Content-Type: application/json
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  X-Request-ID: test-request-138
  ```

### 响应示例
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "id": "1234567890",
    "username": "admin",
    "nickname": "管理员",
    "avatar": "https://example.com/avatar.jpg",
    "email": "admin@example.com",
    "phone": "13800138000",
    "gender": 1,
    "birthday": "1990-01-01",
    "status": 1,
    "userType": 1,
    "lastLoginTime": "2024-12-19T10:00:00Z",
    "createTime": "2024-01-01T00:00:00Z",
    "updateTime": "2024-12-19T10:00:00Z"
  },
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 获取用户信息测试
curl -X GET "http://localhost:8081/auth/user/info" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Request-ID: test-request-138"

# 未授权访问测试
curl -X GET "http://localhost:8081/auth/user/info" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-139"
```

## 8. 获取用户权限

### 接口描述
获取当前用户的权限列表。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/auth/user/permissions`
- **请求头**: 
  ```
  Content-Type: application/json
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  X-Request-ID: test-request-140
  ```

### 响应示例
```json
{
  "code": 200,
  "message": "获取用户权限成功",
  "data": {
    "userId": "1234567890",
    "username": "admin",
    "roles": ["ADMIN", "USER"],
    "permissions": [
      "user:read",
      "user:write",
      "user:delete",
      "admin:all",
      "system:config",
      "system:monitor"
    ]
  },
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 获取用户权限测试
curl -X GET "http://localhost:8081/auth/user/permissions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Request-ID: test-request-140"

# 未授权访问测试
curl -X GET "http://localhost:8081/auth/user/permissions" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-141"
```

## 9. Feign调用示例

### 令牌验证Feign调用
```java
@FeignClient(name = "service-auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    
    @PostMapping("/auth/validate")
    ResultData<Boolean> validateToken(@RequestParam("token") String token);
    
    @GetMapping("/auth/user/info")
    ResultData<SysUser> getUserInfo(@RequestParam("userId") String userId);
    
    @GetMapping("/auth/user/permissions")
    ResultData<Map<String, Object>> getUserPermissions(@RequestParam("userId") String userId);
}
```

### 调用示例
```java
@Service
public class UserService {
    
    @Autowired
    private AuthFeignClient authFeignClient;
    
    public boolean validateUserToken(String token) {
        ResultData<Boolean> result = authFeignClient.validateToken(token);
        return result.isSuccess() && Boolean.TRUE.equals(result.getData());
    }
    
    public SysUser getUserInfo(String userId) {
        ResultData<SysUser> result = authFeignClient.getUserInfo(userId);
        return result.isSuccess() ? result.getData() : null;
    }
}
```

## 10. 错误处理测试

### 参数验证错误
```bash
# 缺少用户名
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "password": "123456"
  }'

# 缺少密码
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin"
  }'

# 空用户名
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "",
    "password": "123456"
  }'
```

### 业务逻辑错误
```bash
# 用户不存在
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nonexistent",
    "password": "123456"
  }'

# 密码错误
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "wrong_password"
  }'

# 账号被禁用
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "disabled_user",
    "password": "123456"
  }'
```

## 11. 性能测试

### 并发登录测试
```bash
# 使用ab进行并发登录测试
ab -n 100 -c 10 -p login_data.json -T application/json \
  http://localhost:8081/auth/login

# login_data.json内容
{
  "username": "admin",
  "password": "123456"
}
```

### 令牌验证性能测试
```bash
# 使用wrk进行令牌验证压力测试
wrk -t12 -c400 -d30s -s token_validate.lua \
  http://localhost:8081/auth/validate

# token_validate.lua脚本
wrk.method = "POST"
wrk.headers["Content-Type"] = "application/json"
wrk.body = '{"token":"valid_token_here"}'
```

## 12. 配置说明

### JWT配置
```yaml
# JWT配置
jwt:
  secret: your-secret-key-here-must-be-at-least-256-bits-long
  expiration: 3600000  # 1小时
  refresh-expiration: 86400000  # 24小时
  token-prefix: "Bearer"
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 1
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
```

### 安全配置
```yaml
spring:
  security:
    user:
      name: admin
      password: admin123
      roles: ADMIN
```

## 13. 注意事项

1. **令牌安全**: 确保JWT密钥足够复杂且定期更换
2. **密码安全**: 使用BCrypt加密存储密码
3. **会话管理**: 合理设置令牌过期时间
4. **黑名单管理**: 及时清理过期的黑名单记录
5. **权限控制**: 严格验证用户权限
6. **日志记录**: 记录关键操作日志，避免记录敏感信息
7. **性能优化**: 使用Redis缓存用户权限信息
8. **监控告警**: 监控登录失败率和异常行为 