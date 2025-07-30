# Spring Boot 降级解决方案

## 版本降级说明

### 当前版本配置
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.2.5</spring-boot.version>        <!-- 已降级 -->
    <spring-cloud.version>2023.0.0</spring-cloud.version>   <!-- 已调整 -->
</properties>
```

### 版本兼容性表
| Spring Boot版本 | Spring Cloud版本 | Spring Cloud Alibaba版本 | 代号 |
|----------------|-----------------|------------------------|------|
| 3.5.x | 2025.0.x | 2022.0.0.0 | Northfields |
| 3.4.x | 2024.0.x | 2022.0.0.0 | Moorgate |
| **3.2.x** | **2023.0.x** | **2022.0.0.0** | **Leyton** ✅ |
| 3.0.x, 3.1.x | 2022.0.x | 2021.0.5.0 | Kilburn |

### 降级原因
1. **版本兼容性**: Spring Boot 3.2.5 与 Spring Cloud 2023.0.0 完全兼容
2. **稳定性**: 3.2.x 是更稳定的版本，有更多的生产环境验证
3. **依赖可用性**: 避免新版本依赖在Maven仓库中不可用的问题

### 清理Maven缓存
```bash
# 清理并强制更新
./mvnw clean -U

# 删除本地仓库中的缓存
rm -rf ~/.m2/repository/org/springframework/boot/spring-boot-starter-parent
rm -rf ~/.m2/repository/org/springframework/cloud/spring-cloud-dependencies

# 重新编译
./mvnw clean compile
```

### 验证步骤
1. **检查版本**: 确认所有模块使用正确的Spring Boot版本
2. **编译项目**: 确保没有版本冲突
3. **启动服务**: 验证服务能正常启动

### 启动命令
```bash
# 启动网关服务
./mvnw spring-boot:run -pl infra/infra-gateway

# 启动认证服务
./mvnw spring-boot:run -pl service/service-auth
```

## 注意事项
- Spring Boot 3.2.5 与 Java 21 完全兼容
- Spring Cloud 2023.0.0 是稳定版本
- Spring Cloud Alibaba 2022.0.0.0 与 Spring Cloud 2023.0.0 兼容
- 降级后需要清理Maven缓存并重新下载依赖 