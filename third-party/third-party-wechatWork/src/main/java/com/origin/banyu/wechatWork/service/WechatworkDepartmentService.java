package com.origin.banyu.wechatWork.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.wechatWork.entity.WechatworkDepartment;
import com.origin.banyu.wechatWork.mapper.WechatworkDepartmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 企业微信部门业务服务类
 * 专门负责业务逻辑和API接口，给控制器使用
 * 专注于业务层面的操作，调用WechatworkDepartmentAdapterService进行数据操作
 * 
 * @author scccy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WechatworkDepartmentService extends ServiceImpl<WechatworkDepartmentMapper, WechatworkDepartment> {
    
    private final WechatworkDepartmentAdapterService departmentAdapterService;

    /**
     * 同步企业微信部门信息（控制器专用）
     * 调用适配器服务进行实际的数据同步操作
     * 
     * @param departmentId 部门ID，为null时同步所有部门
     * @return 同步的部门数量
     */
    public int syncWechatWorkDepartments(Integer departmentId) {
        try {
            log.info("控制器开始同步企业微信部门信息，部门ID: {}", departmentId);
            
            // 调用适配器服务进行数据同步
            int totalCount = departmentAdapterService.syncWechatWorkDepartments(departmentId);
            
            log.info("控制器企业微信部门同步完成，共同步 {} 个部门", totalCount);
            return totalCount;
            
        } catch (Exception e) {
            log.error("控制器同步企业微信部门失败", e);
            throw new RuntimeException("控制器同步企业微信部门失败: " + e.getMessage(), e);
        }
    }


}
