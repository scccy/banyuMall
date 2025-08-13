# Gitignore 最佳实践

## 概述
本文档记录了 banyuMall 项目中微服务模块的 `.gitignore` 文件管理最佳实践。

## 微服务模块 .gitignore 统一标准

### 基础排除规则（所有模块通用）
```gitignore
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iws
*.iml
*.ipr
.vscode/
.cursor/
.trae/

# OS
.DS_Store
Thumbs.db

# Logs
*.log
*.log.gz
*.log.bz2
logs/

# Temporary files
*.tmp
*.temp
*.bak
*.backup

# Environment files
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# Application specific
application-local.yml
application-local.properties
```

### 特殊模块排除规则

#### third-party-aliyunOss 模块
```gitignore
# OSS specific
upload-temp/
oss-cache/
```

#### third-party-wechatWork 模块
```gitignore
# WeChat Work specific
qrcode-temp/
wechat-cache/
```

## 最佳实践规则

### 1. 统一性
- 所有微服务模块必须使用相同的 `.gitignore` 基础规则
- 确保排除规则的一致性，避免遗漏重要文件

### 2. 模块特定性
- 根据模块功能特点，添加特定的排除规则
- 第三方服务模块需要排除相关的临时文件和缓存

### 3. 安全性
- 必须排除所有环境配置文件（.env*）
- 必须排除本地配置文件（application-local.*）
- 必须排除日志文件和临时文件

### 4. 维护性
- 定期检查和更新 `.gitignore` 文件
- 新增模块时必须同步创建 `.gitignore` 文件
- 记录排除规则的原因和影响

## 验证清单

创建新的微服务模块时，请确保：

- [ ] 创建了 `.gitignore` 文件
- [ ] 包含了所有基础排除规则
- [ ] 根据模块特点添加了特定排除规则
- [ ] 验证了排除规则的有效性
- [ ] 更新了本文档

## 常见问题

### Q: 为什么需要为每个模块单独创建 .gitignore？
A: 每个微服务模块都是独立的 Git 仓库，需要独立的 `.gitignore` 文件来管理该模块的文件排除规则。

### Q: 如何处理模块特定的临时文件？
A: 在基础规则基础上，根据模块功能添加特定的排除规则，如 OSS 模块的 upload-temp/ 目录。

### Q: 如何确保排除规则的一致性？
A: 使用统一的模板，定期检查和更新所有模块的 `.gitignore` 文件。

## 更新记录

- **2025-08-13**: 创建统一的微服务模块 .gitignore 标准
- **2025-08-13**: 为所有 8 个微服务模块创建 .gitignore 文件
