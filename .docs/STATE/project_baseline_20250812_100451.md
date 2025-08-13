# banyuMall 项目状态基线

**文档ID**: project_baseline_20250812_100451  
**创建时间**: 2025-08-12 10:04:51  
**基线版本**: v1.0.0  
**状态**: 初始化完成

## 项目架构状态

### 整体架构
```
banyuMall/
├── core/                    # 核心业务模块
│   └── core-publisher/     # 发布者核心业务
├── service/                # 服务层模块
│   ├── service-auth/       # 认证服务
│   ├── service-base/       # 基础服务
│   ├── service-common/     # 通用服务
│   ├── service-gateway/    # 网关服务
│   └── service-user/       # 用户服务
├── third-party/            # 第三方集成模块
│   ├── third-party-aliyunOss/    # 阿里云OSS集成
│   └── third-party-wechatWork/   # 企业微信集成
└── docs/                   # 项目文档
```

### 技术栈基线
- **框架**: Spring Boot
- **构建工具**: Maven
- **数据库**: MySQL (推测)
- **缓存**: 待确认
- **消息队列**: 待确认
- **注册中心**: 待确认

## 模块状态基线

### Core模块
- **core-publisher**: 
  - 状态: 已实现
  - 主要功能: 发布者业务逻辑
  - 包含: Controller、Service、Entity、DTO、Mapper

### Service模块
- **service-auth**: 
  - 状态: 已实现
  - 主要功能: 用户认证和授权
  - 包含: 认证配置、第三方配置

- **service-user**: 
  - 状态: 已实现
  - 主要功能: 用户管理
  - 包含: 用户信息管理、用户档案

- **service-gateway**: 
  - 状态: 已实现
  - 主要功能: API网关
  - 包含: 路由配置、过滤器、异常处理

- **service-base**: 
  - 状态: 基础框架
  - 主要功能: 基础组件

- **service-common**: 
  - 状态: 通用组件
  - 主要功能: 通用工具和服务

### Third-party模块
- **third-party-wechatWork**: 
  - 状态: 已实现
  - 主要功能: 企业微信集成
  - 包含: 用户管理、朋友圈管理、二维码认证

- **third-party-aliyunOss**: 
  - 状态: 已实现
  - 主要功能: 阿里云对象存储
  - 包含: 文件上传、日志记录

## 数据库状态基线

### 已知表结构
- **用户相关**: SysUser, UserProfile
- **企业微信**: ExternalUser, WechatWorkMoments, WechatWorkMomentInteractions
- **发布者**: PublisherShareReview, PublisherTaskCompletion, PublisherTaskDetail
- **OSS**: OssUploadLog
- **第三方配置**: ThirdPartyConfig

## 配置状态基线

### 环境配置
- **开发环境**: dev/application.yml
- **测试环境**: test/application.yml  
- **生产环境**: prod/application.yml

### 日志配置
- **框架**: Log4j2
- **配置文件**: log4j2.xml

## 开发状态基线

### 代码质量
- **命名规范**: camelCase
- **包结构**: com.origin.banyu.[module]
- **分层架构**: Controller -> Service -> Mapper

### 测试状态
- **单元测试**: 部分模块已实现
- **集成测试**: 待完善
- **测试覆盖率**: 待评估

## 部署状态基线

### 构建配置
- **主POM**: 根目录pom.xml
- **模块POM**: 各模块独立pom.xml
- **构建脚本**: mvnw, mvnw.cmd

### 部署方式
- **打包**: Maven JAR包
- **运行**: Spring Boot内嵌容器
- **配置**: 环境变量 + 配置文件

## 基线变更记录
- 2025-08-12 10:04:51: 初始基线创建

---
*此基线文档记录项目的当前状态，任何重大变更都需要更新此文档。*
