# API文档配置说明

## 技术栈选择

本项目使用 **Knife4j** 作为API文档解决方案，它是基于SpringDoc OpenAPI 3的增强版本。

### 为什么选择Knife4j？

1. **功能丰富**: 在SpringDoc基础上提供了更多功能
2. **界面美观**: 提供更美观的API文档界面
3. **中文支持**: 提供完整的中文文档和界面
4. **增强功能**: 支持接口调试、Mock数据等

## 依赖配置

### 根pom.xml
```xml
<properties>
    <knife4j.version>4.4.0</knife4j.version>
</properties>

<dependencyManagement>
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <version>${knife4j.version}</version>
    </dependency>
</dependencyManagement>
```

### 服务模块pom.xml
```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
</dependency>
```

## 配置说明

### 1. Knife4j配置类
已创建 `Knife4jConfig.java` 配置类，包含：
- API文档基本信息
- 联系信息
- 许可证信息

### 2. 配置文件
在 `application.yml` 中可以添加以下配置：

```yaml
# Knife4j配置
knife4j:
  enable: true
  setting:
    language: zh-CN
    enable-swagger-models: true
    enable-document-manage: true
    swagger-model-name: 实体类列表
    enable-version: false
    enable-reload-cache-parameter: false
    enable-after-script: true
    enable-filter-multipart-api-method-type: POST
    enable-filter-multipart-apis: false
    enable-request-cache: true
    enable-host: false
    enable-host-text: ""
    enable-home-custom: false
    home-custom-path: ""
    enable-search: true
    enable-footer: false
    enable-footer-custom: true
    footer-custom-content: Apache License 2.0 | Copyright  2024-[BanyuMall](https://github.com/scccy/banyuMall)
    enable-dynamic-parameter: false
    enable-debug: true
    enable-open-api: false
    enable-group: true
```

## 使用方法

### 1. 访问地址
启动服务后，访问以下地址查看API文档：
- **Knife4j界面**: `http://localhost:端口/doc.html`
- **OpenAPI JSON**: `http://localhost:端口/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:端口/v3/api-docs.yaml`

### 2. 注解使用
```java
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @Parameter(name = "id", description = "用户ID", required = true)
    @GetMapping("/{id}")
    public ResultData<UserProfile> getUserById(@PathVariable Long id) {
        // 实现逻辑
    }
}
```

### 3. 常用注解
- `@Tag`: 标记控制器类
- `@Operation`: 标记接口方法
- `@Parameter`: 标记参数
- `@Schema`: 标记实体类属性

## 问题解决

### 1. 依赖冲突
如果遇到 `springdoc-openapi-ui` 相关错误，请确保：
- 移除所有 `springdoc-openapi-ui` 依赖
- 只使用 `knife4j-openapi3-jakarta-spring-boot-starter`

### 2. 版本兼容性
- Knife4j 4.4.0 兼容 Spring Boot 3.x
- 确保使用 Jakarta EE 版本

### 3. 清理缓存
如果遇到依赖解析问题，请清理Maven缓存：
```bash
./mvnw clean
rm -rf ~/.m2/repository/com/github/xiaoymin/knife4j*
./mvnw compile
```

## 最佳实践

1. **统一配置**: 在service-base模块中统一配置Knife4j
2. **版本管理**: 在根pom.xml中统一管理版本
3. **文档规范**: 使用标准的OpenAPI 3注解
4. **安全配置**: 在生产环境中适当配置访问权限 