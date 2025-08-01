# third-party-oss API接口说明

> **文档位置**: `infra/moudleDocs/third-party-oss/API接口说明.md`
> **创建时间**: 2025-08-01
> **作者**: scccy

## 接口概述

### 基础信息
- **服务名称**: third-party-oss
- **服务端口**: 8085
- **基础路径**: `/third-party/oss`
- **API文档地址**: http://localhost:8085/doc.html

### 通用响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 1. 文件上传接口

### 1.1 单文件上传

**接口地址**: `POST /third-party/oss/upload`

**接口描述**: 上传单个文件到阿里云OSS

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | MultipartFile | 是 | 上传的文件 |
| sourceService | String | 是 | 来源服务名称 |
| businessType | String | 是 | 业务类型 |
| filePath | String | 否 | 文件路径 |
| uploadUserId | Long | 否 | 上传用户ID |
| uploadUserName | String | 否 | 上传用户名 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileId": 1,
    "originalName": "test.jpg",
    "accessUrl": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/task-image/2025-01-27/uuid.jpg",
    "fileSize": 1024000,
    "mimeType": "image/jpeg",
    "filePath": "core-publisher/task-image/2025/01/27/",
    "uploadStatus": "SUCCESS",
    "message": "文件上传成功"
  }
}
```

**测试示例**:
```bash
curl -X POST "http://localhost:8085/third-party/oss/upload" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/test.jpg" \
  -F "sourceService=core-publisher" \
  -F "businessType=task-image" \
  -F "filePath=core-publisher/task-image/2025/01/27/" \
  -F "uploadUserId=1" \
  -F "uploadUserName=testuser"
```

### 1.2 批量文件上传

**接口地址**: `POST /third-party/oss/upload/batch`

**接口描述**: 批量上传多个文件到阿里云OSS

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| files | List<MultipartFile> | 是 | 文件列表 |
| sourceService | String | 是 | 来源服务名称 |
| businessType | String | 是 | 业务类型 |
| userId | Long | 否 | 用户ID |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "fileId": 1,
      "originalName": "test1.jpg",
      "accessUrl": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/task-image/2025-01-27/uuid1.jpg",
      "fileSize": 1024000,
      "mimeType": "image/jpeg",
      "filePath": "core-publisher/task-image/2025/01/27/",
      "uploadStatus": "SUCCESS",
      "message": "文件上传成功"
    },
    {
      "fileId": 2,
      "originalName": "test2.png",
      "accessUrl": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/task-image/2025-01-27/uuid2.png",
      "fileSize": 2048000,
      "mimeType": "image/png",
      "filePath": "core-publisher/task-image/2025/01/27/",
      "uploadStatus": "SUCCESS",
      "message": "文件上传成功"
    }
  ]
}
```

**测试示例**:
```bash
curl -X POST "http://localhost:8085/third-party/oss/upload/batch" \
  -H "Content-Type: multipart/form-data" \
  -F "files=@/path/to/file1.jpg" \
  -F "files=@/path/to/file2.png" \
  -F "sourceService=core-publisher" \
  -F "businessType=task-image" \
  -F "userId=1"
```

## 2. 文件访问接口

### 2.1 文件下载

**接口地址**: `GET /third-party/oss/download/{fileId}`

**接口描述**: 下载指定文件

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileId | Long | 是 | 文件ID |

**响应**: 文件流

**测试示例**:
```bash
curl -X GET "http://localhost:8085/third-party/oss/download/1" \
  -o downloaded_file.jpg
```

### 2.2 文件预览

**接口地址**: `GET /third-party/oss/preview/{fileId}`

**接口描述**: 获取文件预览URL

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileId | Long | 是 | 文件ID |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/task-image/2025-01-27/uuid.jpg"
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8085/third-party/oss/preview/1"
```

## 3. 文件管理接口

### 3.1 获取文件信息

**接口地址**: `GET /third-party/oss/file/{fileId}`

**接口描述**: 获取指定文件的详细信息

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileId | Long | 是 | 文件ID |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "originalName": "test.jpg",
    "fileSize": 1024000,
    "mimeType": "image/jpeg",
    "objectKey": "task-image/2025-01-27/uuid.jpg",
    "filePath": "core-publisher/task-image/2025/01/27/",
    "accessUrl": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/task-image/2025-01-27/uuid.jpg",
    "bucketName": "banyumall-files",
    "sourceService": "core-publisher",
    "businessType": "task-image",
    "uploadUserId": 1,
    "uploadUserName": "testuser",
    "md5Hash": "d41d8cd98f00b204e9800998ecf8427e",
    "uploadStatus": "SUCCESS",
    "createTime": "2025-01-27T10:00:00",
    "updateTime": "2025-01-27T10:00:00"
  }
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8085/third-party/oss/file/1"
```

### 3.2 文件列表查询

**接口地址**: `GET /third-party/oss/file/list`

**接口描述**: 分页查询文件列表

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 页大小，默认10 |
| sourceService | String | 否 | 来源服务 |
| businessType | String | 否 | 业务类型 |
| uploadUserId | Long | 否 | 上传用户ID |
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "originalName": "test.jpg",
        "fileSize": 1024000,
        "mimeType": "image/jpeg",
        "accessUrl": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/task-image/2025-01-27/uuid.jpg",
        "sourceService": "core-publisher",
        "businessType": "task-image",
        "uploadUserId": 1,
        "uploadUserName": "testuser",
        "uploadStatus": "SUCCESS",
        "createTime": "2025-01-27T10:00:00"
      }
    ]
  }
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8085/third-party/oss/file/list?pageNum=1&pageSize=10&sourceService=core-publisher"
```

### 3.3 删除文件

**接口地址**: `DELETE /third-party/oss/file/{fileId}`

**接口描述**: 删除指定文件

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileId | Long | 是 | 文件ID |

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 操作用户ID |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**测试示例**:
```bash
curl -X DELETE "http://localhost:8085/third-party/oss/file/1?userId=1"
```

## 4. 统计接口

### 4.1 上传统计

**接口地址**: `GET /third-party/oss/stats/upload`

**接口描述**: 获取文件上传统计信息

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sourceService | String | 否 | 来源服务 |
| businessType | String | 否 | 业务类型 |
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalFiles": 1000,
    "totalSize": 1024000000,
    "successCount": 980,
    "failedCount": 20,
    "successRate": 0.98,
    "avgFileSize": 1024000,
    "topFileTypes": [
      {
        "mimeType": "image/jpeg",
        "count": 500,
        "percentage": 0.5
      },
      {
        "mimeType": "image/png",
        "count": 300,
        "percentage": 0.3
      }
    ],
    "dailyStats": [
      {
        "date": "2025-01-27",
        "count": 50,
        "size": 51200000
      }
    ]
  }
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8085/third-party/oss/stats/upload?sourceService=core-publisher&startTime=2025-01-01&endTime=2025-01-31"
```

### 4.2 访问统计

**接口地址**: `GET /third-party/oss/stats/access`

**接口描述**: 获取文件访问统计信息

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileId | Long | 否 | 文件ID |
| accessType | String | 否 | 访问类型 |
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalAccess": 5000,
    "uniqueUsers": 1000,
    "avgResponseTime": 150,
    "successRate": 0.99,
    "topAccessTypes": [
      {
        "accessType": "DOWNLOAD",
        "count": 3000,
        "percentage": 0.6
      },
      {
        "accessType": "PREVIEW",
        "count": 2000,
        "percentage": 0.4
      }
    ],
    "dailyAccess": [
      {
        "date": "2025-01-27",
        "count": 200,
        "uniqueUsers": 50
      }
    ],
    "topFiles": [
      {
        "fileId": 1,
        "originalName": "test.jpg",
        "accessCount": 100,
        "uniqueUsers": 30
      }
    ]
  }
}
```

**测试示例**:
```bash
curl -X GET "http://localhost:8085/third-party/oss/stats/access?startTime=2025-01-01&endTime=2025-01-31"
```

## 5. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 文件不存在 |
| 413 | 文件过大 |
| 415 | 不支持的文件类型 |
| 500 | 服务器内部错误 |
| 1001 | 文件上传失败 |
| 1002 | 文件下载失败 |
| 1003 | 文件删除失败 |
| 1004 | 文件不存在 |
| 1005 | 文件访问权限不足 |

## 6. 注意事项

### 6.1 文件上传限制
- 支持的文件类型：jpg, jpeg, png, gif, pdf, doc, docx, xls, xlsx, txt
- 单个文件大小限制：10MB
- 批量上传文件数量限制：10个

### 6.2 安全要求
- 所有接口需要JWT Token认证
- 文件上传需要验证文件类型和大小
- 文件删除需要验证用户权限

### 6.3 性能优化
- 大文件支持分片上传
- 文件下载支持断点续传
- 使用CDN加速文件访问

### 6.4 监控告警
- 文件上传失败率监控
- 文件访问响应时间监控
- 存储空间使用率监控 