package com.peach.scheduler.entity;

import com.peach.common.generator.MapperGenerator;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/03/06 23:43
 */
@Data
@Table(name = "AUTOMATIC_TASK_STATUS")
public class AutomaticTaskStatusDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @ApiModelProperty(value = "任务状态id")
    private String id;

    @Column(name = "TASK_ID")
    @ApiModelProperty(value = "任务id")
    private String taskId;

    @Column(name = "START_TIME")
    @ApiModelProperty(value = "第一次执行时间")
    private String startTime;

    @Column(name = "LAST_EXE_TIME")
    @ApiModelProperty(value = "最后次执行时间")
    private String lastExeTime;

    @Column(name = "LAST_TOTAL_TIME")
    @ApiModelProperty(value = "最后一次执行耗时,单位毫秒")
    private Integer lastTotalTime;

    @Column(name = "TOTAL_COUNT")
    @ApiModelProperty(value = "运行总次数")
    private Integer totalCount;

    @Column(name = "ERR_COUNT")
    @ApiModelProperty(value = "运行失败次数")
    private Integer errCount;

    @Column(name = "OK_COUNT")
    @ApiModelProperty(value = "运行成功次数")
    private Integer okCount;

    @Column(name = "TASK_MESSAGE")
    @ApiModelProperty(value = "任务操作信息")
    private String taskMessage;

    @Column(name = "STATUS")
    @ApiModelProperty(value = "运行状态:1:未启用 2:运行中 3:挂起 4:执行中")
    private Integer status;

    public static void main(String[] args) {
        System.out.println(MapperGenerator.genMapper(AutomaticTaskStatusDO.class));
    }

}
