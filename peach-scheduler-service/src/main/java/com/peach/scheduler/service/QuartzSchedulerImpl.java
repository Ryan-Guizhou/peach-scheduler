package com.peach.scheduler.service;

import com.peach.common.response.Response;
import com.peach.scheduler.api.IQuartzScheduler;
import com.peach.scheduler.entity.AutomaticTaskDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 00:04
 */
@Slf4j
@Indexed
@Service
public class QuartzSchedulerImpl implements IQuartzScheduler {


    @Override
    public Response startJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response pauseJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response doJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response resumeJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response deleteJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response getCron(AutomaticTaskDO automaticTaskDO) {
        return null;
    }
}
