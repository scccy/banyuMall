# Third Party OSS API 测试文档

## 📋 测试概述

**模块名称**: third-party-aliyunOss  
**测试类型**: API功能测试  
**测试环境**: 开发环境  
**测试工具**: Postman / curl  
**作者**: scccy  
**创建时间**: 2025-01-27  

### 测试目标
- 验证文件上传功能
- 验证文件访问功能
- 验证文件删除功能
- 验证批量文件上传功能
- 验证文件路径生成功能

## 🏗️ 测试环境准备

### 服务启动
```bash
# 启动OSS服务
cd third-party/third-party-aliyunOss
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 启动依赖服务
# MySQL: 3306
# 阿里云OSS: 配置环境变量
```

### 测试基础信息
- **服务地址**: http://localhost:8084
- **数据库**: MySQL (third_party)
- **阿里云OSS**: 需要配置AccessKey和Bucket

## 📁 文件上传测试

### 1. 单文件上传测试

#### 测试用例 1.1: 上传图片文件
**测试目标**: 验证图片文件上传功能

**请求信息**:
```http
POST http://localhost:8084/tp/oss/upload
Content-Type: multipart/form-data

file: [选择图片文件]
sourceService: core-publisher
businessType: task-image
filePath: core-publisher/task-image/2025/01/27/
uploadUserId: 1
uploadUserName: testuser
```

**预期响应**:
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "fileId": 1,
    "originalName": "test-image.jpg",
    "fileSize": 102400,
    "mimeType": "image/jpeg",
    "objectKey": "core-publisher/task-image/2025/01/27/test-image-20250127-143000.jpg",
    "accessUrl": "https://example-bucket.oss-cn-beijing.aliyuncs.com/core-publisher/task-image/2025/01/27/test-image-20250127-143000.jpg",
    "bucketName": "example-bucket",
    "filePath": "core-publisher/task-image/2025/01/27/",
    "sourceService": "core-publisher",
    "businessType": "task-image",
    "uploadUserId": 1,
    "uploadUserName": "testuser",
    "uploadTime": "2025-01-27T14:30:00"
  }
}
```

**验证要点**:
- [ ] 响应状态码为 200
- [ ] 返回文件ID和访问URL
- [ ] 文件成功上传到OSS
- [ ] 数据库中创建了文件记录

#### 测试用例 1.2: 上传文档文件
**测试目标**: 验证文档文件上传功能

**请求信息**:
```http
POST http://localhost:8084/tp/oss/upload
Content-Type: multipart/form-data

file: [选择PDF文档]
sourceService: service-user
businessType: user-document
filePath: service-user/user-document/2025/01/27/
uploadUserId: 2
uploadUserName: admin
```

**预期响应**:
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "fileId": 2,
    "originalName": "document.pdf",
    "fileSize": 2048000,
    "mimeType": "application/pdf",
    "objectKey": "service-user/user-document/2025/01/27/document-20250127-143000.pdf",
    "accessUrl": "https://example-bucket.oss-cn-beijing.aliyuncs.com/service-user/user-document/2025/01/27/document-20250127-143000.pdf",
    "bucketName": "example-bucket",
    "filePath": "service-user/user-document/2025/01/27/",
    "sourceService": "service-user",
    "businessType": "user-document",
    "uploadUserId": 2,
    "uploadUserName": "admin",
    "uploadTime": "2025-01-27T14:30:00"
  }
}
```

### 2. 文件上传参数验证测试

#### 测试用例 2.1: 文件类型验证
**测试目标**: 验证不支持的文件类型

**请求信息**:
```http
POST http://localhost:8084/tp/oss/upload
Content-Type: multipart/form-data

file: [选择.exe文件]
sourceService: core-publisher
businessType: task-image
filePath: core-publisher/task-image/2025/01/27/
```

**预期响应**:
```json
{
  "code": 415,
  "message": "不支持的文件类型",
  "data": null
}
```

#### 测试用例 2.2: 文件大小验证
**测试目标**: 验证文件大小限制

**请求信息**:
```http
POST http://localhost:8084/tp/oss/upload
Content-Type: multipart/form-data

file: [选择超过100MB的文件]
sourceService: core-publisher
businessType: task-image
filePath: core-publisher/task-image/2025/01/27/
```

**预期响应**:
```json
{
  "code": 413,
  "message": "文件大小超过限制",
  "data": null
}
```

#### 测试用例 2.3: 必填参数验证
**测试目标**: 验证必填参数

**请求信息**:
```http
POST http://localhost:8084/tp/oss/upload
Content-Type: multipart/form-data

file: [选择文件]
sourceService: 
businessType: 
filePath: 
```

**预期响应**:
```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": null
}
```

## 🔗 文件访问测试

### 测试用例 3.1: 获取文件访问URL
**测试目标**: 验证文件访问URL获取功能

**请求信息**:
```http
GET http://localhost:8084/tp/oss/url/1
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "获取文件访问URL成功",
  "data": "https://example-bucket.oss-cn-beijing.aliyuncs.com/core-publisher/task-image/2025/01/27/test-image-20250127-143000.jpg"
}
```

### 测试用例 3.2: 获取不存在的文件URL
**测试目标**: 验证不存在的文件处理

**请求信息**:
```http
GET http://localhost:8084/tp/oss/url/999
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 404,
  "message": "文件不存在",
  "data": null
}
```

## 🗑️ 文件删除测试

### 测试用例 4.1: 删除文件
**测试目标**: 验证文件删除功能

**请求信息**:
```http
DELETE http://localhost:8084/tp/oss/file/1?userId=1
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "文件删除成功",
  "data": true
}
```

### 测试用例 4.2: 删除不存在的文件
**测试目标**: 验证删除不存在的文件

**请求信息**:
```http
DELETE http://localhost:8084/tp/oss/file/999?userId=1
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 404,
  "message": "文件不存在",
  "data": false
}
```

### 测试用例 4.3: 删除文件权限验证
**测试目标**: 验证删除权限

**请求信息**:
```http
DELETE http://localhost:8084/tp/oss/file/1?userId=999
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 403,
  "message": "无权限删除该文件",
  "data": false
}
```

## 📦 批量文件上传测试

### 测试用例 5.1: 批量上传文件
**测试目标**: 验证批量文件上传功能

**请求信息**:
```http
POST http://localhost:8084/tp/oss/batch-upload
Content-Type: multipart/form-data

files: [选择多个文件]
sourceService: core-publisher
businessType: task-images
userId: 1
```

**预期响应**:
```json
{
  "code": 200,
  "message": "批量文件上传成功",
  "data": [
    {
      "fileId": 3,
      "originalName": "image1.jpg",
      "fileSize": 51200,
      "mimeType": "image/jpeg",
      "objectKey": "core-publisher/task-images/2025/01/27/image1-20250127-143000.jpg",
      "accessUrl": "https://example-bucket.oss-cn-beijing.aliyuncs.com/core-publisher/task-images/2025/01/27/image1-20250127-143000.jpg",
      "bucketName": "example-bucket",
      "filePath": "core-publisher/task-images/2025/01/27/",
      "sourceService": "core-publisher",
      "businessType": "task-images",
      "uploadUserId": 1,
      "uploadTime": "2025-01-27T14:30:00"
    },
    {
      "fileId": 4,
      "originalName": "image2.jpg",
      "fileSize": 76800,
      "mimeType": "image/jpeg",
      "objectKey": "core-publisher/task-images/2025/01/27/image2-20250127-143000.jpg",
      "accessUrl": "https://example-bucket.oss-cn-beijing.aliyuncs.com/core-publisher/task-images/2025/01/27/image2-20250127-143000.jpg",
      "bucketName": "example-bucket",
      "filePath": "core-publisher/task-images/2025/01/27/",
      "sourceService": "core-publisher",
      "businessType": "task-images",
      "uploadUserId": 1,
      "uploadTime": "2025-01-27T14:30:00"
    }
  ]
}
```

### 测试用例 5.2: 批量上传部分失败
**测试目标**: 验证批量上传部分失败的处理

**请求信息**:
```http
POST http://localhost:8084/tp/oss/batch-upload
Content-Type: multipart/form-data

files: [选择包含无效文件的多个文件]
sourceService: core-publisher
businessType: task-images
userId: 1
```

**预期响应**:
```json
{
  "code": 207,
  "message": "部分文件上传失败",
  "data": [
    {
      "fileId": 5,
      "originalName": "valid-image.jpg",
      "uploadSuccess": true
    },
    {
      "originalName": "invalid-file.exe",
      "uploadSuccess": false,
      "errorMessage": "不支持的文件类型"
    }
  ]
}
```

## 🛣️ 文件路径生成测试

### 测试用例 6.1: 生成文件路径
**测试目标**: 验证文件路径生成功能

**请求信息**:
```http
GET http://localhost:8084/tp/oss/generate-path?sourceService=core-publisher&businessType=task-image
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "生成文件路径成功",
  "data": "core-publisher/task-image/2025/01/27/"
}
```

### 测试用例 6.2: 生成路径参数验证
**测试目标**: 验证路径生成参数验证

**请求信息**:
```http
GET http://localhost:8084/tp/oss/generate-path?sourceService=&businessType=
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": null
}
```

## 📊 性能测试

### 测试用例 7.1: 大文件上传性能
**测试目标**: 验证大文件上传性能

**测试步骤**:
1. 准备10MB、50MB、100MB的测试文件
2. 分别上传这些文件
3. 记录上传时间和成功率

**预期结果**:
- 10MB文件上传时间 < 30秒
- 50MB文件上传时间 < 120秒
- 100MB文件上传时间 < 300秒
- 上传成功率 > 95%

### 测试用例 7.2: 并发上传测试
**测试目标**: 验证并发上传性能

**测试步骤**:
1. 同时发起10个文件上传请求
2. 记录响应时间和成功率
3. 验证数据库记录正确性

**预期结果**:
- 平均响应时间 < 10秒
- 并发成功率 > 90%
- 数据库记录完整准确

## 📋 测试检查清单

### 文件上传功能
- [ ] 单文件上传正常
- [ ] 批量文件上传正常
- [ ] 文件类型验证正常
- [ ] 文件大小验证正常
- [ ] 参数验证正常
- [ ] 错误处理正常

### 文件访问功能
- [ ] 获取文件URL正常
- [ ] 不存在的文件处理正常
- [ ] URL有效期正常
- [ ] 访问权限控制正常

### 文件删除功能
- [ ] 文件删除正常
- [ ] 权限验证正常
- [ ] 不存在的文件处理正常
- [ ] 删除日志记录正常

### 文件路径生成功能
- [ ] 路径生成正常
- [ ] 参数验证正常
- [ ] 路径格式正确

### 性能表现
- [ ] 上传响应时间 < 10秒
- [ ] 访问响应时间 < 1秒
- [ ] 并发处理能力 > 10 QPS
- [ ] 大文件上传稳定

### 异常处理
- [ ] 网络异常处理正常
- [ ] OSS服务异常处理正常
- [ ] 数据库异常处理正常
- [ ] 参数异常处理正常

## 🚀 自动化测试脚本

### Postman Collection
```json
{
  "info": {
    "name": "Third Party OSS API Tests",
    "description": "第三方OSS服务API测试集合"
  },
  "item": [
    {
      "name": "文件上传",
      "item": [
        {
          "name": "上传图片文件",
          "request": {
            "method": "POST",
            "url": "http://localhost:8084/tp/oss/upload",
            "header": [
              {
                "key": "Content-Type",
                "value": "multipart/form-data"
              }
            ],
            "body": {
              "mode": "formdata",
              "formdata": [
                {
                  "key": "file",
                  "type": "file",
                  "src": []
                },
                {
                  "key": "sourceService",
                  "value": "core-publisher"
                },
                {
                  "key": "businessType",
                  "value": "task-image"
                },
                {
                  "key": "filePath",
                  "value": "core-publisher/task-image/2025/01/27/"
                }
              ]
            }
          }
        }
      ]
    },
    {
      "name": "文件访问",
      "item": [
        {
          "name": "获取文件URL",
          "request": {
            "method": "GET",
            "url": "http://localhost:8084/tp/oss/url/{{fileId}}"
          }
        }
      ]
    },
    {
      "name": "文件删除",
      "item": [
        {
          "name": "删除文件",
          "request": {
            "method": "DELETE",
            "url": "http://localhost:8084/tp/oss/file/{{fileId}}?userId=1"
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

# OSS服务测试
echo "=== OSS服务测试 ==="

# 上传文件
echo "上传文件..."
curl -X POST http://localhost:8084/tp/oss/upload \
  -F "file=@test-image.jpg" \
  -F "sourceService=core-publisher" \
  -F "businessType=task-image" \
  -F "filePath=core-publisher/task-image/2025/01/27/" \
  -F "uploadUserId=1" \
  -F "uploadUserName=testuser"

# 获取文件URL
echo "获取文件URL..."
curl -X GET http://localhost:8084/tp/oss/url/1

# 生成文件路径
echo "生成文件路径..."
curl -X GET "http://localhost:8084/tp/oss/generate-path?sourceService=core-publisher&businessType=task-image"

echo "=== 测试完成 ==="
```

## 🚨 错误码说明

| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 400 | 参数错误 | 请求参数格式错误或缺少必填参数 | 检查请求参数格式和完整性 |
| 401 | 认证失败 | 用户未登录或令牌无效 | 重新登录获取有效令牌 |
| 403 | 权限不足 | 用户没有访问该资源的权限 | 联系管理员分配相应权限 |
| 404 | 资源不存在 | 请求的资源不存在 | 检查资源ID是否正确 |
| 413 | 文件过大 | 上传的文件超过大小限制 | 压缩文件或选择较小的文件 |
| 415 | 文件类型不支持 | 上传的文件类型不支持 | 使用支持的文件格式 |
| 500 | 服务器错误 | 服务器内部错误 | 查看服务器日志，联系技术支持 |
| 503 | 服务不可用 | OSS服务暂时不可用 | 稍后重试或联系运维人员 |

### OSS服务专用错误码
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

### 文件上传相关错误码
| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 4109 | 文件上传失败 | 文件上传到OSS失败 | 检查网络连接，稍后重试 |
| 4110 | 文件下载失败 | 从OSS下载文件失败 | 检查文件是否存在，稍后重试 |
| 4111 | 文件删除失败 | 从OSS删除文件失败 | 检查文件权限，稍后重试 |
| 4112 | 文件访问被拒绝 | OSS文件访问权限不足 | 检查文件访问权限 |
| 4113 | 文件大小超限 | 文件大小超过限制 | 压缩文件或选择较小的文件 |
| 4114 | 文件类型不支持 | 不支持的文件类型 | 使用支持的文件格式 |
| 4115 | 文件路径无效 | 文件路径格式错误 | 检查文件路径格式 |
| 4116 | 文件已存在 | 文件已存在，不允许覆盖 | 使用不同的文件名 |
| 4117 | 文件损坏 | 文件已损坏或格式错误 | 检查文件完整性 |

## 📝 注意事项

1. **文件大小限制**: 注意不同文件类型的大小限制
2. **文件格式支持**: 确保上传的文件格式在支持列表中
3. **权限控制**: 确保只有有权限的用户才能上传和删除文件
4. **路径安全**: 注意文件路径的安全性，防止路径遍历攻击
5. **存储管理**: 定期清理不必要的文件，避免存储空间浪费
6. **性能优化**: 大文件上传时注意性能优化
7. **安全防护**: 防止恶意文件上传和访问
8. **监控告警**: 设置合适的监控告警，及时发现异常

---

**文档版本**: v1.0  
**最后更新**: 2025-01-27  
**维护人员**: scccy 