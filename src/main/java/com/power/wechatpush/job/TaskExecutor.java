package com.power.wechatpush.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.wechatpush.dao.entity.MediaFile;
import com.power.wechatpush.dao.entity.Task;
import com.power.wechatpush.dao.entity.TaskLog;
import com.power.wechatpush.service.MediaFileService;
import com.power.wechatpush.service.TaskService;
import com.power.wechatpush.service.WxUserService;
import com.power.wechatpush.util.CommonUtil;
import com.power.wechatpush.util.SpringUtil;
import com.power.wechatpush.video.VideoSDK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TaskExecutor {

     private static final Logger LOG = LoggerFactory.getLogger(TaskExecutor.class);

     public void execute(Task task) {
        String taskDetail = task.getDetail();
        TaskLogWriter logWriter = null;
        String taskName = task.getName();
        try {
            logWriter = new TaskLogWriter();
            logWriter.write("任务执行开始");
            MediaFileService mediaFileService = SpringUtil.getApplicationContext().getBean(MediaFileService.class);
            WxUserService wxUserService = SpringUtil.getApplicationContext().getBean(WxUserService.class);
            TaskExecutorService taskExecutorService = SpringUtil.getApplicationContext().getBean(TaskExecutorService.class);


            // TODO 任务执行
            JSONObject detailObj = JSONObject.parseObject(taskDetail);
            JSONArray users = detailObj.getJSONArray("users");
            JSONArray deviceResources = detailObj.getJSONArray("deviceResource");
            JSONArray pushTypes = detailObj.getJSONArray("pushType");
            int duration = detailObj.getIntValue("duration");
            String resourceDir = SpringUtil.getApplicationContext().getEnvironment().getProperty("resource.dir");
            String address = SpringUtil.getApplicationContext().getEnvironment().getProperty("video.address");
            String username = SpringUtil.getApplicationContext().getEnvironment().getProperty("video.username");
            String password = SpringUtil.getApplicationContext().getEnvironment().getProperty("video.password");
            String epid = SpringUtil.getApplicationContext().getEnvironment().getProperty("video.epid");
            String templateId = SpringUtil.getApplicationContext().getEnvironment().getProperty("wx.message.templateId");
            String domain = SpringUtil.getApplicationContext().getEnvironment().getProperty("wx.my.domain");


            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateStr = sdf.format(new Date());
            File dir = new File(resourceDir, dateStr);
            String requestPath = "/resources/" + dateStr;
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 批次号
            String batchNo = CommonUtil.getBatchNo();

            List<Future<Integer>> futures = new ArrayList<>();
            for (int i = 0; i < deviceResources.size(); i++) {
                JSONObject res = deviceResources.getJSONObject(i);
                final String puid = res.getString("puid");
                final int index = res.getIntValue("index");
                final String devName = res.getString("devName");
                final String resName = res.getString("resName");
                int isVideo = 0;
                if (pushTypes.contains("video")) {
                    isVideo = 1;
                }
                final int isVideo1  = isVideo;

                String partName = puid + "-"+ index + "-" + System.currentTimeMillis();
                String filename = dir + File.separator + partName;
                String filename2 = requestPath + "/"+ partName;
                futures.add(taskExecutorService.submit(new Callable<Integer>() {
                   @Override
                   public Integer call() throws Exception {
                       int ret = -1;
                       try {
//                           taskExecutorService.init();
                           ret = recordMedia(address, username, password, epid, puid, index, isVideo1, duration, filename);
                       } catch (Throwable e) {
                           //String filename2 = "/resources/default/" + puid + "-"+ index;
                           LOG.error("录制失败", e);
                       } finally {
//                           taskExecutorService.destroy();
                       }
                       String srcFileName;
                       if (ret == 0) {
                            srcFileName = filename2;
                       } else {
                           srcFileName = "/resources/default/" + puid + "-"+ index;
                       }

                       List<MediaFile> mediaFiles = new ArrayList<>();
                       MediaFile file = new MediaFile();
                       file.setBatchNo(batchNo);
                       file.setCreateTime(new Date());
                       file.setFileType("image");
                       file.setFileName(srcFileName + ".jpg");
                       file.setTaskName(taskName);
                       file.setDevName(devName);
                       file.setResName(resName);
                       mediaFiles.add(file);
                       if (isVideo1 == 1) {
                           file = new MediaFile();
                           file.setBatchNo(batchNo);
                           file.setCreateTime(new Date());
                           file.setFileType("video");
                           file.setFileName(srcFileName + ".mp4");
                           file.setTaskName(taskName);
                           file.setDevName(devName);
                           file.setResName(resName);
                           mediaFiles.add(file);
                       }
                       mediaFileService.saveMediaFiles(mediaFiles);
                       return 0;
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

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 微信推送给用户
            for (int i = 0; i < users.size(); i++) {
                String openid = users.getString(i);
                wxUserService.saveUserMediaFile(openid, batchNo);
                System.out.println("wechat push openid: " + openid + ", batchNo: " + batchNo);
                String url = domain + "/user-device-detail/" + batchNo;
                Map<String, Object> data = new HashMap<>();
                data.put("taskNo", task.getName());
                data.put("triggerTime", sdf1.format(new Date()));
                wxUserService.sendTemplateMessage(openid, templateId, url, data);
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

    private int recordMedia(long session,String puid, int index, int isVideo,int duration, String filename) {
        return VideoSDK.recordMedia(session, puid, index, isVideo, duration, filename);
    }

    private int recordMedia(String address, String username, String password, String epid,String puid, int index, int isVideo,int duration, String filename) {
        return VideoSDK.recordMediaNew(address,username, password, epid, puid, index, isVideo, duration, filename);
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
