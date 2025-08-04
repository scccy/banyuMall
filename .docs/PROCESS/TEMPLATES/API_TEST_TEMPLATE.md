# [模块名称] API测试文档

## 📋 文档说明

> **文档位置**: zinfra/moudleDocs/{模块名称}/api-test.md  
> **测试代码位置**: src/test/java/com/origin/[模块包名]/controller/  
> **测试方法命名**: 接口名称 + Test (如: loginTest, getUserInfoTest)

## 🔧 REST接口功能列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | 测试方法 | 查看详情 |
|------|----------|----------|----------|----------|----------|----------|----------|
| 1 | [接口名称] | [HTTP方法] | `/service/[entity]/[action]` | [功能描述] | [职责对应] | [methodName]Test | [查看详情](#1-接口名称) |
| 2 | [接口名称] | [HTTP方法] | `/service/[entity]/[action]` | [功能描述] | [职责对应] | [methodName]Test | [查看详情](#2-接口名称) |
| 3 | [接口名称] | [HTTP方法] | `/service/[entity]/[action]` | [功能描述] | [职责对应] | [methodName]Test | [查看详情](#3-接口名称) |

## 🔗 Feign客户端接口列表

| 序号 | 服务名称 | Feign客户端 | 接口路径 | 主要用途 | 测试方法 | 查看详情 |
|------|----------|-------------|----------|----------|----------|----------|
| 1 | [服务名称] | [FeignClient] | `/service/[entity]/[action]` | [用途描述] | [methodName]Test | [查看详情](#feign-1-接口名称) |
| 2 | [服务名称] | [FeignClient] | `/service/[entity]/[action]` | [用途描述] | [methodName]Test | [查看详情](#feign-2-接口名称) |

---

## 🔧 REST接口详情

### 1. [接口名称]

#### 接口描述
[接口的详细功能描述]

#### 请求信息
- **请求方法**: [HTTP方法]
- **请求路径**: `/service/[entity]/[action]`
- **请求头**: 
  ```
  Content-Type: application/json
  Authorization: Bearer [token] (如需要)
  X-Request-ID: [request-id]
  ```

#### 请求参数
```json
{
  "[paramName]": "[paramValue]",
  "[paramName]": "[paramValue]"
}
```

#### 路径参数
- `[paramName]`: [参数描述]

#### 查询参数
- `[paramName]`: [参数描述] (类型: [类型], 必填: [是/否])

#### 响应示例
```json
{
  "code": 200,
  "message": "[响应消息]",
  "data": {
    "[fieldName]": "[fieldValue]",
    "[fieldName]": "[fieldValue]"
  },
  "timestamp": [timestamp]
}
```

#### 错误响应示例
```json
{
  "code": [错误码],
  "message": "[错误消息]",
  "data": null,
  "timestamp": [timestamp]
}
```

#### 测试方法
- **测试类**: `[Controller]Test`
- **测试方法**: `[methodName]Test`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

### 2. [接口名称]

#### 接口描述
[接口的详细功能描述]

#### 请求信息
- **请求方法**: [HTTP方法]
- **请求路径**: `/service/[entity]/[action]`
- **请求头**: 
  ```
  Content-Type: application/json
  Authorization: Bearer [token] (如需要)
  X-Request-ID: [request-id]
  ```

#### 请求参数
```json
{
  "[paramName]": "[paramValue]",
  "[paramName]": "[paramValue]"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "[响应消息]",
  "data": {
    "[fieldName]": "[fieldValue]",
    "[fieldName]": "[fieldValue]"
  },
  "timestamp": [timestamp]
}
```

#### 测试方法
- **测试类**: `[Controller]Test`
- **测试方法**: `[methodName]Test`
- **测试场景**: 
  - 正常请求测试
  - 参数验证测试
  - 权限验证测试
  - 异常情况测试

---

## 🔗 Feign客户端接口详情

### Feign-1. [接口名称]

#### 接口描述
[Feign接口的详细功能描述]

#### 服务信息
- **服务名称**: [service-name]
- **Feign客户端**: [FeignClient]
- **接口路径**: `/service/[entity]/[action]`
- **主要用途**: [用途描述]

#### 接口定义
```java
@FeignClient(name = "[service-name]", fallback = [FeignClientFallback].class)
public interface [FeignClient] {
    
    @[HTTP方法]Mapping("/service/[entity]/[action]")
    ResultData<[ResponseType]> [methodName](@RequestParam String param);
    
    @[HTTP方法]Mapping("/service/[entity]/[action]")
    ResultData<[ResponseType]> [methodName](@RequestBody [RequestType] request);
}
```

#### 请求参数
```json
{
  "[paramName]": "[paramValue]",
  "[paramName]": "[paramValue]"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "[响应消息]",
  "data": {
    "[fieldName]": "[fieldValue]",
    "[fieldName]": "[fieldValue]"
  },
  "timestamp": [timestamp]
}
```

#### 降级处理
```java
@Component
public class [FeignClientFallback] implements [FeignClient] {
    
    @Override
    public ResultData<[ResponseType]> [methodName]([RequestType] request) {
        return ResultData.error(ErrorCode.SERVICE_UNAVAILABLE, "服务暂时不可用");
    }
}
```

#### 测试方法
- **测试类**: `[FeignClient]Test`
- **测试方法**: `[methodName]Test`
- **测试场景**: 
  - 正常调用测试
  - 服务不可用降级测试
  - 超时降级测试
  - 参数验证测试

---

### Feign-2. [接口名称]

#### 接口描述
[Feign接口的详细功能描述]

#### 服务信息
- **服务名称**: [service-name]
- **Feign客户端**: [FeignClient]
- **接口路径**: `/service/[entity]/[action]`
- **主要用途**: [用途描述]

#### 接口定义
```java
@FeignClient(name = "[service-name]", fallback = [FeignClientFallback].class)
public interface [FeignClient] {
    
    @[HTTP方法]Mapping("/service/[entity]/[action]")
    ResultData<[ResponseType]> [methodName](@RequestParam String param);
}
```

#### 请求参数
```json
{
  "[paramName]": "[paramValue]",
  "[paramName]": "[paramValue]"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "[响应消息]",
  "data": {
    "[fieldName]": "[fieldValue]",
    "[fieldName]": "[fieldValue]"
  },
  "timestamp": [timestamp]
}
```

#### 测试方法
- **测试类**: `[FeignClient]Test`
- **测试方法**: `[methodName]Test`
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
class [Controller]Test {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("[接口名称] - 正常请求测试")
    void [methodName]Test() {
        // 测试实现
    }
    
    @Test
    @DisplayName("[接口名称] - 参数验证测试")
    void [methodName]ValidationTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("[接口名称] - 权限验证测试")
    void [methodName]PermissionTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("[接口名称] - 异常情况测试")
    void [methodName]ExceptionTest() {
        // 测试实现
    }
}
```

### Feign客户端测试
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class [FeignClient]Test {
    
    @Autowired
    private [FeignClient] [feignClient];
    
    @Test
    @DisplayName("[Feign接口名称] - 正常调用测试")
    void [methodName]Test() {
        // 测试实现
    }
    
    @Test
    @DisplayName("[Feign接口名称] - 服务不可用降级测试")
    void [methodName]FallbackTest() {
        // 测试实现
    }
    
    @Test
    @DisplayName("[Feign接口名称] - 超时降级测试")
    void [methodName]TimeoutTest() {
        // 测试实现
    }
}
```

## 📊 测试数据准备

### 测试用户数据
```sql
-- 测试用户数据
INSERT INTO sys_user (user_id, username, password, user_type, status) VALUES
('test_user_001', 'testuser1', '$2a$12$...', 2, 1),
('test_user_002', 'testuser2', '$2a$12$...', 3, 1),
('test_user_003', 'disabled_user', '$2a$12$...', 2, 2);
```

### 测试业务数据
```sql
-- 测试业务数据
INSERT INTO [table_name] ([field1], [field2], [field3]) VALUES
('[value1]', '[value2]', '[value3]'),
('[value1]', '[value2]', '[value3]');
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
public class TestConfig {
    
    @Bean
    @Primary
    public [Service] mock[Service]() {
        return Mockito.mock([Service].class);
    }
}
```

## 📈 性能测试

### 接口性能基准
| 接口名称 | 平均响应时间 | 95%响应时间 | 99%响应时间 | 并发用户数 |
|----------|-------------|-------------|-------------|------------|
| [接口名称] | < 100ms | < 200ms | < 500ms | 100 |
| [接口名称] | < 150ms | < 300ms | < 800ms | 50 |

### 性能测试方法
- **测试类**: `[Controller]PerformanceTest`
- **测试方法**: `[methodName]PerformanceTest`
- **测试工具**: JMeter / Gatling
- **测试场景**: 并发请求、压力测试、稳定性测试

## 🚨 错误码说明

| 错误码 | 错误类型 | 错误描述 | 处理建议 |
|--------|----------|----------|----------|
| 400 | 参数错误 | 请求参数格式错误或缺少必填参数 | 检查请求参数格式和完整性 |
| 401 | 认证失败 | 用户未登录或令牌无效 | 重新登录获取有效令牌 |
| 403 | 权限不足 | 用户没有访问该资源的权限 | 联系管理员分配相应权限 |
| 404 | 资源不存在 | 请求的资源不存在 | 检查资源ID是否正确 |
| 500 | 服务器错误 | 服务器内部错误 | 查看服务器日志，联系技术支持 |
| 503 | 服务不可用 | 依赖的服务暂时不可用 | 稍后重试或使用降级方案 |

## 📝 注意事项

1. **测试数据隔离**: 每个测试方法使用独立的测试数据，避免相互影响
2. **测试清理**: 测试完成后及时清理测试数据
3. **Mock使用**: 合理使用Mock对象，避免对外部服务的依赖
4. **测试覆盖**: 确保覆盖正常流程、异常流程、边界条件等场景
5. **性能测试**: 定期进行性能测试，确保接口性能符合要求
6. **文档同步**: 及时更新API文档，确保文档与实际实现一致

---

**文档版本**: v1.0  
**创建时间**: [YYYY-MM-DD]  
**维护人员**: scccy 