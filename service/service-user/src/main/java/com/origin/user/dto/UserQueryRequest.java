package com.origin.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;



/**
 * 用户查询请求DTO
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Data
@Accessors(chain = true)
public class UserQueryRequest {
    
    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer current = 1;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 10;
    
    /**
     * 用户名（模糊查询）
     */
    private String username;
    
    /**
     * 昵称（模糊查询）
     */
    private String nickname;
    
    /**
     * 手机号（精确查询）
     */
    private String phone;
    
    /**
     * 邮箱（模糊查询）
     */
    private String email;
    
    /**
     * 用户类型：1-最高权限，2-普通发布者
     */
    private Integer userType;
    
    /**
     * 状态：0-禁用，1-正常，2-待审核，3-已删除
     */
    private Integer status;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
} 