# 网关路由调试指南

## 问题描述
网关日志显示访问的是 `/auth/test`，但期望的是 `/auth/login`。

## 调试步骤

### 1. 检查实际请求
```bash
# 使用curl测试，确保请求路径正确
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'

# 检查响应和日志
```

### 2. 检查Nacos配置
登录Nacos控制台，查看 `service-gateway.yaml` 的配置内容：
```yaml
# 检查路由配置是否正确
gateway:
  routes:
    - id: auth-service
      uri: lb://service-auth
      predicates:
        - Path=/auth/**
      filters:
        - StripPrefix=0
```

### 3. 检查网关启动日志
```bash
# 查看网关启动时的路由加载日志
grep "Route" logs/service-gateway.log

# 查看Nacos配置加载日志
grep "Nacos" logs/service-gateway.log
```

### 4. 检查认证服务日志
```bash
# 查看认证服务是否收到请求
grep "Auth" logs/service-auth.log
```

### 5. 使用浏览器开发者工具
1. 打开浏览器开发者工具 (F12)
2. 切换到 Network 标签
3. 发送登录请求
4. 检查实际发送的请求URL

### 6. 检查前端代码
如果使用前端应用，检查：
- 登录请求的URL配置
- 是否有重定向逻辑
- 是否有路由配置问题

## 可能的原因

### 1. 浏览器缓存
```bash
# 清除浏览器缓存
# 或使用无痕模式测试
```

### 2. 前端路由问题
检查前端代码中的API配置：
```javascript
// 检查API基础URL配置
const API_BASE_URL = 'http://localhost:8080';

// 检查登录接口路径
const LOGIN_URL = '/auth/login'; // 确保路径正确
```

### 3. 网关路由配置问题
检查Nacos中的路由配置是否正确：
```yaml
# 正确的配置应该是
gateway:
  routes:
    - id: auth-service
      uri: lb://service-auth
      predicates:
        - Path=/auth/**
      filters:
        - StripPrefix=0  # 不剥离前缀
```

### 4. 服务发现问题
检查服务注册情况：
```bash
# 检查Nacos中的服务注册
# 确认 service-auth 服务已注册
```

## 验证方法

### 1. 测试不同路径
```bash
# 测试登录接口
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'

# 测试健康检查接口
curl http://localhost:8080/auth/test

# 测试用户服务
curl http://localhost:8080/user/test/health
```

### 2. 检查日志输出
观察网关日志中的路径信息：
```
Gateway Debug - Original Path: /auth/login, URI: http://localhost:8080/auth/login
```

### 3. 检查服务响应
确认认证服务正确响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "2",
    "username": "test",
    "token": "..."
  }
}
```

## 常见解决方案

### 1. 清除缓存
- 清除浏览器缓存
- 重启网关服务
- 清除Nacos配置缓存

### 2. 检查配置
- 确认Nacos配置正确
- 确认服务注册正常
- 确认路由配置正确

### 3. 检查网络
- 确认端口访问正常
- 确认服务间通信正常
- 确认负载均衡正常

## 下一步行动

1. **立即检查**: 使用curl测试实际请求路径
2. **查看日志**: 检查网关和认证服务的完整日志
3. **检查配置**: 确认Nacos中的路由配置
4. **前端排查**: 如果使用前端，检查前端代码 