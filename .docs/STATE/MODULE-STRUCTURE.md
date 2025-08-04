# 模块结构基线

## 📋 项目模块概览

**项目名称**: BanyuMall 微服务项目  
**架构模式**: 微服务架构  
**模块数量**: 8个核心模块  
**创建时间**: 2025-08-04  

## 🏗️ 模块层次结构

```
banyuMall (父项目)
├── service/ (服务层)
│   ├── service-base/ (基础依赖模块)
│   ├── service-common/ (通用组件模块)
│   ├── service-auth/ (认证服务)
│   ├── service-user/ (用户服务)
│   └── service-gateway/ (API网关服务)
├── core/ (核心业务层)
│   └── core-publisher/ (发布者服务)
└── third-party/ (第三方服务层)
    └── aliyun-oss/ (阿里云OSS服务)
```

## 📦 模块详细说明

### 1. service-base (基础依赖模块)
**端口**: 无（仅依赖管理）  
**职责**: Spring Boot基础配置和依赖管理  
**依赖**: 无  
**被依赖**: 所有其他模块  

#### 核心功能
- Spring Boot基础配置
- 依赖版本管理
- 自动配置排除
- 基础Bean定义

#### 配置类
- `DataSourceConfig` - 数据源配置
- `MyBatisPlusConfig` - MyBatis Plus配置
- `Knife4jConfig` - API文档配置
- 其他Spring基础配置

### 2. service-common (通用组件模块)
**端口**: 无（仅组件库）  
**职责**: 业务无关的通用工具类和组件  
**依赖**: service-base  
**被依赖**: 所有业务服务  

#### 核心组件
- `ResultData` - 统一响应格式
- `BusinessException` - 业务异常类
- `JwtUtil` - JWT工具类
- `Log4j2` - 日志配置
- 通用枚举和常量

#### 工具类
- JSON处理工具
- 日期时间工具
- 字符串处理工具
- 加密解密工具

### 3. service-auth (认证服务)
**端口**: 8081  
**职责**: 用户认证、授权和JWT令牌管理  
**依赖**: service-common  
**被依赖**: 其他服务（通过JWT令牌）  

#### 核心功能
- 用户登录认证
- JWT令牌生成和验证
- 用户权限验证
- 密码加密和验证

#### 主要接口
- `/auth/login` - 用户登录
- `/auth/verify` - 令牌验证
- `/auth/refresh` - 令牌刷新

### 4. service-user (用户服务)
**端口**: 8082  
**职责**: 用户管理和用户配置功能  
**依赖**: service-common  
**被依赖**: 其他业务服务  

#### 核心功能
- 用户信息管理
- 用户配置管理
- 用户头像上传
- 用户状态管理

#### 主要接口
- `/service/user/profile` - 用户信息管理
- `/service/user/avatar` - 头像管理
- `/service/user/config` - 用户配置

### 5. service-gateway (API网关服务)
**端口**: 8080  
**职责**: 路由、限流、熔断等网关功能  
**依赖**: service-common（排除Servlet Web依赖）  
**被依赖**: 无（对外入口）  

#### 核心功能
- API路由转发
- 请求限流控制
- 服务熔断降级
- 请求日志记录
- 跨域处理

#### 技术特点
- 使用WebFlux响应式编程
- 支持动态路由配置
- 集成Nacos服务发现

### 6. core-publisher (发布者服务)
**端口**: 8084  
**职责**: 任务发布、审核、参与等核心业务流程管理  
**依赖**: service-common, service-base  
**被依赖**: 无  

#### 核心功能
- 任务发布管理
- 任务审核流程
- 任务完成记录
- 社群分享审核
- 积分奖励管理

#### 主要接口
- `/service/publisher/task` - 任务管理
- `/service/publisher/review` - 审核管理
- `/service/publisher/completion` - 完成记录

### 7. aliyun-oss (阿里云OSS服务)
**端口**: 8085  
**职责**: 文件存储和管理  
**依赖**: service-common  
**被依赖**: 其他服务（通过Feign调用）  

#### 核心功能
- 文件上传下载
- 文件访问控制
- 文件元数据管理
- 文件访问日志

#### 主要接口
- `/service/oss/upload` - 文件上传
- `/service/oss/download` - 文件下载
- `/service/oss/delete` - 文件删除

## 🔗 模块依赖关系

### 依赖层次图
```
service-gateway
    ↓ (依赖)
service-common
    ↓ (依赖)
service-base

service-auth
    ↓ (依赖)
service-common

service-user
    ↓ (依赖)
service-common

core-publisher
    ↓ (依赖)
service-common, service-base

aliyun-oss
    ↓ (依赖)
service-common
```

### 服务调用关系
```
外部请求 → service-gateway
    ↓
service-auth (认证)
    ↓
service-user (用户服务)
    ↓
core-publisher (核心业务)
    ↓
aliyun-oss (文件服务)
```

## 📊 模块状态统计

### 模块完成度
| 模块 | 状态 | 完成度 | 说明 |
|------|------|--------|------|
| service-base | ✅ 完成 | 100% | 基础配置完成 |
| service-common | ✅ 完成 | 100% | 通用组件完成 |
| service-auth | ✅ 完成 | 100% | 认证服务完成 |
| service-user | ✅ 完成 | 100% | 用户服务完成 |
| service-gateway | ✅ 完成 | 100% | 网关服务完成 |
| core-publisher | ✅ 完成 | 100% | 发布者服务完成 |
| aliyun-oss | ✅ 完成 | 100% | OSS服务完成 |

### 端口分配
| 服务 | 端口 | 状态 | 说明 |
|------|------|------|------|
| service-gateway | 8080 | ✅ 已分配 | API网关 |
| service-auth | 8081 | ✅ 已分配 | 认证服务 |
| service-user | 8082 | ✅ 已分配 | 用户服务 |
| core-publisher | 8084 | ✅ 已分配 | 发布者服务 |
| aliyun-oss | 8085 | ✅ 已分配 | OSS服务 |

## 🚀 模块启动顺序

### 基础设施启动
1. **MySQL** - 数据库服务
2. **Redis** - 缓存服务(非必要不启用,保留最低限度)
3. **Nacos** - 服务注册中心

### 微服务启动顺序
1. **service-base** - 基础依赖（无端口）
2. **service-common** - 通用组件（无端口）
3. **service-auth** - 认证服务 (8081)
4. **service-user** - 用户服务 (8082)
5. **core-publisher** - 发布者服务 (8084)
6. **aliyun-oss** - OSS服务 (8085)
7. **service-gateway** - API网关 (8080)

## 📝 模块文档组织

### 文档结构
```
zinfra/moudleDocs/
├── service-auth/
│   ├── 模块设计.md
│   ├── 模块迭代设计.md
│   └── 模块迭代说明.md
├── service-user/
│   ├── 模块设计.md
│   ├── 模块迭代设计.md
│   └── 模块迭代说明.md
├── service-gateway/
│   ├── 模块设计.md
│   ├── 模块迭代设计.md
│   └── 模块迭代说明.md
├── core-publisher/
│   ├── 模块设计.md
│   ├── 模块迭代设计.md
│   └── 模块迭代说明.md
└── third-party-oss/
    ├── 模块设计.md
    ├── 模块迭代设计.md
    └── 模块迭代说明.md
```

### 文档规范
- 每个模块都有独立的设计文档
- 包含模块迭代设计和说明
- 注明是否使用Feign进行服务调用
- 包含API接口测试文档

## 🔧 模块配置规范

### 配置文件结构
```
src/main/resources/
├── dev/
│   └── application.yml
├── test/
│   └── application.yml
├── prod/
│   └── application.yml
└── mapper/
    └── *.xml
```

### 环境配置
- **开发环境**: `dev/` - 本地开发配置
- **测试环境**: `test/` - 测试环境配置
- **生产环境**: `prod/` - 生产环境配置

### 配置管理
- 配置类统一在 `service-base` 模块
- 环境特定配置在对应环境目录
- 敏感信息使用环境变量或配置中心

## 📈 模块扩展计划

### 待开发模块
- **service-order** - 订单服务
- **service-payment** - 支付服务
- **service-notification** - 通知服务

### 模块拆分原则
- 按业务领域划分
- 保持模块独立性
- 遵循单一职责原则
- 考虑团队组织架构

---

**版本**: v1.0  
**创建日期**: 2025-08-04  
**最后更新**: 2025-08-04  
**维护者**: scccy 