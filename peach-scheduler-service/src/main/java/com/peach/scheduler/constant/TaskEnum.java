package com.peach.scheduler.constant;

import java.util.Objects;

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
                if (Objects.equals(taskStatusEnum.getCode(), code)) {
                    return taskStatusEnum;
                }
            }
            return null;
        }
    }

    /**
     * 日志记录任务执行状态枚举
     */
    enum TaskStatusExecuteStatus implements TaskEnum {
        SUCCESS("SUCCESS", "成功"),
        FAILED("FAILED", "失败"),
        VETOED("VETOED", "拒绝");

        private final String code;

        private final String desc;

        TaskStatusExecuteStatus(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static TaskStatusExecuteStatus getByCode(String code) {
            if (code == null) {
                return null;
            }
            for (TaskStatusExecuteStatus taskStatusEnum : TaskStatusExecuteStatus.values()) {
                if (code.equals(taskStatusEnum.getCode())) {
                    return taskStatusEnum;
                }
            }
            return null;
        }
    }
}
