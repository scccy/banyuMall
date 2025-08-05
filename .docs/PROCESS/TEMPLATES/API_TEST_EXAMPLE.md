# API测试文档使用示例

## 📋 模板使用说明

本文档展示了如何使用 `API_TEST_TEMPLATE.md` 模板来创建API测试文档。

## 🎯 主要改进点

### 1. 接口分类清晰
- **REST接口**: 对外提供的HTTP接口
- **Feign客户端**: 内部服务间调用的接口

### 2. 测试方法分离
- **文档**: 只描述接口规范和测试方法名称
- **测试代码**: 具体的测试实现放在Java测试文件中

### 3. 测试方法命名规范
- 接口名称 + Test (如: `loginTest`, `getUserInfoTest`)
- 支持多种测试场景 (如: `loginValidationTest`, `loginPermissionTest`)

## 📝 填写示例

### 1. REST接口功能列表填写示例

```markdown
| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | 测试方法 | 查看详情 |
|------|----------|----------|----------|----------|----------|----------|----------|
| 1 | 用户登录 | POST | `/service/auth/login` | 用户登录认证 | 用户认证 | loginTest | [查看详情](#1-用户登录) |
| 2 | 用户登出 | POST | `/service/auth/logout` | 用户登出，清除会话 | 会话管理 | logoutTest | [查看详情](#2-用户登出) |
| 3 | 获取用户信息 | GET | `/service/auth/user/info` | 获取当前用户信息 | 用户信息 | getUserInfoTest | [查看详情](#3-获取用户信息) |
```

### 2. Feign客户端接口列表填写示例

```markdown
| 序号 | 服务名称 | Feign客户端 | 接口路径 | 主要用途 | 测试方法 | 查看详情 |
|------|----------|-------------|----------|----------|----------|----------|
| 1 | service-auth | AuthFeignClient | `/service/auth/validate` | 验证JWT令牌有效性 | validateTokenTest | [查看详情](#feign-1-令牌验证) |
| 2 | service-auth | AuthFeignClient | `/service/auth/user/info` | 获取用户信息 | getUserInfoTest | [查看详情](#feign-2-获取用户信息) |
| 3 | third-party-oss | OssFileFeignClient | `/service/oss/file/upload` | 文件上传 | uploadFileTest | [查看详情](#feign-3-文件上传) |
```

### 3. REST接口详情填写示例

```markdown
### 1. 用户登录

#### 接口描述
用户登录认证，验证用户名密码，返回JWT令牌和用户信息。

#### 请求信息
- **请求方法**: POST
- **请求路径**: `/service/auth/login`
- **请求头**: 
  ```
  Content-Type: application/json
  X-Request-ID: test-request-123
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
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
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
```

### 4. Feign客户端接口详情填写示例

```markdown
### Feign-1. 令牌验证

#### 接口描述
验证JWT令牌的有效性，检查令牌是否过期或被加入黑名单。

#### 服务信息
- **服务名称**: service-auth
- **Feign客户端**: AuthFeignClient
- **接口路径**: `/service/auth/validate`
- **主要用途**: 验证JWT令牌有效性

#### 接口定义
```java
@FeignClient(name = "service-auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    
    @PostMapping("/service/auth/validate")
    ResultData<Boolean> validateToken(@RequestParam("token") String token);
}
```

#### 请求参数
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "令牌验证成功",
  "data": {
    "valid": true,
    "userId": "1234567890",
    "username": "admin",
    "expiresIn": 1800
  },
  "timestamp": 1703123456789
}
```

#### 测试方法
- **测试类**: `AuthFeignClientTest`
- **测试方法**: `validateTokenTest`
- **测试场景**: 
  - 正常调用测试
  - 服务不可用降级测试
  - 超时降级测试
  - 参数验证测试
```

### 5. 测试代码结构示例

#### REST接口测试
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("用户登录 - 正常请求测试")
    void loginTest() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");
        
        // 执行测试
        ResponseEntity<ResultData> response = restTemplate.postForEntity(
            "/service/auth/login", 
            request, 
            ResultData.class
        );
        
        // 验证结果
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo(200);
    }
    
    @Test
    @DisplayName("用户登录 - 参数验证测试")
    void loginValidationTest() {
        // 测试缺少用户名的情况
        LoginRequest request = new LoginRequest();
        request.setPassword("123456");
        
        ResponseEntity<ResultData> response = restTemplate.postForEntity(
            "/service/auth/login", 
            request, 
            ResultData.class
        );
        
        assertThat(response.getBody().getCode()).isEqualTo(400);
    }
    
    @Test
    @DisplayName("用户登录 - 异常情况测试")
    void loginExceptionTest() {
        // 测试不存在的用户
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("123456");
        
        ResponseEntity<ResultData> response = restTemplate.postForEntity(
            "/service/auth/login", 
            request, 
            ResultData.class
        );
        
        assertThat(response.getBody().getCode()).isEqualTo(401);
    }
}
```

#### Feign客户端测试
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthFeignClientTest {
    
    @Autowired
    private AuthFeignClient authFeignClient;
    
    @Test
    @DisplayName("令牌验证 - 正常调用测试")
    void validateTokenTest() {
        // 准备测试数据
        String validToken = "valid_jwt_token_here";
        
        // 执行测试
        ResultData<Boolean> result = authFeignClient.validateToken(validToken);
        
        // 验证结果
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData()).isTrue();
    }
    
    @Test
    @DisplayName("令牌验证 - 服务不可用降级测试")
    void validateTokenFallbackTest() {
        // 模拟服务不可用的情况
        // 这里需要配置Hystrix或Sentinel的降级规则
        
        String token = "any_token";
        ResultData<Boolean> result = authFeignClient.validateToken(token);
        
        // 验证降级处理
        assertThat(result.getCode()).isEqualTo(503);
        assertThat(result.getMessage()).contains("服务暂时不可用");
    }
}
```

## 🔧 模板使用步骤

### 第一步：复制模板
```bash
cp .docs/PROCESS/TEMPLATES/API_TEST_TEMPLATE.md zinfra/moudleDocs/[模块名]/api-test.md
```

### 第二步：替换占位符
使用文本编辑器的查找替换功能，批量替换以下占位符：
- `[模块名称]` → 实际模块名称
- `[模块包名]` → 实际的包名
- `[Controller]` → 实际的控制器类名
- `[FeignClient]` → 实际的Feign客户端类名
- `[接口名称]` → 实际的接口名称

### 第三步：填写接口信息
1. **REST接口列表**: 列出所有对外提供的HTTP接口
2. **Feign客户端列表**: 列出所有内部服务间调用的接口
3. **接口详情**: 为每个接口填写详细的请求和响应信息
4. **测试方法**: 指定对应的测试方法名称

### 第四步：创建测试代码
1. **REST接口测试**: 在 `src/test/java/` 下创建控制器测试类
2. **Feign客户端测试**: 创建Feign客户端测试类
3. **测试数据准备**: 准备测试所需的数据库数据
4. **测试配置**: 配置测试环境和Mock对象

### 第五步：验证完整性
检查以下内容是否完整：
- [ ] 所有占位符都已替换
- [ ] REST接口和Feign接口都已列出
- [ ] 测试方法名称规范
- [ ] 接口详情完整
- [ ] 测试代码结构正确

## 📝 注意事项

### 1. 接口分类原则
- **REST接口**: 对外提供的HTTP接口，可以被前端或其他系统调用
- **Feign客户端**: 内部服务间调用的接口，用于微服务间的通信

### 2. 测试方法命名规范
- 使用驼峰命名法
- 方法名以 `Test` 结尾
- 支持多种测试场景的后缀 (如: `ValidationTest`, `PermissionTest`)

### 3. 测试代码组织
- 按控制器或Feign客户端分组
- 每个接口包含多个测试场景
- 使用 `@DisplayName` 提供清晰的测试描述

### 4. 文档与代码同步
- 文档中的测试方法名称必须与代码中的方法名一致
- 接口路径和参数必须与实际实现一致
- 及时更新文档，确保文档与实际实现同步

---

**示例版本**: v1.0  
**创建时间**: 2025-07-31  
**维护人员**: scccy 