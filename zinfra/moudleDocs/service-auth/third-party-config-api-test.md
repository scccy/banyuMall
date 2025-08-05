# 第三方平台配置管理 API 测试文档

## 概述
本文档描述了第三方平台配置管理功能的API接口测试用例，包括企业微信、钉钉、飞书等第三方平台的配置管理。

## 基础信息
- **服务名称**: Auth Service
- **基础路径**: `/service/auth/third-party/config`
- **作者**: scccy
- **创建时间**: 2025-08-05

## API 接口列表

### 1. 创建第三方平台配置

**接口地址**: `POST /service/auth/third-party/config`

**请求示例**:
```bash
curl -X POST "http://localhost:8080/service/auth/third-party/config" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-001" \
  -d '{
    "platformType": 1,
    "platformName": "企业微信",
    "corpId": "ww1234567890abcdef",
    "corpSecret": "corp_secret_example",
    "token": "token_example",
    "encodingAesKey": "encoding_aes_key_example",
    "echoStr": "echo_str_example",
    "appId": "app_id_example",
    "appSecret": "app_secret_example",
    "accessToken": "access_token_example",
    "webhookUrl": "https://webhook.example.com/wechatwork",
    "callbackUrl": "https://callback.example.com/wechatwork",
    "configStatus": 1,
    "remark": "企业微信平台配置"
  }'
```

**响应示例**:
```json
{
  "code": 200,
  "message": "第三方平台配置创建成功",
  "data": {
    "configId": "config_001",
    "platformType": "wechatwork",
    "platformName": "企业微信",
    "corpId": "ww1234567890abcdef",
    "corpSecret": "corp_secret_example",
    "token": "token_example",
    "encodingAesKey": "encoding_aes_key_example",
    "echoStr": "echo_str_example",
    "appId": "app_id_example",
    "appSecret": "app_secret_example",
    "accessToken": "access_token_example",
    "webhookUrl": "https://webhook.example.com/wechatwork",
    "callbackUrl": "https://callback.example.com/wechatwork",
    "configStatus": 1,
    "remark": "企业微信平台配置",
    "createdTime": "2025-08-05T10:00:00",
    "updatedTime": "2025-08-05T10:00:00",
    "createdBy": "admin",
    "updatedBy": "admin"
  }
}
```

### 2. 更新第三方平台配置

**接口地址**: `PUT /service/auth/third-party/config/{configId}`

**请求示例**:
```bash
curl -X PUT "http://localhost:8080/service/auth/third-party/config/1" \
  -H "Content-Type: application/json" \
  -H "X-Request-ID: test-request-002" \
  -d '{
    "platformType": 1,
    "platformName": "企业微信(更新)",
    "corpId": "ww1234567890abcdef",
    "corpSecret": "new_corp_secret",
    "token": "new_token",
    "encodingAesKey": "new_encoding_aes_key",
    "echoStr": "new_echo_str",
    "appId": "new_app_id",
    "appSecret": "new_app_secret",
    "accessToken": "new_access_token",
    "webhookUrl": "https://new-webhook.example.com/wechatwork",
    "callbackUrl": "https://new-callback.example.com/wechatwork",
    "configStatus": 1,
    "remark": "企业微信平台配置(已更新)"
  }'
```

### 3. 删除第三方平台配置

**接口地址**: `DELETE /service/auth/third-party/config/{configId}`

**请求示例**:
```bash
curl -X DELETE "http://localhost:8080/service/auth/third-party/config/1" \
  -H "X-Request-ID: test-request-003"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "第三方平台配置删除成功",
  "data": true
}
```

### 4. 查询第三方平台配置详情

**接口地址**: `GET /service/auth/third-party/config/{configId}`

**请求示例**:
```bash
curl -X GET "http://localhost:8080/service/auth/third-party/config/1" \
  -H "X-Request-ID: test-request-004"
```

### 5. 根据平台类型查询配置

**接口地址**: `GET /service/auth/third-party/config/platform/{platformType}`

**请求示例**:
```bash
curl -X GET "http://localhost:8080/service/auth/third-party/config/platform/1" \
  -H "X-Request-ID: test-request-005"
```

### 6. 分页查询第三方平台配置列表

**接口地址**: `GET /service/auth/third-party/config/list`

**请求示例**:
```bash
curl -X GET "http://localhost:8080/service/auth/third-party/config/list?current=1&size=10&platformType=1&configStatus=1" \
  -H "X-Request-ID: test-request-006"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "configId": "config_001",
        "platformType": 1,
        "platformName": "企业微信",
        "corpId": "ww1234567890abcdef",
        "configStatus": 1,
        "createdTime": "2025-08-05T10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 7. 更新配置状态

**接口地址**: `PUT /service/auth/third-party/config/{configId}/status`

**请求示例**:
```bash
curl -X PUT "http://localhost:8080/service/auth/third-party/config/1/status?status=0" \
  -H "X-Request-ID: test-request-007"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "第三方平台配置禁用成功",
  "data": true
}
```

### 8. 健康检查

**接口地址**: `GET /service/auth/third-party/config/test`

**请求示例**:
```bash
curl -X GET "http://localhost:8080/service/auth/third-party/config/test" \
  -H "X-Request-ID: test-request-008"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "Third Party Config Service is running!",
  "data": "Third Party Config Service is running!"
}
```

## 测试场景

### 场景1: 企业微信配置管理
1. 创建企业微信配置
2. 查询企业微信配置
3. 更新企业微信配置
4. 禁用企业微信配置
5. 重新启用企业微信配置

### 场景2: 钉钉配置管理
1. 创建钉钉配置
2. 查询钉钉配置
3. 更新钉钉配置
4. 删除钉钉配置

### 场景3: 配置列表管理
1. 分页查询所有配置
2. 按平台类型筛选
3. 按状态筛选
4. 按平台名称模糊搜索

### 场景4: 异常处理测试
1. 创建重复平台类型配置（应失败）
2. 更新不存在的配置（应失败）
3. 删除不存在的配置（应失败）
4. 查询不存在的配置（应返回空）

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 1001 | 配置不存在 |
| 1002 | 平台类型已存在 |
| 1003 | 参数验证失败 |
| 1004 | 配置状态更新失败 |
| 1005 | 配置删除失败 |

## 注意事项

1. **安全性**: 敏感字段如 `corpSecret`、`appSecret` 等在生产环境中建议加密存储
2. **唯一性**: 平台类型必须唯一，不能重复创建相同类型的配置
3. **状态管理**: 只有启用状态的配置才能被正常查询和使用
4. **数据完整性**: 删除操作为逻辑删除，数据不会物理删除
5. **审计日志**: 所有操作都会记录创建人和更新人信息

## 依赖说明

- **Feign**: 否
- **数据库**: MySQL
- **缓存**: 无
- **消息队列**: 无 