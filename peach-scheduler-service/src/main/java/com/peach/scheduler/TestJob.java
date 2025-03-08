package com.peach.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description 测试用例
 * @CreateTime 08 3月 2025 14:21
 */
@Slf4j
@Indexed
@Component
public class TestJob extends BaseJob{
    @Override
    protected void executeInternal(JobExecutionContext context) {
        for (int i = 0; i < 10; i++) {
            log.error("my name is shu : [{}]",i);
        }
    }
}
