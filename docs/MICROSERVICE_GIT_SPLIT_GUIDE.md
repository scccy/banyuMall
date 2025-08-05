# 微服务Git仓库拆分操作指南

## 概述

本指南详细说明了如何将当前的全局Git仓库拆分为独立的微服务仓库，实现每个微服务的独立版本管理。

## 拆分目标

将以下7个微服务模块拆分为独立的Git仓库：

| 微服务名称 | 本地路径 | 远程仓库地址 |
|-----------|---------|-------------|
| service-common | service/service-common | http://117.50.221.113:8077/banyu/service-common.git |
| service-base | service/service-base | http://117.50.221.113:8077/banyu/service-base.git |
| service-auth | service/service-auth | http://117.50.221.113:8077/banyu/service-auth.git |
| service-gateway | service/service-gateway | http://117.50.221.113:8077/banyu/service-gateway.git |
| service-user | service/service-user | http://117.50.221.113:8077/banyu/service-user.git |
| core-publisher | core/core-publisher | http://117.50.221.113:8077/banyu/core-publisher.git |
| third-party-aliyunOss | third-party/third-party-aliyunOss | http://117.50.221.113:8077/banyu/aliyun-oss.git |

## 前置条件

### 1. 远程仓库准备
确保所有远程仓库已在GitLab上创建：
- 仓库名称与微服务名称一致
- 设置为空仓库（不要初始化README）
- 默认分支设置为 `main`

### 2. 权限验证
确保当前用户有推送权限到所有远程仓库：
```bash
# 测试推送权限
git ls-remote http://117.50.221.113:8077/banyu/service-common.git
```

### 3. 当前状态确认
确保当前项目状态：
- 所有代码已提交到当前仓库
- 没有未提交的更改
- 当前在main分支

## 执行步骤

### 步骤1: 检查拆分计划
```bash
./scripts/split_microservice_repos_execute.sh --plan
```

### 步骤2: 验证配置
```bash
./scripts/split_microservice_repos_execute.sh --check
```

### 步骤3: 执行拆分
```bash
./scripts/split_microservice_repos_execute.sh
```

## 脚本功能说明

### 主要功能
1. **自动备份**: 创建项目完整备份
2. **目录复制**: 将各微服务目录复制到临时位置
3. **Git初始化**: 为每个微服务初始化独立的Git仓库
4. **远程推送**: 将代码推送到对应的远程仓库
5. **状态报告**: 显示拆分进度和结果

### 安全措施
- 自动创建项目备份
- 使用临时目录进行拆分操作
- 错误时立即停止执行
- 详细的日志输出

## 执行过程

### 1. 备份阶段
```
[INFO] 创建项目备份到: ../banyuMall-backup-20250127_1430
[SUCCESS] 备份完成: ../banyuMall-backup-20250127_1430
```

### 2. 拆分阶段
对每个微服务执行：
```
[INFO] 开始拆分微服务: service-common
[INFO] 复制 service/service-common 到临时目录
[INFO] 初始化Git仓库
[INFO] 添加远程仓库: http://117.50.221.113:8077/banyu/service-common.git
[INFO] 推送到远程仓库
[SUCCESS] service-common 拆分完成
```

### 3. 完成报告
```
[SUCCESS] 微服务拆分完成！
[INFO] 成功拆分: 7/7
[INFO] 备份位置: ../banyuMall-backup-20250127_1430
[INFO] 临时目录: ../temp-microservices-20250127_1430
```

## 验证结果

### 1. 检查远程仓库
访问各远程仓库，确认：
- 代码已成功推送
- 提交历史正确
- 分支名称为main

### 2. 验证文件完整性
检查每个微服务仓库是否包含：
- 完整的源代码
- pom.xml文件
- .gitignore文件
- 配置文件

### 3. 测试克隆
```bash
# 测试克隆各微服务仓库
git clone http://117.50.221.113:8077/banyu/service-common.git
git clone http://117.50.221.113:8077/banyu/service-base.git
# ... 其他微服务
```

## 后续操作

### 1. 清理当前仓库
拆分完成后，可以删除当前全局仓库的Git信息：
```bash
# 删除当前仓库的Git信息
rm -rf .git
```

### 2. 创建独立开发目录
为每个微服务创建独立的开发目录：
```bash
mkdir -p ../banyuMall-microservices
cd ../banyuMall-microservices

# 克隆各微服务
git clone http://117.50.221.113:8077/banyu/service-common.git
git clone http://117.50.221.113:8077/banyu/service-base.git
git clone http://117.50.221.113:8077/banyu/service-auth.git
git clone http://117.50.221.113:8077/banyu/service-gateway.git
git clone http://117.50.221.113:8077/banyu/service-user.git
git clone http://117.50.221.113:8077/banyu/core-publisher.git
git clone http://117.50.221.113:8077/banyu/aliyun-oss.git
```

### 3. 更新文档
- 更新README.md
- 更新项目文档
- 更新CI/CD配置

## 故障排除

### 常见问题

#### 1. 推送权限错误
```
[ERROR] 推送失败: 权限不足
```
**解决方案**: 检查GitLab用户权限，确保有推送权限

#### 2. 远程仓库不存在
```
[ERROR] 远程仓库不存在
```
**解决方案**: 先在GitLab上创建对应的空仓库

#### 3. 网络连接问题
```
[ERROR] 网络连接超时
```
**解决方案**: 检查网络连接，重试操作

### 回滚操作
如果拆分过程中出现问题，可以使用备份恢复：
```bash
# 恢复项目备份
cp -r ../banyuMall-backup-20250127_1430/* .
```

## 注意事项

1. **备份重要性**: 拆分前会自动创建备份，请保留备份文件
2. **网络稳定性**: 确保网络连接稳定，避免推送中断
3. **权限确认**: 确保有所有远程仓库的推送权限
4. **时间安排**: 拆分过程可能需要几分钟，请耐心等待
5. **验证步骤**: 拆分完成后务必验证各仓库的完整性

## 联系信息

- **作者**: scccy
- **创建时间**: 2025-01-27
- **脚本位置**: `scripts/split_microservice_repos_execute.sh`

---

**重要提醒**: 此操作不可逆，请确保在拆分前已备份重要数据。 