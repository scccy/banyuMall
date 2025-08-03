# DEV-014: 异常处理层次规则

**ID**: DEV-014  
**Name**: 异常处理层次规则  
**Status**: Active  
**创建时间**: 2025-01-27  

## 触发情景 (Context/Trigger)
当需要定义异常类、处理异常或进行异常分类时。

## 指令 (Directive)

### 1. 异常层次结构
- **必须 (MUST)** 在 `service-common` 模块中定义基础异常类
- **必须 (MUST)** 在微服务模块中定义业务特定异常类
- **必须 (MUST)** 遵循异常继承层次结构

### 2. Base模块基础异常类
- **BusinessException**: 业务异常基类
- **SystemException**: 系统异常基类
- **ValidationException**: 参数验证异常
- **AuthenticationException**: 认证异常
- **AuthorizationException**: 授权异常
- **DataAccessException**: 数据访问异常

### 3. 微服务模块业务异常类
- **TaskNotFoundException**: 任务不存在异常
- **TaskStatusException**: 任务状态异常
- **TaskValidationException**: 任务验证异常
- **ReviewException**: 审核异常
- **CompletionException**: 完成异常

### 4. 异常处理原则
- **必须 (MUST)** 使用统一的异常处理机制
- **必须 (MUST)** 记录详细的异常日志
- **必须 (MUST)** 返回友好的错误信息
- **禁止 (MUST NOT)** 直接抛出原始异常

## 理由 (Justification)
此规则源于异常处理最佳实践，确保：
1. **异常分类**: 清晰的异常层次结构
2. **错误处理**: 统一的异常处理机制
3. **用户体验**: 友好的错误信息
4. **系统稳定性**: 避免系统崩溃

## 示例 (Examples)

### ✅ 正确做法
```java
// service-common模块中的基础异常
public class BusinessException extends RuntimeException {
    private Integer code;
    private String message;
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}

public class SystemException extends RuntimeException {
    // 系统异常定义
}

// 微服务模块中的业务异常
public class TaskNotFoundException extends BusinessException {
    public TaskNotFoundException(String taskId) {
        super(ErrorCode.TASK_NOT_FOUND, "任务不存在: " + taskId);
    }
}

public class TaskStatusException extends BusinessException {
    public TaskStatusException(String message) {
        super(ErrorCode.TASK_STATUS_INVALID, "任务状态异常: " + message);
    }
}

// 统一异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResultData<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return ResultData.fail(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(SystemException.class)
    public ResultData<Void> handleSystemException(SystemException e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResultData.fail(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后重试");
    }
}
```

### ❌ 错误做法
```java
// 直接抛出原始异常
public void getTask(String taskId) {
    if (task == null) {
        throw new RuntimeException("任务不存在"); // 不应该直接抛出RuntimeException
    }
}

// 在业务模块中重复定义基础异常
public class BusinessException extends RuntimeException {
    // 重复定义基础异常
}
```

## 相关规则
- **LR-009**: 通用类优先common模块规则
- **DEV-005**: 错误处理规范
- **DEV-012**: 统一响应数据类和实体类规范 