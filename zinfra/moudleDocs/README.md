# 模块文档目录

## 📁 目录结构

```
zinfra/moudleDocs/
├── README.md                           # 本文件
├── xxxxxx-design_templates.md          # 模块设计文档模板
├── xxxxxx-模块主体讨论模板.md          # 模块主体讨论模板
├── xxxxxx-模块迭代说明模板.md          # 模块迭代说明模板
├── xxxxxx-api-test-template.md         # 模块API测试文档模板
├── xxxxxx-development-template.md      # 模块开发文档模板
├── xxxxxx-interface-test-validation-template.md # 接口测试验证模板
├── 微服务总体设计框架.md               # 微服务架构总体设计指导
├── 数据库建表语句和表说明.md           # 数据库设计汇总文档
├── 标准化模块文档结构模板.md           # 模块文档标准化模板
├── 新建模块工作流程.md                 # 模块开发工作流程指导
├── auth-design.md                      # Auth模块设计文档（旧格式）
└── {模块名称}/                         # 按模块名称组织的文件夹
    ├── 模块主体讨论.md                 # 模块需求讨论和功能规划
    ├── 模块设计.md                     # 模块详细设计文档
    ├── API接口说明.md                  # API接口详细说明
    ├── API接口测试.md                  # API接口测试文档
    ├── 模块迭代说明.md                 # 模块迭代需求说明
    └── 模块迭代设计.md                 # 模块迭代设计文档
```

## 🎯 目录组织原则

### 1. 按模块名称创建文件夹
- 每个模块在 `zinfra/moudleDocs/` 下创建独立的文件夹
- 文件夹名称与模块名称保持一致
- 例如：`service-user/`、`service-auth/`、`service-gateway/`

### 2. 文档文件命名规范
- **设计文档**: `design.md`
- **API测试文档**: `api-test.md`
- 文件名统一使用小写字母和连字符

### 3. 文档内容要求
- **设计文档**: 必须包含接口功能列表（Excel表格格式）、数据模型设计、接口设计等
- **API测试文档**: 必须包含完整的curl命令示例、错误场景测试、性能测试等
- **Feign调用标识**: 在接口列表中明确标识是否支持Feign调用

## 📋 现有模块文档

### ✅ 已完成
- **service-auth**: 
  - `service-auth/模块设计.md` - 认证模块设计文档
  - `service-auth/api-test.md` - 认证模块API测试文档
- **service-user**: 
  - `service-user/模块设计.md` - 用户模块设计文档
  - `service-user/api-test.md` - 用户模块API测试文档
- **service-gateway**: 
  - `service-gateway/模块设计.md` - 网关模块设计文档
  - `service-gateway/api-test.md` - 网关模块API测试文档
- **core-publisher**: 
  - `core-publisher/模块设计.md` - 发布者模块设计文档
  - `core-publisher/api-test.md` - 发布者模块API测试文档
- **third-party-oss**: 
  - `third-party-oss/模块设计.md` - OSS模块设计文档
  - `third-party-oss/api-test.md` - OSS模块API测试文档
- **third-party-wechatwork**: 
  - `third-party-wechatwork/模块主体讨论.md` - 企业微信模块主体讨论文档

### 🔄 待迁移
- **auth**: 
  - `auth-design.md` - Auth模块设计文档（旧格式，待迁移到 `service-auth/` 文件夹）

## 🛠️ 使用指南

### 创建新模块文档
1. 在 `zinfra/moudleDocs/` 下创建模块文件夹：`mkdir {模块名称}/`
2. 基于模板创建设计文档：`cp xxxxxx-design_templates.md {模块名称}/design.md`
3. 基于模板创建API测试文档：`cp xxxxxx-api-test-template.md {模块名称}/api-test.md`
4. 编辑文档内容，确保包含所有必需的部分

### 文档模板说明
- **xxxxxx-design_templates.md**: 包含完整的模块设计结构，使用占位符 `{模块名称}` 等
- **xxxxxx-api-test-template.md**: 包含完整的API测试结构，使用占位符 `{模块名称}` 等

### 接口功能列表格式
```markdown
| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | Feign调用 | 详细说明 |
|------|----------|----------|----------|----------|----------|-----------|----------|
| 1 | 创建用户 | POST | `/user` | 创建新用户 | 用户管理 | 否 | [查看详情](#31-用户基础信息管理接口) |
```

### 跳转链接格式
- 锚点ID格式：`{#31-{接口分类名}}`
- 跳转链接格式：`[查看详情](#31-{接口分类名})`

## 📝 注意事项

1. **文档位置**: 所有新模块文档必须放在对应的模块文件夹下
2. **模板使用**: 必须基于提供的模板创建文档，确保结构一致性
3. **Feign标识**: 必须在接口列表中明确标识是否支持Feign调用
4. **跳转链接**: 必须实现接口列表到详细说明的跳转功能
5. **Excel格式**: 接口列表必须使用Excel表格格式展示

## 🔗 相关规则

- **LR-002**: 模块设计优先规则 - 详细说明模块文档创建和使用规范
- **DEV-008**: 文档管理规则 - 说明文档的组织和管理方式

## 📅 更新历史

- **2025-01-27**: 创建新的目录结构，按模块名称组织文档
- **2025-01-27**: 迁移service-user模块文档到新结构
- **2025-01-27**: 更新文档模板，增加Feign调用标识
- **2025-01-27**: 创建core-publisher模块设计文档和API测试文档，接口路径统一使用/core-publisher/前缀
- **2025-01-27**: 创建third-party-wechatwork企业微信模块主体讨论文档
- **2025-08-01**: 优化core-publisher模块，删除任务参与相关接口（接口12-14），专注于发布者核心业务逻辑
- **2025-01-27**: 完成微服务模块设计文档与API测试文档生成任务
  - 完成service-auth模块设计文档和API测试文档
  - 完成service-user模块设计文档和API测试文档
  - 完成service-gateway模块设计文档和API测试文档
  - 完成core-publisher模块设计文档和API测试文档
  - 完成third-party-oss模块设计文档和API测试文档
  - 生成所有模块对应的Java测试代码 