# core-publisher API接口说明

> **文档位置**: `infra/moudleDocs/core-publisher/API接口说明.md`
> **创建时间**: 2025-08-01
> **作者**: scccy

## 1. 接口概述

### 1.1 模块信息
- **模块名称**: core-publisher（发布者服务）
- **父模块**: core（核心层）
- **模块类型**: 任务发布管理服务
- **基础路径**: `/core/publisher`

### 1.2 接口分类
- **任务管理接口**: 任务的增删改查操作
- **任务审核接口**: 任务审核流程管理
- **社群分享审核接口**: 社群分享内容审核

## 2. 任务管理接口

### 2.1 创建任务
- **接口路径**: `POST /core/publisher/tasks`
- **功能描述**: 创建新的任务
- **是否需要Feign客户端**: 否

**请求参数**:
```json
{
  "title": "string",           // 任务标题（必填）
  "description": "string",     // 任务描述
  "taskType": "integer",       // 任务类型：1-点赞，2-评论，3-讨论，4-分享，5-邀请，6-反馈，7-排行榜
  "reward": "integer",         // 任务奖励积分
  "startTime": "datetime",     // 任务开始时间
  "endTime": "datetime",       // 任务结束时间
  "maxParticipants": "integer", // 最大参与人数
  "targetUrl": "string",       // 目标链接
  "requirements": "string"     // 任务要求
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "任务创建成功",
  "data": {
    "id": "string",
    "title": "string",
    "description": "string",
    "taskType": 1,
    "reward": 100,
    "status": 0,
    "createdTime": "2025-08-01T10:00:00",
    "updatedTime": "2025-08-01T10:00:00"
  }
}
```

### 2.2 更新任务
- **接口路径**: `PUT /core/publisher/tasks/{id}`
- **功能描述**: 更新任务信息
- **是否需要Feign客户端**: 否

**请求参数**: 同创建任务

**响应数据**: 同创建任务

### 2.3 删除任务
- **接口路径**: `DELETE /core/publisher/tasks/{id}`
- **功能描述**: 删除任务
- **是否需要Feign客户端**: 否

**响应数据**:
```json
{
  "code": 200,
  "message": "任务删除成功",
  "data": true
}
```

### 2.4 获取任务详情
- **接口路径**: `GET /core/publisher/tasks/{id}`
- **功能描述**: 获取任务详细信息
- **是否需要Feign客户端**: 否

**响应数据**: 同创建任务

### 2.5 分页查询任务列表
- **接口路径**: `GET /core/publisher/tasks`
- **功能描述**: 分页查询任务列表
- **是否需要Feign客户端**: 否

**请求参数**:
```
page: integer      // 页码，默认1
size: integer      // 每页大小，默认10
title: string      // 任务标题（模糊查询）
taskType: integer  // 任务类型
status: integer    // 任务状态
startTime: datetime // 开始时间
endTime: datetime  // 结束时间
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询任务列表成功",
  "data": {
    "records": [
      {
        "id": "string",
        "title": "string",
        "taskType": 1,
        "reward": 100,
        "status": 0,
        "createdTime": "2025-08-01T10:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

## 3. 任务审核接口

### 3.1 提交任务审核
- **接口路径**: `POST /core/publisher/tasks/{id}/submit`
- **功能描述**: 提交任务进行审核
- **是否需要Feign客户端**: 否

**响应数据**:
```json
{
  "code": 200,
  "message": "任务提交审核成功",
  "data": true
}
```

### 3.2 审核任务
- **接口路径**: `POST /core/publisher/tasks/{id}/review`
- **功能描述**: 审核任务
- **是否需要Feign客户端**: 否

**请求参数**:
```json
{
  "reviewResult": "integer",   // 审核结果：1-通过，2-拒绝
  "reviewComment": "string",   // 审核意见
  "reviewerId": "string"       // 审核人ID
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "任务审核成功",
  "data": true
}
```

### 3.3 获取审核历史
- **接口路径**: `GET /core/publisher/tasks/{id}/review-history`
- **功能描述**: 获取任务审核历史
- **是否需要Feign客户端**: 否

**响应数据**:
```json
{
  "code": 200,
  "message": "获取审核历史成功",
  "data": [
    {
      "id": "string",
      "taskId": "string",
      "reviewResult": 1,
      "reviewComment": "string",
      "reviewerId": "string",
      "reviewTime": "2025-08-01T10:00:00"
    }
  ]
}
```

## 4. 社群分享审核接口

### 4.1 提交社群分享审核
- **接口路径**: `POST /core/publisher/share-reviews`
- **功能描述**: 提交社群分享审核申请
- **是否需要Feign客户端**: 否

**请求参数**:
```json
{
  "taskId": "string",          // 任务ID
  "shareUrl": "string",        // 分享链接
  "shareContent": "string",    // 分享内容
  "sharePlatform": "string",   // 分享平台
  "screenshotUrl": "string"    // 截图链接
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "分享审核提交成功",
  "data": {
    "id": "string",
    "taskId": "string",
    "shareUrl": "string",
    "shareContent": "string",
    "status": 0,
    "createdTime": "2025-08-01T10:00:00"
  }
}
```

### 4.2 审核社群分享
- **接口路径**: `PUT /core/publisher/share-reviews/{id}/review`
- **功能描述**: 审核社群分享申请
- **是否需要Feign客户端**: 否

**请求参数**:
```json
{
  "reviewResult": "integer",   // 审核结果：1-通过，2-拒绝
  "reviewComment": "string",   // 审核意见
  "reviewerId": "string"       // 审核人ID
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "分享审核处理成功",
  "data": true
}
```

### 4.3 查询社群分享审核列表
- **接口路径**: `GET /core/publisher/share-reviews`
- **功能描述**: 查询社群分享审核列表
- **是否需要Feign客户端**: 否

**请求参数**:
```
page: integer      // 页码，默认1
size: integer      // 每页大小，默认10
taskId: string     // 任务ID
status: integer    // 审核状态
sharePlatform: string // 分享平台
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询分享审核列表成功",
  "data": {
    "records": [
      {
        "id": "string",
        "taskId": "string",
        "shareUrl": "string",
        "shareContent": "string",
        "status": 0,
        "createdTime": "2025-08-01T10:00:00"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

## 5. 文件上传接口

### 5.1 上传任务文件
- **接口路径**: `POST /core/publisher/tasks/{taskId}/upload-file`
- **功能描述**: 上传任务二维码或任务头像
- **是否需要Feign客户端**: 否

**请求参数**:
```
taskId: string     // 任务ID（路径参数）
fileType: string   // 文件类型：qrcode-二维码，avatar-头像
file: file         // 上传的文件
```

**响应数据**:
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "fileUrl": "string",       // 文件访问URL
    "fileName": "string",      // 文件名
    "fileSize": "long"         // 文件大小
  }
}
```

## 6. 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 200 | 成功 | 操作成功 |
| 400 | 请求参数错误 | 请求参数格式不正确 |
| 401 | 未授权 | 用户未登录或token无效 |
| 403 | 权限不足 | 用户权限不足 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器内部错误 | 服务器内部错误 |

## 7. 注意事项

1. **接口路径规范**: 所有接口路径都遵循 `/core/publisher/{具体功能}` 的格式
2. **参数验证**: 所有必填参数都会进行格式验证
3. **权限控制**: 任务审核相关接口需要管理员权限
4. **文件上传**: 支持的文件格式为jpg、png、gif，文件大小不超过5MB
5. **分页查询**: 默认每页10条记录，最大每页100条记录 