package com.power.wechatpush.job;

public class TaskLogWriter {

    private StringBuilder logContent;

    public TaskLogWriter() {
        this.logContent = new StringBuilder();
    }
    public void write(String content) {
        logContent.append(content);
        logContent.append("\r\n");
    }

    public String getLogContent() {
        return logContent.toString();
    }
}
