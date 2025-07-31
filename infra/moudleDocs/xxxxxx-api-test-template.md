# {模块名称} 接口测试文档

> **文档位置**: `infra/moudleDocs/{模块名称}/api-test.md`

## 接口列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 测试详情 |
|------|----------|----------|----------|----------|----------|
| 1 | {接口名称1} | {请求方法1} | `{接口路径1}` | {功能描述1} | [查看测试](#11-{接口名称1}) |
| 2 | {接口名称2} | {请求方法2} | `{接口路径2}` | {功能描述2} | [查看测试](#12-{接口名称2}) |
| 3 | {接口名称3} | {请求方法3} | `{接口路径3}` | {功能描述3} | [查看测试](#13-{接口名称3}) |
| 4 | {接口名称4} | {请求方法4} | `{接口路径4}` | {功能描述4} | [查看测试](#21-{接口名称4}) |
| 5 | {接口名称5} | {请求方法5} | `{接口路径5}` | {功能描述5} | [查看测试](#22-{接口名称5}) |
| 6 | {接口名称6} | {请求方法6} | `{接口路径6}` | {功能描述6} | [查看测试](#31-{接口名称6}) |

---

## 环境配置

### 基础信息
- **服务地址**: `http://localhost:{端口号}`
- **认证方式**: JWT Token (Bearer)
- **Content-Type**: `application/json`

### 获取Token
```bash
# 登录获取token
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

## 1. {接口分类1}接口

### 1.1 {接口名称1} {#11-{接口名称1}}
```bash
# {功能描述1}
curl -X {请求方法1} http://localhost:{端口号}/{接口路径1} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "{参数名1}": "{参数值1}",
    "{参数名2}": "{参数值2}",
    "{参数名3}": {参数值3}
  }'

# 示例
curl -X {请求方法1} http://localhost:{端口号}/{接口路径1} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "{参数名1}": "{示例值1}",
    "{参数名2}": "{示例值2}"
  }'
```

### 1.2 {接口名称2} {#12-{接口名称2}}
```bash
# {功能描述2}
curl -X {请求方法2} http://localhost:{端口号}/{接口路径2} \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 示例
curl -X {请求方法2} http://localhost:{端口号}/{接口路径2} \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 1.3 {接口名称3} {#13-{接口名称3}}
```bash
# {功能描述3}
curl -X {请求方法3} http://localhost:{端口号}/{接口路径3} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "{参数名1}": "{参数值1}",
    "{参数名2}": "{参数值2}"
  }'

# 示例
curl -X {请求方法3} http://localhost:{端口号}/{接口路径3} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "{参数名1}": "{示例值1}"
  }'
```

## 2. {接口分类2}接口

### 2.1 {接口名称4} {#21-{接口名称4}}
```bash
# {功能描述4}
curl -X {请求方法4} http://localhost:{端口号}/{接口路径4} \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 示例
curl -X {请求方法4} http://localhost:{端口号}/{接口路径4} \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 2.2 {接口名称5} {#22-{接口名称5}}
```bash
# {功能描述5}
curl -X {请求方法5} http://localhost:{端口号}/{接口路径5} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "{参数名1}": "{参数值1}",
    "{参数名2}": "{参数值2}"
  }'

# 示例
curl -X {请求方法5} http://localhost:{端口号}/{接口路径5} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "{参数名1}": "{示例值1}"
  }'
```

## 3. {接口分类3}接口

### 3.1 {接口名称6} {#31-{接口名称6}}
```bash
# {功能描述6}
curl -X {请求方法6} http://localhost:{端口号}/{接口路径6} \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 示例
curl -X {请求方法6} http://localhost:{端口号}/{接口路径6} \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 4. 错误场景测试

### 4.1 权限不足测试
```bash
# 使用普通用户token访问管理员接口
curl -X {请求方法} http://localhost:{端口号}/{接口路径} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer NORMAL_USER_TOKEN" \
  -d '{
    "{参数名1}": "{参数值1}",
    "{参数名2}": "{参数值2}"
  }'
```

### 4.2 参数验证失败测试
```bash
# {参数名}格式错误
curl -X {请求方法} http://localhost:{端口号}/{接口路径} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "{参数名}": "invalid_format",
    "{参数名2}": "{参数值2}"
  }'

# 必填参数缺失
curl -X {请求方法} http://localhost:{端口号}/{接口路径} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "{参数名2}": "{参数值2}"
  }'
```

### 4.3 资源不存在测试
```bash
# 获取不存在的资源
curl -X GET http://localhost:{端口号}/{接口路径}/nonexistent_id \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 4.4 重复数据测试
```bash
# 创建重复数据
curl -X POST http://localhost:{端口号}/{接口路径} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "{参数名1}": "duplicate_value",
    "{参数名2}": "{参数值2}"
  }'
```

## 5. 性能测试

### 5.1 并发查询测试
```bash
# 使用ab工具进行并发测试
ab -n 1000 -c 10 -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  "http://localhost:{端口号}/{接口路径}?page=1&size=10"

# 使用wrk工具进行压力测试
wrk -t12 -c400 -d30s -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  "http://localhost:{端口号}/{接口路径}?page=1&size=10"
```

### 5.2 大数据量测试
```bash
# 查询大量数据
curl -X GET "http://localhost:{端口号}/{接口路径}?page=1&size=1000" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 6. 测试脚本

### 6.1 完整测试流程脚本
```bash
#!/bin/bash

# 设置变量
BASE_URL="http://localhost:{端口号}"
TOKEN="YOUR_TOKEN_HERE"

echo "开始测试 {模块名称} 接口..."

# 1. {测试步骤1}
echo "1. {测试步骤1描述}"
RESPONSE1=$(curl -s -X {请求方法1} $BASE_URL/{接口路径1} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "{参数名1}": "{参数值1}",
    "{参数名2}": "{参数值2}"
  }')
echo "{测试步骤1描述}响应: $RESPONSE1"

# 2. {测试步骤2}
echo "2. {测试步骤2描述}"
RESPONSE2=$(curl -s -X {请求方法2} "$BASE_URL/{接口路径2}" \
  -H "Authorization: Bearer $TOKEN")
echo "{测试步骤2描述}响应: $RESPONSE2"

# 3. {测试步骤3}
echo "3. {测试步骤3描述}"
RESPONSE3=$(curl -s -X {请求方法3} "$BASE_URL/{接口路径3}" \
  -H "Authorization: Bearer $TOKEN")
echo "{测试步骤3描述}响应: $RESPONSE3"

echo "测试完成！"
```

### 6.2 自动化测试脚本
```bash
#!/bin/bash

# 自动化测试脚本
BASE_URL="http://localhost:{端口号}"
TOKEN="YOUR_TOKEN_HERE"

# 测试函数
test_api() {
    local method=$1
    local url=$2
    local data=$3
    local description=$4
    
    echo "测试: $description"
    
    if [ -n "$data" ]; then
        response=$(curl -s -X $method "$BASE_URL$url" \
          -H "Content-Type: application/json" \
          -H "Authorization: Bearer $TOKEN" \
          -d "$data")
    else
        response=$(curl -s -X $method "$BASE_URL$url" \
          -H "Authorization: Bearer $TOKEN")
    fi
    
    echo "响应: $response"
    echo "---"
}

# 执行测试
test_api "{请求方法1}" "/{接口路径1}" '{"{参数名1}":"{参数值1}","{参数名2}":"{参数值2}"}' "{测试描述1}"
test_api "{请求方法2}" "/{接口路径2}" "" "{测试描述2}"
test_api "{请求方法3}" "/{接口路径3}" "" "{测试描述3}"
```

## 7. 测试数据准备

### 7.1 测试数据
```sql
-- 插入测试数据
INSERT INTO {表名} ({字段名1}, {字段名2}, {字段名3}, create_time) VALUES
('{测试值1}', '{测试值2}', {测试值3}, NOW()),
('{测试值4}', '{测试值5}', {测试值6}, NOW()),
('{测试值7}', '{测试值8}', {测试值9}, NOW());
```

### 7.2 清理测试数据
```sql
-- 清理测试数据
DELETE FROM {表名} WHERE {字段名1} LIKE '{测试前缀}%';
```

## 8. 注意事项

1. **Token获取**: 测试前需要先通过auth服务获取有效的JWT token
2. **权限验证**: 管理员接口需要管理员权限，普通用户接口需要用户权限
3. **数据清理**: 测试完成后建议清理测试数据
4. **环境隔离**: 测试环境与生产环境数据隔离
5. **错误处理**: 注意检查错误响应，确保接口按预期工作
6. **性能考虑**: 大量数据测试时注意系统资源使用情况 