package com.origin.banyu.publisher.dto.request;

import lombok.Data;

/**
 * 分享审核请求DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
public class ShareReviewRequest {
    

    private String shareReviewId;
    

    private String reviewComment;
    

    private Integer reviewStatusId;

} 