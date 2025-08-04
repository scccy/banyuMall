# 技术栈基线

## 📋 项目技术栈概览

**项目名称**: BanyuMall 微服务项目  
**架构模式**: 微服务架构  
**Java版本**: 21  
**Spring版本**: 3.2.5  

## 🛠️ 核心技术栈

### 基础框架
| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 21 | JDK版本 |
| Spring Boot | 3.2.5 | 主框架 |
| Spring Cloud | 2023.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.1.0 | 阿里云微服务组件 |
| Maven | 3.8+ | 构建工具 |

### 服务治理
| 组件 | 版本 | 说明 |
|------|------|------|
| Nacos | 2.2.0+ | 服务注册与配置中心 |
| Gateway | 4.0+ | API网关 |
| OpenFeign | 4.0+ | 服务间调用 |

### 数据层
| 组件 | 版本 | 说明 |
|------|------|------|
| MySQL | 8.0+ | 主数据库 |
| MyBatis Plus | 3.5.12 | ORM框架 |
| Redis | 6.0+ | 缓存数据库 |

### 工具库
| 组件 | 版本 | 说明 |
|------|------|------|
| FastJSON2 | 2.0.57 | JSON处理库 |
| Lombok | 1.18.32 | 代码生成工具 |
| Knife4j | 4.4.0 | API文档工具 |
| Log4j2 | 2.20.0 | 日志框架 |

### 安全认证
| 组件 | 版本 | 说明 |
|------|------|------|
| JJWT | 0.12.5 | JWT令牌处理 |
| BCrypt | - | 密码加密 |

### 第三方服务
| 组件 | 版本 | 说明 |
|------|------|------|
| Aliyun OSS | 3.17.4 | 对象存储服务 |

## 📦 模块依赖关系

### 依赖层次结构
```
banyuMall (父项目)
├── service/ (服务层)
│   ├── service-base/ (基础依赖)
│   ├── service-common/ (通用组件)
│   ├── service-auth/ (认证服务)
│   ├── service-user/ (用户服务)
│   └── service-gateway/ (网关服务)
├── core/ (核心业务层)
│   └── core-publisher/ (发布者服务)
└── third-party/ (第三方服务)
    └── aliyun-oss/ (OSS服务)
```

### 依赖关系图
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

## 🔧 配置规范

### 端口分配
| 服务 | 端口 | 说明 |
|------|------|------|
| service-gateway | 8080 | API网关 |
| service-auth | 8081 | 认证服务 |
| service-user | 8082 | 用户服务 |
| core-publisher | 8084 | 发布者服务 |
| aliyun-oss | 8085 | OSS服务 |

### 环境配置
- **开发环境**: `dev/`
- **测试环境**: `test/`
- **生产环境**: `prod/`

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

## 🚀 启动顺序

### 基础设施启动
1. **MySQL** - 数据库服务
2. **Redis** - 缓存服务(非必要不启用,保留最低限度)
3. **Nacos** - 服务注册中心

### 微服务启动
1. **service-base** - 基础依赖
2. **service-common** - 通用组件
3. **service-auth** - 认证服务
4. **service-user** - 用户服务
5. **core-publisher** - 发布者服务
6. **aliyun-oss** - OSS服务
7. **service-gateway** - API网关

## 📊 性能基准

### 启动时间目标
- **开发环境**: < 3秒
- **生产环境**: < 10秒

### 内存使用目标
- **开发环境**: < 512MB
- **生产环境**: < 1GB

### 响应时间目标
- **API响应**: < 200ms
- **数据库查询**: < 50ms
- **缓存查询**: < 10ms

## 🔒 安全配置

### 认证机制
- **JWT令牌**: 基于JJWT实现
- **密码加密**: BCrypt算法
- **会话管理**: 无状态设计

### 权限控制
- **用户类型**: 1-管理员，2-发布者，3-接受者
- **API权限**: 基于用户类型和JWT令牌
- **数据权限**: 基于用户ID隔离

## 📝 开发规范

### 代码规范
- **作者**: scccy
- **语言**: 中文注释和文档
- **命名**: 驼峰命名法
- **包结构**: 按功能模块组织

### 文档规范
- **API文档**: Knife4j自动生成
- **数据库文档**: 在 `zinfra/database/` 下维护
- **模块文档**: 在 `zinfra/moudleDocs/` 下维护

### 测试规范
- **单元测试**: JUnit 5
- **集成测试**: Spring Boot Test
- **API测试**: Knife4j测试工具

---

**版本**: v1.0  
**创建日期**: 2025-08-04  
**最后更新**: 2025-08-04  
**维护者**: scccy 