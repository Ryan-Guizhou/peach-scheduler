package com.peach.scheduler.exception;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/3/7 11:05
 */
public class TaskException extends RuntimeException {

    /**
     * 任务ID
     */
    private final String taskId;

    /**
     * 操作
     */
    private final String operation;

    public TaskException(String message, String taskId, String operation) {
        super(message);
        this.taskId = taskId;
        this.operation = operation;
    }

}