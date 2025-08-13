package com.origin.banyu.publisher.exception;

import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 业务层异常环绕切面
 * 统一拦截 service 层异常，做日志与异常转换
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class ServiceLayerExceptionAspect {

    @Around("execution(* com.origin.banyu.publisher.service..*(..)) || execution(* com.origin.banyu.publisher.service.impl..*(..))")
    public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (BusinessException be) {
            throw be;
        } catch (Throwable t) {
            log.error("Service exception at {}.{}: {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), t.getMessage(), t);
            throw new BusinessException(ErrorCode.CORE_SERVICE_ERROR, "服务处理异常", t);
        }
    }
}


