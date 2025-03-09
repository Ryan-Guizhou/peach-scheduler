package com.peach.scheduler.api;

import com.peach.common.response.PageResult;
import com.peach.scheduler.entity.AutomaticTaskLogDO;
import com.peach.scheduler.qo.AutomaticTaskLogQO;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/3/10 0:27
 */
public interface IAutomaticTaskLog {

    PageResult<AutomaticTaskLogDO> getTaskList(AutomaticTaskLogQO automaticTaskLogQO);

    AutomaticTaskLogDO getTaskLog(String traceId);
}
