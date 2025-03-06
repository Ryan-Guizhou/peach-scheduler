package com.peach.scheduler.dao;

import com.peach.common.BaseDao;
import com.peach.common.anno.MyBatisDao;
import com.peach.scheduler.entity.AutomaticTaskLogDO;
import org.springframework.stereotype.Indexed;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 23:04
 */
@Indexed
@MyBatisDao
public interface AutomaticTaskLogDao extends BaseDao<AutomaticTaskLogDO> {
}
