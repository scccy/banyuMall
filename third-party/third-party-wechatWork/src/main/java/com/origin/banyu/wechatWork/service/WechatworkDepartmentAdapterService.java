package com.origin.banyu.wechatWork.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.origin.banyu.wechatWork.adapter.WechatWorkDepartmentApiAdapter;
import com.origin.banyu.wechatWork.dto.WechatWorkDepartmentInfo;
import com.origin.banyu.wechatWork.entity.WechatworkDepartment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业微信部门适配器服务类
 * 专门负责部门数据同步和批量处理，给迭代器使用
 * 专注于数据层面的操作，不包含业务逻辑
 * 
 * @author scccy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WechatworkDepartmentAdapterService {
    
    private final WechatWorkDepartmentApiAdapter wechatWorkApiAdapter;
    private final AccessTokenService accessTokenService;
    private final WechatworkDepartmentService departmentService;

    /**
     * 同步企业微信部门信息（迭代器专用）
     * 
     * @param departmentId 部门ID，为null时同步所有部门
     * @return 同步的部门数量
     */
    @Transactional
    public int syncWechatWorkDepartments(Integer departmentId) {
        try {
            log.info("迭代器开始同步企业微信部门信息，部门ID: {}", departmentId);
            
            String accessToken = accessTokenService.getAccessToken();

                // 全量同步所有部门 - 从企业微信API获取，批量保存到MySQL
            int   totalCount = syncAllDepartmentsBatch(accessToken);

            
            log.info("迭代器企业微信部门同步完成，共同步 {} 个部门", totalCount);
            return totalCount;
            
        } catch (Exception e) {
            log.error("迭代器同步企业微信部门失败", e);
            throw new RuntimeException("迭代器同步企业微信部门失败: " + e.getMessage(), e);
        }
    }

    /**
     * 全量批量同步所有部门
     * 
     * @param accessToken 访问令牌
     * @return 同步的部门数量
     */
    private int syncAllDepartmentsBatch(String accessToken) {
        try {
            log.info("迭代器开始全量批量同步企业微信部门信息");
            
            // 1. 从企业微信API获取所有部门信息
            List<WechatWorkDepartmentInfo> allDepartments = wechatWorkApiAdapter.getAllDepartments(accessToken);
            log.info("迭代器从企业微信API获取到 {} 个部门", allDepartments.size());
            
            if (allDepartments.isEmpty()) {
                log.warn("迭代器未获取到任何部门信息");
                return 0;
            }
            
            // 2. 批量保存到MySQL
            int savedCount = batchSaveDepartments(allDepartments);
            
            log.info("迭代器全量批量同步完成，成功保存 {} 个部门到MySQL", savedCount);
            return savedCount;
            
        } catch (Exception e) {
            log.error("迭代器全量批量同步部门失败", e);
            throw new RuntimeException("迭代器全量批量同步部门失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量保存部门信息到MySQL
     * 
     * @param departments 部门信息列表
     * @return 保存成功的部门数量
     */
    private int batchSaveDepartments(List<WechatWorkDepartmentInfo> departments) {
        try {
            log.info("迭代器开始批量保存 {} 个部门到MySQL", departments.size());
            
            // 1. 清空现有部门数据（全量同步模式）
            int deletedCount = departmentService.getBaseMapper().delete(null);
            log.info("迭代器清空现有部门数据，删除 {} 条记录", deletedCount);
            
            // 2. 批量插入新数据
            java.util.List<WechatworkDepartment> entityList = new java.util.ArrayList<>(departments.size());
            for (WechatWorkDepartmentInfo deptInfo : departments) {
                try {
                    WechatworkDepartment department = WechatworkDepartment.fromDto(deptInfo);
                    entityList.add(department);
                } catch (Exception e) {
                    log.error("迭代器构建部门实体失败: depId={}, name={}", deptInfo.getId(), deptInfo.getName(), e);
                }
            }
            boolean saved = departmentService.saveBatch(entityList);
            log.info("迭代器批量插入完成，成功插入 {}/{} 个部门", saved ? entityList.size() : 0, departments.size());
            return saved ? entityList.size() : 0;
            
        } catch (Exception e) {
            log.error("迭代器批量保存部门信息失败", e);
            throw new RuntimeException("迭代器批量保存部门信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从MySQL同步指定部门信息
     * 
     * @param departmentId 部门ID
     * @return 同步的部门数量
     */
    private int syncSingleDepartmentFromDB(Integer departmentId) {
        try {
            log.info("迭代器从MySQL同步部门信息，部门ID: {}", departmentId);
            
            WechatworkDepartment department = departmentService.getById(departmentId);
            if (department == null) {
                log.warn("迭代器部门不存在，部门ID: {}", departmentId);
                return 0;
            }
            
            log.info("迭代器从MySQL同步部门信息成功，部门ID: {}, 部门名称: {}", 
                    department.getDepId(), department.getDepName());
            return 1;
            
        } catch (Exception e) {
            log.error("迭代器从MySQL同步部门信息失败，部门ID: {}", departmentId, e);
            throw new RuntimeException("迭代器从MySQL同步部门信息失败: " + e.getMessage(), e);
        }
    }

    // 转换逻辑已迁移至实体：WechatworkDepartment.fromDto(WechatWorkDepartmentInfo)

    /**
     * 获取所有部门信息（迭代器专用）
     * 
     * @return 部门列表
     */
    public List<WechatworkDepartment> getAllDepartments() {
        try {
            return departmentService.list();
        } catch (Exception e) {
            log.error("迭代器获取所有部门信息失败", e);
            throw new RuntimeException("迭代器获取所有部门信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据父部门ID获取子部门列表（迭代器专用）
     * 
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    public List<WechatworkDepartment> getDepartmentsByParentId(Integer parentId) {
        try {
            return departmentService.list(new QueryWrapper<WechatworkDepartment>().eq("parentid", parentId));
        } catch (Exception e) {
            log.error("迭代器获取子部门列表失败，父部门ID: {}", parentId, e);
            throw new RuntimeException("迭代器获取子部门列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建部门层级树（迭代器专用）
     * 
     * @return 部门层级树
     */
    public List<WechatworkDepartment> buildDepartmentTree() {
        try {
            List<WechatworkDepartment> allDepartments = getAllDepartments();
            return buildTree(allDepartments, 0); // 0表示根部门
        } catch (Exception e) {
            log.error("迭代器构建部门层级树失败", e);
            throw new RuntimeException("迭代器构建部门层级树失败: " + e.getMessage(), e);
        }
    }

    /**
     * 递归构建部门树
     * 
     * @param allDepartments 所有部门
     * @param parentId 父部门ID
     * @return 部门树
     */
    private List<WechatworkDepartment> buildTree(List<WechatworkDepartment> allDepartments, Integer parentId) {
        return allDepartments.stream()
                .filter(dept -> dept.getParentid().equals(parentId))
                .collect(Collectors.toList());
    }

    /**
     * 计算部门层级路径（迭代器专用）
     * 
     * @param departmentId 部门ID
     * @return 层级路径字符串 (如: "1/2/3")
     */
    public String calculateDepartmentPath(Integer departmentId) {
        try {
            if (departmentId == null || departmentId == 0) {
                return "0";
            }
            
            List<String> pathParts = new ArrayList<>();
            Integer currentId = departmentId;
            
            while (currentId != null && currentId != 0) {
                WechatworkDepartment dept = departmentService.getById(currentId);
                if (dept == null) {
                    break;
                }
                pathParts.add(0, currentId.toString());
                currentId = dept.getParentid();
            }
            
            return String.join("/", pathParts);
            
        } catch (Exception e) {
            log.error("迭代器计算部门层级路径失败，部门ID: {}", departmentId, e);
            return String.valueOf(departmentId);
        }
    }

    /**
     * 获取部门的所有子部门ID（包括子子部门）（迭代器专用）
     * 
     * @param departmentId 部门ID
     * @return 所有子部门ID列表
     */
    public List<Integer> getAllChildDepartmentIds(Integer departmentId) {
        try {
            List<Integer> allChildIds = new ArrayList<>();
            collectChildIds(departmentId, allChildIds);
            return allChildIds;
        } catch (Exception e) {
            log.error("迭代器获取部门所有子部门ID失败，部门ID: {}", departmentId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 递归收集子部门ID
     * 
     * @param departmentId 部门ID
     * @param allChildIds 所有子部门ID列表
     */
    private void collectChildIds(Integer departmentId, List<Integer> allChildIds) {
        List<WechatworkDepartment> children = getDepartmentsByParentId(departmentId);
        for (WechatworkDepartment child : children) {
            allChildIds.add(child.getDepId());
            collectChildIds(child.getDepId(), allChildIds);
        }
    }

    /**
     * 获取部门深度（迭代器专用）
     * 
     * @param departmentId 部门ID
     * @return 部门深度
     */
    public int getDepartmentDepth(Integer departmentId) {
        try {
            if (departmentId == null || departmentId == 0) {
                return 0;
            }
            
            int depth = 0;
            Integer currentId = departmentId;
            
            while (currentId != null && currentId != 0) {
                WechatworkDepartment dept = departmentService.getById(currentId);
                if (dept == null) {
                    break;
                }
                depth++;
                currentId = dept.getParentid();
            }
            
            return depth;
            
        } catch (Exception e) {
            log.error("迭代器获取部门深度失败，部门ID: {}", departmentId, e);
            return 0;
        }
    }

    /**
     * 验证部门层级关系（迭代器专用）
     * 
     * @return 验证结果
     */
    public boolean validateDepartmentHierarchy() {
        try {
            List<WechatworkDepartment> allDepartments = getAllDepartments();
            
            for (WechatworkDepartment dept : allDepartments) {
                if (dept.getParentid() != null && dept.getParentid() != 0) {
                    WechatworkDepartment parent = departmentService.getById(dept.getParentid());
                    if (parent == null) {
                        log.error("迭代器部门层级关系验证失败：部门 {} 的父部门 {} 不存在", 
                                dept.getDepId(), dept.getParentid());
                        return false;
                    }
                }
            }
            
            log.info("迭代器部门层级关系验证通过");
            return true;
            
        } catch (Exception e) {
            log.error("迭代器验证部门层级关系失败", e);
            return false;
        }
    }

}
