package com.origin.banyu.coreflink.service;

import com.origin.banyu.coreflink.config.FlinkConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.flink.api.common.JobID;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Flink Job服务基类
 * 简化的Job执行框架，直接使用Flink后台管理
 * 所有Flink Job服务都应该继承此类
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService
public abstract class BaseFlinkJobService {

    private final FlinkConfig flinkConfig;

    protected BaseFlinkJobService(FlinkConfig flinkConfig) {
        this.flinkConfig = flinkConfig;
    }

    // ========== Job服务接口方法 ==========

    /**
     * 获取Job名称（子类必须实现）
     * @return Job名称
     */
    public abstract String getJobName();

    /**
     * 获取Job描述（子类必须实现）
     * @return Job描述
     */
    public abstract String getDescription();

    /**
     * 是否自动启动（子类可重写）
     * @return true表示应用启动时自动启动
     */
    public boolean isAutoStart() {
        return true; // 默认自动启动
    }

    /**
     * 获取Job优先级（子类可重写）
     * @return 优先级（数字越小优先级越高）
     */
    public int getPriority() {
        return 100; // 默认优先级
    }

    /**
     * 执行Job逻辑（子类必须实现）
     */
    protected abstract Function<StreamExecutionEnvironment, JobID> getJobLogic();

    // ========== Job执行方法 ==========

    /**
     * 启动Job
     * @return JobID
     */
    public JobID startJob() {
        try {
            log.info("Starting Flink job: {}", getJobName());
            JobID jobId = executeJob(getJobName(), getJobLogic());
            log.info("Job {} started successfully with JobID: {}", getJobName(), jobId);
            return jobId;
        } catch (Exception e) {
            log.error("Failed to start job: {}", getJobName(), e);
            throw new RuntimeException("Failed to start job: " + getJobName(), e);
        }
    }

    /**
     * 执行Flink Job
     * 
     * @param jobName Job名称
     * @param jobLogic Job逻辑函数
     * @return JobID
     * @throws Exception 执行异常
     */
    private JobID executeJob(String jobName, Function<StreamExecutionEnvironment, JobID> jobLogic) throws Exception {
        log.info("Executing Flink job: {}", jobName);
        
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        // 设置作业名称和全局参数
        setupExecutionEnvironment(env, jobName);
        
        // 执行Job逻辑
        JobID jobId = jobLogic.apply(env);
        
        log.info("Successfully executed job: {} with JobID: {}", jobName, jobId);
        return jobId;
    }

    /**
     * 设置执行环境
     */
    private void setupExecutionEnvironment(StreamExecutionEnvironment env, String jobName) {
        // 设置全局作业参数
        env.getConfig().setGlobalJobParameters(
            new org.apache.flink.api.common.ExecutionConfig.GlobalJobParameters() {
                @Override
                public Map<String, String> toMap() {
                    Map<String, String> params = new HashMap<>();
                    params.put("job.name", flinkConfig.getJob().getName());
                    params.put("job.type", jobName);
                    return params;
                }
            }
        );
        
        log.debug("Setup execution environment for job: {}", jobName);
    }
}
