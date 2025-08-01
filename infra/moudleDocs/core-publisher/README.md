# core-publisher 核心发布者模块

## 模块概述

core-publisher 模块是 banyuMall 系统的核心发布者模块，负责管理任务发布、审核、参与等核心业务流程。

## 主要功能

- **任务发布管理**: 创建、更新、删除、查询任务
- **任务审核流程**: 任务审核、审核历史管理
- **社群分享审核**: 社群分享内容的审核管理
- **任务参与管理**: 用户参与任务、完成任务、参与记录查询

## 技术栈

- **框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus
- **缓存**: Redis
- **文档**: Knife4j (OpenAPI 3.0)
- **认证**: JWT
- **服务发现**: Nacos

## 模块结构

```
core-publisher/
├── src/main/java/com/origin/publisher/
│   ├── controller/                   # 控制器层
│   │   ├── PublisherTaskController.java
│   │   └── PublisherShareReviewController.java
│   ├── service/                      # 服务层
│   │   ├── PublisherTaskService.java
│   │   ├── PublisherShareReviewService.java
│   │   └── impl/                     # 服务实现
│   │       ├── PublisherTaskServiceImpl.java
│   │       └── PublisherShareReviewServiceImpl.java
│   ├── mapper/                       # 数据访问层
│   │   ├── PublisherTaskMapper.java
│   │   ├── PublisherLikeTaskMapper.java
│   │   ├── PublisherCommentTaskMapper.java
│   │   ├── PublisherShareTaskMapper.java
│   │   ├── PublisherDiscussTaskMapper.java
│   │   ├── PublisherInviteTaskMapper.java
│   │   ├── PublisherFeedbackTaskMapper.java
│   │   ├── PublisherRankingTaskMapper.java
│   │   ├── PublisherTaskReviewMapper.java
│   │   └── PublisherShareReviewMapper.java
│   ├── entity/                       # 实体类
│   │   ├── PublisherTask.java
│   │   ├── PublisherLikeTask.java
│   │   ├── PublisherCommentTask.java
│   │   ├── PublisherShareTask.java
│   │   ├── PublisherDiscussTask.java
│   │   ├── PublisherInviteTask.java
│   │   ├── PublisherFeedbackTask.java
│   │   ├── PublisherRankingTask.java
│   │   ├── PublisherTaskReview.java
│   │   └── PublisherShareReview.java
│   ├── dto/                          # 数据传输对象
│   │   ├── TaskCreateRequest.java
│   │   ├── TaskQueryRequest.java
│   │   ├── TaskReviewRequest.java
│   │   └── ShareReviewQueryRequest.java
│   ├── config/                       # 配置类
│   │   └── PublisherConfig.java
│   └── ServiceCorePublisherApplication.java  # 启动类
├── src/main/resources/
│   ├── application.yml               # 主配置文件
│   ├── log4j2.xml                    # 日志配置
│   ├── dev/                          # 开发环境配置
│   ├── prod/                         # 生产环境配置
│   └── test/                         # 测试环境配置
└── pom.xml                           # Maven配置
```

## 数据库设计

### 核心表结构

#### 任务相关表
- **publisher_task**: 任务基础表 - 存储所有任务的基本信息
- **publisher_like_task**: 点赞任务表 - 存储点赞任务特有信息
- **publisher_comment_task**: 评论任务表 - 存储评论任务特有信息
- **publisher_discuss_task**: 讨论任务表 - 存储讨论任务特有信息
- **publisher_share_task**: 社群分享任务表 - 存储社群分享任务特有信息
- **publisher_invite_task**: 邀请任务表 - 存储邀请任务特有信息
- **publisher_feedback_task**: 反馈任务表 - 存储反馈任务特有信息
- **publisher_ranking_task**: 排行榜任务表 - 存储排行榜任务特有信息

#### 审核相关表
- **publisher_task_review**: 任务审核记录表 - 存储任务审核历史
- **publisher_share_review**: 社群分享审核表 - 存储社群分享任务的具体审核信息

### 数据库脚本

数据库脚本位置: `infra/database/data/publisher/publisher-schema.sql`

## 接口设计

### 任务管理接口

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 |
|------|----------|----------|----------|----------|
| 1 | 创建任务 | POST | `/core-publisher/tasks` | 创建新的任务 |
| 2 | 更新任务 | PUT | `/core-publisher/tasks/{id}` | 更新任务信息 |
| 3 | 删除任务 | DELETE | `/core-publisher/tasks/{id}` | 删除任务 |
| 4 | 获取任务详情 | GET | `/core-publisher/tasks/{id}` | 获取任务详细信息 |
| 5 | 分页查询任务列表 | GET | `/core-publisher/tasks` | 分页查询任务列表 |
| 6 | 提交任务审核 | POST | `/core-publisher/tasks/{id}/submit` | 提交任务进行审核 |
| 7 | 审核任务 | POST | `/core-publisher/tasks/{id}/review` | 审核任务 |
| 8 | 获取审核历史 | GET | `/core-publisher/tasks/{id}/review-history` | 获取任务审核历史 |

### 社群分享审核接口

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 |
|------|----------|----------|----------|----------|
| 9 | 提交社群分享审核 | POST | `/core-publisher/share-reviews` | 提交社群分享审核申请 |
| 10 | 审核社群分享 | PUT | `/core-publisher/share-reviews/{id}/review` | 审核社群分享申请 |
| 11 | 查询社群分享审核列表 | GET | `/core-publisher/share-reviews` | 查询社群分享审核列表 |

## 配置说明

### 应用配置

- **端口**: 8084
- **服务名**: core-publisher
- **配置文件**: application.yml, dev/application.yml, prod/application.yml, test/application.yml

### 环境配置

#### 开发环境 (dev)
- 数据库: banyu_mall
- Redis: localhost:6379
- 日志级别: DEBUG
- Knife4j: 启用

#### 生产环境 (prod)
- 数据库: 环境变量配置
- Redis: 环境变量配置
- 日志级别: INFO
- Knife4j: 禁用

#### 测试环境 (test)
- 数据库: banyu_mall_test
- Redis: localhost:6379 (database: 1)
- 日志级别: DEBUG
- Knife4j: 启用

## 部署说明

### 本地开发

1. **启动依赖服务**
   ```bash
   # 启动MySQL和Redis
   docker-compose -f infra/docker/docker-compose.dev.yml up -d
   
   # 启动Nacos
   # 访问 http://localhost:8848/nacos
   ```

2. **初始化数据库**
   ```bash
   # 执行数据库脚本
   mysql -u root -p < infra/database/data/publisher/publisher-schema.sql
   ```

3. **启动服务**
   ```bash
   # 在项目根目录执行
   mvn spring-boot:run -pl core/core-publisher
   ```

### Docker部署

1. **构建镜像**
   ```bash
   mvn clean package -pl core/core-publisher
   docker build -t banyumall/core-publisher:latest core/core-publisher/
   ```

2. **运行容器**
   ```bash
   docker run -d --name core-publisher \
     -p 8084:8084 \
     -e SPRING_PROFILES_ACTIVE=prod \
     -e DB_HOST=mysql \
     -e DB_USERNAME=root \
     -e DB_PASSWORD=password \
     -e REDIS_HOST=redis \
     banyumall/core-publisher:latest
   ```

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 统一返回格式

### 数据库规范
- 使用MyBatis-Plus进行数据访问
- 表名使用下划线命名法
- 字段名使用下划线命名法
- 主键使用雪花算法生成

### 接口规范
- 使用RESTful API设计
- 统一使用ApiResponse包装返回结果
- 参数校验使用@Valid注解
- 接口文档使用Knife4j生成

## 监控与运维

### 健康检查
- 端点: `/actuator/health`
- 数据库连接检查
- Redis连接检查

### 日志管理
- 日志文件: `logs/core-publisher.log`
- 日志轮转: 按日期和大小
- 日志级别: 可配置

### 性能监控
- 接口响应时间
- 数据库连接池状态
- JVM内存使用情况

## 版本历史

### v1.0.0 (2025-08-01)
- 基础任务管理功能
- 任务审核流程
- 社群分享审核
- 任务参与管理

## 联系方式

- **作者**: scccy
- **项目地址**: https://github.com/scccy/banyuMall
- **文档地址**: infra/moudleDocs/core-publisher/ 