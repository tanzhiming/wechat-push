package com.power.wechatpush.controller;

import com.power.wechatpush.dao.entity.Task;
import com.power.wechatpush.job.TaskExecutor;
import com.power.wechatpush.job.TimingTaskJob;
import com.power.wechatpush.service.TaskService;
import com.power.wechatpush.util.Page;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/task")
public class TaskController {

    private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    @Autowired @Qualifier("Scheduler")
    private Scheduler scheduler;

    @Autowired
    private TaskService taskService;

    @GetMapping("")
    @ResponseBody
    public List<Task> listTasks(){
        return taskService.listTasks();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Task getTask(@PathVariable Long id){
        return taskService.getTask(id);
    }


    @PostMapping("")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Task saveTask(Task task) throws SchedulerException {

        JobKey jobKey = null;
        if (task.getType() == Task.TYPE_TIMING) {
            jobKey = new JobKey(Key.createUniqueName((String)null), (String)null);
            task.setJobKey(jobKey);
        }
        task = taskService.saveTask(task);
        if (task.getType() == Task.TYPE_TIMING) {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("task", task);
            JobDetail jobDetail = JobBuilder.newJob(TimingTaskJob.class).withIdentity(jobKey).setJobData(jobDataMap).build();
            Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpress())).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
        return task;
    }


    @PostMapping("/{id}")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Task editTask(@PathVariable Long id, Task task) throws SchedulerException {

        Task oldTask = taskService.getTask(id);
        if (oldTask.getType() == Task.TYPE_TIMING) {
            scheduler.deleteJob(oldTask.getJobKey());
        }
        JobKey jobKey = null;
        if (task.getType() == Task.TYPE_TIMING) {
            jobKey = new JobKey(Key.createUniqueName((String)null), (String)null);
            task.setJobKey(jobKey);
        } else {
            task.setCronExpress(null);
            task.setJobName(null);
            task.setJobGroup(null);
        }
        task = taskService.updateTask(task);
        if (task.getType() == Task.TYPE_TIMING) {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("task", task);
            JobDetail jobDetail = JobBuilder.newJob(TimingTaskJob.class).withIdentity(jobKey).setJobData(jobDataMap).build();
            Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpress())).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
        return task;
    }


    @DeleteMapping("/{id}")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Task deleteTask(@PathVariable("id") Long id) throws SchedulerException {
        Task task = taskService.getTask(id);
        taskService.deleteTask(id);
        if (task.getType() == Task.TYPE_TIMING) {
            scheduler.deleteJob(task.getJobKey());
        }
        return task;
    }

    @PostMapping("/execute/{id}")
    @ResponseBody
    @Transactional
    public void executeTask(@PathVariable("id") Long id) throws SchedulerException {
        Task task = taskService.getTask(id);
        TaskExecutor taskExecutor = new TaskExecutor();
        taskExecutor.execute(task);
    }

    @GetMapping("/task-log")
    @ResponseBody
    public Page queryTaskLog(@RequestParam(name="page", required = true) Integer page,
                             @RequestParam(name="rows", required = true) Integer rows) {

        return taskService.getPageForTaskLog(page, rows);
    }

    @GetMapping("/toEditTask/{id}")
    public String toEditTask(@PathVariable Long id, Model model){
        Task task = taskService.getTask(id);
        model.addAttribute("task", task);
        return "editTask";
    }

    @GetMapping("/lateFireTimes")
    @ResponseBody
    public List<String> getLateFireTimes(String cronExpress) {
        List<String> dateStrs = new ArrayList<>();
        try {

            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            // 每天早上10：15触发
            cronTriggerImpl.setCronExpression(cronExpress);

            List<Date> dates = TriggerUtils.computeFireTimes(
                    cronTriggerImpl, null, 5);

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            for (Date date : dates) {
                dateStrs.add(dateFormat.format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStrs;
    }

    @GetMapping("/validateCron")
    @ResponseBody
    public Map<String, Boolean> validateCron(String cronExpress) {
        List<String> dateStrs = new ArrayList<>();
        Map<String, Boolean> retMap = new HashMap<>();
        retMap.put("isValid", false);
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cronExpress);
            Date startDate = new Date(new Date().getTime() - 5 * 60 * 1000);
            Date date = cronTriggerImpl.computeFirstFireTime(null);
            boolean isValid = date != null && date.after(startDate);
            retMap.put("isValid", isValid);
        } catch (ParseException e) {
            LOG.error("validate cron error", e);
            retMap.put("isValid", false);
        }
        return retMap;
    }

}
