package com.origin.publisher.util;

import com.origin.publisher.dto.TaskCreateRequest;
import com.origin.publisher.dto.TaskUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 任务验证工具类
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Slf4j
@Component
public class TaskValidator {
    
    /**
     * 验证任务创建请求
     * @param request 创建请求
     * @return 验证结果
     */
    public ValidationResult validateCreateRequest(TaskCreateRequest request) {
        ValidationResult result = new ValidationResult();
        
        // 验证任务名称
        if (!StringUtils.hasText(request.getTaskName())) {
            result.addError("taskName", "任务名称不能为空");
        } else if (request.getTaskName().length() > 100) {
            result.addError("taskName", "任务名称长度不能超过100个字符");
        }
        
        // 验证任务类型
        if (request.getTaskTypeId() == null) {
            result.addError("taskTypeId", "任务类型不能为空");
        } else if (request.getTaskTypeId() < 1 || request.getTaskTypeId() > 7) {
            result.addError("taskTypeId", "无效的任务类型ID，必须在1-7之间");
        }
        
        // 验证任务积分
        if (request.getTaskReward() == null) {
            result.addError("taskReward", "任务积分不能为空");
        } else if (request.getTaskReward().compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("taskReward", "任务积分必须大于0");
        }
        
        // 验证任务描述长度
        if (StringUtils.hasText(request.getTaskDescription()) && 
            request.getTaskDescription().length() > 1000) {
            result.addError("taskDescription", "任务描述长度不能超过1000个字符");
        }
        
        // 验证任务图标URL
        if (StringUtils.hasText(request.getTaskIconUrl()) && 
            request.getTaskIconUrl().length() > 500) {
            result.addError("taskIconUrl", "任务图标URL长度不能超过500个字符");
        }
        
        return result;
    }
    
    /**
     * 验证任务更新请求
     * @param request 更新请求
     * @return 验证结果
     */
    public ValidationResult validateUpdateRequest(TaskUpdateRequest request) {
        ValidationResult result = new ValidationResult();
        
        // 验证任务名称
        if (!StringUtils.hasText(request.getTaskName())) {
            result.addError("taskName", "任务名称不能为空");
        } else if (request.getTaskName().length() > 100) {
            result.addError("taskName", "任务名称长度不能超过100个字符");
        }
        
        // 验证任务积分
        if (request.getTaskReward() != null && 
            request.getTaskReward().compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("taskReward", "任务积分必须大于0");
        }
        
        // 验证任务描述长度
        if (StringUtils.hasText(request.getTaskDescription()) && 
            request.getTaskDescription().length() > 1000) {
            result.addError("taskDescription", "任务描述长度不能超过1000个字符");
        }
        
        // 验证任务图标URL
        if (StringUtils.hasText(request.getTaskIconUrl()) && 
            request.getTaskIconUrl().length() > 500) {
            result.addError("taskIconUrl", "任务图标URL长度不能超过500个字符");
        }
        
        return result;
    }
    
    /**
     * 验证任务状态
     * @param statusId 状态ID
     * @return 是否有效
     */
    public boolean isValidTaskStatus(Integer statusId) {
        return statusId != null && statusId >= 1 && statusId <= 5;
    }
    
    /**
     * 验证任务类型
     * @param taskTypeId 任务类型ID
     * @return 是否有效
     */
    public boolean isValidTaskType(Integer taskTypeId) {
        return taskTypeId != null && taskTypeId >= 1 && taskTypeId <= 7;
    }
    
    /**
     * 验证完成状态
     * @param completionStatus 完成状态
     * @return 是否有效
     */
    public boolean isValidCompletionStatus(Integer completionStatus) {
        return completionStatus != null && completionStatus >= 1 && completionStatus <= 3;
    }
    
    /**
     * 验证审核状态
     * @param reviewStatus 审核状态
     * @return 是否有效
     */
    public boolean isValidReviewStatus(Integer reviewStatus) {
        return reviewStatus != null && reviewStatus >= 1 && reviewStatus <= 3;
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final java.util.Map<String, String> errors = new java.util.HashMap<>();
        
        public void addError(String field, String message) {
            errors.put(field, message);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public java.util.Map<String, String> getErrors() {
            return new java.util.HashMap<>(errors);
        }
        
        public String getErrorMessage() {
            if (errors.isEmpty()) {
                return "";
            }
            return String.join("; ", errors.values());
        }
    }
} 