package com.origin.banyu.user.exception;

import com.origin.banyu.base.exception.BaseExceptionHandler;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 用户服务异常处理器
 * 继承BaseExceptionHandler，实现优先本模块的异常捕捉
 * 
 * 职责范围：
 * 1. 优先处理用户相关的业务异常（BusinessException）
 * 2. 确保所有异常都返回统一的ResultData格式
 * 3. 继承BaseExceptionHandler，复用通用异常处理逻辑
 * 4. 作为Controller层异常处理器，优先级高于base模块的GlobalExceptionHandler
 * 
 * @author scccy
 * @since 2025-08-14
 */
@Slf4j
@RestControllerAdvice
public class UserExceptionHandler extends BaseExceptionHandler {

    /**
     * 处理用户业务异常
     * 所有业务异常都使用统一的BusinessException，无需区分具体类型
     */
    @ExceptionHandler(BusinessException.class)
    public ResultData<Object> handleBusinessException(BusinessException e) {
        log.warn("用户业务异常: {}", e.getMessage());
        return ResultData.fail(e.getErrorCode(), e.getMessage());
    }
}
