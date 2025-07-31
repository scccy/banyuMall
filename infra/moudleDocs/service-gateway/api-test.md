> **文档位置**: infra/moudleDocs/{模块名称}/api-test.md

# Service-Gateway API测试文档

## 接口功能列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | 查看详情 |
|------|----------|----------|----------|----------|----------|----------|
| 1 | 健康检查 | GET | `/actuator/health` | 网关服务健康状态检查 | 基础设施 | [查看详情](#1-健康检查) |
| 2 | 路由信息 | GET | `/actuator/gateway/routes` | 获取网关路由配置信息 | 路由管理 | [查看详情](#2-路由信息) |
| 3 | 应用信息 | GET | `/actuator/info` | 获取网关应用基本信息 | 基础设施 | [查看详情](#3-应用信息) |
| 4 | 指标信息 | GET | `/actuator/metrics` | 获取网关性能指标信息 | 性能监控 | [查看详情](#4-指标信息) |
| 5 | 认证服务路由 | POST | `/auth/login` | 通过网关访问认证服务 | 路由转发 | [查看详情](#5-认证服务路由) |
| 6 | 用户服务路由 | GET | `/user/profile/{id}` | 通过网关访问用户服务 | 路由转发 | [查看详情](#6-用户服务路由) |
| 7 | 限流测试 | GET | `/test/rate-limit` | 测试网关限流功能 | 限流保护 | [查看详情](#7-限流测试) |
| 8 | 链路追踪测试 | GET | `/test/trace` | 测试链路追踪功能 | 链路追踪 | [查看详情](#8-链路追踪测试) |

## 1. 健康检查

### 接口描述
检查网关服务健康状态，返回服务运行状态信息。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/actuator/health`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

### 响应示例
```json
{
  "status": "UP",
  "components": {
    "discoveryComposite": {
      "status": "UP",
      "components": {
        "discoveryClient": {
          "status": "UP",
          "details": {
            "services": [
              "service-auth",
              "service-user",
              "service-gateway"
            ]
          }
        }
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 419430400000,
        "threshold": 10485760
      }
    }
  }
}
```

### 测试命令
```bash
curl -X GET "http://localhost:8080/actuator/health" \
  -H "Content-Type: application/json"
```

## 2. 路由信息

### 接口描述
获取网关当前配置的所有路由信息。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/actuator/gateway/routes`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

### 响应示例
```json
[
  {
    "route_id": "auth-service",
    "route_definition": {
      "id": "auth-service",
      "predicates": [
        {
          "name": "Path",
          "args": {
            "pattern": "/auth/**"
          }
        }
      ],
      "filters": [
        {
          "name": "AddRequestHeader",
          "args": {
            "name": "X-Gateway-Source",
            "value": "service-gateway"
          }
        },
        {
          "name": "AddRequestHeader",
          "args": {
            "name": "X-Service-Name",
            "value": "service-auth"
          }
        }
      ],
      "uri": "lb://service-auth",
      "order": 0
    },
    "order": 0
  },
  {
    "route_id": "user-service",
    "route_definition": {
      "id": "user-service",
      "predicates": [
        {
          "name": "Path",
          "args": {
            "pattern": "/user/**"
          }
        }
      ],
      "filters": [
        {
          "name": "AddRequestHeader",
          "args": {
            "name": "X-Gateway-Source",
            "value": "service-gateway"
          }
        },
        {
          "name": "AddRequestHeader",
          "args": {
            "name": "X-Service-Name",
            "value": "service-user"
          }
        }
      ],
      "uri": "lb://service-user",
      "order": 0
    },
    "order": 0
  }
]
```

### 测试命令
```bash
curl -X GET "http://localhost:8080/actuator/gateway/routes" \
  -H "Content-Type: application/json"
```

## 3. 应用信息

### 接口描述
获取网关应用的基本信息，包括版本、描述等。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/actuator/info`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

### 响应示例
```json
{
  "app": {
    "name": "service-gateway",
    "version": "1.0.0",
    "description": "BanyuMall API网关服务"
  },
  "git": {
    "commit": {
      "id": "abc123",
      "time": "2024-12-19T10:00:00Z"
    },
    "branch": "main"
  }
}
```

### 测试命令
```bash
curl -X GET "http://localhost:8080/actuator/info" \
  -H "Content-Type: application/json"
```

## 4. 指标信息

### 接口描述
获取网关的性能指标信息，包括请求统计、响应时间等。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/actuator/metrics`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

### 响应示例
```json
{
  "names": [
    "gateway.requests",
    "gateway.requests.seconds",
    "gateway.requests.seconds.max",
    "gateway.requests.seconds.count",
    "gateway.requests.seconds.sum",
    "jvm.memory.used",
    "jvm.memory.max",
    "jvm.threads.live",
    "process.cpu.usage",
    "system.cpu.usage"
  ]
}
```

### 测试命令
```bash
# 获取所有指标
curl -X GET "http://localhost:8080/actuator/metrics" \
  -H "Content-Type: application/json"

# 获取网关请求统计
curl -X GET "http://localhost:8080/actuator/metrics/gateway.requests" \
  -H "Content-Type: application/json"

# 获取响应时间统计
curl -X GET "http://localhost:8080/actuator/metrics/gateway.requests.seconds" \
  -H "Content-Type: application/json"
```

## 5. 认证服务路由

### 接口描述
通过网关访问认证服务的登录接口。

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
  "password": "123456"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_123",
    "expiresIn": 3600,
    "userInfo": {
      "id": "123",
      "username": "admin",
      "email": "admin@example.com"
    }
  },
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 登录测试
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-123" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'

# 带Token的请求测试
curl -X GET "http://localhost:8080/auth/profile" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Request-ID: test-request-124"
```

## 6. 用户服务路由

### 接口描述
通过网关访问用户服务的接口。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/user/profile/{id}`
- **请求头**: 
  ```
  Content-Type: application/json
  Authorization: Bearer token
  X-Request-ID: test-request-125
  ```

### 路径参数
- `id`: 用户ID

### 响应示例
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "id": "123",
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "测试用户",
    "avatar": "https://example.com/avatar.jpg",
    "status": 1,
    "createTime": "2024-12-19T10:00:00Z"
  },
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 获取用户信息
curl -X GET "http://localhost:8080/user/profile/123" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Request-ID: test-request-125"

# 创建用户
curl -X POST "http://localhost:8080/user/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Request-ID: test-request-126" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "123456"
  }'

# 更新用户信息
curl -X PUT "http://localhost:8080/user/profile/123" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Request-ID: test-request-127" \
  -d '{
    "nickname": "新昵称",
    "email": "newemail@example.com"
  }'
```

## 7. 限流测试

### 接口描述
测试网关的限流功能，验证限流策略是否生效。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/test/rate-limit`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

### 响应示例
```json
{
  "code": 200,
  "message": "限流测试成功",
  "data": {
    "timestamp": 1703123456789,
    "requestId": "req-1703123456789-abc12345",
    "clientIp": "192.168.1.100"
  },
  "timestamp": 1703123456789
}
```

### 限流响应示例
```json
{
  "code": 429,
  "message": "请求过于频繁，请稍后重试",
  "data": null,
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 正常请求测试
curl -X GET "http://localhost:8080/test/rate-limit" \
  -H "Content-Type: application/json"

# 限流测试（快速连续请求）
for i in {1..50}; do
  curl -X GET "http://localhost:8080/test/rate-limit" \
    -H "Content-Type: application/json" &
done
wait
```

## 8. 链路追踪测试

### 接口描述
测试网关的链路追踪功能，验证请求ID和链路信息的传递。

### 请求信息
- **请求方法**: GET
- **请求路径**: `/test/trace`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: custom-request-id-123
  X-User-ID: 1001
  X-Client-IP: 192.168.1.100
  User-Agent: curl/7.68.0
  ```

### 响应示例
```json
{
  "code": 200,
  "message": "链路追踪测试成功",
  "data": {
    "requestId": "custom-request-id-123",
    "userId": 1001,
    "clientIp": "192.168.1.100",
    "userAgent": "curl/7.68.0",
    "gatewayTime": 1703123456789,
    "serviceName": "service-gateway",
    "requestPath": "/test/trace",
    "requestMethod": "GET",
    "duration": 15
  },
  "timestamp": 1703123456789
}
```

### 测试命令
```bash
# 链路追踪测试
curl -X GET "http://localhost:8080/test/trace" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: custom-request-id-123" \
  -H "X-User-ID: 1001" \
  -H "X-Client-IP: 192.168.1.100" \
  -H "User-Agent: curl/7.68.0"

# 通过网关访问其他服务并验证链路追踪
curl -X GET "http://localhost:8080/user/profile/123" \
  -H "Authorization: Bearer token" \
  -H "X-Request-ID: trace-test-123" \
  -H "X-User-ID: 1001" \
  -H "X-Client-IP: 192.168.1.100"
```

## 9. 错误处理测试

### 服务不可用测试
```bash
# 测试服务不可用的情况
curl -X GET "http://localhost:8080/auth/nonexistent" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: error-test-123"
```

### 超时测试
```bash
# 测试请求超时的情况
curl -X GET "http://localhost:8080/user/slow" \
  -H "Content-Type: application/json" \
  --max-time 5
```

### 参数错误测试
```bash
# 测试参数错误的情况
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "",
    "password": ""
  }'
```

## 10. 性能测试

### 并发测试
```bash
# 使用ab进行并发测试
ab -n 1000 -c 10 -H "Content-Type: application/json" \
  http://localhost:8080/actuator/health

# 使用wrk进行压力测试
wrk -t12 -c400 -d30s http://localhost:8080/actuator/health
```

### 路由性能测试
```bash
# 测试路由转发性能
for i in {1..100}; do
  curl -X GET "http://localhost:8080/user/profile/123" \
    -H "Authorization: Bearer token" &
done
wait
```

## 11. 配置说明

### 启用监控端点
在`application.yml`中配置：
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,gateway
  endpoint:
    health:
      show-details: always
    gateway:
      enabled: true
```

### 限流配置
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://service-auth
          predicates:
            - Path=/auth/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@userKeyResolver}"
```

### 跨域配置
```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOriginPatterns: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
```

## 12. 注意事项

1. **服务发现**: 确保Nacos服务发现正常工作
2. **负载均衡**: 验证负载均衡策略是否生效
3. **限流保护**: 测试限流功能，防止系统过载
4. **链路追踪**: 确保请求ID在整个链路中正确传递
5. **异常处理**: 验证异常处理逻辑是否正确
6. **性能监控**: 定期检查网关性能指标
7. **安全配置**: 生产环境启用HTTPS和安全认证
8. **日志记录**: 确保请求日志完整记录 