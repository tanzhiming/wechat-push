package com.power.wechatpush.dao.entity;

import java.io.Serializable;

public class Staff implements Serializable {

    private static final long serialVersionUID = -3565631305099172705L;

    public static final int STATUS_ENABLE = 1;
    public static final int STATUS_FORBID = 0;



    Long id;
    String name;
    String password;
    Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
