package com.power.wechatpush.job;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.wechatpush.dao.entity.DevicePo;
import com.power.wechatpush.dao.entity.Task;
import com.power.wechatpush.dao.entity.TaskLog;
import com.power.wechatpush.service.TaskService;
import com.power.wechatpush.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class TaskExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(TaskExecutor.class);

    private ThreadPoolExecutor threadPoolExecutor =  new ThreadPoolExecutor(5, 5,
            0L,TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());


     public void execute(Task task) {
        String taskDetail = task.getDetail();
        TaskLogWriter logWriter = null;
        try {
            logWriter = new TaskLogWriter();
            logWriter.write("任务执行开始");

            // TODO 任务执行
            JSONObject detailObj = JSONObject.parseObject(taskDetail);
            JSONArray users = detailObj.getJSONArray("users");
            JSONArray deviceResources = detailObj.getJSONArray("deviceResource");
            JSONArray pushTypes = detailObj.getJSONArray("pushType");
            int duration = detailObj.getIntValue("duration");
            String resourceDir = SpringUtil.getApplicationContext().getEnvironment().getProperty("resource.dir");
            String address = SpringUtil.getApplicationContext().getEnvironment().getProperty("video.address");
            String usernmae = SpringUtil.getApplicationContext().getEnvironment().getProperty("video.usernmae");
            String password = SpringUtil.getApplicationContext().getEnvironment().getProperty("video.password");
            String epid = SpringUtil.getApplicationContext().getEnvironment().getProperty("video.epid");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateStr = sdf.format(new Date());
            File dir = new File(resourceDir, dateStr);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            List<Future<Integer>> futures = new ArrayList<>();



            for (int i = 0; i < deviceResources.size(); i++) {
                JSONObject res = deviceResources.getJSONObject(i);
                final String puid = res.getString("puid");
                final int index = res.getIntValue("index");
                int isVideo = 0;
                if (pushTypes.contains("video")) {
                    isVideo = 1;
                }
                final int isVideo1  = isVideo;

                String filename = dir + File.separator +  puid + "-"+ index + "-" + System.currentTimeMillis();
                futures.add(threadPoolExecutor.submit(new Callable<Integer>() {
                   @Override
                   public Integer call() throws Exception {
                       int ret = -1;
                       ret = recordMedia(address, usernmae, password, epid, puid, index, isVideo1, duration, filename);
                       return ret;
                   }
                }));
            }

            int allRet = 0;
            for (int i = 0; i < futures.size(); i++) {
                int ret = futures.get(i).get(10, TimeUnit.MINUTES);
                if (ret != 0) {
                    allRet = -1;
                    break;
                }
            }
            if (allRet != 0) {
                throw new RuntimeException("设备录制失败");
            }

            // 微信推送给用户
            for (int i = 0; i < users.size(); i++) {
                String openid = users.getString(i);
                System.out.println("wechat push openid: " + openid);
            }

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

    private int recordMedia(String address, String username, String password, String epid, String puid,
                            int index, int isVideo,int duration, String filename) {
        System.out.printf("recordMedia ---- address: %s, username: %s, password: %s, epid: %s, puid: %s, index: %d, isVideo: %d, filename:%s\n",
                address, username, password, epid, puid, index, isVideo, filename);
        return 0;
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
