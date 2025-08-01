# core-publisher 发布者 模块设计文档

> **文档位置**: `infra/moudleDocs/core-publisher/design.md`

## 1. 模块概述

发布者模块是banyuMall系统的任务发布管理核心模块，负责各类任务的发布、审核、编辑和状态管理。

### 1.1 核心职责
1. **任务发布管理**: 支持7种不同类型任务的创建、编辑和状态管理
2. **任务审核流程**: 对社群分享任务进行审核，包括参与者信息审核和积分发放
3. **任务状态监控**: 实时展示任务参与人数和状态（正常/下架）
4. **权限控制**: 发布者和管理员可查看所有任务，普通用户查看公开任务

### 1.2 支持的任务类型

#### 1.2.1 点赞任务
- **核心字段**: 任务名称、任务描述、任务积分、任务图标
- **特点**: 最简单的任务类型，用户完成点赞即可获得积分

#### 1.2.2 评论任务
- **核心字段**: 任务名称、任务描述、任务积分、任务图标、任务说明
- **可选功能**: 
  - 是否上传图片、最少图片数量、图片地址（阿里云OSS存储，格式：List<URL>）
  - 是否填写分享链接、社群分享结果链接（格式：List<URL>）

#### 1.2.3 讨论任务
- **核心字段**: 任务名称、任务描述、任务积分、任务图标
- **特点**: 用户参与讨论获得积分

#### 1.2.4 社群分享任务
- **核心字段**: 邀请类型（入群/加微信）、任务名称、任务描述、任务积分、任务图标
- **奖励设置**: 被邀请人是否增加积分、被邀请人增加积分数量
- **活动配置**: 活动规则、二维码
- **审核要求**: 需要审核，支持按活动ID、活动名称、审核状态、用户ID、微信昵称查询

#### 1.2.5 邀请任务
- **核心字段**: 邀请类型（入群/加微信）、任务名称、任务描述、任务积分、任务图标
- **奖励设置**: 被邀请人是否增加积分、被邀请人增加积分数量
- **活动配置**: 活动规则、二维码地址

#### 1.2.6 反馈任务
- **核心字段**: 任务名称、任务描述、任务积分、任务图标
- **反馈内容**: 反馈文字或反馈URL（text字段）

#### 1.2.7 查看排行榜任务
- **核心字段**: 任务名称、任务描述、任务积分、任务图标
- **排行榜设置**: 展示积分排名前X位置

### 1.3 审核机制
- **审核范围**: 仅对社群分享任务进行审核
- **审核内容**: 参与者提交的信息审核和积分发放
- **查询条件**: 支持按活动ID、活动名称、审核状态、用户ID、微信昵称进行查询

### 1.4 接口功能列表

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | Feign | 详细说明 |
|------|----------|----------|----------|----------|----------|--------|----------|
| 1 | 创建任务 | POST | `/publisher/tasks` | 创建新的任务 | 任务发布管理 | [查看详情](#51-任务管理接口) |
| 2 | 更新任务 | PUT | `/publisher/tasks/{id}` | 更新任务信息 | 任务发布管理 | [查看详情](#51-任务管理接口) |
| 3 | 删除任务 | DELETE | `/publisher/tasks/{id}` | 删除任务 | 任务发布管理 | [查看详情](#51-任务管理接口) |
| 4 | 获取任务详情 | GET | `/publisher/tasks/{id}` | 获取任务详细信息 | 任务发布管理 | [查看详情](#51-任务管理接口) |
| 5 | 分页查询任务列表 | GET | `/publisher/tasks` | 分页查询任务列表 | 任务发布管理 | [查看详情](#51-任务管理接口) |
| 6 | 提交任务审核 | POST | `/publisher/tasks/{id}/submit` | 提交任务进行审核 | 任务审核流程 | [查看详情](#52-任务审核接口) |
| 7 | 审核任务 | POST | `/publisher/tasks/{id}/review` | 审核任务 | 任务审核流程 | [查看详情](#52-任务审核接口) |
| 8 | 获取审核历史 | GET | `/publisher/tasks/{id}/review-history` | 获取任务审核历史 | 任务审核流程 | [查看详情](#52-任务审核接口) |
| 9 | 提交社群分享审核 | POST | `/publisher/share-reviews` | 提交社群分享审核申请 | 任务审核流程 | [查看详情](#53-社群分享审核接口) |
| 10 | 审核社群分享 | PUT | `/publisher/share-reviews/{id}/review` | 审核社群分享申请 | 任务审核流程 | [查看详情](#53-社群分享审核接口) |
| 11 | 查询社群分享审核列表 | GET | `/publisher/share-reviews` | 查询社群分享审核列表 | 任务审核流程 | [查看详情](#53-社群分享审核接口) |
| 12 | 参与任务 | POST | `/publisher/tasks/{id}/participate` | 用户参与任务 | 任务状态监控 | [查看详情](#54-任务参与接口) |
| 13 | 完成任务 | POST | `/publisher/tasks/{id}/complete` | 用户完成任务 | 任务状态监控 | [查看详情](#54-任务参与接口) |
| 14 | 获取参与记录 | GET | `/publisher/tasks/{id}/participations` | 获取任务参与记录 | 任务状态监控 | [查看详情](#54-任务参与接口) |

### 1.5 技术栈
- **框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus
- **缓存**: Redis
- **认证**: JWT + Spring Security
- **文档**: Knife4j (Swagger)
- **配置中心**: Nacos
- **服务发现**: Nacos Discovery
- **服务调用**: OpenFeign
- **JSON处理**: FastJSON2
- **日志框架**: Log4j2

### 1.6 通用功能
- **任务展示**: 查看所有任务，展示参与人数和状态（正常/下架）
- **权限管理**: 发布者和管理员可查看所有任务
- **文件存储**: 图片统一存储在阿里云OSS中

## 2. 数据模型设计

### 2.1 数据库表结构
参考: [`../database/data/publisher/publisher-schema.sql`](../database/data/publisher/publisher-schema.sql)

### 2.2 核心数据表

#### 2.2.1 任务管理相关表
- **publisher_task**: 任务基础表 - 存储所有任务的基本信息
- **publisher_like_task**: 点赞任务表 - 存储点赞任务特有信息
- **publisher_comment_task**: 评论任务表 - 存储评论任务特有信息
- **publisher_discuss_task**: 讨论任务表 - 存储讨论任务特有信息
- **publisher_share_task**: 社群分享任务表 - 存储社群分享任务特有信息
- **publisher_invite_task**: 邀请任务表 - 存储邀请任务特有信息
- **publisher_feedback_task**: 反馈任务表 - 存储反馈任务特有信息
- **publisher_ranking_task**: 排行榜任务表 - 存储排行榜任务特有信息

#### 2.2.2 审核管理相关表
- **publisher_task_review**: 任务审核记录表 - 存储任务审核历史
- **publisher_share_review**: 社群分享审核表 - 存储社群分享任务的具体审核信息

#### 2.2.3 参与记录相关表
- **publisher_task_participation**: 任务参与记录表 - 存储用户参与任务的记录

### 2.3 表关系设计

#### 2.3.1 主从关系
- **publisher_task** (主表) ← **publisher_*_task** (从表)
  - 通过 `task_id` 外键关联
  - 采用 CASCADE 删除策略

#### 2.3.2 审核关系
- **publisher_task** ← **publisher_task_review**
  - 任务审核历史记录
- **publisher_share_task** ← **publisher_share_review**
  - 社群分享任务审核详情

#### 2.3.3 参与关系
- **publisher_task** ← **publisher_task_participation**
  - 用户任务参与记录
  - 唯一约束：`(task_id, user_id)`

### 2.4 关键字段说明

#### 2.4.1 任务状态字段
- **status**: 1(草稿) → 2(已提交) → 3(已审核) → 5(已发布) → 6(已下架) / 4(已拒绝)
- **review_status**: 1(待审核) → 2(已通过) / 3(已拒绝)

#### 2.4.2 任务类型字段
- **task_type**: 1(点赞) / 2(评论) / 3(讨论) / 4(社群分享) / 5(邀请) / 6(反馈) / 7(排行榜)

#### 2.4.3 邀请类型字段
- **invite_type**: 1(入群) / 2(加微信)

#### 2.4.4 审核状态字段
- **review_status**: 1(待审核) / 2(已通过) / 3(已拒绝)
- **participation_status**: 1(进行中) / 2(已完成) / 3(已失败)

## 3. 实体类设计

### 3.1 基础实体类

#### 3.1.1 任务基础实体
```java
@Data
@TableName("publisher_task")
public class PublisherTask extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_name")
    private String taskName;
    
    @TableField("task_description")
    private String taskDescription;
    
    @TableField("task_points")
    private Integer taskPoints;
    
    @TableField("task_icon")
    private String taskIcon;
    
    @TableField("task_type")
    private Integer taskType;
    
    @TableField("publisher_id")
    private String publisherId;
    
    @TableField("status")
    private Integer status;
    
    @TableField("review_status")
    private Integer reviewStatus;
    
    @TableField("review_comment")
    private String reviewComment;
    
    @TableField("participant_count")
    private Integer participantCount;
}
```

#### 3.1.2 点赞任务实体
```java
@Data
@TableName("publisher_like_task")
public class PublisherLikeTask extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
}
```

#### 3.1.3 评论任务实体
```java
@Data
@TableName("publisher_comment_task")
public class PublisherCommentTask extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("task_instruction")
    private String taskInstruction;
    
    @TableField("require_image")
    private Boolean requireImage;
    
    @TableField("min_image_count")
    private Integer minImageCount;
    
    @TableField("image_urls")
    private String imageUrls; // JSON格式存储
    
    @TableField("require_share_link")
    private Boolean requireShareLink;
    
    @TableField("share_result_urls")
    private String shareResultUrls; // JSON格式存储
}
```

#### 3.1.4 社群分享任务实体
```java
@Data
@TableName("publisher_share_task")
public class PublisherShareTask extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("invite_type")
    private Integer inviteType;
    
    @TableField("invitee_reward")
    private Boolean inviteeReward;
    
    @TableField("invitee_points")
    private Integer inviteePoints;
    
    @TableField("activity_rules")
    private String activityRules;
    
    @TableField("qr_code")
    private String qrCode;
}
```

#### 3.1.5 任务审核记录实体
```java
@Data
@TableName("publisher_task_review")
public class PublisherTaskReview extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("reviewer_id")
    private String reviewerId;
    
    @TableField("review_status")
    private Integer reviewStatus;
    
    @TableField("review_comment")
    private String reviewComment;
    
    @TableField("review_time")
    private LocalDateTime reviewTime;
    
    @TableField("review_type")
    private String reviewType;
}
```

#### 3.1.6 社群分享审核实体
```java
@Data
@TableName("publisher_share_review")
public class PublisherShareReview extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("share_task_id")
    private String shareTaskId;
    
    @TableField("activity_id")
    private String activityId;
    
    @TableField("activity_name")
    private String activityName;
    
    @TableField("user_id")
    private String userId;
    
    @TableField("wechat_nickname")
    private String wechatNickname;
    
    @TableField("review_status")
    private Integer reviewStatus;
    
    @TableField("review_comment")
    private String reviewComment;
    
    @TableField("points_awarded")
    private Integer pointsAwarded;
    
    @TableField("reviewer_id")
    private String reviewerId;
    
    @TableField("review_time")
    private LocalDateTime reviewTime;
}
```

### 3.2 枚举类设计

#### 3.2.1 任务类型枚举
```java
public enum TaskType {
    LIKE(1, "点赞任务"),
    COMMENT(2, "评论任务"),
    DISCUSS(3, "讨论任务"),
    SHARE(4, "社群分享任务"),
    INVITE(5, "邀请任务"),
    FEEDBACK(6, "反馈任务"),
    RANKING(7, "排行榜任务");
    
    private final Integer code;
    private final String description;
    
    TaskType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
}
```

#### 3.2.2 任务状态枚举
```java
public enum TaskStatus {
    DRAFT(1, "草稿"),
    SUBMITTED(2, "已提交"),
    APPROVED(3, "已审核"),
    REJECTED(4, "已拒绝"),
    PUBLISHED(5, "已发布"),
    OFFLINE(6, "已下架");
    
    private final Integer code;
    private final String description;
    
    TaskStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
}
```

## 4. DTO设计

### 4.1 请求DTO

#### 4.1.1 任务创建请求DTO
```java
@Data
public class TaskCreateRequest {
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 100, message = "任务名称长度不能超过100")
    private String taskName;
    
    @NotBlank(message = "任务描述不能为空")
    @Size(max = 1000, message = "任务描述长度不能超过1000")
    private String taskDescription;
    
    @NotNull(message = "任务积分不能为空")
    @Min(value = 1, message = "任务积分必须大于0")
    private Integer taskPoints;
    
    @NotBlank(message = "任务图标不能为空")
    private String taskIcon;
    
    @NotNull(message = "任务类型不能为空")
    private Integer taskType;
    
    // 任务类型特定字段
    private TaskSpecificData taskSpecificData;
}

@Data
public class TaskSpecificData {
    // 评论任务字段
    private String taskInstruction;
    private Boolean requireImage;
    private Integer minImageCount;
    private List<String> imageUrls;
    private Boolean requireShareLink;
    private List<String> shareResultUrls;
    
    // 社群分享任务字段
    private Integer inviteType;
    private Boolean inviteeReward;
    private Integer inviteePoints;
    private String activityRules;
    private String qrCode;
    
    // 邀请任务字段
    private String qrCodeUrl;
    
    // 反馈任务字段
    private String feedbackContent;
    
    // 排行榜任务字段
    private Integer rankingTopCount;
}
```

#### 4.1.2 任务更新请求DTO
```java
@Data
public class TaskUpdateRequest {
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 100, message = "任务名称长度不能超过100")
    private String taskName;
    
    @NotBlank(message = "任务描述不能为空")
    @Size(max = 1000, message = "任务描述长度不能超过1000")
    private String taskDescription;
    
    @NotNull(message = "任务积分不能为空")
    @Min(value = 1, message = "任务积分必须大于0")
    private Integer taskPoints;
    
    @NotBlank(message = "任务图标不能为空")
    private String taskIcon;
    
    // 任务类型特定字段
    private TaskSpecificData taskSpecificData;
}
```

#### 4.1.3 任务查询请求DTO
```java
@Data
public class TaskQueryRequest {
    private String taskName;
    private Integer taskType;
    private Integer status;
    private Integer reviewStatus;
    private String publisherId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 10;
}
```

#### 4.1.4 社群分享审核请求DTO
```java
@Data
public class ShareReviewRequest {
    @NotBlank(message = "活动ID不能为空")
    private String activityId;
    
    @NotBlank(message = "活动名称不能为空")
    private String activityName;
    
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    private String wechatNickname;
    
    @NotNull(message = "审核状态不能为空")
    private Integer reviewStatus;
    
    @NotBlank(message = "审核意见不能为空")
    @Size(max = 500, message = "审核意见长度不能超过500")
    private String reviewComment;
    
    @Min(value = 0, message = "发放积分不能小于0")
    private Integer pointsAwarded = 0;
}
```

### 4.2 响应DTO

#### 4.2.1 任务详情响应DTO
```java
@Data
public class TaskDetailResponse {
    private String id;
    private String taskName;
    private String taskDescription;
    private Integer taskPoints;
    private String taskIcon;
    private Integer taskType;
    private String publisherId;
    private Integer status;
    private Integer reviewStatus;
    private String reviewComment;
    private Integer participantCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 任务类型特定数据
    private TaskSpecificResponse taskSpecificData;
}

@Data
public class TaskSpecificResponse {
    // 评论任务数据
    private String taskInstruction;
    private Boolean requireImage;
    private Integer minImageCount;
    private List<String> imageUrls;
    private Boolean requireShareLink;
    private List<String> shareResultUrls;
    
    // 社群分享任务数据
    private Integer inviteType;
    private Boolean inviteeReward;
    private Integer inviteePoints;
    private String activityRules;
    private String qrCode;
    
    // 邀请任务数据
    private String qrCodeUrl;
    
    // 反馈任务数据
    private String feedbackContent;
    
    // 排行榜任务数据
    private Integer rankingTopCount;
}
```

#### 4.2.2 任务列表响应DTO
```java
@Data
public class TaskListResponse {
    private String id;
    private String taskName;
    private Integer taskPoints;
    private String taskIcon;
    private Integer taskType;
    private Integer status;
    private Integer reviewStatus;
    private Integer participantCount;
    private LocalDateTime createTime;
    private String publisherName;
}
```

## 5. 接口设计

### 5.1 任务管理接口 {#51-任务管理接口}

#### 5.1.1 创建任务
- **接口路径**: `POST /api/publisher/tasks`
- **功能描述**: 创建新的任务
- **请求头**:
  ```
  Content-Type: application/json
  Authorization: Bearer {token}
  ```
- **请求参数**: TaskCreateRequest
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "task_001",
      "taskName": "点赞任务",
      "status": 1,
      "createTime": "2024-01-15T10:30:00"
    }
  }
  ```

#### 5.1.2 更新任务
- **接口路径**: `PUT /api/publisher/tasks/{id}`
- **功能描述**: 更新任务信息
- **请求参数**: TaskUpdateRequest
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "task_001",
      "taskName": "更新后的任务名称",
      "updateTime": "2024-01-15T11:30:00"
    }
  }
  ```

#### 5.1.3 删除任务
- **接口路径**: `DELETE /api/publisher/tasks/{id}`
- **功能描述**: 删除任务
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 5.1.4 获取任务详情
- **接口路径**: `GET /api/publisher/tasks/{id}`
- **功能描述**: 获取任务详细信息
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "task_001",
      "taskName": "点赞任务",
      "taskDescription": "完成点赞获得积分",
      "taskPoints": 10,
      "taskIcon": "icon_url",
      "taskType": 1,
      "status": 5,
      "participantCount": 100,
      "taskSpecificData": {
        // 任务类型特定数据
      }
    }
  }
  ```

#### 5.1.5 分页查询任务列表
- **接口路径**: `GET /api/publisher/tasks`
- **功能描述**: 分页查询任务列表
- **查询参数**: TaskQueryRequest
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "records": [
        {
          "id": "task_001",
          "taskName": "点赞任务",
          "taskPoints": 10,
          "taskType": 1,
          "status": 5,
          "participantCount": 100
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1,
      "pages": 1
    }
  }
  ```

### 5.2 任务审核接口 {#52-任务审核接口}

#### 5.2.1 提交任务审核
- **接口路径**: `POST /api/publisher/tasks/{id}/submit`
- **功能描述**: 提交任务进行审核
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "task_001",
      "status": 2,
      "reviewStatus": 1
    }
  }
  ```

#### 5.2.2 审核任务
- **接口路径**: `POST /api/publisher/tasks/{id}/review`
- **功能描述**: 审核任务
- **请求参数**:
  ```json
  {
    "reviewStatus": 2,
    "reviewComment": "审核通过"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "task_001",
      "status": 3,
      "reviewStatus": 2
    }
  }
  ```

#### 5.2.3 获取审核历史
- **接口路径**: `GET /api/publisher/tasks/{id}/review-history`
- **功能描述**: 获取任务审核历史
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "id": "review_001",
        "reviewerId": "admin_001",
        "reviewStatus": 2,
        "reviewComment": "审核通过",
        "reviewTime": "2024-01-15T13:30:00"
      }
    ]
  }
  ```

### 5.3 社群分享审核接口 {#53-社群分享审核接口}

#### 5.3.1 提交社群分享审核
- **接口路径**: `POST /api/publisher/share-reviews`
- **功能描述**: 提交社群分享审核申请
- **请求参数**: ShareReviewRequest
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "share_review_001",
      "reviewStatus": 1
    }
  }
  ```

#### 5.3.2 审核社群分享
- **接口路径**: `PUT /api/publisher/share-reviews/{id}/review`
- **功能描述**: 审核社群分享申请
- **请求参数**:
  ```json
  {
    "reviewStatus": 2,
    "reviewComment": "审核通过",
    "pointsAwarded": 50
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "share_review_001",
      "reviewStatus": 2,
      "pointsAwarded": 50
    }
  }
  ```

#### 5.3.3 查询社群分享审核列表
- **接口路径**: `GET /api/publisher/share-reviews`
- **功能描述**: 查询社群分享审核列表
- **查询参数**:
  - `activityId`: 活动ID
  - `activityName`: 活动名称
  - `reviewStatus`: 审核状态
  - `userId`: 用户ID
  - `wechatNickname`: 微信昵称
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "records": [
        {
          "id": "share_review_001",
          "activityId": "activity_001",
          "activityName": "测试活动",
          "userId": "user_001",
          "wechatNickname": "测试用户",
          "reviewStatus": 1,
          "createTime": "2024-01-15T10:30:00"
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1,
      "pages": 1
    }
  }
  ```

### 5.4 任务参与接口 {#54-任务参与接口}

#### 5.4.1 参与任务
- **接口路径**: `POST /api/publisher/tasks/{id}/participate`
- **功能描述**: 用户参与任务
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "participation_001",
      "participationStatus": 1
    }
  }
  ```

#### 5.4.2 完成任务
- **接口路径**: `POST /api/publisher/tasks/{id}/complete`
- **功能描述**: 用户完成任务
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "participation_001",
      "participationStatus": 2,
      "pointsEarned": 10
    }
  }
  ```

#### 5.4.3 获取参与记录
- **接口路径**: `GET /api/publisher/tasks/{id}/participations`
- **功能描述**: 获取任务参与记录
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "records": [
        {
          "id": "participation_001",
          "userId": "user_001",
          "participationStatus": 2,
          "pointsEarned": 10,
          "completionTime": "2024-01-15T14:30:00"
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1,
      "pages": 1
    }
  }
  ```

## 6. 技术实现

### 6.1 技术栈
- **后端框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **ORM框架**: MyBatis-Plus
- **缓存**: Redis
- **认证授权**: JWT + Spring Security
- **API文档**: Knife4j (Swagger)
- **配置中心**: Nacos
- **服务发现**: Nacos Discovery
- **参数验证**: Jakarta Validation
- **日志框架**: Log4j2

### 6.2 核心配置
- **数据库连接池**: HikariCP
- **缓存策略**: Redis + 本地缓存
- **事务管理**: Spring声明式事务
- **异常处理**: 全局异常处理器
- **跨域配置**: CORS配置

### 6.3 缓存策略
- **任务信息缓存**: TTL 30分钟
- **审核记录缓存**: TTL 1小时
- **参与记录缓存**: TTL 10分钟
- **配置信息缓存**: TTL 2小时
- **缓存更新策略**: 写入时更新缓存

### 6.4 项目结构
```
src/main/java/com/origin/publisher/
├── config/           # 配置类
├── controller/       # 控制器
├── dto/             # 数据传输对象
├── entity/          # 实体类
├── feign/           # Feign客户端
├── interceptor/     # 拦截器
├── mapper/          # 数据访问层
├── service/         # 业务逻辑层
│   └── impl/        # 业务逻辑实现
└── util/            # 工具类
```

### 6.5 依赖管理

#### 6.5.1 基础依赖包
```xml
<!-- 基础配置包 - Spring Boot公共配置 -->
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-base</artifactId>
    <version>${project.version}</version>
</dependency>

<!-- 公共工具包 - 静态方法和工具类 -->
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

#### 6.5.2 核心依赖
```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>
    
    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Knife4j -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Nacos -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    
    <!-- OpenFeign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    
    <!-- FastJSON2 -->
    <dependency>
        <groupId>com.alibaba.fastjson2</groupId>
        <artifactId>fastjson2</artifactId>
    </dependency>
    
    <!-- Log4j2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
</dependencies>
```

### 6.6 依赖关系
- **service-base**: Spring Boot公共配置（MyBatis-Plus、Redis、Knife4j等配置）
- **service-common**: 公共组件和工具类（ResultData、BaseEntity、BusinessException等）
- **service-auth**: 认证服务（通过Feign调用）
- **service-user**: 用户服务（通过Feign调用获取用户信息）
- **service-gateway**: 网关服务（路由转发）

## 7. 功能特性

### 7.1 任务发布管理
- 支持7种不同类型任务的创建、编辑和删除
- 支持任务状态流转管理
- 支持任务图片和文件管理
- 支持批量操作功能

### 7.2 任务审核流程
- 支持任务审核提交和审批
- 支持审核意见记录
- 支持审核历史查询
- 支持审核状态通知
- 支持审核权限控制

### 7.3 社群分享审核
- 支持社群分享任务专项审核
- 支持多维度查询条件
- 支持积分发放管理
- 支持审核结果通知

### 7.4 任务参与管理
- 支持用户任务参与记录
- 支持任务完成状态跟踪
- 支持积分奖励发放
- 支持参与数据统计

### 7.5 安全特性
- **权限控制**: 基于用户角色和权限的访问控制
- **数据验证**: 请求参数和服务端数据验证
- **审计日志**: 关键操作记录审计日志
- **数据脱敏**: 敏感信息展示时进行脱敏处理
- **内容审核**: 自动检测违禁内容

## 8. 后续优化计划

### 8.1 性能优化
- [ ] 实现任务列表分页缓存
- [ ] 优化图片上传和存储策略
- [ ] 实现统计数据预计算
- [ ] 优化数据库查询性能

### 8.2 功能增强
- [ ] 支持任务模板功能
- [ ] 实现任务批量导入导出
- [ ] 支持任务版本管理
- [ ] 实现任务推荐算法
- [ ] 支持任务评论管理

### 8.3 安全加固
- [ ] 实现内容安全检测
- [ ] 加强权限控制粒度
- [ ] 实现操作日志审计
- [ ] 支持数据加密存储

