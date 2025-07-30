# 基础设施服务启动说明

## 概述
本项目包含一个基础设施服务：
- `infra-gateway`: API网关服务 (端口: 8080)

## 前置条件
1. 确保已安装Java 21
2. 确保Nacos服务可用 (http://117.50.197.170:8849/)
3. 确保Maven可用

## 启动步骤

### 1. 编译项目
```bash
./mvnw clean compile -DskipTests
```

### 2. 启动网关服务
```bash
./mvnw spring-boot:run -pl infra/infra-gateway
```

### 3. 启动认证服务
```bash
./mvnw spring-boot:run -pl service/service-auth
```

## 服务访问地址

### 网关服务
- 网关地址: http://localhost:8080
- 认证服务路由: http://localhost:8080/auth/**
- 健康检查: http://localhost:8080/actuator/health

### 认证服务
- 直接访问: http://localhost:8081/auth
- 通过网关: http://localhost:8080/auth

## Nacos控制台
- 地址: http://117.50.197.170:8849/nacos
- 用户名: nacos
- 密码: olN2y)]VzEISLITO#_}L0J^$>Nk)1S~W

## 验证步骤
1. 访问Nacos控制台，确认服务已注册
2. 通过网关访问认证服务
3. 检查网关日志，确认路由正常工作

## Feign客户端使用

### 在其他服务中调用认证服务
```java
@Autowired
private AuthFeignClient authFeignClient;

// 调用登录接口
ResultData result = authFeignClient.login(loginRequestJson);

// 调用登出接口
ResultData result = authFeignClient.logout("Bearer " + token);
```

### 示例接口
- 登录示例: POST /feign-example/login
- 登出示例: POST /feign-example/logout

## 注意事项
- 确保所有服务的端口不冲突
- 确保网络能够访问Nacos服务器
- 首次启动可能需要较长时间下载依赖
- 使用Feign客户端时，确保目标服务已注册到Nacos 