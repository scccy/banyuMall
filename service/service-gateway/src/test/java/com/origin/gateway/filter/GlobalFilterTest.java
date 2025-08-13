package com.origin.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * GlobalFilter 测试类
 * 
 * @author scccy
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("全局过滤器测试")
class GlobalFilterTest {

    @Autowired
    private GlobalFilter globalFilter;

    @MockBean
    private WebFilterChain filterChain;

    private ServerWebExchange exchange;
    private ServerHttpRequest request;

    @BeforeEach
    void setUp() {
        // 创建模拟请求
        request = MockServerHttpRequest
                .get("/auth/health")
                .header("User-Agent", "TestAgent/1.0")
                .header("X-Forwarded-For", "192.168.1.100")
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                .build();

        // 创建模拟交换对象
        exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

        // 设置过滤器链的模拟行为
        when(filterChain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("请求ID生成测试")
    void testRequestIdGeneration() {
        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证过滤器链被调用
        StepVerifier.create(result)
                .verifyComplete();

        // 验证请求头被添加
        verify(filterChain).filter(any(ServerWebExchange.class));

        // 获取修改后的请求
        ServerHttpRequest modifiedRequest = exchange.getRequest();
        HttpHeaders headers = modifiedRequest.getHeaders();

        // 验证请求ID被添加
        assertTrue(headers.containsKey("X-Request-ID"));
        String requestId = headers.getFirst("X-Request-ID");
        assertNotNull(requestId);
        assertTrue(requestId.startsWith("req-"));
        assertTrue(requestId.length() > 20);
    }

    @Test
    @DisplayName("客户端IP获取测试")
    void testClientIpExtraction() {
        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证过滤器链被调用
        StepVerifier.create(result)
                .verifyComplete();

        // 获取修改后的请求
        ServerHttpRequest modifiedRequest = exchange.getRequest();
        HttpHeaders headers = modifiedRequest.getHeaders();

        // 验证客户端IP被正确提取
        assertTrue(headers.containsKey("X-Client-IP"));
        String clientIp = headers.getFirst("X-Client-IP");
        assertEquals("192.168.1.100", clientIp);
    }

    @Test
    @DisplayName("用户代理获取测试")
    void testUserAgentExtraction() {
        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证过滤器链被调用
        StepVerifier.create(result)
                .verifyComplete();

        // 获取修改后的请求
        ServerHttpRequest modifiedRequest = exchange.getRequest();
        HttpHeaders headers = modifiedRequest.getHeaders();

        // 验证用户代理被正确提取
        assertTrue(headers.containsKey("X-User-Agent"));
        String userAgent = headers.getFirst("X-User-Agent");
        assertEquals("TestAgent/1.0", userAgent);
    }

    @Test
    @DisplayName("链路追踪头添加测试")
    void testTraceHeadersAddition() {
        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证过滤器链被调用
        StepVerifier.create(result)
                .verifyComplete();

        // 获取修改后的请求
        ServerHttpRequest modifiedRequest = exchange.getRequest();
        HttpHeaders headers = modifiedRequest.getHeaders();

        // 验证所有链路追踪头都被添加
        assertTrue(headers.containsKey("X-Request-ID"));
        assertTrue(headers.containsKey("X-Client-IP"));
        assertTrue(headers.containsKey("X-User-Agent"));
        assertTrue(headers.containsKey("X-Request-Time"));
        assertTrue(headers.containsKey("X-Service-Name"));
        assertTrue(headers.containsKey("X-Gateway-Time"));

        // 验证服务名称
        assertEquals("service-gateway", headers.getFirst("X-Service-Name"));
    }

    @Test
    @DisplayName("已存在请求ID测试")
    void testExistingRequestId() {
        // 创建带有已存在请求ID的请求
        MockServerHttpRequest requestWithId = MockServerHttpRequest
                .get("/auth/health")
                .header("X-Request-ID", "existing-req-id-123")
                .build();

        ServerWebExchange exchangeWithId = MockServerWebExchange.from(requestWithId);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchangeWithId, filterChain);

        // 验证过滤器链被调用
        StepVerifier.create(result)
                .verifyComplete();

        // 获取修改后的请求
        ServerHttpRequest modifiedRequest = exchangeWithId.getRequest();
        HttpHeaders headers = modifiedRequest.getHeaders();

        // 验证使用已存在的请求ID
        assertEquals("existing-req-id-123", headers.getFirst("X-Request-ID"));
    }

    @Test
    @DisplayName("无代理IP测试")
    void testNoProxyIp() {
        // 创建没有代理IP的请求
        MockServerHttpRequest requestNoProxy = MockServerHttpRequest
                .get("/auth/health")
                .remoteAddress(new InetSocketAddress("192.168.1.200", 8080))
                .build();

        ServerWebExchange exchangeNoProxy = MockServerWebExchange.from( requestNoProxy);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchangeNoProxy, filterChain);

        // 验证过滤器链被调用
        StepVerifier.create(result)
                .verifyComplete();

        // 获取修改后的请求
        ServerHttpRequest modifiedRequest = exchangeNoProxy.getRequest();
        HttpHeaders headers = modifiedRequest.getHeaders();

        // 验证使用远程地址作为客户端IP
        assertEquals("192.168.1.200", headers.getFirst("X-Client-IP"));
    }

    @Test
    @DisplayName("无用户代理测试")
    void testNoUserAgent() {
        // 创建没有用户代理的请求
        MockServerHttpRequest requestNoUserAgent = MockServerHttpRequest
                .get("/auth/health")
                .build();

        ServerWebExchange exchangeNoUserAgent = MockServerWebExchange.from(requestNoUserAgent);

        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchangeNoUserAgent, filterChain);

        // 验证过滤器链被调用
        StepVerifier.create(result)
                .verifyComplete();

        // 获取修改后的请求
        ServerHttpRequest modifiedRequest = exchangeNoUserAgent.getRequest();
        HttpHeaders headers = modifiedRequest.getHeaders();

        // 验证用户代理为unknown
        assertEquals("unknown", headers.getFirst("X-User-Agent"));
    }

    @Test
    @DisplayName("过滤器优先级测试")
    void testFilterOrder() {
        // 验证过滤器优先级
        assertEquals(Integer.MIN_VALUE, globalFilter.getOrder());
    }

    @Test
    @DisplayName("请求时间戳测试")
    void testRequestTimeHeader() {
        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证过滤器链被调用
        StepVerifier.create(result)
                .verifyComplete();

        // 获取修改后的请求
        ServerHttpRequest modifiedRequest = exchange.getRequest();
        HttpHeaders headers = modifiedRequest.getHeaders();

        // 验证请求时间戳被添加
        assertTrue(headers.containsKey("X-Request-Time"));
        String requestTime = headers.getFirst("X-Request-Time");
        assertNotNull(requestTime);
        
        // 验证时间戳是数字
        assertTrue(requestTime.matches("\\d+"));
        
        // 验证时间戳是当前时间附近
        long timestamp = Long.parseLong(requestTime);
        long currentTime = System.currentTimeMillis();
        assertTrue(Math.abs(timestamp - currentTime) < 1000); // 1秒内
    }

    @Test
    @DisplayName("网关时间戳测试")
    void testGatewayTimeHeader() {
        // 执行过滤器
        Mono<Void> result = globalFilter.filter(exchange, filterChain);

        // 验证过滤器链被调用
        StepVerifier.create(result)
                .verifyComplete();

        // 获取修改后的请求
        ServerHttpRequest modifiedRequest = exchange.getRequest();
        HttpHeaders headers = modifiedRequest.getHeaders();

        // 验证网关时间戳被添加
        assertTrue(headers.containsKey("X-Gateway-Time"));
        String gatewayTime = headers.getFirst("X-Gateway-Time");
        assertNotNull(gatewayTime);
        
        // 验证时间戳是数字
        assertTrue(gatewayTime.matches("\\d+"));
    }
} 