# 模块设计文档使用示例

## 📋 模板使用说明

本文档展示了如何使用 `MODULE_DESIGN_TEMPLATE.md` 模板来创建模块设计文档。

## 🎯 填写要点

### 1. 基本信息填写
```markdown
- **模块名称**: service-user
- **模块类型**: 微服务模块
- **主要职责**: 用户管理、用户信息维护、用户档案管理
- **技术栈**: Spring Boot + MyBatis Plus + Redis + Feign
- **作者**: scccy
- **创建时间**: 2025-01-27
```

### 2. Feign客户端列表填写示例

#### 依赖的外部服务
| 服务名称 | Feign客户端 | 主要用途 | 接口路径 |
|----------|-------------|----------|----------|
| service-auth | AuthFeignClient | 用户认证、权限验证 | `/service/auth` |
| third-party-oss | OssFileFeignClient | 文件上传、头像管理 | `/service/oss` |

#### Feign客户端接口示例
```java
@FeignClient(name = "service-auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    
    // 验证用户权限
    @GetMapping("/service/auth/permission/check")
    ResultData<Boolean> checkPermission(@RequestParam String userId, @RequestParam Integer userType);
    
    // 获取用户信息
    @GetMapping("/service/auth/user/info/{userId}")
    ResultData<UserInfoResponse> getUserInfo(@PathVariable String userId);
}

@FeignClient(name = "third-party-oss", fallback = OssFileFeignClientFallback.class)
public interface OssFileFeignClient {
    
    // 上传文件
    @PostMapping("/service/oss/file/upload")
    ResultData<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file);
    
    // 删除文件
    @DeleteMapping("/service/oss/file/delete/{fileId}")
    ResultData<String> deleteFile(@PathVariable String fileId);
}
```

### 3. 接口说明填写示例
| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户注册 | POST | `/service/user/register` | 新用户注册 |
| 用户登录 | POST | `/service/user/login` | 用户登录验证 |
| 获取用户信息 | GET | `/service/user/info/{userId}` | 获取用户详细信息 |
| 更新用户信息 | PUT | `/service/user/info/{userId}` | 更新用户基本信息 |
| 上传头像 | POST | `/service/user/avatar/{userId}` | 上传用户头像 |
| 删除用户 | DELETE | `/service/user/{userId}` | 删除用户账号 |

### 4. 业务流程填写示例
```
1. 用户注册流程
   1. 客户端发送注册请求
      ↓
   2. 验证用户输入信息
      ↓
   3. 检查用户名是否已存在
      ↓
   4. 调用认证服务创建用户
      ↓
   5. 返回注册成功响应

2. 用户信息更新流程
   1. 客户端发送更新请求
      ↓
   2. 验证用户权限
      ↓
   3. 更新用户基本信息
      ↓
   4. 如果有头像更新，调用OSS服务
      ↓
   5. 返回更新成功响应
```

### 5. 监控指标填写示例
#### 业务指标
- **用户注册成功率**: 注册成功次数/总注册次数
- **用户信息更新频率**: 每日用户信息更新次数
- **活跃用户数**: 当前在线用户数量

#### 外部服务指标
- **认证服务调用成功率**: Auth服务调用成功次数/总调用次数
- **OSS服务响应时间**: 文件上传的平均响应时间
- **Feign降级次数**: 外部服务不可用时的降级处理次数

## 🔧 模板使用步骤

### 第一步：复制模板
```bash
cp .docs/PROCESS/TEMPLATES/MODULE_DESIGN_TEMPLATE.md zinfra/moudleDocs/[模块名]/模块设计.md
```

### 第二步：替换占位符
使用文本编辑器的查找替换功能，批量替换以下占位符：
- `[模块名称]` → 实际模块名称
- `[模块包名]` → 实际的包名
- `[Controller]` → 实际的控制器类名
- `[Service]` → 实际的服务类名
- `[Entity]` → 实际的实体类名
- `[FeignClient]` → 实际的Feign客户端类名

### 第三步：填写具体内容
1. **基本信息**: 填写模块的基本信息
2. **架构设计**: 绘制实际的架构图
3. **目录结构**: 列出实际的目录结构
4. **核心组件**: 填写实际的接口和方法
5. **Feign客户端**: 列出所有依赖的外部服务
6. **数据模型**: 填写实际的实体和DTO
7. **业务流程**: 描述实际的业务流程
8. **配置管理**: 填写实际的配置信息

### 第四步：验证完整性
检查以下内容是否完整：
- [ ] 所有占位符都已替换
- [ ] Feign客户端列表完整
- [ ] 接口说明详细
- [ ] 业务流程清晰
- [ ] 配置信息准确

## 📝 注意事项

### 1. Feign客户端文档化
- **必须列出所有依赖的外部服务**
- **必须包含Feign接口和降级处理**
- **必须说明每个服务的主要用途**

### 2. 接口文档规范
- **使用统一的路径格式**: `/service/[entity]/[action]`
- **包含完整的HTTP方法信息**
- **提供清晰的接口说明**

### 3. 业务流程描述
- **使用流程图格式**
- **步骤清晰明确**
- **包含异常处理说明**

### 4. 配置管理
- **遵循配置分离原则**
- **使用环境变量管理敏感信息**
- **提供配置说明文档**

---

**示例版本**: v1.0  
**创建时间**: 2025-01-27  
**维护人员**: scccy 