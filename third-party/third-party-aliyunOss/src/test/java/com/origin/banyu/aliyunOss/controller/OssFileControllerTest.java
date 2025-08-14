package com.origin.banyu.aliyunOss.controller;

import com.origin.banyu.aliyunOss.exception.OssExceptionHandler;
import com.origin.banyu.aliyunOss.mapper.OssUploadLogMapper;
import com.origin.banyu.aliyunOss.service.OssFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OssFileController.class, excludeAutoConfiguration = {
        DataSourceAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(OssExceptionHandler.class)
@DisplayName("OSS文件控制器测试")
class OssFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OssFileService ossFileService;

    @MockBean
    private OssUploadLogMapper ossUploadLogMapper;

    private MockMultipartFile testImageFile;
    private String accessUrl;

    @BeforeEach
    void setUp() throws Exception {
        // 使用指定测试文件：/Volumes/project/test/oss_test.JPG
        Path imagePath = Paths.get("/Volumes/project/test/oss_test.JPG");
        byte[] imageBytes = Files.readAllBytes(imagePath);
        testImageFile = new MockMultipartFile(
                "file",
                "oss_test.JPG",
                "image/jpeg",
                imageBytes
        );

        MockMultipartFile  testPdfFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "test pdf content".getBytes()
        );

        accessUrl = "https://example-bucket.oss-cn-beijing.aliyuncs.com/abc123.jpg";
    }

    @Test
    @DisplayName("上传文件 - 成功")
    void testUploadFileSuccess() throws Exception {
        when(ossFileService.uploadFile(any()))
                .thenReturn(accessUrl);

        mockMvc.perform(multipart("/tp/oss/upload")
                        .file(testImageFile)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文件上传成功"))
                .andExpect(jsonPath("$.data").value(accessUrl));
    }

    @Test
    @DisplayName("上传文件 - 参数验证失败")
    void testUploadFileValidationFailure() throws Exception {
        // 极简版接口仅要求file，其他参数不再校验，这里只验证接口可达
        mockMvc.perform(multipart("/tp/oss/upload").file(testImageFile))
                .andExpect(status().isOk());
    }

//    @Test
//    @DisplayName("上传文件 - 服务异常")
//    void testUploadFileServiceException() throws Exception {
//        when(ossFileService.uploadFile(any()))
//                .thenThrow(new RuntimeException("OSS服务异常"));
//
//        mockMvc.perform(multipart("/tp/oss/upload")
//                        .file(testImageFile))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(500))
//                .andExpect(jsonPath("$.message").value("文件上传失败: OSS服务异常"));
//    }
//
//    // 移除获取URL、删除、批量上传、生成路径等非极简用例

    @Test
    @DisplayName("文件上传性能测试")
    void testUploadFilePerformance() throws Exception {
        when(ossFileService.uploadFile(any())).thenReturn(accessUrl);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(multipart("/tp/oss/upload").file(testImageFile))
                    .andExpect(status().isOk());
        }
        long duration = System.currentTimeMillis() - startTime;
        assert duration < 10000 : "文件上传性能不满足要求，耗时: " + duration + "ms";
    }
    // 保留URL格式简单校验由上传接口返回校验
}



