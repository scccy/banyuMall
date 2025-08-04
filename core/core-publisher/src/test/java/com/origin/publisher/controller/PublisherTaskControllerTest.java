package com.origin.publisher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.publisher.dto.TaskCreateRequest;
import com.origin.publisher.dto.TaskUpdateRequest;
import com.origin.publisher.dto.TaskListRequest;
import com.origin.publisher.dto.TaskDetailResponse;
import com.origin.publisher.dto.TaskListResponse;
import com.origin.common.dto.PageResult;
import com.origin.publisher.service.PublisherTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PublisherTaskController 测试类
 * 
 * @author scccy
 */
@WebMvcTest(PublisherTaskController.class)
@ActiveProfiles("test")
@DisplayName("任务管理控制器测试")
class PublisherTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublisherTaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskCreateRequest createRequest;
    private TaskUpdateRequest updateRequest;
    private TaskListRequest listRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        createRequest = new TaskCreateRequest();
        createRequest.setTaskName("测试任务");
        createRequest.setTaskTypeId(1);
        createRequest.setTaskDescription("这是一个测试任务");
        createRequest.setTaskReward(new BigDecimal("100.00"));
        createRequest.setTaskIconUrl("http://example.com/icon.png");
        // 创建点赞任务的配置
        Map<String, Object> taskConfig = new HashMap<>();
        taskConfig.put("targetUrl", "http://example.com/post/123");
        taskConfig.put("targetType", "like");
        taskConfig.put("minDuration", 30);
        createRequest.setTaskConfig(taskConfig);

        updateRequest = new TaskUpdateRequest();
        updateRequest.setTaskName("更新后的测试任务");
        updateRequest.setTaskDescription("这是更新后的任务描述");
        updateRequest.setTaskReward(new BigDecimal("150.00"));

        listRequest = new TaskListRequest();
        listRequest.setPage(1);
        listRequest.setSize(10);
        listRequest.setTaskTypeId(1);
        listRequest.setStatusId(2);
    }

    @Test
    @DisplayName("创建任务 - 成功")
    void testCreateTaskSuccess() throws Exception {
        // 模拟服务层返回
        when(taskService.createTask(any(TaskCreateRequest.class)))
                .thenReturn("task_20250127_001");

        // 执行测试
        mockMvc.perform(post("/core/publisher/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务创建成功"))
                .andExpect(jsonPath("$.data").value("task_20250127_001"));
    }

    @Test
    @DisplayName("创建任务 - 参数验证失败")
    void testCreateTaskValidationFailure() throws Exception {
        // 创建无效的请求
        TaskCreateRequest invalidRequest = new TaskCreateRequest();
        invalidRequest.setTaskName(""); // 空任务名称
        invalidRequest.setTaskTypeId(999); // 无效任务类型
        invalidRequest.setTaskReward(new BigDecimal("-100.00")); // 负数奖励

        // 执行测试
        mockMvc.perform(post("/core/publisher/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("更新任务 - 成功")
    void testUpdateTaskSuccess() throws Exception {
        String taskId = "task_20250127_001";

        // 模拟服务层返回（void方法，不需要返回值）
        doNothing().when(taskService).updateTask(eq(taskId), any(TaskUpdateRequest.class));

        // 执行测试
        mockMvc.perform(put("/core/publisher/tasks/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务更新成功"));
    }

    @Test
    @DisplayName("更新任务 - 任务不存在")
    void testUpdateTaskNotFound() throws Exception {
        String taskId = "non_existent_task";

        // 模拟服务层抛出异常
        doThrow(new RuntimeException("任务不存在"))
                .when(taskService).updateTask(eq(taskId), any(TaskUpdateRequest.class));

        // 执行测试
        mockMvc.perform(put("/core/publisher/tasks/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("获取任务详情 - 成功")
    void testGetTaskDetailSuccess() throws Exception {
        String taskId = "task_20250127_001";

        // 模拟服务层返回
        when(taskService.getTaskDetail(taskId))
                .thenReturn(createMockTaskDetailResponse());

        // 执行测试
        mockMvc.perform(get("/core/publisher/tasks/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取任务详情成功"))
                .andExpect(jsonPath("$.data.taskId").value(taskId))
                .andExpect(jsonPath("$.data.taskName").value("测试任务"));
    }

    @Test
    @DisplayName("获取任务详情 - 任务不存在")
    void testGetTaskDetailNotFound() throws Exception {
        String taskId = "non_existent_task";

        // 模拟服务层抛出异常
        when(taskService.getTaskDetail(taskId))
                .thenThrow(new RuntimeException("任务不存在"));

        // 执行测试
        mockMvc.perform(get("/core/publisher/tasks/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("获取任务列表 - 成功")
    void testGetTaskListSuccess() throws Exception {
        // 模拟服务层返回
        when(taskService.getTaskList(any(TaskListRequest.class)))
                .thenReturn(createMockTaskListResponse());

        // 执行测试
        mockMvc.perform(get("/core/publisher/tasks")
                        .param("page", "1")
                        .param("size", "10")
                        .param("taskTypeId", "1")
                        .param("statusId", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取任务列表成功"))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("删除任务 - 成功")
    void testDeleteTaskSuccess() throws Exception {
        String taskId = "task_20250127_001";

        // 模拟服务层返回（void方法，不需要返回值）
        doNothing().when(taskService).deleteTask(taskId);

        // 执行测试
        mockMvc.perform(delete("/core/publisher/tasks/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务删除成功"));
    }

    @Test
    @DisplayName("更新任务状态 - 成功")
    void testUpdateTaskStatusSuccess() throws Exception {
        String taskId = "task_20250127_001";
        Integer statusId = 2;

        // 模拟服务层返回（void方法，不需要返回值）
        doNothing().when(taskService).updateTaskStatus(taskId, statusId);

        // 执行测试
        mockMvc.perform(put("/core/publisher/tasks/{taskId}/status", taskId)
                        .param("statusId", statusId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务状态更新成功"));
    }

    @Test
    @DisplayName("发布任务 - 成功")
    void testPublishTaskSuccess() throws Exception {
        String taskId = "task_20250127_001";

        // 模拟服务层返回（void方法，不需要返回值）
        doNothing().when(taskService).publishTask(taskId);

        // 执行测试
        mockMvc.perform(post("/core/publisher/tasks/{taskId}/publish", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务发布成功"));
    }

    @Test
    @DisplayName("下架任务 - 成功")
    void testUnpublishTaskSuccess() throws Exception {
        String taskId = "task_20250127_001";

        // 模拟服务层返回（void方法，不需要返回值）
        doNothing().when(taskService).unpublishTask(taskId);

        // 执行测试
        mockMvc.perform(post("/core/publisher/tasks/{taskId}/unpublish", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务下架成功"));
    }

    @Test
    @DisplayName("任务列表分页参数验证")
    void testTaskListPaginationValidation() throws Exception {
        // 测试无效的分页参数
        mockMvc.perform(get("/core/publisher/tasks")
                        .param("page", "0") // 无效页码
                        .param("size", "10000") // 无效页面大小
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("任务状态更新参数验证")
    void testTaskStatusUpdateValidation() throws Exception {
        String taskId = "task_20250127_001";

        // 测试无效的状态ID
        mockMvc.perform(put("/core/publisher/tasks/{taskId}/status", taskId)
                        .param("statusId", "999") // 无效状态ID
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // 或者根据实际业务逻辑调整期望状态
    }

    // 辅助方法：创建模拟的任务详情响应
    private TaskDetailResponse createMockTaskDetailResponse() {
        TaskDetailResponse response = new TaskDetailResponse();
        response.setTaskId("task_20250127_001");
        response.setTaskName("测试任务");
        response.setTaskTypeId(1);
        response.setTaskDescription("这是一个测试任务");
        response.setTaskReward(new BigDecimal("100.00"));
        response.setTaskIconUrl("http://example.com/icon.png");
        response.setStatusId(1);
        response.setCreatedTime(LocalDateTime.now());
        response.setUpdatedTime(LocalDateTime.now());
        response.setCompletionCount(5);
        
        // 设置任务配置
        Map<String, Object> taskConfig = new HashMap<>();
        taskConfig.put("targetUrl", "http://example.com/post/123");
        taskConfig.put("targetType", "like");
        taskConfig.put("minDuration", 30);
        response.setTaskConfig(taskConfig);
        
        return response;
    }

    // 辅助方法：创建模拟的任务列表响应
    private PageResult<TaskListResponse> createMockTaskListResponse() {
        TaskListResponse taskResponse = new TaskListResponse();
        taskResponse.setTaskId("task_20250127_001");
        taskResponse.setTaskName("测试任务");
        taskResponse.setTaskTypeId(1);
        taskResponse.setTaskReward(new BigDecimal("100.00"));
        taskResponse.setStatusId(2);
        taskResponse.setCompletionCount(5);
        
        PageResult<TaskListResponse> pageResult = new PageResult<>();
        pageResult.setRecords(java.util.Arrays.asList(taskResponse));
        pageResult.setTotal(1L);
        pageResult.setSize(10L);
        pageResult.setCurrent(1L);
        
        return pageResult;
    }
} 