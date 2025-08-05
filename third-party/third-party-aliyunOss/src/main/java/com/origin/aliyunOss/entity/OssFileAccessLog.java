package com.origin.aliyunOss.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * OSS文件访问日志实体
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oss_file_access_log")
public class OssFileAccessLog {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    @TableField("file_id")
    private Long fileId;

    /**
     * 访问类型(VIEW/DOWNLOAD)
     */
    @TableField("access_type")
    private String accessType;

    /**
     * 访问用户ID
     */
    @TableField("access_user_id")
    private Long accessUserId;

    /**
     * 访问用户名
     */
    @TableField("access_user_name")
    private String accessUserName;

    /**
     * 访问IP地址
     */
    @TableField("access_ip")
    private String accessIp;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 来源页面
     */
    @TableField("referer")
    private String referer;

    /**
     * 访问时间
     */
    @TableField("access_time")
    private LocalDateTime accessTime;

    /**
     * 响应时间(毫秒)
     */
    @TableField("response_time")
    private Integer responseTime;

    /**
     * HTTP状态码
     */
    @TableField("status_code")
    private Integer statusCode;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;
} 