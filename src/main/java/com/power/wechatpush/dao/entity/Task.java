package com.power.wechatpush.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.quartz.JobKey;

import java.io.Serializable;

public class Task implements Serializable {

    private static final long serialVersionUID = 2821380144200895138L;
    private Long id;
    private String name;
    private Integer type;
    private String cronExpress;
    private String detail;
    private String jobName;
    private String jobGroup;

    public static final int TYPE_MANUAL = 1;
    public static final int TYPE_TIMING = 2;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCronExpress() {
        return cronExpress;
    }

    public void setCronExpress(String cronExpress) {
        this.cronExpress = cronExpress;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public void setJobKey(JobKey jobKey) {
        this.setJobName(jobKey.getName());
        this.setJobGroup(jobKey.getGroup());
    }

    @JsonIgnore
    public JobKey getJobKey() {
        JobKey jobKey = new JobKey(this.getJobName(), this.getJobGroup());
        return jobKey;
    }
}
