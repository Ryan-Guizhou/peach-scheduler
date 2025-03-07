package com.peach.scheduler.api;

import com.peach.common.response.PageResult;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.qo.AutomaticTaskQO;

import java.util.Map;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 22:41
 */
public interface IAutomaticTask {
    
    /**
     * 查询自动任务列表
     */
    PageResult<AutomaticTaskDO> getTaskList(AutomaticTaskQO automaticTaskQO);

    /**
     * 根据task_id获取任务调度信息
     */
    AutomaticTaskDO selectById(AutomaticTaskDO automaticTaskDO);

    /**
     * 根据id更新自动任务信息
     */
    void updateById(AutomaticTaskDO automaticTaskDO);

    /**
     * 启用定时任务
     */
    Map<String,String> startQuartz(AutomaticTaskDO automaticTask);

    /**
     * 删除定时任务
     */
    Map<String,String> deleteQuartz(AutomaticTaskDO automaticTask);

    /**
     * 立即执行定时任务
     */
    Map<String,String> immediateQuartz(AutomaticTaskDO automaticTask);


    /**
     *
     * 暂停定时任务
     */
    void pauseQuartz(AutomaticTaskDO automaticTask);

    /**
     *
     * 重启定时任务
     */
    void repauseQuartz(AutomaticTaskDO automaticTask);

    /**
     *
     * 添加定时任务
     */
    void addQuartz(AutomaticTaskDO automaticTask);

}
