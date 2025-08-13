package com.origin.banyu.publisher.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务完成详情响应DTO
 * 作者: AI小张
 * 创建时间: 2025-08-13
 */
@Data
public class CompletionDetailResponseDto {
    
    /**
     * 任务类型ID
     */
    private Integer taskTypeId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 普通任务详情（类型1,2,3,4,6）
     */
    private NormalTaskDetail normalTaskDetail;
    
    /**
     * 邀请任务详情（类型5）
     */
    private InviteTaskDetail inviteTaskDetail;
    
    /**
     * 排名任务详情（类型7）
     */
    private RankTaskDetail rankTaskDetail;
    
    /**
     * 普通任务详情内部类
     */
    @Data
    public static class NormalTaskDetail {
        /**
         * 用户ID
         */
        private String userId;
        
        /**
         * 微信昵称
         */
        private String wechatNickname;
        
        /**
         * 任务积分
         */
        private BigDecimal taskReward;
        
        /**
         * 积分状态
         */
        private Integer completionStatus;
        
        /**
         * 完成时间
         */
        private LocalDateTime completionTime;
    }
    
    /**
     * 邀请任务详情内部类
     */
    @Data
    public static class InviteTaskDetail {
        /**
         * 用户ID（邀请用户）
         */
        private String inviterUserId;
        
        /**
         * 企业微信昵称
         */
        private String wechatWorkNickname;
        
        /**
         * 用户ID（被邀请用户）
         */
        private String invitedUserId;
        
        /**
         * 任务积分
         */
        private BigDecimal taskReward;
        
        /**
         * 积分状态
         */
        private Integer completionStatus;
        
        /**
         * 发起邀请时间
         */
        private LocalDateTime inviteStartTime;
        
        /**
         * 成功邀请时间
         */
        private LocalDateTime inviteSuccessTime;
    }
    
    /**
     * 排名任务详情内部类
     */
    @Data
    public static class RankTaskDetail {
        /**
         * 排名ID
         */
        private String rankId;
        
        /**
         * 用户ID
         */
        private String userId;
        
        /**
         * 微信昵称
         */
        private String wechatNickname;
        
        /**
         * 获取积分
         */
        private BigDecimal earnedReward;
    }
}
