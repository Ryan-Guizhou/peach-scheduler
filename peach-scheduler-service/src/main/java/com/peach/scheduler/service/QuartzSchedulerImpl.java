package com.peach.scheduler.service;


import com.peach.common.util.StringUtil;
import com.peach.scheduler.GeneratorRestJob;
import com.peach.scheduler.api.IQuartzScheduler;
import com.peach.scheduler.constant.TaskConstant;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.exception.TaskException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 00:04
 */
@Slf4j
@Indexed
@Service
public class QuartzSchedulerImpl implements IQuartzScheduler {

    @Resource
    private Scheduler scheduler;

    @Override
    public Map<String,String> startJob(AutomaticTaskDO automaticTaskDO) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        try {
            JobKey jobKey = buildJobKey(automaticTaskDO);
            TriggerKey triggerKey = buildTriggerKey(automaticTaskDO);

            // 检查 Job 是否存在
            JobDetail existsJobDetail = scheduler.getJobDetail(jobKey);
            if (existsJobDetail == null) {
                Class jobClass = createClass(automaticTaskDO);
                JobDetail jobDetail = JobBuilder.newJob(jobClass)
                        .withIdentity(jobKey)
                        .storeDurably()
                        .build();
                jobDetail.getJobDataMap().putAll(automaticTaskDO.toMap());

                // 创建触发器
                Trigger trigger = automaticTaskDO.getTaskType() == TaskConstant.TASK_TYPE_ONE
                        ? getIntervalTrigger(automaticTaskDO)
                        : getRegularTrigger(automaticTaskDO);

                // 确保使用相同的 triggerKey
                trigger = trigger.getTriggerBuilder()
                        .withIdentity(triggerKey)
                        .forJob(jobKey)  // 显式指定 JobKey
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);
                resultMap.put("message", "定时任务启用成功");
            } else {
                log.info("jobKey:[{}] has been started!", jobKey);
                resultMap.put("message", "定时任务已经启动，无需再次启动");
            }
            
            scheduler.start();
        } catch (Exception e) {
            log.error("启动定时任务失败", e);
            resultMap.put("message", "启动定时任务失败: " + e.getMessage());
            throw new TaskException("启动定时任务失败: " , automaticTaskDO.getTaskId(), "startJob");
        }
        return resultMap;
    }


    @Override
    public Map<String,String> doJob(AutomaticTaskDO automaticTaskDO) {
        Map<String, String> resultMap = new HashMap<>();
        JobKey jobKey = buildJobKey(automaticTaskDO);
        try {
            if (scheduler.checkExists(jobKey)){
                // 检查任务是否正在执行
                List<JobExecutionContext> currentJobs = scheduler.getCurrentlyExecutingJobs();
                boolean isRunning = currentJobs.stream()
                        .anyMatch(job -> job.getJobDetail().getKey().equals(jobKey));

                if (isRunning) {
                    resultMap.put("message", "任务正在执行中");
                    resultMap.put("flag", "false");
                    return resultMap;
                }

                // 触发任务执行
                scheduler.triggerJob(jobKey);
                resultMap.put("message", "定时任务已成功触发执行");
                resultMap.put("flag", "true");
                return resultMap;
            }
            resultMap.put("message","该任务处于未开启状态");
            resultMap.put("flag","false");
            return resultMap;
        }catch (Exception ex){
            log.error("立即执行定时任务失败,jobKey:[{}]",jobKey,ex);
            throw new TaskException("立即执行定时任务失败: ",automaticTaskDO.getTaskId(),"doJob");
        }
    }

    @Override
    public Map<String,String> pauseJob(AutomaticTaskDO automaticTaskDO) {
        Map<String, String> resultMap = new HashMap<>();
        JobKey jobKey = buildJobKey(automaticTaskDO);
        try {
            if (scheduler.checkExists(jobKey)){
                scheduler.pauseJob(jobKey);
                resultMap.put("message","定时任务暂停成功");
                return resultMap;
            }
            resultMap.put("message","任务不存在，无法暂停");
            return resultMap;
        }catch (Exception ex){
            log.error("定时任务停用暂停,jobKey:[{}]",jobKey,ex);
            throw new TaskException("定时任务停用失败: ",automaticTaskDO.getTaskId(),"pauseJob");
        }
    }

    @Override
    public Map<String,String> resumeJob(AutomaticTaskDO automaticTaskDO) {
        Map<String, String> resultMap = new HashMap<>();
        JobKey jobKey = buildJobKey(automaticTaskDO);
        try {
            if (scheduler.checkExists(jobKey)){
                scheduler.resumeJob(jobKey);
                resultMap.put("message","定时任务重启成功");
                return resultMap;
            }
            resultMap.put("message","该任务已重启");
            return resultMap;
        }catch (Exception ex){
            log.error("定时任务重启暂停,jobKey:[{}]",jobKey,ex);
            throw new TaskException("定时任务重启失败: ",automaticTaskDO.getTaskId(),"resumeJob");
        }
    }

    @Override
    public Map<String,String> deleteJob(AutomaticTaskDO automaticTaskDO) {
        Map<String, String> resultMap = new HashMap<>();
        JobKey jobKey = buildJobKey(automaticTaskDO);
        TriggerKey triggerKey = buildTriggerKey(automaticTaskDO);
        try {
            if (scheduler.checkExists(jobKey)){
                // 先暂停触发器
                scheduler.pauseTrigger(triggerKey);
                // 等待当前执行的任务完成
                scheduler.interrupt(jobKey);
                // 移除触发器
                scheduler.unscheduleJob(triggerKey);
                // 删除任务
                boolean flag = scheduler.deleteJob(jobKey);

                String msg = flag ? "定时任务停用成功" : "定时任务停用失败";
                resultMap.put("message", msg);
                log.info("任务[{}]已停用,触发器[{}]已删除", jobKey, triggerKey);
            }
            resultMap.put("message","该任务已停用");
            return resultMap;
        } catch (Exception ex ) {
            log.error("定时任务停用失败,jobKey:[{}]",jobKey,ex);
            throw new TaskException("定时任务停用失败: ",automaticTaskDO.getTaskId(),"deleteJob");
        }
    }

    @Override
    public String getCron(AutomaticTaskDO automaticTaskDO) {

        if (automaticTaskDO == null || automaticTaskDO.getTaskType() == null) {
            throw new IllegalArgumentException("Task information is missing or invalid");
        }

        if (TaskConstant.TASK_TYPE_TWO != automaticTaskDO.getTaskType()) {
            return ""; // 非定期任务返回空字符串
        }

        StringBuffer cron = new StringBuffer();
        cron.append("0");
        Integer minOfDay = automaticTaskDO.getMinOfDay();
        Integer outHourOfDay = automaticTaskDO.getHourOfDay();
        // 设置分钟和小时
        cron.append(" ").append(minOfDay == null ? "0" : minOfDay);
        cron.append(" ").append(outHourOfDay == null ? "0" : outHourOfDay);

        Integer dayOfWeek = automaticTaskDO.getDayOfWeek();
        Integer dayOfMonth = automaticTaskDO.getDayOfMonth();
        Integer monthOfYear = automaticTaskDO.getMonthOfYear();

        // 设置年月日
        if (dayOfWeek != null && dayOfWeek != 0) {
            cron.append(" ? * ").append(getWeekday(dayOfWeek));
        } else {
            cron.append(" ").append(dayOfMonth != null && dayOfMonth != 0 ? dayOfMonth : "*");
            cron.append(" ").append(monthOfYear != null && monthOfYear != 0 ? monthOfYear : "*");
            cron.append(" ?");
        }
        return new String(cron);
    }

    /**
     * 根据周几获取对应的周几
     * @param dayOfWeek
     * @return
     */
    private String getWeekday(int dayOfWeek) {
        String[] weekdays = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        return (dayOfWeek >= 1 && dayOfWeek <= 7) ? weekdays[dayOfWeek - 1] : "MON";
    }

    /**
     * 根据任务类型创建对应的任务类
     * @param automaticTaskDO
     * @return
     * @throws Exception
     */
    private Class createClass(AutomaticTaskDO automaticTaskDO) throws Exception{
        String className = automaticTaskDO.getTaskClass();
        boolean isExternalJob = StringUtil.isNotEmpty(className) &&
                (className.startsWith(TaskConstant.TASK_CLASS_PREFIX_HTTP) || className.startsWith(TaskConstant.TASK_CLASS_PREFIX_HTTPS));
        return isExternalJob ? GeneratorRestJob.class : Class.forName(className);
    }

    /**
     * 获取间歇性触发器
     * 1、设置ScheduleBuilder
     * 2、设置定时器和触发规则
     * @param automaticTaskDO
     * @return
     */
    private SimpleTrigger getIntervalTrigger(AutomaticTaskDO automaticTaskDO){

        Integer taskInterval = automaticTaskDO.getTaskInterval();
        Integer runTimes = automaticTaskDO.getRunTimes();

        // 开始监控时间
        Date startDate = getStarDate(automaticTaskDO.getStartDate());
        // 结束监控时间
        Date endDate = getStarDate(automaticTaskDO.getEndDate());


        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(taskInterval)
                .withRepeatCount(Math.max(runTimes - 1, 0)) // 避免负数问题
                .withMisfireHandlingInstructionFireNow()
                .withMisfireHandlingInstructionNextWithRemainingCount();// 避免任务丢失

        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity(buildTriggerKey(automaticTaskDO))
                .withSchedule(simpleScheduleBuilder)
                .startAt(startDate)
                .endAt(endDate)
                .build();

        return simpleTrigger;

    }

    /**
     * 通过年月日 时分秒组装 cron表达式 得到定时器和触发器规则
     * 1、设置ScheduleBuilder
     * 2、设置定时器和触发规则
     * @param automaticTaskDO
     * @return
     */
    private CronTrigger getRegularTrigger(AutomaticTaskDO automaticTaskDO){
        // 获取cron表达式
        String cron = getCron(automaticTaskDO);
        // 设置cron规则
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        // 设置定时器和触发规则
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(buildTriggerKey(automaticTaskDO))
                .withSchedule(cronScheduleBuilder)
                .build();

        return cronTrigger;
    }

    /**
     * 获取开始时间
     * @param starDate
     * @return
     */
    public static Date getStarDate(String starDate) {
        // 默认为当前时间
        Date parse = new Date();
        if (StringUtils.isBlank(starDate)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // 开始时间
                parse = sdf.parse(starDate);
                Calendar c = new GregorianCalendar();
                // 设置参数时间
                c.setTime(parse);
                c.add(Calendar.SECOND, -c.get(Calendar.SECOND));
                parse = c.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return parse;
    }


    /**
     * 构建JobKey
     * @param automaticTaskDO
     * @return
     */
    private JobKey buildJobKey(AutomaticTaskDO automaticTaskDO){
        String job = automaticTaskDO.getTaskId();
        String group = String.valueOf(automaticTaskDO.getTaskType());
        return JobKey.jobKey(job,group);
    }

    /**
     * 构建buildTriggerKey
     * @param automaticTaskDO
     * @return
     */
    private TriggerKey buildTriggerKey(AutomaticTaskDO automaticTaskDO){
        String job = automaticTaskDO.getTaskId();
        String group = String.valueOf(automaticTaskDO.getTaskType());
        return TriggerKey.triggerKey(job,group);
    }

    @Override
    public Map<String, String> updateSchedulerStatus(AutomaticTaskDO automaticTaskDO) {
        Map<String, String> resultMap = new HashMap<>();
        JobKey jobKey = buildJobKey(automaticTaskDO);
        
        try {
            // 先检查 Job 是否存在
            if (!scheduler.checkExists(jobKey)) {
                // 如果 Job 不存在，直接创建新的
                return startJob(automaticTaskDO);
            }

            // 获取当前的 JobDetail
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            // 更新 JobDataMap
            jobDetail.getJobDataMap().putAll(automaticTaskDO.toMap());

            // 创建新的触发器
            TriggerKey triggerKey = buildTriggerKey(automaticTaskDO);
            Trigger newTrigger = (automaticTaskDO.getTaskType() == TaskConstant.TASK_TYPE_ONE
                    ? getIntervalTrigger(automaticTaskDO)
                    : getRegularTrigger(automaticTaskDO))
                    .getTriggerBuilder()
                    .withIdentity(triggerKey)
                    .forJob(jobKey)
                    .build();

            // 删除旧的触发器并添加新的
            scheduler.unscheduleJob(triggerKey);
            scheduler.scheduleJob(newTrigger);
            
            resultMap.put("message", "任务更新成功");
            log.info("任务[{}]更新成功", automaticTaskDO.getTaskId());
            
        } catch (Exception e) {
            log.error("更新任务失败: [{}]", jobKey, e);
            resultMap.put("message", "任务更新失败: " + e.getMessage());
            throw new TaskException("更新任务失败", automaticTaskDO.getTaskId(), "update");
        }
        
        return resultMap;
    }




}
