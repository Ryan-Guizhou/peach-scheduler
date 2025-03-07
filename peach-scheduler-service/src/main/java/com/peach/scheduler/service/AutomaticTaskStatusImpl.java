package com.peach.scheduler.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.peach.common.enums.StatusEnum;
import com.peach.common.exception.BusniessException;
import com.peach.common.response.PageResult;
import com.peach.common.util.PeachCollectionUtils;
import com.peach.common.util.StringUtil;
import com.peach.scheduler.api.IAutomaticTaskStatus;
import com.peach.scheduler.dao.AutomaticTaskStatusDao;
import com.peach.scheduler.entity.AutomaticTaskStatusDO;
import com.peach.scheduler.qo.AutomaticTaskStatusQO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 22:45
 */
@Slf4j
@Indexed
@Service
public class AutomaticTaskStatusImpl implements IAutomaticTaskStatus {

    @Resource
    private AutomaticTaskStatusDao automaticTaskStatusDao;

    @Override
    public PageResult<AutomaticTaskStatusDO> getTaskStatusByTaskIds(AutomaticTaskStatusQO qo) {
        PageInfo pageInfo = null;
        try {
            pageInfo = PageHelper.startPage(qo.getPageNum(), qo.getPageSize())
                    .doSelectPageInfo(() -> {
                        automaticTaskStatusDao.selectByQO(qo);
                    });
        }catch (Exception e){
            log.error("getPaTaskStatusByTaskIds error:{}", e.getMessage());
        }
        return new PageResult<AutomaticTaskStatusDO>()
                .setTotal(pageInfo.getTotal())
                .setResult(pageInfo.getList());
    }

    @Override
    public List<AutomaticTaskStatusDO> getTaskStatusByTaskId(AutomaticTaskStatusQO qo) {
        return automaticTaskStatusDao.selectByQO(qo);
    }

    @Override
    public void delete() {
        automaticTaskStatusDao.del(new AutomaticTaskStatusDO());
    }

    @Override
    public void deleteByTaskId(AutomaticTaskStatusDO automaticTaskStatusDO) {
        String taskId = automaticTaskStatusDO.getTaskId();
        if (StringUtil.isBlank(taskId)){
            log.warn("taskId is null ,can't  delete");
            return;
        }
        automaticTaskStatusDao.delByTaskId(taskId);
    }

    @Override
    public void insertBatch(List<AutomaticTaskStatusDO> list) {
        if (PeachCollectionUtils.isEmpty(list)){
            log.warn("插入的数据为空");
            return;
        }
        automaticTaskStatusDao.batchInsert(list);
    }

    @Override
    public void insert(AutomaticTaskStatusDO automaticTaskStatusDO) {
        if (automaticTaskStatusDO == null){
            return;
        }
        automaticTaskStatusDao.insert(automaticTaskStatusDO);
    }

    @Override
    public void update(AutomaticTaskStatusDO automaticTaskStatusDO) {
        automaticTaskStatusDao.update(automaticTaskStatusDO);
    }
}
