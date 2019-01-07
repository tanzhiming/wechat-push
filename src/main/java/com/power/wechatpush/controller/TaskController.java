package com.power.wechatpush.controller;

import com.power.wechatpush.service.TaskService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @GetMapping("/hello")
    public void helloTask() throws SchedulerException {
        taskService.addHelloTask("* * * * * ?");
    }

    @GetMapping("/allTask")
    public void queryTask() {
        taskService.getAllJobs();
    }
}
