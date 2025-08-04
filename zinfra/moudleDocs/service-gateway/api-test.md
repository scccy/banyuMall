# Service Gateway API 测试文档

## 📋 测试概述

**模块名称**: service-gateway  
**测试类型**: API功能测试  
**测试环境**: 开发环境  
**测试工具**: Postman / curl  
**作者**: scccy  
**创建时间**: 2025-01-27  

### 测试目标
- 验证网关路由转发功能
- 验证全局过滤器功能
- 验证链路追踪功能
- 验证异常处理功能
- 验证跨域处理功能

## 🏗️ 测试环境准备

### 服务启动
```bash
# 启动网关服务
cd service/service-gateway
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 启动依赖服务
# service-auth: 8081
# service-user: 8082
```

### 测试基础信息
- **网关地址**: http://localhost:8080
- **认证服务**: http://localhost:8081
- **用户服务**: http://localhost:8082

## 🔧 路由转发测试

### 1. 认证服务路由测试

#### 测试用例 1.1: 认证服务路由转发
**测试目标**: 验证 `/auth/**` 路径正确转发到认证服务

**请求信息**:
```http
GET http://localhost:8080/auth/health
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "认证服务运行正常",
  "data": {
    "service": "service-auth",
    "status": "UP",
    "timestamp": "2025-01-27T14:30:00"
  }
}
```

**测试步骤**:
1. 发送请求到网关
2. 验证请求被正确转发到认证服务
3. 验证响应头中包含网关标识
4. 验证响应内容正确

**验证要点**:
- [ ] 请求成功转发到认证服务
- [ ] 响应头包含 `X-Gateway-Source: service-gateway`
- [ ] 响应头包含 `X-Service-Name: service-auth`
- [ ] 响应状态码为 200

#### 测试用例 1.2: 认证服务登录接口
**测试目标**: 验证登录接口通过网关转发

**请求信息**:
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "testpass"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "userId": 1,
      "username": "testuser",
      "userType": 1
    }
  }
}
```

### 2. 用户服务路由测试

#### 测试用例 2.1: 用户服务路由转发
**测试目标**: 验证 `/service/user/**` 路径正确转发到用户服务

**请求信息**:
```http
GET http://localhost:8080/service/user/profile
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**预期响应**:
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "userType": 1,
    "status": 1
  }
}
```

**验证要点**:
- [ ] 请求成功转发到用户服务
- [ ] 响应头包含 `X-Gateway-Source: service-gateway`
- [ ] 响应头包含 `X-Service-Name: service-user`
- [ ] 响应状态码为 200

#### 测试用例 2.2: 用户注册接口
**测试目标**: 验证用户注册接口通过网关转发

**请求信息**:
```http
POST http://localhost:8080/service/user/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "newpass123",
  "email": "newuser@example.com",
  "phone": "13900139000"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "用户注册成功",
  "data": {
    "userId": 2,
    "username": "newuser",
    "email": "newuser@example.com",
    "phone": "13900139000"
  }
}
```

## 🔍 链路追踪测试

### 测试用例 3.1: 请求ID生成
**测试目标**: 验证每个请求都生成唯一的请求ID

**请求信息**:
```http
GET http://localhost:8080/auth/health
Content-Type: application/json
```

**验证要点**:
- [ ] 响应头包含 `X-Request-ID`
- [ ] 请求ID格式正确 (req-timestamp-uuid)
- [ ] 每次请求的ID都不同

### 测试用例 3.2: 链路追踪头传递
**测试目标**: 验证链路追踪头正确传递给下游服务

**请求信息**:
```http
GET http://localhost:8080/service/user/profile
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**验证要点**:
- [ ] 响应头包含 `X-Request-ID`
- [ ] 响应头包含 `X-Client-IP`
- [ ] 响应头包含 `X-User-Agent`
- [ ] 响应头包含 `X-Request-Time`
- [ ] 响应头包含 `X-Service-Name`

### 测试用例 3.3: 客户端IP获取
**测试目标**: 验证正确获取客户端真实IP

**请求信息**:
```http
GET http://localhost:8080/auth/health
Content-Type: application/json
X-Forwarded-For: 192.168.1.100
X-Real-IP: 192.168.1.100
```

**验证要点**:
- [ ] 响应头 `X-Client-IP` 为 `192.168.1.100`
- [ ] 优先使用 `X-Forwarded-For`
- [ ] 其次使用 `X-Real-IP`
- [ ] 最后使用远程地址

## 🚨 异常处理测试

### 测试用例 4.1: 路由不存在异常
**测试目标**: 验证访问不存在的路由时返回正确错误信息

**请求信息**:
```http
GET http://localhost:8080/not-exist/path
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 404,
  "message": "路由不存在",
  "data": null,
  "timestamp": "2025-01-27T14:30:00"
}
```

**验证要点**:
- [ ] 响应状态码为 404
- [ ] 响应格式为 ResultData
- [ ] 错误信息友好

### 测试用例 4.2: 下游服务不可用
**测试目标**: 验证下游服务不可用时返回正确错误信息

**前提条件**: 关闭认证服务

**请求信息**:
```http
GET http://localhost:8080/auth/health
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 503,
  "message": "服务暂时不可用，请稍后重试",
  "data": null,
  "timestamp": "2025-01-27T14:30:00"
}
```

**验证要点**:
- [ ] 响应状态码为 503
- [ ] 响应格式为 ResultData
- [ ] 错误信息友好

### 测试用例 4.3: 请求超时
**测试目标**: 验证请求超时时返回正确错误信息

**请求信息**:
```http
GET http://localhost:8080/service/user/slow-operation
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 504,
  "message": "请求超时，请稍后重试",
  "data": null,
  "timestamp": "2025-01-27T14:30:00"
}
```

## 🌐 跨域处理测试

### 测试用例 5.1: CORS预检请求
**测试目标**: 验证OPTIONS预检请求正确处理

**请求信息**:
```http
OPTIONS http://localhost:8080/auth/login
Origin: http://localhost:3000
Access-Control-Request-Method: POST
Access-Control-Request-Headers: Content-Type
```

**预期响应**:
```http
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: POST
Access-Control-Allow-Headers: Content-Type
Access-Control-Allow-Credentials: true
```

**验证要点**:
- [ ] 响应状态码为 200
- [ ] 包含正确的CORS头
- [ ] 支持凭证

### 测试用例 5.2: 跨域POST请求
**测试目标**: 验证跨域POST请求正确处理

**请求信息**:
```http
POST http://localhost:8080/auth/login
Origin: http://localhost:3000
Content-Type: application/json

{
  "username": "testuser",
  "password": "testpass"
}
```

**预期响应**:
```http
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Credentials: true
```

**验证要点**:
- [ ] 请求成功处理
- [ ] 包含正确的CORS头
- [ ] 响应内容正确

## 📊 性能测试

### 测试用例 6.1: 并发请求测试
**测试目标**: 验证网关在高并发下的性能

**测试工具**: Apache Bench (ab)

**测试命令**:
```bash
# 1000个请求，100个并发
ab -n 1000 -c 100 http://localhost:8080/auth/health
```

**预期结果**:
- 平均响应时间 < 100ms
- 95%响应时间 < 200ms
- 错误率 < 1%

### 测试用例 6.2: 响应时间测试
**测试目标**: 验证网关响应时间

**测试步骤**:
1. 发送100个请求
2. 记录每个请求的响应时间
3. 计算平均响应时间

**预期结果**:
- 平均响应时间 < 50ms
- 最大响应时间 < 200ms

## 🔧 日志验证测试

### 测试用例 7.1: 请求日志验证
**测试目标**: 验证请求日志正确记录

**测试步骤**:
1. 发送测试请求
2. 检查网关日志文件
3. 验证日志内容

**预期日志格式**:
```
2025-01-27 14:30:00 [reactor-http-nio-2] INFO  c.o.g.filter.GlobalFilter - Gateway Request - RequestId: req-1706344200000-abc12345, Time: 2025-01-27T14:30:00, Method: GET, Path: /auth/health, ClientIP: 127.0.0.1, UserAgent: PostmanRuntime/7.32.3
```

**验证要点**:
- [ ] 包含请求ID
- [ ] 包含请求时间
- [ ] 包含请求方法和路径
- [ ] 包含客户端IP和User-Agent

### 测试用例 7.2: 响应日志验证
**测试目标**: 验证响应日志正确记录

**预期日志格式**:
```
2025-01-27 14:30:00 [reactor-http-nio-2] INFO  c.o.g.filter.GlobalFilter - Gateway Response - RequestId: req-1706344200000-abc12345, Time: 2025-01-27T14:30:00, Method: GET, Path: /auth/health, Status: 200, Duration: 45ms
```

**验证要点**:
- [ ] 包含请求ID
- [ ] 包含响应时间
- [ ] 包含响应状态码
- [ ] 包含处理时长

## 📋 测试检查清单

### 路由功能
- [ ] 认证服务路由 `/auth/**` 正确转发
- [ ] 用户服务路由 `/service/user/**` 正确转发
- [ ] 不存在的路由返回404错误
- [ ] 路由转发时添加正确的请求头

### 链路追踪
- [ ] 每个请求生成唯一请求ID
- [ ] 链路追踪头正确传递给下游服务
- [ ] 客户端IP正确获取
- [ ] 请求和响应日志完整记录

### 异常处理
- [ ] 路由不存在异常正确处理
- [ ] 下游服务不可用异常正确处理
- [ ] 请求超时异常正确处理
- [ ] 异常响应格式统一

### 跨域处理
- [ ] OPTIONS预检请求正确处理
- [ ] 跨域POST请求正确处理
- [ ] CORS头正确设置
- [ ] 支持携带凭证

### 性能表现
- [ ] 平均响应时间 < 100ms
- [ ] 并发处理能力 > 1000 QPS
- [ ] 错误率 < 1%
- [ ] 内存使用稳定

## 🚀 自动化测试脚本

### Postman Collection
```json
{
  "info": {
    "name": "Service Gateway API Tests",
    "description": "网关服务API测试集合"
  },
  "item": [
    {
      "name": "路由转发测试",
      "item": [
        {
          "name": "认证服务路由",
          "request": {
            "method": "GET",
            "url": "http://localhost:8080/auth/health"
          }
        },
        {
          "name": "用户服务路由",
          "request": {
            "method": "GET",
            "url": "http://localhost:8080/service/user/profile"
          }
        }
      ]
    },
    {
      "name": "异常处理测试",
      "item": [
        {
          "name": "路由不存在",
          "request": {
            "method": "GET",
            "url": "http://localhost:8080/not-exist/path"
          }
        }
      ]
    }
  ]
}
```

### curl测试脚本
```bash
#!/bin/bash

# 网关基础测试
echo "=== 网关基础测试 ==="

# 认证服务路由测试
echo "测试认证服务路由..."
curl -X GET http://localhost:8080/auth/health \
  -H "Content-Type: application/json" \
  -w "\n响应时间: %{time_total}s\n状态码: %{http_code}\n"

# 用户服务路由测试
echo "测试用户服务路由..."
curl -X GET http://localhost:8080/service/user/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer test-token" \
  -w "\n响应时间: %{time_total}s\n状态码: %{http_code}\n"

# 异常处理测试
echo "测试异常处理..."
curl -X GET http://localhost:8080/not-exist/path \
  -H "Content-Type: application/json" \
  -w "\n响应时间: %{time_total}s\n状态码: %{http_code}\n"

echo "=== 测试完成 ==="
```

## 🚨 错误码说明

| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 400 | 参数错误 | 请求参数格式错误或缺少必填参数 | 检查请求参数格式和完整性 |
| 401 | 认证失败 | 用户未登录或令牌无效 | 重新登录获取有效令牌 |
| 403 | 权限不足 | 用户没有访问该资源的权限 | 联系管理员分配相应权限 |
| 404 | 资源不存在 | 请求的资源不存在 | 检查资源ID是否正确 |
| 500 | 服务器错误 | 服务器内部错误 | 查看服务器日志，联系技术支持 |
| 502 | 网关错误 | 网关服务错误 | 检查网关配置，联系运维人员 |
| 503 | 服务不可用 | 后端服务不可用 | 稍后重试或使用降级方案 |
| 504 | 网关超时 | 网关请求超时 | 检查网络连接，稍后重试 |

### 网关服务专用错误码
| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 5001 | 路由不存在 | 请求的路由不存在 | 检查请求路径是否正确 |
| 5002 | 下游服务不可用 | 下游服务暂时不可用 | 稍后重试或联系运维人员 |
| 5003 | 请求超时 | 请求处理超时 | 检查网络连接，稍后重试 |
| 5004 | 限流触发 | 请求频率超限 | 降低请求频率，稍后重试 |
| 5005 | 熔断触发 | 服务熔断保护 | 稍后重试或使用降级方案 |
| 5006 | 网关配置错误 | 网关配置错误 | 联系运维人员检查配置 |

## 📝 注意事项

1. **路由配置**: 确保所有路由配置正确且下游服务可用
2. **限流配置**: 合理配置限流参数，避免误杀正常请求
3. **熔断配置**: 合理配置熔断参数，确保服务稳定性
4. **监控告警**: 设置合适的监控告警，及时发现异常
5. **日志记录**: 确保请求和响应日志完整记录
6. **性能优化**: 定期进行性能测试和优化
7. **安全防护**: 加强安全防护，防止恶意攻击
8. **跨域处理**: 正确配置CORS，支持前端跨域请求

---

**文档版本**: v1.0  
**最后更新**: 2025-01-27  
**维护人员**: scccy 