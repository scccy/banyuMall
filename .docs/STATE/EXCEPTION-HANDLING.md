# 异常处理架构基线

## 概述
本文档描述了半语积分商城项目的异常处理架构，包括错误码定义、业务异常类和全局异常处理机制。

## 异常处理架构

### 1. 错误码定义 (ErrorCode)
**位置**: `service/service-base/src/main/java/com/origin/common/exception/ErrorCode.java`

**设计原则**:
- 使用枚举类型定义所有错误码
- 错误码分类：HTTP状态码(200-599)、业务错误码(1000-9999)
- 每个错误码包含code和message两个属性

**错误码分类**:
- **成功**: 200
- **客户端错误**: 400-499
- **服务器错误**: 500-599
- **业务错误**: 1000-9999
  - 用户相关: 1000-1999
  - 积分相关: 2000-2999
  - 任务相关: 3000-3999
  - 文件相关: 4000-4999

### 2. 业务异常类 (BusinessException)
**位置**: `service/service-base/src/main/java/com/origin/common/exception/BusinessException.java`

**设计原则**:
- 继承RuntimeException，避免强制异常处理
- 包含ErrorCode和自定义消息
- 支持异常链传递

**构造函数**:
- `BusinessException(ErrorCode errorCode)`
- `BusinessException(ErrorCode errorCode, String message)`
- `BusinessException(ErrorCode errorCode, String message, Throwable cause)`
- `BusinessException(ErrorCode errorCode, Throwable cause)`

### 3. 统一响应格式 (ResultData)
**位置**: `service/service-base/src/main/java/com/origin/common/ResultData.java`

**设计原则**:
- 统一的API响应格式
- 支持成功和失败响应
- 支持ErrorCode和自定义消息

**主要方法**:
- `ResultData.ok()` - 成功响应
- `ResultData.fail(ErrorCode errorCode)` - 使用ErrorCode的失败响应
- `ResultData.fail(ErrorCode errorCode, String message)` - 自定义消息的失败响应

### 4. 全局异常处理
**位置**: `service/service-auth/src/main/java/com/origin/auth/exception/AuthExceptionHandler.java`

**设计原则**:
- 使用@ExceptionHandler注解处理特定异常
- 将异常转换为统一的ResultData格式
- 记录异常日志用于调试

## 使用示例

### 抛出业务异常
```java
// 在业务逻辑中抛出异常
throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");

// 带原因的异常
throw new BusinessException(ErrorCode.PARAM_ERROR, "参数验证失败", cause);
```

### 处理异常
```java
@ExceptionHandler(BusinessException.class)
public ResultData handleBusinessException(BusinessException e) {
    return ResultData.fail(e.getErrorCode(), e.getMessage());
}
```

### 返回统一响应
```java
// 成功响应
return ResultData.ok("操作成功", data);

// 失败响应
return ResultData.fail(ErrorCode.PARAM_ERROR, "参数错误");
```

## 错误码列表

### HTTP状态码
- `SUCCESS(200, "操作成功")`
- `PARAM_ERROR(400, "参数错误")`
- `UNAUTHORIZED(401, "未授权")`
- `FORBIDDEN(403, "禁止访问")`
- `NOT_FOUND(404, "资源不存在")`
- `INTERNAL_ERROR(500, "服务器内部错误")`

### 业务错误码
- `USER_NOT_FOUND(1001, "用户不存在")`
- `USER_ALREADY_EXISTS(1002, "用户已存在")`
- `PASSWORD_ERROR(1003, "密码错误")`
- `ACCOUNT_DISABLED(1004, "账号已被禁用")`
- `TOKEN_EXPIRED(1005, "令牌已过期")`
- `TOKEN_INVALID(1006, "令牌无效")`
- `INSUFFICIENT_POINTS(2001, "积分不足")`
- `TASK_NOT_FOUND(3001, "任务不存在")`
- `FILE_UPLOAD_FAILED(4001, "文件上传失败")`

## 最佳实践

1. **异常粒度**: 为不同的业务场景定义具体的错误码
2. **异常信息**: 提供有意义的错误消息，便于调试和用户理解
3. **异常链**: 保留原始异常信息，便于问题排查
4. **日志记录**: 在异常处理中记录详细的错误日志
5. **安全考虑**: 避免在错误消息中暴露敏感信息

## 版本历史
- 2025-01-27: 创建ErrorCode和BusinessException类
- 2025-01-27: 修复ResultData中的导入问题
- 2025-01-27: 建立异常处理架构基线 