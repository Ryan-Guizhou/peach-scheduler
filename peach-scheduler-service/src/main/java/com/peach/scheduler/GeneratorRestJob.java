package com.peach.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description 远程调用定时任务
 * @CreateTime 06 3月 2025 00:02
 */
@Slf4j
@Indexed
@Component
public class GeneratorRestJob extends BaseJob{

    @Override
    protected void executeInternal(JobExecutionContext context) {

    }
}
