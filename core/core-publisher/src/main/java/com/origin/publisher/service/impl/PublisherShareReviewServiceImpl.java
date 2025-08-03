package com.origin.publisher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.common.dto.PageResult;
import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;
import com.origin.publisher.dto.ShareReviewRequest;
import com.origin.publisher.dto.ShareReviewResponse;
import com.origin.publisher.entity.PublisherShareReview;
import com.origin.publisher.mapper.PublisherShareReviewMapper;
import com.origin.publisher.service.PublisherShareReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 社群分享审核服务实现类
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherShareReviewServiceImpl implements PublisherShareReviewService {
    
    private final PublisherShareReviewMapper shareReviewMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitShareReview(ShareReviewRequest request) {
        log.info("提交分享审核，请求参数：{}", request);
        
        PublisherShareReview review = new PublisherShareReview();
        review.setTaskId(request.getTaskId());
        review.setShareContent(request.getShareContent());
        review.setSharePlatform(request.getSharePlatform());
        review.setShareUrl(request.getShareUrl());
        review.setScreenshotUrl(request.getScreenshotUrl());
        review.setReviewStatusId(1); // 待审核状态
        
        shareReviewMapper.insert(review);
        
        log.info("分享审核提交成功，ID：{}", review.getShareReviewId());
        return review.getShareReviewId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewShareReview(String shareReviewId, Integer reviewStatus, String reviewComment) {
        log.info("审核分享内容，ID：{}，审核状态：{}，审核意见：{}", shareReviewId, reviewStatus, reviewComment);
        
        PublisherShareReview review = shareReviewMapper.selectById(shareReviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.TASK_REVIEW_NOT_FOUND);
        }
        
        if (review.getReviewStatusId() != 1) {
            throw new BusinessException(ErrorCode.TASK_REVIEW_STATUS_INVALID, "只有待审核状态的记录才能审核");
        }
        
        review.setReviewStatusId(reviewStatus);
        review.setReviewComment(reviewComment);
        
        shareReviewMapper.updateById(review);
        
        log.info("分享审核完成，ID：{}，审核结果：{}", shareReviewId, reviewStatus);
    }
    
    @Override
    public PageResult<ShareReviewResponse> getShareReviewList(Integer page, Integer size, Integer reviewStatus) {
        log.info("获取分享审核列表，页码：{}，大小：{}，审核状态：{}", page, size, reviewStatus);
        
        LambdaQueryWrapper<PublisherShareReview> wrapper = new LambdaQueryWrapper<>();
        
        if (reviewStatus != null) {
            wrapper.eq(PublisherShareReview::getReviewStatusId, reviewStatus);
        }
        
        wrapper.orderByDesc(PublisherShareReview::getCreatedTime);
        
        Page<PublisherShareReview> pageParam = new Page<>(page, size);
        Page<PublisherShareReview> result = shareReviewMapper.selectPage(pageParam, wrapper);
        
        List<ShareReviewResponse> responses = result.getRecords().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PageResult<>(responses, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    private ShareReviewResponse convertToResponse(PublisherShareReview review) {
        ShareReviewResponse response = new ShareReviewResponse();
        BeanUtils.copyProperties(review, response);
        return response;
    }
} 