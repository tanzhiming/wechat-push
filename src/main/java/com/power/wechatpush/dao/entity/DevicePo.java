package com.power.wechatpush.dao.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("_parentId")
    private Long parentId;

    private List<DevicePo> children;

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
        DevicePo devicePo = toDevicePo(device);
        return devicePo;
    }

    private static DevicePo toDevicePo(Device device) {
        DevicePo devicePo = new DevicePo();
        devicePo.setDescription(device.getDescription());
        devicePo.setIndex(device.getIndex());
        devicePo.setName(device.getName());
        devicePo.setPuid(device.getPuid());
        devicePo.setType(device.getType());
        devicePo.setUsable(device.getUsable());
        return devicePo;
    }

    private static DevicePo toDevicePo(Resource device) {
        DevicePo devicePo = new DevicePo();
        devicePo.setDescription(device.getDescription());
        devicePo.setIndex(device.getIndex());
        devicePo.setName(device.getName());
        devicePo.setPuid(device.getPuid());
        devicePo.setType(device.getType());
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
                DevicePo devicePo = toDevicePo(resource);
                devicePo.setParentId(devId);
                devicePos.add(devicePo);
            }
        }
        return devicePos;
    }

    public List<DevicePo> getChildren() {
        return children;
    }

    public void setChildren(List<DevicePo> children) {
        this.children = children;
    }
}
