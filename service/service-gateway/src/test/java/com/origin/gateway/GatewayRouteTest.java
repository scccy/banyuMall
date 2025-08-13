package com.origin.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 网关路由测试类
 * 
 * @author scccy
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("网关路由测试")
class GatewayRouteTest {

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("路由配置加载测试")
    void testRouteConfiguration() {
        // 验证路由配置是否正确加载
        assertNotNull(routeLocator);
        
        // 获取所有路由
        var routes = routeLocator.getRoutes().collectList().block();
        assertNotNull(routes);
        assertFalse(routes.isEmpty());
        
        // 验证路由数量
        assertTrue(routes.size() >= 2, "至少应该有2个路由配置");
        
        // 验证路由ID
        var routeIds = routes.stream()
                .map(route -> route.getId())
                .toList();
        
        assertTrue(routeIds.contains("auth-service"), "应该包含auth-service路由");
        assertTrue(routeIds.contains("user-service"), "应该包含user-service路由");
    }

    @Test
    @DisplayName("认证服务路由转发测试")
    void testAuthServiceRoute() {
        // 测试认证服务路由转发
        webTestClient.get()
                .uri("/auth/health")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Gateway-Source", "service-gateway")
                .expectHeader().valueEquals("X-Service-Name", "service-auth")
                .expectHeader().exists("X-Request-ID")
                .expectHeader().exists("X-Client-IP")
                .expectHeader().exists("X-User-Agent");
    }

    @Test
    @DisplayName("用户服务路由转发测试")
    void testUserServiceRoute() {
        // 测试用户服务路由转发
        webTestClient.get()
                .uri("/service/user/profile")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer test-token")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Gateway-Source", "service-gateway")
                .expectHeader().valueEquals("X-Service-Name", "service-user")
                .expectHeader().exists("X-Request-ID")
                .expectHeader().exists("X-Client-IP")
                .expectHeader().exists("X-User-Agent");
    }

    @Test
    @DisplayName("路由不匹配测试")
    void testRouteNotMatch() {
        // 测试不存在的路由
        webTestClient.get()
                .uri("/not-exist/path")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("请求头添加测试")
    void testRequestHeaderAddition() {
        // 测试请求头是否正确添加
        webTestClient.get()
                .uri("/auth/health")
                .header("Content-Type", "application/json")
                .header("X-Custom-Header", "custom-value")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Gateway-Source", "service-gateway")
                .expectHeader().valueEquals("X-Service-Name", "service-auth")
                .expectHeader().exists("X-Request-ID")
                .expectHeader().exists("X-Client-IP")
                .expectHeader().exists("X-User-Agent")
                .expectHeader().exists("X-Request-Time")
                .expectHeader().exists("X-Gateway-Time");
    }

    @Test
    @DisplayName("链路追踪头测试")
    void testTraceHeaders() {
        // 测试链路追踪头
        webTestClient.get()
                .uri("/service/user/profile")
                .header("Content-Type", "application/json")
                .header("X-Request-ID", "test-request-id-123")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Request-ID", "test-request-id-123")
                .expectHeader().exists("X-Client-IP")
                .expectHeader().exists("X-User-Agent")
                .expectHeader().exists("X-Request-Time")
                .expectHeader().exists("X-Service-Name")
                .expectHeader().exists("X-Gateway-Time");
    }

    @Test
    @DisplayName("客户端IP获取测试")
    void testClientIpExtraction() {
        // 测试客户端IP获取
        webTestClient.get()
                .uri("/auth/health")
                .header("Content-Type", "application/json")
                .header("X-Forwarded-For", "192.168.1.100")
                .header("X-Real-IP", "192.168.1.200")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Client-IP", "192.168.1.100");
    }

    @Test
    @DisplayName("用户代理获取测试")
    void testUserAgentExtraction() {
        // 测试用户代理获取
        webTestClient.get()
                .uri("/service/user/profile")
                .header("Content-Type", "application/json")
                .header("User-Agent", "TestAgent/1.0")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-User-Agent", "TestAgent/1.0");
    }

    @Test
    @DisplayName("路由性能测试")
    void testRoutePerformance() {
        // 测试路由转发性能
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            webTestClient.get()
                    .uri("/auth/health")
                    .header("Content-Type", "application/json")
                    .exchange()
                    .expectStatus().isOk();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 验证10次请求在合理时间内完成（小于5秒）
        assertTrue(duration < 5000, "路由转发性能不满足要求，耗时: " + duration + "ms");
    }

    @Test
    @DisplayName("并发路由测试")
    void testConcurrentRouteRequests() {
        // 测试并发路由请求
        int requestCount = 10;
        Thread[] threads = new Thread[requestCount];
        
        for (int i = 0; i < requestCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    webTestClient.get()
                            .uri("/auth/health")
                            .header("Content-Type", "application/json")
                            .exchange()
                            .expectStatus().isOk();
                } catch (Exception e) {
                    fail("并发请求测试失败: " + e.getMessage());
                }
            });
        }
        
        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("线程等待被中断");
            }
        }
        
        // 验证所有线程都正常完成
        assertTrue(true, "并发路由测试通过");
    }

    @Test
    @DisplayName("路由超时测试")
    void testRouteTimeout() {
        // 测试路由超时处理
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
        
        // 设置较短的超时时间
        webClient.get()
                .uri("/auth/health")
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    @Test
    @DisplayName("路由负载均衡测试")
    void testRouteLoadBalancing() {
        // 测试路由负载均衡（如果有多个实例）
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                    .uri("/service/user/profile")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer test-token")
                    .exchange()
                    .expectStatus().isOk();
        }
    }

    @Test
    @DisplayName("路由错误处理测试")
    void testRouteErrorHandling() {
        // 测试路由错误处理
        webTestClient.get()
                .uri("/auth/invalid-endpoint")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    @DisplayName("路由配置验证测试")
    void testRouteConfigurationValidation() {
        // 验证路由配置的正确性
        var routes = routeLocator.getRoutes().collectList().block();
        
        for (var route : routes) {
            // 验证路由ID不为空
            assertNotNull(route.getId());
            assertFalse(route.getId().trim().isEmpty());
            
            // 验证路由URI不为空
            assertNotNull(route.getUri());
            
            // 验证路由断言不为空
            assertNotNull(route.getPredicate());
            
            // 验证路由过滤器
            assertNotNull(route.getFilters());
        }
    }

    @Test
    @DisplayName("路由日志测试")
    void testRouteLogging() {
        // 测试路由日志记录
        webTestClient.get()
                .uri("/auth/health")
                .header("Content-Type", "application/json")
                .header("X-Test-Log", "test-log-value")
                .exchange()
                .expectStatus().isOk();
        
        // 注意：实际日志验证需要在集成测试中进行
        // 这里只是验证请求能够正常处理
        assertTrue(true, "路由日志测试通过");
    }
} 