# 第三方业务 - 阿里云OSS文件存储服务设计文档

## 1. 模块概述

### 1.1 模块定位
- **模块名称**: aliyun-oss
- **模块类型**: 三级微服务
- **父模块**: third-party (二级模块 - 第三方业务模块)
- **服务端口**: 8085
- **主要职责**: 提供文件存储服务，集成阿里云OSS，支持图片、文档等文件的上传、下载、管理

### 1.2 业务价值
- 为其他微服务提供统一的文件存储Feign客户端
- 简化文件上传流程，其他服务只需调用接口即可
- 统一管理OSS存储，避免重复代码
- 提供文件去重功能（基于MD5哈希值）
- 记录文件存储行为，便于审计和统计

## 2. 技术架构

### 2.1 技术栈
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **阿里云OSS SDK**: 3.17.4
- **MyBatis Plus**: 3.5.3.1
- **Redis**: 6.0+ (缓存)
- **MySQL**: 8.0+ (元数据存储)
- **Knife4j**: 4.3.0 (API文档)

### 2.2 架构设计
```
third-party/                    # 二级模块 - 第三方业务模块
└── aliyun-oss/               # 三级微服务 - 阿里云OSS服务
    ├── controller/           # 控制器层
    ├── service/             # 业务逻辑层
    ├── mapper/              # 数据访问层
    ├── entity/              # 实体类
    ├── dto/                 # 数据传输对象
    ├── config/              # 配置类
    ├── util/                # 工具类
    └── exception/           # 异常处理
```

## 3. 数据库设计

数据库脚本位置：`zinfra/database/data/third-party/third-party-oss-schema.sql`

### 3.1 文件存储记录表 (oss_file_storage)
**业务功能**: 记录文件存储行为，支持文件路径管理
- 文件基本信息：原始文件名、文件大小、MIME类型
- 存储信息：OSS对象键、访问URL、存储桶名称
- 路径信息：文件路径（按服务/业务类型/日期组织）
- 业务信息：来源服务、业务类型、上传用户
- 时间信息：上传时间、创建时间

### 3.2 文件访问日志表 (oss_file_access_log)
**业务功能**: 记录文件访问情况，支持审计和统计
- 访问信息：访问类型、用户信息、IP地址
- 技术信息：用户代理、来源页面、响应时间
- 状态信息：HTTP状态码、错误信息
- 时间信息：访问时间

## 4. 实体类设计

### 4.1 文件存储记录实体 (OssFileStorage)
```java
@Data
@TableName("oss_file_storage")
public class OssFileStorage extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("original_name")
    private String originalName;
    
    @TableField("file_size")
    private Long fileSize;
    
    @TableField("mime_type")
    private String mimeType;
    
    @TableField("object_key")
    private String objectKey;
    
    @TableField("file_path")
    private String filePath;
    
    @TableField("access_url")
    private String accessUrl;
    
    @TableField("bucket_name")
    private String bucketName;
    
    @TableField("source_service")
    private String sourceService;
    
    @TableField("business_type")
    private String businessType;
    
    @TableField("upload_user_id")
    private Long uploadUserId;
    
    @TableField("upload_user_name")
    private String uploadUserName;
    
    @TableField("upload_time")
    private LocalDateTime uploadTime;
}
```

## 5. DTO设计

### 5.1 文件上传请求 (FileUploadRequest)
```java
@Data
public class FileUploadRequest {
    
    @NotNull(message = "文件不能为空")
    private MultipartFile file;
    
    @NotBlank(message = "来源服务不能为空")
    private String sourceService;
    
    @NotBlank(message = "业务类型不能为空")
    private String businessType;
    
    @NotBlank(message = "文件路径不能为空")
    private String filePath;
    
    private Long uploadUserId;
    
    private String uploadUserName;
}
```

### 5.2 文件上传响应 (FileUploadResponse)
```java
@Data
public class FileUploadResponse {
    
    private Long fileId;
    
    private String originalName;
    
    private String accessUrl;
    
    private Long fileSize;
    
    private String mimeType;
    
    private String filePath;
    
    private String uploadStatus;
    
    private String message;
}
```

## 6. 服务接口设计

### 6.1 文件上传服务 (OssFileService)
```java
public interface OssFileService {
    
    /**
     * 上传文件到OSS
     */
    FileUploadResponse uploadFile(FileUploadRequest request);
    
    /**
     * 获取文件访问URL
     */
    String getFileAccessUrl(Long fileId);
    
    /**
     * 生成文件路径
     */
    String generateFilePath(String sourceService, String businessType);
}
```

### 6.2 Feign客户端接口 (OssFileFeignClient)
```java
@FeignClient(name = "aliyun-oss", fallback = OssFileFeignClientFallback.class)
public interface OssFileFeignClient {
    
    /**
     * 上传文件到OSS
     */
    @PostMapping("/api/v1/oss/upload")
    ResultData<FileUploadResponse> uploadFile(@RequestPart("file") MultipartFile file,
                                            @RequestParam("sourceService") String sourceService,
                                            @RequestParam("businessType") String businessType,
                                            @RequestParam("filePath") String filePath,
                                            @RequestParam(value = "uploadUserId", required = false) Long uploadUserId,
                                            @RequestParam(value = "uploadUserName", required = false) String uploadUserName);
}
```

## 7. 控制器设计

### 7.1 文件上传控制器 (OssFileController)
```java
@RestController
@RequestMapping("/api/v1/oss")
@Tag(name = "OSS文件上传", description = "阿里云OSS文件上传接口")
@Validated
public class OssFileController {
    
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件到阿里云OSS并返回访问地址")
    public ResultData<FileUploadResponse> uploadFile(@RequestPart("file") MultipartFile file,
                                                   @RequestParam("sourceService") String sourceService,
                                                   @RequestParam("businessType") String businessType,
                                                   @RequestParam("filePath") String filePath,
                                                   @RequestParam(value = "uploadUserId", required = false) Long uploadUserId,
                                                   @RequestParam(value = "uploadUserName", required = false) String uploadUserName) {
        // 实现文件上传逻辑
    }
    
    @GetMapping("/url/{fileId}")
    @Operation(summary = "获取文件访问URL", description = "获取文件的访问URL")
    public ResultData<String> getFileAccessUrl(@PathVariable Long fileId) {
        // 实现获取访问URL逻辑
    }
}
```

## 8. 配置设计

### 8.1 阿里云OSS配置
```yaml
# 阿里云OSS配置
aliyun:
  oss:
    endpoint: ${OSS_ENDPOINT:oss-cn-hangzhou.aliyuncs.com}
    access-key-id: ${OSS_ACCESS_KEY_ID:your-access-key-id}
    access-key-secret: ${OSS_ACCESS_KEY_SECRET:your-access-key-secret}
    bucket-name: ${OSS_BUCKET_NAME:banyumall-files}
    # 文件上传配置
    upload:
      max-file-size: 100MB
      allowed-file-types:
        - image/jpeg
        - image/png
        - image/gif
        - image/webp
        - application/pdf
        - application/msword
        - application/vnd.openxmlformats-officedocument.wordprocessingml.document
        - text/plain
      # 图片处理配置
      image:
        resize-enabled: true
        max-width: 4096
        max-height: 4096
        quality: 85
        format: JPEG
        watermark-enabled: false
        watermark-text: "BanyuMall"
        watermark-position: "BOTTOM_RIGHT"
    # 文件访问配置
    access:
      url-expire-seconds: 3600
      private-access-enabled: true
      referer-check-enabled: true
      allowed-referers:
        - "*.banyumall.com"
        - "*.example.com"
```

### 8.2 缓存配置
```yaml
# 文件缓存配置
cache:
  oss:
    file-info:
      key-prefix: "oss:file:info:"
      ttl: 1800  # 30分钟
    file-url:
      key-prefix: "oss:file:url:"
      ttl: 300   # 5分钟
    category:
      key-prefix: "oss:category:"
      ttl: 3600  # 1小时
    statistics:
      key-prefix: "oss:statistics:"
      ttl: 600   # 10分钟
```

## 9. 安全设计

### 9.1 文件访问权限控制
- **公开文件**: 任何人都可以访问
- **私有文件**: 需要签名URL才能访问
- **用户文件**: 只有上传用户才能访问
- **角色文件**: 特定角色用户才能访问

### 9.2 防盗链配置
- 支持Referer白名单
- 支持IP白名单
- 支持时间限制
- 支持下载次数限制

### 9.3 文件类型限制
- 支持的文件类型白名单
- 文件大小限制
- 文件内容检查
- 病毒扫描集成

## 10. 性能优化

### 10.1 缓存策略
- 文件元数据缓存
- 访问URL缓存
- 分类信息缓存
- 统计信息缓存

### 10.2 异步处理
- 文件上传异步处理
- 图片处理异步处理
- 文件删除异步处理
- 日志记录异步处理

### 10.3 批量操作
- 批量文件上传
- 批量文件删除
- 批量文件信息更新
- 批量文件下载

## 11. 监控和日志

### 11.1 业务监控
- 文件上传成功率
- 文件下载成功率
- 文件访问频率
- 存储空间使用率

### 11.2 性能监控
- 上传响应时间
- 下载响应时间
- 文件处理时间
- 缓存命中率

### 11.3 日志记录
- 文件操作日志
- 访问日志
- 错误日志
- 审计日志

## 12. 部署配置

### 12.1 环境配置
- 开发环境配置
- 测试环境配置
- 生产环境配置
- 容器化配置

### 12.2 健康检查
- 服务健康检查
- OSS连接检查
- 数据库连接检查
- Redis连接检查

## 13. 测试策略

### 13.1 单元测试
- 服务层测试
- 工具类测试
- 配置类测试
- 异常处理测试

### 13.2 集成测试
- 文件上传测试
- 文件下载测试
- 文件删除测试
- 分类管理测试

### 13.3 性能测试
- 并发上传测试
- 大文件上传测试
- 批量操作测试
- 缓存性能测试

## 14. 文档和API

### 14.1 API文档
- Swagger/OpenAPI文档
- 接口使用示例
- 错误码说明
- 最佳实践指南

### 14.2 运维文档
- 部署指南
- 配置说明
- 故障排查
- 性能调优

## 15. 扩展性设计

### 15.1 存储扩展
- 支持多种存储后端
- 支持存储迁移
- 支持多区域部署
- 支持CDN集成

### 15.2 功能扩展
- 文件版本管理
- 文件分享功能
- 文件评论功能
- 文件标签功能

### 15.3 业务扩展
- 图片处理服务
- 文档转换服务
- 视频处理服务
- 音频处理服务 