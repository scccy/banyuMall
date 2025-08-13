package com.origin.banyu.publisher.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.publisher.dto.request.ShareReviewListRequest;
import com.origin.banyu.publisher.dto.request.ShareReviewRequest;
import com.origin.banyu.publisher.dto.response.ShareReviewResponse;

/**
 * 社群分享审核服务接口
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
public interface PublisherTaskShareReviewService {
    
    /**
     * 提交分享审核
     * @param request 分享审核请求
     * @return 分享审核ID
     */
    String submitShareReview(ShareReviewRequest request);
    
    /**
     * 审核分享内容
     * @param shareReviewId 分享审核ID
     * @param reviewStatus 审核状态
     * @param reviewComment 审核意见
     */
    void reviewShareReview(String shareReviewId, Integer reviewStatus, String reviewComment);
    
    /**
     * 获取分享审核列表
     * @param page 页码
     * @param size 每页大小
     * @param reviewStatus 审核状态
     * @return 分享审核列表
     */
     IPage<ShareReviewResponse> getShareReviewList(ShareReviewListRequest request);

     /**
      * 获取分享审核列表（不分页，用于导出）
      * @param request 查询条件
      * @return 全量分享审核列表
      */
     java.util.List<ShareReviewResponse> getShareReviewListAll(ShareReviewListRequest request);
} 