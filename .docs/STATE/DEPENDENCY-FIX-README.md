# Spring Cloud Gateway 依赖问题解决方案

## 问题描述
遇到错误：`org.springframework.cloud:spring-cloud-starter-gateway:jar:unknown was not found`

## 解决方案

### 1. 版本兼容性修复
根据[Spring Cloud官方文档](https://spring.io/projects/spring-cloud)，已修复版本兼容性：

```xml
<!-- 根POM中的版本配置 -->
<properties>
    <spring-boot.version>3.5.4</spring-boot.version>
    <spring-cloud.version>2025.0.0</spring-cloud.version>  <!-- 已修复 -->
</properties>
```

### 2. 版本兼容性表
| Spring Boot版本 | Spring Cloud版本 | 代号 |
|----------------|-----------------|------|
| 3.5.x | 2025.0.x | Northfields |
| 3.4.x | 2024.0.x | Moorgate |
| 3.3.x, 3.2.x | 2023.0.x | Leyton |

### 3. 清理Maven缓存
```bash
# 清理本地仓库缓存
./mvnw clean -U

# 或者强制更新依赖
./mvnw dependency:purge-local-repository -DmanualInclude="org.springframework.cloud:spring-cloud-starter-gateway"
```

### 4. 项目结构
```
banyuMall/
├── infra/
│   ├── pom.xml                    # infra父POM
│   └── infra-gateway/             # 网关服务 (保持您想要的名称)
│       ├── pom.xml
│       └── src/
├── service/
│   ├── service-auth/
│   └── service-base/
└── pom.xml                        # 根POM
```

### 5. 启动命令
```bash
# 启动网关服务
./mvnw spring-boot:run -pl infra/infra-gateway

# 启动认证服务
./mvnw spring-boot:run -pl service/service-auth
```

### 6. 依赖配置
- **Spring Cloud Gateway**: 通过BOM管理版本
- **Nacos Discovery**: 2022.0.0.0版本
- **Nacos Config**: 2022.0.0.0版本

### 7. 注意事项
- 确保Java 21环境已安装
- 确保网络能够访问Maven仓库
- 首次启动可能需要较长时间下载依赖

## 验证步骤
1. 安装Java 21环境
2. 运行 `./mvnw clean -U` 清理并更新依赖
3. 运行 `./mvnw compile` 编译项目
4. 启动服务验证功能 