package com.origin.auth.interceptor;

import com.origin.auth.util.JwtUtil;
import com.origin.auth.util.JwtTokenManager;
import com.origin.common.exception.BusinessException;
import com.origin.common.entity.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器，用于验证请求中的JWT令牌
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final JwtTokenManager jwtTokenManager;

    @Value("${jwt.header:Authorization}")
    private String tokenHeader;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取token
        String token = request.getHeader(tokenHeader);
        
        // 如果请求头中没有token，则尝试从请求参数中获取
        if (!StringUtils.hasText(token)) {
            token = request.getParameter("token");
        }
        
        // 如果token为空，则抛出异常
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未提供认证令牌");
        }
        
        // 检查token是否在黑名单中
        if (jwtTokenManager.isBlacklisted(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "认证令牌已失效");
        }

        // 验证token是否有效
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "认证令牌无效或已过期");
        }
        
        // 从token中获取用户ID和用户名，并设置到请求属性中
        String userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        
        return true;
    }
} 