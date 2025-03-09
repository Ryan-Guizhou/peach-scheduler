package com.peach.scheduler.endpoint;

import com.peach.scheduler.metrics.SchedulerMetrics;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Indexed
@Component
@Endpoint(id = "scheduler")
public class SchedulerEndpoint {
    
    @Resource
    private Scheduler scheduler;
    
    @Resource
    private SchedulerMetrics schedulerMetrics;

    @ReadOperation
    public Map<String, Object> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", getSchedulerStatus());
        info.put("statistics", getJobStatistics());
        info.put("metrics", getPerformanceMetrics());
        return info;
    }
    
    private Map<String, Object> getSchedulerStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            // 获取调度器基本信息
            SchedulerMetaData metaData = scheduler.getMetaData();
            status.put("schedulerName", metaData.getSchedulerName());
            status.put("schedulerInstanceId", metaData.getSchedulerInstanceId());
            status.put("version", metaData.getVersion());
            status.put("running", metaData.isStarted());
            status.put("shutdown", metaData.isShutdown());
            status.put("standbyMode", metaData.isInStandbyMode());
            status.put("jobStoreSupportsPersistence", metaData.isJobStoreSupportsPersistence());
            status.put("numberOfJobsExecuted", metaData.getNumberOfJobsExecuted());
            status.put("threadPoolSize", metaData.getThreadPoolSize());
            status.put("threadPoolClass", metaData.getThreadPoolClass());
            
            // 获取调度器当前状态
            status.put("isStarted", scheduler.isStarted());
            status.put("isInStandbyMode", scheduler.isInStandbyMode());
            status.put("isShutdown", scheduler.isShutdown());
            
        } catch (SchedulerException e) {
            status.put("error", e.getMessage());
        }
        return status;
    }
    
    private Map<String, Object> getJobStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        try {
            // 获取所有Job组
            List<String> jobGroupNames = scheduler.getJobGroupNames();
            statistics.put("totalGroups", jobGroupNames.size());
            
            int totalJobs = 0;
            int runningJobs = 0;
            int pausedJobs = 0;
            
            // 遍历所有Job组统计信息
            for (String groupName : jobGroupNames) {
                Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
                totalJobs += jobKeys.size();
                
                for (JobKey jobKey : jobKeys) {
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    for (Trigger trigger : triggers) {
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                        switch (triggerState) {
                            case NORMAL:
                                runningJobs++;
                                break;
                            case PAUSED:
                                pausedJobs++;
                                break;
                        }
                    }
                }
            }
            
            statistics.put("totalJobs", totalJobs);
            statistics.put("runningJobs", runningJobs);
            statistics.put("pausedJobs", pausedJobs);
            statistics.put("completedJobs", scheduler.getMetaData().getNumberOfJobsExecuted());
            
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            statistics.put("error", e.getMessage());
        }
        return statistics;
    }
    
    private Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // 获取任务执行计时器统计
        Timer jobExecutionTimer = schedulerMetrics.getJobExecutionTimer();
        metrics.put("totalExecutions", jobExecutionTimer.count());
        metrics.put("meanExecutionTime", jobExecutionTimer.mean(TimeUnit.MILLISECONDS));
        metrics.put("maxExecutionTime", jobExecutionTimer.max(TimeUnit.MILLISECONDS));
        
        // 获取任务调度计时器统计
        Timer schedulingTimer = schedulerMetrics.getJobSchedulingTimer();
        metrics.put("totalSchedulings", schedulingTimer.count());
        metrics.put("meanSchedulingTime", schedulingTimer.mean(TimeUnit.MILLISECONDS));
        metrics.put("maxSchedulingTime", schedulingTimer.max(TimeUnit.MILLISECONDS));
        
        try {
            // 获取线程池信息
            metrics.put("threadPoolSize", scheduler.getMetaData().getThreadPoolSize());
            metrics.put("threadPoolType", scheduler.getMetaData().getThreadPoolClass());
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            metrics.put("error", e.getMessage());
        }
        
        return metrics;
    }
} 