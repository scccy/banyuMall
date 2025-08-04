package com.origin.gateway;

import com.origin.gateway.filter.GlobalFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Gateway全局过滤器测试类
 */
@SpringBootTest
@ActiveProfiles("test")
class GlobalFilterTest {

    @Autowired
    private GlobalFilter globalFilter;

    private WebFilterChain filterChain;

    @BeforeEach
    void setUp() {
        filterChain = mock(WebFilterChain.class);
        when(filterChain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("全局过滤器 - 请求ID生成测试")
    void requestIdGenerationTest() {
        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .header("X-Request-ID", "existing-request-id")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证请求ID
        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-ID");
        assert requestId != null;
        assert requestId.equals("existing-request-id");
    }

    @Test
    @DisplayName("全局过滤器 - 新请求ID生成测试")
    void newRequestIdGenerationTest() {
        // 创建没有请求ID的测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证新生成的请求ID
        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-ID");
        assert requestId != null;
        assert requestId.startsWith("req-");
    }

    @Test
    @DisplayName("全局过滤器 - 客户端信息提取测试")
    void clientInfoExtractionTest() {
        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .header("X-Forwarded-For", "192.168.1.100")
                .header("User-Agent", "Test User Agent")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证客户端IP
        String clientIp = exchange.getRequest().getHeaders().getFirst("X-Client-IP");
        assert clientIp != null;
        assert clientIp.equals("192.168.1.100");

        // 验证用户代理
        String userAgent = exchange.getRequest().getHeaders().getFirst("X-User-Agent");
        assert userAgent != null;
        assert userAgent.equals("Test User Agent");
    }

    @Test
    @DisplayName("全局过滤器 - 链路追踪头添加测试")
    void traceHeaderAdditionTest() {
        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .header("X-Request-ID", "test-request-id")
                .header("X-Forwarded-For", "192.168.1.100")
                .header("User-Agent", "Test User Agent")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证链路追踪头
        HttpHeaders headers = exchange.getRequest().getHeaders();
        assert headers.getFirst("X-Request-ID") != null;
        assert headers.getFirst("X-Client-IP") != null;
        assert headers.getFirst("X-User-Agent") != null;
        assert headers.getFirst("X-Request-Time") != null;
        assert headers.getFirst("X-Service-Name") != null;
        assert headers.getFirst("X-Gateway-Time") != null;
    }

    @Test
    @DisplayName("全局过滤器 - 日志记录测试")
    void logRecordTest() {
        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .header("X-Request-ID", "test-request-id")
                .header("X-Forwarded-For", "192.168.1.100")
                .header("User-Agent", "Test User Agent")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from( request);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证请求和响应日志记录
        // 注意：实际日志记录测试需要配置日志捕获器
    }

    @Test
    @DisplayName("全局过滤器 - 性能监控测试")
    void performanceMonitoringTest() {
        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .header("X-Request-ID", "test-request-id")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证性能监控头
        String requestTime = exchange.getRequest().getHeaders().getFirst("X-Request-Time");
        String gatewayTime = exchange.getRequest().getHeaders().getFirst("X-Gateway-Time");
        
        assert requestTime != null;
        assert gatewayTime != null;
        
        // 验证时间戳格式
        assert requestTime.matches("\\d+");
        assert gatewayTime.matches("\\d+");
    }

    @Test
    @DisplayName("全局过滤器 - 空请求头测试")
    void emptyHeaderTest() {
        // 创建没有特殊请求头的测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证默认值处理
        String clientIp = exchange.getRequest().getHeaders().getFirst("X-Client-IP");
        String userAgent = exchange.getRequest().getHeaders().getFirst("X-User-Agent");
        
        assert clientIp != null;
        assert userAgent != null;
    }

    @Test
    @DisplayName("全局过滤器 - 多种IP头测试")
    void multipleIpHeaderTest() {
        // 测试多种IP头的优先级
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .header("X-Forwarded-For", "192.168.1.100")
                .header("X-Real-IP", "192.168.1.200")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证结果
        StepVerifier.create(result)
                .verifyComplete();

        // 验证IP提取优先级（X-Forwarded-For优先）
        String clientIp = exchange.getRequest().getHeaders().getFirst("X-Client-IP");
        assert clientIp != null;
        assert clientIp.equals("192.168.1.100");
    }

    @Test
    @DisplayName("全局过滤器 - 过滤器链异常测试")
    void filterChainExceptionTest() {
        // 创建测试请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/test")
                .header("X-Request-ID", "test-request-id")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // 模拟过滤器链异常
        when(filterChain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.error(new RuntimeException("Filter chain error")));

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证异常处理
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("全局过滤器 - 不同HTTP方法测试")
    void differentHttpMethodTest() {
        // 测试不同的HTTP方法
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};
        
        for (String method : methods) {
            MockServerHttpRequest request = MockServerHttpRequest
                    .method(org.springframework.http.HttpMethod.valueOf(method), "/auth/test")
                    .header("X-Request-ID", "test-request-id")
                    .build();

            ServerWebExchange exchange = MockServerWebExchange.from(request);

            // 执行过滤器
            Mono<Void> result = globalFilter.filter(exchange, filterChain);

            // 验证结果
            StepVerifier.create(result)
                    .verifyComplete();

            // 验证请求头添加
            assert exchange.getRequest().getHeaders().getFirst("X-Request-ID") != null;
        }
    }

    @Test
    @DisplayName("全局过滤器 - 过滤器顺序测试")
    void filterOrderTest() {
        // 验证过滤器顺序
        int order = globalFilter.getOrder();
        assert order == org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
    }
} 