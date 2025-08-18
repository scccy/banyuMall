//package com.origin.banyu.base.aspect;
//
//
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
// * Controller层异常处理切面
// * 统一捕获Controller层异常，确保异常被正确处理
// * 作为Controller层异常处理的兜底机制
// *
// * @author AI Assistant
// * @since 2025-08-14
// */
//@Slf4j
//@Aspect
//@Component
//@Order(1)
//public class ControllerExceptionAspect {
//
//    /**
//     * 环绕通知：处理Controller层异常
//     * 拦截所有controller包下的方法调用
//     *
//     * @param joinPoint 连接点
//     * @return 方法执行结果
//     * @throws Throwable 异常
//     */
//    @Around("execution(* com.origin.banyu..*.controller..*(..))")
//    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
//            return joinPoint.proceed();
//        } catch (BusinessException be) {
//            // 业务异常直接抛出，由ExceptionHandler处理
//            log.warn("Controller业务异常: {}.{} - {}",
//                joinPoint.getSignature().getDeclaringTypeName(),
//                joinPoint.getSignature().getName(),
//                be.getMessage());
//            throw be;
//        } catch (Exception e) {
//            // 其他异常转换为BusinessException
//            log.error("Controller异常: {}.{} - {}",
//                joinPoint.getSignature().getDeclaringTypeName(),
//                joinPoint.getSignature().getName(),
//                e.getMessage(), e);
//            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "请求处理异常，请稍后重试", e);
//        }
//    }
//}
