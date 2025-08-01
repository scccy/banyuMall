package com.origin.publisher.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.origin.common.dto.ResultData;
import com.origin.publisher.dto.ShareReviewQueryRequest;
import com.origin.publisher.dto.TaskReviewRequest;
import com.origin.publisher.entity.PublisherShareReview;
import com.origin.publisher.service.PublisherShareReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



/**
 * 社群分享审核控制器
 * 作者: scccy
 * 创建时间: 2025-08-01
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
    @Operation(summary = "提交社群分享审核", description = "提交社群分享审核申请")
    public ResultData<PublisherShareReview> submitShareReview(@RequestBody @Valid PublisherShareReview review) {
        log.info("提交社群分享审核请求，参数：{}", review);
        PublisherShareReview result = shareReviewService.submitShareReview(review);
        return ResultData.ok("分享审核提交成功", result);
    }
    
    @PutMapping("/{id}/review")
    @Operation(summary = "审核社群分享", description = "审核社群分享申请")
    public ResultData<Boolean> reviewShareReview(@PathVariable String id,
                                                 @RequestBody @Valid TaskReviewRequest request) {
        log.info("审核社群分享请求，ID：{}，审核参数：{}", id, request);
        boolean result = shareReviewService.reviewShareReview(id, request);
        return ResultData.ok("分享审核处理成功", result);
    }
    
    @GetMapping
    @Operation(summary = "查询社群分享审核列表", description = "查询社群分享审核列表")
    public ResultData<IPage<PublisherShareReview>> queryShareReviews(ShareReviewQueryRequest request) {
        log.info("查询社群分享审核列表请求，参数：{}", request);
        IPage<PublisherShareReview> result = shareReviewService.queryShareReviews(request);
        return ResultData.ok("查询分享审核列表成功", result);
    }
} 