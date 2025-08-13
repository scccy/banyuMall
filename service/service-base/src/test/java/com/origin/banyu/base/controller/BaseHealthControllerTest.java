package com.origin.banyu.base.controller;

import com.origin.banyu.common.dto.HealthResponse;
import com.origin.banyu.common.dto.ResultData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BaseHealthController测试类
 * 
 * @author origin
 * @since 2025-08-07
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("基础健康检查控制器测试")
class BaseHealthControllerTest {

    private TestHealthController testController;

    @BeforeEach
    void setUp() {
        testController = new TestHealthController();
    }

    @Test
    @DisplayName("健康检查 - 正常响应测试")
    void testHealth() {
        // 执行健康检查
        ResultData<HealthResponse> result = testController.health();

        // 验证响应
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("测试服务运行正常", result.getMessage());

        // 验证数据
        HealthResponse healthResponse = result.getData();
        assertNotNull(healthResponse);
        assertEquals("UP", healthResponse.getStatus());
        assertEquals("test-service", healthResponse.getService());
        assertEquals("0.0.1-SNAPSHOT", healthResponse.getVersion());
        assertEquals("测试服务运行正常", healthResponse.getDescription());
        assertNotNull(healthResponse.getTimestamp());
        assertTrue(healthResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("健康检查 - 路径获取测试")
    void testGetHealthPath() {
        String healthPath = testController.getHealthPath();
        assertEquals("/test/health", healthPath);
    }

    /**
     * 测试用的健康检查控制器实现
     */
    private static class TestHealthController extends BaseHealthController {
        @Override
        protected String getHealthPath() {
            return "/test/health";
        }

        @Override
        protected String getServiceDescription() {
            return "测试服务运行正常";
        }
    }
} 