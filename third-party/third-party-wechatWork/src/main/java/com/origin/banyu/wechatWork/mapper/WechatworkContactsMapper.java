package com.origin.banyu.wechatWork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.banyu.wechatWork.entity.WechatworkContacts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业微信联系人Mapper接口
 * 
 * @author scccy
 */
@Mapper
public interface WechatworkContactsMapper extends BaseMapper<WechatworkContacts> {

    /**
     * 根据企业微信用户ID查询联系人信息
     * 
     * @param userid 企业微信用户ID
     * @return 联系人信息
     */
    WechatworkContacts selectByUserid(@Param("userid") String userid);

    /**
     * 根据部门ID查询联系人列表
     * 
     * @param departmentId 部门ID
     * @return 联系人列表
     */
    List<WechatworkContacts> selectByDepartmentId(@Param("departmentId") Integer departmentId);

    /**
     * 查询所有联系人信息
     * 
     * @return 联系人列表
     */
    List<WechatworkContacts> selectAllContacts();

    /**
     * 根据姓名模糊查询
     * 
     * @param name 姓名
     * @return 联系人列表
     */
    List<WechatworkContacts> selectByNameLike(@Param("name") String name);

    /**
     * 批量插入联系人信息
     * 
     * @param contacts 联系人列表
     * @return 插入数量
     */
    int batchInsert(@Param("contacts") List<WechatworkContacts> contacts);

    /**
     * 批量更新联系人信息
     * 
     * @param contacts 联系人列表
     * @return 更新数量
     */
    int batchUpdate(@Param("contacts") List<WechatworkContacts> contacts);
}
