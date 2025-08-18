package com.origin.banyu.wechatWork.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.origin.banyu.wechatWork.adapter.WechatWorkUserApiAdapter;
import com.origin.banyu.wechatWork.dto.WechatWorkUserInfo;
import com.origin.banyu.wechatWork.entity.WechatworkContacts;
import com.origin.banyu.wechatWork.entity.WechatworkDepartment;
import com.origin.banyu.wechatWork.exception.WechatWorkServiceException;
import com.origin.banyu.wechatWork.mapper.WechatworkContactsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信联系人服务
 * 符合第三方架构特殊规则：使用适配器模式封装第三方API
 * 
 * @author scccy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WechatworkContactsService {

    private final WechatworkContactsMapper contactsMapper;
    private final WechatWorkUserApiAdapter userApiAdapter;
    private final AccessTokenService accessTokenService;
    private final WechatworkDepartmentService departmentService;

    /**
     * 同步企业微信联系人信息
     * 
     * @param depId 部门ID，为null时同步所有部门
     * @param fetchChild 是否递归获取子部门成员，0-否，1-是
     * @return 同步成功的联系人数量
     */
    public int syncWechatWorkContacts(Integer depId, Integer fetchChild) {
        try {
            log.info("开始同步企业微信联系人信息，depId={}, fetchChild={}", depId, fetchChild);
            
            String accessToken = accessTokenService.getAccessToken();
            int totalCount = 0;
            
            if (depId != null) {
                // 同步指定部门的联系人
                totalCount = syncDepartmentContacts(accessToken, depId, fetchChild != null ? fetchChild : 0);
            } else {
                // 同步所有部门的联系人
                totalCount = syncAllDepartmentContacts(accessToken, fetchChild != null ? fetchChild : 0);
            }
            
            log.info("企业微信联系人同步完成，总共同步 {} 个联系人", totalCount);
            return totalCount;
            
        } catch (Exception e) {
            log.error("同步企业微信联系人信息失败", e);
            throw new WechatWorkServiceException("WECHATWORK_CONTACTS_SYNC_FAILED", 
                    "同步企业微信联系人信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 同步所有部门的联系人
     * 
     * @param accessToken 访问令牌
     * @param fetchChild 是否递归获取子部门下面的成员：1-是，0-否
     * @return 同步的联系人数量
     */
    private int syncAllDepartmentContacts(String accessToken, Integer fetchChild) {
        try {
            log.info("开始同步所有部门的联系人信息，fetchChild={}", fetchChild);
            
            // 1. 从MySQL获取所有部门ID
            List<WechatworkDepartment> allDepartments = departmentService.getAllDepartments();
            log.info("从MySQL获取到 {} 个部门", allDepartments.size());
            
            if (allDepartments.isEmpty()) {
                log.warn("未获取到任何部门信息，请先同步部门信息");
                return 0;
            }
            
            // 2. 遍历每个部门，获取联系人信息
            int totalContacts = 0;
            for (WechatworkDepartment dept : allDepartments) {
                try {
                    int count = syncDepartmentContacts(accessToken, dept.getDepId(), fetchChild);
                    totalContacts += count;
                } catch (Exception e) {
                    log.error("同步部门 {} 的联系人失败", dept.getDepId(), e);
                    // 继续处理其他部门，不中断整个流程
                }
            }
            
            log.info("所有部门联系人同步完成，共同步 {} 个联系人", totalContacts);
            return totalContacts;
            
        } catch (Exception e) {
            log.error("同步所有部门联系人失败", e);
            throw new WechatWorkServiceException("WECHATWORK_ALL_DEPARTMENT_CONTACTS_SYNC_FAILED", 
                    "同步所有部门联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 同步指定部门的联系人信息
     * 
     * @param accessToken 访问令牌
     * @param depId 部门ID
     * @param fetchChild 是否递归获取子部门成员
     * @return 同步成功的联系人数量
     */
    private int syncDepartmentContacts(String accessToken, Integer depId, Integer fetchChild) {
        try {
            log.info("开始同步部门 {} 的联系人信息，fetchChild={}", depId, fetchChild);
            
            // 1. 从企业微信API获取部门成员信息
            List<WechatWorkUserInfo> users = userApiAdapter.getDepartmentUsers(accessToken, depId, fetchChild);
            log.info("从企业微信API获取到部门 {} 的 {} 个成员", depId, users.size());
            
            if (users.isEmpty()) {
                log.info("部门 {} 没有成员", depId);
                return 0;
            }
            
            // 2. 批量保存到MySQL
            int savedCount = batchSaveContacts(users);
            
            log.info("部门 {} 的联系人同步完成，成功保存 {} 个联系人", depId, savedCount);
            return savedCount;
            
        } catch (Exception e) {
            log.error("同步部门 {} 的联系人失败", depId, e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_CONTACTS_SYNC_FAILED", 
                    "同步部门联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量保存联系人信息到MySQL
     * 
     * @param users 用户信息列表
     * @return 保存成功的联系人数量
     */
    private int batchSaveContacts(List<WechatWorkUserInfo> users) {
        try {
            log.info("开始批量保存 {} 个联系人到MySQL", users.size());
            
            // 1. 清空现有联系人数据（全量同步模式）
            int deletedCount = contactsMapper.delete(null);
            log.info("清空现有联系人数据，删除 {} 条记录", deletedCount);
            
            // 2. 批量插入新数据
            List<WechatworkContacts> entityList = new ArrayList<>();
            for (WechatWorkUserInfo userInfo : users) {
                // 使用实体的静态构造方法进行数据转换
                WechatworkContacts entity = convertToEntity(userInfo);
                entityList.add(entity);
            }
            
            // 3. 直接批量插入所有数据
            int insertedCount = 0;
            if (!entityList.isEmpty()) {
                for (WechatworkContacts contact : entityList) {
                    try {
                        contactsMapper.insert(contact);
                        insertedCount++;
                    } catch (Exception e) {
                        log.error("插入联系人失败: contactId={}, name={}", contact.getContactId(), contact.getName(), e);
                        // 继续处理其他联系人，不中断整个流程
                    }
                }
                
                log.info("批量插入完成，成功插入 {}/{} 个联系人", insertedCount, entityList.size());
            }
            
            return insertedCount;
            
        } catch (Exception e) {
            log.error("批量保存联系人信息失败", e);
            throw new WechatWorkServiceException("WECHATWORK_CONTACTS_BATCH_SAVE_FAILED", 
                    "批量保存联系人信息失败: " + e.getMessage());
        }
    }

    /**
     * 将DTO转换为实体对象
     * 
     * @param userInfo 用户信息DTO
     * @return 联系人实体对象
     */
    private WechatworkContacts convertToEntity(WechatWorkUserInfo userInfo) {
        // 将DTO转换为JSONObject，然后使用实体的静态构造方法
        JSONObject apiResponse = new JSONObject();
        apiResponse.put("userid", userInfo.getUserid());
        apiResponse.put("name", userInfo.getName());
        apiResponse.put("department", userInfo.getDepIds());
        apiResponse.put("position", userInfo.getPosition());
        apiResponse.put("mobile", userInfo.getMobile());
        apiResponse.put("gender", userInfo.getGender());
        apiResponse.put("email", userInfo.getEmail());
        apiResponse.put("biz_mail", userInfo.getBizMail());
        apiResponse.put("avatar", userInfo.getAvatar());
        apiResponse.put("status", userInfo.getStatus());
        apiResponse.put("enable", userInfo.getEnable());
        apiResponse.put("alias", userInfo.getAlias());
        apiResponse.put("isleader", userInfo.getIsleader());
        apiResponse.put("hide_mobile", userInfo.getHideMobile());
        apiResponse.put("telephone", userInfo.getTelephone());
        apiResponse.put("english_name", userInfo.getEnglishName());
        apiResponse.put("main_department", userInfo.getMainDepartment());
        apiResponse.put("qr_code", userInfo.getQrCode());
        apiResponse.put("external_position", userInfo.getExternalPosition());
        apiResponse.put("external_profile", userInfo.getExternalProfile());
        apiResponse.put("open_userid", userInfo.getOpenUserid());
        
        return WechatworkContacts.fromApiResponse(apiResponse);
    }

    /**
     * 根据企业微信用户ID获取联系人信息
     * 
     * @param contactId 企业微信用户ID
     * @return 联系人信息
     */
    public WechatworkContacts getContactByContactId(String contactId) {
        try {
            WechatworkContacts contact = contactsMapper.selectByContactId(contactId);
            if (contact == null) {
                throw new WechatWorkServiceException("WECHATWORK_CONTACT_NOT_FOUND", 
                        "联系人不存在: " + contactId);
            }
            return contact;
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取联系人信息失败: contactId={}", contactId, e);
            throw new WechatWorkServiceException("WECHATWORK_CONTACT_GET_FAILED", 
                    "获取联系人信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取所有联系人信息
     * 
     * @return 联系人列表
     */
    public List<WechatworkContacts> getAllContacts() {
        try {
            return contactsMapper.selectAllContacts();
        } catch (Exception e) {
            log.error("获取所有联系人信息失败", e);
            throw new WechatWorkServiceException("WECHATWORK_CONTACTS_GET_FAILED", 
                    "获取所有联系人信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据部门ID获取联系人列表
     * 
     * @param depId 部门ID
     * @return 联系人列表
     */
    public List<WechatworkContacts> getContactsByDepId(Integer depId) {
        try {
            return contactsMapper.selectByDepId(depId);
        } catch (Exception e) {
            log.error("获取部门联系人列表失败: depId={}", depId, e);
            throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_CONTACTS_GET_FAILED", 
                    "获取部门联系人列表失败: " + e.getMessage(), e);
        }
    }
}
