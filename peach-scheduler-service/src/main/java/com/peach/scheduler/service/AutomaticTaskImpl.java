package com.peach.scheduler.service;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.peach.common.constant.PubCommonConst;
import com.peach.common.enums.StatusEnum;
import com.peach.common.exception.BusniessException;
import com.peach.common.response.PageResult;
import com.peach.common.util.IDGenerator;
import com.peach.common.util.PeachCollectionUtils;
import com.peach.scheduler.api.IAutomaticTask;
import com.peach.scheduler.api.IAutomaticTaskStatus;
import com.peach.scheduler.api.IQuartzScheduler;
import com.peach.scheduler.constant.TaskConstant;
import com.peach.scheduler.constant.TaskEnum;
import com.peach.scheduler.dao.AutomaticTaskDao;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.entity.AutomaticTaskStatusDO;
import com.peach.scheduler.qo.AutomaticTaskQO;
import com.peach.scheduler.qo.AutomaticTaskStatusQO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 22:45
 */
@Slf4j
@Indexed
@Service
public class AutomaticTaskImpl implements IAutomaticTask, ApplicationRunner {

    @Resource
    private AutomaticTaskDao automaticTaskDao;

    @Resource
    private IAutomaticTaskStatus iAutomaticTaskStatus;

    @Resource
    private IQuartzScheduler iQuartzScheduler;

    /**
     * 是否启用定时任务 run 启动
     */
    @Value("${peach-scheduler:run}")
    private String autoTaskStatus;

    /**
     * 实现ApplicationRunner接口,项目启动时启动所有定时任务
     * @param args incoming application arguments
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!TaskConstant.TASK_STATUS.equals(autoTaskStatus)){
            log.info("不需要启动定时任务");
            return;
        }
        List<AutomaticTaskDO> allExistTaskList = automaticTaskDao.select(new AutomaticTaskDO());
        if (PeachCollectionUtils.isEmpty(allExistTaskList)){
            log.info("没有需要启动的定时任务");
            return;
        }

        List<AutomaticTaskStatusDO> allTaskStatusList = Lists.newArrayList();
        for (AutomaticTaskDO automaticTaskDO : allExistTaskList) {
            Integer isEnabled = automaticTaskDO.getIsEnabled();
            if (PubCommonConst.LOGIC_TRUE == isEnabled){
                try {
                    iQuartzScheduler.startJob(automaticTaskDO);
                    log.info("定时任务,任务编码:[{}],任务名称:[{}],启动成功",automaticTaskDO.getTaskCode(),automaticTaskDO.getTaskName());
                }catch (Exception e){
                    log.error("定时任务,任务编码:[{}],任务名称:[{}],启动失败",automaticTaskDO.getTaskCode(),automaticTaskDO.getTaskName(),e);
                }
                AutomaticTaskStatusDO automaticTaskStatusDO = new AutomaticTaskStatusDO();
                automaticTaskStatusDO.setTaskId(automaticTaskDO.getTaskId());
                automaticTaskStatusDO.setStatus(2);
                allTaskStatusList.add(automaticTaskStatusDO);
            }
        }

        //清空明细表
        iAutomaticTaskStatus.delete();
        if (!PeachCollectionUtils.isNotEmpty(allTaskStatusList)){
            return;
        }
        iAutomaticTaskStatus.insertBatch(allTaskStatusList);
    }

    @Override
    public PageResult<AutomaticTaskDO> getTaskList(AutomaticTaskQO automaticTaskQO) {
        PageInfo<AutomaticTaskDO> pageInfo = null;
        pageInfo = PageHelper.startPage(automaticTaskQO.getPageNum(), automaticTaskQO.getPageSize())
                .doSelectPageInfo(() -> {
                    automaticTaskDao.selectListByQO(automaticTaskQO);
                });
        return new PageResult().setTotal(pageInfo.getTotal()).setResult(pageInfo.getList());
    }

    @Override
    public AutomaticTaskDO selectById(AutomaticTaskDO automaticTaskDO) {
        AutomaticTaskDO resultDO = automaticTaskDao.selectById(automaticTaskDO.getTaskId());
        return resultDO == null ? new AutomaticTaskDO() : resultDO;
    }

    @Override
    public void updateById(AutomaticTaskDO automaticTaskDO) {
        automaticTaskDao.updateById(automaticTaskDO.getTaskId());
    }

    @Override
    public Map<String, String> startQuartz(AutomaticTaskDO automaticTask) {
        if (!TaskConstant.TASK_STATUS.equals(autoTaskStatus)){
            return Collections.emptyMap();
        }
        AutomaticTaskDO automaticTaskDO = selectById(automaticTask);
        if (automaticTaskDO == null){
            return Collections.emptyMap();
        }
        automaticTaskDO.setIsEnabled(TaskConstant.TASK_ENABLE_STATUS_TRUE);
        automaticTaskDao.update(automaticTaskDO);
        Map<String, String> resultMap = iQuartzScheduler.startJob(automaticTaskDO);
        AutomaticTaskStatusDO automaticTaskStatusDO =   new AutomaticTaskStatusDO();
        automaticTaskStatusDO.setTaskId(automaticTaskDO.getTaskId());
        automaticTaskStatusDO.setStatus(TaskEnum.TaskStatusEnum.RUNNING.getCode());
        updateQuartzStatus(automaticTaskStatusDO);
        return resultMap;
    }

    @Override
    public Map<String, String> deleteQuartz(AutomaticTaskDO automaticTask) {
        if (!TaskConstant.TASK_STATUS.equals(autoTaskStatus)){
            return Collections.emptyMap();
        }
        AutomaticTaskDO automaticTaskDO = selectById(automaticTask);
        if (automaticTaskDO == null){
            return Collections.emptyMap();
        }

       automaticTaskDO.setIsEnabled(TaskConstant.TASK_ENABLE_STATUS_FALSE);
       automaticTaskDao.update(automaticTaskDO);
        AutomaticTaskStatusDO automaticTaskStatusDO = new AutomaticTaskStatusDO();
        automaticTaskStatusDO.setTaskId(automaticTaskDO.getTaskId());
        automaticTaskStatusDO.setStatus(TaskEnum.TaskStatusEnum.UNSTART.getCode());
        updateQuartzStatus(automaticTaskStatusDO);
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> immediateQuartz(AutomaticTaskDO automaticTask) {
        // 1、判断任务是否开启并且存在
        Map<String, String> resultMap = null;
        log.info("任务调用总开关开启状态=>[{}]",autoTaskStatus);
        if (!TaskConstant.TASK_STATUS.equals(autoTaskStatus)){
            log.info("任务调用总开关关闭,任务[{}]调用失败",automaticTask.getTaskId());
            return Collections.emptyMap();
        }

        AutomaticTaskDO automaticTaskDO = selectById(automaticTask);
        if (automaticTaskDO == null){
            log.info("立即执行的任务已被删除");
            return Collections.emptyMap();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = System.currentTimeMillis();
        resultMap = iQuartzScheduler.doJob(automaticTask);
        log.info("任务调用任务立即执行=>resultMap:[{}]", JSON.toJSON(resultMap));
        long endTime = System.currentTimeMillis();
        // 2、记录任务的状态
        if (resultMap.get("flag").equals("true")) {
            AutomaticTaskStatusQO qo = new AutomaticTaskStatusQO();
            qo.setTaskId(automaticTask.getTaskId());
            List<AutomaticTaskStatusDO> existsTaskStatusList = iAutomaticTaskStatus.getTaskStatusByTaskId(qo);
            if (PeachCollectionUtils.isNotEmpty(existsTaskStatusList)){
                AutomaticTaskStatusDO ts = existsTaskStatusList.get(0);
                AutomaticTaskStatusDO automaticTaskStatusDO = new AutomaticTaskStatusDO();
                automaticTaskStatusDO.setStartTime(ts.getStartTime() == null ? df.format(new Date()) : ts.getStartTime());
                automaticTaskStatusDO.setLastExeTime(df.format(new Date()));
                Long lastTotalTime = endTime - startTime;
                automaticTaskStatusDO.setLastTotalTime(lastTotalTime.intValue());
                automaticTaskStatusDO.setTotalCount(ts.getTotalCount() == null ? 1 : ts.getTotalCount() + 1);
                automaticTaskStatusDO.setOkCount(ts.getOkCount() == null ? 1 : ts.getOkCount() + 1);
                iAutomaticTaskStatus.update(automaticTaskStatusDO);
            }
        }
        return resultMap;
    }

    @Override
    public void pauseQuartz(AutomaticTaskDO automaticTask) {
        iQuartzScheduler.resumeJob(automaticTask);
        AutomaticTaskStatusDO automaticTaskStatusDO =  new AutomaticTaskStatusDO();
        automaticTaskStatusDO.setTaskId(automaticTask.getTaskId());
        automaticTaskStatusDO.setStatus(TaskEnum.TaskStatusEnum.HANGUP.getCode());
        updateQuartzStatus(automaticTaskStatusDO);

    }

    @Override
    public void repauseQuartz(AutomaticTaskDO automaticTask) {
        iQuartzScheduler.resumeJob(automaticTask);
        AutomaticTaskStatusDO automaticTaskStatusDO =  new AutomaticTaskStatusDO();
        automaticTaskStatusDO.setTaskId(automaticTask.getTaskId());
        automaticTaskStatusDO.setStatus(TaskEnum.TaskStatusEnum.RUNNING.getCode());
        updateQuartzStatus(automaticTaskStatusDO);
    }

    @Override
    public void addQuartz(AutomaticTaskDO automaticTask) {
        String taskCode = automaticTask.getTaskCode();
        String taskName = automaticTask.getTaskName();
        if (StringUtils.isBlank(taskCode) || StringUtils.isBlank(taskName)){
            throw new BusniessException(StatusEnum.PARAM_ERROR,"必填参数为空");
        }
        int sameTaskCode = automaticTaskDao.isSameTaskCode(taskName, taskCode);
        if (sameTaskCode <= 0){
            throw new BusniessException(StatusEnum.NOT_FOUND,"该任务已存在");
        }
        automaticTask.setTaskId(IDGenerator.UUID());
        automaticTaskDao.insert(automaticTask);

        // 如果全局开关是 "run"，并且该任务是启用状态，则立即注册到 Quartz
        if (TaskConstant.TASK_STATUS.equalsIgnoreCase(autoTaskStatus) && automaticTask.getIsEnabled() == PubCommonConst.LOGIC_TRUE) {
            iQuartzScheduler.startJob(automaticTask);
        }
    }

    /**
     * 更新状态
     * @param statusDO
     */
    private void updateQuartzStatus(AutomaticTaskStatusDO statusDO) {
        AutomaticTaskStatusQO automaticTaskStatusQO =  new AutomaticTaskStatusQO();
        automaticTaskStatusQO.setTaskId(statusDO.getTaskId());
        List<AutomaticTaskStatusDO> statusDOList = iAutomaticTaskStatus.getTaskStatusByTaskId(automaticTaskStatusQO);
        if (PeachCollectionUtils.isNotEmpty(statusDOList)){
            AutomaticTaskStatusDO automaticTaskStatusDO = statusDOList.get(0);
            automaticTaskStatusDO.setStatus(statusDO.getStatus());
            automaticTaskStatusDO.setOkCount(automaticTaskStatusDO.getOkCount() == null ? 1 : automaticTaskStatusDO.getOkCount() + 1);
            automaticTaskStatusDO.setTotalCount(automaticTaskStatusDO.getTotalCount() == null ? 1 : automaticTaskStatusDO.getTotalCount() + 1);
            iAutomaticTaskStatus.update(automaticTaskStatusDO);
        }else {
            AutomaticTaskStatusDO automaticTaskStatusDO = new AutomaticTaskStatusDO();
            automaticTaskStatusDO.setStatus(statusDO.getStatus());
            automaticTaskStatusDO.setId(IDGenerator.UUID());
            automaticTaskStatusDO.setOkCount(1);
            automaticTaskStatusDO.setTotalCount(1);
            iAutomaticTaskStatus.insert(automaticTaskStatusDO);
        }
    }
}
