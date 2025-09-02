package com.origin.banyu.coreflink.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户注册事件实体
 * 
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationEvent {
    
    /**
     * 注册人ID
     */
    private String userId;
    
    /**
     * 注册时间
     */
    private LocalDateTime registerTime;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 性别 (MALE/FEMALE/UNKNOWN)
     */
    private String gender;
    
    /**
     * 事件时间戳（用于Flink时间处理）
     */
    private Long timestamp;
    
    /**
     * 数据源
     */
    private String source = "user-registration";
}
