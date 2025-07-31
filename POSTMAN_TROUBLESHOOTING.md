# Postman问题排查指南

## 问题描述
curl测试成功，但Postman请求失败，网关日志显示访问的是 `/auth/test` 而不是 `/auth/login`。

## 问题分析

### 1. curl成功说明
- ✅ 网关路由配置正确
- ✅ 认证服务正常运行
- ✅ 密码验证修复生效
- ✅ JJWT序列化器修复生效

### 2. Postman问题可能原因

#### 2.1 请求配置问题
- **URL配置错误**: Postman中可能配置了错误的URL
- **请求方法错误**: 可能使用了GET而不是POST
- **Content-Type错误**: 可能没有设置正确的Content-Type

#### 2.2 缓存问题
- **Postman缓存**: Postman可能缓存了之前的请求
- **浏览器缓存**: 如果使用Postman Web版本，可能有浏览器缓存

#### 2.3 重定向问题
- **认证重定向**: 可能有认证重定向到测试接口
- **CORS问题**: 可能有跨域重定向

## 排查步骤

### 1. 检查Postman请求配置

#### 1.1 确认请求方法
```
Method: POST (不是GET)
```

#### 1.2 确认URL
```
URL: http://localhost:8080/auth/login
```

#### 1.3 确认Headers
```
Content-Type: application/json
```

#### 1.4 确认Body
```json
{
  "username": "test",
  "password": "123456"
}
```

### 2. 清除Postman缓存

#### 2.1 清除请求历史
1. 在Postman中点击 "History" 标签
2. 清除所有历史记录

#### 2.2 清除缓存
1. 重启Postman应用
2. 或者使用 "Clear All" 功能

### 3. 检查Postman设置

#### 3.1 检查代理设置
1. 打开Postman设置
2. 检查是否有代理配置
3. 确保没有代理影响请求

#### 3.2 检查SSL设置
1. 在Settings > General中
2. 检查SSL证书验证设置
3. 对于localhost，可以关闭SSL验证

### 4. 使用Postman Console调试

#### 4.1 启用Console
1. 在Postman中点击 "Console" 按钮
2. 发送请求
3. 查看详细的请求和响应信息

#### 4.2 检查请求详情
在Console中查看：
- 实际发送的URL
- 请求头信息
- 请求体内容
- 响应状态码
- 响应内容

### 5. 对比curl和Postman

#### 5.1 使用相同的curl命令
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}' \
  -v
```

#### 5.2 在Postman中复制curl
1. 在Postman中点击 "Import" 
2. 选择 "Raw text"
3. 粘贴curl命令
4. 自动生成Postman请求

### 6. 检查网关日志

#### 6.1 查看完整日志
```bash
# 查看网关日志
tail -f logs/service-gateway.log

# 查看认证服务日志
tail -f logs/service-auth.log
```

#### 6.2 对比请求
- curl请求的日志
- Postman请求的日志
- 找出差异

## 常见解决方案

### 1. 重新创建请求
1. 删除现有的Postman请求
2. 重新创建一个新的请求
3. 手动配置所有参数

### 2. 使用Postman Collection
1. 创建一个新的Collection
2. 在Collection中创建请求
3. 确保Collection设置正确

### 3. 检查环境变量
1. 检查Postman环境变量
2. 确保没有变量冲突
3. 使用绝对URL而不是变量

### 4. 使用不同的Postman版本
1. 尝试使用Postman桌面版
2. 或者尝试使用Postman Web版
3. 排除版本问题

## 验证方法

### 1. 成功的curl请求
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

### 2. 期望的Postman配置
- **Method**: POST
- **URL**: http://localhost:8080/auth/login
- **Headers**: Content-Type: application/json
- **Body**: raw JSON
```json
{
  "username": "test",
  "password": "123456"
}
```

### 3. 期望的响应
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

## 下一步行动

1. **立即检查**: Postman中的请求配置
2. **清除缓存**: 重启Postman或清除历史
3. **使用Console**: 查看详细的请求信息
4. **对比日志**: 查看网关日志中的差异

## 如果问题仍然存在

如果按照上述步骤仍然无法解决，请提供：
1. Postman Console中的详细日志
2. 网关日志中的完整请求信息
3. Postman请求的截图 