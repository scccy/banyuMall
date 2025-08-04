# API路由设计规则

## 📋 规则概述

**ID**: API-001  
**Name**: API路由设计规则  
**Status**: Active  
**创建时间**: 2025-08-04  

## 🎯 核心原则

### 1. 路由命名规范
- **路由格式**: 使用 `/service/<entity>` 格式
- **避免格式**: 不使用 `/service-user/<entity>` 格式
- **RESTful设计**: 遵循REST设计原则
- **版本控制**: 支持API版本管理

### 2. 响应格式规范
- **统一响应**: 使用 `success()` 方法返回成功响应
- **错误处理**: 统一的错误响应格式
- **状态码**: 使用标准HTTP状态码
- **数据格式**: 使用FastJSON2进行序列化

### 3. 技术选择规范
- **JSON处理**: 使用 FastJSON2 而非 Jackson
- **序列化**: 优先使用 FastJSON2 进行JSON序列化
- **文档生成**: 使用 Knife4j 生成API文档

## 🛣️ 路由设计规范

### 1. 基础路由格式
```
/service/<entity>                    # 基础实体路由
/service/<entity>/<id>              # 单个实体操作
/service/<entity>/<action>          # 特定操作
/service/<entity>/<id>/<sub-entity> # 子实体操作
```

### 2. 路由示例
```java
// 用户服务路由
@RestController
@RequestMapping("/service/user")
public class UserController {
    
    @GetMapping("/{id}")                    // GET /service/user/{id}
    public ResultData<UserInfo> getUserById(@PathVariable String id) {
        return ResultData.success(userService.getById(id));
    }
    
    @PostMapping                            // POST /service/user
    public ResultData<UserInfo> createUser(@RequestBody UserRequest request) {
        return ResultData.success(userService.create(request));
    }
    
    @PutMapping("/{id}")                    // PUT /service/user/{id}
    public ResultData<UserInfo> updateUser(@PathVariable String id, @RequestBody UserRequest request) {
        return ResultData.success(userService.update(id, request));
    }
    
    @DeleteMapping("/{id}")                 // DELETE /service/user/{id}
    public ResultData<Void> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return ResultData.success();
    }
    
    @GetMapping("/{id}/profile")            // GET /service/user/{id}/profile
    public ResultData<UserProfile> getUserProfile(@PathVariable String id) {
        return ResultData.success(userService.getProfile(id));
    }
}
```

### 3. 任务服务路由示例
```java
// 任务服务路由
@RestController
@RequestMapping("/service/publisher/task")
public class TaskController {
    
    @GetMapping("/{id}")                    // GET /service/publisher/task/{id}
    public ResultData<TaskInfo> getTaskById(@PathVariable String id) {
        return ResultData.success(taskService.getById(id));
    }
    
    @PostMapping                            // POST /service/publisher/task
    public ResultData<TaskInfo> createTask(@RequestBody TaskRequest request) {
        return ResultData.success(taskService.create(request));
    }
    
    @PostMapping("/{id}/complete")          // POST /service/publisher/task/{id}/complete
    public ResultData<TaskCompletion> completeTask(@PathVariable String id, @RequestBody CompletionRequest request) {
        return ResultData.success(taskService.complete(id, request));
    }
    
    @GetMapping("/{id}/completions")        // GET /service/publisher/task/{id}/completions
    public ResultData<List<TaskCompletion>> getTaskCompletions(@PathVariable String id) {
        return ResultData.success(taskService.getCompletions(id));
    }
}
```

## 📡 响应格式规范

### 1. 统一响应格式
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultData<T> {
    private Integer code;           // 响应码
    private String message;         // 响应消息
    private T data;                // 响应数据
    private Long timestamp;        // 时间戳
    
    // 成功响应
    public static <T> ResultData<T> success() {
        return new ResultData<>(200, "操作成功", null, System.currentTimeMillis());
    }
    
    public static <T> ResultData<T> success(T data) {
        return new ResultData<>(200, "操作成功", data, System.currentTimeMillis());
    }
    
    public static <T> ResultData<T> success(String message, T data) {
        return new ResultData<>(200, message, data, System.currentTimeMillis());
    }
    
    // 错误响应
    public static <T> ResultData<T> error(String message) {
        return new ResultData<>(500, message, null, System.currentTimeMillis());
    }
    
    public static <T> ResultData<T> error(Integer code, String message) {
        return new ResultData<>(code, message, null, System.currentTimeMillis());
    }
}
```

### 2. 响应示例
```json
// 成功响应
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "userId": "123456",
        "username": "testuser",
        "email": "test@example.com"
    },
    "timestamp": 1640995200000
}

// 错误响应
{
    "code": 400,
    "message": "参数验证失败",
    "data": null,
    "timestamp": 1640995200000
}
```

## 🔧 技术实现规范

### 1. FastJSON2配置
```java
@Configuration
public class FastJsonConfig {
    
    @Bean
    @Primary
    public HttpMessageConverter<Object> fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        
        // 配置序列化特性
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        config.setCharset(StandardCharsets.UTF_8);
        config.setWriteMapNullValue(false);
        config.setWriteNullListAsEmpty(true);
        config.setWriteNullStringAsEmpty(true);
        
        converter.setFastJsonConfig(config);
        return converter;
    }
}
```

### 2. 控制器基类
```java
@RestController
public abstract class BaseController {
    
    /**
     * 成功响应
     */
    protected <T> ResultData<T> success() {
        return ResultData.success();
    }
    
    /**
     * 成功响应（带数据）
     */
    protected <T> ResultData<T> success(T data) {
        return ResultData.success(data);
    }
    
    /**
     * 成功响应（带消息和数据）
     */
    protected <T> ResultData<T> success(String message, T data) {
        return ResultData.success(message, data);
    }
    
    /**
     * 错误响应
     */
    protected <T> ResultData<T> error(String message) {
        return ResultData.error(message);
    }
    
    /**
     * 错误响应（带状态码）
     */
    protected <T> ResultData<T> error(Integer code, String message) {
        return ResultData.error(code, message);
    }
}
```

## 📚 API文档规范

### 1. Knife4j配置
```java
@Configuration
@EnableOpenApi
public class Knife4jConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BanyuMall API")
                        .version("1.0.0")
                        .description("BanyuMall微服务API文档")
                        .contact(new Contact()
                                .name("scccy")
                                .email("scccy@example.com")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("开发环境"),
                        new Server().url("https://api.banyumall.com").description("生产环境")
                ));
    }
}
```

### 2. 接口文档注解
```java
@RestController
@RequestMapping("/service/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResultData<UserInfo> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable String id) {
        return ResultData.success(userService.getById(id));
    }
    
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public ResultData<UserInfo> createUser(
            @Parameter(description = "用户信息", required = true) @RequestBody UserRequest request) {
        return ResultData.success(userService.create(request));
    }
}
```

## 🚫 禁止事项

### 严格禁止
1. **路由格式错误** - 不使用 `/service-user/<entity>` 格式
2. **响应格式不统一** - 不使用 `success()` 方法
3. **JSON处理混乱** - 不使用Jackson处理JSON
4. **缺少文档注解** - 重要接口必须有文档注解

### 不推荐
1. **路由过深** - 避免路由层级过深
2. **参数过多** - 避免URL参数过多
3. **缺少验证** - 避免缺少参数验证
4. **错误处理不当** - 避免错误处理不规范

## ✅ 推荐事项

### 最佳实践
1. **RESTful设计** - 遵循REST设计原则
2. **版本管理** - 支持API版本管理
3. **参数验证** - 完善的参数验证机制
4. **错误处理** - 统一的错误处理机制
5. **文档同步** - 代码和文档同步更新

### 设计模式
1. **统一响应模式** - 使用统一的响应格式
2. **参数验证模式** - 使用注解进行参数验证
3. **异常处理模式** - 使用全局异常处理
4. **文档生成模式** - 使用注解生成文档

---

**版本**: v1.0  
**创建时间**: 2025-08-04  
**最后更新**: 2025-08-04  
**维护者**: scccy 