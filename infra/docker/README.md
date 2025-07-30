# Docker容器化部署

## 概述

本目录包含BanyuMall项目的Docker容器化部署相关文件，支持本地开发、测试和生产环境的容器化部署。

## 目录结构

```
infra/docker/
├── build.sh                    # Docker镜像构建脚本
├── push.sh                     # Docker镜像推送脚本
├── docker-compose.dev.yml      # 开发环境Docker Compose配置
├── docker-compose.test.yml     # 测试环境Docker Compose配置
├── docker-compose.prod.yml     # 生产环境Docker Compose配置
├── templates/                  # Docker模板文件
│   ├── Dockerfile.template     # Dockerfile模板
│   └── dockerignore.template   # .dockerignore模板
└── README.md                   # 本文件
```

## 快速开始

### 1. 构建镜像

```bash
# 构建单个服务
./infra/docker/build.sh service-auth

# 构建指定环境
./infra/docker/build.sh service-auth dev

# 构建指定版本
./infra/docker/build.sh service-auth prod v1.0.0
```

### 2. 推送镜像

```bash
# 推送镜像到仓库
./infra/docker/push.sh service-auth v1.0.0
```

### 3. 本地开发启动

```bash
# 启动开发环境
docker-compose -f infra/docker/docker-compose.dev.yml up

# 后台启动
docker-compose -f infra/docker/docker-compose.dev.yml up -d

# 启动单个服务
docker-compose -f infra/docker/docker-compose.dev.yml up service-auth
```

## 支持的服务

- **service-auth**: 认证授权服务 (端口: 8081)
- **service-user**: 用户管理服务 (端口: 8082)
- **service-gateway**: API网关服务 (端口: 8080)
- **service-base**: 基础服务模块

## 环境配置

### 开发环境 (dev)
- 包含完整的开发环境依赖
- 支持热重载和调试
- 使用本地数据卷

### 测试环境 (test)
- 模拟生产环境配置
- 使用测试数据
- 支持自动化测试

### 生产环境 (prod)
- 生产级别配置
- 高可用性设置
- 安全加固配置

## 环境变量

### 数据库配置
```bash
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=banyumall
MYSQL_USER=banyumall
MYSQL_PASSWORD=banyumall123
```

### Nacos配置
```bash
NACOS_USERNAME=nacos
NACOS_PASSWORD=nacos
NACOS_AUTH_TOKEN=SecretKey012345678901234567890123456789012345678901234567890123456789
```

### MinIO配置
```bash
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin123
```

## 健康检查

所有服务都配置了健康检查端点：
- **路径**: `/actuator/health`
- **检查间隔**: 30秒
- **超时时间**: 3秒
- **重试次数**: 3次

## 网络配置

- **网络名称**: `banyumall-network`
- **网络类型**: bridge
- **服务间通信**: 通过服务名访问

## 数据卷

- **mysql_data**: MySQL数据持久化
- **redis_data**: Redis数据持久化
- **nacos_data**: Nacos数据持久化
- **nacos_logs**: Nacos日志持久化
- **minio_data**: MinIO数据持久化

## 故障排除

### 1. 镜像构建失败
```bash
# 检查Dockerfile是否存在
ls -la service/service-auth/Dockerfile

# 使用模板创建Dockerfile
cp infra/docker/templates/Dockerfile.template service/service-auth/Dockerfile
```

### 2. 服务启动失败
```bash
# 查看服务日志
docker-compose -f infra/docker/docker-compose.dev.yml logs service-auth

# 检查服务状态
docker-compose -f infra/docker/docker-compose.dev.yml ps
```

### 3. 网络连接问题
```bash
# 检查网络配置
docker network ls
docker network inspect banyumall-network
```

## 最佳实践

1. **镜像标签**: 使用语义化版本标签
2. **安全配置**: 使用非root用户运行容器
3. **资源限制**: 设置合理的CPU和内存限制
4. **健康检查**: 配置适当的健康检查端点
5. **日志管理**: 使用结构化日志输出

## 相关文档

- [Docker容器化部署规则](../.docs/RULES/DEV-006.md)
- [Kubernetes部署配置](../k8s/)
- [Jenkins流水线配置](../jenkins/) 