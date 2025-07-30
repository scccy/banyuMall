# 阿里云OSS集成规范

## 基本信息
- **集成对象**: 阿里云对象存储服务 (OSS)
- **SDK版本**: aliyun-sdk-oss 3.17.4
- **Java版本要求**: Java 7+
- **创建时间**: 2024-12-19

## 依赖配置

### Maven依赖
```xml
<!-- 阿里云OSS SDK -->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>

<!-- Java 9+ 需要添加JAXB依赖 -->
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.3</version>
</dependency>
```

## 配置管理

### 环境变量配置
```bash
# Linux/macOS
export OSS_ACCESS_KEY_ID='YOUR_ACCESS_KEY_ID'
export OSS_ACCESS_KEY_SECRET='YOUR_ACCESS_KEY_SECRET'

# Windows CMD
setx OSS_ACCESS_KEY_ID "YOUR_ACCESS_KEY_ID"
setx OSS_ACCESS_KEY_SECRET "YOUR_ACCESS_KEY_SECRET"

# Windows PowerShell
[Environment]::SetEnvironmentVariable("OSS_ACCESS_KEY_ID", "YOUR_ACCESS_KEY_ID", [EnvironmentVariableTarget]::User)
[Environment]::SetEnvironmentVariable("OSS_ACCESS_KEY_SECRET", "YOUR_ACCESS_KEY_SECRET", [EnvironmentVariableTarget]::User)
```

### Spring Boot配置
```yaml
# application.yml
aliyun:
  oss:
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: ${OSS_ACCESS_KEY_ID:your-access-key-id}
    access-key-secret: ${OSS_ACCESS_KEY_SECRET:your-access-key-secret}
    bucket-name: your-bucket-name
    region: cn-hangzhou
    # 安全配置
    security-token: ${OSS_SECURITY_TOKEN:}
    # 连接池配置
    max-connections: 200
    connection-timeout: 30000
    socket-timeout: 60000
    # 重试配置
    max-retries: 3
    retry-delay: 1000
```

## 核心配置类

### OSS配置类
```java
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
public class OssConfig {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String region;
    private String securityToken;
    private Integer maxConnections = 200;
    private Integer connectionTimeout = 30000;
    private Integer socketTimeout = 60000;
    private Integer maxRetries = 3;
    private Integer retryDelay = 1000;
}
```

### OSS客户端配置
```java
@Configuration
public class OssClientConfig {
    
    @Autowired
    private OssConfig ossConfig;
    
    @Bean
    public OSS ossClient() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setMaxConnections(ossConfig.getMaxConnections());
        conf.setConnectionTimeout(ossConfig.getConnectionTimeout());
        conf.setSocketTimeout(ossConfig.getSocketTimeout());
        conf.setMaxErrorRetry(ossConfig.getMaxRetries());
        
        return new OSSClientBuilder().build(
            ossConfig.getEndpoint(),
            ossConfig.getAccessKeyId(),
            ossConfig.getAccessKeySecret(),
            conf
        );
    }
}
```

## 服务层实现

### OSS服务接口
```java
public interface OssService {
    /**
     * 上传文件
     */
    String uploadFile(String objectName, InputStream inputStream);
    
    /**
     * 下载文件
     */
    InputStream downloadFile(String objectName);
    
    /**
     * 删除文件
     */
    boolean deleteFile(String objectName);
    
    /**
     * 生成预签名URL
     */
    String generatePresignedUrl(String objectName, long expiration);
    
    /**
     * 检查文件是否存在
     */
    boolean doesObjectExist(String objectName);
}
```

### OSS服务实现
```java
@Service
@Slf4j
public class OssServiceImpl implements OssService {
    
    @Autowired
    private OSS ossClient;
    
    @Autowired
    private OssConfig ossConfig;
    
    @Override
    public String uploadFile(String objectName, InputStream inputStream) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucketName(), 
                objectName, 
                inputStream
            );
            ossClient.putObject(putObjectRequest);
            return objectName;
        } catch (OSSException | ClientException e) {
            log.error("文件上传失败: {}", objectName, e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public InputStream downloadFile(String objectName) {
        try {
            OSSObject ossObject = ossClient.getObject(
                ossConfig.getBucketName(), 
                objectName
            );
            return ossObject.getObjectContent();
        } catch (OSSException | ClientException e) {
            log.error("文件下载失败: {}", objectName, e);
            throw new RuntimeException("文件下载失败", e);
        }
    }
    
    @Override
    public boolean deleteFile(String objectName) {
        try {
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
            return true;
        } catch (OSSException | ClientException e) {
            log.error("文件删除失败: {}", objectName, e);
            return false;
        }
    }
    
    @Override
    public String generatePresignedUrl(String objectName, long expiration) {
        try {
            Date expirationDate = new Date(System.currentTimeMillis() + expiration);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                ossConfig.getBucketName(), 
                objectName
            );
            request.setExpiration(expirationDate);
            request.setMethod(HttpMethod.GET);
            
            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        } catch (OSSException | ClientException e) {
            log.error("生成预签名URL失败: {}", objectName, e);
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }
    
    @Override
    public boolean doesObjectExist(String objectName) {
        try {
            return ossClient.doesObjectExist(ossConfig.getBucketName(), objectName);
        } catch (OSSException | ClientException e) {
            log.error("检查文件存在性失败: {}", objectName, e);
            return false;
        }
    }
}
```

## 安全配置

### RAM用户配置
1. 在RAM控制台创建RAM用户
2. 为用户授予 `AliyunOSSFullAccess` 权限
3. 创建AccessKey并保存

### 权限策略
```json
{
    "Version": "1",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "oss:GetObject",
                "oss:PutObject",
                "oss:DeleteObject",
                "oss:ListObjects"
            ],
            "Resource": [
                "acs:oss:*:*:your-bucket-name/*"
            ]
        }
    ]
}
```

## 错误处理

### 常见错误及处理
| 错误类型 | 错误原因 | 解决方案 |
|---------|---------|----------|
| OSSException | 服务端错误 | 检查请求参数和权限 |
| ClientException | 客户端错误 | 检查网络连接和配置 |
| NoSuchKey | 文件不存在 | 检查文件路径是否正确 |
| SocketException | 网络连接问题 | 检查网络和连接池配置 |

### 异常处理规范
```java
@ControllerAdvice
public class OssExceptionHandler {
    
    @ExceptionHandler(OSSException.class)
    public ResponseEntity<String> handleOssException(OSSException e) {
        log.error("OSS服务异常: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("文件服务异常: " + e.getMessage());
    }
    
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<String> handleClientException(ClientException e) {
        log.error("OSS客户端异常: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("文件服务配置异常: " + e.getMessage());
    }
}
```

## 性能优化

### 连接池配置
- 最大连接数：200
- 连接超时：30秒
- Socket超时：60秒
- 重试次数：3次

### 批量操作
```java
public void batchDeleteFiles(List<String> objectNames) {
    DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(ossConfig.getBucketName());
    deleteRequest.setKeys(objectNames);
    ossClient.deleteObjects(deleteRequest);
}
```

### 分片上传
```java
public String multipartUpload(String objectName, InputStream inputStream, long fileSize) {
    InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
        ossConfig.getBucketName(), objectName
    );
    InitiateMultipartUploadResult initResult = ossClient.initiateMultipartUpload(initRequest);
    String uploadId = initResult.getUploadId();
    
    // 分片上传逻辑
    // ...
    
    return objectName;
}
```

## 监控和日志

### 日志配置
```xml
<!-- logback-spring.xml -->
<logger name="com.aliyun.oss" level="INFO"/>
<logger name="org.apache.http" level="WARN"/>
```

### 监控指标
- 上传成功率
- 下载成功率
- 平均响应时间
- 错误率统计

## 最佳实践

### 文件命名规范
- 使用UUID避免文件名冲突
- 按日期分目录存储
- 添加文件类型后缀

### 安全最佳实践
- 使用RAM用户而非主账号
- 定期轮换AccessKey
- 设置合理的文件访问权限
- 使用HTTPS协议

### 性能最佳实践
- 合理设置连接池参数
- 使用批量操作减少请求次数
- 大文件使用分片上传
- 合理设置缓存策略 