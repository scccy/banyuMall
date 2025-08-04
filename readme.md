# BanyuMall 微服务项目

## 项目简介

BanyuMall 是一个基于 Spring Boot 3.x 和 Spring Cloud 2023.x 的微服务架构项目，采用现代化的技术栈和最佳实践。项目已重构为多个独立的微服务仓库，支持独立开发、部署和维护。

## 技术栈

- **Spring Boot**: 3.2.5
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Alibaba**: 2023.0.1.0
- **Java**: 21
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Nacos**: 2.2.0+
- **Log4j2**: 2.20.0
- **FastJSON2**: 2.0.57
- **MyBatis Plus**: 3.5.12
- **Knife4j**: 4.4.0

## 微服务架构

### 独立仓库结构

项目已重构为多个独立的微服务仓库，每个微服务都有自己独立的Git仓库：

#### 基础服务模块

| 微服务名称 | 仓库地址 | 描述 | 依赖关系 |
|-----------|---------|------|----------|
| service-common | http://117.50.221.113:8077/banyu/service-common | 通用工具模块 | 无依赖 |
| service-base | http://117.50.221.113:8077/banyu/service-base | 基础配置模块 | 依赖service-common |

#### 业务服务模块

| 微服务名称 | 仓库地址 | 描述 | 依赖关系 |
|-----------|---------|------|----------|
| service-auth | http://117.50.221.113:8077/banyu/service-auth | 认证服务 | 依赖service-base, service-common |
| service-gateway | http://117.50.221.113:8077/banyu/service-gateway | 网关服务 | 依赖service-common |
| service-user | http://117.50.221.113:8077/banyu/service-user | 用户服务 | 依赖service-base, service-common |

#### 核心业务模块

| 微服务名称 | 仓库地址 | 描述 | 依赖关系 |
|-----------|---------|------|----------|
| core-publisher | http://117.50.221.113:8077/banyu/core-publisher | 发布者核心服务 | 依赖service-base, service-common |
| core-acceptor | 待开发 | 接受者核心服务 | 依赖service-base, service-common |

#### 第三方服务模块

| 微服务名称 | 仓库地址 | 描述 | 依赖关系 |
|-----------|---------|------|----------|
| third-party-aliyunOss | http://117.50.221.113:8077/banyu/third-party-aliyunOss | 阿里云OSS服务 | 依赖service-base, service-common |
| third-party-wechatwork | 待开发 | 企业微信集成服务 | 依赖service-base, service-common |
| third-party-youzan | 待开发 | 有赞商城集成服务 | 依赖service-base, service-common |

### 依赖关系图

```
service-common (基础工具)
    ↑
service-base (基础配置)
    ↑
service-auth, service-user, core-publisher, core-acceptor, third-party-aliyunOss, third-party-wechatwork, third-party-youzan
    ↑
service-gateway (仅依赖service-common)
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.2.0+

### 克隆微服务仓库

```bash
# 克隆基础服务
git clone http://117.50.221.113:8077/banyu/service-common.git
git clone http://117.50.221.113:8077/banyu/service-base.git

# 克隆业务服务
git clone http://117.50.221.113:8077/banyu/service-auth.git
git clone http://117.50.221.113:8077/banyu/service-gateway.git
git clone http://117.50.221.113:8077/banyu/service-user.git

# 克隆核心业务
git clone http://117.50.221.113:8077/banyu/core-publisher.git
# git clone http://117.50.221.113:8077/banyu/core-acceptor.git  # 待开发

# 克隆第三方服务
git clone http://117.50.221.113:8077/banyu/third-party-aliyunOss.git
# git clone http://117.50.221.113:8077/banyu/third-party-wechatwork.git  # 待开发
# git clone http://117.50.221.113:8077/banyu/third-party-youzan.git  # 待开发
```

### 启动步骤

1. **启动基础设施**
   ```bash
   # 启动MySQL和Redis
   docker-compose -f zinfra/docker/docker-compose.dev.yml up -d
   
   # 启动Nacos
   # 访问 http://localhost:8848/nacos
   # 用户名/密码: nacos/nacos
   ```

2. **初始化数据库**
   ```bash
   # 执行数据库初始化脚本
   mysql -u root -p < zinfra/database/data/user-init-data.sql
   ```

3. **按依赖顺序启动服务**
   ```bash
   # 1. 启动service-common（基础工具模块）
   cd service-common
   mvn clean install
   
   # 2. 启动service-base（基础配置模块）
   cd service-base
   mvn clean install
   
   # 3. 启动业务服务（可并行）
   cd service-auth
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   
   cd service-user
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   
   cd service-gateway
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   
   cd core-publisher
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# cd core-acceptor  # 待开发
# mvn spring-boot:run -Dspring-boot.run.profiles=dev

cd third-party-aliyunOss
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# cd third-party-wechatwork  # 待开发
# mvn spring-boot:run -Dspring-boot.run.profiles=dev

# cd third-party-youzan  # 待开发
# mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

### 服务访问

- **网关服务**: http://localhost:8080
- **认证服务**: http://localhost:8081
- **用户服务**: http://localhost:8082
- **发布者服务**: http://localhost:8084
- **接受者服务**: http://localhost:8086 (待开发)
- **阿里云OSS服务**: http://localhost:8085
- **企业微信服务**: http://localhost:8087 (待开发)
- **有赞商城服务**: http://localhost:8088 (待开发)
- **API文档**: http://localhost:8080/swagger-ui.html

## 开发指南

### 开发流程

1. **基础模块优先**: 先开发和发布service-common，再开发service-base
2. **业务模块开发**: 在基础模块发布后，开发业务微服务
3. **独立部署**: 每个微服务都可以独立编译、测试和部署
4. **版本管理**: 每个微服务独立管理版本号

### Maven依赖管理

所有微服务都配置了Maven私有仓库发布：

```xml
<distributionManagement>
    <repository>
        <id>nexus-releases</id>
        <name>Nexus Release Repository</name>
        <url>http://117.50.185.51:8881/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>nexus-snapshots</id>
        <name>Nexus Snapshot Repository</name>
        <url>http://117.50.185.51:8881/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

### 部署顺序

1. 部署service-common
2. 部署service-base
3. 部署其他业务微服务（可并行）

## 配置管理

### 配置文件结构

每个微服务都采用分层配置结构：

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
- **[core-publisher](zinfra/moudleDocs/core-publisher/)** - 发布者服务模块
  - [模块主体讨论](zinfra/moudleDocs/core-publisher/模块主体讨论.md)
  - [模块设计](zinfra/moudleDocs/core-publisher/模块设计.md)
  - [API接口测试](zinfra/moudleDocs/core-publisher/api-test.md)

### 服务模块 (service/)
- **[service-common](zinfra/moudleDocs/service-common/)** - 通用工具模块
  - [模块设计](zinfra/moudleDocs/service-common/模块设计.md)
  - [API接口测试](zinfra/moudleDocs/service-common/api-test.md)
- **[service-user](zinfra/moudleDocs/service-user/)** - 用户服务模块
  - [模块设计](zinfra/moudleDocs/service-user/模块设计.md)
  - [API接口测试](zinfra/moudleDocs/service-user/api-test.md)

- **[service-auth](zinfra/moudleDocs/service-auth/)** - 认证服务模块
  - [模块设计](zinfra/moudleDocs/service-auth/模块设计.md)
  - [API接口测试](zinfra/moudleDocs/service-auth/api-test.md)

- **[service-gateway](zinfra/moudleDocs/service-gateway/)** - 网关服务模块
  - [模块设计](zinfra/moudleDocs/service-gateway/模块设计.md)
  - [API接口测试](zinfra/moudleDocs/service-gateway/api-test.md)

### 第三方模块 (third-party/)
- **[third-party-aliyunOss](zinfra/moudleDocs/third-party-oss/)** - 第三方OSS服务模块
  - [模块设计](zinfra/moudleDocs/third-party-oss/模块设计.md)
  - [API接口测试](zinfra/moudleDocs/third-party-oss/api-test.md)

### 项目文档
- [微服务仓库结构](MICROSERVICE_REPOSITORIES.md) - 详细的微服务仓库说明
- [数据库建表语句和表说明](zinfra/moudleDocs/数据库建表语句和表说明.md)
- [错误码汇总](zinfra/moudleDocs/错误码汇总.md)

## API 文档

详细的API接口文档请参考：[API.md](API.md)

## 开发规范

### 代码规范

- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 统一返回格式
- 使用FastJSON2进行JSON处理

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
./zinfra/docker/build.sh

# 启动服务
docker-compose -f zinfra/docker/docker-compose.prod.yml up -d
```

### Kubernetes部署

```bash
# 部署到K8s
kubectl apply -f zinfra/k8s/prod/
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

## 注意事项

1. **依赖顺序**: 必须按照依赖关系顺序进行开发和部署
2. **版本兼容**: 确保各微服务之间的版本兼容性
3. **配置管理**: 每个微服务都有独立的配置文件
4. **日志管理**: 每个微服务独立管理日志
5. **监控告警**: 建议为每个微服务配置独立的监控和告警

## 迁移历史

- **迁移时间**: 2025-01-30
- **迁移方式**: 从单一仓库拆分为多个独立仓库
- **备份位置**: `../banyuMall-backup-20250130_1430/`
- **临时目录**: `../temp-microservices/`

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 创建 Pull Request

## 许可证

[MIT License](LICENSE)

## 联系方式

- 项目维护者: scccy
- 项目地址: [https://github.com/scccy/banyuMall]

## 更新日志

### v0.0.1-SNAPSHOT (2025-01-30)

#### 架构重构
- 完成微服务Git仓库重构
- 将单一仓库拆分为7个独立微服务仓库
- 所有微服务使用main作为默认分支
- 实现独立的版本管理和部署

#### 微服务拆分
- service-common: 通用工具模块
- service-base: 基础配置模块
- service-auth: 认证服务
- service-gateway: 网关服务
- service-user: 用户服务
- core-publisher: 发布者核心服务
- core-acceptor: 接受者核心服务 (待开发)
- third-party-aliyunOss: 阿里云OSS服务
- third-party-wechatwork: 企业微信集成服务 (待开发)
- third-party-youzan: 有赞商城集成服务 (待开发)

#### 技术改进
- 升级到Spring Boot 3.2.5
- 升级到Spring Cloud 2023.0.0
- 升级到Spring Cloud Alibaba 2023.0.1.0
- 升级到Java 21
- 优化依赖管理和冲突解决

#### 文档更新
- 创建MICROSERVICE_REPOSITORIES.md文档
- 更新README.md反映新的架构
- 完善微服务开发指南
- 添加部署顺序说明

#### 架构优化
- 实现清晰的模块职责分离
- 优化服务间依赖关系
- 完善配置分层管理
- 提升系统可维护性和可扩展性

#### 待开发模块
- core-acceptor: 接受者核心服务，负责任务接受、执行、审核等功能
- third-party-wechatwork: 企业微信集成服务，提供企业微信API封装和消息推送
- third-party-youzan: 有赞商城集成服务，提供有赞商城API集成和订单管理