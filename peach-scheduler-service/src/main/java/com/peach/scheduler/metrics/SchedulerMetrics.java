package com.peach.scheduler.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

@Slf4j
@Indexed
@Component
public class SchedulerMetrics {

    private final Timer jobExecutionTimer;
    private final Timer jobSchedulingTimer;
    
    public SchedulerMetrics(MeterRegistry registry) {
        this.jobExecutionTimer = Timer.builder("scheduler.job.execution")
                .description("Job execution time")
                .register(registry);
                
        this.jobSchedulingTimer = Timer.builder("scheduler.job.scheduling")
                .description("Job scheduling time")
                .register(registry);
    }
    
    public Timer getJobExecutionTimer() {
        return jobExecutionTimer;
    }
    
    public Timer getJobSchedulingTimer() {
        return jobSchedulingTimer;
    }
} 