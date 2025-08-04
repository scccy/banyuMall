# BanyuMall 项目文档中心

## 📋 项目概述

**项目名称**: BanyuMall 微服务项目  
**技术栈**: Java 21 + Spring Boot 3.2.5 + Spring Cloud 2023.0.0 + Nacos  
**架构模式**: 微服务架构  
**作者**: scccy  

## 🧠 共享大脑结构

本项目的 `.docs` 目录是我们的"共享大脑"，包含以下核心组件：

### 📁 目录结构

```
.docs/
├── README-DOCS.md              # 本文档 - 项目文档中心入口
├── STATE/                      # 事实基线 - 项目当前状态描述
│   ├── TECH-STACK.md          # 技术栈基线
│   ├── MODULE-STRUCTURE.md    # 模块结构基线
│   ├── DB-SCHEMA.md           # 数据库设计基线
│   └── API-BASELINE.md        # API接口基线
├── PROCESS/                    # 过程流水 - 推动状态变更的过程文档
│   ├── TEMPLATES/             # 文档模板
│   │   ├── MODULE-DESIGN.md   # 模块设计模板
│   │   ├── API-DESIGN.md      # API设计模板
│   │   └── TASK-TEMPLATE.md   # 任务模板
│   └── PROPOSALS/             # 提案文档
├── RULES/                      # 开发规则 - 从经验中沉淀的规则
│   ├── RULES_SUMMARY.md       # 规则汇总
│   ├── DB-RULES.md            # 数据库开发规则
│   ├── API-RULES.md           # API开发规则
│   └── MODULE-RULES.md        # 模块开发规则
└── TEMP/                       # 临时状态 - 任务执行状态跟踪
```

## 🎯 核心原则

### 1. 依据驱动 (Evidence-Driven)
- 每个规划和重要建议都必须明确给出事实依据
- 参考 `.docs/RULES/` 下的适用规则
- 基于 `.docs/STATE/` 下的状态基线

### 2. 基线先行 (Baseline-First)
- **STATE/**: 存放描述项目当前状态的"事实基线"文档
- **PROCESS/**: 存放用于推动状态变更的"过程流水"文档
- **RULES/**: 存放从过往经验中沉淀的"开发规则"，拥有高优先级

### 3. 状态外化 (Stateful Task Execution)
- 非平凡任务必须将状态持久化到 `.docs/TEMP/` 目录
- 确保任务状态的一致性和可追溯性

### 4. 测试驱动 (Test-Driven)
- 逻辑相关的编码工作必须先定义可执行的测试用例

### 5. 模板驱动 (Template-Driven)
- 创建新的过程文档必须使用 `.docs/PROCESS/TEMPLATES/` 下的模板

### 6. 经验学习 (Experience Learning)
- 主动发现问题、不一致性和优化机会
- 将经验沉淀为可复用的规则

## 🔄 工作流程

### 标准开发流程
1. **加载核心上下文** → 读取相关规则和状态基线
2. **分析** → 理解需求和约束
3. **查找加载相关上下文** → 定位相关文档和规则
4. **规划与状态外化** → 创建任务状态文件
5. **分步执行与状态更新** → 按计划执行并更新状态
6. **任务后回顾与经验萃取** → 分析变更和解决方案
7. **规则泛化与基线整合** → 沉淀新规则和更新基线
8. **自我纠错** → 检查是否违反规则

### 任务分流机制
- **A. [零仪式通道]**: 明显的错字修正、注释调整等
- **B. [快速通道]**: 小功能、简单代码修改或查询
- **C. [标准通道]**: 常规开发任务，需要持久化任务状态
- **D. [提案驱动流程]**: 重大功能、架构变更，需要用户批准

## 📚 核心规则汇总

### 数据库开发规则
- 主键命名：`tableName_id` 格式
- 字段类型：主键使用 `VARCHAR(32)`，状态使用 `INT`
- 关联逻辑：左表存在右表ID，避免外键约束
- 索引设计：`idx_` 前缀普通索引，`uk_` 前缀唯一索引

### 模块开发规则
- 配置类在 `service-base` 模块，工具类在 `service-common` 模块
- 使用整数映射枚举类型，避免使用 `VARCHAR`
- 文档按模块在 `zinfra/moudleDocs/` 下组织

### API开发规则
- 路由命名：`/service/<entity>` 格式
- 响应格式：使用 `success()` 方法返回成功响应
- JSON处理：使用 FastJSON2 而非 Jackson

### 项目规范
- 代码作者：scccy
- 使用中文回答和文档
- 不自动启动服务，用户使用 IntelliJ IDEA 运行

## 🚀 快速开始

### 新任务开始
1. 检查 `.docs/RULES/RULES_SUMMARY.md` 了解最新规则
2. 根据任务复杂度选择对应通道
3. 创建任务状态文件（标准通道及以上）
4. 按计划执行并更新状态

### 新模块开发
1. 使用 `.docs/PROCESS/TEMPLATES/MODULE-DESIGN.md` 模板
2. 遵循模块开发规则
3. 更新 `.docs/STATE/MODULE-STRUCTURE.md` 基线

### 数据库变更
1. 遵循 `.docs/RULES/DB-RULES.md` 规则
2. 更新 `.docs/STATE/DB-SCHEMA.md` 基线
3. 在 `zinfra/database/data/` 下创建对应SQL文件

## 📝 文档维护

### 状态基线更新
- 技术栈变更 → 更新 `STATE/TECH-STACK.md`
- 模块结构变更 → 更新 `STATE/MODULE-STRUCTURE.md`
- 数据库设计变更 → 更新 `STATE/DB-SCHEMA.md`
- API接口变更 → 更新 `STATE/API-BASELINE.md`

### 规则沉淀
- 新经验 → 创建 `.docs/RULES/LR-XXX.md` 文件
- 更新汇总 → 同步到 `RULES/RULES_SUMMARY.md`
- 规则冲突 → 优先遵循 `RULES/` 下的明确规则

---

**版本**: v1.0  
**创建日期**: 2025-08-04  
**最后更新**: 2025-08-04  
**维护者**: scccy 