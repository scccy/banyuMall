package com.origin.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultData测试类
 * 
 * @author origin
 * @since 2024-12-19
 */
@DisplayName("ResultData测试")
class ResultDataTest {

    @Test
    @DisplayName("测试成功响应（无数据）")
    void testOkWithoutData() {
        ResultData result = ResultData.ok();
        
        assertEquals(200, result.getCode());
        assertEquals("SUCCESS", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试成功响应（有数据）")
    void testOkWithData() {
        String testData = "test data";
        ResultData result = ResultData.ok("Success", testData);
        
        assertEquals(200, result.getCode());
        assertEquals("Success", result.getMsg());
        assertEquals(testData, result.getData());
    }
    


    @Test
    @DisplayName("测试失败响应（无数据）")
    void testFailWithoutData() {
        ResultData result = ResultData.fail();
        
        assertEquals(500, result.getCode());
        assertEquals("FAIL", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试失败响应（有数据）")
    void testFailWithData() {
        String errorData = "error details";
        ResultData result = ResultData.fail("400", "Bad Request", errorData);
        
        assertEquals(400, result.getCode());
        assertEquals("Bad Request", result.getMsg());
        assertEquals(errorData, result.getData());
    }

    @Test
    @DisplayName("测试链式调用")
    void testChainCall() {
        ResultData result = new ResultData()
            .setCode(200)
            .setMsg("Custom Message")
            .setData("Custom Data");
        
        assertEquals(200, result.getCode());
        assertEquals("Custom Message", result.getMsg());
        assertEquals("Custom Data", result.getData());
    }
} 