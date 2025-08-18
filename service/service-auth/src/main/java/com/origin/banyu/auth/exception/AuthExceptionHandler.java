//package com.origin.banyu.auth.exception;
//
//import com.origin.banyu.common.dto.ResultData;
//import com.origin.banyu.common.entity.ErrorCode;
//import com.origin.banyu.common.exception.BusinessException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
///**
// * 认证异常处理器
// * 处理认证服务相关的业务异常
// *
// * 职责范围：
// * 1. 处理认证相关异常（AuthenticationException、BadCredentialsException）
// * 2. 处理授权相关异常（AccessDeniedException）
// * 3. 处理业务异常（BusinessException）
// * 4. 确保所有异常都返回统一的ResultData格式
// *
// * @author origin
// * @since 2024-12-19
// */
//@Slf4j
//@RestControllerAdvice
//public class AuthExceptionHandler {
//
//    /**
//     * 处理认证异常
//     *
//     * @param e 认证异常
//     * @return 错误响应
//     */
//    @ExceptionHandler({IllegalArgumentException.class, SecurityException.class})
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleAuthenticationException(Exception e) {
//        log.error("认证异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.UNAUTHORIZED, "认证失败: " + e.getMessage());
//    }
//
//    /**
//     * 处理业务异常
//     *
//     * @param e 业务异常
//     * @return 错误响应
//     */
//    @ExceptionHandler(BusinessException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResultData<Object> handleBusinessException(BusinessException e) {
//        log.error("业务异常: {}", e.getMessage());
//        return ResultData.fail(e.getErrorCode(), e.getMessage());
//    }
//
//    /**
//     * 处理授权异常
//     *
//     * @param e 授权异常
//     * @return 错误响应
//     */
//    @ExceptionHandler(SecurityException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public ResultData<Object> handleAccessDeniedException(SecurityException e) {
//        log.error("授权异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.FORBIDDEN, "没有权限访问该资源");
//    }
//
//    /**
//     * 处理运行时异常
//     *
//     * @param e 运行时异常
//     * @return 错误响应
//     */
//    @ExceptionHandler(RuntimeException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResultData<Object> handleRuntimeException(RuntimeException e) {
//        log.error("运行时异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.INTERNAL_ERROR, e.getMessage());
//    }
//
//    // ========== 新增异常处理方法 ==========
//
//    /**
//     * 处理JWT解析异常
//     */
//    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleJwtException(io.jsonwebtoken.JwtException e) {
//        log.error("JWT解析异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.TOKEN_INVALID, "令牌格式无效");
//    }
//
//    /**
//     * 处理JWT过期异常
//     */
//    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleExpiredJwtException(io.jsonwebtoken.ExpiredJwtException e) {
//        log.error("JWT过期异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.TOKEN_EXPIRED, "令牌已过期");
//    }
//
//    /**
//     * 处理JWT签名异常
//     */
//    @ExceptionHandler(io.jsonwebtoken.SignatureException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleSignatureException(io.jsonwebtoken.SignatureException e) {
//        log.error("JWT签名异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.TOKEN_INVALID, "令牌签名无效");
//    }
//
//    /**
//     * 处理JWT格式异常
//     */
//    @ExceptionHandler(io.jsonwebtoken.MalformedJwtException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleMalformedJwtException(io.jsonwebtoken.MalformedJwtException e) {
//        log.error("JWT格式异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.TOKEN_INVALID, "令牌格式错误");
//    }
//
//    /**
//     * 处理JWT声明异常
//     */
//    @ExceptionHandler(io.jsonwebtoken.PrematureJwtException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handlePrematureJwtException(io.jsonwebtoken.PrematureJwtException e) {
//        log.error("JWT声明异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.TOKEN_INVALID, "令牌尚未生效");
//    }
//
//    /**
//     * 处理JWT不支持异常
//     */
//    @ExceptionHandler(io.jsonwebtoken.UnsupportedJwtException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleUnsupportedJwtException(io.jsonwebtoken.UnsupportedJwtException e) {
//        log.error("JWT不支持异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.TOKEN_INVALID, "令牌类型不支持");
//    }
//
//    /**
//     * 处理第三方配置异常
//     */
//    @ExceptionHandler(org.springframework.beans.factory.BeanCreationException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResultData<Object> handleBeanCreationException(org.springframework.beans.factory.BeanCreationException e) {
//        log.error("第三方配置异常: {}", e.getMessage());
//        return ResultData.fail(ErrorCode.THIRD_PARTY_SERVICE_CONFIG_ERROR, "第三方服务配置错误");
//    }
//
//    /**
//     * 处理用户账号被锁定异常
//     */
//    @ExceptionHandler(IllegalStateException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleLockedException(IllegalStateException e) {
//        if (e.getMessage() != null && e.getMessage().contains("locked")) {
//            log.error("用户账号被锁定: {}", e.getMessage());
//            return ResultData.fail(ErrorCode.ACCOUNT_DISABLED, "账号已被锁定，请联系管理员");
//        }
//        throw e; // 重新抛出非锁定相关的异常
//    }
//
//    /**
//     * 处理用户账号被禁用异常
//     */
//    @ExceptionHandler(IllegalStateException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleDisabledException(IllegalStateException e) {
//        if (e.getMessage() != null && e.getMessage().contains("disabled")) {
//            log.error("用户账号被禁用: {}", e.getMessage());
//            return ResultData.fail(ErrorCode.ACCOUNT_DISABLED, "账号已被禁用，请联系管理员");
//        }
//        throw e; // 重新抛出非禁用相关的异常
//    }
//
//    /**
//     * 处理用户账号过期异常
//     */
//    @ExceptionHandler(IllegalStateException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleAccountExpiredException(IllegalStateException e) {
//        if (e.getMessage() != null && e.getMessage().contains("expired")) {
//            log.error("用户账号过期: {}", e.getMessage());
//            return ResultData.fail(ErrorCode.ACCOUNT_DISABLED, "账号已过期，请联系管理员");
//        }
//        throw e; // 重新抛出非过期相关的异常
//    }
//
//    /**
//     * 处理用户凭据过期异常
//     */
//    @ExceptionHandler(IllegalStateException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResultData<Object> handleCredentialsExpiredException(IllegalStateException e) {
//        if (e.getMessage() != null && e.getMessage().contains("credentials")) {
//            log.error("用户凭据过期: {}", e.getMessage());
//            return ResultData.fail(ErrorCode.TOKEN_INVALID, "密码已过期，请修改密码");
//        }
//        throw e; // 重新抛出非凭据相关的异常
//    }
//}