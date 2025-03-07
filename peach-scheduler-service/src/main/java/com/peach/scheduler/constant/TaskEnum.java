package com.peach.scheduler.constant;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/3/7 17:55
 */
public interface TaskEnum {

    enum TaskStatusEnum implements TaskEnum {

        UNSTART(1, "未启用"),
        RUNNING(2, "运行中"),
        HANGUP(3, "挂起"),
        COMPLETE(4, "执行中");

        private final Integer code;

        private final String desc;

        TaskStatusEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static TaskStatusEnum getByCode(Integer code) {
            for (TaskStatusEnum taskStatusEnum : TaskStatusEnum.values()) {
                if (taskStatusEnum.getCode() == code) {
                    return taskStatusEnum;
                }
            }
            return null;
        }
    }
}
