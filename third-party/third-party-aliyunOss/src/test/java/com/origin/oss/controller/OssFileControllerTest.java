package com.origin.aliyunOss.controller;

import com.origin.common.dto.FileUploadRequest;
import com.origin.common.dto.FileUploadResponse;
import com.origin.aliyunOss.service.OssFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OssFileController 测试类
 * 
 * @author scccy
 */
@WebMvcTest(OssFileController.class)
@ActiveProfiles("test")
@DisplayName("OSS文件控制器测试")
class OssFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OssFileService ossFileService;

    private MockMultipartFile testImageFile;
    private MockMultipartFile testPdfFile;
    private FileUploadResponse uploadResponse;

    @BeforeEach
    void setUp() {
        // 初始化测试文件
        testImageFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        testPdfFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "test pdf content".getBytes()
        );

        // 初始化上传响应
        uploadResponse = new FileUploadResponse();
        uploadResponse.setFileId(1L);
        uploadResponse.setOriginalName("test-image.jpg");
        uploadResponse.setFileSize(102400L);
        uploadResponse.setMimeType("image/jpeg");
        uploadResponse.setObjectKey("core-publisher/task-image/2025/01/27/test-image-20250127-143000.jpg");
        uploadResponse.setAccessUrl("https://example-bucket.oss-cn-beijing.aliyuncs.com/core-publisher/task-image/2025/01/27/test-image-20250127-143000.jpg");
        uploadResponse.setBucketName("example-bucket");
        uploadResponse.setFilePath("core-publisher/task-image/2025/01/27/");
        uploadResponse.setSourceService("core-publisher");
        uploadResponse.setBusinessType("task-image");
        uploadResponse.setUploadUserId(1L);
        uploadResponse.setUploadUserName("testuser");
        uploadResponse.setUploadTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("上传文件 - 成功")
    void testUploadFileSuccess() throws Exception {
        // 模拟服务层返回
        when(ossFileService.uploadFile(any(FileUploadRequest.class)))
                .thenReturn(uploadResponse);

        // 执行测试
        mockMvc.perform(multipart("/tp/oss/upload")
                        .file(testImageFile)
                        .param("sourceService", "core-publisher")
                        .param("businessType", "task-image")
                        .param("filePath", "core-publisher/task-image/2025/01/27/")
                        .param("uploadUserId", "1")
                        .param("uploadUserName", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文件上传成功"))
                .andExpect(jsonPath("$.data.fileId").value(1))
                .andExpect(jsonPath("$.data.originalName").value("test-image.jpg"))
                .andExpect(jsonPath("$.data.accessUrl").exists());
    }

    @Test
    @DisplayName("上传文件 - 参数验证失败")
    void testUploadFileValidationFailure() throws Exception {
        // 执行测试 - 缺少必填参数
        mockMvc.perform(multipart("/tp/oss/upload")
                        .file(testImageFile)
                        .param("sourceService", "") // 空参数
                        .param("businessType", "") // 空参数
                        .param("filePath", "")) // 空参数
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("上传文件 - 服务异常")
    void testUploadFileServiceException() throws Exception {
        // 模拟服务层抛出异常
        when(ossFileService.uploadFile(any(FileUploadRequest.class)))
                .thenThrow(new RuntimeException("OSS服务异常"));

        // 执行测试
        mockMvc.perform(multipart("/tp/oss/upload")
                        .file(testImageFile)
                        .param("sourceService", "core-publisher")
                        .param("businessType", "task-image")
                        .param("filePath", "core-publisher/task-image/2025/01/27/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("文件上传失败: OSS服务异常"));
    }

    @Test
    @DisplayName("获取文件访问URL - 成功")
    void testGetFileAccessUrlSuccess() throws Exception {
        String expectedUrl = "https://example-bucket.oss-cn-beijing.aliyuncs.com/test-file.jpg";
        
        // 模拟服务层返回
        when(ossFileService.getFileAccessUrl(1L))
                .thenReturn(expectedUrl);

        // 执行测试
        mockMvc.perform(get("/tp/oss/url/{fileId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取文件访问URL成功"))
                .andExpect(jsonPath("$.data").value(expectedUrl));
    }

    @Test
    @DisplayName("获取文件访问URL - 文件不存在")
    void testGetFileAccessUrlNotFound() throws Exception {
        // 模拟服务层抛出异常
        when(ossFileService.getFileAccessUrl(999L))
                .thenThrow(new RuntimeException("文件不存在"));

        // 执行测试
        mockMvc.perform(get("/tp/oss/url/{fileId}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取文件访问URL失败: 文件不存在"));
    }

    @Test
    @DisplayName("删除文件 - 成功")
    void testDeleteFileSuccess() throws Exception {
        // 模拟服务层返回
        when(ossFileService.deleteFile(1L, 1L))
                .thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/tp/oss/file/{fileId}", 1)
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文件删除成功"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("删除文件 - 文件不存在")
    void testDeleteFileNotFound() throws Exception {
        // 模拟服务层返回
        when(ossFileService.deleteFile(999L, 1L))
                .thenReturn(false);

        // 执行测试
        mockMvc.perform(delete("/tp/oss/file/{fileId}", 999)
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文件删除成功"))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("删除文件 - 参数验证失败")
    void testDeleteFileValidationFailure() throws Exception {
        // 执行测试 - 缺少userId参数
        mockMvc.perform(delete("/tp/oss/file/{fileId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("批量上传文件 - 成功")
    void testBatchUploadFilesSuccess() throws Exception {
        // 创建多个测试文件
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "image1.jpg",
                "image/jpeg",
                "image1 content".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "image2.jpg",
                "image/jpeg",
                "image2 content".getBytes()
        );

        // 创建批量上传响应
        FileUploadResponse response1 = new FileUploadResponse();
        response1.setFileId(1L);
        response1.setOriginalName("image1.jpg");
        response1.setAccessUrl("https://example.com/image1.jpg");

        FileUploadResponse response2 = new FileUploadResponse();
        response2.setFileId(2L);
        response2.setOriginalName("image2.jpg");
        response2.setAccessUrl("https://example.com/image2.jpg");

        List<FileUploadResponse> responses = Arrays.asList(response1, response2);

        // 模拟服务层返回
        when(ossFileService.batchUploadFiles(anyList(), eq("core-publisher"), eq("task-images"), eq(1L)))
                .thenReturn(responses);

        // 执行测试
        mockMvc.perform(multipart("/tp/oss/batch-upload")
                        .file(file1)
                        .file(file2)
                        .param("sourceService", "core-publisher")
                        .param("businessType", "task-images")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量文件上传成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("批量上传文件 - 参数验证失败")
    void testBatchUploadFilesValidationFailure() throws Exception {
        // 执行测试 - 缺少必填参数
        mockMvc.perform(multipart("/tp/oss/batch-upload")
                        .file(testImageFile)
                        .param("sourceService", "") // 空参数
                        .param("businessType", "") // 空参数
                        .param("userId", "")) // 空参数
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("生成文件路径 - 成功")
    void testGenerateFilePathSuccess() throws Exception {
        String expectedPath = "core-publisher/task-image/2025/01/27/";
        
        // 模拟服务层返回
        when(ossFileService.generateFilePath("core-publisher", "task-image"))
                .thenReturn(expectedPath);

        // 执行测试
        mockMvc.perform(get("/tp/oss/generate-path")
                        .param("sourceService", "core-publisher")
                        .param("businessType", "task-image")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("生成文件路径成功"))
                .andExpect(jsonPath("$.data").value(expectedPath));
    }

    @Test
    @DisplayName("生成文件路径 - 参数验证失败")
    void testGenerateFilePathValidationFailure() throws Exception {
        // 执行测试 - 缺少必填参数
        mockMvc.perform(get("/tp/oss/generate-path")
                        .param("sourceService", "") // 空参数
                        .param("businessType", "") // 空参数
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("上传文档文件 - 成功")
    void testUploadDocumentFileSuccess() throws Exception {
        // 创建文档文件响应
        FileUploadResponse docResponse = new FileUploadResponse();
        docResponse.setFileId(2L);
        docResponse.setOriginalName("test-document.pdf");
        docResponse.setFileSize(2048000L);
        docResponse.setMimeType("application/pdf");
        docResponse.setObjectKey("service-user/user-document/2025/01/27/test-document-20250127-143000.pdf");
        docResponse.setAccessUrl("https://example-bucket.oss-cn-beijing.aliyuncs.com/service-user/user-document/2025/01/27/test-document-20250127-143000.pdf");
        docResponse.setBucketName("example-bucket");
        docResponse.setFilePath("service-user/user-document/2025/01/27/");
        docResponse.setSourceService("service-user");
        docResponse.setBusinessType("user-document");
        docResponse.setUploadUserId(2L);
        docResponse.setUploadUserName("admin");
        docResponse.setUploadTime(LocalDateTime.now());

        // 模拟服务层返回
        when(ossFileService.uploadFile(any(FileUploadRequest.class)))
                .thenReturn(docResponse);

        // 执行测试
        mockMvc.perform(multipart("/tp/oss/upload")
                        .file(testPdfFile)
                        .param("sourceService", "service-user")
                        .param("businessType", "user-document")
                        .param("filePath", "service-user/user-document/2025/01/27/")
                        .param("uploadUserId", "2")
                        .param("uploadUserName", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文件上传成功"))
                .andExpect(jsonPath("$.data.fileId").value(2))
                .andExpect(jsonPath("$.data.originalName").value("test-document.pdf"))
                .andExpect(jsonPath("$.data.mimeType").value("application/pdf"));
    }

    @Test
    @DisplayName("文件上传性能测试")
    void testUploadFilePerformance() throws Exception {
        // 模拟服务层返回
        when(ossFileService.uploadFile(any(FileUploadRequest.class)))
                .thenReturn(uploadResponse);

        // 执行多次上传测试
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(multipart("/tp/oss/upload")
                            .file(testImageFile)
                            .param("sourceService", "core-publisher")
                            .param("businessType", "task-image")
                            .param("filePath", "core-publisher/task-image/2025/01/27/"))
                    .andExpect(status().isOk());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证10次上传在合理时间内完成（小于10秒）
        assert duration < 10000 : "文件上传性能不满足要求，耗时: " + duration + "ms";
    }

    @Test
    @DisplayName("文件访问URL格式验证")
    void testFileAccessUrlFormat() throws Exception {
        String expectedUrl = "https://example-bucket.oss-cn-beijing.aliyuncs.com/test-file.jpg";
        
        // 模拟服务层返回
        when(ossFileService.getFileAccessUrl(1L))
                .thenReturn(expectedUrl);

        // 执行测试
        mockMvc.perform(get("/tp/oss/url/{fileId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(expectedUrl))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.startsWith("https://")));
    }
} 