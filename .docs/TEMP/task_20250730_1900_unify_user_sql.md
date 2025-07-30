# 任务：统一用户SQL文件管理
状态: 已完成

目标: 统一管理所有用户相关的SQL文件，删除重复的表定义，实现统一的用户和权限管理

执行步骤
- [x] 步骤 1: 检查所有用户相关的SQL文件
- [x] 步骤 2: 分析表结构重复和冲突
- [x] 步骤 3: 设计统一的用户表结构
- [x] 步骤 4: 合并和清理SQL文件
- [x] 步骤 5: 更新相关文档和配置
- [x] 步骤 6: 验证表结构一致性

## 文件分析

### 当前SQL文件
- `service/service-auth/src/main/resources/db/auth-schema.sql` - 认证服务表结构
- `service/service-auth/src/main/resources/db/init.sql` - 认证服务初始化数据
- `service/service-user/src/main/resources/db/user-schema.sql` - 用户服务表结构

### 需要解决的问题
1. 用户表重复定义
2. 权限表分散管理
3. 初始化数据重复
4. 表结构不一致

## 执行记录

### 2025-07-30 19:00
- 任务开始
- 创建任务状态文件 