# 企业微信配置解析功能使用指南

## 概述

本文档介绍如何在 `third-party-wechatWork` 模块中解析 `platformConfig` 字段，提取 `corpId` 和 `corpSecret`，并获取企业微信的 `access_token`。

## 数据结构说明

根据 `/zinfra/查询返回示例.json` 中的数据结构，企业微信配置通过以下API获取：

```
GET /service/auth/tp/config/{configId}
```

返回的数据结构：

```json
{
    "code": 200,
    "message": "查询第三方平台配置成功",
    "data": {
        "configId": 1,
        "platformType": 1,
        "platformName": "企业微信",
        "platformConfig": "{\"appId\": \"\", \"token\": \"\", \"corpId\": \"test1\", \"echoStr\": \"\", \"appSecret\": \"\", \"corpSecret\": \"test2\", \"webhookUrl\": \"https://your.webhook.url\", \"accessToken\": \"\", \"callbackUrl\": \"https://your.callback.url\", \"encodingAesKey\": \"\"}",
        "configStatus": 1,
        "remark": null,
        "createdTime": "2025-08-06T16:54:22",
        "updatedTime": "2025-08-07T11:38:19",
        "createdBy": null,
        "updatedBy": null,
        "deleted": 0
    }
}
```

### platformConfig 字段结构

`platformConfig` 是一个JSON字符串，包含以下企业微信配置信息：

```json
{
    "corpId": "test1",           // 企业ID
    "corpSecret": "test2",       // 企业Secret
    "appId": "",                 // 应用ID
    "appSecret": "",             // 应用Secret
    "token": "",                 // Token
    "encodingAesKey": "",        // 消息加解密密钥
    "echoStr": "",               // 回调验证字符串
    "accessToken": "",           // 访问令牌
    "webhookUrl": "https://your.webhook.url",     // Webhook URL
    "callbackUrl": "https://your.callback.url"   // 回调URL
}
```

## 核心功能类

### 1. WechatWorkConfigService

主要的配置解析服务类，提供以下功能：

- `parseWechatWorkConfig()`: 解析完整的企业微信配置
- `getCorpId()`: 获取企业ID
- `getCorpSecret()`: 获取企业Secret
- `getAccessToken()`: 获取访问令牌

### 2. WechatWorkConfigController

提供REST API接口：

- `GET /wechat-work/config`: 获取完整配置信息
- `GET /wechat-work/config/corp-id`: 获取企业ID
- `GET /wechat-work/config/corp-secret`: 获取企业Secret（脱敏）
- `GET /wechat-work/config/access-token`: 获取访问令牌
- `GET /wechat-work/config/details`: 获取配置详情
- `GET /wechat-work/config/test-connection`: 测试连通性

## 使用示例

### 1. 基本使用

```java
@Autowired
private WechatWorkConfigService configService;

// 解析完整配置
ThirdPartyPlatformConfigDTO.WechatWorkConfig config = configService.parseWechatWorkConfig();

// 获取关键信息
String corpId = configService.getCorpId();
String corpSecret = configService.getCorpSecret();

// 获取访问令牌
String accessToken = configService.getAccessToken();
```

### 2. 通过API接口使用

```bash
# 获取企业ID
curl -X GET "http://localhost:8080/wechat-work/config/corp-id"

# 获取企业Secret（脱敏）
curl -X GET "http://localhost:8080/wechat-work/config/corp-secret"

# 获取访问令牌
curl -X GET "http://localhost:8080/wechat-work/config/access-token"

# 测试连通性
curl -X GET "http://localhost:8080/wechat-work/config/test-connection"
```

### 3. 完整业务流程示例

参考 `WechatWorkConfigExample.java` 中的示例代码：

```java
@Component
public class MyWechatWorkService {
    
    @Autowired
    private WechatWorkConfigService configService;
    
    public void doSomethingWithWechatWork() {
        try {
            // 1. 获取配置信息
            String corpId = configService.getCorpId();
            String corpSecret = configService.getCorpSecret();
            
            // 2. 获取访问令牌
            String accessToken = configService.getAccessToken();
            
            // 3. 使用访问令牌调用企业微信API
            if (accessToken != null) {
                // 调用企业微信API进行业务操作
                callWechatWorkApi(accessToken);
            }
            
        } catch (Exception e) {
            log.error("企业微信操作失败", e);
        }
    }
    
    private void callWechatWorkApi(String accessToken) {
        // 实现具体的企业微信API调用
    }
}
```

## 错误处理

服务类会抛出以下异常：

- `RuntimeException("获取企业微信配置失败")`: 当无法获取配置时
- `RuntimeException("platformConfig配置为空")`: 当配置为空时
- `RuntimeException("解析platformConfig失败")`: 当JSON解析失败时
- `RuntimeException("获取access_token失败")`: 当获取访问令牌失败时

## 测试

运行测试类验证功能：

```bash
# 运行单元测试
mvn test -Dtest=WechatWorkConfigServiceTest

# 运行示例代码
# 在Spring Boot应用中注入WechatWorkConfigExample并调用runAllExamples()方法
```

## 安全注意事项

1. **敏感信息保护**: `corpSecret` 和 `accessToken` 等敏感信息在日志中会被脱敏处理
2. **访问控制**: API接口应该添加适当的权限控制
3. **令牌管理**: `accessToken` 有有效期限制，需要定期刷新
4. **配置验证**: 使用前应验证配置的完整性和有效性

## 依赖关系

本功能依赖以下组件：

- `WechatWorkAuthFeignClient`: 用于获取配置信息
- `ThirdPartyPlatformConfigDTO`: 配置数据传输对象
- `FastJSON`: JSON解析库
- `Spring Boot`: Web框架
- `Lombok`: 代码简化工具

## 扩展说明

如需扩展功能，可以：

1. 在 `WechatWorkConfigService` 中添加新的配置解析方法
2. 在 `WechatWorkConfigController` 中添加新的API接口
3. 在 `ThirdPartyPlatformConfigDTO.WechatWorkConfig` 中添加新的配置字段
4. 更新相应的测试用例

## 相关文档

- [企业微信API文档](https://developer.work.weixin.qq.com/document/)
- [third-party-wechatWork模块README](./README.md)
- [第三方平台配置设计文档](../../docs/third-party-config-iteration-design.md)