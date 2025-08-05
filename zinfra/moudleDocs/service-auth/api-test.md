# service-auth API测试文档

## 📋 文档说明

> **文档位置**: zinfra/moudleDocs/service-auth/api-test.md  
> **测试代码位置**: src/test/java/com/origin/auth/controller/  
> **测试方法命名**: 接口名称 + Test (如: loginTest, logoutTest)

## 🔧 REST接口功能列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | 测试方法 | 查看详情 |
|------|----------|----------|----------|----------|----------|----------|----------|
| 1 | 用户登录 | POST | `/service/auth/login` | 处理用户登录请求，返回JWT令牌 | 用户认证 | loginTest | [查看详情](#1-用户登录) |
| 2 | 用户登出 | POST | `/service/auth/logout` | 处理用户登出请求，清除会话 | 会话管理 | logoutTest | [查看详情](#2-用户登出) |
| 3 | 强制登出用户 | POST | `/service/auth/logout/force/{userId}` | 管理员强制登出指定用户 | 会话管理 | forceLogoutTest | [查看详情](#3-强制登出用户) |
| 4 | 密码加密 | POST | `/service/auth/password/encrypt` | 为其他服务提供密码加密功能 | 密码管理 | encryptPasswordTest | [查看详情](#4-密码加密) |
| 5 | 密码验证 | POST | `/service/auth/password/verify` | 为其他服务提供密码验证功能 | 密码管理 | verifyPasswordTest | [查看详情](#5-密码验证) |
| 6 | 获取用户信息 | GET | `/service/auth/user/info` | 为其他服务提供用户信息查询 | 用户信息 | getUserInfoTest | [查看详情](#6-获取用户信息) |
| 7 | 验证JWT令牌 | POST | `/service/auth/validate` | 为其他服务提供JWT令牌验证 | 令牌管理 | validateTokenTest | [查看详情](#7-验证jwt令牌) |
| 8 | 健康检查 | GET | `/service/auth/test` | 服务健康检查接口 | 系统监控 | testTest | [查看详情](#8-健康检查) |

## 🔗 Feign客户端接口列表

| 序号 | 服务名称 | Feign客户端 | 接口路径 | 主要用途 | 测试方法 | 查看详情 |
|------|----------|-------------|----------|----------|----------|----------|
| 无 | 无 | 无 | 无 | 认证服务为其他服务提供认证功能，不依赖外部服务 | 无 | 无 |

---

## 🔧 REST接口详情

### 1. 用户登录

#### 接口描述
处理用户登录请求，验证用户名和密码，生成JWT令牌并返回用户信息。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/auth/login`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 请求参数
```json
{
  "username": "admin",
  "password": "123456",
  "captcha": "1234",
  "captchaKey": "captcha_key",
  "rememberMe": true
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "1234567890",
    "username": "admin",
    "nickname": "管理员",
    "avatar": "https://example.com/avatar.jpg",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userType": 1,
    "userTypeDesc": "系统管理员"
  },
  "timestamp": 1703123456789
}
```

#### 错误响应示例
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `AuthControllerTest`
- **测试方法**: `loginTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 2. 用户登出

#### 接口描述
处理用户登出请求，清除用户会话信息和JWT令牌。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/auth/logout`
- **请求头**: 
  ```
  Content-Type: application/json
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  X-Request-ID: test-request-123
  X-Client-IP: 127.0.0.1
  X-User-Agent: Mozilla/5.0
  ```

#### 响应示例
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1703123456789
}
```

#### 错误响应示例
```json
{
  "code": 401,
  "message": "登出失败：缺少有效的token",
  "data": null,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `AuthControllerTest`
- **测试方法**: `logoutTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 3. 强制登出用户

#### 接口描述
管理员强制登出指定用户，清除该用户的所有会话。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/auth/logout/force/{userId}`
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
  "message": "强制登出成功",
  "data": null,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `AuthControllerTest`
- **测试方法**: `forceLogoutTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 4. 密码加密

#### 接口描述
为其他微服务提供密码加密功能，使用BCrypt算法加密密码。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/auth/password/encrypt`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

#### 请求参数
```json
{
  "username": "admin",
  "password": "123456"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "密码加密成功",
  "data": {
    "username": "admin",
    "encryptedPassword": "$2a$12$..."
  },
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `AuthControllerTest`
- **测试方法**: `encryptPasswordTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 异常情况测试

---

### 5. 密码验证

#### 接口描述
为其他微服务提供密码验证功能，验证用户输入的密码是否正确。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/auth/password/verify`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

#### 请求参数
```json
{
  "username": "admin",
  "password": "123456"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "密码验证完成",
  "data": true,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `AuthControllerTest`
- **测试方法**: `verifyPasswordTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 异常情况测试

---

### 6. 获取用户信息

#### 接口描述
为其他微服务提供用户信息查询功能，根据用户ID获取用户详细信息。

#### 请求信息
- **请求方法**: GET
- **请求路径**: `/service/auth/user/info`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

#### 查询参数
- `userId`: 用户ID (类型: String, 必填: 是)

#### 响应示例
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "userId": "1234567890",
    "username": "admin",
    "nickname": "管理员",
    "avatar": "https://example.com/avatar.jpg",
    "email": "admin@example.com",
    "phone": "13800138000",
    "wechatId": "wx123456",
    "youzanId": "yz123456",
    "gender": 1,
    "birthday": "1990-01-01",
    "userType": 1,
    "status": 1,
    "profileId": "profile123",
    "lastLoginTime": "2025-07-31T10:00:00",
    "createTime": "2025-07-31T10:00:00",
    "updateTime": "2025-07-31T10:00:00"
  },
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `AuthControllerTest`
- **测试方法**: `getUserInfoTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 异常情况测试

---

### 7. 验证JWT令牌

#### 接口描述
为其他微服务提供JWT令牌验证功能，验证令牌的有效性。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/auth/validate`
- **请求头**: 
  ```
  Content-Type: application/json
  ```

#### 查询参数
- `token`: JWT令牌 (类型: String, 必填: 是)

#### 响应示例
```json
{
  "code": 200,
  "message": "令牌验证完成",
  "data": true,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `AuthControllerTest`
- **测试方法**: `validateTokenTest`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 异常情况测试

---

### 8. 健康检查

#### 接口描述
用于验证认证服务是否正常运行的接口。

#### 请求信息
- **请求方法**: GET
- **请求路径**: `/service/auth/test`
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
  "message": "认证服务运行正常",
  "data": null,
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `AuthControllerTest`
- **测试方法**: `testTest`
- **测试场景**: 
  - 正常请求测试
  - 服务状态测试

---

## 🧪 测试代码结构

### REST接口测试
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("用户登录 - 正常请求测试")
    void loginTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("用户登录 - 参数验证测试")
    void loginValidationTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("用户登录 - 权限验证测试")
    void loginPermissionTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("用户登录 - 异常情况测试")
    void loginExceptionTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("用户登出 - 正常请求测试")
    void logoutTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("强制登出用户 - 正常请求测试")
    void forceLogoutTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("密码加密 - 正常请求测试")
    void encryptPasswordTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("密码验证 - 正常请求测试")
    void verifyPasswordTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("获取用户信息 - 正常请求测试")
    void getUserInfoTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("验证JWT令牌 - 正常请求测试")
    void validateTokenTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("健康检查 - 正常请求测试")
    void testTest() {
        // 测试实现
    }
}
```

## 📊 测试数据准备

### 测试用户数据
```sql
-- 测试用户数据
INSERT INTO sys_user (user_id, username, password, nickname, email, phone, user_type, status) VALUES
('test_user_001', 'admin', '$2a$12$...', '管理员', 'admin@example.com', '13800138000', 1, 1),
('test_user_002', 'user1', '$2a$12$...', '普通用户1', 'user1@example.com', '13800138001', 2, 1),
('test_user_003', 'user2', '$2a$12$...', '普通用户2', 'user2@example.com', '13800138002', 2, 1),
('test_user_004', 'disabled_user', '$2a$12$...', '禁用用户', 'disabled@example.com', '13800138003', 2, 2);
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

# JWT测试配置
jwt:
  secret: test-secret-key
  expiration: 3600
  header: Authorization

# 测试专用配置
test:
  mock:
    enabled: true
  timeout:
    feign: 5000
```

### Mock配置
```java
@TestConfiguration
public class TestConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 在测试环境中排除所有需要认证的路径
        // 这样可以专注于测试业务逻辑，而不需要处理认证
    }
}

@TestConfiguration
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfig {
    
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
        return http.build();
    }
}
```

## 📈 性能测试

### 接口性能基准
| 接口名称 | 平均响应时间 | 95%响应时间 | 99%响应时间 | 并发用户数 |
|----------|-------------|-------------|-------------|------------|
| 用户登录 | < 200ms | < 500ms | < 1000ms | 100 |
| 用户登出 | < 100ms | < 200ms | < 500ms | 100 |
| 密码加密 | < 50ms | < 100ms | < 200ms | 50 |
| 密码验证 | < 100ms | < 200ms | < 500ms | 50 |
| 获取用户信息 | < 50ms | < 100ms | < 200ms | 100 |
| 验证JWT令牌 | < 20ms | < 50ms | < 100ms | 200 |

### 性能测试方法
- **测试类**: `AuthControllerPerformanceTest`
- **测试方法**: `loginPerformanceTest`
- **测试工具**: JMeter / Gatling
- **测试场景**: 并发请求、压力测试、稳定性测试

## 🚨 错误码说明

| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 400 | 参数错误 | 请求参数格式错误或缺少必填参数 | 检查请求参数格式和完整性 |
| 401 | 认证失败 | 用户名或密码错误，或令牌无效 | 检查用户名密码或重新登录 |
| 403 | 权限不足 | 用户没有访问该资源的权限 | 联系管理员分配相应权限 |
| 404 | 资源不存在 | 请求的用户不存在 | 检查用户ID是否正确 |
| 500 | 服务器错误 | 服务器内部错误 | 查看服务器日志，联系技术支持 |
| 503 | 服务不可用 | 认证服务暂时不可用 | 稍后重试或联系运维人员 |

### 认证服务专用错误码
| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 1101 | 认证失败 | 认证过程失败 | 检查认证凭据或联系管理员 |
| 1102 | 认证令牌缺失 | 未提供认证令牌 | 在请求头中添加Authorization令牌 |
| 1103 | 认证令牌已过期 | 认证令牌已过期 | 重新登录获取新的令牌 |
| 1104 | 认证令牌无效 | 认证令牌格式或签名无效 | 检查令牌格式或重新登录 |
| 1105 | 用户账号已被禁用 | 用户账号被禁用或待审核 | 联系管理员激活账号 |
| 1106 | 用户名或密码错误 | 登录凭据错误 | 检查用户名和密码是否正确 |

## 📝 注意事项

1. **测试数据隔离**: 每个测试方法使用独立的测试数据，避免相互影响
2. **测试清理**: 测试完成后及时清理测试数据
3. **Mock使用**: 合理使用Mock对象，避免对外部服务的依赖
4. **测试覆盖**: 确保覆盖正常流程、异常流程、边界条件等场景
5. **性能测试**: 定期进行性能测试，确保接口性能符合要求
6. **文档同步**: 及时更新API文档，确保文档与实际实现一致
7. **安全测试**: 重点关注认证和授权相关的安全测试
8. **令牌管理**: 测试JWT令牌的生成、验证和过期处理

---

**文档版本**: v1.0  
**创建时间**: 2025-07-31  
**维护人员**: scccy 