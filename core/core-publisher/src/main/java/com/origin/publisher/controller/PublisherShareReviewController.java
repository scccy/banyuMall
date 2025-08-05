package com.origin.publisher.controller;

import com.origin.common.dto.ResultData;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.publisher.dto.ShareReviewRequest;
import com.origin.publisher.dto.ShareReviewResponse;
import com.origin.publisher.service.PublisherShareReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 社群分享审核控制器
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Slf4j
@RestController
@RequestMapping("/core/publisher/share-reviews")
@RequiredArgsConstructor
@Tag(name = "社群分享审核", description = "社群分享审核相关接口")
@Validated
public class PublisherShareReviewController {
    
    private final PublisherShareReviewService shareReviewService;
    
    @PostMapping
    @Operation(summary = "提交分享审核", description = "提交社群分享审核申请")
    public ResultData<String> submitShareReview(@RequestBody @Valid ShareReviewRequest request) {
        log.info("提交分享审核请求，参数：{}", request);
        String shareReviewId = shareReviewService.submitShareReview(request);
        return ResultData.ok("分享审核提交成功", shareReviewId);
    }
    
    @PutMapping("/{shareReviewId}")
    @Operation(summary = "审核分享内容", description = "审核社群分享内容")
    public ResultData<Void> reviewShareReview(@PathVariable String shareReviewId,
                                              @RequestParam Integer reviewStatus,
                                              @RequestParam(required = false) String reviewComment) {
        log.info("审核分享内容请求，ID：{}，审核状态：{}，审核意见：{}", shareReviewId, reviewStatus, reviewComment);
        shareReviewService.reviewShareReview(shareReviewId, reviewStatus, reviewComment);
        return ResultData.ok("分享审核处理成功");
    }
    
    @GetMapping
    @Operation(summary = "获取分享审核列表", description = "获取分享审核列表")
    public ResultData<IPage<ShareReviewResponse>> getShareReviewList(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小必须大于0") @Max(value = 1000, message = "每页大小不能超过1000") Integer size,
            @RequestParam(required = false) Integer reviewStatus) {
        log.info("获取分享审核列表请求，页码：{}，大小：{}，审核状态：{}", page, size, reviewStatus);
        IPage<ShareReviewResponse> result = shareReviewService.getShareReviewList(page, size, reviewStatus);
        return ResultData.ok("获取分享审核列表成功", result);
    }
} 