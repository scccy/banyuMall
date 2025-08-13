package com.origin.banyu.publisher.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 任务完成详情分页响应（外层携带 taskName 与 taskTypeId，包裹具体数据）
 */
@Data
public class CompletionDetailsPageResponse {
    private String taskName;
    private Integer taskTypeId;

    private long current;
    private long size;
    private long total;
    private long pages;

    private List<CompletionDetailResponseDto.NormalTaskDetail> normalRecords;
    private List<CompletionDetailResponseDto.InviteTaskDetail> inviteRecords;
    private List<CompletionDetailResponseDto.RankTaskDetail> rankRecords;
}


