package com.origin.common;

import com.origin.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultData测试类
 *
 * @author origin
 * @since 2025-01-27
 */
public class ResultDataTest {

    @Test
    public void testOk() {
        ResultData result = ResultData.ok();
        assertEquals(200, result.getCode());
        assertEquals("SUCCESS", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    public void testOkWithMessage() {
        String message = "操作成功";
        ResultData result = ResultData.ok(message);
        assertEquals(200, result.getCode());
        assertEquals(message, result.getMsg());
        assertNull(result.getData());
    }

    @Test
    public void testOkWithMessageAndData() {
        String message = "查询成功";
        String data = "test data";
        ResultData<String> result = ResultData.ok(message, data);
        assertEquals(200, result.getCode());
        assertEquals(message, result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    public void testFail() {
        ResultData result = ResultData.fail();
        assertEquals(500, result.getCode());
        assertEquals("FAIL", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    public void testFailWithMessage() {
        String message = "操作失败";
        ResultData result = ResultData.fail(message);
        assertEquals(500, result.getCode());
        assertEquals(message, result.getMsg());
        assertNull(result.getData());
    }

    @Test
    public void testFailWithCodeAndMessage() {
        Integer code = 400;
        String message = "参数错误";
        ResultData result = ResultData.fail(code, message);
        assertEquals(code, result.getCode());
        assertEquals(message, result.getMsg());
        assertNull(result.getData());
    }

    @Test
    public void testFailWithErrorCode() {
        ResultData result = ResultData.fail(ErrorCode.PARAM_ERROR);
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), result.getCode());
        assertEquals(ErrorCode.PARAM_ERROR.getMessage(), result.getMsg());
        assertNull(result.getData());
    }

    @Test
    public void testFailWithErrorCodeAndMessage() {
        String customMessage = "自定义错误消息";
        ResultData result = ResultData.fail(ErrorCode.PARAM_ERROR, customMessage);
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), result.getCode());
        assertEquals(customMessage, result.getMsg());
        assertNull(result.getData());
    }

    @Test
    public void testFailWithErrorCodeAndMessageAndData() {
        String customMessage = "自定义错误消息";
        String data = "error data";
        ResultData<String> result = ResultData.fail(ErrorCode.PARAM_ERROR, customMessage, data);
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), result.getCode());
        assertEquals(customMessage, result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    public void testChainCall() {
        ResultData<String> result = new ResultData<String>()
                .setCode(200)
                .setMsg("链式调用")
                .setData("test");
        
        assertEquals(200, result.getCode());
        assertEquals("链式调用", result.getMsg());
        assertEquals("test", result.getData());
    }

    @Test
    public void testIsSuccess() {
        ResultData<String> successResult = ResultData.ok("成功", "data");
        assertTrue(successResult.isSuccess());
        
        ResultData<String> failResult = ResultData.fail("失败");
        assertFalse(failResult.isSuccess());
    }

    @Test
    public void testIsFail() {
        ResultData<String> successResult = ResultData.ok("成功", "data");
        assertFalse(successResult.isFail());
        
        ResultData<String> failResult = ResultData.fail("失败");
        assertTrue(failResult.isFail());
    }

    @Test
    public void testGetDataWithType() {
        String testData = "test data";
        ResultData<String> result = ResultData.ok("成功", testData);
        
        String data = result.getData(String.class);
        assertEquals(testData, data);
        
        // 测试类型不匹配的情况
        Integer intData = result.getData(Integer.class);
        assertNull(intData);
    }

    @Test
    public void testGetMessageOrDefault() {
        ResultData<String> result = new ResultData<>();
        result.setCode(200);
        result.setMsg("自定义消息");
        
        assertEquals("自定义消息", result.getMessageOrDefault("默认消息"));
        
        result.setMsg(null);
        assertEquals("默认消息", result.getMessageOrDefault("默认消息"));
        
        result.setMsg("");
        assertEquals("默认消息", result.getMessageOrDefault("默认消息"));
    }

    @Test
    public void testGetCodeOrDefault() {
        ResultData<String> result = new ResultData<>();
        result.setCode(200);
        
        assertEquals(200, result.getCodeOrDefault(500));
        
        result.setCode(null);
        assertEquals(500, result.getCodeOrDefault(500));
    }
} 