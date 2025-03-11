package com.peach.scheduler.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.peach.common.response.PageResult;
import com.peach.scheduler.api.IAutomaticTaskLog;
import com.peach.scheduler.dao.AutomaticTaskLogDao;
import com.peach.scheduler.entity.AutomaticTaskLogDO;
import com.peach.scheduler.qo.AutomaticTaskLogQO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/3/10 0:28
 */
@Slf4j
@Indexed
@Service
@ConditionalOnProperty(prefix = "log-storage", name = "type", havingValue = "mysql")
public class MysqlAutomaticTaskLogImpl implements IAutomaticTaskLog {

    @Resource
    private AutomaticTaskLogDao automaticTaskLogDao;

    @Override
    public PageResult<AutomaticTaskLogDO> getTaskList(AutomaticTaskLogQO automaticTaskLogQO) {
        PageInfo<AutomaticTaskLogDO> pageInfo = PageHelper.startPage(automaticTaskLogQO.getPageNum(), automaticTaskLogQO.getPageSize())
                .doSelectPageInfo(() -> {
                    automaticTaskLogDao.selectListByQO(automaticTaskLogQO);
                });
        return new PageResult<AutomaticTaskLogDO>()
                .setTotal(pageInfo.getTotal())
                .setResult(pageInfo.getList());
    }

    @Override
    public AutomaticTaskLogDO getTaskLog(String traceId) {
        return automaticTaskLogDao.selectByTraceId(traceId);
    }
}
