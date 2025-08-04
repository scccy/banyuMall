package com.origin.user.feign;

import com.origin.common.dto.ResultData;
import com.origin.common.dto.FileUploadRequest;
import com.origin.common.dto.FileUploadResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 文件服务Feign客户端测试
 * 
 * @author scccy
 * @since 2025-08-04
 */
@SpringBootTest
@ActiveProfiles("test")
class OssFileFeignClientTest {

    @MockBean
    private OssFileFeignClient ossFileFeignClient;

    @Test
    @DisplayName("文件服务调用 - 正常调用测试")
    void ossFeignTest() {
        // 模拟文件上传响应
        FileUploadResponse mockResponse = new FileUploadResponse();
        mockResponse.setFileId(123L);
        mockResponse.setAccessUrl("https://oss.example.com/files/test-file-id-123.jpg");
        mockResponse.setOriginalName("test-avatar.jpg");
        mockResponse.setFileSize(1024L);
        
        when(ossFileFeignClient.uploadFile(any(FileUploadRequest.class)))
                .thenReturn(ResultData.ok("文件上传成功", mockResponse));
        
        // 测试文件上传功能
        FileUploadRequest request = new FileUploadRequest();
        request.setSourceService("service-user");
        request.setBusinessType("user-avatar");
        request.setFilePath("service-user/user-avatar/2025/08/04/");
        request.setUploadUserId(1L);
        request.setUploadUserName("testuser");
        
        ResultData<FileUploadResponse> result = ossFileFeignClient.uploadFile(request);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        FileUploadResponse response = result.getData();
        assertNotNull(response);
        assertNotNull(response.getFileId());
        assertNotNull(response.getAccessUrl());
        assertTrue(response.getAccessUrl().contains("oss.example.com"));
    }

    @Test
    @DisplayName("文件服务调用 - 获取文件URL测试")
    void getFileUrlTest() {
        // 模拟获取文件URL响应
        String mockFileUrl = "https://oss.example.com/files/test-file-id.jpg";
        when(ossFileFeignClient.getFileUrl("test-file-id"))
                .thenReturn(ResultData.ok("获取文件URL成功", mockFileUrl));
        
        // 测试获取文件URL功能
        String fileId = "test-file-id";
        ResultData<String> result = ossFileFeignClient.getFileUrl(fileId);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        String fileUrl = result.getData();
        assertNotNull(fileUrl);
        assertTrue(fileUrl.contains("oss.example.com"));
        assertTrue(fileUrl.contains(fileId));
    }

    @Test
    @DisplayName("文件服务调用 - 服务不可用降级测试")
    void fallbackTest() {
        // 模拟服务不可用时的降级响应
        when(ossFileFeignClient.uploadFile(any(FileUploadRequest.class)))
                .thenReturn(ResultData.fail("服务暂时不可用"));
        
        // 测试服务不可用时的降级处理
        FileUploadRequest request = new FileUploadRequest();
        request.setSourceService("service-user");
        request.setBusinessType("user-avatar");
        request.setFilePath("service-user/user-avatar/2025/08/04/");
        
        ResultData<FileUploadResponse> result = ossFileFeignClient.uploadFile(request);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("服务暂时不可用"));
    }
} 