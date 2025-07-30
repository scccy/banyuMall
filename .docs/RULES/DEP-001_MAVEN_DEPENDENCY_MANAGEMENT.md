# Maven多模块依赖管理规则

**ID**: DEP-001  
**Name**: Maven多模块依赖管理规则  
**Status**: Active  
**创建时间**: 2025-07-30  

## 触发情景 (Context/Trigger)
当处理多模块Maven项目时，需要管理依赖版本、解决版本冲突、优化构建配置。

## 指令 (Directive)

### 1. 版本统一管理
- **必须 (MUST)** 在根pom.xml的properties中定义所有版本号
- **必须 (MUST)** 使用dependencyManagement统一管理依赖版本
- **禁止 (MUST NOT)** 在子模块中重复声明版本号

### 2. BOM依赖管理
- **必须 (MUST)** 使用BOM (Bill of Materials) 管理相关依赖
- **必须 (MUST)** 在dependencyManagement中导入Spring Cloud、Spring Cloud Alibaba等BOM
- **必须 (MUST)** 优先使用BOM管理的版本，避免手动指定版本

### 3. 依赖声明优化
- **必须 (MUST)** 移除重复的依赖声明
- **必须 (MUST)** 清理空的license、developer、scm标签
- **必须 (MUST)** 将依赖按功能分组并添加注释

### 4. 构建配置优化
- **必须 (MUST)** 使用pluginManagement统一管理插件配置
- **必须 (MUST)** 避免在子模块中重复声明相同的插件配置
- **必须 (MUST)** 统一Lombok等注解处理器的配置

### 5. 版本冲突解决
- **必须 (MUST)** 定期检查并解决版本冲突
- **必须 (MUST)** 使用mvn dependency:tree分析依赖关系
- **必须 (MUST)** 优先使用较新的稳定版本

## 理由 (Justification)
此规则源于任务 `task_20250127_1430_pom_dependency_optimization.md`。在该任务中，发现多模块项目存在版本冲突、重复依赖、配置冗余等问题，通过统一版本管理和优化依赖声明解决了这些问题。

## 最佳实践
- 使用Maven Dependency Plugin进行依赖分析
- 定期更新依赖版本
- 使用Maven Enforcer Plugin强制执行版本规则
- 建立依赖更新流程和测试验证机制

## 示例

### 根pom.xml版本管理示例
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.2.5</spring-boot.version>
    <spring-cloud.version>2023.0.0</spring-cloud.version>
    <jwt.version>0.12.5</jwt.version>
    <fastjson2.version>2.0.57</fastjson2.version>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- Spring Cloud BOM -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        
        <!-- JWT Dependencies -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jwt.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 子模块依赖声明示例
```xml
<dependencies>
    <!-- 使用BOM管理的版本，无需指定版本号 -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
    </dependency>
</dependencies>
```

## 检查清单
- [ ] 所有版本号是否在根pom.xml中统一管理
- [ ] 是否使用了BOM管理相关依赖
- [ ] 是否移除了重复的依赖声明
- [ ] 是否清理了空的标签
- [ ] 是否使用了pluginManagement统一插件配置
- [ ] 是否解决了版本冲突
- [ ] 是否按功能分组并添加了注释 