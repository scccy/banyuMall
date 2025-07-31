package com.origin.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 全局过滤器 - 处理链路追踪和请求日志
 * 
 * @author origin
 */
@Slf4j
@Component
public class GlobalFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {
    
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String CLIENT_IP_HEADER = "X-Client-IP";
    private static final String USER_AGENT_HEADER = "X-User-Agent";
    private static final String REQUEST_TIME_HEADER = "X-Request-Time";
    private static final String SERVICE_NAME_HEADER = "X-Service-Name";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        long startTime = System.currentTimeMillis();
        
        // 生成请求ID
        String requestId = generateRequestId(request);
        
        // 获取客户端信息
        String clientIp = getClientIp(request);
        String userAgent = getUserAgent(request);
        
        // 记录请求日志
        log.info("Gateway Request - RequestId: {}, Time: {}, Method: {}, Path: {}, ClientIP: {}, UserAgent: {}", 
                requestId, LocalDateTime.now(), method, path, clientIp, userAgent);
        
        // 添加调试信息
        log.debug("Gateway Debug - Original Path: {}, URI: {}, Query: {}", 
                path, request.getURI(), request.getQueryParams());
        
        // 添加链路追踪请求头
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .header(CLIENT_IP_HEADER, clientIp)
                .header(USER_AGENT_HEADER, userAgent)
                .header(REQUEST_TIME_HEADER, String.valueOf(startTime))
                .header(SERVICE_NAME_HEADER, "service-gateway")
                .header("X-Gateway-Time", String.valueOf(System.currentTimeMillis()))
                .build();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    log.info("Gateway Response - RequestId: {}, Time: {}, Method: {}, Path: {}, Status: {}, Duration: {}ms", 
                            requestId, LocalDateTime.now(), method, path, 
                            exchange.getResponse().getStatusCode(), duration);
                }));
    }
    
    /**
     * 生成请求ID
     */
    private String generateRequestId(ServerHttpRequest request) {
        // 优先使用请求头中的请求ID
        String existingRequestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (existingRequestId != null && !existingRequestId.trim().isEmpty()) {
            return existingRequestId;
        }
        
        // 生成新的请求ID
        return "req-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 获取客户端IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String clientIp = request.getHeaders().getFirst("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeaders().getFirst("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        }
        return clientIp;
    }
    
    /**
     * 获取用户代理
     */
    private String getUserAgent(ServerHttpRequest request) {
        String userAgent = request.getHeaders().getFirst("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
} 