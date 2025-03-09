package com.peach.scheduler.dao;

import com.peach.common.BaseDao;
import com.peach.common.anno.MyBatisDao;
import com.peach.scheduler.entity.AutomaticTaskLogDO;
import com.peach.scheduler.qo.AutomaticTaskLogQO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Indexed;

import java.util.List;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 23:04
 */
@Indexed
@MyBatisDao
public interface AutomaticTaskLogDao extends BaseDao<AutomaticTaskLogDO> {

    List<AutomaticTaskLogDO> selectByTaskId(@Param("taskId") String taskId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    AutomaticTaskLogDO selectByTraceId(@Param("traceId") String traceId);

    void deleteByDays(@Param("days") int days);

    List<AutomaticTaskLogDO> selectListByQO(AutomaticTaskLogQO automaticTaskLogQO);


}
