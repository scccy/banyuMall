package com.origin.banyu.base.exception;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 基础异常处理器抽象类
 * 提供通用的异常处理方法，作为Service层的异常处理基类
 * 由ServiceLayerExceptionAspect调用，处理Service层的异常
 * 避免重复代码，统一异常处理逻辑
 * 
 * @author AI Assistant
 * @since 2025-08-14
 */
@Slf4j
public abstract class BaseExceptionHandler {
    
    /**
     * 处理业务异常
     * 
     * @param e 业务异常
     * @return 统一返回格式
     */
    protected ResultData<Object> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResultData.fail(e.getErrorCode(), e.getMessage());
    }
    
    /**
     * 处理运行时异常
     * 
     * @param e 运行时异常
     * @return 统一返回格式
     */
    protected ResultData<Object> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return ResultData.fail(ErrorCode.INTERNAL_ERROR, "系统内部错误");
    }
    
    /**
     * 处理系统异常
     * 
     * @param e 系统异常
     * @return 统一返回格式
     */
    protected ResultData<Object> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResultData.fail(ErrorCode.INTERNAL_ERROR, "系统异常，请联系管理员");
    }
}
