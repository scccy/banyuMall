# CURL 请求示例

## 1. 用户登录

### 基本登录请求
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "123456"
  }'
```

### 带详细输出的登录请求
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: $(uuidgen)" \
  -H "X-Client-IP: 127.0.0.1" \
  -H "X-User-Agent: curl/7.68.0" \
  -d '{
    "username": "test",
    "password": "123456"
  }' \
  -v
```

### 预期响应
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "2",
    "username": "test",
    "nickname": "测试用户",
    "avatar": null,
    "roles": ["ROLE_USER"],
    "permissions": ["task:list:view"],
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJwZXJtaXNzaW9ucyI6IltcInRhc2s6bGlzdDp2aWV3XCJdIiwicm9sZXMiOiJbXCJST0xFX1VTRVJcIl0iLCJ1c2VySWQiOiIyIiwidXNlcm5hbWUiOiJ0ZXN0IiwiaWF0IjoxNzUzOTU0NzMyLCJleHAiOjE3NTM5NTgzMzJ9.ZsihDo1hZAARmYpB5boXDbNOiNPK9-1LObFS16oLKF8",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "timestamp": 1753954732931,
  "success": true,
  "fail": false
}
```

## 2. 用户登出

### 使用登录返回的token进行登出
```bash
# 先登录获取token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "123456"
  }' | jq -r '.data.token')

echo "获取到的token: $TOKEN"

# 使用token进行登出
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### 手动指定token进行登出
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJwZXJtaXNzaW9ucyI6IltcInRhc2s6bGlzdDp2aWV3XCJdIiwicm9sZXMiOiJbXCJST0xFX1VTRVJcIl0iLCJ1c2VySWQiOiIyIiwidXNlcm5hbWUiOiJ0ZXN0IiwiaWF0IjoxNzUzOTU0NzMyLCJleHAiOjE3NTM5NTgzMzJ9.ZsihDo1hZAARmYpB5boXDbNOiNPK9-1LObFS16oLKF8" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: $(uuidgen)" \
  -H "X-Client-IP: 127.0.0.1" \
  -H "X-User-Agent: curl/7.68.0"
```

### 预期响应
```json
{
  "code": 200,
  "message": "登出成功",
  "data": "登出成功",
  "timestamp": 1753954732931,
  "success": true,
  "fail": false
}
```

## 3. 健康检查

### 认证服务健康检查
```bash
curl -X GET http://localhost:8080/auth/test \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: $(uuidgen)" \
  -H "X-Client-IP: 127.0.0.1" \
  -H "X-User-Agent: curl/7.68.0"
```

### 预期响应
```json
{
  "code": 200,
  "message": "Auth Service is running!",
  "data": "Auth Service is running!",
  "timestamp": 1753954732931,
  "success": true,
  "fail": false
}
```

## 4. 管理员强制登出

### 强制登出指定用户
```bash
curl -X POST http://localhost:8080/auth/logout/force/2 \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: $(uuidgen)" \
  -H "X-Client-IP: 127.0.0.1" \
  -H "X-User-Agent: curl/7.68.0"
```

## 5. 完整的测试脚本

### 创建测试脚本
```bash
#!/bin/bash

# 设置基础URL
BASE_URL="http://localhost:8080"
REQUEST_ID=$(uuidgen)

echo "=== JWT认证测试脚本 ==="
echo "请求ID: $REQUEST_ID"
echo ""

# 1. 健康检查
echo "1. 健康检查..."
curl -s -X GET "$BASE_URL/auth/test" \
  -H "X-Request-ID: $REQUEST_ID" \
  -H "X-Client-IP: 127.0.0.1" \
  -H "X-User-Agent: curl/7.68.0" | jq '.'
echo ""

# 2. 用户登录
echo "2. 用户登录..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: $REQUEST_ID" \
  -H "X-Client-IP: 127.0.0.1" \
  -H "X-User-Agent: curl/7.68.0" \
  -d '{
    "username": "test",
    "password": "123456"
  }')

echo "登录响应:"
echo "$LOGIN_RESPONSE" | jq '.'

# 提取token
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.token')
echo ""
echo "提取的token: $TOKEN"
echo ""

# 3. 使用token访问受保护的资源
echo "3. 使用token访问受保护资源..."
curl -s -X GET "$BASE_URL/user/profile" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Request-ID: $REQUEST_ID" \
  -H "X-Client-IP: 127.0.0.1" \
  -H "X-User-Agent: curl/7.68.0" | jq '.'
echo ""

# 4. 用户登出
echo "4. 用户登出..."
LOGOUT_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/logout" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: $REQUEST_ID" \
  -H "X-Client-IP: 127.0.0.1" \
  -H "X-User-Agent: curl/7.68.0")

echo "登出响应:"
echo "$LOGOUT_RESPONSE" | jq '.'
echo ""

# 5. 验证token已失效
echo "5. 验证token已失效..."
curl -s -X GET "$BASE_URL/user/profile" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Request-ID: $REQUEST_ID" \
  -H "X-Client-IP: 127.0.0.1" \
  -H "X-User-Agent: curl/7.68.0" | jq '.'
echo ""

echo "=== 测试完成 ==="
```

### 保存并运行脚本
```bash
# 保存脚本
cat > test_jwt.sh << 'EOF'
#!/bin/bash
# 上面的完整脚本内容
EOF

# 给脚本执行权限
chmod +x test_jwt.sh

# 运行脚本
./test_jwt.sh
```

## 6. 错误情况测试

### 无效用户名密码
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "invalid",
    "password": "wrong"
  }'
```

### 缺少Authorization头
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Content-Type: application/json"
```

### 无效的token格式
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: InvalidToken" \
  -H "Content-Type: application/json"
```

### 已过期的token
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer expired.token.here" \
  -H "Content-Type: application/json"
```

## 7. 环境变量设置

### 设置环境变量便于测试
```bash
# 设置基础URL
export API_BASE_URL="http://localhost:8080"

# 设置测试用户
export TEST_USERNAME="test"
export TEST_PASSWORD="123456"

# 生成请求ID
export REQUEST_ID=$(uuidgen)

# 登录并保存token
export JWT_TOKEN=$(curl -s -X POST "$API_BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$TEST_USERNAME\",
    \"password\": \"$TEST_PASSWORD\"
  }" | jq -r '.data.token')

echo "Token已保存到环境变量: $JWT_TOKEN"
```

### 使用环境变量进行请求
```bash
# 使用保存的token进行登出
curl -X POST "$API_BASE_URL/auth/logout" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: $REQUEST_ID"
```

## 8. 性能测试

### 并发登录测试
```bash
# 使用ab进行并发测试
ab -n 100 -c 10 -p login_data.json -T application/json http://localhost:8080/auth/login
```

### 创建测试数据文件
```bash
cat > login_data.json << EOF
{
  "username": "test",
  "password": "123456"
}
EOF
```

## 9. 调试技巧

### 查看详细请求信息
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "123456"}' \
  -v \
  -w "\n\n时间统计:\n   DNS解析: %{time_namelookup}s\n   连接时间: %{time_connect}s\n   SSL握手: %{time_appconnect}s\n   总时间: %{time_total}s\n"
```

### 保存响应到文件
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "123456"}' \
  -o response.json \
  -s
```

### 格式化JSON输出
```bash
# 使用jq格式化输出
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "123456"}' | jq '.'
```

## 10. 常见问题排查

### 检查服务状态
```bash
# 检查端口是否开放
netstat -tlnp | grep 8080

# 检查服务进程
ps aux | grep java

# 检查日志
tail -f logs/application.log
```

### 网络连接测试
```bash
# 测试端口连通性
telnet localhost 8080

# 使用nc测试
nc -zv localhost 8080
```

## 总结

这些curl示例涵盖了：

1. **基本操作**: 登录、登出、健康检查
2. **错误处理**: 各种错误情况的测试
3. **自动化**: 完整的测试脚本
4. **性能测试**: 并发和压力测试
5. **调试技巧**: 详细的请求信息和格式化输出

使用这些示例可以全面测试JWT认证系统的功能！ 