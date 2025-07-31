package com.origin.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 用户创建请求DTO
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Data
@Accessors(chain = true)
public class UserCreateRequest {
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 微信ID
     */
    @NotBlank(message = "微信ID不能为空")
    private String wechatId;
    
    /**
     * 有赞ID
     */
    @NotBlank(message = "有赞ID不能为空")
    private String youzanId;
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含字母、数字、下划线，长度4-20位")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
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
    
    /**
     * 用户类型：1-最高权限，2-普通发布者
     */
    @NotNull(message = "用户类型不能为空")
    @Min(value = 1, message = "用户类型不正确")
    @Max(value = 2, message = "用户类型不正确")
    private Integer userType;
} 