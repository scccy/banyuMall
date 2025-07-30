# 用户服务架构基线文档

## 概述
service-user模块负责用户信息管理，与service-auth共享用户基础数据，专注于用户扩展信息和配置管理。

## 架构设计

### 数据共享策略
- **共享表**: `sys_user` - 用户基础信息表，由service-auth和service-user共同使用
- **专用表**: 
  - `user_profile` - 用户扩展信息表
  - `user_config` - 用户配置表

### 职责分工
- **service-auth**: 负责认证、授权、密码管理、登录时间等
- **service-user**: 负责用户扩展信息、配置管理、用户状态管理等

## 核心组件

### 实体类
- `UserProfile` - 用户扩展信息实体，继承BaseEntity
- `UserConfig` - 用户配置实体，继承BaseEntity

### 数据访问层
- `UserProfileMapper` - 用户扩展信息数据访问
- `UserConfigMapper` - 用户配置数据访问

### 业务逻辑层
- `UserProfileService` - 用户扩展信息服务
- `UserConfigService` - 用户配置服务

### 控制器层
- `UserProfileController` - 用户扩展信息API
- `UserConfigController` - 用户配置API

## API接口设计

### 用户扩展信息接口
- `GET /user/profile/{userId}` - 获取用户扩展信息
- `POST /user/profile/{userId}` - 创建或更新用户扩展信息

### 用户配置接口
- `GET /user/config/{userId}` - 获取用户所有配置
- `GET /user/config/{userId}/map` - 获取用户配置Map
- `GET /user/config/{userId}/{configKey}` - 获取指定配置
- `POST /user/config/{userId}/{configKey}` - 设置用户配置
- `POST /user/config/{userId}/batch` - 批量设置用户配置
- `DELETE /user/config/{userId}/{configKey}` - 删除用户配置

## 配置管理

### 环境配置
- `application.yml` - 主配置文件
- `application-dev.yml` - 开发环境配置
- `application-prod.yml` - 生产环境配置
- `nacos-config-template-user.yml` - Nacos配置模板

### 自定义配置
```yaml
user:
  config:
    default-type: string
    max-config-count: 100
  profile:
    allow-real-name-edit: true
    allow-company-edit: true
```

## 数据库设计

### 用户扩展信息表 (user_profile)
```sql
CREATE TABLE user_profile (
  id varchar(32) NOT NULL COMMENT 'ID',
  user_id varchar(32) NOT NULL COMMENT '用户ID',
  real_name varchar(50) DEFAULT NULL COMMENT '真实姓名',
  company_name varchar(100) DEFAULT NULL COMMENT '公司名称',
  company_address varchar(255) DEFAULT NULL COMMENT '公司地址',
  contact_person varchar(50) DEFAULT NULL COMMENT '联系人',
  contact_phone varchar(20) DEFAULT NULL COMMENT '联系电话',
  industry varchar(50) DEFAULT NULL COMMENT '所属行业',
  description text DEFAULT NULL COMMENT '个人简介',
  create_by varchar(32) DEFAULT NULL COMMENT '创建人ID',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by varchar(32) DEFAULT NULL COMMENT '更新人ID',
  update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_id (user_id),
  KEY idx_company_name (company_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户扩展信息表';
```

### 用户配置表 (user_config)
```sql
CREATE TABLE user_config (
  id varchar(32) NOT NULL COMMENT 'ID',
  user_id varchar(32) NOT NULL COMMENT '用户ID',
  config_key varchar(50) NOT NULL COMMENT '配置键',
  config_value text DEFAULT NULL COMMENT '配置值',
  config_type varchar(20) DEFAULT 'string' COMMENT '配置类型：string,number,boolean,json',
  create_by varchar(32) DEFAULT NULL COMMENT '创建人ID',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by varchar(32) DEFAULT NULL COMMENT '更新人ID',
  update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_key (user_id, config_key),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户配置表';
```

## 技术栈
- Spring Boot 2.7.x
- Spring Cloud Alibaba
- MyBatis-Plus
- MySQL
- Redis
- Nacos (服务注册与配置中心)
- Swagger (API文档)

## 部署信息
- 服务端口: 8082
- 上下文路径: /user
- 服务名称: service-user 