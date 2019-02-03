package com.power.wechatpush.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.wechatpush.dao.entity.MediaFile;
import com.power.wechatpush.service.MediaFileService;
import com.power.wechatpush.service.WxUserService;
import com.power.wechatpush.util.CommonUtil;
import com.power.wechatpush.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MediaFileController {


    @Value("${resource.dir}")
    private String resourceDir;

    @Value("${wx.message.templateId}")
    private String templateId;

    @Value("${wx.my.domain}")
    private String domain;

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private WxUserService wxUserService;

    @GetMapping("/user-device-detail/{batchNo}")
    public String getWxDeviceList(@PathVariable String batchNo, Model model) {
        List<MediaFile> mediaFiles = mediaFileService.queryMediaFileByBatchNo(batchNo);
        model.addAttribute("mediaFiles", mediaFiles);
        return "user-device-detail";
    }

    @GetMapping(value = "/dev-res/{id}")
    public void getVideo(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        MediaFile mediaFile = mediaFileService.getMediaFile(id);
        File file = new File(mediaFile.getFileName());
//        FileInputStream inputStream = new FileInputStream(file);
//        byte[] bytes = new byte[inputStream.available()];
//        inputStream.read(bytes, 0, inputStream.available());
        FileInputStream in = new FileInputStream(file);
        byte[] buf = new byte[1024];
        response.setContentLength(in.available());
        if ("image".equals(mediaFile.getFileType())) {
            response.setContentType("image/jpeg");
        } else if ("video".equals(mediaFile.getFileType())) {
            response.setContentType("video/mp4");
        }
        OutputStream out = response.getOutputStream();
        int len = -1;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
            out.flush();
        }
        out.flush();
    }

    @GetMapping(value = "/play/{id}")
    public String toPlay(@PathVariable Long id, Model model) throws IOException {
        MediaFile mediaFile = mediaFileService.getMediaFile(id);
        model.addAttribute("mediaFile", mediaFile);
        return "play";
    }

    @GetMapping("/user-file")
    public Page<MediaFile> getPageForUserMeidaFile(@RequestParam(name="openId", required = true) String openId,
                                                   @RequestParam(name="type") String type,
                                                   @RequestParam(name="page", required = true) int page,
                                                   @RequestParam(name="rows", required = true)int rows) {
        return mediaFileService.getPageForUserMeidaFile(openId, type, page, rows);
    }

    @PostMapping("/send-alarm-file")
    @ResponseBody
    public MediaFile sendAlarmFile(@RequestParam(name="detail", required = true) String detail, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(detail);
        System.out.println(file.getName());
        JSONObject detailObj = JSONObject.parseObject(detail);
        JSONArray users = detailObj.getJSONArray("users");
        JSONArray deviceResources = detailObj.getJSONArray("deviceResource");

        String devName = "";
        String resName = "";
        String puid = "";
        int index = 0;
        if (deviceResources != null && deviceResources.size() > 0) {
            JSONObject res = deviceResources.getJSONObject(0);
            puid = res.getString("puid");
            index = res.getIntValue("index");
            devName = res.getString("devName");
            resName = res.getString("resName");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        File dir = new File(resourceDir, dateStr);
        String requestPath = "/resources/" + dateStr;
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String partName = puid + "-"+ index + "-" + System.currentTimeMillis();
        String filename = dir + File.separator + partName;
        String filename2 = requestPath + "/"+ partName;
        String batchNo = CommonUtil.getBatchNo();
        InputStream ins = file.getInputStream();
        OutputStream out = new FileOutputStream(filename + getExtensionName(file.getOriginalFilename()));
        MediaFile mediaFile;
        try {
            byte[] buf = new byte[4069];
            int len;
            while ((len = ins.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            mediaFile = new MediaFile();
            mediaFile.setBatchNo(batchNo);
            mediaFile.setCreateTime(new Date());
            mediaFile.setFileType("alarm");
            mediaFile.setFileName(filename2 + getExtensionName(file.getOriginalFilename()));
            mediaFile.setTaskName("alarmTask");
            mediaFile.setDevName(devName);
            mediaFile.setResName(resName);
            mediaFile = mediaFileService.saveMediaFile(mediaFile);

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 微信推送给用户
            for (int i = 0; i < users.size(); i++) {
                String openid = users.getString(i);
                wxUserService.saveUserMediaFile(openid, batchNo);
                System.out.println("wechat push openid: " + openid + ", batchNo: " + batchNo);
                String url = domain + "/user-device-detail/" + batchNo;
                Map<String, Object> data = new HashMap<>();
                data.put("taskNo", "alarmTask");
                data.put("triggerTime", sdf1.format(new Date()));
                String res = wxUserService.sendTemplateMessage(openid, templateId, url, data);
                System.out.println(res);
            }
        } finally {
            if (ins != null) {
                ins.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return mediaFile;
    }


    private String getExtensionName(String filename) {
        if (filename == null) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
