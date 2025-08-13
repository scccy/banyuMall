# Third-Party-Wechatwork 企业微信第三方服务模块

## 模块简介

Third-Party-Wechatwork 是 BanyuMall 项目中的企业微信第三方服务模块，提供企业微信用户授权绑定、联系人管理、群聊管理、朋友圈互动数据获取等功能。

## 技术栈

- Spring Boot 2.x
- Spring Cloud OpenFeign
- MyBatis Plus
- MySQL 8.0
- Redis
- Hutool
- FastJSON
- ZXing (二维码生成)

## 主要功能

### 1. 用户授权绑定
- 生成企业微信授权二维码
- 处理授权回调
- 绑定企业微信用户到系统用户
- 获取用户授权状态

### 2. 用户信息管理
- 同步企业微信用户信息
- 获取用户详情
- 按部门查询用户

### 3. 客户联系人管理
- 获取用户的客户列表
- 获取客户详情
- 同步客户信息
- 更新客户备注

### 4. 群聊成员管理
- 获取群成员列表
- 同步群成员信息
- 获取群聊信息

### 5. 朋友圈互动数据
- 获取朋友圈动态列表
- 获取朋友圈互动数据
- 同步朋友圈数据
- 生成统计报告

## 数据库设计

### 核心数据表

1. **wechatwork_users** - 企业微信用户信息表
2. **external_users** - 企业微信外部用户信息表
3. **wechatwork_contacts** - 客户联系人关系表
4. **wechatwork_group_members** - 群聊成员关系表
5. **wechatwork_moments** - 朋友圈动态表
6. **wechatwork_moment_interactions** - 朋友圈互动表

## API 接口

### 授权相关接口

- `GET /tp/wechatWork/auth/qrcode` - 获取授权二维码
- `GET /tp/wechatWork/auth/callback` - 处理授权回调
- `POST /tp/wechatWork/auth/bind` - 绑定企业微信用户
- `POST /tp/wechatWork/auth/unbind` - 解绑企业微信用户
- `GET /tp/wechatWork/auth/status/{userId}` - 获取用户授权状态

### 用户管理接口

- `POST /tp/wechatWork/user/sync` - 同步企业微信用户信息
- `GET /tp/wechatWork/user/{userid}` - 获取用户详情

## 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/third_party
    username: root
    password: 123456
```

### Redis配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
```

### 企业微信配置

企业微信配置信息存储在 auth 服务的 `third_party_config` 表中，平台类型为 1。

## 部署说明

1. 创建数据库并执行 `sql/wechatwork-schema.sql`
2. 配置数据库连接信息
3. 配置 Redis 连接信息
4. 在 auth 服务中配置企业微信相关信息
5. 启动服务

## 开发说明

### 项目结构

```
src/main/java/com/origin/wechatwork/
├── WechatworkApplication.java          # 启动类
├── config/                             # 配置类
├── controller/                         # 控制器
├── dto/                               # 数据传输对象
├── entity/                            # 实体类
├── feign/                             # Feign客户端
├── mapper/                            # 数据访问层
└── service/                           # 业务逻辑层
```

### 开发规范

- 遵循项目的编码规范
- 使用 Lombok 简化代码
- 使用 MyBatis Plus 进行数据访问
- 使用 Feign 进行服务间调用
- 使用 Redis 进行缓存

## 注意事项

1. 企业微信 API 调用需要有效的 access_token
2. access_token 会自动刷新，无需手动管理
3. 所有数据库操作都支持软删除
4. 外部用户信息会自动同步到本地数据库

## 作者

scccy 