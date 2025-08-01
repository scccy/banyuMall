# core-publisher 模块 API 测试文档

> **文档位置**: `infra/moudleDocs/core-publisher/api-test.md`
> **作者**: scccy
> **创建时间**: 2025-01-27

## 1. 测试环境配置

### 1.1 测试环境信息
- **测试环境**: 开发环境 (dev)
- **服务地址**: http://localhost:8080
- **数据库**: MySQL 8.0 (banyu_mall_dev)
- **Redis**: Redis 6.0
- **认证方式**: JWT Token

### 1.2 测试工具
- **API测试工具**: Postman / Knife4j
- **数据库工具**: MySQL Workbench / Navicat
- **Redis工具**: Redis Desktop Manager

### 1.3 测试数据准备
```sql
-- 测试用户数据
INSERT INTO sys_user (id, username, password, nickname, email, phone, status, create_time, update_time) 
VALUES 
('test_user_001', 'testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '测试用户1', 'test1@example.com', '13800138001', 1, NOW(), NOW()),
('test_user_002', 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '测试用户2', 'test2@example.com', '13800138002', 1, NOW(), NOW()),
('test_publisher_001', 'publisher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '发布者1', 'publisher1@example.com', '13800138003', 1, NOW(), NOW());

-- 测试角色数据
INSERT INTO sys_role (id, role_name, role_code, description, status, create_time, update_time) 
VALUES 
('role_user', '普通用户', 'USER', '普通用户角色', 1, NOW(), NOW()),
('role_publisher', '发布者', 'PUBLISHER', '任务发布者角色', 1, NOW(), NOW()),
('role_reviewer', '审核员', 'REVIEWER', '任务审核员角色', 1, NOW(), NOW());

-- 测试用户角色关联
INSERT INTO sys_user_role (id, user_id, role_id, create_time, update_time) 
VALUES 
('ur_001', 'test_user_001', 'role_user', NOW(), NOW()),
('ur_002', 'test_user_002', 'role_user', NOW(), NOW()),
('ur_003', 'test_publisher_001', 'role_publisher', NOW(), NOW());
```

## 2. 认证测试

### 2.1 用户登录获取Token
**接口**: `POST /auth/login`

**请求参数**:
```json
{
  "username": "testuser1",
  "password": "123456"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": "test_user_001",
      "username": "testuser1",
      "nickname": "测试用户1",
      "email": "test1@example.com"
    }
  }
}
```

**测试步骤**:
1. 使用测试用户账号登录
2. 获取JWT Token
3. 验证Token格式和有效期

## 3. 任务管理接口测试

### 3.1 创建任务测试

#### 3.1.1 创建点赞任务
**接口**: `POST /core-publisher/tasks`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "taskName": "测试点赞任务",
  "taskType": "LIKE",
  "taskDescription": "这是一个测试点赞任务，完成点赞即可获得积分奖励",
  "rewardAmount": 10.00,
  "publisherId": "test_publisher_001",
  "startTime": "2025-01-27T10:00:00",
  "endTime": "2025-01-30T18:00:00",
  "maxParticipants": 100,
  "targetUrl": "https://example.com/test-post",
  "likeCount": 1,
  "commentRequired": false
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "task_001",
    "taskName": "测试点赞任务",
    "taskType": "LIKE",
    "taskDescription": "这是一个测试点赞任务，完成点赞即可获得积分奖励",
    "rewardAmount": 10.00,
    "publisherId": "test_publisher_001",
    "status": "DRAFT",
    "startTime": "2025-01-27T10:00:00",
    "endTime": "2025-01-30T18:00:00",
    "maxParticipants": 100,
    "currentParticipants": 0,
    "createTime": "2025-01-27T09:30:00",
    "updateTime": "2025-01-27T09:30:00"
  }
}
```

**测试步骤**:
1. 使用发布者账号登录获取Token
2. 发送创建任务请求
3. 验证响应状态码和数据格式
4. 检查数据库中的任务记录

#### 3.1.2 创建评论任务
**请求参数**:
```json
{
  "taskName": "测试评论任务",
  "taskType": "COMMENT",
  "taskDescription": "这是一个测试评论任务，完成评论即可获得积分奖励",
  "rewardAmount": 15.00,
  "publisherId": "test_publisher_001",
  "startTime": "2025-01-27T10:00:00",
  "endTime": "2025-01-30T18:00:00",
  "maxParticipants": 50,
  "targetUrl": "https://example.com/test-post",
  "commentTemplate": "这是一条测试评论",
  "minCommentLength": 10,
  "maxCommentLength": 200,
  "commentCount": 1
}
```

#### 3.1.3 创建社群分享任务
**请求参数**:
```json
{
  "taskName": "测试社群分享任务",
  "taskType": "SHARE",
  "taskDescription": "这是一个测试社群分享任务，完成分享即可获得积分奖励",
  "rewardAmount": 20.00,
  "publisherId": "test_publisher_001",
  "startTime": "2025-01-27T10:00:00",
  "endTime": "2025-01-30T18:00:00",
  "maxParticipants": 30,
  "shareContent": "分享内容模板",
  "sharePlatform": "WECHAT",
  "shareUrl": "https://example.com/share-content",
  "screenshotRequired": true,
  "shareCount": 1
}
```

### 3.2 更新任务测试
**接口**: `PUT /core-publisher/tasks/{id}`

**请求参数**:
```json
{
  "taskName": "更新后的点赞任务",
  "taskType": "LIKE",
  "taskDescription": "这是更新后的测试点赞任务",
  "rewardAmount": 12.00,
  "publisherId": "test_publisher_001",
  "startTime": "2025-01-27T10:00:00",
  "endTime": "2025-01-30T18:00:00",
  "maxParticipants": 120,
  "targetUrl": "https://example.com/updated-post",
  "likeCount": 1,
  "commentRequired": false
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "task_001",
    "taskName": "更新后的点赞任务",
    "taskDescription": "这是更新后的测试点赞任务",
    "rewardAmount": 12.00,
    "maxParticipants": 120,
    "updateTime": "2025-01-27T10:30:00"
  }
}
```

### 3.3 删除任务测试
**接口**: `DELETE /core-publisher/tasks/{id}`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**测试步骤**:
1. 删除指定任务
2. 验证响应状态
3. 检查数据库中任务是否被标记为删除

### 3.4 获取任务详情测试
**接口**: `GET /core-publisher/tasks/{id}`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "task_001",
    "taskName": "测试点赞任务",
    "taskType": "LIKE",
    "taskDescription": "这是一个测试点赞任务，完成点赞即可获得积分奖励",
    "rewardAmount": 10.00,
    "publisherId": "test_publisher_001",
    "status": "DRAFT",
    "startTime": "2025-01-27T10:00:00",
    "endTime": "2025-01-30T18:00:00",
    "maxParticipants": 100,
    "currentParticipants": 0,
    "createTime": "2025-01-27T09:30:00",
    "updateTime": "2025-01-27T09:30:00"
  }
}
```

### 3.5 分页查询任务列表测试
**接口**: `GET /core-publisher/tasks`

**查询参数**:
```
pageNum=1&pageSize=10&taskType=LIKE&status=DRAFT
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "task_001",
        "taskName": "测试点赞任务",
        "taskType": "LIKE",
        "rewardAmount": 10.00,
        "status": "DRAFT",
        "maxParticipants": 100,
        "currentParticipants": 0,
        "createTime": "2025-01-27T09:30:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

## 4. 任务审核接口测试

### 4.1 提交任务审核测试
**接口**: `POST /core-publisher/tasks/{id}/submit`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**测试步骤**:
1. 提交任务进行审核
2. 验证任务状态变更为"已提交"
3. 检查审核记录

### 4.2 审核任务测试
**接口**: `POST /core-publisher/tasks/{id}/review`

**请求参数**:
```json
{
  "reviewStatus": "APPROVED",
  "reviewComment": "审核通过，任务内容符合要求",
  "reviewerId": "test_reviewer_001"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**测试步骤**:
1. 使用审核员账号登录
2. 审核任务
3. 验证任务状态变更为"已审核"
4. 检查审核历史记录

### 4.3 获取审核历史测试
**接口**: `GET /core-publisher/tasks/{id}/review-history`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "review_001",
      "taskId": "task_001",
      "reviewerId": "test_reviewer_001",
      "reviewStatus": "APPROVED",
      "reviewComment": "审核通过，任务内容符合要求",
      "reviewTime": "2025-01-27T11:00:00"
    }
  ]
}
```

## 5. 社群分享审核接口测试

### 5.1 提交社群分享审核测试
**接口**: `POST /core-publisher/share-reviews`

**请求参数**:
```json
{
  "taskId": "task_003",
  "shareContent": "分享内容",
  "sharePlatform": "WECHAT",
  "shareUrl": "https://example.com/share-content",
  "screenshotUrl": "https://oss.example.com/screenshot.jpg"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "share_review_001",
    "taskId": "task_003",
    "shareContent": "分享内容",
    "sharePlatform": "WECHAT",
    "shareUrl": "https://example.com/share-content",
    "screenshotUrl": "https://oss.example.com/screenshot.jpg",
    "reviewStatus": "PENDING",
    "createTime": "2025-01-27T12:00:00"
  }
}
```

### 5.2 审核社群分享测试
**接口**: `PUT /core-publisher/share-reviews/{id}/review`

**请求参数**:
```json
{
  "reviewStatus": "APPROVED",
  "reviewComment": "分享内容符合要求，审核通过",
  "reviewerId": "test_reviewer_001"
}
```

### 5.3 查询社群分享审核列表测试
**接口**: `GET /core-publisher/share-reviews`

**查询参数**:
```
pageNum=1&pageSize=10&reviewStatus=PENDING
```



## 7. 异常情况测试

### 7.1 权限不足测试
**测试场景**: 普通用户尝试创建任务

**预期响应**:
```json
{
  "code": 403,
  "message": "权限不足",
  "data": null
}
```

### 7.2 参数验证失败测试
**测试场景**: 创建任务时缺少必填参数

**请求参数**:
```json
{
  "taskName": "",
  "taskType": "INVALID_TYPE"
}
```

**预期响应**:
```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": {
    "taskName": "任务名称不能为空",
    "taskType": "无效的任务类型"
  }
}
```

### 7.3 资源不存在测试
**测试场景**: 访问不存在的任务

**接口**: `GET /core-publisher/tasks/non-existent-id`

**预期响应**:
```json
{
  "code": 404,
  "message": "任务不存在",
  "data": null
}
```

### 7.4 业务逻辑错误测试
**测试场景**: 重复提交审核

**预期响应**:
```json
{
  "code": 400,
  "message": "任务已提交审核，不能重复提交",
  "data": null
}
```

## 8. 性能测试

### 8.1 接口响应时间测试
**测试目标**: 接口响应时间 < 500ms

**测试方法**:
1. 使用JMeter或Postman进行并发测试
2. 测试不同并发用户数下的响应时间
3. 记录平均响应时间、最大响应时间、最小响应时间

### 8.2 并发处理能力测试
**测试目标**: 支持100个并发用户

**测试场景**:
1. 100个用户同时查询任务列表
2. 100个用户同时提交任务审核
3. 100个用户同时查询审核历史

### 8.3 数据库性能测试
**测试目标**: 数据库查询时间 < 100ms

**测试方法**:
1. 测试大数据量下的分页查询性能
2. 测试复杂条件查询性能
3. 测试关联查询性能

## 9. 安全测试

### 9.1 SQL注入测试
**测试场景**: 在查询参数中注入SQL语句

**测试参数**:
```
taskName=test' OR '1'='1
```

**预期结果**: 应该被参数验证拦截或安全转义

### 9.2 XSS攻击测试
**测试场景**: 在任务描述中注入JavaScript代码

**测试参数**:
```json
{
  "taskDescription": "<script>alert('XSS')</script>"
}
```

**预期结果**: 应该被内容过滤或安全转义

### 9.3 越权访问测试
**测试场景**: 用户尝试访问其他用户的任务

**测试方法**:
1. 用户A创建任务
2. 用户B尝试修改用户A的任务
3. 验证权限控制是否生效

## 10. 测试报告模板

### 10.1 功能测试报告
```
测试模块: core-publisher
测试时间: 2025-01-27
测试人员: scccy

功能测试结果:
✅ 任务创建功能 - 通过
✅ 任务更新功能 - 通过
✅ 任务删除功能 - 通过
✅ 任务查询功能 - 通过
✅ 任务审核功能 - 通过
✅ 社群分享审核功能 - 通过

异常测试结果:
✅ 权限控制 - 通过
✅ 参数验证 - 通过
✅ 业务逻辑验证 - 通过

总体评价: 功能完整，测试通过
```

### 10.2 性能测试报告
```
性能测试结果:
- 平均响应时间: 150ms
- 最大响应时间: 300ms
- 最小响应时间: 50ms
- 并发用户数: 100
- 成功率: 100%

性能评价: 性能良好，满足要求
```

### 10.3 安全测试报告
```
安全测试结果:
✅ SQL注入防护 - 通过
✅ XSS攻击防护 - 通过
✅ 越权访问防护 - 通过
✅ 认证授权机制 - 通过

安全评价: 安全措施完善
```

## 11. 测试环境清理

### 11.1 测试数据清理
```sql
-- 清理测试任务数据
DELETE FROM publisher_task WHERE publisher_id LIKE 'test_%';

-- 清理测试审核数据
DELETE FROM publisher_task_review WHERE reviewer_id LIKE 'test_%';



-- 清理测试用户数据
DELETE FROM sys_user_role WHERE user_id LIKE 'test_%';
DELETE FROM sys_user WHERE id LIKE 'test_%';

-- 清理测试角色数据
DELETE FROM sys_role WHERE id IN ('role_user', 'role_publisher', 'role_reviewer');
```

### 11.2 缓存清理
```bash
# 清理Redis缓存
redis-cli FLUSHDB
```

## 12. 持续集成测试

### 12.1 自动化测试脚本
```bash
#!/bin/bash

# 运行API测试
echo "开始运行API测试..."

# 启动测试环境
docker-compose -f docker-compose.test.yml up -d

# 等待服务启动
sleep 30

# 运行测试
mvn test -Dtest=CorePublisherApiTest

# 生成测试报告
mvn surefire-report:report

# 清理测试环境
docker-compose -f docker-compose.test.yml down

echo "API测试完成"
```

### 12.2 测试覆盖率要求
- **单元测试覆盖率**: > 80%
- **集成测试覆盖率**: > 70%
- **API测试覆盖率**: > 90%

## 13. 测试维护

### 13.1 测试用例维护
- 定期更新测试用例以适应业务变化
- 及时添加新功能的测试用例
- 删除过时的测试用例

### 13.2 测试数据维护
- 定期更新测试数据
- 确保测试数据的真实性和有效性
- 及时清理测试产生的垃圾数据

### 13.3 测试环境维护
- 定期更新测试环境
- 确保测试环境的稳定性
- 及时修复测试环境问题

## 14. 文件上传接口测试

### 14.1 上传任务二维码测试

#### 14.1.1 正常上传二维码
**接口**: `POST /core-publisher/tasks/{taskId}/upload-file`

**请求头**:
```
Content-Type: multipart/form-data
Authorization: Bearer {token}
```

**请求参数**:
```
fileType: qr_code
file: [选择二维码图片文件]
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "taskId": "task_id_123",
    "fileType": "qr_code",
    "fileUrl": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/qr_code/2025-08-01/550e8400-e29b-41d4-a716-446655440000.jpg",
    "fileSize": 102400,
    "mimeType": "image/jpeg",
    "originalFileName": "qrcode.jpg"
  }
}
```

**测试步骤**:
1. 准备一个有效的二维码图片文件（JPG、PNG或GIF格式）
2. 确保任务ID存在
3. 使用multipart/form-data格式上传文件
4. 验证返回的文件URL是否有效

#### 14.1.2 文件格式错误测试
**请求参数**:
```
fileType: qr_code
file: [选择非图片文件，如PDF]
```

**预期响应**:
```json
{
  "code": 400,
  "message": "不支持的文件格式，仅支持JPG、PNG、GIF格式"
}
```

#### 14.1.3 文件大小超限测试
**请求参数**:
```
fileType: qr_code
file: [选择大于5MB的图片文件]
```

**预期响应**:
```json
{
  "code": 400,
  "message": "文件大小不能超过5MB"
}
```

### 14.2 上传任务头像测试

#### 14.2.1 正常上传任务头像
**请求参数**:
```
fileType: task_avatar
file: [选择头像图片文件]
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "taskId": "task_id_123",
    "fileType": "task_avatar",
    "fileUrl": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/task_avatar/2025-08-01/550e8400-e29b-41d4-a716-446655440001.jpg",
    "fileSize": 51200,
    "mimeType": "image/png",
    "originalFileName": "avatar.png"
  }
}
```

#### 14.2.2 文件类型错误测试
**请求参数**:
```
fileType: invalid_type
file: [选择图片文件]
```

**预期响应**:
```json
{
  "code": 400,
  "message": "不支持的文件类型，仅支持qr_code和task_avatar"
}
```

### 14.3 任务不存在测试
**请求参数**:
```
fileType: qr_code
file: [选择图片文件]
```

**预期响应**:
```json
{
  "code": 404,
  "message": "任务不存在"
}
```

### 14.4 使用curl命令测试
```bash
# 上传任务二维码
curl -X POST http://localhost:8080/core-publisher/tasks/task_id_123/upload-file \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -F "fileType=qr_code" \
  -F "file=@/path/to/qrcode.jpg"

# 上传任务头像
curl -X POST http://localhost:8080/core-publisher/tasks/task_id_123/upload-file \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -F "fileType=task_avatar" \
  -F "file=@/path/to/avatar.jpg"
```

### 14.5 使用Postman测试
1. **Method**: POST
2. **URL**: `http://localhost:8080/core-publisher/tasks/{taskId}/upload-file`
3. **Headers**: 
   - Authorization: Bearer {token}
4. **Body**: form-data
   - Key: fileType, Value: qr_code 或 task_avatar
   - Key: file, Type: File, Value: 选择图片文件

### 14.6 测试验证要点
1. **文件上传成功**: 验证文件是否成功上传到OSS
2. **URL保存正确**: 验证任务实体中的URL字段是否正确更新
3. **文件访问正常**: 验证返回的URL是否可以正常访问
4. **错误处理完善**: 验证各种异常情况的错误处理
5. **权限控制**: 验证只有任务发布者才能上传文件 