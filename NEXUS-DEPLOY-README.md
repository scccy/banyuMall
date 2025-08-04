# banyuMall项目 - Nexus私有仓库部署指南

## 概述

本文档说明如何将banyuMall项目的各个微服务模块发布到Nexus私有仓库。

## Nexus仓库信息

- **外网地址**: http://117.50.185.51:8881/
- **内网地址**: http://10.60.202.128:8881/
- **用户名**: admin
- **密码**: 6GrSxO3w

## 已配置的模块

### 根模块
- `com.origin:banyuMall:0.0.1-SNAPSHOT` - 项目根模块

### Service模块
- `com.origin:service:0.0.1-SNAPSHOT` - 服务聚合模块
- `com.origin:service-common:0.0.1-SNAPSHOT` - 通用工具模块
- `com.origin:service-base:0.0.1-SNAPSHOT` - 基础配置模块
- `com.origin:service-auth:0.0.1-SNAPSHOT` - 认证服务
- `com.origin:service-user:0.0.1-SNAPSHOT` - 用户服务
- `com.origin:service-gateway:0.0.1-SNAPSHOT` - 网关服务

### Core模块
- `com.origin:core:0.0.1-SNAPSHOT` - 核心业务聚合模块
- `com.origin:core-publisher:0.0.1-SNAPSHOT` - 发布者核心服务

### Third-Party模块
- `com.origin:third-party:0.0.1-SNAPSHOT` - 第三方服务聚合模块
- `com.origin:aliyun-oss:0.0.1-SNAPSHOT` - 阿里云OSS服务

## 配置文件说明

### Maven配置文件
已修改 `/Volumes/soft/maven/conf/settings.xml`，包含：
- Nexus仓库认证信息（admin/6GrSxO3w）
- 仓库地址配置
- 阿里云镜像配置

### 各模块pom.xml
每个模块的pom.xml文件都已添加：
- `distributionManagement` - 发布仓库配置
- `maven-deploy-plugin` - 发布插件配置
- `maven-source-plugin` - 源码打包插件
- `maven-javadoc-plugin` - Javadoc打包插件

## 部署方式

### 方式一：使用部署脚本（推荐）

```bash
# 给脚本添加执行权限（如果还没有）
chmod +x deploy-to-nexus.sh

# 执行部署脚本
./deploy-to-nexus.sh
```

### 方式二：手动执行Maven命令

```bash
# 直接使用Maven命令（无需指定settings文件）
mvn clean deploy -DskipTests
```

## 验证部署

1. 访问 http://117.50.185.51:8881/
2. 使用admin/6GrSxO3w登录
3. 在"Browse"中查看已发布的构件

## 在其他项目中引用

```xml
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## 注意事项

- 当前所有模块都使用 `0.0.1-SNAPSHOT` 版本
- Maven配置文件已更新，无需额外配置
- 确保网络能够访问Nexus仓库地址

## 联系方式

如有问题，请联系项目维护者：scccy 