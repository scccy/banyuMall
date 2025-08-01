# BanyuMall 微服务项目

## 项目简介

BanyuMall 是一个基于 Spring Boot 3.x 和 Spring Cloud 2023.x 的微服务架构项目，采用现代化的技术栈和最佳实践。

## 技术栈

- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Alibaba**: 2022.0.0.0
- **Java**: 21
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Nacos**: 2.2.0+
- **Log4j2**: 2.20.0
- **FastJSON2**: 2.0.40
- **MyBatis Plus**: 3.5.3.1
- **Knife4j**: 4.3.0

## 项目架构

### 模块结构

```
banyuMall/
├── service/                          # 微服务模块
│   ├── service-common/              # 通用组件模块
│   │   ├── common/                  # 通用工具类和组件
│   │   ├── config/                  # 通用配置类
│   │   └── exception/               # 全局异常处理
│   ├── service-base/                # Spring基础依赖模块
│   │   └── (仅包含依赖管理)
│   ├── service-auth/                # 认证服务
│   ├── service-user/                # 用户服务
│   └── service-gateway/             # API网关服务
├── core/                            # 核心业务模块
│   └── core-publisher/              # 核心发布者服务
├── infra/                           # 基础设施模块
│   ├── database/                    # 数据库相关
│   ├── docker/                      # Docker配置
│   ├── k8s/                         # Kubernetes配置
│   └── jenkins/                     # CI/CD配置
└── proto/                           # 协议文件
```

### 模块职责

#### service-common (通用组件模块)
- **职责**: 提供业务无关的通用工具类和组件
- **包含**: ResultData、BusinessException、JWT工具、日志配置等
- **依赖**: 所有业务服务都依赖此模块

#### service-base (Spring基础依赖模块)
- **职责**: 提供Spring Boot基础配置和依赖管理
- **包含**: 依赖版本管理、排除配置等
- **依赖**: 作为基础依赖被其他模块继承

#### service-auth (认证服务)
- **职责**: 用户认证、授权和JWT令牌管理
- **端口**: 8081
- **依赖**: service-common

#### service-user (用户服务)
- **职责**: 用户管理和用户配置功能
- **端口**: 8082
- **依赖**: service-common

#### core-publisher (核心发布者服务)
- **职责**: 任务发布、审核、参与等核心业务流程管理
- **端口**: 8084
- **依赖**: service-common, service-base
- **功能**: 任务管理、审核流程、社群分享审核

#### service-gateway (API网关服务)
- **职责**: 路由、限流、熔断等网关功能
- **端口**: 8080
- **依赖**: service-common (排除Servlet Web依赖)
- **特点**: 使用WebFlux响应式编程

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.2.0+

### 启动步骤

1. **启动基础设施**
   ```bash
   # 启动MySQL和Redis
   docker-compose -f infra/docker/docker-compose.dev.yml up -d
   
   # 启动Nacos
   # 访问 http://localhost:8848/nacos
   # 用户名/密码: nacos/nacos
   ```

2. **初始化数据库**
   ```bash
   # 执行数据库初始化脚本
   mysql -u root -p < infra/database/data/user-init-data.sql
   ```

3. **启动服务**
   ```bash
   # 启动认证服务
   cd service/service-auth
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   
   # 启动用户服务
   cd service/service-user
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   
   # 启动网关服务
   cd service/service-gateway
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

### 服务访问

- **网关服务**: http://localhost:8080
- **认证服务**: http://localhost:8081
- **用户服务**: http://localhost:8082
- **API文档**: http://localhost:8080/swagger-ui.html

## 配置管理

### 配置文件结构

每个服务都采用分层配置结构：

```
src/main/resources/
├── application.yml                  # 主启动配置
├── dev/
│   ├── application.yml             # 开发环境启动配置
│   └── service-xxx.yaml           # Nacos远程配置
├── test/
│   ├── application.yml             # 测试环境启动配置
│   └── service-xxx.yaml           # Nacos远程配置
└── prod/
    ├── application.yml             # 生产环境启动配置
    └── service-xxx.yaml           # Nacos远程配置
```

### 环境变量

支持丰富的环境变量配置，包括：
- Nacos连接配置
- 数据库连接配置
- Redis连接配置
- JWT配置
- 日志配置

## 模块文档

### 核心模块 (core/)
- **[core-publisher](infra/moudleDocs/core-publisher/)** - 发布者服务模块
  - [模块主体讨论](infra/moudleDocs/core-publisher/模块主体讨论.md)
  - [模块设计](infra/moudleDocs/core-publisher/模块设计.md)
  - [API接口说明](infra/moudleDocs/core-publisher/API接口说明.md)
  - [API接口测试](infra/moudleDocs/core-publisher/api-test.md)
  - [模块迭代说明](infra/moudleDocs/core-publisher/模块迭代说明.md)
  - [模块迭代设计](infra/moudleDocs/core-publisher/模块迭代设计.md)

### 服务模块 (service/)
- **[service-user](infra/moudleDocs/service-user/)** - 用户服务模块
  - [模块主体讨论](infra/moudleDocs/service-user/模块主体讨论.md)
  - [模块设计](infra/moudleDocs/service-user/模块设计.md)
  - [API接口说明](infra/moudleDocs/service-user/API接口说明.md)
  - [API接口测试](infra/moudleDocs/service-user/api-test.md)
  - [模块迭代说明](infra/moudleDocs/service-user/模块迭代说明.md)
  - [模块迭代设计](infra/moudleDocs/service-user/模块迭代设计.md)

- **[service-auth](infra/moudleDocs/service-auth/)** - 认证服务模块
  - [模块主体讨论](infra/moudleDocs/service-auth/模块主体讨论.md)
  - [模块设计](infra/moudleDocs/service-auth/模块设计.md)
  - [API接口说明](infra/moudleDocs/service-auth/API接口说明.md)
  - [API接口测试](infra/moudleDocs/service-auth/api-test.md)
  - [模块迭代说明](infra/moudleDocs/service-auth/模块迭代说明.md)
  - [模块迭代设计](infra/moudleDocs/service-auth/模块迭代设计.md)

- **[service-gateway](infra/moudleDocs/service-gateway/)** - 网关服务模块
  - [模块主体讨论](infra/moudleDocs/service-gateway/模块主体讨论.md)
  - [模块设计](infra/moudleDocs/service-gateway/模块设计.md)
  - [API接口说明](infra/moudleDocs/service-gateway/API接口说明.md)
  - [API接口测试](infra/moudleDocs/service-gateway/api-test.md)
  - [模块迭代说明](infra/moudleDocs/service-gateway/模块迭代说明.md)
  - [模块迭代设计](infra/moudleDocs/service-gateway/模块迭代设计.md)

- **[service-common](infra/moudleDocs/service-common/)** - 通用服务模块
  - [模块主体讨论](infra/moudleDocs/service-common/模块主体讨论.md)
  - [模块设计](infra/moudleDocs/service-common/模块设计.md)
  - [API接口说明](infra/moudleDocs/service-common/API接口说明.md)
  - [API接口测试](infra/moudleDocs/service-common/api-test.md)
  - [模块迭代说明](infra/moudleDocs/service-common/模块迭代说明.md)
  - [模块迭代设计](infra/moudleDocs/service-common/模块迭代设计.md)

- **[service-base](infra/moudleDocs/service-base/)** - 基础服务模块
  - [模块主体讨论](infra/moudleDocs/service-base/模块主体讨论.md)
  - [模块设计](infra/moudleDocs/service-base/模块设计.md)
  - [API接口说明](infra/moudleDocs/service-base/API接口说明.md)
  - [API接口测试](infra/moudleDocs/service-base/api-test.md)
  - [模块迭代说明](infra/moudleDocs/service-base/模块迭代说明.md)
  - [模块迭代设计](infra/moudleDocs/service-base/模块迭代设计.md)

### 第三方模块 (third-party/)
- **[tp-oss](infra/moudleDocs/third-party-oss/)** - 第三方OSS服务模块
  - [模块主体讨论](infra/moudleDocs/third-party-oss/模块主体讨论.md)
  - [模块设计](infra/moudleDocs/third-party-oss/模块设计.md)
  - [API接口说明](infra/moudleDocs/third-party-oss/API接口说明.md)
  - [API接口测试](infra/moudleDocs/third-party-oss/api-test.md)
  - [模块迭代说明](infra/moudleDocs/third-party-oss/模块迭代说明.md)
  - [模块迭代设计](infra/moudleDocs/third-party-oss/模块迭代设计.md)

### 项目文档
- [微服务总体设计框架](infra/moudleDocs/微服务总体设计框架.md)
- [标准化模块文档结构模板](infra/moudleDocs/标准化模块文档结构模板.md)
- [数据库建表语句和表说明](infra/moudleDocs/数据库建表语句和表说明.md)
- [新建模块工作流程](infra/moudleDocs/新建模块工作流程.md)

## API 文档

详细的API接口文档请参考：[API.md](API.md)

## 接口规范

接口规范和开发规则请参考：[.docs/RULES/API-DOC-001.md](.docs/RULES/API-DOC-001.md)

## 开发规范

### 代码规范

- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 统一返回格式

### 日志规范

- 使用Log4j2作为日志框架
- 统一的日志格式和级别
- 支持异步日志输出
- 支持日志文件轮转

### 配置规范

- 敏感信息通过环境变量配置
- 业务配置通过Nacos管理
- 启动配置本地化
- 支持配置热刷新

## 部署

### Docker部署

```bash
# 构建镜像
./infra/docker/build.sh

# 启动服务
docker-compose -f infra/docker/docker-compose.prod.yml up -d
```

### Kubernetes部署

```bash
# 部署到K8s
kubectl apply -f infra/k8s/prod/
```

## 监控

### 健康检查

- 服务健康检查: `/actuator/health`
- 应用信息: `/actuator/info`
- 指标监控: `/actuator/metrics`

### 日志监控

- 日志文件位置: `logs/`
- 支持日志聚合和分析
- 支持日志告警

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 创建 Pull Request

## 许可证

[MIT License](LICENSE)

## 联系方式

- 项目维护者: origin
- 邮箱: [your-email@example.com]
- 项目地址: [https://github.com/your-username/banyuMall]

## 更新日志

### v0.0.1-SNAPSHOT (2025-08-01)

#### 文档标准化
- 按照LR-003规则更新所有模块文档
- 统一接口路径规范（/service/user, /core/publisher, /third-party/oss等）
- 完善接口功能列表，添加Feign客户端标识
- 更新数据模型设计指向infra/database/data/目录
- 创建缺失的API接口说明文档
- 标准化文档命名和结构
- 修改相关控制器代码以符合新的接口路径规范

#### 新增功能
- 完成微服务架构重构
- 实现service-common和service-base模块分离
- 优化Gateway服务WebFlux环境配置
- 完善配置管理和环境变量支持

#### 技术改进
- 升级到Spring Boot 3.2.0
- 升级到Spring Cloud 2023.0.0
- 升级到Java 21
- 优化依赖管理和冲突解决

#### 架构优化
- 实现清晰的模块职责分离
- 优化服务间依赖关系
- 完善配置分层管理
- 提升系统可维护性