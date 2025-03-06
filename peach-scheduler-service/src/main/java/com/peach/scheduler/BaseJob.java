package com.peach.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description 初始化BaseJob
 * @CreateTime 05 3月 2025 23:58
 */
public abstract class BaseJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        executeInternal(context);
    }


    protected abstract void executeInternal(JobExecutionContext context);

}
