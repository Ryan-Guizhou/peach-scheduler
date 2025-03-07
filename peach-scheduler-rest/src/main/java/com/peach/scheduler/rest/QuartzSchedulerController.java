package com.peach.scheduler.rest;

import com.peach.common.response.PageResult;
import com.peach.common.response.Response;
import com.peach.scheduler.api.IAutomaticTask;
import com.peach.scheduler.api.IAutomaticTaskStatus;
import com.peach.scheduler.entity.AutomaticTaskDO;
import com.peach.scheduler.entity.AutomaticTaskStatusDO;
import com.peach.scheduler.qo.AutomaticTaskQO;
import com.peach.scheduler.qo.AutomaticTaskStatusQO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/quartz")
public class QuartzSchedulerController {

    @Resource
    private IAutomaticTask iAutomaticTask;

    @Resource
    private IAutomaticTaskStatus iAutomaticTaskStatus;


    @PostMapping("/getAllTask")
    @ApiOperation(value = "获取所有任务调度列表")
    public Response getAllTask(@RequestBody AutomaticTaskQO automaticTaskQO) {
        PageResult<AutomaticTaskDO> taskList = iAutomaticTask.getTaskList(automaticTaskQO);
        return Response.success().setData(taskList);
    }


    @PostMapping("/getBtTaskId")
    @ApiOperation(value = "根据taskId获取调度信息")
    public Response getBtTaskId(@RequestBody AutomaticTaskDO automaticTaskDO) {
        AutomaticTaskDO resultTask =  iAutomaticTask.selectById(automaticTaskDO);
        return Response.success().setData(resultTask);
    }


    @PostMapping("/updateById")
    @ApiOperation(value = "编辑自动任务")
    public Response updateById(@RequestBody AutomaticTaskDO automaticTask) {
       iAutomaticTask.updateById(automaticTask);
       return Response.success();
    }

    @PostMapping("/addQuartz")
    @ApiOperation(value = "添加一个新的定时任务")
    public Response addQuartz(@RequestBody AutomaticTaskDO automaticTask) {
        iAutomaticTask.addQuartz(automaticTask);
        return Response.success();
    }


    @PostMapping("/startQuartz")
    @ApiOperation(value = "启用一个定时任务")
    public Response startQuartz(@RequestBody AutomaticTaskDO automaticTask) {
        String message = iAutomaticTask.startQuartz(automaticTask).get("message");
        return Response.success().setData(message);
    }


    @PostMapping("/deleteQuartz")
    @ApiOperation(value = "停用一个定时任务")
    public Response deleteQuartz(@RequestBody AutomaticTaskDO automaticTask) {
        String message = iAutomaticTask.deleteQuartz(automaticTask).get("message");
        return Response.success().setData(message);
    }


    @PostMapping("/immediateQuartz")
    @ApiOperation(value = "立即执行定时任务")
    public Response immediateQuartz(@RequestBody AutomaticTaskDO automaticTask) {
       String message = iAutomaticTask.immediateQuartz(automaticTask).get("message");
       return Response.success().setData(message);
    }

    @PostMapping("/getTaskStatusByTaskIds")
    @ApiOperation(value = "查询任务执行情况明细")
    public Response getTaskStatusByTaskIds(@RequestBody AutomaticTaskStatusQO automaticTaskStatusQO) {
        PageResult<AutomaticTaskStatusDO> pageResult = iAutomaticTaskStatus.getTaskStatusByTaskIds(automaticTaskStatusQO);
        return Response.success().setData(pageResult);

    }

    @PostMapping(path = "/pauseQuartz")
    @ApiOperation("暂停定时任务")
    public Response pauseQuartz(@RequestBody AutomaticTaskDO automaticTask) {
        iAutomaticTask.pauseQuartz(automaticTask);
        return Response.success();
    }


    @PostMapping(path = "/repauseQuartz")
    @ApiOperation("恢复定时任务")
    public Response repauseQuartz(@RequestBody AutomaticTaskDO automaticTask) {
        iAutomaticTask.repauseQuartz(automaticTask);
        return Response.success();
    }


}
