package com.origin.banyu.coreflink.service;

import com.origin.banyu.coreflink.config.FlinkConfig;
import com.origin.banyu.coreflink.job.UserRegistrationAnalysisJob;
import org.apache.flink.api.common.JobID;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * 用户注册分析服务
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Service
public class UserRegistrationAnalysisService extends BaseFlinkJobService {

    private final UserRegistrationAnalysisJob analysisJob;

    public UserRegistrationAnalysisService(UserRegistrationAnalysisJob analysisJob, FlinkConfig flinkConfig) {
        super(flinkConfig);
        this.analysisJob = analysisJob;
    }

    @Override
    public String getJobName() {
        return "user-registration-analysis";
    }

    @Override
    public String getDescription() {
        return """
                用户注册实时分析服务

                功能：
                1. 从Kafka消费用户注册事件
                2. 使用Flink Table API进行实时分析
                3. 按1分钟时间窗口聚合注册数据
                4. 统计总注册人数、男性、女性、未知性别人数
                5. 将结果输出到Redis

                数据格式：
                - 输入：用户注册事件（userId, registerTime, nickname, gender）
                - 输出：注册汇总（windowStart, windowEnd, totalRegistrations, maleCount, femaleCount, unknownCount）

                管理功能：
                - 启动/停止作业
                - 查看作业状态和运行时间
                - 获取作业日志
                - 多作业并发管理
                """;
    }

    @Override
    public int getPriority() {
        return 10; // 高优先级
    }

    @Override
    protected Function<StreamExecutionEnvironment, JobID> getJobLogic() {
        return env -> {
            try {
                return analysisJob.execute(env);
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute user registration analysis job", e);
            }
        };
    }
}
