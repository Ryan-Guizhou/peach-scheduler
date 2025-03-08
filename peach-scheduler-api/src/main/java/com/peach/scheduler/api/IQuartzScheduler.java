package com.peach.scheduler.api;

import com.peach.scheduler.entity.AutomaticTaskDO;

import java.util.Map;

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
    Map<String,String> startJob(AutomaticTaskDO automaticTaskDO) throws Exception;

    /**
     *
     * 暂停定时任务
     * @return Map<String,String>
     */
    Map<String,String> pauseJob(AutomaticTaskDO automaticTaskDO);


    /**
     *
     * 立即执行定时任务
     * @return Map<String,String>
     */
    Map<String,String> doJob(AutomaticTaskDO automaticTaskDO);

    /**
     *
     * 重启定时任务
     * @return Map<String,String>
     */
    Map<String,String> resumeJob(AutomaticTaskDO automaticTaskDO);

    /**
     *
     * 重启定时任务
     * @return Map<String,String>
     */
    Map<String,String> deleteJob(AutomaticTaskDO automaticTaskDO);

    /**
     *
     * 获取corn表达式
     * @return Map<String,String>
     */
    String getCron(AutomaticTaskDO automaticTaskDO);

    /**
     *
     * 更新定时任务状态
     * @return Map<String,String>
     */
    Map<String, String> updateSchedulerStatus(AutomaticTaskDO automaticTaskDO) ;

}
