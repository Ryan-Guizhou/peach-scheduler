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
 * @CreateTime 2025/03/06 23:02
 */
@Data
@Table(name = "AUTOMATIC_TASK_LOG")
public class AutomaticTaskLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @ApiModelProperty(value = "任务日志主键")
    private String id;

    @Column(name = "TASK_ID")
    @ApiModelProperty(value = "任务主键")
    private String taskId;

    @Column(name = "TASK_CODE")
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column(name = "TASK_NAME")
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @Column(name = "TASK_DESC")
    @ApiModelProperty(value = "定时任务描述")
    private String taskDesc;

    @Column(name = "OPT_CODE")
    @ApiModelProperty(value = "操作编码")
    private String optCode;

    @Column(name = "OPT_NAME")
    @ApiModelProperty(value = "操作名称")
    private String optName;

    @Column(name = "OPT_USER_ID")
    @ApiModelProperty(value = "操作人ID")
    private String optUserId;

    @Column(name = "OPT_USER_NAME")
    @ApiModelProperty(value = "操作人名称")
    private String optUserName;

    @Column(name = "OPT_TIME")
    @ApiModelProperty(value = "操作时间")
    private String optTime;

    public static void main(String[] args) {
        System.out.println(MapperGenerator.genMapper(AutomaticTaskLogDO.class));
    }

}
