package com.peach.scheduler.entity;

import com.peach.common.generator.MapperGenerator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.beanutils.PropertyUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/03/06 23:43
 */
@Data
@Table(name = "AUTOMATIC_TASK")
public class AutomaticTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TASK_ID")
    private String taskId;

    @Column(name = "TASK_CODE")
    @ApiModelProperty(value = "编码")
    private String taskCode;

    @Column(name = "TASK_NAME")
    @ApiModelProperty(value = "名称")
    private String taskName;

    @Column(name = "TASK_DESC")
    @ApiModelProperty(value = "定时任务描述")
    private String taskDesc;

    @Column(name = "TASK_TYPE")
    @ApiModelProperty(value = "1：间隔运行 2：定期运行")
    private Integer taskType;

    @Column(name = "START_DATE")
    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss")
    private String startDate;

    @Column(name = "END_DATE")
    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss")
    private String endDate;

    @Column(name = "TASK_INTERVAL")
    @ApiModelProperty(value = "时间间隔(秒)")
    private Integer taskInterval;

    @Column(name = "RUN_TIMES")
    @ApiModelProperty(value = "运行次数")
    private Integer runTimes;

    @Column(name = "IS_ENABLED")
    @ApiModelProperty(value = "是否启用")
    private Integer isEnabled;

    @Column(name = "TASK_CLASS")
    @ApiModelProperty(value = "自动任务运行服务类")
    private String taskClass;

    @Column(name = "TASK_PARAM")
    @ApiModelProperty(value = "任务服务类参数")
    private String taskParam;

    @Column(name = "REMARK")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "SCHEDULE_TYPE")
    @ApiModelProperty(value = "0：每年 1：每月 2：每周 3：每日")
    private Integer scheduleType;

    @Column(name = "MONTH_OF_YEAR")
    @ApiModelProperty(value = "第几月")
    private Integer monthOfYear;

    @Column(name = "DAY_OF_WEEK")
    @ApiModelProperty(value = "每周几")
    private Integer dayOfWeek;

    @Column(name = "DAY_OF_MONTH")
    @ApiModelProperty(value = "每月第几天")
    private Integer dayOfMonth;

    @Column(name = "HOUR_OF_DAY")
    @ApiModelProperty(value = "小时")
    private Integer hourOfDay;

    @Column(name = "MIN_OF_DAY")
    @ApiModelProperty(value = "分钟")
    private Integer minOfDay;

    @Column(name = "SYS_ID")
    @ApiModelProperty(value = "所属模块")
    private String sysId;

    /**
     * 转换为Map
     *
     * @return
     */
    public Map toMap() {
        try {
            Map map = PropertyUtils.describe(this);
            map.remove("class");
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        System.out.println(MapperGenerator.genMapper(AutomaticTaskDO.class));
    }
}
