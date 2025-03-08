package com.peach.scheduler.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

@Slf4j
@Indexed
@Component
public class TaskTriggerListener implements TriggerListener {

    @Override
    public String getName() {
        return "taskTriggerListener";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        // 触发器触发时的处理
        log.info("触发器[{}]被触发", trigger.getKey());
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        // 是否否决任务执行
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        // 触发器错过触发时的处理
        log.info("触发器[{}]错过触发", trigger.getKey());
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
                                Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        // 触发器完成时的处理
        log.info("触发器[{}]执行完成", trigger.getKey());
    }
}