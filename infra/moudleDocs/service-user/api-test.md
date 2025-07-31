# Service-User 接口测试文档

## 接口列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | Feign调用 | 测试详情 |
|------|----------|----------|----------|----------|-----------|----------|
| 1 | 创建用户 | POST | `/user` | 创建新用户，支持管理员和发布者两种用户类型 | 否 | [查看测试](#11-创建用户) |
| 2 | 获取用户信息 | GET | `/user/{userId}` | 根据用户ID获取用户详细信息 | **是** | [查看测试](#12-获取用户信息) |
| 3 | 更新用户信息 | PUT | `/user/{userId}` | 更新用户的基础信息（昵称、头像、邮箱等） | 否 | [查看测试](#13-更新用户信息) |
| 4 | 删除用户 | DELETE | `/user/{userId}` | 软删除指定用户 | 否 | [查看测试](#14-删除用户) |
| 5 | 用户列表查询 | GET | `/user/list` | 分页查询用户列表，支持多条件筛选 | **是** | [查看测试](#15-用户列表查询) |
| 6 | 批量删除用户 | DELETE | `/user/batch` | 批量软删除多个用户 | 否 | [查看测试](#16-批量删除用户) |
| 7 | 获取用户扩展信息 | GET | `/profile/{userId}` | 获取用户的详细资料和公司信息 | **是** | [查看测试](#21-获取用户扩展信息) |
| 8 | 创建或更新用户扩展信息 | POST | `/profile/{userId}` | 创建或更新用户的扩展信息（真实姓名、公司信息等） | 否 | [查看测试](#22-创建或更新用户扩展信息) |

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

### 1.2 获取用户信息 {#12-获取用户信息}
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

### 1.3 更新用户信息 {#13-更新用户信息}
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

### 1.4 删除用户 {#14-删除用户}
```bash
# 删除指定用户
curl -X DELETE http://localhost:8080/user/USER_ID_HERE \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 1.5 用户列表查询 {#15-用户列表查询}
```bash
# 查询用户列表（支持Feign调用）
curl -X GET "http://localhost:8080/user/list?page=1&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 按条件筛选
curl -X GET "http://localhost:8080/user/list?username=test&userType=2&page=1&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 1.6 批量删除用户 {#16-批量删除用户}
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

## 3. Feign调用示例

### 3.1 在其他服务中调用用户接口
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

## 4. 错误场景测试

### 4.1 权限不足测试
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

### 4.2 参数验证失败测试
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

## 5. 注意事项

1. **路由路径**: 用户服务的路由路径为 `/user/**`，简洁明了
2. **网关访问**: 外部客户端通过 `http://localhost:8080/user/**` 访问
3. **Feign调用**: 服务间通过 `service-user/user/**` 直接调用
4. **Feign接口**: 只有查询类接口支持Feign调用，写操作接口不支持
5. **权限控制**: 管理员接口需要管理员权限，普通用户接口需要用户权限
6. **数据验证**: 注意检查参数格式和业务规则验证
