package com.origin.aliyunOss.feign;

import com.origin.common.dto.ResultData;
import com.origin.common.dto.FileUploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OSS文件服务Feign客户端接口
 * 
 * @author scccy
 * @since 2025-07-31
 */
@FeignClient(name = "third-party-aliyunOss", path = "/tp/oss", fallback = OssFileFeignClientFallback.class)
public interface OssFileFeignClient {

    /**
     * 上传文件到OSS
     * 
     * @param file 上传的文件
     * @param sourceService 来源服务
     * @param businessType 业务类型
     * @param filePath 文件路径
     * @param uploadUserId 上传用户ID
     * @param uploadUserName 上传用户名
     * @return 文件上传响应
     */
    @PostMapping("/upload")
    ResultData<FileUploadResponse> uploadFile(@RequestPart("file") MultipartFile file,
                                            @RequestParam("sourceService") String sourceService,
                                            @RequestParam("businessType") String businessType,
                                            @RequestParam("filePath") String filePath,
                                            @RequestParam(value = "uploadUserId", required = false) Long uploadUserId,
                                            @RequestParam(value = "uploadUserName", required = false) String uploadUserName);

    /**
     * 获取文件访问URL
     * 
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    @GetMapping("/url/{fileId}")
    ResultData<String> getFileAccessUrl(@PathVariable("fileId") Long fileId);

    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/file/{fileId}")
    ResultData<Boolean> deleteFile(@PathVariable("fileId") Long fileId,
                                 @RequestParam("userId") Long userId);

    /**
     * 批量上传文件
     * 
     * @param files 文件列表
     * @param sourceService 来源服务
     * @param businessType 业务类型
     * @param userId 用户ID
     * @return 批量上传结果
     */
    @PostMapping("/batch-upload")
    ResultData<List<FileUploadResponse>> batchUploadFiles(@RequestPart("files") List<MultipartFile> files,
                                                        @RequestParam("sourceService") String sourceService,
                                                        @RequestParam("businessType") String businessType,
                                                        @RequestParam("userId") Long userId);

    /**
     * 生成文件路径
     * 
     * @param sourceService 来源服务
     * @param businessType 业务类型
     * @return 文件路径
     */
    @GetMapping("/generate-path")
    ResultData<String> generateFilePath(@RequestParam("sourceService") String sourceService,
                                      @RequestParam("businessType") String businessType);
} 