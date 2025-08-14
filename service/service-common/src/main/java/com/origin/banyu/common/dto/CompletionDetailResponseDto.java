package com.origin.banyu.common.dto;

import com.alibaba.fastjson2.annotation.JSONField;
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
        @JSONField(name = "wechatNickname", alternateNames = {"wechat_nickname", "user.wechatNickname"})
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
        @JSONField(name = "inviterUserId", alternateNames = {"inviter_user_id"})
        private String inviterUserId;
        
        /**
         * 企业微信昵称
         */
        @JSONField(name = "wechatWorkNickname", alternateNames = {"wechatWork_nickname"})
        private String wechatWorkNickname;
        
        /**
         * 用户ID（被邀请用户）
         */
        @JSONField(name = "invitedUserId", alternateNames = {"invited_user_id"})
        private String invitedUserId;
        /**
         * 企业微信昵称
         */
        @JSONField(name = "wechatNickname", alternateNames = {"wechat_nickname"})
        private String wechatNickname;
        /**
         * 任务积分
         */
        @JSONField(name = "taskReward", alternateNames = {"task_reward"})
        private BigDecimal taskReward;
        
        /**
         * 积分状态
         */
        @JSONField(name = "completionStatus", alternateNames = {"completion_status"})
        private Integer completionStatus;
        
        /**
         * 发起邀请时间
         */
        @JSONField(name = "inviteStartTime", alternateNames = {"invite_start_time"}, format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime inviteStartTime;
        
        /**
         * 成功邀请时间
         */
        @JSONField(name = "inviteSuccessTime", alternateNames = {"invite_success_time"}, format = "yyyy-MM-dd HH:mm:ss")
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
