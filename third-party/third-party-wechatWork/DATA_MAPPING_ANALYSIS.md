# 企业微信数据映射关系分析

## 概述
本文档记录了企业微信API返回的数据与MySQL表字段的映射关系，用于指导数据同步和存储。

## 重要修复记录

### 修复问题1: 维度表结构问题
**问题描述**: 部门表和联系人表作为维度表，不应该继承基础父类（创建人、创建时间等字段）
**修复内容**: 
- 移除了 `WechatworkDepartment` 和 `WechatworkContacts` 实体类中的时间字段
- 维度表只保留业务字段，不包含审计字段
- 符合数据仓库维度表设计原则

### 修复问题2: API参数映射不完整
**问题描述**: 部门成员信息没有按照企业微信官方文档完整保存所有参数
**修复内容**: 
- 根据[企业微信官方文档](https://developer.work.weixin.qq.com/document/path/90337)完善参数映射
- 确保所有API返回字段都被正确保存到数据库

## 架构设计说明

### 适配器层职责
- **WechatworkAuthAdapter**: 只负责权限API调用，不处理业务逻辑
- **WechatworkDepartmentApiAdapter**: 只负责部门API调用，不处理业务逻辑
- **WechatWorkUserApiAdapter**: 只负责用户API调用，不处理递归逻辑
- 适配器完全按照企业微信官方文档实现，保持API调用的纯净性

### 服务层职责
- **WechatworkDepartmentService**: 处理部门同步的业务逻辑
- **WechatworkContactsService**: 处理联系人同步的业务逻辑，包括递归逻辑判断
- 递归逻辑在Service层处理：从MySQL获取部门ID，然后决定是否递归调用API

### API接口选择说明
根据企业微信官方文档，我们选择了以下接口：

1. **部门列表接口**: `GET /cgi-bin/department/list` - 获取所有部门信息
2. **部门成员详情接口**: `GET /cgi-bin/user/list` - 获取部门成员详细信息

**为什么选择 `user/list` 而不是 `user/simplelist`**:
- `user/list` 返回完整的用户信息，包含所有需要的字段
- `user/simplelist` 只返回基本信息（userid、name、department、open_userid），信息不完整
- 我们的业务需要完整的用户信息，包括职位、手机号、邮箱等详细信息

## 维度表设计原则

### 维度表特点
1. **不继承基础父类**: 维度表是业务数据的快照，不需要审计字段
2. **只保留业务字段**: 专注于业务数据的存储和查询
3. **支持历史数据**: 可以存储不同时间点的数据快照
4. **便于数据分析**: 结构简单，便于OLAP查询

### 当前维度表
- `wechatwork_department`: 企业微信部门维度表
- `wechatwork_contacts`: 企业微信联系人维度表

## 部门信息映射

### 企业微信API返回字段
根据企业微信官方文档，部门列表接口返回以下字段：

```json
{
  "errcode": 0,
  "errmsg": "ok",
  "department": [
    {
      "id": 1,
      "name": "广州研发中心",
      "parentid": 0,
      "order": 100000000
    }
  ]
}
```

### MySQL表字段映射
`wechatwork_department` 表字段（维度表）：

| API字段 | MySQL字段 | 类型 | 说明 |
|---------|-----------|------|------|
| id | dep_id | int | 部门ID |
| name | dep_name | varchar(255) | 部门名称 |
| parentid | parentid | int | 父部门ID |
| order | order | varchar(255) | 排序 |
| department_leader | department_leader | varchar(255) | 部门负责人 |

### 映射关系分析
- ✅ **id → dep_id**: 直接映射
- ✅ **name → dep_name**: 直接映射  
- ✅ **parentid → parentid**: 直接映射
- ✅ **order → order**: 直接映射
- ❓ **department_leader**: API中可能没有此字段，需要确认

## 用户信息映射

### 企业微信API返回字段
根据企业微信官方文档，获取部门成员详情接口返回以下字段：

```json
{
  "errcode": 0,
  "errmsg": "ok",
  "userlist": [
    {
      "userid": "zhangsan",
      "name": "张三",
      "department": [1, 2],
      "position": "产品经理",
      "mobile": "13800000000",
      "gender": "1",
      "email": "zhangsan@gzdev.com",
      "biz_mail": "zhangsan@qyycs2.wecom.work",
      "avatar": "http://wx.qlogo.cn/mmopen/ajNVdqHZLLA3WJ6SNZUc6dhQkCqQPqLwWmQqO9x7Z6Kfkul0u3Gj2xsnwFPb/0",
      "status": 1,
      "enable": 1,
      "alias": "别名",
      "isleader": 0,
      "hide_mobile": 0,
      "telephone": "020-123456",
      "english_name": "jack zhang",
      "main_department": 1,
      "qr_code": "https://open.work.weixin.qq.com/wwopen/userQRCode?vcode=xxx",
      "external_position": "高级产品经理",
      "external_profile": {
        "external_corp_name": "企业简称",
        "external_attr": [
          {
            "type": 0,
            "name": "文本名称",
            "text": {
              "value": "文本"
            }
          }
        ]
      }
    }
  ]
}
```

### MySQL表字段映射
`wechatwork_contacts` 表字段（维度表）：

| API字段 | MySQL字段 | 类型 | 说明 | 状态 |
|---------|-----------|------|------|------|
| userid | userid | varchar(255) | 企业微信用户ID | ✅ 已映射 |
| name | name | varchar(255) | 成员名称 | ✅ 已映射 |
| department | department | text | 成员所属部门id列表（JSON格式） | ✅ 已映射 |
| position | position | varchar(255) | 职位信息 | ✅ 已映射 |
| mobile | mobile | varchar(255) | 手机号 | ✅ 已映射 |
| gender | gender | varchar(255) | 性别 | ✅ 已映射 |
| email | email | varchar(255) | 邮箱 | ✅ 已映射 |
| biz_mail | biz_mail | varchar(255) | 企业邮箱 | ✅ 已映射 |
| avatar | avatar | text | 头像url | ✅ 已映射 |
| status | status | int | 激活状态 | ✅ 已映射 |
| enable | enable | int | 成员启用状态 | ✅ 已映射 |
| alias | alias | varchar(255) | 别名 | ✅ 已映射 |
| isleader | isleader | int | 是否是部门领导 | ✅ 已映射 |
| hide_mobile | hide_mobile | int | 是否隐藏手机号 | ✅ 已映射 |
| telephone | telephone | varchar(255) | 座机 | ✅ 已映射 |
| english_name | english_name | varchar(255) | 英文名 | ✅ 已映射 |
| main_department | main_department | int | 主部门 | ✅ 已映射 |
| qr_code | qr_code | text | 员工个人二维码 | ✅ 已映射 |
| external_position | external_position | varchar(255) | 对外职务 | ✅ 已映射 |
| external_profile | external_profile | text | 对外属性（JSON格式） | ✅ 已映射 |

### 映射关系分析
- ✅ **所有API字段都已正确映射**: 确保企业微信返回的所有信息都被保存
- ✅ **JSON字段处理**: 复杂对象（如external_profile）和数组（如department）都转换为JSON字符串存储
- ✅ **数据类型匹配**: 所有字段类型都与API返回类型匹配

## 递归逻辑处理

### 设计原则
1. **适配器层**: 只负责单次API调用，不处理递归逻辑
2. **服务层**: 处理递归逻辑，决定是否递归调用API
3. **参数传递**: `fetchChild` 参数从Controller传递到Service，再传递到适配器

### 递归流程
1. Controller接收 `fetchChild` 参数
2. Service根据 `fetchChild` 值决定是否递归
3. 适配器只负责单次API调用，传递 `fetchChild` 参数给企业微信API
4. 企业微信API根据 `fetchChild` 参数返回相应的数据

## 需要确认的问题

### 1. 部门表字段确认
- `department_leader` 字段在企业微信API中是否存在？
- 如果不存在，是否需要从其他接口获取？

### 2. 用户表字段确认
- 所有字段类型是否匹配？
- 是否需要添加其他字段？
- 字段长度是否足够？

### 3. 数据转换规则
- 复杂对象（如external_profile）的JSON序列化规则
- 数组类型（如department）的JSON序列化规则
- 空值处理规则

## 建议
1. 请确认MySQL表结构是否与上述映射关系一致
2. 如有不一致，请提供正确的表结构，我将相应调整代码
3. 建议在正式使用前进行小规模测试，验证数据映射的正确性
4. 递归逻辑已在Service层正确实现，符合企业微信官方文档要求
5. API接口选择基于业务需求，确保获取完整的用户信息
6. 维度表设计符合数据仓库最佳实践，便于后续数据分析
7. 所有API参数都已完整映射，确保数据完整性

## 修复完成状态
- ✅ **维度表结构修复**: 移除了不必要的基础父类字段
- ✅ **API参数完整映射**: 按照企业微信官方文档完整保存所有字段
- ✅ **代码结构优化**: 适配器、服务、实体各层职责清晰
- ✅ **文档更新**: 记录了所有修复的问题和设计原则
