package com.power.wechatpush.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String data = (String)context.getJobDetail().getJobDataMap().get("data");
        System.out.println(data);
    }
}
