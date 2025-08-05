# 异常错误处理规则

## 📋 规则概述

**ID**: EXCEPTION-001  
**Name**: 异常错误处理规则  
**Status**: Active  
**创建时间**: 2025-07-31  

## 🎯 核心原则

### 1. 统一异常处理原则
- **全局异常处理**: 使用 `@ControllerAdvice` 统一处理异常
- **统一响应格式**: 所有异常都返回 `ResultData` 格式
- **错误码规范**: 使用 `ErrorCode` 枚举定义错误码
- **日志记录**: 异常必须记录详细日志

### 2. 异常分类原则
- **业务异常**: 使用 `BusinessException` 处理业务逻辑异常
- **系统异常**: 使用 `GlobalExceptionHandler` 处理系统异常
- **参数异常**: 使用 `@Valid` 和 `@Validated` 进行参数验证
- **权限异常**: 使用专门的权限异常类

### 3. 错误码分配原则
- **HTTP状态码**: 使用标准HTTP状态码表示基础错误
- **业务错误码**: 使用1000-9999范围内的错误码表示业务错误
- **模块隔离**: 不同模块使用不同的错误码范围
- **语义明确**: 错误码含义要明确，便于理解和维护

## 🏗️ 异常处理架构

### 1. 异常处理层次
```
Controller层
    ↓
Service层
    ↓
Mapper层
    ↓
数据库层
```

### 2. 异常处理流程
```
1. 异常发生
   ↓
2. 异常拦截器捕获
   ↓
3. 异常分类处理
   ↓
4. 错误码映射
   ↓
5. 错误信息格式化
   ↓
6. 返回统一错误响应
```

## 📝 异常类设计规范

### 1. 基础异常类
```java
// 业务异常基类
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
```

### 2. 模块异常类
```java
// 用户模块异常
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }
    
    public UserNotFoundException(String userId, String message) {
        super(ErrorCode.USER_NOT_FOUND, "用户ID: " + userId + ", " + message);
    }
}
```

### 3. 异常类命名规范
- **异常类名**: 使用 `XxxException` 格式
- **包路径**: `com.origin.模块名.exception`
- **继承关系**: 继承自 `BusinessException`
- **构造函数**: 提供多个构造函数重载

## 🔢 错误码设计规范

### 1. 错误码分类
- **HTTP状态码**: 200-599，标准HTTP状态码
- **用户相关**: 1000-1999，用户管理相关错误
- **认证相关**: 1100-1199，认证授权相关错误
- **核心服务**: 2000-3999，核心业务服务错误
- **第三方服务**: 4000-4999，第三方服务错误
- **网关相关**: 5000-5999，网关服务相关错误

### 2. 错误码命名规范
- **英文命名**: 使用英文单词或短语命名
- **驼峰命名**: 使用驼峰命名法
- **语义清晰**: 错误码名称要能清楚表达错误含义
- **避免缩写**: 尽量避免使用缩写，提高可读性

## 🔧 异常处理实现规范

### 1. Service层异常处理
```java
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public UserResponse getUserById(String userId) {
        // 参数验证
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        
        // 业务逻辑
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId, "用户不存在");
        }
        
        // 返回结果
        return convertToResponse(user);
    }
}
```

### 2. Controller层异常处理
```java
@RestController
@RequestMapping("/service/user")
public class UserController {
    
    @GetMapping("/{userId}")
    public ResultData<UserResponse> getUserById(@PathVariable String userId) {
        try {
            UserResponse response = userService.getUserById(userId);
            return ResultData.ok(response);
        } catch (BusinessException e) {
            // 业务异常由全局异常处理器处理
            throw e;
        } catch (Exception e) {
            // 系统异常记录日志后重新抛出
            log.error("获取用户信息失败，用户ID: {}", userId, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "获取用户信息失败");
        }
    }
}
```

## 📊 日志记录规范

### 1. 异常日志级别
- **DEBUG**: 调试信息，开发环境使用
- **INFO**: 一般信息，正常业务流程
- **WARN**: 警告信息，业务异常但可恢复
- **ERROR**: 错误信息，系统异常需要关注

### 2. 日志记录内容
```java
// 业务异常日志
log.warn("用户不存在: 用户ID={}, 错误信息={}", userId, e.getMessage());

// 系统异常日志
log.error("数据库连接失败: 数据库={}, 错误信息={}", dbName, e.getMessage(), e);
```

## 🚫 禁止事项

### 严格禁止
1. **吞掉异常**: 不允许捕获异常后不处理或记录
2. **暴露敏感信息**: 不允许在错误信息中暴露系统内部信息
3. **硬编码错误信息**: 不允许硬编码错误信息，必须使用错误码
4. **忽略异常**: 不允许忽略重要的异常信息

### 不推荐
1. **过度捕获**: 避免过度捕获异常，让异常自然传播
2. **重复处理**: 避免在多个层次重复处理同一异常
3. **复杂异常链**: 避免创建过于复杂的异常继承链
4. **异常作为控制流**: 避免使用异常作为正常的控制流程

## ✅ 推荐事项

### 最佳实践
1. **统一异常处理**: 使用全局异常处理器统一处理异常
2. **错误码管理**: 使用枚举统一管理错误码
3. **日志记录**: 异常必须记录详细日志
4. **用户友好**: 错误信息要对用户友好
5. **监控告警**: 重要异常要配置监控告警

---

**版本**: v1.0  
**创建日期**: 2025-07-31  
**最后更新**: 2025-07-31  
**维护者**: scccy 