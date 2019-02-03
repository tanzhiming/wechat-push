package com.power.wechatpush.dao.entity;

public class UserFile {

    private Long id;
    private String openId;
    private String mediaBatchNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getMediaBatchNo() {
        return mediaBatchNo;
    }

    public void setMediaBatchNo(String mediaBatchNo) {
        this.mediaBatchNo = mediaBatchNo;
    }
}
