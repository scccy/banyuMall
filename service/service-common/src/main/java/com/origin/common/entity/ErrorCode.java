package com.origin.common.entity;

/**
 * 错误码枚举
 * 定义系统中所有可能的错误码
 */
public enum ErrorCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误 (4xx)
    PARAM_ERROR(400, "参数错误"),
    PARAMS_ERROR(400, "参数错误"), // 别名，保持兼容性
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    CONFLICT(409, "资源冲突"),
    
    // 服务器错误 (5xx)
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    // 业务错误 (1000-9999)
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    ACCOUNT_DISABLED(1004, "账号已被禁用"),
    TOKEN_EXPIRED(1005, "令牌已过期"),
    TOKEN_INVALID(1006, "令牌无效"),
    
    // 积分相关错误 (2000-2999)
    INSUFFICIENT_POINTS(2001, "积分不足"),
    POINT_TRANSACTION_FAILED(2002, "积分交易失败"),
    
    // 任务相关错误 (3000-3999)
    TASK_NOT_FOUND(3001, "任务不存在"),
    TASK_ALREADY_COMPLETED(3002, "任务已完成"),
    TASK_EXPIRED(3003, "任务已过期"),
    TASK_STATUS_INVALID(3004, "任务状态无效"),
    TASK_REVIEW_NOT_FOUND(3005, "任务审核记录不存在"),
    TASK_REVIEW_STATUS_INVALID(3006, "任务审核状态无效"),
    TASK_COMPLETION_NOT_FOUND(3007, "任务完成记录不存在"),
    
    // 文件相关错误 (4000-4999)
    FILE_UPLOAD_FAILED(4001, "文件上传失败"),
    FILE_NOT_FOUND(4002, "文件不存在"),
    FILE_SIZE_EXCEEDED(4003, "文件大小超限");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
} 