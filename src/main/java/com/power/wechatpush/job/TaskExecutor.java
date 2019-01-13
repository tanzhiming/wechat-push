package com.power.wechatpush.job;

import com.power.wechatpush.dao.entity.Task;
import com.power.wechatpush.dao.entity.TaskLog;
import com.power.wechatpush.service.TaskService;
import com.power.wechatpush.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * Created by tanzhiming on 2018/4/18.
 */
public class TaskExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(TaskExecutor.class);

    public void execute(Task task) {
        String taskDetail = task.getDetail();
        TaskLogWriter logWriter = null;
        try {
            logWriter = new TaskLogWriter();
            logWriter.write("任务执行开始");

            // TODO 任务执行



            logWriter.write("任务执行成功");
        } catch (Exception e) {
            logWriter.write("任务执行失败：" +  limit(this.getStackTrace(e), 2000));
            LOG.error("execute task failed", e);
        }
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskName(task.getName());
        taskLog.setTaskType(task.getType());
        taskLog.setCreateTime(new Date());
        taskLog.setLog(logWriter.getLogContent());
        TaskService taskService =  SpringUtil.getApplicationContext().getBean(TaskService.class);
        taskService.insertTaskLog(taskLog);
    }

    private String limit(String content, int length) {
        if (content != null && content.length() > length) {
            content.substring(0, length);
            return content;
        } else {
            return content;
        }
    }

    public String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        try {
            sw.close();
        } catch (IOException e1) {
            //ignore
        }
        return sw.toString();
    }
}
