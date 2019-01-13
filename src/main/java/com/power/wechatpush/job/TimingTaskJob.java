package com.power.wechatpush.job;

import com.power.wechatpush.dao.entity.Task;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TimingTaskJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
        Task task = (Task)map.get("task");
        TaskExecutor taskExecutor = new TaskExecutor();
        taskExecutor.execute(task);
    }
}
