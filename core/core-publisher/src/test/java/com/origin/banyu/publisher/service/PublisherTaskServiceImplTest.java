package com.origin.banyu.publisher.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;
import com.origin.banyu.publisher.dto.request.TaskCreateRequest;
import com.origin.banyu.publisher.dto.request.TaskListRequest;
import com.origin.banyu.publisher.dto.request.TaskUpdateRequest;
import com.origin.banyu.publisher.dto.response.TaskDetailResponse;
import com.origin.banyu.publisher.dto.response.TaskListResponse;
import com.origin.banyu.publisher.entity.PublisherTask;
import com.origin.banyu.publisher.entity.PublisherTaskDetail;
import com.origin.banyu.publisher.mapper.PublisherTaskCompletionMapper;
import com.origin.banyu.publisher.mapper.PublisherTaskDetailMapper;
import com.origin.banyu.publisher.mapper.PublisherTaskMapper;
import com.origin.banyu.publisher.service.impl.PublisherTaskServiceImpl;
import com.origin.banyu.publisher.util.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 服务层集成业务流测试（基于模拟的持久层）
 * - 覆盖7种 taskTypeId 的 创建/查询/更新/删除 流程
 * - 不引入额外配置和环境依赖
 */
class PublisherTaskServiceImplTest {

    @Mock
    private PublisherTaskMapper taskMapper;
    @Mock
    private PublisherTaskDetailMapper taskDetailMapper;
    @Mock
    private PublisherTaskCompletionMapper taskCompletionMapper;

    // 使用真实的解析与校验，避免纯粹桩实现，贴近业务

    private TaskValidator taskValidator;

    @InjectMocks
    private PublisherTaskServiceImpl publisherTaskService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        this.taskValidator = new TaskValidator();
        // 手动注入真实工具类
        this.publisherTaskService = new PublisherTaskServiceImpl(
                taskMapper, taskDetailMapper, taskCompletionMapper,
                taskValidator
        );
    }

    @DisplayName("创建任务 - 7种任务类型全部成功")
    @ParameterizedTest(name = "taskTypeId={0}")
    @MethodSource("taskTypes")
    void createTask_allTypes_success(Integer taskTypeId) {
        TaskCreateRequest req = baseCreateRequest(taskTypeId, buildConfig(taskTypeId));

        // 拦截 insert 行为，补充生成的ID，模拟 MyBatis 自动ID
        doAnswer(invocation -> {
            PublisherTask arg = invocation.getArgument(0);
            arg.setTaskId("task_" + taskTypeId + "_001");
            return 1;
        }).when(taskMapper).insert(any(PublisherTask.class));

        doAnswer(invocation -> {
            PublisherTaskDetail detail = invocation.getArgument(0);
            detail.setDetailId("detail_" + taskTypeId + "_001");
            return 1;
        }).when(taskDetailMapper).insert(any(PublisherTaskDetail.class));

        String taskId = publisherTaskService.createTask(req);
        assertThat(taskId).isEqualTo("task_" + taskTypeId + "_001");

        // 校验主表 insert 参数
        ArgumentCaptor<PublisherTask> taskCaptor = ArgumentCaptor.forClass(PublisherTask.class);
        verify(taskMapper, times(1)).insert(taskCaptor.capture());
        PublisherTask inserted = taskCaptor.getValue();
        assertEquals(req.getTaskName(), inserted.getTaskName());
        assertEquals(req.getTaskTypeId(), inserted.getTaskTypeId());
        assertNotNull(inserted.getCreatedTime());
        assertNotNull(inserted.getUpdatedTime());

        // 校验详情 insert 参数与 JSON 序列化
        ArgumentCaptor<PublisherTaskDetail> detailCaptor = ArgumentCaptor.forClass(PublisherTaskDetail.class);
        verify(taskDetailMapper, times(1)).insert(detailCaptor.capture());
        PublisherTaskDetail detailInserted = detailCaptor.getValue();
        assertEquals("task_" + taskTypeId + "_001", detailInserted.getTaskId());
        assertThat(detailInserted.getTaskConfig()).isNotBlank();
    }

    @Test
    @DisplayName("创建任务 - 参数非法触发业务异常")
    void createTask_invalidParams_throw() {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTaskName("");
        req.setTaskTypeId(9); // 非法
        req.setTaskReward(new BigDecimal("0"));

        BusinessException ex = assertThrows(BusinessException.class, () -> publisherTaskService.createTask(req));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PARAMS_ERROR);
    }

    @Test
    @DisplayName("查询任务详情 - 读取并解析配置")
    void getTaskDetail_success() {
        String taskId = "task_1_001";
        PublisherTask mockTask = new PublisherTask();
        mockTask.setTaskId(taskId);
        mockTask.setTaskName("点赞任务");
        mockTask.setTaskTypeId(1);
        when(taskMapper.selectById(taskId)).thenReturn(mockTask);

        PublisherTaskDetail mockDetail = new PublisherTaskDetail();
        mockDetail.setTaskId(taskId);
        mockDetail.setTaskConfig(JSON.toJSONString(buildConfig(1)));
        when(taskDetailMapper.selectByTaskId(taskId)).thenReturn(mockDetail);

        TaskDetailResponse resp = publisherTaskService.getTaskDetail(taskId);
        assertThat(resp.getTaskId()).isEqualTo(taskId);
        assertThat(resp.getTaskConfig()).isNotNull();
        assertThat(resp.getTaskConfig()).contains("targetType");
    }

    @Test
    @DisplayName("更新任务 - 覆盖名称与配置")
    void updateTask_success() {
        String taskId = "task_2_001";
        PublisherTask existing = new PublisherTask();
        existing.setTaskId(taskId);
        existing.setTaskTypeId(2); // 评论任务
        when(taskMapper.selectById(taskId)).thenReturn(existing);

        PublisherTaskDetail existingDetail = new PublisherTaskDetail();
        existingDetail.setDetailId("detail_2_001");
        existingDetail.setTaskId(taskId);
        when(taskDetailMapper.selectByTaskId(taskId)).thenReturn(existingDetail);

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setTaskName("更新后的评论任务");
        Map<String, Object> newCfg = new HashMap<>();
        newCfg.put("targetType", "comment");
        newCfg.put("minCommentLength", 20);
        // 将Map配置转换为JSON字符串
        req.setTaskConfig(JSON.toJSONString(newCfg));

        publisherTaskService.updateTask(taskId, req);

        // 验证主表更新
        ArgumentCaptor<PublisherTask> updateTaskCaptor = ArgumentCaptor.forClass(PublisherTask.class);
        verify(taskMapper, times(1)).updateById(updateTaskCaptor.capture());
        PublisherTask updatedTask = updateTaskCaptor.getValue();
        assertThat(updatedTask.getTaskId()).isEqualTo(taskId);
        assertThat(updatedTask.getTaskName()).isEqualTo("更新后的评论任务");

        // 验证详情更新
        ArgumentCaptor<PublisherTaskDetail> updateDetailCaptor = ArgumentCaptor.forClass(PublisherTaskDetail.class);
        verify(taskDetailMapper, times(1)).updateById(updateDetailCaptor.capture());
        PublisherTaskDetail updatedDetail = updateDetailCaptor.getValue();
        assertThat(updatedDetail.getDetailId()).isEqualTo("detail_2_001");
        assertThat(updatedDetail.getTaskConfig()).contains("minCommentLength");
    }

    @Test
    @DisplayName("删除任务 - 同步删除详情")
    void deleteTask_success() {
        String taskId = "task_3_001";
        PublisherTask existing = new PublisherTask();
        existing.setTaskId(taskId);
        when(taskMapper.selectById(taskId)).thenReturn(existing);

        publisherTaskService.deleteTask(taskId);

        verify(taskMapper, times(1)).deleteById(taskId);
        verify(taskDetailMapper, times(1)).delete(any());
    }

    @DisplayName("七种任务类型 - 独立配置的完整CRUD流程")
    @ParameterizedTest(name = "CRUD all for taskTypeId={0}")
    @MethodSource("taskTypes")
    void crud_allTypes_success(Integer taskTypeId) {
        // 1) Create
        TaskCreateRequest createReq = baseCreateRequest(taskTypeId, buildConfig(taskTypeId));

        doAnswer(invocation -> {
            PublisherTask arg = invocation.getArgument(0);
            arg.setTaskId("task_" + taskTypeId + "_crud");
            return 1;
        }).when(taskMapper).insert(any(PublisherTask.class));
        doAnswer(invocation -> {
            PublisherTaskDetail detail = invocation.getArgument(0);
            detail.setDetailId("detail_" + taskTypeId + "_crud");
            return 1;
        }).when(taskDetailMapper).insert(any(PublisherTaskDetail.class));

        String createdTaskId = publisherTaskService.createTask(createReq);
        assertThat(createdTaskId).isEqualTo("task_" + taskTypeId + "_crud");

        // 2) Read (detail)
        PublisherTask taskFromDb = new PublisherTask();
        taskFromDb.setTaskId(createdTaskId);
        taskFromDb.setTaskName(createReq.getTaskName());
        taskFromDb.setTaskTypeId(taskTypeId);
        when(taskMapper.selectById(createdTaskId)).thenReturn(taskFromDb);

        PublisherTaskDetail detailFromDb = new PublisherTaskDetail();
        detailFromDb.setTaskId(createdTaskId);
        detailFromDb.setDetailId("detail_" + taskTypeId + "_crud");
        // 现在createReq.getTaskConfig()已经是JSON字符串，直接使用
        detailFromDb.setTaskConfig(createReq.getTaskConfig());
        when(taskDetailMapper.selectByTaskId(createdTaskId)).thenReturn(detailFromDb);

        TaskDetailResponse readResp = publisherTaskService.getTaskDetail(createdTaskId);
        assertThat(readResp.getTaskId()).isEqualTo(createdTaskId);
        assertThat(readResp.getTaskConfig()).isNotNull();

        // 3) Update (name + config)
        TaskUpdateRequest updateReq = new TaskUpdateRequest();
        updateReq.setTaskName("更新-" + taskTypeId);
        Map<String, Object> updatedCfg = buildUpdatedConfig(taskTypeId);
        // 将Map配置转换为JSON字符串
        updateReq.setTaskConfig(JSON.toJSONString(updatedCfg));

        // selectById already mocked above
        // detail select mocked above
        publisherTaskService.updateTask(createdTaskId, updateReq);

        ArgumentCaptor<PublisherTask> updateTaskCaptor = ArgumentCaptor.forClass(PublisherTask.class);
        verify(taskMapper, atLeastOnce()).updateById(updateTaskCaptor.capture());
        assertThat(updateTaskCaptor.getValue().getTaskName()).isEqualTo("更新-" + taskTypeId);

        ArgumentCaptor<PublisherTaskDetail> updateDetailCaptor = ArgumentCaptor.forClass(PublisherTaskDetail.class);
        verify(taskDetailMapper, atLeastOnce()).updateById(updateDetailCaptor.capture());
        assertThat(updateDetailCaptor.getValue().getTaskConfig()).contains("updated", "type:" + taskTypeId);

        // 4) Delete
        when(taskMapper.selectById(createdTaskId)).thenReturn(taskFromDb);
        publisherTaskService.deleteTask(createdTaskId);
        verify(taskMapper, atLeastOnce()).deleteById(createdTaskId);
        verify(taskDetailMapper, atLeastOnce()).delete(any());
    }

    @Test
    @DisplayName("获取任务列表 - 验证完成人数统计")
    void getTaskList_withCompletionCount_success() {
        // 准备测试数据
        List<PublisherTask> mockTasks = List.of(
                createMockTask("task_001", "任务1"),
                createMockTask("task_002", "任务2"),
                createMockTask("task_003", "任务3")
        );

        // 模拟分页结果
        Page<PublisherTask> mockPage = new Page<>();
        mockPage.setRecords(mockTasks);
        mockPage.setTotal(3);
        mockPage.setCurrent(1);
        mockPage.setSize(10);

        // 模拟任务完成人数统计
        List<Map<String, Object>> mockCompletionCounts = List.of(
                Map.of("taskId", "task_001", "completionCount", 5),
                Map.of("taskId", "task_002", "completionCount", 3),
                Map.of("taskId", "task_003", "completionCount", 0)
        );

        // 模拟Mapper调用
        when(taskMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);
        when(taskCompletionMapper.selectCompletionCountByTaskIds(any())).thenReturn(mockCompletionCounts);

        // 创建请求对象
        TaskListRequest request = new TaskListRequest();
        request.setPage(1);
        request.setSize(10);

        // 执行测试
        IPage<TaskListResponse> result = publisherTaskService.getTaskList(request);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getRecords()).hasSize(3);
        assertThat(result.getTotal()).isEqualTo(3);

        // 验证完成人数是否正确设置
        List<TaskListResponse> responses = result.getRecords();
        assertThat(responses.get(0).getCompletionCount()).isEqualTo(5);
        assertThat(responses.get(1).getCompletionCount()).isEqualTo(3);
        assertThat(responses.get(2).getCompletionCount()).isEqualTo(0);

        // 验证Mapper方法被正确调用
        verify(taskCompletionMapper, times(1)).selectCompletionCountByTaskIds(any());
    }

    // --------- helpers ---------

    static Stream<Integer> taskTypes() {
        return IntStream.rangeClosed(1, 7).boxed();
    }

    private TaskCreateRequest baseCreateRequest(Integer taskTypeId, Map<String, Object> cfg) {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTaskName("任务-" + taskTypeId);
        req.setTaskTypeId(taskTypeId);
        req.setTaskReward(new BigDecimal("10.00"));
        req.setTaskDescription("描述-" + taskTypeId);
        req.setTaskIconUrl("http://example.com/icon-" + taskTypeId + ".png");
        // 将Map配置转换为JSON字符串
        req.setTaskConfig(JSON.toJSONString(cfg));
        return req;
    }

    private Map<String, Object> buildConfig(int type) {
        Map<String, Object> cfg = new HashMap<>();
        switch (type) {
            case 1 -> { // 点赞
                cfg.put("targetUrl", "http://example.com/post/123");
                cfg.put("targetType", "like");
                cfg.put("minDuration", 30);
            }
            case 2 -> { // 评论
                cfg.put("targetUrl", "http://example.com/post/123");
                cfg.put("targetType", "comment");
                cfg.put("minCommentLength", 10);
                cfg.put("requireScreenshot", true);
            }
            case 3 -> { // 讨论
                cfg.put("targetUrl", "http://example.com/forum/topic/456");
                cfg.put("targetType", "discussion");
                cfg.put("minContentLength", 50);
                cfg.put("requireScreenshot", true);
            }
            case 4 -> { // 分享
                cfg.put("taskDescription", "分享到朋友圈或微信群");
                cfg.put("requireImage", true);
                cfg.put("minImageCount", 1);
                cfg.put("requireLink", true);
                cfg.put("linkUrl", "http://example.com/share/789");
                cfg.put("sharePlatforms", List.of("wechat", "weibo", "qq"));
            }
            case 5 -> { // 邀请
                cfg.put("inviteType", "group");
                cfg.put("rewardInvitee", true);
                cfg.put("inviteeReward", 50.00);
                cfg.put("activityRules", "邀请好友入群，双方均可获得积分奖励");
                cfg.put("qrCodeUrl", "http://example.com/qr/group123.png");
            }
            case 6 -> { // 反馈
                cfg.put("taskImage", "http://example.com/feedback/icon.png");
                cfg.put("feedbackUrl", "http://example.com/feedback/form");
                cfg.put("requireScreenshot", true);
            }
            case 7 -> { // 排行榜
                cfg.put("topCount", 10);
                cfg.put("rankingType", "points");
                cfg.put("displayPeriod", "weekly");
            }
            default -> {}
        }
        return cfg;
    }

    private Map<String, Object> buildUpdatedConfig(int type) {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("updated", true);
        cfg.put("type:" + type, true);
        // 针对不同类型添加一个与初始不同的关键字段
        switch (type) {
            case 1 -> cfg.put("minDuration", 45);
            case 2 -> cfg.put("minCommentLength", 25);
            case 3 -> cfg.put("minContentLength", 80);
            case 4 -> cfg.put("minImageCount", 2);
            case 5 -> cfg.put("inviteeReward", 80.00);
            case 6 -> cfg.put("requireScreenshot", false);
            case 7 -> cfg.put("displayPeriod", "monthly");
            default -> {}
        }
        return cfg;
    }

    private PublisherTask createMockTask(String taskId, String taskName) {
        PublisherTask task = new PublisherTask();
        task.setTaskId(taskId);
        task.setTaskName(taskName);
        task.setTaskTypeId(1); // 示例类型
        task.setTaskReward(new BigDecimal("10.00"));
        task.setTaskDescription("描述");
        task.setTaskIconUrl("http://example.com/icon.png");
        task.setCreatedTime(LocalDateTime.now());
        task.setUpdatedTime(LocalDateTime.now());
        return task;
    }
}


