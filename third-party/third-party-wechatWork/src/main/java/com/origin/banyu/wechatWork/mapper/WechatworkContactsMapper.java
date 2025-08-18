package com.origin.banyu.wechatWork.mapper;

import com.origin.banyu.wechatWork.entity.WechatworkContacts;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
     * @param contactId 企业微信用户ID
     * @return 联系人信息
     */
    WechatworkContacts selectByContactId(@Param("contactId") String contactId);

    /**
     * 根据部门ID查询联系人列表
     * 
     * @param depId 部门ID
     * @return 联系人列表
     */
    List<WechatworkContacts> selectByDepId(@Param("depId") Integer depId);

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
