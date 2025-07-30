# 模板统一管理目录

## 概述

本目录统一管理BanyuMall项目的所有模板文件，包括邮件模板、通知模板、文档模板、配置模板等。

## 目录结构

```
infra/templates/
├── README.md                    # 本说明文档
├── email/                       # 邮件模板
│   ├── welcome.html             # 欢迎邮件模板
│   ├── password-reset.html      # 密码重置邮件模板
│   ├── verification.html        # 邮箱验证邮件模板
│   └── README.md               # 邮件模板说明
├── notification/                # 通知模板
│   ├── system-notification.json # 系统通知模板
│   ├── user-notification.json   # 用户通知模板
│   └── README.md               # 通知模板说明
├── document/                    # 文档模板
│   ├── api-doc-template.md      # API文档模板
│   ├── user-manual-template.md  # 用户手册模板
│   └── README.md               # 文档模板说明
└── config/                      # 配置模板
    ├── nacos-config-template.yml # Nacos配置模板
    ├── nacos-config-template-auth.yml # Auth服务Nacos配置模板
    ├── nacos-config-template-user.yml # User服务Nacos配置模板
    ├── docker-compose-template.yml # Docker Compose模板
    └── README.md               # 配置模板说明
```

## 模板类型说明

### 1. 邮件模板 (email/)
- **用途**: 系统发送的各种邮件模板
- **格式**: HTML + 变量占位符
- **变量**: 使用 `${variable}` 格式

### 2. 通知模板 (notification/)
- **用途**: 系统内部通知和用户通知
- **格式**: JSON + 变量占位符
- **变量**: 使用 `${variable}` 格式

### 3. 文档模板 (document/)
- **用途**: 项目文档和用户手册
- **格式**: Markdown + 变量占位符
- **变量**: 使用 `{{variable}}` 格式

### 4. 配置模板 (config/)
- **用途**: 各种配置文件模板
- **格式**: YAML/JSON + 环境变量
- **变量**: 使用 `${ENV_VAR}` 格式

## 使用方式

### 1. 邮件模板使用
```java
// 读取邮件模板
String template = TemplateLoader.loadEmailTemplate("welcome.html");

// 替换变量
String content = TemplateProcessor.process(template, variables);

// 发送邮件
emailService.sendEmail(to, subject, content);
```

### 2. 通知模板使用
```java
// 读取通知模板
String template = TemplateLoader.loadNotificationTemplate("system-notification.json");

// 替换变量
String content = TemplateProcessor.process(template, variables);

// 发送通知
notificationService.sendNotification(userId, content);
```

### 3. 文档模板使用
```java
// 读取文档模板
String template = TemplateLoader.loadDocumentTemplate("api-doc-template.md");

// 替换变量
String content = TemplateProcessor.process(template, variables);

// 生成文档
documentService.generateDocument(content);
```

### 4. 配置模板使用
```bash
# 使用环境变量替换配置模板
envsubst < infra/templates/config/nacos-config-template.yml > nacos-config.yml
```

## 变量规范

### 1. 邮件模板变量
- 用户相关: `${userName}`, `${userEmail}`, `${userId}`
- 系统相关: `${systemName}`, `${systemUrl}`, `${currentTime}`
- 业务相关: `${orderId}`, `${productName}`, `${amount}`

### 2. 通知模板变量
- 用户相关: `${userName}`, `${userId}`
- 消息相关: `${message}`, `${title}`, `${type}`
- 时间相关: `${timestamp}`, `${expireTime}`

### 3. 文档模板变量
- 项目相关: `{{projectName}}`, `{{version}}`, `{{author}}`
- API相关: `{{apiName}}`, `{{endpoint}}`, `{{method}}`
- 时间相关: `{{currentDate}}`, `{{lastUpdate}}`

### 4. 配置模板变量
- 环境相关: `${ENV}`, `${PROFILE}`
- 服务相关: `${SERVICE_NAME}`, `${SERVICE_PORT}`
- 数据库相关: `${DB_HOST}`, `${DB_PORT}`, `${DB_NAME}`

## 模板管理

### 1. 版本控制
- 所有模板文件纳入版本控制
- 使用语义化版本号管理模板变更
- 记录模板变更历史

### 2. 测试验证
- 为每个模板编写测试用例
- 验证变量替换的正确性
- 检查模板渲染效果

### 3. 国际化支持
- 支持多语言模板
- 使用语言标识符区分模板
- 提供默认语言模板

## 最佳实践

### 1. 模板设计
- 保持模板简洁明了
- 使用语义化的变量名
- 提供默认值和示例

### 2. 变量管理
- 统一变量命名规范
- 提供变量说明文档
- 避免硬编码值

### 3. 性能优化
- 缓存编译后的模板
- 预编译常用模板
- 异步处理模板渲染

## 相关文档

- [邮件服务配置说明](../email-service/README.md)
- [通知服务配置说明](../notification-service/README.md)
- [文档生成服务说明](../document-service/README.md) 