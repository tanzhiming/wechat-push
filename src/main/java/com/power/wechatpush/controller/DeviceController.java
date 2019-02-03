package com.power.wechatpush.controller;

import com.power.wechatpush.dao.entity.DevicePo;
import com.power.wechatpush.service.DeviceService;
import com.power.wechatpush.util.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/device")
public class DeviceController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    @Value("${resource.dir}")
    private String resourceDir;

    @PostMapping("/build")
    public boolean buildDevice() {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                deviceService.buildDevices();
            }
        });
        return true;
    }

    @GetMapping("/list")
    public Page<DevicePo> queryDevices(@RequestParam(name = "name", required = false) String name,
                                       @RequestParam(name="page", required = true) Integer page,
                                       @RequestParam(name="rows", required = true) Integer rows) {
        return deviceService.queryDevices(name, page, rows);
    }

    @PostMapping("/default-image-and-video")
    public void setDefaultImageAndVideo(@RequestParam("id") Long id,
                                        @RequestParam("image-file")  MultipartFile imageFile,
                                        @RequestParam("video-file") MultipartFile videoFile) {
        DevicePo devicePo = deviceService.getDeviceById(id);
        if (devicePo == null) {
            return;
        }
        File defaultFileDir = new File(resourceDir, "default");
        String imageFileName = devicePo.getPuid() + "-"+ devicePo.getIndex() + ".jpg";
        String videoFileName = devicePo.getPuid() + "-"+ devicePo.getIndex() + ".mp4";
        if (!defaultFileDir.exists()) {
            defaultFileDir.mkdirs();
        }
        InputStream imgIns = null;
        InputStream videoIns = null;
        FileOutputStream imgOs = null;
        FileOutputStream videoOs = null;
        byte[] buf;
        int len;
        try {
            if (imageFile != null) {
                imgIns = imageFile.getInputStream();
                buf = new byte[4069];
                imgOs = new FileOutputStream(new File(defaultFileDir, imageFileName));
                while ((len = imgIns.read(buf)) != -1) {
                    imgOs.write(buf, 0, len);
                }
            }

            if (videoFile != null) {
                videoIns = videoFile.getInputStream();
                buf = new byte[4069];
                videoOs = new FileOutputStream(new File(defaultFileDir, videoFileName));
                while ((len = videoIns.read(buf)) != -1) {
                    videoOs.write(buf, 0, len);
                }
            }

        } catch (IOException e) {
            LOG.error("设置默认图片和视频失败", e);
        } finally {

            try {
                if (imgIns != null) {
                    imgIns.close();
                    imgIns = null;
                }
                if (videoIns != null) {
                    videoIns.close();
                    videoIns = null;
                }
                if (imgOs != null) {
                    imgOs.close();
                    imgOs = null;
                }
                if (videoOs != null) {
                    videoOs.close();
                    videoOs = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
