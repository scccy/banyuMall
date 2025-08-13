# 分享审核Excel导出功能说明

## 功能概述
为发布者模块添加了分享审核列表的Excel导出功能，支持多条件查询并导出所有匹配的数据（不分页）。

## 技术实现
- **框架**: 使用EasyExcel 4.0.3版本
- **导出格式**: Excel 2007+ (.xlsx)
- **编码**: UTF-8
- **响应方式**: 流式下载，避免内存溢出

## API接口

### 导出分享审核列表
- **接口路径**: `POST /core/publisher/share/reviews/export`
- **请求方式**: POST
- **Content-Type**: application/json

#### 请求参数
```json
{
  "taskId": "TASK001",           // 可选：任务ID
  "taskName": "测试任务",         // 可选：任务名称（模糊匹配）
  "reviewStatus": 1,             // 可选：审核状态（1-待审核,2-通过,3-拒绝）
  "userId": "USER001",           // 可选：用户ID
  "wechatNickname": "测试用户"    // 可选：微信昵称（模糊匹配）
}
```

#### 响应说明
- **成功**: 直接下载Excel文件
- **失败**: 返回错误信息文本

#### 文件名规则
导出的Excel文件名会根据查询条件自动生成，格式如下：
```
分享审核列表_[条件1]_[条件2]_[时间戳].xlsx
```

示例：
- `分享审核列表_任务TASK001_待审核_1703123456789.xlsx`
- `分享审核列表_测试任务_已通过_1703123456789.xlsx`

## Excel列结构

| 序号 | 列名 | 字段 | 说明 |
|------|------|------|------|
| 0 | 分享审核ID | shareReviewId | 唯一标识 |
| 1 | 任务ID | taskId | 关联的任务ID |
| 2 | 提交用户ID | userId | 提交审核的用户ID |
| 3 | 微信昵称 | wechatNickname | 用户的微信昵称 |
| 4 | 分享内容 | shareContent | 分享的文字内容 |
| 5 | 分享平台 | sharePlatform | 分享的平台名称 |
| 6 | 分享链接 | shareUrl | 多个链接用分号分隔 |
| 7 | 截图URL | screenshotUrl | 多个图片URL用分号分隔 |
| 8 | 审核状态 | reviewStatus | 中文状态描述 |
| 9 | 审核意见 | reviewComment | 审核人员的意见 |
| 10 | 创建时间 | createdTime | 格式：yyyy-MM-dd HH:mm:ss |

## 使用示例

### 1. 导出所有数据
```bash
curl -X POST "http://localhost:8080/core/publisher/share/reviews/export" \
  -H "Content-Type: application/json" \
  -d '{}' \
  --output "分享审核列表.xlsx"
```

### 2. 导出特定任务的数据
```bash
curl -X POST "http://localhost:8080/core/publisher/share/reviews/export" \
  -H "Content-Type: application/json" \
  -d '{"taskId": "TASK001"}' \
  --output "分享审核列表_任务TASK001.xlsx"
```

### 3. 导出待审核的数据
```bash
curl -X POST "http://localhost:8080/core/publisher/share/reviews/export" \
  -H "Content-Type: application/json" \
  -d '{"reviewStatus": 1}' \
  --output "分享审核列表_待审核.xlsx"
```

### 4. 组合条件查询
```bash
curl -X POST "http://localhost:8080/core/publisher/share/reviews/export" \
  -H "Content-Type: application/json" \
  -d '{
    "taskName": "测试任务",
    "reviewStatus": 2,
    "wechatNickname": "测试用户"
  }' \
  --output "分享审核列表_测试任务_已通过_测试用户.xlsx"
```

## 注意事项

1. **数据量**: 导出功能会查询所有匹配条件的数据，大数据量时请谨慎使用
2. **权限控制**: 建议在生产环境中添加适当的权限验证
3. **超时设置**: 大数据量导出时可能需要调整HTTP超时时间
4. **内存使用**: 使用EasyExcel的流式写入，避免内存溢出
5. **并发限制**: 建议限制同时导出的用户数量

## 错误处理

### 常见错误
- **500 Internal Server Error**: 服务器内部错误，检查日志获取详细信息
- **文件下载失败**: 检查网络连接和服务器状态
- **数据为空**: 确认查询条件是否正确

### 日志查看
导出过程中的关键日志：
```
INFO  - 导出分享审核列表请求: [请求参数]
INFO  - 分享审核列表导出成功，共导出X条记录
ERROR - 导出分享审核列表失败: [错误信息]
```

## 相关文件
- `PublisherTaskShareReviewController.java` - 控制器，包含导出接口
- `PublisherTaskShareReviewService.java` - 服务接口
- `PublisherTaskTaskShareReviewServiceImpl.java` - 服务实现
- `PublisherShareReviewMapper.java` - 数据访问层
- `ShareReviewExportDTO.java` - 导出数据转换对象
- `PublisherShareReviewMapper.xml` - MyBatis映射文件

## 更新历史
- **2025-08-13**: 初始版本，实现基本的Excel导出功能
- 支持多条件查询
- 智能文件名生成
- 完整的错误处理
