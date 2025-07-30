# 用户服务数据流向总结

## 📊 数据表关系

### 核心表结构
```
sys_user (用户基础表 - 与auth共享)
├── id (主键)
├── username, password, nickname, avatar, email, phone
├── gender, birthday, status, user_type
├── last_login_time
└── 公共字段 (create_by, create_time, update_by, update_time, is_deleted)

user_profile (用户扩展信息表)
├── id (主键)
├── user_id (外键 -> sys_user.id)
├── real_name, company_name, company_address
├── contact_person, contact_phone, industry, description
└── 公共字段

user_config (用户配置表)
├── id (主键)
├── user_id (外键 -> sys_user.id)
├── config_key, config_value, config_type
└── 公共字段
```

### 表关系说明
- **sys_user** ↔ **user_profile**: 1:1 关系（一个用户对应一个扩展信息）
- **sys_user** ↔ **user_config**: 1:N 关系（一个用户对应多个配置项）

## 🔄 Java类映射关系

### 实体类继承关系
```
BaseEntity (公共字段父类)
├── createBy, createTime, updateBy, updateTime, isDeleted
├── SysUser (用户基础信息)
├── UserProfile (用户扩展信息)
└── UserConfig (用户配置)
```

### 分层架构映射
```
Controller层 (API接口)
├── UserProfileController
│   ├── getUserProfile(userId, token)
│   └── createOrUpdateProfile(userId, request, token)
└── UserConfigController
    ├── getUserConfigs(userId)
    ├── getUserConfigMap(userId)
    ├── setUserConfig(userId, configKey, configValue)
    └── deleteUserConfig(userId, configKey)

Service层 (业务逻辑)
├── UserProfileService
│   ├── getByUserId(userId)
│   └── createOrUpdateProfile(userId, profile)
└── UserConfigService
    ├── getByUserId(userId)
    ├── getConfigMap(userId)
    ├── setConfig(userId, configKey, configValue)
    └── setConfigs(userId, configMap)

Feign客户端层 (服务间调用)
├── AuthFeignClient
│   ├── getUserInfo(userId, token)
│   ├── checkUserExists(userId, token)
│   └── getUserStatus(userId, token)
└── AuthFeignClientFallback
    ├── 服务降级处理
    └── 错误日志记录

Mapper层 (数据访问)
├── UserProfileMapper
│   ├── selectByUserId(userId)
│   ├── insert(profile)
│   └── updateById(profile)
└── UserConfigMapper
    ├── selectByUserId(userId)
    ├── selectByUserIdAndKey(userId, configKey)
    ├── insert(config)
    └── updateById(config)
```

## 🚀 数据流向图

### 查询用户扩展信息流程
```
客户端 → UserProfileController.getUserProfile()
    ↓
UserProfileService.getByUserId()
    ↓
UserProfileMapper.selectByUserId()
    ↓
数据库查询 user_profile 表
    ↓
返回 UserProfile 对象
    ↓
ResultData 包装 → 客户端
```

### 更新用户扩展信息流程
```
客户端 → UserProfileController.createOrUpdateProfile()
    ↓
获取认证token
    ↓
UserProfileService.createOrUpdateProfile()
    ↓
AuthFeignClient.checkUserExists() → 验证用户存在性
    ↓
检查 user_profile 是否存在
    ↓
存在? → 更新 (UPDATE)
不存在? → 创建 (INSERT)
    ↓
数据库操作
    ↓
返回操作结果 → 客户端
```

### 用户配置管理流程
```
客户端 → UserConfigController
    ↓
UserConfigService
    ↓
UserConfigMapper
    ↓
数据库操作 (user_config 表)
    ↓
返回结果 → 客户端
```

## 📋 关键业务流程

### 1. 用户扩展信息管理
- **查询**: 根据userId获取用户的扩展信息（公司信息、联系方式等）
- **创建/更新**: 支持创建新的扩展信息或更新现有信息

### 2. 用户配置管理
- **查询**: 获取用户的所有配置项或指定配置项
- **设置**: 支持单个配置设置和批量配置设置
- **删除**: 删除指定的配置项

### 3. 数据一致性保证
- **事务管理**: 使用@Transactional确保数据一致性
- **逻辑删除**: 使用isDeleted字段进行软删除
- **公共字段**: 自动填充创建人、更新人、时间戳等

## 🎯 核心特点

1. **数据共享**: 与service-auth共享sys_user表，避免数据重复
2. **职责分离**: service-user专注于扩展信息和配置管理
3. **统一架构**: 所有实体类继承BaseEntity，统一公共字段管理
4. **灵活配置**: 支持键值对形式的用户配置存储
5. **RESTful API**: 提供标准的REST接口
6. **异常处理**: 统一的异常处理和错误响应 