package com.peach.scheduler.dao;

import com.peach.common.BaseDao;
import com.peach.common.anno.MyBatisDao;
import com.peach.scheduler.entity.AutomaticTaskStatusDO;
import com.peach.scheduler.qo.AutomaticTaskStatusQO;
import org.springframework.stereotype.Indexed;

import java.util.List;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 22:38
 */
@Indexed
@MyBatisDao
public interface AutomaticTaskStatusDao extends BaseDao<AutomaticTaskStatusDO> {


    List<AutomaticTaskStatusDO> selectByQO(AutomaticTaskStatusQO automaticTaskStatusQO);

    void delByTaskId(String taskId);
}
