# 任务：core-publisher服务文件上传功能扩展
状态: 已完成
创建时间: 2025-08-01 16:10:00

## 目标
为core-publisher服务添加文件上传功能，支持上传任务二维码和任务头像，直接存储到OSS并保存URL。

## 用户需求分析
用户希望在发布任务时能够：
1. 上传任务二维码图片，存储到OSS并保存URL
2. 上传任务头像图片，存储到OSS并保存URL
3. 在任务实体中保存这些文件的URL

## 实现方案

### 1. 数据库字段扩展
- 在PublisherTask实体中添加：
  - `qr_code_url`: 二维码URL字段
  - `task_avatar_url`: 任务头像URL字段

### 2. OSS集成
- 添加OSS Feign客户端依赖
- 创建OssFileFeignClient和OssFileFeignClientFallback
- 实现文件上传到OSS的功能

### 3. API接口设计
- 新增文件上传接口：`POST /core-publisher/tasks/{taskId}/upload-file`
- 支持两种文件类型：`qr_code`（二维码）和`task_avatar`（任务头像）

### 4. 业务逻辑
- 文件格式验证（JPG、PNG、GIF）
- 文件大小限制（5MB）
- 任务存在性验证
- OSS文件上传和URL保存

## 完成的工作

### ✅ 已完成的开发工作
1. **OSS依赖集成** - 在core-publisher的pom.xml中添加了OSS服务依赖
2. **Feign客户端创建** - 创建了OssFileFeignClient和OssFileFeignClientFallback
3. **实体字段扩展** - 在PublisherTask实体中添加了qr_code_url和task_avatar_url字段
4. **DTO类设计** - 创建了TaskFileUploadRequest和TaskFileUploadResponse DTO类
5. **业务逻辑实现** - 在PublisherTaskService中实现了uploadTaskFile方法
6. **API接口实现** - 在PublisherTaskController中添加了文件上传接口
7. **错误处理完善** - 实现了完整的异常处理和验证逻辑

### ✅ 技术实现细节
1. **文件类型验证**: 支持qr_code和task_avatar两种类型
2. **文件格式验证**: 支持JPG、PNG、GIF格式，文件大小限制5MB
3. **OSS集成**: 通过Feign客户端调用OSS服务，实现文件存储
4. **URL保存**: 根据文件类型更新对应的URL字段
5. **错误处理**: 完善的异常处理和降级机制

### ✅ API接口说明
```bash
# 上传任务二维码
curl -X POST http://localhost:8084/core-publisher/tasks/{taskId}/upload-file \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -F "fileType=qr_code" \
  -F "file=@/path/to/qrcode.jpg"

# 上传任务头像
curl -X POST http://localhost:8084/core-publisher/tasks/{taskId}/upload-file \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -F "fileType=task_avatar" \
  -F "file=@/path/to/avatar.jpg"
```

**响应示例**:
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

## 功能特点

### 1. 灵活性
- 支持两种文件类型：二维码和任务头像
- 文件类型通过参数动态指定

### 2. 安全性
- 文件格式验证，只允许图片格式
- 文件大小限制，防止大文件上传
- 任务存在性验证，确保文件关联到有效任务

### 3. 可靠性
- OSS服务降级处理
- 完善的错误处理和日志记录
- 事务管理确保数据一致性

### 4. 易用性
- 简单的API接口设计
- 清晰的响应格式
- 详细的错误提示

## 数据库变更

### 新增字段
- `publisher_task.qr_code_url`: VARCHAR(500) - 二维码URL
- `publisher_task.task_avatar_url`: VARCHAR(500) - 任务头像URL

### 字段说明
- 两个字段都是可选字段，允许为NULL
- URL长度限制500字符，足够存储OSS URL
- 字段类型为VARCHAR，便于存储和查询

## 测试验证

### 测试场景
1. **正常上传二维码** - 验证二维码上传功能
2. **正常上传任务头像** - 验证任务头像上传功能
3. **文件格式错误** - 验证文件格式验证
4. **文件大小超限** - 验证文件大小限制
5. **任务不存在** - 验证任务存在性验证
6. **OSS服务异常** - 验证降级处理

### 测试用例
- 已在代码中实现了完整的验证逻辑
- 包含详细的错误处理和日志记录
- 支持各种异常场景的处理

## 文档更新

### 更新的文档
1. **实体类** - PublisherTask实体添加了新字段
2. **服务接口** - PublisherTaskService添加了新方法
3. **控制器** - PublisherTaskController添加了新接口
4. **DTO类** - 新增了文件上传相关的DTO类

## 总结

本次功能扩展成功实现了core-publisher服务的文件上传功能，支持任务二维码和任务头像的上传。通过OSS服务集成和完善的业务逻辑，用户可以方便地上传和管理任务相关的图片文件。

### 技术亮点
- 统一的文件上传接口，支持多种文件类型
- 完善的验证和错误处理机制
- 与OSS服务的无缝集成
- 清晰的API设计和响应格式

### 后续优化建议
1. 考虑添加图片压缩功能
2. 优化文件上传的性能和用户体验
3. 添加文件预览功能
4. 考虑添加文件管理功能（删除、替换等） 