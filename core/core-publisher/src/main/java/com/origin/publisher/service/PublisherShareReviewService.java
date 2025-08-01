package com.origin.publisher.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.publisher.dto.ShareReviewQueryRequest;
import com.origin.publisher.dto.TaskReviewRequest;
import com.origin.publisher.entity.PublisherShareReview;

/**
 * 社群分享审核服务接口
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
public interface PublisherShareReviewService {
    
    /**
     * 提交社群分享审核
     */
    PublisherShareReview submitShareReview(PublisherShareReview review);
    
    /**
     * 审核社群分享
     */
    boolean reviewShareReview(String id, TaskReviewRequest request);
    
    /**
     * 查询社群分享审核列表
     */
    IPage<PublisherShareReview> queryShareReviews(ShareReviewQueryRequest request);
} 