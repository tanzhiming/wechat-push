package com.power.wechatpush.dao.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import org.quartz.JobKey;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    @JsonIgnore
    private JSONObject detailObj;

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
        try {
            detailObj = JSONObject.parseObject(detail);
        } catch (Exception e) {

        }
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

    public String getUserNames() {
        if (detailObj != null) {
            return detailObj.getString("userNames");
        }
        return "";
    }

    public String getPushType() {
        String pushType = "";
        if (detailObj != null) {
            JSONArray jsonArray = detailObj.getJSONArray("pushType");
            if (jsonArray != null) {
                List<String> arr = new ArrayList<>();
                if (jsonArray.contains("image")) {
                   arr.add("图片");
                }
                if (jsonArray.contains("video")) {
                    arr.add("视频");
                }
                return Joiner.on(",").join(arr.toArray());
            }
        }
        return pushType;
    }

    public String getStartTime() {
        if (detailObj != null) {
            JSONObject timeObj = detailObj.getJSONObject("startTime");
            if (timeObj != null) {
                String timeStr = "";
                int hours = timeObj.getIntValue("hours");
                int minutes = timeObj.getIntValue("minutes");
                int seconds = timeObj.getIntValue("seconds");
                if (hours >= 10) {
                    timeStr += hours;
                } else {
                    timeStr += "0" + hours;
                }
                timeStr += ":";
                if (minutes >= 10) {
                    timeStr += minutes;
                } else {
                    timeStr += "0" + minutes;
                }
                timeStr += ":";
                if (seconds >= 10) {
                    timeStr += seconds;
                } else {
                    timeStr += "0" + seconds;
                }
                return timeStr;
            }
        }
        return "";
    }
    public String getDuration() {
        if (detailObj != null) {
            return "" + detailObj.getIntValue("duration");
        }
        return "";
    }

}
