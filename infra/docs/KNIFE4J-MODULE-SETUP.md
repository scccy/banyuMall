# Knife4j模块配置说明

## 配置架构

### 模块依赖关系
```
service-base (Knife4jConfig)
    ├── service-auth (继承配置)
    └── service-user (继承配置)
```

### 配置位置
- **Knife4jConfig**: `service/service-base/src/main/java/com/origin/config/Knife4jConfig.java`
- **自动配置**: `service/service-base/src/main/resources/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## 配置详情

### 1. service-base模块
**位置**: `service/service-base/src/main/java/com/origin/config/Knife4jConfig.java`

```java
@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BanyuMall API文档")
                        .description("BanyuMall微服务架构API接口文档")
                        .version("1.0.0")
                        // ... 其他配置
                );
    }
}
```

### 2. 自动配置注册
**位置**: `service/service-base/src/main/resources/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

```
com.origin.base.config.Knife4jConfig
```

### 3. 服务模块继承

#### service-auth模块
- **依赖**: 继承service-base模块
- **组件扫描**: `@ComponentScan({"com.origin"})`
- **测试接口**: `/test/health`, `/test/knife4j`

#### service-user模块
- **依赖**: 继承service-base模块
- **组件扫描**: `@ComponentScan(basePackages = {"com.origin"})`
- **测试接口**: `/test/health`, `/test/knife4j`

## 访问地址

### Auth服务
- **Knife4j界面**: `http://localhost:8081/auth/doc.html`
- **OpenAPI JSON**: `http://localhost:8081/auth/v3/api-docs`
- **健康检查**: `http://localhost:8081/auth/test/health`

### User服务
- **Knife4j界面**: `http://localhost:8082/user/doc.html`
- **OpenAPI JSON**: `http://localhost:8082/user/v3/api-docs`
- **健康检查**: `http://localhost:8082/user/test/health`

## 验证方法

### 1. 启动服务
```bash
# 启动Auth服务
cd service/service-auth
./mvnw spring-boot:run

# 启动User服务
cd service/service-user
./mvnw spring-boot:run
```

### 2. 访问测试接口
```bash
# Auth服务测试
curl http://localhost:8081/auth/test/health
curl http://localhost:8081/auth/test/knife4j

# User服务测试
curl http://localhost:8082/user/test/health
curl http://localhost:8082/user/test/knife4j
```

### 3. 访问API文档
- 打开浏览器访问对应的doc.html地址
- 检查API文档是否正确显示
- 验证测试接口是否在文档中可见

## 配置优势

### 1. 统一配置
- ✅ 所有服务模块共享相同的Knife4j配置
- ✅ 统一的API文档风格和信息
- ✅ 减少重复配置

### 2. 自动注入
- ✅ 通过Spring Boot自动配置机制自动注入
- ✅ 无需在每个服务中手动配置
- ✅ 支持条件化配置

### 3. 模块化设计
- ✅ service-base作为基础模块提供通用配置
- ✅ 各服务模块专注于业务逻辑
- ✅ 便于维护和扩展

## 注意事项

1. **组件扫描**: 确保各服务模块的`@ComponentScan`包含`com.origin`包
2. **自动配置**: 确保`AutoConfiguration.imports`文件包含`Knife4jConfig`
3. **依赖管理**: 确保所有模块都正确依赖service-base模块
4. **端口配置**: 注意不同服务的端口配置，避免冲突

## 故障排除

### 1. 文档无法访问
- 检查服务是否正常启动
- 验证端口配置是否正确
- 确认Knife4j依赖是否正确引入

### 2. 配置不生效
- 检查自动配置文件是否正确
- 验证组件扫描范围是否包含配置类
- 确认依赖关系是否正确

### 3. 接口不显示
- 检查控制器是否正确使用`@Tag`和`@Operation`注解
- 验证包扫描范围是否包含控制器
- 确认接口路径配置是否正确 