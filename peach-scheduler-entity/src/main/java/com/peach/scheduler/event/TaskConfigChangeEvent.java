package com.peach.scheduler.event;

import com.peach.scheduler.entity.AutomaticTaskDO;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description 任务配置变更时间
 * @CreateTime 2025/03/06 23:43
 */
public class TaskConfigChangeEvent extends ApplicationEvent {
    
    private final AutomaticTaskDO taskDO;
    
    public TaskConfigChangeEvent(Object source, AutomaticTaskDO taskDO) {
        super(source);
        this.taskDO = taskDO;
    }
    
    public AutomaticTaskDO getTaskDO() {
        return taskDO;
    }
}