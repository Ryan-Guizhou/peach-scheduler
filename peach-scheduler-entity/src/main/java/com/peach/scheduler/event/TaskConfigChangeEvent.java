package com.peach.scheduler.event;

import com.peach.scheduler.entity.AutomaticTaskDO;
import org.springframework.context.ApplicationEvent;

/**
 * 任务配置变更事件
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