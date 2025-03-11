package com.peach.scheduler.dao;

import com.peach.common.BaseDao;
import com.peach.common.anno.MyBatisDao;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.qo.AutomaticTaskQO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Indexed;

import java.util.List;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 22:21
 */
@Indexed
@MyBatisDao
public interface AutomaticTaskDao extends BaseDao<AutomaticTaskDO> {


    List<AutomaticTaskDO> selectListByQO(AutomaticTaskQO automaticTaskQO);

    int isSameTaskCode(@Param("taskName") String taskName, @Param("taskCode") String taskCode);

}
