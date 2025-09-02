package com.origin.banyu.coreflink.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 注册汇总结果实体
 * 
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationSummary {
    
    /**
     * 时间窗口开始时间
     */
    private LocalDateTime windowStart;
    
    /**
     * 时间窗口结束时间
     */
    private LocalDateTime windowEnd;
    
    /**
     * 注册总人数
     */
    private Long totalRegistrations;
    
    /**
     * 男性注册人数
     */
    private Long maleCount;
    
    /**
     * 女性注册人数
     */
    private Long femaleCount;
    
    /**
     * 未知性别注册人数
     */
    private Long unknownCount;
    
    /**
     * 处理时间
     */
    private LocalDateTime processTime;
    
    /**
     * 窗口类型
     */
    private String windowType = "TUMBLING_1_MINUTE";
}
