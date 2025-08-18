package com.origin.banyu.wechatWork.service;

import com.origin.banyu.wechatWork.entity.WechatworkDepartment;
import com.origin.banyu.wechatWork.mapper.WechatworkDepartmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class WechatworkDepartmentService {
    
    private final WechatworkDepartmentMapper departmentMapper;
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

    /**
     * 获取所有部门信息（控制器专用）
     * 
     * @return 部门列表
     */
    public List<WechatworkDepartment> getAllDepartments() {
        try {
            log.info("控制器开始获取所有部门信息");
            List<WechatworkDepartment> departments = departmentMapper.selectAllDepartments();
            log.info("控制器获取所有部门信息成功，部门数量: {}", departments.size());
            return departments;
        } catch (Exception e) {
            log.error("控制器获取所有部门信息失败", e);
            throw new RuntimeException("控制器获取所有部门信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据部门ID获取部门信息（控制器专用）
     * 
     * @param depId 部门ID
     * @return 部门信息
     */
    public WechatworkDepartment getDepartmentById(Integer depId) {
        if (depId == null) {
            log.warn("控制器查询部门信息失败：部门ID为空");
            return null;
        }
        
        try {
            log.info("控制器查询部门信息，部门ID: {}", depId);
            WechatworkDepartment department = departmentMapper.selectByDepId(depId);
            
            if (department == null) {
                log.info("控制器查询部门信息：部门不存在，部门ID: {}", depId);
            } else {
                log.info("控制器查询部门信息成功，部门ID: {}, 部门名称: {}", depId, department.getDepName());
            }
            
            return department;
        } catch (Exception e) {
            log.error("控制器根据部门ID查询部门信息失败，部门ID: {}", depId, e);
            throw new RuntimeException("控制器查询部门信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据父部门ID获取子部门列表（控制器专用）
     * 
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    public List<WechatworkDepartment> getDepartmentsByParentId(Integer parentId) {
        if (parentId == null) {
            log.warn("控制器查询子部门列表失败：父部门ID为空");
            return new java.util.ArrayList<>();
        }
        
        try {
            log.info("控制器查询子部门列表，父部门ID: {}", parentId);
            List<WechatworkDepartment> departments = departmentMapper.selectByParentId(parentId);
            log.info("控制器查询子部门列表成功，父部门ID: {}, 子部门数量: {}", parentId, departments.size());
            return departments;
        } catch (Exception e) {
            log.error("控制器获取子部门列表失败，父部门ID: {}", parentId, e);
            throw new RuntimeException("控制器获取子部门列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建部门层级树（控制器专用）
     * 
     * @return 部门层级树
     */
    public List<WechatworkDepartment> buildDepartmentTree() {
        try {
            log.info("控制器开始构建部门层级树");
            List<WechatworkDepartment> tree = departmentAdapterService.buildDepartmentTree();
            log.info("控制器构建部门层级树成功");
            return tree;
        } catch (Exception e) {
            log.error("控制器构建部门层级树失败", e);
            throw new RuntimeException("控制器构建部门层级树失败: " + e.getMessage(), e);
        }
    }

    /**
     * 计算部门层级路径（控制器专用）
     * 
     * @param departmentId 部门ID
     * @return 层级路径字符串 (如: "1/2/3")
     */
    public String calculateDepartmentPath(Integer departmentId) {
        if (departmentId == null) {
            log.warn("控制器计算部门层级路径失败：部门ID为空");
            return "0";
        }
        
        try {
            log.info("控制器开始计算部门层级路径，部门ID: {}", departmentId);
            String path = departmentAdapterService.calculateDepartmentPath(departmentId);
            log.info("控制器计算部门层级路径成功，部门ID: {}, 路径: {}", departmentId, path);
            return path;
        } catch (Exception e) {
            log.error("控制器计算部门层级路径失败，部门ID: {}", departmentId, e);
            return String.valueOf(departmentId);
        }
    }

    /**
     * 获取部门的所有子部门ID（包括子子部门）（控制器专用）
     * 
     * @param departmentId 部门ID
     * @return 所有子部门ID列表
     */
    public List<Integer> getAllChildDepartmentIds(Integer departmentId) {
        if (departmentId == null) {
            log.warn("控制器获取部门所有子部门ID失败：部门ID为空");
            return new java.util.ArrayList<>();
        }
        
        try {
            log.info("控制器开始获取部门所有子部门ID，部门ID: {}", departmentId);
            List<Integer> childIds = departmentAdapterService.getAllChildDepartmentIds(departmentId);
            log.info("控制器获取部门所有子部门ID成功，部门ID: {}, 子部门数量: {}", departmentId, childIds.size());
            return childIds;
        } catch (Exception e) {
            log.error("控制器获取部门所有子部门ID失败，部门ID: {}", departmentId, e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 获取部门深度（控制器专用）
     * 
     * @param departmentId 部门ID
     * @return 部门深度
     */
    public int getDepartmentDepth(Integer departmentId) {
        if (departmentId == null) {
            log.warn("控制器获取部门深度失败：部门ID为空");
            return 0;
        }
        
        try {
            log.info("控制器开始获取部门深度，部门ID: {}", departmentId);
            int depth = departmentAdapterService.getDepartmentDepth(departmentId);
            log.info("控制器获取部门深度成功，部门ID: {}, 深度: {}", departmentId, depth);
            return depth;
        } catch (Exception e) {
            log.error("控制器获取部门深度失败，部门ID: {}", departmentId, e);
            return 0;
        }
    }

    /**
     * 验证部门层级关系（控制器专用）
     * 
     * @return 验证结果
     */
    public boolean validateDepartmentHierarchy() {
        try {
            log.info("控制器开始验证部门层级关系");
            boolean isValid = departmentAdapterService.validateDepartmentHierarchy();
            log.info("控制器验证部门层级关系完成，结果: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.error("控制器验证部门层级关系失败", e);
            return false;
        }
    }

    /**
     * 获取部门统计信息（控制器专用）
     * 
     * @return 部门统计信息
     */
    public Object getDepartmentStatistics() {
        try {
            log.info("控制器开始获取部门统计信息");
            
            // 获取所有部门
            List<WechatworkDepartment> allDepartments = departmentMapper.selectAllDepartments();
            
            // 统计根部门和子部门数量
            long rootDepartments = allDepartments.stream().filter(dept -> dept.getParentid() == null || dept.getParentid() == 0).count();
            long childDepartments = allDepartments.size() - rootDepartments;
            
            // 构建统计信息
            java.util.Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("totalDepartments", allDepartments.size());
            statistics.put("rootDepartments", rootDepartments);
            statistics.put("childDepartments", childDepartments);
            statistics.put("lastUpdateTime", java.time.LocalDateTime.now().toString());
            
            log.info("控制器获取部门统计信息成功，总部门数: {}, 根部门: {}, 子部门: {}", 
                    statistics.get("totalDepartments"), statistics.get("rootDepartments"), statistics.get("childDepartments"));
            
            return statistics;
            
        } catch (Exception e) {
            log.error("控制器获取部门统计信息失败", e);
            throw new RuntimeException("控制器获取部门统计信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 搜索部门信息（控制器专用）
     * 
     * @param keyword 搜索关键词（部门名称等）
     * @return 匹配的部门列表
     */
    public List<WechatworkDepartment> searchDepartments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("控制器搜索部门失败：搜索关键词为空");
            return new java.util.ArrayList<>();
        }
        
        try {
            log.info("控制器开始搜索部门，关键词: {}", keyword);
            
            // 获取所有部门，然后进行本地搜索
            List<WechatworkDepartment> allDepartments = departmentMapper.selectAllDepartments();
            
            // 根据关键词过滤部门
            List<WechatworkDepartment> matchedDepartments = allDepartments.stream()
                    .filter(dept -> 
                        (dept.getDepName() != null && dept.getDepName().contains(keyword)) ||
                        (dept.getDepId() != null && dept.getDepId().toString().contains(keyword))
                    )
                    .collect(java.util.stream.Collectors.toList());
            
            log.info("控制器搜索部门完成，关键词: {}, 匹配部门数: {}", keyword, matchedDepartments.size());
            return matchedDepartments;
            
        } catch (Exception e) {
            log.error("控制器搜索部门失败，关键词: {}", keyword, e);
            throw new RuntimeException("控制器搜索部门失败: " + e.getMessage(), e);
        }
    }
}
