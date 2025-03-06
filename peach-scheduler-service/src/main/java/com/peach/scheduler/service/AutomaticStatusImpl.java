package com.peach.scheduler.service;

import com.google.common.collect.Lists;
import com.peach.common.constant.PubCommonConst;
import com.peach.common.response.Response;
import com.peach.common.util.PeachCollectionUtils;
import com.peach.scheduler.api.IAutomaticTask;
import com.peach.scheduler.api.IAutomaticTaskStatus;
import com.peach.scheduler.api.IQuartzScheduler;
import com.peach.scheduler.dao.AutomaticTaskDao;
import com.peach.scheduler.dao.AutomaticTaskStatusDao;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.entity.AutomaticTaskStatusDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
public class AutomaticStatusImpl implements IAutomaticTask, ApplicationRunner {

    @Resource
    private AutomaticTaskDao automaticTaskDao;

    @Autowired
    private AutomaticTaskStatusDao automaticTaskStatusDao;

    @Resource
    private IQuartzScheduler iQuartzScheduler;

    @Override
    public Response startJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response pauseJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response doJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response resumeJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response deleteJob(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    @Override
    public Response getCron(AutomaticTaskDO automaticTaskDO) {
        return null;
    }

    private final String autoTaskStatus = "run";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!"run".equals(autoTaskStatus)){
            return;
        }
        List<AutomaticTaskDO> allExistTaskList = automaticTaskDao.select(new AutomaticTaskDO());
        if (PeachCollectionUtils.isEmpty(allExistTaskList)){
            return;
        }

        List<AutomaticTaskStatusDO> allTaskStatusList = Lists.newArrayList();
        for (AutomaticTaskDO automaticTaskDO : allExistTaskList) {
            Integer isEnabled = automaticTaskDO.getIsEnabled();
            if (PubCommonConst.LOGIC_TRUE == isEnabled){
                try {
                    iQuartzScheduler.startJob(automaticTaskDO);
                }catch (Exception e){
                    log.error("定时任务:[{}],启动失败"+e.getMessage(),automaticTaskDO);
                }
                AutomaticTaskStatusDO automaticTaskStatusDO = new AutomaticTaskStatusDO();
                automaticTaskStatusDO.setTaskId(automaticTaskDO.getTaskId());
                automaticTaskStatusDO.setStatus(2);
                allTaskStatusList.add(automaticTaskStatusDO);
            }
        }

        //清空明细表
        automaticTaskStatusDao.del(new AutomaticTaskStatusDO());
        if (!PeachCollectionUtils.isNotEmpty(allTaskStatusList)){
            return;
        }
        automaticTaskStatusDao.batchInsert(allTaskStatusList);
    }
}
