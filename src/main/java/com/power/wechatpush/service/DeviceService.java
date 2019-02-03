package com.power.wechatpush.service;

import com.power.wechatpush.dao.DeviceDao;
import com.power.wechatpush.dao.entity.DevicePo;
import com.power.wechatpush.util.Page;
import com.power.wechatpush.video.Device;
import com.power.wechatpush.video.VideoSDK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceDao deviceDao;
    static {
        System.loadLibrary("video");
    }

    @Value("${video.address}")
    private String address;

    @Value("${video.username}")
    private String username;

    @Value("${video.password}")
    private String password;

    @Value("${video.epid}")
    private String epid;

    @Transactional
    public boolean buildDevices() {
        long start = System.currentTimeMillis();
        int ret = VideoSDK.initialize();
        LOG.info("初始化完成, ret={}", ret);
        long[] sesson = new long[1];
        ret = VideoSDK.open(address, username, password, epid, sesson);
        LOG.info("已打开连接, ret={}", ret);

        try {

            deviceDao.deleteAllDevices();

            int offset = 0;
            List<Device> deviceList = null;
            do {
                deviceList = VideoSDK.getDeviceList(sesson[0], offset);
                if (deviceList != null && deviceList.size() > 0) {
                    for (Device device : deviceList) {
                        long id = deviceDao.saveDevice(DevicePo.fromDevice(device));
                        deviceDao.saveDevices(DevicePo.fromResources(id, device));
                    }
                }
                offset += deviceList.size();
            } while (deviceList != null && deviceList.size() == 500);
        } finally {
            ret = VideoSDK.close(sesson[0]);
            LOG.info("已关闭连接, ret={}", ret);
            ret = VideoSDK.terminate();
            LOG.info("已终止, ret={}", ret);
        }
        LOG.info("spent time : {}ms", System.currentTimeMillis()-start);
        return true;
    }

    public Page<DevicePo> queryDevices(String name, int page, int rows) {
        int start = (page - 1) * rows;
        List<DevicePo> deviceList = deviceDao.queryDevices(name,"SELF", null, start, rows);
        int count = deviceDao.countDevices(name, "SELF", null);
        if (deviceList != null) {
            for (DevicePo dp : deviceList) {
                List<DevicePo> children = deviceDao.queryDevices(null, "IV", dp.getId(), -1, -1);
                dp.setChildren(children);
            }
        }
        return Page.create(count, deviceList);
    }

    public DevicePo getDeviceById(Long id) {
        return deviceDao.getDeviceById(id);
    }

}
