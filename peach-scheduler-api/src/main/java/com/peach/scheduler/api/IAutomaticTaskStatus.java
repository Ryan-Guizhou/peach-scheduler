package com.peach.scheduler.api;

import com.peach.common.response.PageResult;
import com.peach.scheduler.entity.AutomaticTaskStatusDO;
import com.peach.scheduler.qo.AutomaticTaskStatusQO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 22:41
 */
public interface IAutomaticTaskStatus {

    /**
     * 查询任务执行情况明细
     */
    PageResult<AutomaticTaskStatusDO> getTaskStatusByTaskIds(AutomaticTaskStatusQO automaticTaskStatusQO);

    /**
     * 根据id查询任务执行情况明细
     */
    List<AutomaticTaskStatusDO> getTaskStatusByTaskId(AutomaticTaskStatusQO automaticTaskStatusQO);

    /**
     * 清空任务执行情况明细表数据
     */
    void delete();

    /**
     * 根据id删除任务执行情况明细表数据
     */
    void deleteByTaskId(AutomaticTaskStatusDO automaticTaskStatusDO);

    /**
     * 批量插入任务执行情况明细表数据
     */
    void insertBatch(List<AutomaticTaskStatusDO> list);

    /**
     * 插入任务执行情况明细表数据
     */
    void insert(AutomaticTaskStatusDO automaticTaskStatusDO);

    /**
     * 更新任务执行情况明细表数据
     */
    void update(AutomaticTaskStatusDO automaticTaskStatusDO);
}
