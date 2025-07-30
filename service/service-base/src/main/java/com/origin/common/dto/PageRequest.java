package com.origin.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 分页请求DTO
 * 
 * @author origin
 * @since 2024-12-19
 */
@Data
@Accessors(chain = true)
public class PageRequest {
    
    /**
     * 当前页码，默认第1页
     */
    private Integer current = 1;
    
    /**
     * 每页大小，默认10条
     */
    private Integer size = 10;
    
    /**
     * 排序字段
     */
    private String orderBy;
    
    /**
     * 排序方向：asc/desc，默认desc
     */
    private String orderDirection = "desc";
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 类型
     */
    private String type;
} 