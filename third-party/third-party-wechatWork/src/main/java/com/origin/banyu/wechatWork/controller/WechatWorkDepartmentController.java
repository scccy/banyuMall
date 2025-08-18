package com.origin.banyu.wechatWork.controller;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.wechatWork.service.WechatworkDepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 企业微信部门管理控制器
 * 
 * @author scccy
 */
@RestController
@RequestMapping("/tp/wechatWork/department")
@Slf4j
@RequiredArgsConstructor
public class WechatWorkDepartmentController {
    
    private final WechatworkDepartmentService departmentService;
    
    /**
     * 同步企业微信部门信息
     * 
     * @param departmentId 部门ID，为null时同步所有部门
     * @return 同步结果
     */
    @PostMapping("/sync")
    public ResultData<String> syncWechatWorkDepartments(@RequestParam(value = "departmentId", required = false) Integer departmentId) {
        try {
            log.info("开始同步企业微信部门信息: departmentId={}", departmentId);
            
            int count = departmentService.syncWechatWorkDepartments(departmentId);
            
            String message = departmentId != null ? 
                    String.format("部门 %d 同步成功，共同步 %d 个部门", departmentId, count) :
                    String.format("全量同步成功，共同步 %d 个部门", count);
            
            log.info("企业微信部门同步完成: {}", message);
            return ResultData.success(message);
            
        } catch (Exception e) {
            log.error("同步企业微信部门失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "同步失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取部门同步状态
     * 
     * @return 同步状态信息
     */
    @GetMapping("/sync/status")
    public ResultData<Object> getSyncStatus() {
        try {
            // 获取部门总数
            int totalDepartments = departmentService.getAllDepartments().size();
            
            // 构建状态信息
            var status = new Object() {
                public final String lastSyncTime = java.time.LocalDateTime.now().toString();
                public final String status = "ready";
            };
            
            return ResultData.success(status);
            
        } catch (Exception e) {
            log.error("获取部门同步状态失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "获取状态失败");
        }
    }
}
