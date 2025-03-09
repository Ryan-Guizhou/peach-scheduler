package com.peach.scheduler.listener;

import com.peach.common.util.DateUtil;
import com.peach.common.util.IDGenerator;
import com.peach.scheduler.constant.TaskEnum;
import com.peach.scheduler.dao.AutomaticTaskLogDao;
import com.peach.scheduler.entity.AutomaticTaskLogDO;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;
import javax.annotation.Resource;

@Slf4j
@Indexed
@Component
public class JobExecutionLogListener implements JobListener {

    @Resource
    private AutomaticTaskLogDao automaticTaskLogDao;

    private final ThreadLocal<AutomaticTaskLogDO> taskLogDOThreadLocal = new ThreadLocal<>();

    @Override
    public String getName() {
        return "JobExecutionLogListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String traceId = IDGenerator.UUID();
        MDC.put("traceId", traceId);

        String taskId = context.getJobDetail().getKey().getName();
        String taskName = context.getJobDetail().getDescription();
        log.info("Job starting - taskId: {}, taskName: {}, traceId: {}", taskId, taskName, traceId);

        // 创建执行日志
        AutomaticTaskLogDO log = AutomaticTaskLogDO.builder()
                .logId(IDGenerator.UUID())
                .taskId(taskId)
                .taskName(taskName)
                .startTime(DateUtil.nowTime())
                .traceId(traceId)
                .params(context.getMergedJobDataMap().toString())
                // 需要获取当前登录用户 获取直接将此信息放到任务信息中
                .optUserId(IDGenerator.UUID())
                .optUserId("Mr Shu")
                .build();

        taskLogDOThreadLocal.set(log);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        AutomaticTaskLogDO automaticTaskLogDO = taskLogDOThreadLocal.get();
        if (automaticTaskLogDO != null) {
            automaticTaskLogDO.setStatus(TaskEnum.TaskStatusExecuteStatus.VETOED.getCode());
            automaticTaskLogDO.setEndTime(DateUtil.nowTime());
            automaticTaskLogDO.setExecutionTime(DateUtil.parseTime(automaticTaskLogDO.getEndTime()).getTime() - DateUtil.parseTime(automaticTaskLogDO.getStartTime()).getTime());

            log.info("Job vetoed - taskId: {}, taskName: {}, traceId: {}, executionTime: {}ms",
                    automaticTaskLogDO.getTaskId(), automaticTaskLogDO.getTaskName(), automaticTaskLogDO.getTraceId(), automaticTaskLogDO.getExecutionTime());

            automaticTaskLogDao.insert(automaticTaskLogDO);
        }

        MDC.remove("traceId");
        taskLogDOThreadLocal.remove();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        AutomaticTaskLogDO automaticTaskLogDO = taskLogDOThreadLocal.get();
        if (automaticTaskLogDO != null) {
            automaticTaskLogDO.setEndTime(DateUtil.nowTime());
            automaticTaskLogDO.setExecutionTime(DateUtil.parseTime(automaticTaskLogDO.getEndTime()).getTime() - DateUtil.parseTime(automaticTaskLogDO.getStartTime()).getTime());

            if (jobException != null) {
                automaticTaskLogDO.setStatus(TaskEnum.TaskStatusExecuteStatus.FAILED.getCode());
                automaticTaskLogDO.setErrorMsg(jobException.getMessage());
                log.error("Job failed - taskId: {}, taskName: {}, traceId: {}, executionTime: {}ms, error: {}",
                        automaticTaskLogDO.getTaskId(), automaticTaskLogDO.getTaskName(), automaticTaskLogDO.getTraceId(),
                        automaticTaskLogDO.getExecutionTime(), jobException.getMessage(), jobException);
            } else {
                automaticTaskLogDO.setStatus(TaskEnum.TaskStatusExecuteStatus.SUCCESS.getCode());
                Object result = context.getResult();
                if (result != null) {
                    automaticTaskLogDO.setResult(result.toString());
                }
                log.info("Job completed - taskId: {}, taskName: {}, traceId: {}, executionTime: {}ms",
                        automaticTaskLogDO.getTaskId(), automaticTaskLogDO.getTaskName(), automaticTaskLogDO.getTraceId(), automaticTaskLogDO.getExecutionTime());
            }

            automaticTaskLogDao.insert(automaticTaskLogDO);
        }

        MDC.remove("traceId");
        taskLogDOThreadLocal.remove();
    }
}