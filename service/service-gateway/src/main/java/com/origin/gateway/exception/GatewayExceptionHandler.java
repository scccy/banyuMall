package com.origin.gateway.exception;

import com.origin.common.exception.BusinessException;
import com.origin.common.dto.ResultData;
import com.origin.common.entity.ErrorCode;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Gateway WebFlux异常处理器
 * 处理Gateway服务中的异常，返回统一的错误响应
 * 
 * 职责范围：
 * 1. 处理网关层面的异常（路由异常、连接异常等）
 * 2. 处理业务异常（BusinessException）
 * 3. 处理系统异常（其他未捕获的异常）
 * 4. 确保所有异常都返回统一的ResultData格式
 * 
 * @author origin
 */
@Slf4j
@Component
@Order(-1)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ResultData<Object> resultData;
        
        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            log.warn("Gateway业务异常: {}", businessException.getMessage());
            resultData = ResultData.fail(businessException.getErrorCode(), businessException.getMessage());
        } else {
            log.error("Gateway系统异常: ", ex);
            resultData = ResultData.fail(ErrorCode.INTERNAL_ERROR, "网关服务异常，请稍后重试");
        }

        String responseBody = JSON.toJSONString(resultData);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
} 