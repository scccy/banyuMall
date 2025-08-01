# 统一响应数据类和实体类规范

**ID**: DEV-012  
**Name**: 统一响应数据类和实体类规范  
**Status**: Active  
**创建时间**: 2025-08-01  

## 触发情景 (Context/Trigger)
当开发任何微服务模块的Controller接口或Entity实体类时。

## 指令 (Directive)

### 1. 统一响应数据类规范
- **必须 (MUST)** 所有Controller接口的返回类型都使用 `com.origin.common.dto.ResultData<T>`
- **必须 (MUST)** 使用 `ResultData.success()` 或 `ResultData.fail()` 方法创建响应
- **禁止 (MUST NOT)** 直接返回原始数据类型或自定义响应类
- **必须 (MUST)** 在Controller方法上添加 `@ResponseBody` 注解（如果使用 `@RestController` 则自动包含）

### 2. 统一实体类规范
- **必须 (MUST)** 所有数据库表对应的实体类都继承 `com.origin.common.entity.BaseEntity`
- **必须 (MUST)** 使用 `@Data` 注解自动生成getter/setter方法
- **必须 (MUST)** 使用 `@TableName` 注解指定表名
- **必须 (MUST)** 使用 `@TableId` 注解指定主键字段
- **必须 (MUST)** 使用 `@TableField` 注解指定字段映射关系

### 3. 异常处理规范
- **必须 (MUST)** base包的异常处理仅处理通用基础异常（如参数校验、运行时异常等）
- **必须 (MUST)** 每个微服务的业务异常处理在各自模块中实现
- **必须 (MUST)** 微服务模块创建自己的 `@RestControllerAdvice` 类处理业务异常
- **必须 (MUST)** 业务异常继承 `com.origin.common.exception.BusinessException`
- **禁止 (MUST NOT)** 在base包中添加特定业务的异常处理逻辑

### 4. 工具类规范
- **必须 (MUST)** 通用工具类优先放在 `service-common` 模块
- **必须 (MUST)** 工具类使用 `public static` 方法
- **必须 (MUST)** 工具类添加完整的JavaDoc注释
- **禁止 (MUST NOT)** 在业务模块中重复实现通用工具类

## 代码示例

### Controller响应示例
```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/{id}")
    public ResultData<UserDTO> getUserById(@PathVariable String id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResultData.success(user);
        } catch (UserNotFoundException e) {
            return ResultData.fail(ErrorCode.USER_NOT_FOUND, e.getMessage());
        }
    }
    
    @PostMapping
    public ResultData<String> createUser(@Valid @RequestBody CreateUserRequest request) {
        String userId = userService.createUser(request);
        return ResultData.success("用户创建成功", userId);
    }
}
```

### Entity实体类示例
```java
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    @TableField("username")
    private String username;
    
    @TableField("email")
    private String email;
    
    @TableField("phone")
    private String phone;
}
```

### 微服务异常处理示例
```java
@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultData<Object> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("用户不存在: {}", e.getMessage());
        return ResultData.fail(ErrorCode.USER_NOT_FOUND, e.getMessage());
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResultData<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        log.warn("用户已存在: {}", e.getMessage());
        return ResultData.fail(ErrorCode.USER_ALREADY_EXISTS, e.getMessage());
    }
}
```

### 工具类示例
```java
/**
 * 用户相关工具类
 * 
 * @author origin
 * @since 2025-08-01
 */
public class UserUtils {
    
    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱地址
     * @return true 如果邮箱格式正确，false 否则
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    /**
     * 生成用户昵称
     * 
     * @param username 用户名
     * @return 生成的昵称
     */
    public static String generateNickname(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "用户" + System.currentTimeMillis();
        }
        return username + "_" + System.currentTimeMillis();
    }
}
```

## 理由 (Justification)
此规则源于任务 `task_20250801_1727_microservice_framework_core_issues.md`。在该任务中，需要统一微服务架构的响应格式、实体类结构和异常处理机制，确保系统的一致性和可维护性。

## 合规性检查清单
- [ ] 所有Controller方法返回 `ResultData<T>` 类型
- [ ] 所有Entity类继承 `BaseEntity`
- [ ] 微服务模块有自己的异常处理器
- [ ] 通用工具类放在 `service-common` 模块
- [ ] 业务异常继承 `BusinessException`
- [ ] 使用 `@Data` 注解生成getter/setter
- [ ] 使用MyBatis-Plus注解进行字段映射

## 相关规则
- **DEV-001**: 基础开发规范
- **DEV-005**: 错误处理规范
- **DEV-010**: Lombok @Data注解使用规范 