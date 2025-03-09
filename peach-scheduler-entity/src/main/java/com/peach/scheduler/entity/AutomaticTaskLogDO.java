package com.peach.scheduler.entity;

import com.peach.common.generator.MapperGenerator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/03/09 23:52
 */
@Data
@Builder
@Table(name = "AUTOMATIC_TASK_LOG")
public class AutomaticTaskLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "LOG_ID")
    @ApiModelProperty(value = "日志ID")
    private String logId;

    @Column(name = "TASK_ID")
    @ApiModelProperty(value = "任务ID")
    private String taskId;

    @Column(name = "TASK_NAME")
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @Column(name = "START_TIME")
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @Column(name = "END_TIME")
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @Column(name = "EXECUTION_TIME")
    @ApiModelProperty(value = "执行时长(毫秒)")
    private Long executionTime;

    @Column(name = "STATUS")
    @ApiModelProperty(value = "执行状态:SUCCESS-成功,FAILED-失败,VETOED-被否决")
    private String status;

    @Column(name = "ERROR_MSG")
    @ApiModelProperty(value = "错误信息")
    private String errorMsg;

    @Column(name = "TRACE_ID")
    @ApiModelProperty(value = "追踪ID")
    private String traceId;

    @Column(name = "PARAMS")
    @ApiModelProperty(value = "执行参数")
    private String params;

    @Column(name = "RESULT")
    @ApiModelProperty(value = "执行结果")
    private String result;

    @Column(name = "OPT_USER_ID")
    @ApiModelProperty(value = "操作人ID")
    private String optUserId;

    @Column(name = "OPT_USER_NAME")
    @ApiModelProperty(value = "操作人名称")
    private String optUserName;

    public static void main(String[] args) {
        System.out.println(MapperGenerator.genMapper(AutomaticTaskLogDO.class));
    }

}
