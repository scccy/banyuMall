# Maven模块冲突修复说明

## 问题描述
遇到错误：`源根 '/Volumes/project/github/banyuMall/infra/infra-gateway/src/main/resources' 在模块 'infra-gateway' 中重复。`

## 问题原因分析

### 1. 可能的冲突原因
- **artifactId冲突**: 模块的artifactId与目录名称可能冲突
- **路径重复**: Maven解析时发现重复的源代码路径
- **版本不兼容**: Spring Cloud Alibaba版本配置不正确

### 2. 当前项目结构
```
banyuMall/
├── pom.xml                     # 根POM
├── infra/
│   ├── pom.xml                 # infra父模块 (artifactId: infra)
│   └── infra-gateway/          # 网关模块目录
│       ├── pom.xml             # infra-gateway模块 (artifactId: infra-gateway)
│       └── src/main/
│           ├── java/
│           └── resources/
└── service/
    ├── pom.xml                 # service父模块
    ├── service-base/
    └── service-auth/
```

### 3. 已修复的问题

#### 3.1 Spring Cloud Alibaba版本确认
```xml
<!-- 当前正确版本 -->
<version>2023.0.1.0</version>
```

#### 3.2 模块配置检查
- infra父模块: `artifactId: infra`
- infra-gateway子模块: `artifactId: infra-gateway`
- 目录结构: `infra/infra-gateway/`

#### 3.3 版本兼容性确认
- Spring Boot: 3.2.5
- Spring Cloud: 2023.0.0
- Spring Cloud Alibaba: 2023.0.1.0 ✅

### 4. 解决方案

#### 4.1 清理Maven缓存
```bash
# 清理项目
./mvnw clean

# 删除本地仓库中的缓存
rm -rf ~/.m2/repository/com/origin/infra-gateway
rm -rf ~/.m2/repository/com/origin/infra

# 重新编译
./mvnw compile
```

#### 4.2 验证模块配置
```bash
# 检查模块结构
./mvnw dependency:tree

# 验证POM文件
./mvnw validate
```

### 5. 预防措施

#### 5.1 模块命名规范
- 父模块: 使用功能名称 (如 `infra`, `service`)
- 子模块: 使用具体服务名称 (如 `infra-gateway`, `service-auth`)
- 避免artifactId与目录名称冲突

#### 5.2 版本管理
- 统一在根POM中管理版本
- 确保版本兼容性
- 避免在子模块中重复定义版本

### 6. 验证步骤
1. 清理Maven缓存
2. 重新编译项目
3. 检查依赖树
4. 启动服务验证

## 注意事项
- 确保每个模块的artifactId唯一
- 避免目录名称与artifactId冲突
- 保持版本配置的一致性
- 定期清理Maven缓存 