package com.origin.banyu.publisher.dto.request;

import lombok.Data;

/**
 * 任务完成详情查询请求DTO
 * 作者: AI小张
 * 创建时间: 2025-08-13
 */
@Data
public class CompletionsGetDetailsRequestDto {
    
    /**
     * 页码，默认1
     */
    private Integer page = 1;
    
    /**
     * 每页大小，默认10，最大1000
     */
    private Integer size = 10;
    
    /**
     * 任务ID
     */
    private String taskId;

    private Integer rankType ;

    private String starDate;

    private String endData;
}
