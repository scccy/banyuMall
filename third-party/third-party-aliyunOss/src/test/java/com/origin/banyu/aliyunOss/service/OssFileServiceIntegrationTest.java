package com.origin.banyu.aliyunOss.service;

import com.origin.banyu.aliyunOss.entity.OssUploadLog;
import com.origin.banyu.aliyunOss.mapper.OssUploadLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
// 测试数据库，不需要回滚，保留真实数据
@DisplayName("OSS文件服务集成测试")
class OssFileServiceIntegrationTest {

    @Autowired
    private OssFileService ossFileService;

    @Autowired
    private OssUploadLogMapper ossUploadLogMapper;

    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() throws Exception {
        // 使用真实测试文件
        byte[] fileContent = Files.readAllBytes(Paths.get("/Volumes/project/test/oss_test.JPG"));
        testFile = new MockMultipartFile(
                "file",
                "service_test.jpg",
                "image/jpeg",
                fileContent
        );
    }

    @Test
    @DisplayName("测试文件上传Service逻辑 - 包含数据库记录")
    void testUploadFileServiceLogic() throws Exception {
        // 记录测试前的数据库记录数
        List<OssUploadLog> beforeLogs = ossUploadLogMapper.selectList(null);
        int beforeCount = beforeLogs.size();

        // 执行文件上传
        String accessUrl = ossFileService.uploadFile(testFile);

        // 验证返回结果
        assertNotNull(accessUrl, "上传后应该返回访问URL");
        assertTrue(accessUrl.startsWith("http"), "返回的URL应该是有效的HTTP地址");

        // 验证数据库记录
        List<OssUploadLog> afterLogs = ossUploadLogMapper.selectList(null);
        assertEquals(beforeCount + 1, afterLogs.size(), "数据库应该新增一条上传记录");

        // 验证新增记录的内容
        OssUploadLog newLog = afterLogs.stream()
                .filter(log -> "service_test.jpg".equals(log.getOriginalName()))
                .findFirst()
                .orElse(null);

        assertNotNull(newLog, "应该能找到新增的上传记录");
        assertEquals("service_test.jpg", newLog.getOriginalName(), "原始文件名应该正确");
        assertNotNull(newLog.getObjectPath(), "对象路径不应该为空");
        assertNotNull(newLog.getFileName(), "文件名不应该为空");
        assertNotNull(newLog.getObjectKey(), "对象键不应该为空");
        assertEquals(accessUrl, newLog.getAccessUrl(), "访问URL应该与返回值一致");
        assertNotNull(newLog.getLogId(), "日志ID不应该为空");

        // 验证字段格式
        assertTrue(newLog.getObjectPath().matches("uploads/\\d{4}/\\d{2}/\\d{2}"), "对象路径应该是日期格式");
        assertTrue(newLog.getFileName().matches("[a-f0-9-]+\\.jpg"), "文件名应该是UUID格式");
        assertTrue(newLog.getObjectKey().equals(newLog.getObjectPath() + "/" + newLog.getFileName()), 
                  "对象键应该是路径+文件名");

        System.out.println("=== 测试成功信息 ===");
        System.out.println("上传文件: " + newLog.getOriginalName());
        System.out.println("对象路径: " + newLog.getObjectPath());
        System.out.println("文件名: " + newLog.getFileName());
        System.out.println("对象键: " + newLog.getObjectKey());
        System.out.println("访问URL: " + newLog.getAccessUrl());
        System.out.println("记录ID: " + newLog.getLogId());
    }

    @Test
    @DisplayName("测试文件上传异常处理")
    void testUploadFileException() {
        // 测试空文件
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.txt", "text/plain", new byte[0]
        );

        // 验证异常处理
        assertThrows(Exception.class, () -> {
            ossFileService.uploadFile(emptyFile);
        }, "空文件应该抛出异常");
    }

    @Test
    @DisplayName("测试数据库字段完整性")
    void testDatabaseFieldCompleteness() throws Exception {
        // 执行上传
        String accessUrl = ossFileService.uploadFile(testFile);

        // 查询最新记录
        List<OssUploadLog> logs = ossUploadLogMapper.selectList(null);
        OssUploadLog latestLog = logs.stream()
                .max((a, b) -> a.getCreatedTime().compareTo(b.getCreatedTime()))
                .orElse(null);

        assertNotNull(latestLog, "应该有最新的上传记录");
        
        // 验证基础字段（继承自BaseEntity）
        assertNotNull(latestLog.getCreatedTime(), "创建时间不应该为空");
        assertNotNull(latestLog.getUpdatedTime(), "更新时间不应该为空");
        // 注意：created_by和updated_by可能为空，取决于业务逻辑
        assertNotNull(latestLog.getDeleted(), "删除标志不应该为空");
        assertEquals(0, latestLog.getDeleted().intValue(), "删除标志应该为0（未删除）");
    }
}
