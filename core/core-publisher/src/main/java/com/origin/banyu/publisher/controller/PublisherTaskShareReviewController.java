package com.origin.banyu.publisher.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.publisher.dto.export.ShareReviewExportDTO;
import com.origin.banyu.publisher.dto.request.ShareReviewListRequest;
import com.origin.banyu.publisher.dto.request.ShareReviewRequest;
import com.origin.banyu.publisher.dto.request.SubmitShareReviewListDTO;
import com.origin.banyu.publisher.dto.response.ShareReviewResponse;
import com.origin.banyu.publisher.service.PublisherTaskShareReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社群分享审核控制器
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Slf4j
@RestController
@RequestMapping("/core/publisher/share/reviews")
@RequiredArgsConstructor
@Tag(name = "社群分享审核", description = "社群分享审核相关接口")
@Validated
public class PublisherTaskShareReviewController {
    
    private final PublisherTaskShareReviewService shareReviewService;
    
    @PostMapping
    @Operation(summary = "更新审核", description = "更新审核")
    public ResultData<String> submitShareReview(@RequestBody  ShareReviewRequest request) {
        String shareReviewId = shareReviewService.submitShareReview(request);
        return ResultData.success("更新审核", shareReviewId);
    }
    @PostMapping("/list")
    @Operation(summary = "批量更新审核", description = "批量更新审核")
    public ResultData<String> submitShareReviewList(@RequestBody SubmitShareReviewListDTO request) {
        String shareReviewId = shareReviewService.submitShareReviewList(request);
        return ResultData.success("更新审核", shareReviewId);
    }
    
    @PostMapping("/getList")
    @Operation(summary = "获取分享审核列表", description = "多条件获取分享审核列表：taskIds / taskName / reviewStatus / userIds / wechatNickname 可任意组合")
    public ResultData<IPage<ShareReviewResponse>> getShareReviewList(@Valid @RequestBody ShareReviewListRequest request) {
        log.info("获取分享审核列表请求: taskIds={}, taskName={}, reviewStatus={}, userIds={}, wechatNickname={}", 
                request.getTaskIds(), request.getTaskName(), request.getReviewStatus(), request.getUserIds(), request.getWechatNickname());
        
        try {
            
            IPage<ShareReviewResponse> result = shareReviewService.getShareReviewList(request);
            log.info("获取分享审核列表成功，共查询到{}条记录", result.getTotal());
            return ResultData.success("获取分享审核列表成功", result);
        } catch (Exception e) {
            log.error("获取分享审核列表失败", e);
            return ResultData.fail("获取分享审核列表失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/export")
    @Operation(summary = "导出分享审核列表", description = "导出分享审核列表为Excel文件，支持多条件查询：taskIds / taskName / reviewStatus / userIds / wechatNickname 可任意组合")
    public void exportShareReviewList(@Valid ShareReviewListRequest request, HttpServletResponse response) throws IOException {
        log.info("导出分享审核列表请求: taskIds={}, taskName={}, reviewStatus={}, userIds={}, wechatNickname={}", 
                request.getTaskIds(), request.getTaskName(), request.getReviewStatus(), request.getUserIds(), request.getWechatNickname());
        
        try {
            // 获取所有数据（不分页）
            List<ShareReviewResponse> dataList = shareReviewService.getShareReviewListAll(request);
            
            // 转换为导出DTO
            List<ShareReviewExportDTO> exportData = dataList.stream()
                    .map(ShareReviewExportDTO::fromResponse)
                    .collect(Collectors.toList());
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            
            // 生成文件名（包含查询条件信息）
            String fileName = generateFileName(request);
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
            
            // 使用EasyExcel生成Excel文件
            EasyExcel.write(response.getOutputStream(), ShareReviewExportDTO.class)
                    .sheet("分享审核列表")
                    .doWrite(exportData);
            
            log.info("分享审核列表导出成功，共导出{}条记录", exportData.size());
            
        } catch (Exception e) {
            log.error("导出分享审核列表失败", e);
            response.setContentType("text/plain;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("导出失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证请求参数
     * @param request 查询请求
     */
    private void validateRequest(ShareReviewListRequest request) {
        // 验证分页参数
        if (request.getPage() != null && request.getPage() < 1) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (request.getSize() != null && (request.getSize() < 1 || request.getSize() > 1000)) {
            throw new IllegalArgumentException("每页大小必须在1-1000之间");
        }
        
        // 验证列表参数大小
        if (request.getTaskIds() != null && request.getTaskIds().size() > 100) {
            throw new IllegalArgumentException("任务ID列表不能超过100个");
        }
        if (request.getUserIds() != null && request.getUserIds().size() > 100) {
            throw new IllegalArgumentException("用户ID列表不能超过100个");
        }
        
        // 验证审核状态
        if (request.getReviewStatus() != null && (request.getReviewStatus() < 1 || request.getReviewStatus() > 3)) {
            throw new IllegalArgumentException("审核状态必须是1(待审核)、2(已通过)或3(已拒绝)");
        }
    }
    
    /**
     * 根据查询条件生成文件名
     * @param request 查询请求
     * @return 文件名
     */
    private String generateFileName(ShareReviewListRequest request) {
        StringBuilder fileName = new StringBuilder("分享审核列表");
        
        if (request.getTaskIds() != null && !request.getTaskIds().isEmpty()) {
            String taskIdsStr = String.join(",", request.getTaskIds());
            fileName.append("_任务").append(taskIdsStr);
        }
        if (request.getTaskName() != null && !request.getTaskName().isEmpty()) {
            fileName.append("_").append(request.getTaskName());
        }
        if (request.getReviewStatus() != null) {
            String statusText = switch (request.getReviewStatus()) {
                case 1 -> "待审核";
                case 2 -> "已通过";
                case 3 -> "已拒绝";
                default -> "未知状态";
            };
            fileName.append("_").append(statusText);
        }
        if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
            String userIdsStr = String.join(",", request.getUserIds());
            fileName.append("_用户").append(userIdsStr);
        }
        if (request.getWechatNickname() != null && !request.getWechatNickname().isEmpty()) {
            fileName.append("_").append(request.getWechatNickname());
        }
        
        fileName.append("_").append(System.currentTimeMillis());
        return fileName.toString();
    }
} 