package com.origin.publisher.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 任务配置解析工具类
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Slf4j
@Component
public class TaskConfigParser {
    
    /**
     * 将Map配置转换为JSON字符串
     * @param config 配置Map
     * @return JSON字符串
     */
    public String parseToJson(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return "{}";
        }
        try {
            return JSON.toJSONString(config);
        } catch (Exception e) {
            log.error("配置转换为JSON失败", e);
            throw new RuntimeException("配置格式错误");
        }
    }
    
    /**
     * 将JSON字符串转换为Map配置
     * @param jsonConfig JSON字符串
     * @return 配置Map
     */
    public Map<String, Object> parseFromJson(String jsonConfig) {
        if (jsonConfig == null || jsonConfig.trim().isEmpty()) {
            return Map.of();
        }
        try {
            return JSON.parseObject(jsonConfig, Map.class);
        } catch (Exception e) {
            log.error("JSON配置解析失败: {}", jsonConfig, e);
            throw new RuntimeException("配置格式错误");
        }
    }
    
    /**
     * 验证任务配置格式
     * @param taskTypeId 任务类型ID
     * @param config 配置Map
     * @return 是否有效
     */
    public boolean validateTaskConfig(Integer taskTypeId, Map<String, Object> config) {
        if (config == null) {
            return true; // 允许空配置
        }
        
        try {
            switch (taskTypeId) {
                case 1: // 点赞任务
                    return validateLikeTaskConfig(config);
                case 2: // 评论任务
                    return validateCommentTaskConfig(config);
                case 3: // 讨论任务
                    return validateDiscussTaskConfig(config);
                case 4: // 分享任务
                    return validateShareTaskConfig(config);
                case 5: // 邀请任务
                    return validateInviteTaskConfig(config);
                case 6: // 反馈任务
                    return validateFeedbackTaskConfig(config);
                case 7: // 排行榜任务
                    return validateRankingTaskConfig(config);
                default:
                    log.warn("未知的任务类型ID: {}", taskTypeId);
                    return false;
            }
        } catch (Exception e) {
            log.error("任务配置验证失败", e);
            return false;
        }
    }
    
    private boolean validateLikeTaskConfig(Map<String, Object> config) {
        // 点赞任务配置验证
        return true; // 简化验证，实际可根据需要添加具体验证逻辑
    }
    
    private boolean validateCommentTaskConfig(Map<String, Object> config) {
        // 评论任务配置验证
        return true;
    }
    
    private boolean validateDiscussTaskConfig(Map<String, Object> config) {
        // 讨论任务配置验证
        return true;
    }
    
    private boolean validateShareTaskConfig(Map<String, Object> config) {
        // 分享任务配置验证
        return true;
    }
    
    private boolean validateInviteTaskConfig(Map<String, Object> config) {
        // 邀请任务配置验证
        return true;
    }
    
    private boolean validateFeedbackTaskConfig(Map<String, Object> config) {
        // 反馈任务配置验证
        return true;
    }
    
    private boolean validateRankingTaskConfig(Map<String, Object> config) {
        // 排行榜任务配置验证
        return true;
    }
} 