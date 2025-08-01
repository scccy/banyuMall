package com.origin.publisher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;
import com.origin.publisher.dto.ShareReviewQueryRequest;
import com.origin.publisher.dto.TaskReviewRequest;
import com.origin.publisher.entity.PublisherShareReview;
import com.origin.publisher.mapper.PublisherShareReviewMapper;
import com.origin.publisher.service.PublisherShareReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 社群分享审核服务实现类
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherShareReviewServiceImpl implements PublisherShareReviewService {
    
    private final PublisherShareReviewMapper shareReviewMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PublisherShareReview submitShareReview(PublisherShareReview review) {
        log.info("提交社群分享审核，请求参数：{}", review);
        
        review.setReviewStatus("PENDING");
        shareReviewMapper.insert(review);
        
        log.info("社群分享审核提交成功，ID：{}", review.getId());
        return review;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewShareReview(String id, TaskReviewRequest request) {
        log.info("审核社群分享，ID：{}，审核请求：{}", id, request);
        
        PublisherShareReview review = shareReviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException(ErrorCode.TASK_REVIEW_NOT_FOUND);
        }
        
        if (!"PENDING".equals(review.getReviewStatus())) {
            throw new BusinessException(ErrorCode.TASK_REVIEW_STATUS_INVALID, "只有待审核状态的记录才能审核");
        }
        
        review.setReviewerId(request.getReviewerId());
        review.setReviewStatus(request.getReviewStatus());
        review.setReviewComment(request.getReviewComment());
        
        shareReviewMapper.updateById(review);
        
        log.info("社群分享审核完成，ID：{}，审核结果：{}", id, request.getReviewStatus());
        return true;
    }
    
    @Override
    public IPage<PublisherShareReview> queryShareReviews(ShareReviewQueryRequest request) {
        log.info("查询社群分享审核列表，请求参数：{}", request);
        
        LambdaQueryWrapper<PublisherShareReview> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(request.getTaskId())) {
            wrapper.eq(PublisherShareReview::getTaskId, request.getTaskId());
        }
        if (StringUtils.hasText(request.getSharePlatform())) {
            wrapper.eq(PublisherShareReview::getSharePlatform, request.getSharePlatform());
        }
        if (StringUtils.hasText(request.getReviewStatus())) {
            wrapper.eq(PublisherShareReview::getReviewStatus, request.getReviewStatus());
        }
        if (StringUtils.hasText(request.getReviewerId())) {
            wrapper.eq(PublisherShareReview::getReviewerId, request.getReviewerId());
        }
        
        wrapper.orderByDesc(PublisherShareReview::getCreatedTime);
        
        Page<PublisherShareReview> page = new Page<>(request.getPageNum(), request.getPageSize());
        return shareReviewMapper.selectPage(page, wrapper);
    }
} 