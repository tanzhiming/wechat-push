package com.power.wechatpush.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.wechatpush.dao.entity.MediaFile;
import com.power.wechatpush.service.MediaFileService;
import com.power.wechatpush.service.WxUserService;
import com.power.wechatpush.util.CommonUtil;
import com.power.wechatpush.util.Page;
import com.power.wechatpush.wechat.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MediaFileController {


    @Value("${resource.dir}")
    private String resourceDir;

    @Value("${wx.message.templateId}")
    private String templateId;

    @Value("${wx.my.domain}")
    private String domain;

    @Value("${wx.AppID}")
    private String appId;

    @Value("${wx.AppSecret}")
    private String appSecret;


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
                data.put("first", "");
                data.put("keyword1", "alarmTask");
                data.put("keyword2", sdf1.format(new Date()));
                data.put("remark", "");
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

    @GetMapping("/user-history-file")
    public String getUserHistoryFile(@CookieValue (value = "openId", required = false)  String openId,
                                              @RequestParam("type")  String type,
                                              @RequestParam("page") int page,
                                              @RequestParam("rows") int rows, Model model) throws UnsupportedEncodingException {
        if (openId != null) {
            Page<MediaFile> pages = mediaFileService.getPageForUserMeidaFile(openId, type , page, rows);
            model.addAttribute("pages", pages);
            model.addAttribute("mediaType", type);
            model.addAttribute("mediaPage", page);
            int totalPage = (pages.getTotal() - 1) / rows + 1;
            model.addAttribute("hasNextPage", page < totalPage);
            return "user-history-file";
        }
        String redirect = String.format("redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect",
            appId, URLEncoder.encode(domain +"/user-history-callback", "utf-8"), type);

        return redirect;
    }



    @GetMapping("/user-history-callback")
    public String userHistoryCallback(@RequestParam("code") String code, @RequestParam("state") String type, Model model, HttpServletResponse response) {
        String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
               appId, appSecret, code);
        String jsonStr = HttpUtil.doGet(url);
        JSONObject accessToken = JSON.parseObject(jsonStr);
        String openId = accessToken.getString("openid");
        Page<MediaFile> pages = mediaFileService.getPageForUserMeidaFile(openId, type , 1, 50);
        model.addAttribute("pages", pages);
        model.addAttribute("mediaPage", 1);
        model.addAttribute("mediaType", type);
        int totalPage = (pages.getTotal() - 1) / 50 + 1;
        model.addAttribute("hasNextPage", 1 < totalPage);
        Cookie cookie = new Cookie("openId", openId);
        response.addCookie(cookie);
        return "user-history-file";
    }
}
