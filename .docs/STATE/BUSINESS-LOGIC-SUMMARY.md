# 业务逻辑汇总文档

**创建时间**: 2025-07-30  
**最后更新**: 2025-07-31  
**版本**: 1.1  

## 📋 **目录**
- [响应数据结构](#响应数据结构)
- [链路追踪机制](#链路追踪机制)
- [API响应规范](#api响应规范)

---

## 响应数据结构

### 重构后的响应类结构
基于单一职责原则，将原有的ResultData类拆分为三个独立的类：

#### 1. ResultData - 核心响应数据结构
**位置**: `service/service-common/src/main/java/com/origin/common/ResultData.java`
**职责**: 核心响应数据结构管理
**内容**:
- 响应码、响应消息、响应数据
- 响应时间戳
- 成功/失败响应方法
- 工具方法（isSuccess、getData等）

#### 2. RequestTrace - 请求追踪类
**位置**: `service/service-common/src/main/java/com/origin/common/RequestTrace.java`
**职责**: 链路追踪和请求上下文管理
**内容**:
- 请求ID、用户ID、客户端IP、用户代理
- 请求/响应时间戳
- 服务名称、请求路径、请求方法
- 处理耗时计算
- 追踪状态管理

#### 3. ApiResponse - API响应包装类
**位置**: `service/service-common/src/main/java/com/origin/common/ApiResponse.java`
**职责**: 组合响应数据和请求追踪信息
**内容**:
- 组合ResultData和RequestTrace
- 提供统一的API响应接口
- 兼容性方法（ok、success、fail）
- 便捷的追踪信息设置方法

### 使用示例

#### 简单响应（不需要追踪）
```java
// 使用ResultData
return ResultData.success(data);

// 使用ApiResponse
return ApiResponse.success(data);
```

#### 需要追踪的响应
```java
// 创建追踪信息
RequestTrace trace = RequestTrace.create("req-123", userId, clientIp, userAgent)
    .setRequestInfo("service-auth", "/api/login", "POST", "{}");

// 使用ApiResponse
return ApiResponse.successWithTrace("登录成功", userInfo, trace);
```

#### 在拦截器中完成追踪
```java
// 在响应拦截器中
apiResponse.completeTrace(responseStatus);
```

---

## 链路追踪机制

### 追踪信息管理
- **请求ID**: 用于链路追踪的唯一标识
- **用户信息**: 用户ID、客户端IP、用户代理
- **请求信息**: 服务名称、请求路径、请求方法、请求参数
- **时间信息**: 请求时间戳、响应时间戳、处理耗时

### 追踪状态管理
- **创建阶段**: 记录请求开始时间和基本信息
- **处理阶段**: 记录请求处理过程中的关键信息
- **完成阶段**: 记录响应状态和处理耗时

---

## API响应规范

### 响应格式
```json
{
  "result": {
    "code": 200,
    "message": "SUCCESS",
    "data": {},
    "timestamp": 1640995200000
  },
  "trace": {
    "requestId": "req-123",
    "userId": 1001,
    "clientIp": "192.168.1.1",
    "userAgent": "Mozilla/5.0...",
    "requestTimestamp": 1640995200000,
    "responseTimestamp": 1640995201000,
    "duration": 1000,
    "serviceName": "service-auth",
    "requestPath": "/api/login",
    "requestMethod": "POST",
    "responseStatus": 200
  }
}
```

### 响应码规范
- **200**: 成功
- **400**: 客户端错误
- **401**: 未授权
- **403**: 禁止访问
- **500**: 服务器错误

---

## 📅 **更新历史**
- **2025-07-30**: 初始创建业务逻辑汇总文档
- **2025-07-31**: 更新响应数据结构，记录ResultData重构 