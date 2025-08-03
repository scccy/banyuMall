# Service-User 接口测试文档

## 接口列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | Feign调用 | 测试详情 |
|------|----------|----------|----------|----------|-----------|----------|
| 1 | 创建用户 | POST | `/user` | 创建新用户，支持管理员和发布者两种用户类型 | 否 | [查看测试](#11-创建用户) |
| 2 | 创建用户（支持头像上传） | POST | `/user/with-avatar` | 创建新用户并上传头像，支持管理员和发布者两种用户类型 | 否 | [查看测试](#12-创建用户支持头像上传) |
| 3 | 获取用户信息 | GET | `/user/{userId}` | 根据用户ID获取用户详细信息 | **是** | [查看测试](#13-获取用户信息) |
| 4 | 更新用户信息 | PUT | `/user/{userId}` | 更新用户的基础信息（昵称、头像、邮箱等） | 否 | [查看测试](#14-更新用户信息) |
| 5 | 删除用户 | DELETE | `/user/{userId}` | 软删除指定用户 | 否 | [查看测试](#15-删除用户) |
| 6 | 用户列表查询 | GET | `/user/list` | 分页查询用户列表，支持多条件筛选 | **是** | [查看测试](#16-用户列表查询) |
| 7 | 批量删除用户 | DELETE | `/user/batch` | 批量软删除多个用户 | 否 | [查看测试](#17-批量删除用户) |
| 8 | 获取用户扩展信息 | GET | `/profile/{userId}` | 获取用户的详细资料和公司信息 | **是** | [查看测试](#21-获取用户扩展信息) |
| 9 | 创建或更新用户扩展信息 | POST | `/profile/{userId}` | 创建或更新用户的扩展信息（真实姓名、公司信息等） | 否 | [查看测试](#22-创建或更新用户扩展信息) |
| 10 | 上传用户头像 | POST | `/user/{userId}/avatar/upload` | 上传用户头像图片并存储到OSS服务 | 否 | [查看测试](#31-上传用户头像) |
| 11 | 获取用户头像信息 | GET | `/user/{userId}/avatar` | 获取用户的头像URL和相关信息 | **是** | [查看测试](#32-获取用户头像信息) |

---

## 环境配置

### 基础信息
- **服务地址**: `http://localhost:8082`
- **网关地址**: `http://localhost:8080`
- **认证方式**: JWT Token (Bearer)
- **Content-Type**: `application/json`

### 路由说明
- **网关访问**: `http://localhost:8080/user/**` (外部客户端)
- **Feign调用**: `service-user/user/**` (服务间调用)

### 获取Token
```bash
# 登录获取token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

## 1. 用户基础信息管理接口

### 1.1 创建用户 {#11-创建用户}
```bash
# 创建普通发布者用户
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "phone": "13800138001",
    "wechatId": "wx_001",
    "youzanId": "yz_001",
    "username": "testuser001",
    "password": "password123",
    "nickname": "测试用户001",
    "email": "test001@example.com",
    "gender": 1,
    "birthday": "1990-01-01",
    "userType": 2
  }'
```

### 1.2 创建用户（支持头像上传） {#12-创建用户支持头像上传}
```bash
# 创建用户并上传头像（使用multipart/form-data格式）
curl -X POST http://localhost:8080/user/with-avatar \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -F "userInfo={\"phone\":\"13800138002\",\"wechatId\":\"wx_002\",\"youzanId\":\"yz_002\",\"username\":\"testuser002\",\"password\":\"password123\",\"nickname\":\"测试用户002\",\"email\":\"test002@example.com\",\"gender\":2,\"birthday\":\"1990-02-02\",\"userType\":2}" \
  -F "avatarFile=@/path/to/avatar.jpg"

# 使用Postman测试
# Method: POST
# URL: http://localhost:8080/user/with-avatar
# Headers: Authorization: Bearer YOUR_TOKEN_HERE
# Body: form-data
#   - Key: userInfo, Type: Text, Value: {"phone":"13800138002","wechatId":"wx_002","youzanId":"yz_002","username":"testuser002","password":"password123","nickname":"测试用户002","email":"test002@example.com","gender":2,"birthday":"1990-02-02","userType":2}
#   - Key: avatarFile, Type: File, Value: 选择头像文件（可选）
```

**响应示例**:
```json
{
  "code": 200,
  "message": "用户创建成功",
  "data": {
    "id": "user_id_123",
    "username": "testuser002",
    "nickname": "测试用户002",
    "avatar": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/avatar/2025-08-01/550e8400-e29b-41d4-a716-446655440000.jpg",
    "email": "test002@example.com",
    "phone": "13800138002",
    "gender": 2,
    "birthday": "1990-02-02",
    "userType": 2,
    "status": 1,
    "createTime": "2025-08-01T15:30:00",
    "updateTime": "2025-08-01T15:30:00"
  }
}
```

### 1.3 获取用户信息 {#13-获取用户信息}
```bash
# 通过网关获取用户信息
curl -X GET http://localhost:8080/user/USER_ID_HERE \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# Feign调用示例（服务间）
# 在Auth服务中调用
@FeignClient(name = "service-user")
public interface UserFeignClient {
    @GetMapping("/user/{userId}")
    ResultData<UserInfo> getUserInfo(@PathVariable String userId);
}
```

### 1.4 更新用户信息 {#14-更新用户信息}
```bash
# 更新用户基础信息
curl -X PUT http://localhost:8080/user/USER_ID_HERE \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "nickname": "新昵称",
    "email": "newemail@example.com",
    "gender": 2
  }'
```

### 1.5 删除用户 {#15-删除用户}
```bash
# 删除指定用户
curl -X DELETE http://localhost:8080/user/USER_ID_HERE \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 1.6 用户列表查询 {#16-用户列表查询}
```bash
# 查询用户列表（支持Feign调用）
curl -X GET "http://localhost:8080/user/list?page=1&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 按条件筛选
curl -X GET "http://localhost:8080/user/list?username=test&userType=2&page=1&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 1.7 批量删除用户 {#17-批量删除用户}
```bash
# 批量删除用户
curl -X DELETE http://localhost:8080/user/batch \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "userIds": ["user_id_1", "user_id_2", "user_id_3"]
  }'
```

## 2. 用户扩展信息管理接口

### 2.1 获取用户扩展信息 {#21-获取用户扩展信息}
```bash
# 获取用户扩展信息（支持Feign调用）
curl -X GET http://localhost:8080/user/profile/USER_ID_HERE \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 2.2 创建或更新用户扩展信息 {#22-创建或更新用户扩展信息}
```bash
# 创建或更新用户扩展信息
curl -X POST http://localhost:8080/user/profile/USER_ID_HERE \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "realName": "张三",
    "companyName": "测试公司",
    "contactPhone": "13900139001",
    "industry": "互联网"
  }'
```

## 3. 用户头像管理接口

### 3.1 上传用户头像 {#31-上传用户头像}
```bash
# 上传用户头像（使用multipart/form-data格式）
curl -X POST http://localhost:8080/user/USER_ID_HERE/avatar/upload \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -F "file=@/path/to/avatar.jpg"

# 使用Postman测试
# Method: POST
# URL: http://localhost:8080/user/USER_ID_HERE/avatar/upload
# Headers: Authorization: Bearer YOUR_TOKEN_HERE
# Body: form-data
#   - Key: file, Type: File, Value: 选择头像文件
```

**响应示例**:
```json
{
  "code": 200,
  "message": "头像上传成功",
  "data": {
    "userId": "user_id_123",
    "avatarUrl": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/avatar/2025-08-01/550e8400-e29b-41d4-a716-446655440000.jpg",
    "fileSize": 102400,
    "mimeType": "image/jpeg",
    "originalFileName": "avatar.jpg"
  }
}
```

### 3.2 获取用户头像信息 {#32-获取用户头像信息}
```bash
# 获取用户头像信息（支持Feign调用）
curl -X GET http://localhost:8080/user/USER_ID_HERE/avatar \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "获取头像信息成功",
  "data": {
    "userId": "user_id_123",
    "avatarUrl": "https://banyumall-files.oss-cn-hangzhou.aliyuncs.com/avatar/2025-08-01/550e8400-e29b-41d4-a716-446655440000.jpg"
  }
}
```

**Feign调用示例**:
```java
// 在其他服务中调用头像接口
@FeignClient(name = "service-user")
public interface UserFeignClient {
    
    // 获取用户头像信息
    @GetMapping("/user/{userId}/avatar")
    ResultData<AvatarResponse> getAvatarInfo(@PathVariable String userId);
}
```

## 4. Feign调用示例

### 4.1 在其他服务中调用用户接口
```java
// 在Auth服务中定义Feign客户端
@FeignClient(name = "service-user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    
    // 获取用户信息
    @GetMapping("/user/{userId}")
    ResultData<UserInfo> getUserInfo(@PathVariable String userId);
    
    // 获取用户列表
    @GetMapping("/user/list")
    ResultData<PageResult<UserInfo>> getUserList(@RequestParam Map<String, Object> params);
    
    // 获取用户扩展信息
    @GetMapping("/profile/{userId}")
    ResultData<UserProfile> getUserProfile(@PathVariable String userId);
}

// 使用示例
@Service
public class AuthService {
    
    @Autowired
    private UserFeignClient userFeignClient;
    
    public void validateUser(String userId) {
        ResultData<UserInfo> result = userFeignClient.getUserInfo(userId);
        if (result.getCode() == 200) {
            UserInfo userInfo = result.getData();
            // 处理用户信息
        }
    }
}
```

## 5. 错误场景测试

### 5.1 权限不足测试
```bash
# 使用普通用户token访问管理员接口
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer NORMAL_USER_TOKEN" \
  -d '{
    "phone": "13800138003",
    "wechatId": "wx_003",
    "youzanId": "yz_003",
    "username": "testuser003",
    "password": "password123",
    "userType": 2
  }'
```

### 5.2 参数验证失败测试
```bash
# 手机号格式错误
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "phone": "invalid_phone",
    "wechatId": "wx_004",
    "youzanId": "yz_004",
    "username": "testuser004",
    "password": "password123",
    "userType": 2
  }'
```

## 6. 注意事项

1. **路由路径**: 用户服务的路由路径为 `/user/**`，简洁明了
2. **网关访问**: 外部客户端通过 `http://localhost:8080/user/**` 访问
3. **Feign调用**: 服务间通过 `service-user/user/**` 直接调用
4. **Feign接口**: 只有查询类接口支持Feign调用，写操作接口不支持
5. **权限控制**: 管理员接口需要管理员权限，普通用户接口需要用户权限
6. **数据验证**: 注意检查参数格式和业务规则验证
