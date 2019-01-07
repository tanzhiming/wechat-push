package com.power.wechatpush.service;

import com.power.wechatpush.job.HelloJob;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.utils.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    @Qualifier("Scheduler")
    private Scheduler scheduler;

    public void addHelloTask(String cronExpress) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("data", "hello world");
        JobKey jobKey = new JobKey(Key.createUniqueName((String)null), (String)null);
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity(jobKey).setJobData(jobDataMap).build();
        Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cronExpress)).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }


    public void getAllJobs(){
        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Date nextFireTime = triggers.get(0).getNextFireTime();
                    System.out.println("[jobName] : " + jobName + " [groupName] : "
                            + jobGroup + " - " + nextFireTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
