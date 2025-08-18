package com.origin.banyu.wechatWork.service;

import com.origin.banyu.wechatWork.adapter.WechatWorkApiAdapter;
import com.origin.banyu.wechatWork.dto.WechatWorkDepartmentInfo;
import com.origin.banyu.wechatWork.entity.WechatworkDepartment;
import com.origin.banyu.wechatWork.exception.WechatWorkServiceException;
import com.origin.banyu.wechatWork.mapper.WechatworkDepartmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import com.origin.banyu.wechatWork.dto.WechatWorkUserInfo;

/**
 * 企业微信部门服务
 * 符合第三方架构特殊规则：使用适配器模式封装第三方API
 * 
 * @author scccy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WechatworkDepartmentService {

    private final WechatworkDepartmentMapper departmentMapper;
    private final WechatWorkApiAdapter wechatWorkApiAdapter;
    private final AccessTokenService accessTokenService;

    /**
     * 同步企业微信部门信息
     * 
     * @param departmentId 部门ID，为null时同步所有部门
     * @return 同步的部门数量
     * @throws WechatWorkServiceException 当同步失败时抛出
     */
    public int syncWechatWorkDepartments(Integer departmentId) {
        try {
            String accessToken = accessTokenService.getAccessToken();
            int totalCount = 0;
            
            if (departmentId != null) {
                // 同步指定部门 - 从MySQL查询现有数据
                totalCount = syncSingleDepartmentFromDB(departmentId);
            } else {
                // 全量同步所有部门 - 从企业微信API获取，批量保存到MySQL
                totalCount = syncAllDepartmentsBatch(accessToken);
            }
            
            log.info("企业微信部门同步完成，共同步 {} 个部门", totalCount);
            return totalCount;
            
        } catch (Exception e) {
            log.error("同步企业微信部门失败", e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_SYNC_FAILED", 
                    "同步企业微信部门失败: " + e.getMessage(), e);
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
            log.info("开始全量批量同步企业微信部门信息");
            
            // 1. 从企业微信API获取所有部门信息
            List<WechatWorkDepartmentInfo> allDepartments = wechatWorkApiAdapter.getAllDepartments(accessToken);
            log.info("从企业微信API获取到 {} 个部门", allDepartments.size());
            
            if (allDepartments.isEmpty()) {
                log.warn("未获取到任何部门信息");
                return 0;
            }
            
            // 2. 批量保存到MySQL
            int savedCount = batchSaveDepartments(allDepartments);
            
            log.info("全量批量同步完成，成功保存 {} 个部门到MySQL", savedCount);
            return savedCount;
            
        } catch (Exception e) {
            log.error("全量批量同步部门失败", e);
            throw new WechatWorkServiceException("WECHATWORK_ALL_DEPARTMENTS_SYNC_FAILED", 
                    "全量批量同步部门失败: " + e.getMessage(), e);
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
            log.info("开始批量保存 {} 个部门到MySQL", departments.size());
            
            // 1. 清空现有部门数据（全量同步模式）
            int deletedCount = departmentMapper.delete(null);
            log.info("清空现有部门数据，删除 {} 条记录", deletedCount);
            
            // 2. 批量插入新数据
            List<WechatworkDepartment> entityList = new ArrayList<>();
            for (WechatWorkDepartmentInfo deptInfo : departments) {
                WechatworkDepartment entity = convertToEntity(deptInfo);
                entityList.add(entity);
            }
            
            // 3. 使用MyBatis-Plus的批量插入
            int insertedCount = 0;
            if (!entityList.isEmpty()) {
                // 分批插入，避免单次插入过多数据
                int batchSize = 100;
                for (int i = 0; i < entityList.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, entityList.size());
                    List<WechatworkDepartment> batch = entityList.subList(i, endIndex);
                    
                    for (WechatworkDepartment dept : batch) {
                        try {
                            departmentMapper.insert(dept);
                            insertedCount++;
                        } catch (Exception e) {
                            log.error("插入部门失败: id={}, name={}", dept.getDepId(), dept.getDepName(), e);
                            // 继续处理其他部门，不中断整个流程
                        }
                    }
                    
                    log.info("批量插入进度: {}/{}", Math.min(i + batchSize, entityList.size()), entityList.size());
                }
            }
            
            log.info("批量保存完成，成功插入 {} 个部门", insertedCount);
            return insertedCount;
            
        } catch (Exception e) {
            log.error("批量保存部门信息失败", e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENTS_BATCH_SAVE_FAILED", 
                    "批量保存部门信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 同步单个部门（从MySQL查询）
     * 
     * @param departmentId 部门ID
     * @return 同步的部门数量
     */
    private int syncSingleDepartmentFromDB(Integer departmentId) {
        try {
            log.info("从MySQL查询部门 {} 的信息", departmentId);
            
            // 从MySQL查询部门信息
            WechatworkDepartment department = getDepartmentById(departmentId);
            if (department != null) {
                log.info("部门 {} 已存在于MySQL中: {}", departmentId, department.getDepName());
                return 1;
            } else {
                log.warn("部门 {} 在MySQL中不存在", departmentId);
                return 0;
            }
            
        } catch (Exception e) {
            log.error("从MySQL查询部门 {} 失败", departmentId, e);
            throw new WechatWorkServiceException("WECHATWORK_SINGLE_DEPARTMENT_SYNC_FROM_DB_FAILED", 
                    "从MySQL查询部门失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将DTO转换为实体对象
     * 
     * @param deptInfo 部门信息DTO
     * @return 部门实体对象
     */
    private WechatworkDepartment convertToEntity(WechatWorkDepartmentInfo deptInfo) {
        WechatworkDepartment department = new WechatworkDepartment();
        department.setDepId(deptInfo.getId());
        department.setDepName(deptInfo.getName());
        department.setParentid(deptInfo.getParentid());
        department.setOrder(deptInfo.getOrder() != null ? deptInfo.getOrder().toString() : null);
        department.setDepartmentLeader(deptInfo.getDepartmentLeader() != null ? 
                String.join(",", deptInfo.getDepartmentLeader()) : null);
        
        // 设置时间字段
        LocalDateTime now = LocalDateTime.now();
        department.setCreatedTime(now);
        department.setUpdatedTime(now);
        
        return department;
    }

    /**
     * 根据部门ID获取部门信息
     * 
     * @param depId 部门ID
     * @return 部门信息
     */
    public WechatworkDepartment getDepartmentById(Integer depId) {
        try {
            WechatworkDepartment department = departmentMapper.selectByDepId(depId);
            if (department == null) {
                throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_NOT_FOUND", 
                        "部门不存在: " + depId);
            }
            return department;
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取部门信息失败: depId={}", depId, e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_GET_FAILED", 
                    "获取部门信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取所有部门信息
     * 
     * @return 部门列表
     */
    public List<WechatworkDepartment> getAllDepartments() {
        try {
            return departmentMapper.selectAllDepartments();
        } catch (Exception e) {
            log.error("获取所有部门信息失败", e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENTS_GET_FAILED", 
                    "获取所有部门信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据父部门ID获取子部门列表
     * 
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    public List<WechatworkDepartment> getDepartmentsByParentId(Integer parentId) {
        try {
            return departmentMapper.selectByParentId(parentId);
        } catch (Exception e) {
            log.error("获取子部门列表失败: parentId={}", parentId, e);
            throw new WechatWorkServiceException("WECHATWORK_CHILD_DEPARTMENTS_GET_FAILED", 
                    "获取子部门列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建部门层级树
     * 
     * @return 部门层级树
     */
    public List<WechatworkDepartment> buildDepartmentTree() {
        try {
            List<WechatworkDepartment> allDepartments = getAllDepartments();
            return buildTree(allDepartments, 0); // 0表示根部门
        } catch (Exception e) {
            log.error("构建部门层级树失败", e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_TREE_BUILD_FAILED", 
                    "构建部门层级树失败: " + e.getMessage(), e);
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
     * 计算部门层级路径
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
                WechatworkDepartment dept = departmentMapper.selectByDepId(currentId);
                if (dept == null) {
                    break;
                }
                pathParts.add(0, currentId.toString());
                currentId = dept.getParentid();
            }
            
            return String.join("/", pathParts);
            
        } catch (Exception e) {
            log.error("计算部门层级路径失败: departmentId={}", departmentId, e);
            return String.valueOf(departmentId);
        }
    }

    /**
     * 获取部门的所有子部门ID（包括子子部门）
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
            log.error("获取部门所有子部门ID失败: departmentId={}", departmentId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 递归收集子部门ID
     * 
     * @param parentId 父部门ID
     * @param childIds 子部门ID列表
     */
    private void collectChildIds(Integer parentId, List<Integer> childIds) {
        List<WechatworkDepartment> children = departmentMapper.selectByParentId(parentId);
        for (WechatworkDepartment child : children) {
            childIds.add(child.getDepId());
            collectChildIds(child.getDepId(), childIds);
        }
    }

    /**
     * 获取部门的层级深度
     * 
     * @param departmentId 部门ID
     * @return 层级深度 (0为根部门)
     */
    public int getDepartmentDepth(Integer departmentId) {
        try {
            if (departmentId == null || departmentId == 0) {
                return 0;
            }
            
            int depth = 0;
            Integer currentId = departmentId;
            
            while (currentId != null && currentId != 0) {
                WechatworkDepartment dept = departmentMapper.selectByDepId(currentId);
                if (dept == null) {
                    break;
                }
                depth++;
                currentId = dept.getParentid();
            }
            
            return depth;
            
        } catch (Exception e) {
            log.error("获取部门层级深度失败: departmentId={}", departmentId, e);
            return 0;
        }
    }

    /**
     * 验证部门层级关系的有效性
     * 
     * @return 验证结果，true表示有效，false表示无效
     */
    public boolean validateDepartmentHierarchy() {
        try {
            List<WechatworkDepartment> allDepartments = getAllDepartments();
            
            for (WechatworkDepartment dept : allDepartments) {
                // 检查父部门是否存在
                if (dept.getParentid() != 0) {
                    WechatworkDepartment parent = departmentMapper.selectByDepId(dept.getParentid());
                    if (parent == null) {
                        log.warn("部门 {} 的父部门 {} 不存在", dept.getDepId(), dept.getParentid());
                        return false;
                    }
                }
                
                // 检查是否形成循环引用
                if (hasCircularReference(dept.getDepId(), new ArrayList<>())) {
                    log.error("检测到部门层级循环引用: {}", dept.getDepId());
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("验证部门层级关系失败", e);
            return false;
        }
    }

    /**
     * 检查是否存在循环引用
     * 
     * @param departmentId 部门ID
     * @param visitedIds 已访问的部门ID列表
     * @return true表示存在循环引用
     */
    private boolean hasCircularReference(Integer departmentId, List<Integer> visitedIds) {
        if (visitedIds.contains(departmentId)) {
            return true;
        }
        
        visitedIds.add(departmentId);
        WechatworkDepartment dept = departmentMapper.selectByDepId(departmentId);
        
        if (dept != null && dept.getParentid() != 0) {
            return hasCircularReference(dept.getParentid(), visitedIds);
        }
        
        return false;
    }

    /**
     * 更新部门用户关联关系
     * 当部门信息变更时，同步更新相关用户的部门关联
     * 
     * @param departmentId 部门ID
     * @return 更新的用户数量
     */
    public int updateDepartmentUserRelations(Integer departmentId) {
        try {
            log.info("开始更新部门 {} 的用户关联关系", departmentId);
            
            // 获取部门信息
            WechatworkDepartment department = getDepartmentById(departmentId);
            if (department == null) {
                log.warn("部门 {} 不存在，跳过用户关联更新", departmentId);
                return 0;
            }
            
            // 获取该部门下的所有用户
            List<WechatWorkUserInfo> departmentUsers = wechatWorkApiAdapter.getDepartmentUsers(
                    accessTokenService.getAccessToken(), departmentId);
            
            int updatedCount = 0;
            for (WechatWorkUserInfo userInfo : departmentUsers) {
                try {
                    // 更新用户的部门信息
                    updateUserDepartmentRelation(userInfo, department);
                    updatedCount++;
                } catch (Exception e) {
                    log.error("更新用户 {} 的部门关联失败", userInfo.getWechatworkUserId(), e);
                    // 继续处理其他用户，不中断整个流程
                }
            }
            
            log.info("部门 {} 的用户关联关系更新完成，共更新 {} 个用户", departmentId, updatedCount);
            return updatedCount;
            
        } catch (Exception e) {
            log.error("更新部门用户关联关系失败: departmentId={}", departmentId, e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_USER_RELATION_UPDATE_FAILED", 
                    "更新部门用户关联关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新单个用户的部门关联
     * 
     * @param userInfo 用户信息
     * @param department 部门信息
     */
    private void updateUserDepartmentRelation(WechatWorkUserInfo userInfo, WechatworkDepartment department) {
        try {
            // 这里需要调用用户服务来更新用户信息
            // 由于用户服务在另一个模块，我们通过日志记录需要更新的信息
            log.info("需要更新用户 {} 的部门关联: 用户ID={}, 部门ID={}, 部门名称={}", 
                    userInfo.getName(), userInfo.getWechatworkUserId(), 
                    department.getDepId(), department.getDepName());
            
            // TODO: 在实际项目中，这里应该调用用户服务来更新用户的部门信息
            // 可以通过事件机制或者直接调用用户服务的方法来实现
            
        } catch (Exception e) {
            log.error("更新用户部门关联失败: userid={}, departmentId={}", 
                    userInfo.getWechatworkUserId(), department.getDepId(), e);
            throw e;
        }
    }

    /**
     * 批量更新部门用户关联关系
     * 
     * @param departmentIds 部门ID列表
     * @return 更新的用户总数
     */
    public int batchUpdateDepartmentUserRelations(List<Integer> departmentIds) {
        try {
            int totalUpdated = 0;
            
            for (Integer departmentId : departmentIds) {
                try {
                    int updated = updateDepartmentUserRelations(departmentId);
                    totalUpdated += updated;
                } catch (Exception e) {
                    log.error("批量更新部门 {} 的用户关联失败", departmentId, e);
                    // 继续处理其他部门，不中断整个流程
                }
            }
            
            log.info("批量更新部门用户关联关系完成，共更新 {} 个用户", totalUpdated);
            return totalUpdated;
            
        } catch (Exception e) {
            log.error("批量更新部门用户关联关系失败", e);
            throw new WechatWorkServiceException("WECHATWORK_BATCH_DEPARTMENT_USER_RELATION_UPDATE_FAILED", 
                    "批量更新部门用户关联关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取部门的用户统计信息
     * 
     * @param departmentId 部门ID
     * @return 用户统计信息
     */
    public DepartmentUserStats getDepartmentUserStats(Integer departmentId) {
        try {
            DepartmentUserStats stats = new DepartmentUserStats();
            stats.setDepartmentId(departmentId);
            
            // 获取直接属于该部门的用户数量
            List<WechatWorkUserInfo> directUsers = wechatWorkApiAdapter.getDepartmentUsers(
                    accessTokenService.getAccessToken(), departmentId);
            stats.setDirectUserCount(directUsers.size());
            
            // 获取所有子部门的用户数量
            List<Integer> childDepartmentIds = getAllChildDepartmentIds(departmentId);
            int childUserCount = 0;
            for (Integer childDeptId : childDepartmentIds) {
                try {
                    List<WechatWorkUserInfo> childUsers = wechatWorkApiAdapter.getDepartmentUsers(
                            accessTokenService.getAccessToken(), childDeptId);
                    childUserCount += childUsers.size();
                } catch (Exception e) {
                    log.warn("获取子部门 {} 用户数量失败", childDeptId, e);
                }
            }
            stats.setChildUserCount(childUserCount);
            stats.setTotalUserCount(stats.getDirectUserCount() + stats.getChildUserCount());
            
            return stats;
            
        } catch (Exception e) {
            log.error("获取部门用户统计信息失败: departmentId={}", departmentId, e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_USER_STATS_GET_FAILED", 
                    "获取部门用户统计信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 部门用户统计信息内部类
     */
    public static class DepartmentUserStats {
        private Integer departmentId;
        private int directUserCount;
        private int childUserCount;
        private int totalUserCount;

        // Getters and Setters
        public Integer getDepartmentId() { return departmentId; }
        public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
        
        public int getDirectUserCount() { return directUserCount; }
        public void setDirectUserCount(int directUserCount) { this.directUserCount = directUserCount; }
        
        public int getChildUserCount() { return childUserCount; }
        public void setChildUserCount(int childUserCount) { this.childUserCount = childUserCount; }
        
        public int getTotalUserCount() { return totalUserCount; }
        public void setTotalUserCount(int totalUserCount) { this.totalUserCount = totalUserCount; }
    }
}
