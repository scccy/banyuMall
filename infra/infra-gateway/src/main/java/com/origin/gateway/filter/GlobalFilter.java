package com.origin.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * 全局过滤器
 * 
 * @author origin
 */
@Slf4j
@Component
public class GlobalFilter implements  Ordered {
    

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        
        log.info("Gateway Request - Time: {}, Method: {}, Path: {}, RemoteAddress: {}", 
                LocalDateTime.now(), method, path, request.getRemoteAddress());
        
        // 添加请求头
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Gateway-Time", String.valueOf(System.currentTimeMillis()))
                .build();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    log.info("Gateway Response - Time: {}, Method: {}, Path: {}, Status: {}", 
                            LocalDateTime.now(), method, path, 
                            exchange.getResponse().getStatusCode());
                }));
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
} 