package com.origin.banyu.publisher.dto.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享审核导出DTO
 * 用于EasyExcel导出Excel文件
 * 作者: scccy
 * 创建时间: 2025-08-13
 */
@Data
public class ShareReviewExportDTO {
    
    @ExcelProperty(value = "分享审核ID", index = 0)
    private String shareReviewId;
    
    @ExcelProperty(value = "任务ID", index = 1)
    private String taskId;
    
    @ExcelProperty(value = "提交用户ID", index = 2)
    private String userId;
    
    @ExcelProperty(value = "微信昵称", index = 3)
    private String wechatNickname;
    
    @ExcelProperty(value = "分享内容", index = 4)
    private String shareContent;
    
    @ExcelProperty(value = "分享平台", index = 5)
    private String sharePlatform;
    
    @ExcelProperty(value = "分享链接", index = 6)
    private String shareUrl;
    
    @ExcelProperty(value = "截图URL", index = 7)
    private String screenshotUrl;
    
    @ExcelProperty(value = "审核状态", index = 8)
    private String reviewStatus;
    
    @ExcelProperty(value = "审核意见", index = 9)
    private String reviewComment;
    
    @ExcelProperty(value = "创建时间", index = 10)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    /**
     * 将ShareReviewResponse转换为导出DTO
     * @param response 查询响应
     * @return 导出DTO
     */
    public static ShareReviewExportDTO fromResponse(com.origin.banyu.publisher.dto.response.ShareReviewResponse response) {
        ShareReviewExportDTO exportDTO = new ShareReviewExportDTO();
        exportDTO.setShareReviewId(response.getShareReviewId());
        exportDTO.setTaskId(response.getTaskId());
        exportDTO.setUserId(response.getUserId());
        exportDTO.setWechatNickname(response.getWechatNickname());
        exportDTO.setShareContent(response.getShareContent());
        exportDTO.setSharePlatform(response.getSharePlatform());
        
        // 处理多链接和多图片，用分号分隔
        if (response.getLinks() != null && !response.getLinks().isEmpty()) {
            exportDTO.setShareUrl(String.join("; ", response.getLinks()));
        }
        if (response.getImages() != null && !response.getImages().isEmpty()) {
            exportDTO.setScreenshotUrl(String.join("; ", response.getImages()));
        }
        
        // 转换审核状态为中文描述
        if (response.getReviewStatusId() != null) {
            switch (response.getReviewStatusId()) {
                case 1:
                    exportDTO.setReviewStatus("待审核");
                    break;
                case 2:
                    exportDTO.setReviewStatus("通过");
                    break;
                case 3:
                    exportDTO.setReviewStatus("拒绝");
                    break;
                default:
                    exportDTO.setReviewStatus("未知状态");
            }
        }
        
        exportDTO.setReviewComment(response.getReviewComment());
        exportDTO.setCreatedTime(response.getCreatedTime());
        
        return exportDTO;
    }
}
