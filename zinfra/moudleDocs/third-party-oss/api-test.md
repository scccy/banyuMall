# 阿里云OSS服务 - API测试文档

## 服务信息
- **服务名称**: aliyun-oss
- **服务端口**: 8085
- **服务地址**: http://localhost:8085
- **API文档地址**: http://localhost:8085/doc.html

## 接口测试

### 1. 文件上传接口

#### 1.1 单文件上传
**接口地址**: `POST /tp/oss/upload`

**请求参数**:
- `file`: 上传的文件 (MultipartFile)
- `sourceService`: 来源服务 (String)
- `businessType`: 业务类型 (String)
- `filePath`: 文件路径 (String)
- `uploadUserId`: 上传用户ID (Long, 可选)
- `uploadUserName`: 上传用户名 (String, 可选)

**测试示例**:
```bash
curl -X POST "http://localhost:8085/tp/oss/test-upload" -H "Content-Type: multipart/form-data" -F "file=@/Volumes/project/test/oss_test.JPG" -F "sourceService=core-publisher" -F "businessType=task-image" -F "filePath=core-publisher/task-image/2025/01/27/" -F "uploadUserId=1" -F "uploadUserName=testuser"
```

**预期响应**:
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

#### 1.2 批量文件上传
**接口地址**: `POST /tp/oss/batch-upload`

**请求参数**:
- `files`: 文件列表 (List<MultipartFile>)
- `sourceService`: 来源服务 (String)
- `businessType`: 业务类型 (String)
- `userId`: 用户ID (Long)

**测试示例**:
```bash
curl -X POST "http://localhost:8085/tp/oss/batch-upload" \
  -H "Content-Type: multipart/form-data" \
  -F "files=@/path/to/file1.jpg" \
  -F "files=@/path/to/file2.png" \
  -F "sourceService=core-publisher" \
  -F "businessType=task-image" \
  -F "userId=1"
```

### 2. 文件访问接口

#### 2.1 获取文件访问URL
**接口地址**: `GET /tp/oss/url/{fileId}`

**路径参数**:
- `fileId`: 文件ID (Long)

**测试示例**:
```bash
curl -X GET "http://localhost:8085/tp/oss/url/1"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/task-image/2025-01-27/uuid.jpg"
}
```

### 3. 文件管理接口

#### 3.1 删除文件
**接口地址**: `DELETE /tp/oss/file/{fileId}`

**路径参数**:
- `fileId`: 文件ID (Long)

**查询参数**:
- `userId`: 用户ID (Long)

**测试示例**:
```bash
curl -X DELETE "http://localhost:8085/api/v1/oss/file/1?userId=1"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

#### 3.2 生成文件路径
**接口地址**: `GET /api/v1/oss/generate-path`

**查询参数**:
- `sourceService`: 来源服务 (String)
- `businessType`: 业务类型 (String)

**测试示例**:
```bash
curl -X GET "http://localhost:8085/api/v1/oss/generate-path?sourceService=core-publisher&businessType=task-image"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": "core-publisher/task-image/2025/01/27/"
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 403 | 权限不足 |
| 404 | 文件不存在 |
| 413 | 文件大小超限 |
| 415 | 文件类型不支持 |
| 500 | 服务器内部错误 |

## 测试数据准备

### 1. 测试文件
准备以下测试文件：
- `test.jpg` (1MB以内的JPEG图片)
- `test.png` (1MB以内的PNG图片)
- `test.pdf` (1MB以内的PDF文档)
- `large-file.jpg` (超过10MB的文件，用于测试文件大小限制)

### 2. 数据库准备
确保数据库中已创建相关表：
```sql
-- 执行数据库脚本
source zinfra/database/data/third-party/third-party-oss-schema.sql
```

## 测试场景

### 1. 正常上传测试
- 上传小文件 (小于1MB)
- 上传中等文件 (1-5MB)
- 上传大文件 (5-10MB)

### 2. 异常情况测试
- 上传超过10MB的文件
- 上传不支持的文件类型
- 上传空文件
- 使用无效的参数

### 3. 权限测试
- 删除自己的文件
- 删除他人的文件
- 访问不存在的文件

### 4. 批量操作测试
- 批量上传多个文件
- 批量上传包含无效文件的情况

## 性能测试

### 1. 并发上传测试
```bash
# 使用Apache Bench进行并发测试
ab -n 100 -c 10 -p upload_data.txt -T "multipart/form-data" http://localhost:8085/api/v1/oss/upload
```

### 2. 大文件上传测试
- 测试10MB文件的上传时间
- 测试网络中断后的重试机制

## 监控指标

### 1. 业务指标
- 文件上传成功率
- 文件上传平均响应时间
- 文件删除成功率
- 文件访问成功率

### 2. 系统指标
- CPU使用率
- 内存使用率
- 磁盘I/O
- 网络带宽

## 注意事项

1. **文件大小限制**: 单文件最大10MB
2. **文件类型限制**: 仅支持图片、PDF、文档等安全类型
3. **路径格式**: 文件路径必须符合规范 `{服务名}/{业务类型}/{日期}/`
4. **权限控制**: 只能删除自己上传的文件
5. **URL过期**: 预签名URL有1小时的过期时间

## 常见问题

### 1. 文件上传失败
- 检查文件大小是否超过限制
- 检查文件类型是否支持
- 检查OSS配置是否正确

### 2. 文件访问失败
- 检查文件是否存在
- 检查URL是否过期
- 检查权限设置

### 3. 服务启动失败
- 检查数据库连接
- 检查OSS配置
- 检查端口是否被占用 