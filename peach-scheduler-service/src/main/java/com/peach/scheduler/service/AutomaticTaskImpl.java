package com.peach.scheduler.service;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.peach.common.constant.PubCommonConst;
import com.peach.common.enums.StatusEnum;
import com.peach.common.exception.BusniessException;
import com.peach.common.response.PageResult;
import com.peach.common.util.DateUtil;
import com.peach.common.util.IDGenerator;
import com.peach.common.util.PeachCollectionUtil;
import com.peach.scheduler.api.IAutomaticTask;
import com.peach.scheduler.api.IAutomaticTaskStatus;
import com.peach.scheduler.api.IQuartzScheduler;
import com.peach.scheduler.constant.TaskConstant;
import com.peach.scheduler.constant.TaskEnum;
import com.peach.scheduler.dao.AutomaticTaskDao;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.entity.AutomaticTaskStatusDO;
import com.peach.scheduler.event.TaskConfigChangeEvent;
import com.peach.scheduler.qo.AutomaticTaskQO;
import com.peach.scheduler.qo.AutomaticTaskStatusQO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private ApplicationEventPublisher eventPublisher;

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
        if (PeachCollectionUtil.isEmpty(allExistTaskList)){
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
                Integer statusCode = TaskEnum.TaskStatusEnum.RUNNING.getCode();
                AutomaticTaskStatusDO automaticTaskStatusDO = buildTaskStatus(automaticTaskDO,statusCode);
                allTaskStatusList.add(automaticTaskStatusDO);
            }
        }

        //清空明细表
        iAutomaticTaskStatus.delete();
        if (!PeachCollectionUtil.isNotEmpty(allTaskStatusList)){
            return;
        }
        iAutomaticTaskStatus.insertBatch(allTaskStatusList);
    }

    @Override
    public PageResult<AutomaticTaskDO> getTaskList(AutomaticTaskQO automaticTaskQO) {
        PageInfo<AutomaticTaskDO> pageInfo = PageHelper.startPage(automaticTaskQO.getPageNum(), automaticTaskQO.getPageSize())
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
        // 更新数据库
        automaticTaskDao.updateById(automaticTaskDO);

        // 为了便于测试，这里直接查询数据库，真实环境可从前端传递所有的参数信息
        AutomaticTaskDO newAutomaticTaskDO = automaticTaskDao.selectById(automaticTaskDO.getTaskId());
        // 发布任务配置变更事件
        eventPublisher.publishEvent(new TaskConfigChangeEvent(this, newAutomaticTaskDO));
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
        eventPublisher.publishEvent(new TaskConfigChangeEvent(this, automaticTaskDO));
        Integer statusCode = TaskEnum.TaskStatusEnum.RUNNING.getCode();
        AutomaticTaskStatusDO automaticTaskStatusDO = buildTaskStatus(automaticTaskDO, statusCode);
        updateQuartzStatus(automaticTaskStatusDO);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("message", "定时任务启用成功");
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
       automaticTaskDao.updateById(automaticTaskDO);
       iQuartzScheduler.deleteJob(automaticTaskDO);

       Integer statusCode = TaskEnum.TaskStatusEnum.UNSTART.getCode();
       AutomaticTaskStatusDO automaticTaskStatusDO = buildTaskStatus(automaticTaskDO, statusCode);
       updateQuartzStatus(automaticTaskStatusDO);
       Map<String, String> resultMap = new HashMap<>();
       resultMap.put("message", String.format("定时任务删除成功,taskCode:[%s]", automaticTaskDO.getTaskCode()));
       return resultMap;
    }

    @Override
    public Map<String, String> immediateQuartz(AutomaticTaskDO automaticTask) {
        // 1、判断任务是否开启并且存在
        Map<String, String> resultMap = new HashMap<>();
        log.info("任务调用总开关开启状态=>[{}]",autoTaskStatus);
        if (!TaskConstant.TASK_STATUS.equals(autoTaskStatus)){
            resultMap.put("message", "任务调用总开关关闭");
            return resultMap;
        }

        AutomaticTaskDO automaticTaskDO = selectById(automaticTask);
        if (automaticTaskDO == null){
            resultMap.put("message", "立即执行的任务已被删除");
            return resultMap;
        }
        long startTime = System.currentTimeMillis();
        resultMap = iQuartzScheduler.doJob(automaticTaskDO);
        log.info("任务调用任务立即执行=>resultMap:[{}]", JSON.toJSON(resultMap));
        long endTime = System.currentTimeMillis();
        // 2、记录任务的状态
        if (resultMap.get("flag").equals("true")) {
            AutomaticTaskStatusQO qo = new AutomaticTaskStatusQO();
            qo.setTaskId(automaticTask.getTaskId());
            List<AutomaticTaskStatusDO> existsTaskStatusList = iAutomaticTaskStatus.getTaskStatusByTaskId(qo);
            if (PeachCollectionUtil.isNotEmpty(existsTaskStatusList)){
                AutomaticTaskStatusDO ts = existsTaskStatusList.get(0);
                AutomaticTaskStatusDO automaticTaskStatusDO = new AutomaticTaskStatusDO();
                automaticTaskStatusDO.setStartTime(ts.getStartTime() == null ? DateUtil.nowTime() : ts.getStartTime());
                automaticTaskStatusDO.setLastExeTime(DateUtil.nowTime());
                Long lastTotalTime = endTime - startTime;
                automaticTaskStatusDO.setLastTotalTime(lastTotalTime.intValue());
                automaticTaskStatusDO.setTotalCount(ts.getTotalCount() == null ? 1 : ts.getTotalCount() + 1);
                automaticTaskStatusDO.setOkCount(ts.getOkCount() == null ? 1 : ts.getOkCount() + 1);
                automaticTaskStatusDO.setId(ts.getId());
                iAutomaticTaskStatus.update(automaticTaskStatusDO);
            }
        }
        return resultMap;
    }

    @Override
    public void pauseQuartz(AutomaticTaskDO automaticTask) {
        iQuartzScheduler.pauseJob(automaticTask);
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
    }

    /**
     * 更新状态
     * @param statusDO
     */
    private void updateQuartzStatus(AutomaticTaskStatusDO statusDO) {
        AutomaticTaskStatusQO automaticTaskStatusQO =  new AutomaticTaskStatusQO();
        automaticTaskStatusQO.setTaskId(statusDO.getTaskId());
        List<AutomaticTaskStatusDO> statusDOList = iAutomaticTaskStatus.getTaskStatusByTaskId(automaticTaskStatusQO);
        if (PeachCollectionUtil.isNotEmpty(statusDOList)){
            AutomaticTaskStatusDO automaticTaskStatusDO = statusDOList.get(0);
            automaticTaskStatusDO.setStatus(statusDO.getStatus());
            automaticTaskStatusDO.setOkCount(automaticTaskStatusDO.getOkCount() == null ? 1 : automaticTaskStatusDO.getOkCount() + 1);
            automaticTaskStatusDO.setTotalCount(automaticTaskStatusDO.getTotalCount() == null ? 1 : automaticTaskStatusDO.getTotalCount() + 1);
            automaticTaskStatusDO.setLastExeTime(DateUtil.nowTime());
            iAutomaticTaskStatus.update(automaticTaskStatusDO);
        }else {
            AutomaticTaskStatusDO automaticTaskStatusDO = new AutomaticTaskStatusDO();
            automaticTaskStatusDO.setStatus(statusDO.getStatus());
            automaticTaskStatusDO.setId(IDGenerator.UUID());
            automaticTaskStatusDO.setOkCount(1);
            automaticTaskStatusDO.setTotalCount(1);
            automaticTaskStatusDO.setTaskId(statusDO.getTaskId());
            iAutomaticTaskStatus.insert(automaticTaskStatusDO);
        }
    }

    /**
     * 构建AutomaticTaskStatusDO
     * @param automaticTaskDO
     * @param status
     * @return AutomaticTaskStatusDO
     */
    private AutomaticTaskStatusDO buildTaskStatus(AutomaticTaskDO automaticTaskDO,Integer status) {
        AutomaticTaskStatusDO automaticTaskStatusDO = new AutomaticTaskStatusDO();
        automaticTaskStatusDO.setTaskId(automaticTaskDO.getTaskId());
        automaticTaskStatusDO.setStatus(status);
        automaticTaskStatusDO.setId(IDGenerator.UUID());
        automaticTaskStatusDO.setStartTime(DateUtil.nowTime());
        automaticTaskStatusDO.setLastExeTime(DateUtil.nowTime());
        automaticTaskStatusDO.setTotalCount(1);
        automaticTaskStatusDO.setErrCount(1);
        automaticTaskStatusDO.setOkCount(1);
        automaticTaskStatusDO.setTaskMessage(String.format("定时任务,[%s]启动成功",automaticTaskDO.getTaskCode()));
        return automaticTaskStatusDO;
    }
}
