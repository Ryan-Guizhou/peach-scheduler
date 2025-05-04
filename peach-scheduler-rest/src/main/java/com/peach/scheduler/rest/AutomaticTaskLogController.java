package com.peach.scheduler.rest;

import com.peach.common.response.PageResult;
import com.peach.common.response.Response;
import com.peach.scheduler.api.IAutomaticTaskLog;
import com.peach.scheduler.entity.AutomaticTaskLogDO;
import com.peach.scheduler.qo.AutomaticTaskLogQO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description 任务调度相关接口
 * @CreateTime 2025/3/7 14:34
 */
@Slf4j
@Indexed
@RestController
@RequestMapping("/quartz/log")
@Api(tags = "automaticTaskLogController" ,value = "任务调度日志")
public class AutomaticTaskLogController {

    @Resource
    private IAutomaticTaskLog iAutomaticTaskLog;


    @PostMapping("/getAllLog")
    @ApiOperation(value = "获取所有任务执行日志")
    public Response getAllTaskLog(@RequestBody AutomaticTaskLogQO automaticTaskLogQO) {
        PageResult<AutomaticTaskLogDO> taskLogList = iAutomaticTaskLog.getTaskList(automaticTaskLogQO);
        return Response.success().setData(taskLogList);
    }


    @PostMapping("/getTaskLogByTraceId/{traceId}")
    @ApiOperation(value = "根据TraceId获取调度执行日志")
    public Response getBtTaskId(@PathVariable("traceId") String traceId) {
        AutomaticTaskLogDO taskLog = iAutomaticTaskLog.getTaskLog(traceId);
        return Response.success().setData(taskLog);
    }

}
