package com.peach.scheduler.constant;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/3/7 11:05
 */
public interface TaskConstant {

    /**
     * 任务状态
     */
    String TASK_STATUS = "run";

    /**
     * 定时任务类名前缀 http:// 使用该方式则表示需要使用API形式调用外部服务
     */
    String TASK_CLASS_PREFIX_HTTP = "http://";

    /**
     * 定时任务类名前缀 https:// 使用该方式则表示需要使用API形式调用外部服务
     */
    String TASK_CLASS_PREFIX_HTTPS = "https://";

    /**
     * 间隔执行
     */
    Integer TASK_TYPE_ONE= 1;

    /**
     * 定期执行
     */
    Integer TASK_TYPE_TWO = 2;

    /**
     * 启用
     */
    Integer TASK_ENABLE_STATUS_TRUE = 1;

    /**
     * 禁用
     */
    Integer TASK_ENABLE_STATUS_FALSE = 0;

}
