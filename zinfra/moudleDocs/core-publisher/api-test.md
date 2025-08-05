# Core Publisher API 测试文档

## 📋 测试概述

**模块名称**: core-publisher  
**测试类型**: API功能测试  
**测试环境**: 开发环境  
**测试工具**: Postman / curl  
**作者**: scccy  
**创建时间**: 2025-07-31  

### 测试目标
- 验证任务管理功能
- 验证任务完成管理功能
- 验证社群分享审核功能
- 验证文件上传功能
- 验证企业微信回调功能

## 🏗️ 测试环境准备

### 服务启动
```bash
# 启动核心发布者服务
cd core/core-publisher
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 启动依赖服务
# MySQL: 3306
# OSS服务: 8084
```

### 测试基础信息
- **服务地址**: http://localhost:8083
- **数据库**: MySQL (publisher)
- **OSS服务**: http://localhost:8084

## 🔧 任务管理测试

### 1. 任务创建测试

#### 测试用例 1.1: 创建点赞任务
**测试目标**: 验证点赞任务创建功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks
Content-Type: application/json

{
  "taskName": "点赞测试任务",
  "taskTypeId": 1,
  "taskDescription": "这是一个点赞测试任务",
  "taskReward": 100.00,
  "taskIconUrl": "http://example.com/icon.png",
  "taskConfig": {
    "targetUrl": "http://example.com/post/123",
    "targetType": "like",
    "minDuration": 30
  }
}
```

#### 测试用例 1.2: 创建评论任务
**测试目标**: 验证评论任务创建功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks
Content-Type: application/json

{
  "taskName": "评论测试任务",
  "taskTypeId": 2,
  "taskDescription": "这是一个评论测试任务",
  "taskReward": 150.00,
  "taskIconUrl": "http://example.com/comment-icon.png",
  "taskConfig": {
    "targetUrl": "http://example.com/post/123",
    "targetType": "comment",
    "minCommentLength": 10,
    "requireScreenshot": true
  }
}
```

#### 测试用例 1.3: 创建社群分享任务
**测试目标**: 验证社群分享任务创建功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks
Content-Type: application/json

{
  "taskName": "社群分享测试任务",
  "taskTypeId": 4,
  "taskDescription": "这是一个社群分享测试任务",
  "taskReward": 200.00,
  "taskIconUrl": "http://example.com/share-icon.png",
  "taskConfig": {
    "taskDescription": "分享到朋友圈或微信群",
    "requireImage": true,
    "minImageCount": 1,
    "requireLink": true,
    "linkUrl": "http://example.com/share/789",
    "sharePlatforms": ["wechat", "weibo", "qq"]
  }
}
```

#### 测试用例 1.4: 创建邀请好友任务
**测试目标**: 验证邀请好友任务创建功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks
Content-Type: application/json

{
  "taskName": "邀请好友测试任务",
  "taskTypeId": 5,
  "taskDescription": "这是一个邀请好友测试任务",
  "taskReward": 300.00,
  "taskIconUrl": "http://example.com/invite-icon.png",
  "taskConfig": {
    "inviteType": "group",
    "rewardInvitee": true,
    "inviteeReward": 50.00,
    "activityRules": "邀请好友入群，双方均可获得积分奖励",
    "qrCodeUrl": "http://example.com/qr/group123.png"
  }
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "任务创建成功",
  "data": "task_20250127_001"
}
```

**验证要点**:
- [ ] 响应状态码为 200
- [ ] 返回任务ID
- [ ] 数据库中创建了任务记录
- [ ] 创建了任务详情记录

#### 测试用例 1.2: 创建任务参数验证
**测试目标**: 验证任务创建参数验证

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks
Content-Type: application/json

{
  "taskName": "",
  "taskTypeId": 999,
  "taskReward": -100.00
}
```

**预期响应**:
```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": null
}
```

### 2. 任务查询测试

#### 测试用例 2.1: 获取任务详情
**测试目标**: 验证任务详情查询功能

**请求信息**:
```http
GET http://localhost:8083/core/publisher/tasks/task_20250127_001
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "获取任务详情成功",
  "data": {
    "taskId": "task_20250127_001",
    "taskName": "测试任务",
    "taskTypeId": 1,
    "taskDescription": "这是一个测试任务",
    "taskReward": 100.00,
    "taskIconUrl": "http://example.com/icon.png",
    "statusId": 1,
    "createTime": "2025-07-31T14:30:00"
  }
}
```

#### 测试用例 2.2: 获取任务列表
**测试目标**: 验证任务列表查询功能

**请求信息**:
```http
GET http://localhost:8083/core/publisher/tasks?page=1&size=10&taskTypeId=1&statusId=2
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "获取任务列表成功",
  "data": {
    "records": [
      {
        "taskId": "task_20250127_001",
        "taskName": "测试任务",
        "taskTypeId": 1,
        "taskReward": 100.00,
        "statusId": 2,
        "completionCount": 5
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

### 3. 任务更新测试

#### 测试用例 3.1: 更新任务信息
**测试目标**: 验证任务更新功能

**请求信息**:
```http
PUT http://localhost:8083/core/publisher/tasks/task_20250127_001
Content-Type: application/json

{
  "taskName": "更新后的测试任务",
  "taskDescription": "这是更新后的任务描述",
  "taskReward": 150.00
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "任务更新成功",
  "data": null
}
```

#### 测试用例 3.2: 更新任务状态
**测试目标**: 验证任务状态更新功能

**请求信息**:
```http
PUT http://localhost:8083/core/publisher/tasks/task_20250127_001/status?statusId=2
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "任务状态更新成功",
  "data": null
}
```

### 4. 任务发布测试

#### 测试用例 4.1: 发布任务
**测试目标**: 验证任务发布功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks/task_20250127_001/publish
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "任务发布成功",
  "data": null
}
```

#### 测试用例 4.2: 下架任务
**测试目标**: 验证任务下架功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks/task_20250127_001/unpublish
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "任务下架成功",
  "data": null
}
```

## 🔄 任务完成管理测试

### 1. 任务完成提交测试

#### 测试用例 5.1: 提交任务完成
**测试目标**: 验证任务完成提交功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks/task_20250127_001/complete
Content-Type: application/json

{
  "userId": "user_001",
  "completionContent": "任务已完成，截图已上传",
  "screenshotUrls": ["http://example.com/screenshot1.jpg"],
  "completionTime": "2025-07-31T15:30:00"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "任务完成提交成功",
  "data": "completion_20250127_001"
}
```

#### 测试用例 5.2: 重复提交任务完成
**测试目标**: 验证重复提交任务完成的处理

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks/task_20250127_001/complete
Content-Type: application/json

{
  "userId": "user_001",
  "completionContent": "重复提交",
  "screenshotUrls": []
}
```

**预期响应**:
```json
{
  "code": 409,
  "message": "用户已完成该任务",
  "data": null
}
```

### 2. 任务完成查询测试

#### 测试用例 6.1: 获取任务完成列表
**测试目标**: 验证任务完成列表查询功能

**请求信息**:
```http
GET http://localhost:8083/core/publisher/tasks/task_20250127_001/completions?page=1&size=10
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "获取任务完成列表成功",
  "data": {
    "records": [
      {
        "completionId": "completion_20250127_001",
        "taskId": "task_20250127_001",
        "userId": "user_001",
        "completionStatus": 1,
        "completionContent": "任务已完成，截图已上传",
        "reviewStatus": 1,
        "createTime": "2025-07-31T15:30:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

### 3. 任务完成审核测试

#### 测试用例 7.1: 审核任务完成
**测试目标**: 验证任务完成审核功能

**请求信息**:
```http
PUT http://localhost:8083/core/publisher/completions/completion_20250127_001/review?reviewStatus=2&comment=审核通过
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "任务完成审核成功",
  "data": null
}
```

#### 测试用例 7.2: 拒绝任务完成
**测试目标**: 验证任务完成拒绝功能

**请求信息**:
```http
PUT http://localhost:8083/core/publisher/completions/completion_20250127_001/review?reviewStatus=3&comment=截图不符合要求
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "任务完成审核成功",
  "data": null
}
```

### 4. 任务完成状态检查测试

#### 测试用例 8.1: 检查任务完成状态
**测试目标**: 验证任务完成状态检查功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks/task_20250127_001/check-completion?userId=user_001
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "任务完成状态检查完成",
  "data": null
}
```

## 📝 社群分享审核测试

### 1. 分享审核提交测试

#### 测试用例 9.1: 提交分享审核
**测试目标**: 验证分享审核提交功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/share-reviews
Content-Type: application/json

{
  "userId": "user_001",
  "shareContent": "分享内容测试",
  "shareImages": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
  "sharePlatform": "wechat"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "分享审核提交成功",
  "data": "share_review_20250127_001"
}
```

### 2. 分享审核处理测试

#### 测试用例 10.1: 审核分享内容
**测试目标**: 验证分享内容审核功能

**请求信息**:
```http
PUT http://localhost:8083/core/publisher/share-reviews/share_review_20250127_001?reviewStatus=2&reviewComment=内容符合要求
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "分享审核处理成功",
  "data": null
}
```

### 3. 分享审核查询测试

#### 测试用例 11.1: 获取分享审核列表
**测试目标**: 验证分享审核列表查询功能

**请求信息**:
```http
GET http://localhost:8083/core/publisher/share-reviews?page=1&size=10&reviewStatus=1
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "获取分享审核列表成功",
  "data": {
    "records": [
      {
        "shareReviewId": "share_review_20250127_001",
        "userId": "user_001",
        "shareContent": "分享内容测试",
        "shareImages": ["http://example.com/image1.jpg"],
        "reviewStatus": 1,
        "createTime": "2025-07-31T16:30:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

## 🔗 企业微信回调测试

### 测试用例 12.1: 企业微信回调处理
**测试目标**: 验证企业微信回调处理功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/wechatwork/callback?taskId=task_20250127_001&userId=user_001&status=completed
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "企业微信回调处理成功",
  "data": null
}
```

## 📁 文件上传测试

### 测试用例 13.1: 文件上传集成测试
**测试目标**: 验证文件上传功能

**请求信息**:
```http
POST http://localhost:8083/core/publisher/tasks/task_20250127_001/upload-screenshot
Content-Type: multipart/form-data

file: [选择文件]
```

**预期响应**:
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "fileId": "file_20250127_001",
    "fileUrl": "http://example.com/file_20250127_001.jpg"
  }
}
```

## 📋 测试检查清单

### 任务管理功能
- [ ] 任务创建功能正常
- [ ] 任务查询功能正常
- [ ] 任务更新功能正常
- [ ] 任务删除功能正常
- [ ] 任务状态管理正常
- [ ] 任务发布/下架正常

### 任务完成管理功能
- [ ] 任务完成提交正常
- [ ] 任务完成查询正常
- [ ] 任务完成审核正常
- [ ] 任务完成状态检查正常
- [ ] 重复提交处理正常

### 社群分享审核功能
- [ ] 分享审核提交正常
- [ ] 分享审核处理正常
- [ ] 分享审核查询正常
- [ ] 审核状态更新正常

### 企业微信集成功能
- [ ] 企业微信回调处理正常
- [ ] 回调参数验证正常
- [ ] 状态同步正常

### 文件管理功能
- [ ] 文件上传正常
- [ ] 文件访问正常
- [ ] 文件删除正常

### 异常处理
- [ ] 参数验证异常处理正常
- [ ] 业务逻辑异常处理正常
- [ ] 系统异常处理正常

### 性能表现
- [ ] 接口响应时间 < 500ms
- [ ] 并发处理能力 > 100 QPS
- [ ] 数据库查询性能正常
- [ ] 文件上传性能正常

## 🚀 自动化测试脚本

### Postman Collection
```json
{
  "info": {
    "name": "Core Publisher API Tests",
    "description": "核心发布者服务API测试集合"
  },
  "item": [
    {
      "name": "任务管理",
      "item": [
        {
          "name": "创建任务",
          "request": {
            "method": "POST",
            "url": "http://localhost:8083/core/publisher/tasks",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\"taskName\":\"测试任务\",\"taskTypeId\":1,\"taskDescription\":\"测试任务描述\",\"taskReward\":100.00}"
            }
          }
        },
        {
          "name": "获取任务详情",
          "request": {
            "method": "GET",
            "url": "http://localhost:8083/core/publisher/tasks/{{taskId}}"
          }
        }
      ]
    },
    {
      "name": "任务完成管理",
      "item": [
        {
          "name": "提交任务完成",
          "request": {
            "method": "POST",
            "url": "http://localhost:8083/core/publisher/tasks/{{taskId}}/complete",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\"userId\":\"user_001\",\"completionContent\":\"任务已完成\"}"
            }
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

# 核心发布者服务测试
echo "=== 核心发布者服务测试 ==="

# 创建任务
echo "创建任务..."
TASK_ID=$(curl -X POST http://localhost:8083/core/publisher/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "taskName": "测试任务",
    "taskTypeId": 1,
    "taskDescription": "测试任务描述",
    "taskReward": 100.00
  }' | jq -r '.data')

echo "任务ID: $TASK_ID"

# 获取任务详情
echo "获取任务详情..."
curl -X GET http://localhost:8083/core/publisher/tasks/$TASK_ID \
  -H "Content-Type: application/json"

# 提交任务完成
echo "提交任务完成..."
curl -X POST http://localhost:8083/core/publisher/tasks/$TASK_ID/complete \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user_001",
    "completionContent": "任务已完成"
  }'

echo "=== 测试完成 ==="
```

## 🚨 错误码说明

| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 400 | 参数错误 | 请求参数格式错误或缺少必填参数 | 检查请求参数格式和完整性 |
| 401 | 认证失败 | 用户未登录或令牌无效 | 重新登录获取有效令牌 |
| 403 | 权限不足 | 用户没有访问该资源的权限 | 联系管理员分配相应权限 |
| 404 | 资源不存在 | 请求的资源不存在 | 检查资源ID是否正确 |
| 409 | 资源冲突 | 资源状态不允许操作 | 检查资源当前状态 |
| 413 | 文件过大 | 上传的文件超过大小限制 | 压缩文件或选择较小的文件 |
| 415 | 文件类型不支持 | 上传的文件类型不支持 | 使用支持的文件格式 |
| 500 | 服务器错误 | 服务器内部错误 | 查看服务器日志，联系技术支持 |
| 503 | 服务不可用 | 依赖的服务暂时不可用 | 稍后重试或使用降级方案 |

### 发布者服务专用错误码
| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 2101 | 任务不存在 | 指定的任务不存在 | 检查任务ID是否正确 |
| 2102 | 任务已完成 | 任务已经完成，不能重复操作 | 检查任务当前状态 |
| 2103 | 任务已过期 | 任务已过期，无法操作 | 检查任务有效期 |
| 2104 | 任务状态无效 | 任务状态不允许当前操作 | 检查任务当前状态 |
| 2105 | 任务审核记录不存在 | 指定的审核记录不存在 | 检查审核记录ID是否正确 |
| 2106 | 任务审核状态无效 | 审核状态不允许当前操作 | 检查审核记录当前状态 |
| 2107 | 任务完成记录不存在 | 指定的完成记录不存在 | 检查完成记录ID是否正确 |
| 2108 | 任务配置错误 | 任务配置格式或内容错误 | 检查任务配置格式 |
| 2109 | 任务验证失败 | 任务参数验证失败 | 检查任务参数是否符合要求 |
| 2110 | 任务发布失败 | 任务发布操作失败 | 稍后重试或联系技术支持 |
| 2111 | 任务下架失败 | 任务下架操作失败 | 稍后重试或联系技术支持 |
| 2112 | 任务删除失败 | 任务删除操作失败 | 稍后重试或联系技术支持 |
| 2113 | 任务重复提交 | 任务重复提交，不允许 | 检查是否已经提交过 |

### 文件上传相关错误码
| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 4101 | OSS服务错误 | OSS服务内部错误 | 稍后重试或联系技术支持 |
| 4102 | OSS上传错误 | OSS文件上传失败 | 检查网络连接，稍后重试 |
| 4103 | OSS下载错误 | OSS文件下载失败 | 检查文件是否存在，稍后重试 |
| 4104 | OSS删除错误 | OSS文件删除失败 | 检查文件权限，稍后重试 |
| 4105 | OSS访问被拒绝 | OSS访问权限不足 | 检查OSS访问权限 |
| 4106 | OSS存储桶不存在 | 指定的存储桶不存在 | 检查存储桶配置 |
| 4107 | OSS对象不存在 | 指定的对象不存在 | 检查文件路径是否正确 |
| 4108 | OSS配额超限 | OSS存储配额超限 | 清理不必要的文件或扩容 |

## 📝 注意事项

1. **任务状态管理**: 确保任务状态转换的正确性和一致性
2. **文件上传**: 注意文件大小和格式限制
3. **权限控制**: 确保只有有权限的用户才能操作任务
4. **数据验证**: 严格验证任务参数和配置格式
5. **异常处理**: 合理处理各种异常情况
6. **性能优化**: 注意大量任务查询的性能优化
7. **安全防护**: 防止恶意任务提交和文件上传
8. **监控告警**: 设置合适的监控告警，及时发现异常

---

**文档版本**: v1.0  
**最后更新**: 2025-07-31  
**维护人员**: scccy 