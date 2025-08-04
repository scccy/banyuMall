# service-user API测试文档

## 📋 文档说明

> **文档位置**: zinfra/moudleDocs/service-user/api-test.md  
> **测试代码位置**: src/test/java/com/origin/user/controller/  
> **测试方法命名**: 接口名称 + Test (如: createUserTest, getUserInfoTest)

## 🔧 REST接口功能列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | 测试方法 | 查看详情 |
|------|----------|----------|----------|----------|----------|----------|----------|
| 1 | 创建用户 | POST | `/service/user` | 创建新用户，支持头像上传 | 用户管理 | createUserTest | [查看详情](#1-创建用户) |
| 2 | 获取用户信息 | GET | `/service/user/{userId}` | 根据用户ID获取用户详细信息 | 用户信息 | getUserInfoTest | [查看详情](#2-获取用户信息) |
| 3 | 更新用户信息 | POST | `/service/user/{userId}` | 更新用户信息，支持头像更新 | 用户管理 | updateUserTest | [查看详情](#3-更新用户信息) |
| 4 | 删除用户 | POST | `/service/user/{userId}/delete` | 软删除指定用户 | 用户管理 | deleteUserTest | [查看详情](#4-删除用户) |
| 5 | 用户列表查询 | GET | `/service/user/list` | 分页查询用户列表，支持多条件筛选 | 用户查询 | getUserListTest | [查看详情](#5-用户列表查询) |
| 6 | 批量删除用户 | POST | `/service/user/batch/delete` | 批量软删除多个用户 | 用户管理 | batchDeleteUsersTest | [查看详情](#6-批量删除用户) |
| 7 | 获取用户头像信息 | GET | `/service/user/{userId}/avatar` | 获取用户的头像URL和相关信息 | 头像管理 | getAvatarInfoTest | [查看详情](#7-获取用户头像信息) |
| 8 | 健康检查 | GET | `/service/user/test` | 服务健康检查接口 | 系统监控 | testTest | [查看详情](#8-健康检查) |

## 🔗 Feign客户端接口列表

| 序号 | 服务名称 | Feign客户端 | 接口路径 | 主要用途 | 测试方法 | 查看详情 |
|------|----------|-------------|----------|----------|----------|----------|
| 1 | service-auth | AuthFeignClient | `/service/auth` | 用户认证、密码加密验证、权限验证 | authFeignTest | [查看详情](#feign-1-认证服务) |
| 2 | third-party-aliyunOss | OssFileFeignClient | `/tp/oss` | 文件上传、头像管理 | ossFeignTest | [查看详情](#feign-2-文件服务) |
| 3 | service-user | UserFeignClient | `/service/user` | 用户信息查询（内部调用） | userFeignTest | [查看详情](#feign-3-用户服务) |

---

## 🔧 REST接口详情

### 1. 创建用户

#### 接口描述
创建新用户，支持管理员和发布者两种用户类型，可选择上传头像。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/user`
- **请求头**: 
  ```
  Content-Type: multipart/form-data
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 请求参数
```json
{
  "userInfo": {
    "username": "testuser",
    "password": "123456",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "13800138000",
    "userType": 2
  },
  "avatarFile": "文件对象（可选）"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "用户创建成功",
  "data": {
    "userId": "test_user_001",
    "username": "testuser",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "13800138000",
    "avatar": "https://oss.example.com/avatars/test_user_001.jpg",
    "userType": 2,
    "status": 1,
    "createTime": "2025-01-27T10:00:00",
    "updateTime": "2025-01-27T10:00:00"
  },
  "timestamp": 1703123456789
}
```

#### 错误响应示例
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `UserControllerTest`
- **测试方法**: `createUserTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 2. 获取用户信息

#### 接口描述
根据用户ID获取用户详细信息。

#### 请求信息
- **请求方法**: GET
- **请求路径**: `/service/user/{userId}`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 路径参数
- `userId`: 用户ID (必填)

#### 响应示例
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "userId": "test_user_001",
    "username": "testuser",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "13800138000",
    "avatar": "https://oss.example.com/avatars/test_user_001.jpg",
    "userType": 2,
    "status": 1,
    "createTime": "2025-01-27T10:00:00",
    "updateTime": "2025-01-27T10:00:00"
  },
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `UserControllerTest`
- **测试方法**: `getUserInfoTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 3. 更新用户信息

#### 接口描述
更新用户的基础信息（昵称、头像、邮箱等），可选择上传头像。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/user/{userId}`
- **请求头**: 
  ```
  Content-Type: multipart/form-data
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 路径参数
- `userId`: 用户ID (必填)

#### 请求参数
```json
{
  "userInfo": {
    "nickname": "更新后的昵称",
    "email": "newemail@example.com",
    "phone": "13800138001"
  },
  "avatarFile": "文件对象（可选）"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "用户信息更新成功",
  "data": {
    "userId": "test_user_001",
    "username": "testuser",
    "nickname": "更新后的昵称",
    "email": "newemail@example.com",
    "phone": "13800138001",
    "avatar": "https://oss.example.com/avatars/test_user_001_new.jpg",
    "userType": 2,
    "status": 1,
    "updateTime": "2025-01-27T11:00:00"
  },
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `UserControllerTest`
- **测试方法**: `updateUserTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 4. 删除用户

#### 接口描述
软删除指定用户。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/user/{userId}/delete`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 路径参数
- `userId`: 用户ID (必填)

#### 响应示例
```json
{
  "code": 200,
  "message": "用户删除成功",
  "data": null,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `UserControllerTest`
- **测试方法**: `deleteUserTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 5. 用户列表查询

#### 接口描述
分页查询用户列表，支持多条件筛选。

#### 请求信息
- **请求方法**: GET
- **请求路径**: `/service/user/list`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 查询参数
- `current`: 当前页码 (类型: Integer, 必填: 否, 默认: 1)
- `size`: 每页大小 (类型: Integer, 必填: 否, 默认: 10)
- `keyword`: 关键词 (类型: String, 必填: 否)
- `userType`: 用户类型 (类型: Integer, 必填: 否)
- `status`: 用户状态 (类型: Integer, 必填: 否)
- `sortField`: 排序字段 (类型: String, 必填: 否)
- `sortOrder`: 排序方向 (类型: String, 必填: 否, 默认: desc)

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "userId": "test_user_001",
        "username": "testuser",
        "nickname": "测试用户",
        "email": "test@example.com",
        "phone": "13800138000",
        "avatar": "https://oss.example.com/avatars/test_user_001.jpg",
        "userType": 2,
        "status": 1,
        "createTime": "2025-01-27T10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  },
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `UserControllerTest`
- **测试方法**: `getUserListTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 6. 批量删除用户

#### 接口描述
批量软删除多个用户。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/user/batch/delete`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 请求参数
```json
[
  "test_user_001",
  "test_user_002",
  "test_user_003"
]
```

#### 响应示例
```json
{
  "code": 200,
  "message": "批量删除完成，成功删除 3 个用户",
  "data": null,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `UserControllerTest`
- **测试方法**: `batchDeleteUsersTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 7. 获取用户头像信息

#### 接口描述
获取用户的头像URL和相关信息。

#### 请求信息
- **请求方法**: GET
- **请求路径**: `/service/user/{userId}/avatar`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 路径参数
- `userId`: 用户ID (必填)

#### 响应示例
```json
{
  "code": 200,
  "message": "获取头像信息成功",
  "data": {
    "userId": "test_user_001",
    "avatarUrl": "https://oss.example.com/avatars/test_user_001.jpg",
    "avatarThumbnail": "https://oss.example.com/avatars/test_user_001_thumb.jpg",
    "fileSize": 102400,
    "fileType": "image/jpeg",
    "uploadTime": "2025-01-27T10:00:00"
  },
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `UserControllerTest`
- **测试方法**: `getAvatarInfoTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 8. 健康检查

#### 接口描述
用于验证用户服务是否正常运行的接口。

#### 请求信息
- **请求方法**: GET
- **请求路径**: `/service/user/test`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 响应示例
```json
{
  "code": 200,
  "message": "User Service is running!",
  "data": null,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `UserControllerTest`
- **测试方法**: `testTest`
- **测试场景**: 
  - 正常请求测试
  - 服务状态测试

---

## 🔗 Feign客户端接口详情

### Feign-1. 认证服务

#### 接口描述
用户认证、密码加密验证、权限验证相关的Feign客户端接口。

#### 服务信息
- **服务名称**: service-auth
- **Feign客户端**: AuthFeignClient
- **接口路径**: `/service/auth`
- **主要用途**: 用户认证、密码加密验证、权限验证

#### 接口定义
```java
@FeignClient(name = "service-auth", path = "/service/auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    
    // 用户登录
    @PostMapping("/login")
    ResultData login(@RequestBody LoginRequest loginRequest);
    
    // 验证JWT令牌
    @PostMapping("/validate")
    ResultData<Boolean> validateToken(@RequestParam("token") String token);
    
    // 获取用户信息
    @GetMapping("/user/info")
    ResultData<SysUser> getUserInfo(@RequestParam("userId") String userId);
    
    // 密码加密
    @PostMapping("/password/encrypt")
    ResultData<PasswordEncryptResponse> encryptPassword(@RequestBody PasswordEncryptRequest request);
    
    // 密码验证
    @PostMapping("/password/verify")
    ResultData<Boolean> verifyPassword(@RequestBody PasswordEncryptRequest request);
}
```

#### 测试方法
- **测试类**: `AuthFeignClientTest`
- **测试方法**: `authFeignTest`
- **测试场景**: 
  - 正常调用测试
  - 服务不可用降级测试
  - 超时降级测试
  - 参数验证测试

---

### Feign-2. 文件服务

#### 接口描述
文件上传、头像管理相关的Feign客户端接口。

#### 服务信息
- **服务名称**: third-party-aliyunOss
- **Feign客户端**: OssFileFeignClient
- **接口路径**: `/tp/oss`
- **主要用途**: 文件上传、头像管理

#### 接口定义
```java
@FeignClient(name = "third-party-aliyunOss", path = "/tp/oss", fallback = OssFileFeignClientFallback.class)
public interface OssFileFeignClient {
    
    // 上传文件到OSS
    @PostMapping("/upload")
    ResultData<FileUploadResponse> uploadFile(@RequestBody FileUploadRequest request);
    
    // 获取文件访问URL
    @GetMapping("/url/{fileId}")
    ResultData<String> getFileUrl(@PathVariable("fileId") String fileId);
}
```

#### 测试方法
- **测试类**: `OssFileFeignClientTest`
- **测试方法**: `ossFeignTest`
- **测试场景**: 
  - 正常调用测试
  - 服务不可用降级测试
  - 超时降级测试
  - 参数验证测试

---

### Feign-3. 用户服务

#### 接口描述
用户信息查询相关的Feign客户端接口（内部调用）。

#### 服务信息
- **服务名称**: service-user
- **Feign客户端**: UserFeignClient
- **接口路径**: `/service/user`
- **主要用途**: 用户信息查询（内部调用）

#### 接口定义
```java
@FeignClient(name = "service-user", path = "/service/user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    
    // 获取用户信息
    @GetMapping("/{userId}")
    ResultData<SysUser> getUserInfo(@PathVariable("userId") String userId);
    
    // 用户列表查询
    @GetMapping("/list")
    ResultData<IPage<SysUser>> getUserList(@RequestParam Map<String, Object> params);
}
```

#### 测试方法
- **测试类**: `UserFeignClientTest`
- **测试方法**: `userFeignTest`
- **测试场景**: 
  - 正常调用测试
  - 服务不可用降级测试
  - 超时降级测试
  - 参数验证测试

---

## 🧪 测试代码结构

### REST接口测试
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("创建用户 - 正常请求测试")
    void createUserTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("创建用户 - 参数验证测试")
    void createUserValidationTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("获取用户信息 - 正常请求测试")
    void getUserInfoTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("更新用户信息 - 正常请求测试")
    void updateUserTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("删除用户 - 正常请求测试")
    void deleteUserTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("用户列表查询 - 正常请求测试")
    void getUserListTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("批量删除用户 - 正常请求测试")
    void batchDeleteUsersTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("获取用户头像信息 - 正常请求测试")
    void getAvatarInfoTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("健康检查 - 正常请求测试")
    void testTest() {
        // 测试实现
    }
}
```

### Feign客户端测试
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthFeignClientTest {
    
    @Autowired
    private AuthFeignClient authFeignClient;
    
    @Test
    @DisplayName("认证服务调用 - 正常调用测试")
    void authFeignTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("认证服务调用 - 服务不可用降级测试")
    void authFeignFallbackTest() {
        // 测试实现
    }
}

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OssFileFeignClientTest {
    
    @Autowired
    private OssFileFeignClient ossFileFeignClient;
    
    @Test
    @DisplayName("文件服务调用 - 正常调用测试")
    void ossFeignTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("文件服务调用 - 服务不可用降级测试")
    void ossFeignFallbackTest() {
        // 测试实现
    }
}
```

## 📊 测试数据准备

### 测试用户数据
```sql
-- 测试用户数据
INSERT INTO sys_user (user_id, username, password, nickname, email, phone, user_type, status) VALUES
('test_user_001', 'testuser1', '$2a$12$...', '测试用户1', 'test1@example.com', '13800138001', 2, 1),
('test_user_002', 'testuser2', '$2a$12$...', '测试用户2', 'test2@example.com', '13800138002', 2, 1),
('test_user_003', 'admin', '$2a$12$...', '管理员', 'admin@example.com', '13800138003', 1, 1),
('test_user_004', 'disabled_user', '$2a$12$...', '禁用用户', 'disabled@example.com', '13800138004', 2, 2);
```

## 🔧 测试配置

### 测试环境配置
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

# Feign测试配置
feign:
  client:
    config:
      service-auth:
        connect-timeout: 1000
        read-timeout: 2000
      third-party-aliyunOss:
        connect-timeout: 1000
        read-timeout: 2000

# 测试专用配置
test:
  mock:
    enabled: true
  timeout:
    feign: 2000
```

### Mock配置
```java
@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public SysUserService mockSysUserService() {
        return Mockito.mock(SysUserService.class);
    }
    
    @Bean
    @Primary
    public AuthFeignClient mockAuthFeignClient() {
        return Mockito.mock(AuthFeignClient.class);
    }
    
    @Bean
    @Primary
    public OssFileFeignClient mockOssFileFeignClient() {
        return Mockito.mock(OssFileFeignClient.class);
    }
}
```

## 📈 性能测试

### 接口性能基准
| 接口名称 | 平均响应时间 | 95%响应时间 | 99%响应时间 | 并发用户数 |
|----------|-------------|-------------|-------------|------------|
| 创建用户 | < 500ms | < 1000ms | < 2000ms | 50 |
| 获取用户信息 | < 100ms | < 200ms | < 500ms | 100 |
| 更新用户信息 | < 300ms | < 600ms | < 1200ms | 50 |
| 删除用户 | < 100ms | < 200ms | < 500ms | 50 |
| 用户列表查询 | < 200ms | < 400ms | < 800ms | 100 |
| 批量删除用户 | < 500ms | < 1000ms | < 2000ms | 30 |
| 获取用户头像信息 | < 50ms | < 100ms | < 200ms | 100 |

### 性能测试方法
- **测试类**: `UserControllerPerformanceTest`
- **测试方法**: `createUserPerformanceTest`
- **测试工具**: JMeter / Gatling
- **测试场景**: 并发请求、压力测试、稳定性测试

## 🚨 错误码说明

| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 400 | 参数错误 | 请求参数格式错误或缺少必填参数 | 检查请求参数格式和完整性 |
| 401 | 认证失败 | 用户未登录或令牌无效 | 重新登录获取有效令牌 |
| 403 | 权限不足 | 用户没有访问该资源的权限 | 联系管理员分配相应权限 |
| 404 | 资源不存在 | 请求的用户不存在 | 检查用户ID是否正确 |
| 409 | 资源冲突 | 用户名已存在或邮箱已被使用 | 使用不同的用户名或邮箱 |
| 413 | 文件过大 | 上传的头像文件超过大小限制 | 压缩图片或选择较小的文件 |
| 415 | 文件类型不支持 | 上传的文件类型不支持 | 使用支持的图片格式 |
| 500 | 服务器错误 | 服务器内部错误 | 查看服务器日志，联系技术支持 |
| 503 | 服务不可用 | 依赖的服务暂时不可用 | 稍后重试或使用降级方案 |

### 用户服务专用错误码
| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 1001 | 用户不存在 | 指定的用户不存在 | 检查用户ID是否正确 |
| 1002 | 用户已存在 | 用户名或手机号已存在 | 使用不同的用户名或手机号 |
| 1003 | 密码错误 | 用户密码错误 | 检查密码是否正确 |
| 1004 | 账号已被禁用 | 用户账号已被禁用 | 联系管理员激活账号 |
| 1005 | 令牌已过期 | JWT令牌已过期 | 重新登录获取新的令牌 |
| 1006 | 令牌无效 | JWT令牌无效 | 检查令牌格式或重新登录 |
| 1007 | 用户档案不存在 | 用户档案信息不存在 | 检查用户档案ID是否正确 |
| 1008 | 用户头像上传失败 | 用户头像上传操作失败 | 检查文件格式和大小，稍后重试 |
| 1009 | 用户权限不足 | 用户权限不足以执行操作 | 联系管理员分配相应权限 |

### 文件上传相关错误码
| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 4109 | 文件上传失败 | 文件上传到OSS失败 | 检查网络连接，稍后重试 |
| 4110 | 文件下载失败 | 从OSS下载文件失败 | 检查文件是否存在，稍后重试 |
| 4111 | 文件删除失败 | 从OSS删除文件失败 | 检查文件权限，稍后重试 |
| 4112 | 文件访问被拒绝 | OSS文件访问权限不足 | 检查文件访问权限 |

## 📝 注意事项

1. **测试数据隔离**: 每个测试方法使用独立的测试数据，避免相互影响
2. **测试清理**: 测试完成后及时清理测试数据
3. **Mock使用**: 合理使用Mock对象，避免对外部服务的依赖
4. **测试覆盖**: 确保覆盖正常流程、异常流程、边界条件等场景
5. **性能测试**: 定期进行性能测试，确保接口性能符合要求
6. **文档同步**: 及时更新API文档，确保文档与实际实现一致
7. **文件上传测试**: 重点关注头像文件上传的测试
8. **Feign降级测试**: 确保外部服务不可用时的降级处理正确
9. **权限验证**: 测试不同用户类型的权限控制
10. **批量操作**: 测试批量删除等批量操作的性能

---

**文档版本**: v1.0  
**创建时间**: 2025-01-27  
**维护人员**: scccy 