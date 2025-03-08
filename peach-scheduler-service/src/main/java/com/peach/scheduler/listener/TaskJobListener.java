package com.peach.scheduler.listener;

import com.peach.scheduler.api.IQuartzScheduler;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.dao.AutomaticTaskDao;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
public class TaskJobListener implements JobListener {

    @Resource
    private AutomaticTaskDao automaticTaskDao;

    @Resource
    private IQuartzScheduler iQuartzScheduler;

    @Override
    public String getName() {
        return "taskJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        // 获取任务ID
        String taskId = jobDataMap.getString("taskId");

        try {
            // 从数据库获取最新任务配置
            AutomaticTaskDO latestTask = automaticTaskDao.selectById(taskId);
            if (latestTask == null) {
                log.warn("任务[{}]在数据库中不存在", taskId);
                return;
            }

            // 检查任务配置是否发生变化
            if (isTaskConfigChanged(jobDataMap, latestTask)) {
                log.info("任务[{}]配置发生变化,准备更新调度", taskId);
                iQuartzScheduler.updateJob(latestTask);
            }
        } catch (Exception e) {
            log.error("检查任务配置变更失败", e);
        }
    }

    /**
     * 检查任务配置是否发生变化
     */
    private boolean isTaskConfigChanged(JobDataMap jobDataMap, AutomaticTaskDO latestTask) {
        // 检查关键配置是否变化
        if (!Objects.equals(jobDataMap.get("taskType"), latestTask.getTaskType())) {
            return true;
        }
        if (!Objects.equals(jobDataMap.get("taskInterval"), latestTask.getTaskInterval())) {
            return true;
        }
        if (!Objects.equals(jobDataMap.get("scheduleType"), latestTask.getScheduleType())) {
            return true;
        }
        // 检查其他配置...
        return false;
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // 任务被否决时的处理
        log.info("任务[{}]被否决执行", context.getJobDetail().getKey());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        // 任务执行完成后的处理
        JobDetail jobDetail = context.getJobDetail();
        log.info("任务[{}]执行完成", jobDetail.getKey());
    }
}