package com.power.wechatpush.controller;

import com.power.wechatpush.dao.entity.DevicePo;
import com.power.wechatpush.service.DeviceService;
import com.power.wechatpush.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Executors;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

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

}
