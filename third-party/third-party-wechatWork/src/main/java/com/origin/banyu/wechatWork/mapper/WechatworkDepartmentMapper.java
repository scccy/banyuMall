package com.origin.banyu.wechatWork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.banyu.wechatWork.entity.WechatworkDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业微信部门Mapper接口
 * 
 * @author scccy
 */
@Mapper
public interface WechatworkDepartmentMapper extends BaseMapper<WechatworkDepartment> {

    /**
     * 根据部门ID查询部门信息
     * 
     * @param depId 部门ID
     * @return 部门信息
     */
    WechatworkDepartment selectByDepId(@Param("depId") Integer depId);

    /**
     * 根据父部门ID查询子部门列表
     * 
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    List<WechatworkDepartment> selectByParentId(@Param("parentId") Integer parentId);

    /**
     * 查询所有部门信息
     * 
     * @return 部门列表
     */
    List<WechatworkDepartment> selectAllDepartments();

    /**
     * 根据部门名称模糊查询
     * 
     * @param depName 部门名称
     * @return 部门列表
     */
    List<WechatworkDepartment> selectByDepNameLike(@Param("depName") String depName);

    /**
     * 查询部门层级路径
     * 
     * @param depId 部门ID
     * @return 层级路径字符串
     */
    String selectDepartmentPath(@Param("depId") Integer depId);

    /**
     * 批量插入部门信息
     * 
     * @param departments 部门列表
     * @return 插入数量
     */
    int batchInsert(@Param("departments") List<WechatworkDepartment> departments);

    /**
     * 批量更新部门信息
     * 
     * @param departments 部门列表
     * @return 更新数量
     */
    int batchUpdate(@Param("departments") List<WechatworkDepartment> departments);
}