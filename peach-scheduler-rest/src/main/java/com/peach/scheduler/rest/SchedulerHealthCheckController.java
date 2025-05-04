package com.peach.scheduler.rest;

import com.peach.common.response.Response;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 22:46
 */
@Slf4j
@Indexed
@RestController
@RequestMapping("/scheduler")
@Api(tags = "schedulerHealthCheckController",value = "调度健康检查")
public class SchedulerHealthCheckController {

    @GetMapping("/healthCheck")
    public Response healthCheck() {
        return Response.success().setData("I am healthy");
    }

}



