package com.origin.banyu.wechatWork.controller;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.wechatWork.service.WechatworkContactsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 企业微信联系人管理控制器
 * 
 * @author scccy
 */
@RestController
@RequestMapping("/tp/wechatWork/contacts")
@Slf4j
@RequiredArgsConstructor
public class WechatWorkContactsController {
    
    private final WechatworkContactsService contactsService;
    
    /**
     * 同步企业微信联系人信息
     * 主要逻辑：从MySQL中变量查询部门id，然后批量保存
     * 完全按照企业微信官方文档实现：https://developer.work.weixin.qq.com/document/path/90337
     * 
     * @param departmentId 部门ID，为null时同步所有部门
     * @param fetchChild 是否递归获取子部门下面的成员：1-是，0-否
     * @return 同步结果
     */
    @PostMapping("/sync")
    public ResultData<String> syncWechatWorkContacts(
            @RequestParam(value = "departmentId", required = false) Integer departmentId,
            @RequestParam(value = "fetchChild", required = false, defaultValue = "0") Integer fetchChild) {
        try {
            log.info("开始同步企业微信联系人信息: departmentId={}, fetchChild={}", departmentId, fetchChild);
            
            int count = contactsService.syncWechatWorkContacts(departmentId, fetchChild);
            
            String message = departmentId != null ? 
                    String.format("部门 %d 联系人同步成功，共同步 %d 个联系人", departmentId, count) :
                    String.format("全量联系人同步成功，共同步 %d 个联系人", count);
            
            log.info("企业微信联系人同步完成: {}", message);
            return ResultData.success(message);
            
        } catch (Exception e) {
            log.error("同步企业微信联系人失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "同步失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取联系人同步状态
     * 
     * @return 同步状态信息
     */
    @GetMapping("/sync/status")
    public ResultData<Object> getSyncStatus() {
        try {
            // 获取联系人总数
            int totalContacts = contactsService.getAllContacts().size();
            
            // 构建状态信息
            var status = new Object() {
                public final String lastSyncTime = java.time.LocalDateTime.now().toString();
                public final String status = "ready";
            };
            
            return ResultData.success(status);
            
        } catch (Exception e) {
            log.error("获取联系人同步状态失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "获取状态失败");
        }
    }
    
    /**
     * 根据部门ID获取联系人列表
     * 
     * @param departmentId 部门ID
     * @return 联系人列表
     */
    @GetMapping("/department/{departmentId}")
    public ResultData<Object> getContactsByDepartment(@PathVariable("departmentId") Integer departmentId) {
        try {
            var contacts = contactsService.getContactsByDepartmentId(departmentId);
            return ResultData.success(contacts);
        } catch (Exception e) {
            log.error("获取部门联系人列表失败: departmentId={}", departmentId, e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "获取部门联系人失败");
        }
    }
    
    /**
     * 获取所有联系人信息
     * 
     * @return 联系人列表
     */
    @GetMapping("/all")
    public ResultData<Object> getAllContacts() {
        try {
            var contacts = contactsService.getAllContacts();
            return ResultData.success(contacts);
        } catch (Exception e) {
            log.error("获取所有联系人信息失败", e);
            return ResultData.fail(ErrorCode.WECHATWORK_SERVICE_ERROR, "获取联系人失败");
        }
    }
}
