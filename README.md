# banyuMall

## 项目简介
banyuMall 是一个基于 Spring Boot 的微服务电商平台，采用分布式架构设计，提供完整的电商解决方案。

## 🚀 快速开始

### 一键下载
现在您可以通过一个简单的命令获取完整的项目：

```bash
git clone git@117.50.221.113:originproject/banyumall.git
```

或者使用 HTTPS：
```bash
git clone https://117.50.221.113:8077/originproject/banyumall.git
```

### 项目结构
```
banyuMall/
├── core/                          # 核心业务模块
│   └── core-publisher/            # 发布者服务
├── service/                       # 基础服务模块
│   ├── service-auth/              # 认证服务
│   ├── service-base/              # 基础服务
│   ├── service-common/            # 公共组件
│   ├── service-gateway/           # 网关服务
│   └── service-user/              # 用户服务
├── third-party/                   # 第三方集成模块
│   ├── third-party-aliyunOss/     # 阿里云OSS服务
│   └── third-party-wechatWork/    # 企业微信服务
├── docs/                          # 项目文档
├── scripts/                       # 工具脚本
└── pom.xml                        # 主项目配置
```

### 开发环境要求
- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+

### 启动步骤
1. 克隆项目
2. 配置数据库连接
3. 运行 `mvn clean install`
4. 启动各个微服务

## 📋 微服务模块

### Core 模块
- **core-publisher**: 发布者服务，负责内容发布和管理

### Service 模块
- **service-auth**: 认证服务，处理用户登录和权限验证
- **service-base**: 基础服务，提供通用功能组件
- **service-common**: 公共组件，包含共享的工具类和配置
- **service-gateway**: 网关服务，统一入口和路由管理
- **service-user**: 用户服务，用户信息管理

### Third-party 模块
- **third-party-aliyunOss**: 阿里云OSS服务，文件存储管理
- **third-party-wechatWork**: 企业微信服务，第三方集成

## 🔧 技术栈

- **后端框架**: Spring Boot 2.x
- **微服务**: Spring Cloud
- **数据库**: MySQL + MyBatis Plus
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **注册中心**: Nacos
- **网关**: Spring Cloud Gateway
- **负载均衡**: Spring Cloud LoadBalancer

## 📚 文档

- 项目文档: `./docs/`
- API 文档: 启动服务后访问 Swagger UI
- 部署文档: 参考各模块的 README.md

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。

## 📄 许可证

本项目采用 MIT 许可证。

## 🔗 相关链接

- 项目主页: http://117.50.221.113:8077/originproject/banyumall
- 问题反馈: http://117.50.221.113:8077/originproject/banyumall/-/issues
- 合并请求: http://117.50.221.113:8077/originproject/banyumall/-/merge_requests
