package com.peach.scheduler.api;

import com.peach.common.response.Response;
import com.peach.scheduler.entity.AutomaticTaskDO;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 00:04
 */
public interface IQuartzScheduler {

    /**
     *
     * 启动定时任务
     */
    Response startJob(AutomaticTaskDO automaticTaskDO);

    /**
     *
     * 暂停定时任务
     * @return Response
     */
    Response pauseJob(AutomaticTaskDO automaticTaskDO);


    /**
     *
     * 立即执行定时任务
     * @return Response
     */
    Response doJob(AutomaticTaskDO automaticTaskDO);

    /**
     *
     * 重启定时任务
     * @return Response
     */
    Response resumeJob(AutomaticTaskDO automaticTaskDO);

    /**
     *
     * 重启定时任务
     * @return Response
     */
    Response deleteJob(AutomaticTaskDO automaticTaskDO);

    /**
     *
     * 获取corn表达式
     * @return Response
     */
    Response getCron(AutomaticTaskDO automaticTaskDO);

}
