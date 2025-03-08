package com.peach.scheduler.listener;


import com.peach.scheduler.api.IQuartzScheduler;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.event.TaskConfigChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TaskConfigChangeListener {
    
    @Resource
    private IQuartzScheduler iQuartzScheduler;
    
    /**
     * 监听任务配置变更事件
     */
    @EventListener(TaskConfigChangeEvent.class)
    public void onTaskConfigChange(TaskConfigChangeEvent event) {
        AutomaticTaskDO taskDO = event.getTaskDO();
        try {
            iQuartzScheduler.updateJob(taskDO);
        } catch (Exception e) {
            log.error("更新任务调度失败", e);
        }
    }
} 