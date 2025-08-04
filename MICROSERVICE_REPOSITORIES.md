# BanyuMall 微服务仓库结构

## 概述

BanyuMall项目已成功拆分为多个独立的微服务仓库，每个微服务都有自己独立的Git仓库，便于独立开发、部署和维护。

## 微服务仓库列表

### 基础服务模块

| 微服务名称 | 仓库地址 | 描述 | 依赖关系 |
|-----------|---------|------|----------|
| service-common | http://117.50.221.113:8077/banyu/service-common | 通用工具模块 | 无依赖 |
| service-base | http://117.50.221.113:8077/banyu/service-base | 基础配置模块 | 依赖service-common |

### 业务服务模块

| 微服务名称 | 仓库地址 | 描述 | 依赖关系 |
|-----------|---------|------|----------|
| service-auth | http://117.50.221.113:8077/banyu/service-auth | 认证服务 | 依赖service-base, service-common |
| service-gateway | http://117.50.221.113:8077/banyu/service-gateway | 网关服务 | 依赖service-common |
| service-user | http://117.50.221.113:8077/banyu/service-user | 用户服务 | 依赖service-base, service-common |

### 核心业务模块

| 微服务名称 | 仓库地址 | 描述 | 依赖关系 |
|-----------|---------|------|----------|
| core-publisher | http://117.50.221.113:8077/banyu/core-publisher | 发布者核心服务 | 依赖service-base, service-common |

### 第三方服务模块

| 微服务名称 | 仓库地址 | 描述 | 依赖关系 |
|-----------|---------|------|----------|
| third-party-aliyunOss | http://117.50.221.113:8077/banyu/third-party-aliyunOss | 阿里云OSS服务 | 依赖service-base, service-common |

## 依赖关系图

```
service-common (基础工具)
    ↑
service-base (基础配置)
    ↑
service-auth, service-user, core-publisher, third-party-aliyunOss
    ↑
service-gateway (仅依赖service-common)
```

## 开发指南

### 1. 克隆微服务仓库

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

# 克隆第三方服务
git clone http://117.50.221.113:8077/banyu/third-party-aliyunOss.git
```

### 2. 开发流程

1. **基础模块优先**: 先开发和发布service-common，再开发service-base
2. **业务模块开发**: 在基础模块发布后，开发业务微服务
3. **独立部署**: 每个微服务都可以独立编译、测试和部署
4. **版本管理**: 每个微服务独立管理版本号

### 3. Maven依赖管理

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

### 4. 部署顺序

1. 部署service-common
2. 部署service-base
3. 部署其他业务微服务（可并行）

## 技术栈

- **Java版本**: 21
- **Spring Boot**: 3.2.5
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Alibaba**: 2023.0.1.0
- **Maven**: 3.x
- **Git**: 分布式版本控制

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

---

**维护者**: scccy  
**最后更新**: 2025-01-30 