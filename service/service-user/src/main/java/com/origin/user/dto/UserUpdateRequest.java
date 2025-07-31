package com.origin.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 用户更新请求DTO
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Data
@Accessors(chain = true)
public class UserUpdateRequest {
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    private Integer gender;
    
    /**
     * 生日
     */
    private LocalDate birthday;
    
    // ==================== 扩展信息字段 ====================
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 公司名称
     */
    private String companyName;
    
    /**
     * 公司地址
     */
    private String companyAddress;
    
    /**
     * 联系人
     */
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;
    
    /**
     * 行业
     */
    private String industry;
    
    /**
     * 描述
     */
    private String description;
} 