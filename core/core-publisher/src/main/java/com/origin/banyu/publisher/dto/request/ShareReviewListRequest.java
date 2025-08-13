package com.origin.banyu.publisher.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分享审核列表查询请求
 */
@Data
public class ShareReviewListRequest {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 10;

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "活动名称（任务名称，模糊匹配）")
    private String taskName;

    @Schema(description = "审核状态：1-待审核,2-通过,3-拒绝")
    private Integer reviewStatus;

    @Schema(description = "用户ID（提交人）")
    private String userId;

    @Schema(description = "微信昵称（提交人，模糊匹配）")
    private String wechatNickname;
}


