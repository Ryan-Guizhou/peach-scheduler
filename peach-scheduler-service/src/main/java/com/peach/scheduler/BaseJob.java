package com.peach.scheduler;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description 初始化BaseJob
 * @CreateTime 05 3月 2025 23:58
 */
@Slf4j
public abstract class BaseJob implements Job {


    /**
     * 定义任务名称的前缀
     */
    private static String REDIS_KEY_PREFIX = "peach-scheduler:";

    /**
     * 使用懒加载方式
     * @return
     */
    protected RedissonClient getRedissonClient() {
        return SpringUtil.getBean(RedissonClient.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String uniqueKey = REDIS_KEY_PREFIX + getJobName();
        RLock lock = getRedissonClient().getLock(uniqueKey);;
        boolean acquired = false;
        try {
            acquired = lock.tryLock(1,30, TimeUnit.SECONDS);
            if (acquired) {
                log.info("开始执行任务: {}", uniqueKey);
                executeInternal(context);
                log.info("任务执行完成: {}", uniqueKey);
            }else {
                log.info("任务已在其他节点执行，跳过当前执行: {}", uniqueKey);
            }
        } catch (Exception e) {
            log.error("任务执行异常: {}", uniqueKey, e);
            throw new JobExecutionException(e);
        }finally {
            if (acquired && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }


    /**
     * 需要定时执行的方法
     * @param context
     */
    protected abstract void executeInternal(JobExecutionContext context);

    /**
     * 获取子类的job名称
     * @return
     */
    protected abstract String getJobName();

}
