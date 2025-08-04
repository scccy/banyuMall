package com.origin.gateway;

import com.origin.common.dto.ResultData;
import com.origin.common.exception.BusinessException;
import com.origin.common.entity.ErrorCode;
import com.origin.gateway.exception.GatewayExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Gateway异常处理器测试类
 * 
 * @author scccy
 */
@SpringBootTest
@ActiveProfiles("test")
class GatewayExceptionHandlerTest {

    @Autowired
    private GatewayExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        // 设置测试环境
    }

    @Test
    @DisplayName("异常处理 - 业务异常处理测试")
    void businessExceptionTest() {
        // 创建业务异常
        BusinessException businessException = new BusinessException(ErrorCode.PARAM_ERROR, "参数错误");

        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from( request);

        // 执行异常处理
        Mono<Void> result = exceptionHandler.handle(exchange, businessException);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证响应状态
        assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        // 验证响应体内容 - 使用DataBufferUtils读取响应体
        StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
                .expectNextMatches(dataBuffer -> {
                    String content = dataBuffer.toString(StandardCharsets.UTF_8);
                    return content.contains("参数错误") && 
                           content.contains(String.valueOf(ErrorCode.PARAM_ERROR.getCode()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("异常处理 - 系统异常处理测试")
    void systemExceptionTest() {
        // 创建系统异常
        RuntimeException systemException = new RuntimeException("系统内部错误");

        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

        // 执行异常处理
        Mono<Void> result = exceptionHandler.handle(exchange, systemException);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证响应状态
        assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        // 验证响应体内容
        StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
                .expectNextMatches(dataBuffer -> {
                    String content = dataBuffer.toString(StandardCharsets.UTF_8);
                    return content.contains("网关服务异常，请稍后重试") && 
                           content.contains(String.valueOf(ErrorCode.INTERNAL_ERROR.getCode()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("异常处理 - 异常响应格式测试")
    void exceptionResponseFormatTest() {
        // 创建业务异常
        BusinessException businessException = new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");

        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

        // 执行异常处理
        Mono<Void> result = exceptionHandler.handle(exchange, businessException);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证响应体内容
        StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
                .expectNextMatches(dataBuffer -> {
                    String content = dataBuffer.toString(StandardCharsets.UTF_8);
                    return content.contains("\"code\"") &&
                           content.contains("\"message\"") &&
                           content.contains("\"data\"") &&
                           content.contains("\"timestamp\"") &&
                           content.contains("用户不存在") &&
                           content.contains(String.valueOf(ErrorCode.USER_NOT_FOUND.getCode()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("异常处理 - 空异常测试")
    void nullExceptionTest() {
        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

        // 执行异常处理（空异常）
        Mono<Void> result = exceptionHandler.handle(exchange, null);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证响应状态
        assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        // 验证响应体内容
        StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
                .expectNextMatches(dataBuffer -> {
                    String content = dataBuffer.toString(StandardCharsets.UTF_8);
                    return content.contains("网关服务异常，请稍后重试") && 
                           content.contains(String.valueOf(ErrorCode.INTERNAL_ERROR.getCode()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("异常处理 - 不同错误码测试")
    void differentErrorCodeTest() {
        // 测试不同的错误码
        ErrorCode[] errorCodes = {
                ErrorCode.PARAM_ERROR,
                ErrorCode.USER_NOT_FOUND,
                ErrorCode.UNAUTHORIZED,
                ErrorCode.FORBIDDEN,
                ErrorCode.INTERNAL_ERROR
        };

        for (ErrorCode errorCode : errorCodes) {
            // 创建业务异常
            BusinessException businessException = new BusinessException(errorCode, "测试错误消息");

            // 创建测试请求
            MockServerHttpRequest request = MockServerHttpRequest
                    .get("/auth/test")
                    .build();

            MockServerWebExchange exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

            // 执行异常处理
            Mono<Void> result = exceptionHandler.handle(exchange, businessException);

            // 验证结果
            StepVerifier.create(result)
                    .verifyComplete();

            // 验证响应体内容
            StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
                    .expectNextMatches(dataBuffer -> {
                        String content = dataBuffer.toString(StandardCharsets.UTF_8);
                        return content.contains(String.valueOf(errorCode.getCode())) &&
                               content.contains("测试错误消息");
                    })
                    .verifyComplete();
        }
    }

    @Test
    @DisplayName("异常处理 - 响应头测试")
    void responseHeaderTest() {
        // 创建业务异常
        BusinessException businessException = new BusinessException(ErrorCode.PARAM_ERROR, "参数错误");

        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

        // 执行异常处理
        Mono<Void> result = exceptionHandler.handle(exchange, businessException);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证响应头
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
        assertEquals(StandardCharsets.UTF_8, exchange.getResponse().getHeaders().getContentType().getCharset());
    }

    @Test
    @DisplayName("异常处理 - 大数据量异常测试")
    void largeDataExceptionTest() {
        // 创建包含大量数据的异常消息
        StringBuilder largeMessage = new StringBuilder();
        largeMessage.append("这是一个很长的错误消息，用于测试大数据量异常处理。".repeat(1000));

        BusinessException businessException = new BusinessException(ErrorCode.PARAM_ERROR, largeMessage.toString());

        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

        // 执行异常处理
        Mono<Void> result = exceptionHandler.handle(exchange, businessException);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证响应状态
        assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        // 验证响应体内容
        StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
                .expectNextMatches(dataBuffer -> {
                    String content = dataBuffer.toString(StandardCharsets.UTF_8);
                    return content.contains(largeMessage.toString()) &&
                           content.contains(String.valueOf(ErrorCode.PARAM_ERROR.getCode()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("异常处理 - 特殊字符异常测试")
    void specialCharacterExceptionTest() {
        // 创建包含特殊字符的异常消息
        String specialMessage = "特殊字符测试：!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        BusinessException businessException = new BusinessException(ErrorCode.PARAM_ERROR, specialMessage);

        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

        // 执行异常处理
        Mono<Void> result = exceptionHandler.handle(exchange, businessException);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证响应体内容
        StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
                .expectNextMatches(dataBuffer -> {
                    String content = dataBuffer.toString(StandardCharsets.UTF_8);
                    return content.contains(specialMessage) &&
                           content.contains(String.valueOf(ErrorCode.PARAM_ERROR.getCode()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("异常处理 - 并发异常测试")
    void concurrentExceptionTest() {
        // 测试并发异常处理
        BusinessException businessException = new BusinessException(ErrorCode.PARAM_ERROR, "并发测试异常");

        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

        // 执行异常处理
        Mono<Void> result = exceptionHandler.handle(exchange, businessException);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证响应状态
        assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        // 验证响应体内容
        StepVerifier.create(DataBufferUtils.join(exchange.getResponse().getBody()))
                .expectNextMatches(dataBuffer -> {
                    String content = dataBuffer.toString(StandardCharsets.UTF_8);
                    return content.contains("并发测试异常") &&
                           content.contains(String.valueOf(ErrorCode.PARAM_ERROR.getCode()));
                })
                .verifyComplete();
    }
} 