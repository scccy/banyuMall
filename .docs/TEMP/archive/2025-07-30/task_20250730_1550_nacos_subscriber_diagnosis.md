# 任务：Nacos订阅者列表诊断
状态: 执行中

目标: 诊断为什么Nacos订阅者列表为空，排查服务注册问题

## 问题描述
Nacos订阅者列表为空，服务可能没有成功注册到Nacos。

## 可能的原因分析

### 1. 服务未启动
- 服务可能没有启动
- 服务启动失败
- 服务启动但注册失败

### 2. 网络连接问题
- 无法连接到Nacos服务器
- 网络超时
- 防火墙阻止

### 3. 配置问题
- 命名空间ID不正确
- 用户名密码错误
- 服务器地址错误

### 4. 服务注册问题
- 服务注册被禁用
- 注册超时
- 心跳失败

## 诊断步骤
[ ] 步骤 1: 检查服务是否正在运行
[ ] 步骤 2: 检查网络连接
[ ] 步骤 3: 验证配置正确性
[ ] 步骤 4: 检查服务启动日志
[ ] 步骤 5: 手动测试Nacos连接

## 当前配置状态

### 命名空间配置
- **Namespace ID**: `a341fa3f-beb6-434f-adbc-98c13249bfd7`
- **Group**: `DEFAULT_GROUP`
- **Server Address**: `117.50.197.170:8849`

### 服务配置
- **infra-gateway**: 端口 8080
- **service-auth**: 端口 8081

## 排查建议

### 1. 检查服务状态
```bash
# 检查服务是否在运行
ps aux | grep java
# 或者检查端口占用
netstat -tlnp | grep 8080
netstat -tlnp | grep 8081
```

### 2. 检查网络连接
```bash
# 测试Nacos服务器连接
telnet 117.50.197.170 8849
# 或者使用curl测试
curl -X GET "http://117.50.197.170:8849/nacos/v1/ns/operator/metrics"
```

### 3. 检查服务日志
- 查看服务启动日志
- 查找Nacos相关的错误信息
- 检查服务注册相关的日志

### 4. 验证配置
- 确认命名空间ID正确
- 确认用户名密码正确
- 确认服务器地址正确

## 常见解决方案

### 1. 重启服务
```bash
# 重启gateway服务
cd infra/infra-gateway
mvn spring-boot:run

# 重启auth服务
cd service/service-auth
mvn spring-boot:run
```

### 2. 检查Nacos控制台
- 确认命名空间存在
- 确认配置正确
- 检查服务列表

### 3. 临时使用public命名空间
如果问题持续，可以临时使用public命名空间进行测试：
```yaml
namespace: public
``` 