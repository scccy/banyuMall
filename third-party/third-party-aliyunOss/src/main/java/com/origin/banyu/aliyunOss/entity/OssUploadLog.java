package com.origin.banyu.aliyunOss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.origin.banyu.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * OSS上传流水表
 */
@Schema(description = "OSS上传流水表")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "oss_upload_log")
public class OssUploadLog extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 上传记录ID
     */
    @TableId(value = "log_id", type = IdType.INPUT)
    @Schema(description = "上传记录ID")
    private String logId;

    /**
     * 原始文件名
     */
    @TableField(value = "original_name")
    @Schema(description = "原始文件名")
    private String originalName;

    /**
     * 对象路径（目录，如：uploads/2025/08/09）
     */
    @TableField(value = "object_path")
    @Schema(description = "对象路径（目录，如：uploads/2025/08/09）")
    private String objectPath;

    /**
     * 保存到OSS的文件名
     */
    @TableField(value = "file_name")
    @Schema(description = "保存到OSS的文件名")
    private String fileName;

    /**
     * OSS对象键
     */
    @TableField(value = "object_key")
    @Schema(description = "OSS对象键")
    private String objectKey;

    /**
     * 返回的访问URL
     */
    @TableField(value = "access_url")
    @Schema(description = "返回的访问URL")
    private String accessUrl;
}