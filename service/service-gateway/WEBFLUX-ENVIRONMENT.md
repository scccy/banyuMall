# Gateway WebFlux环境配置说明

## 问题描述

Gateway服务启动时出现以下错误：

### 1. Web服务器配置错误
```
Description:
Web application could not be started as there was no org.springframework.boot.web.servlet.server.ServletWebServerFactory bean defined in the context.

Action:
Check your application's dependencies for a supported servlet web server.
Check the configured web application type.
```

### 2. Bean冲突错误
```
The bean 'requestMappingHandlerMapping', defined in class path resource [org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$EnableWebMvcConfiguration.class], could not be registered. A bean with that name has already been defined in class path resource [org/springframework/web/reactive/config/DelegatingWebFluxConfiguration.class] and overriding is disabled.
```

## 原因分析

1. **Web环境冲突**: Spring Cloud Gateway需要WebFlux环境，但service-base包含了传统的Servlet Web依赖
2. **依赖冲突**: `spring-boot-starter-web`与`spring-cloud-starter-gateway`冲突
3. **配置冲突**: WebMvc和WebFlux配置同时存在，导致Bean名称冲突
4. **自动配置冲突**: Spring Boot同时检测到Servlet Web和WebFlux的自动配置

## 解决方案

### 1. 排除Servlet Web依赖

在Gateway服务的service-base依赖中排除Servlet Web相关依赖：

```xml
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-base</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <exclusions>
        <!-- 排除Servlet Web依赖，Gateway使用WebFlux -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </exclusion>
        <!-- 排除Validation依赖，Gateway不需要 -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </exclusion>
        <!-- 排除MyBatis Plus，Gateway不需要数据库 -->
        <exclusion>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        </exclusion>
        <!-- 排除Redis，Gateway不需要 -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </exclusion>
        <!-- 排除OSS，Gateway不需要 -->
        <exclusion>
            <groupId>com.aliyun.oss</groupId>
            <artifactId>aliyun-sdk-oss</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 2. 排除Servlet Web自动配置

在GatewayApplication中排除所有Servlet Web相关的自动配置：

```java
@SpringBootApplication(
    scanBasePackages = {"com.origin"},
    exclude = {
        WebMvcAutoConfiguration.class,
        WebMvcConfig.class,
        GlobalExceptionHandler.class
    }
)
@EnableDiscoveryClient
public class GatewayApplication {
    // ...
}
```

### 3. 配置WebFlux环境

创建专门的WebFlux配置类：

```java
@Configuration
@EnableWebFlux
public class GatewayWebConfig implements WebFluxConfigurer {
    
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 1MB
    }
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}
```

### 4. 创建WebFlux异常处理器

创建Gateway专用的WebFlux异常处理器：

```java
@Slf4j
@Component
@Order(-1)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ResultData<Object> resultData;
        
        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            log.warn("Gateway业务异常: {}", businessException.getMessage());
            resultData = ResultData.fail(businessException.getErrorCode(), businessException.getMessage());
        } else {
            log.error("Gateway系统异常: ", ex);
            resultData = ResultData.fail(ErrorCode.INTERNAL_ERROR, "网关服务异常，请稍后重试");
        }

        String responseBody = resultData.toString();
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
}
```

## WebFlux vs Servlet Web

### Spring Cloud Gateway (WebFlux)
- **特点**: 响应式编程模型，非阻塞I/O
- **适用场景**: 高并发、低延迟的API网关
- **依赖**: `spring-cloud-starter-gateway`
- **Web环境**: WebFlux
- **异常处理**: `ErrorWebExceptionHandler`
- **CORS配置**: `CorsWebFilter`

### 传统Spring Boot Web (Servlet)
- **特点**: 传统的同步编程模型
- **适用场景**: 业务服务、REST API
- **依赖**: `spring-boot-starter-web`
- **Web环境**: Servlet
- **异常处理**: `@RestControllerAdvice`
- **CORS配置**: `WebMvcConfigurer`

## 依赖管理策略

### Gateway服务需要的依赖
- `spring-cloud-starter-gateway` (包含WebFlux)
- `spring-boot-starter-actuator`
- `spring-cloud-starter-alibaba-nacos-discovery`
- `spring-cloud-starter-alibaba-nacos-config`
- `service-base` (排除Servlet Web依赖)

### Gateway服务不需要的依赖
- `spring-boot-starter-web` (Servlet Web)
- `spring-boot-starter-validation`
- `mybatis-plus-spring-boot3-starter`
- `spring-boot-starter-data-redis`
- `aliyun-sdk-oss`

### Gateway服务需要排除的配置
- `WebMvcAutoConfiguration` (Servlet Web自动配置)
- `WebMvcConfig` (Servlet Web配置)
- `GlobalExceptionHandler` (Servlet Web异常处理)

## 配置验证

### 1. 检查Web环境
启动后检查日志，确认使用的是WebFlux环境：
```
Started GatewayApplication in X.XXX seconds (process running for X.XXX)
```

### 2. 验证路由功能
测试Gateway路由是否正常工作：
```bash
curl -X GET "http://localhost:8080/actuator/health"
```

### 3. 检查依赖冲突
使用Maven命令检查依赖：
```bash
./mvnw dependency:tree -pl service/service-gateway
```

### 4. 验证异常处理
测试异常处理是否正常工作：
```bash
curl -X GET "http://localhost:8080/nonexistent"
```

## 注意事项

1. **环境隔离**: Gateway服务必须使用WebFlux环境，不能与Servlet Web混用
2. **依赖排除**: 必须排除service-base中的Servlet Web依赖
3. **配置分离**: Gateway的配置应该与其他服务分离
4. **性能优化**: WebFlux环境适合高并发场景
5. **异常处理**: 使用WebFlux的异常处理机制
6. **CORS配置**: 使用WebFlux的CORS配置方式

## 相关文件

- `service/service-gateway/pom.xml`
- `service/service-gateway/src/main/java/com/origin/gateway/config/GatewayWebConfig.java`
- `service/service-gateway/src/main/java/com/origin/gateway/exception/GatewayExceptionHandler.java`
- `service/service-gateway/src/main/java/com/origin/gateway/GatewayApplication.java`
- `service/service-gateway/src/main/resources/dev/application.yml` 