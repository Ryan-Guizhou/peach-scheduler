package com.peach.scheduler.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peach.scheduler.api.IQuartzScheduler;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.dao.AutomaticTaskDao;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Objects;

@Slf4j
@Indexed
@Component
public class TaskJobListener implements JobListener {

    @Resource
    private AutomaticTaskDao automaticTaskDao;

    @Resource
    private IQuartzScheduler iQuartzScheduler;

    private static final ObjectMapper _MAPPER =  new ObjectMapper();

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
            AutomaticTaskDO existsAutomaticTask = automaticTaskDao.selectById(taskId);
            if (existsAutomaticTask == null) {
                log.warn("任务[{}]在数据库中不存在", taskId);
                return;
            }

            // 检查任务配置是否发生变化
            String jsonMap = _MAPPER.writeValueAsString(jobDataMap);
            AutomaticTaskDO newAutomaticTask =  _MAPPER.readValue(jsonMap, AutomaticTaskDO.class);
            if (!isTaskConfigChanged(newAutomaticTask, existsAutomaticTask)) {
                log.info("任务[{}]配置发生变化,准备更新调度", taskId);
                iQuartzScheduler.updateSchedulerStatus(existsAutomaticTask);
            }
        } catch (Exception e) {
            log.error("检查任务配置变更失败", e);
        }
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

    /**
     * 检查任务配置是否发生变化
     */
    public boolean isTaskConfigChanged(Object obj1, Object obj2) {
        if (obj1 == obj2) return true;
        if (obj1 == null || obj2 == null) return false;
        if (!obj1.getClass().equals(obj2.getClass())) return false;

        Field[] fields = obj1.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value1 = field.get(obj1);
                Object value2 = field.get(obj2);
                if (!Objects.equals(value1, value2)) {
                    log.info("字段变化：" + field.getName() + " | 旧值：" + value1 + " | 新值：" + value2);
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("反射异常", e);
        }
        return true;
    }
}