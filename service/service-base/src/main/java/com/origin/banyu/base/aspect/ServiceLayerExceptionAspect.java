//package com.origin.banyu.base.aspect;
//
//import com.origin.banyu.base.exception.BaseExceptionHandler;
//import com.origin.banyu.common.dto.ResultData;
//import com.origin.banyu.common.entity.ErrorCode;
//import com.origin.banyu.common.exception.BusinessException;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
///**
// * Service层异常处理切面
// * 统一捕获Service层异常，调用BaseExceptionHandler处理
// * 让Service层专注于业务逻辑，异常处理由AOP自动完成
// *
// * @author AI Assistant
// * @since 2025-08-14
// */
//@Slf4j
//@Aspect
//@Component
//@Order(2)
//public class ServiceLayerExceptionAspect extends BaseExceptionHandler {
//
//    /**
//     * 环绕通知：处理Service层异常
//     * 拦截所有service包下的方法调用
//     *
//     * @param joinPoint 连接点
//     * @return 方法执行结果
//     * @throws Throwable 异常
//     */
//    @Around("execution(* com.origin.banyu..*.service..*(..))")
//    public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
//            return joinPoint.proceed();
//        } catch (BusinessException be) {
//            // 业务异常直接抛出，不转换
//            log.warn("Service业务异常: {}.{} - {}",
//                joinPoint.getSignature().getDeclaringTypeName(),
//                joinPoint.getSignature().getName(),
//                be.getMessage());
//            throw be;
//        } catch (Exception e) {
//            // 其他异常转换为BusinessException
//            log.error("Service异常: {}.{} - {}",
//                joinPoint.getSignature().getDeclaringTypeName(),
//                joinPoint.getSignature().getName(),
//                e.getMessage(), e);
//
//            // 调用BaseExceptionHandler的方法处理异常
//            ResultData<Object> result = handleException(e);
//            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "服务异常，请稍后重试", e);
//        }
//    }
//}
