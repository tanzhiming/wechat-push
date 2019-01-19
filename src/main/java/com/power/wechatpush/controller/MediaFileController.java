package com.power.wechatpush.controller;

import com.power.wechatpush.dao.entity.MediaFile;
import com.power.wechatpush.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Controller
public class MediaFileController {

    @Autowired
    private MediaFileService mediaFileService;

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
}
