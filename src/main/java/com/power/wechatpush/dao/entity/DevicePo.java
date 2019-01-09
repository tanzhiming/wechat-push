package com.power.wechatpush.dao.entity;

import com.power.wechatpush.video.Device;
import com.power.wechatpush.video.Resource;

import java.util.ArrayList;
import java.util.List;

public class DevicePo {

    private Long id;
    private String puid;
    private String name;
    private String type;
    private int index;
    private int usable;
    private String description;
    private Long parentId;
    private int deviceFlag;

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getUsable() {
        return usable;
    }

    public void setUsable(int usable) {
        this.usable = usable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDeviceFlag() {
        return deviceFlag;
    }

    public void setDeviceFlag(int deviceFlag) {
        this.deviceFlag = deviceFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }


    public static DevicePo fromDevice(Device device) {
        DevicePo devicePo = new DevicePo();
        devicePo.setDescription(device.getDescription());
        devicePo.setDeviceFlag(1);
        devicePo.setIndex(device.getIndex());
        devicePo.setName(device.getName());
        devicePo.setPuid(device.getPuid());
        devicePo.setType(device.getName());
        devicePo.setUsable(device.getUsable());
        return devicePo;
    }

    public static List<DevicePo> fromResources(Long devId, Device device) {

        if (device == null) {
            return null;
        }
        List<DevicePo> devicePos = new ArrayList<>();
        if (device.getResources() != null) {
            for (Resource resource : device.getResources()) {
                DevicePo devicePo = new DevicePo();
                devicePo.setDescription(resource.getDescription());
                devicePo.setDeviceFlag(0);
                devicePo.setIndex(resource.getIndex());
                devicePo.setName(resource.getName());
                devicePo.setPuid(resource.getPuid());
                devicePo.setType(resource.getName());
                devicePo.setUsable(resource.getUsable());
                devicePo.setParentId(devId);
                devicePos.add(devicePo);
            }
        }
        return devicePos;
    }

}
