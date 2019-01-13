package com.power.wechatpush.service;

import com.power.wechatpush.dao.TaskDao;
import com.power.wechatpush.dao.entity.Task;
import com.power.wechatpush.dao.entity.TaskLog;
import com.power.wechatpush.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TaskService {

    @Autowired
    private TaskDao taskDao;

    public List<Task> listTasks(){
        return taskDao.listTasks();
    }

    public Task getTask(Long id){
        return taskDao.getTask(id);
    }


    public void deleteTask(Long id){
        taskDao.deleteTask(id);
    }

    @Transactional
    public Task saveTask(Task task) {
        return taskDao.saveTask(task);
    }

    @Transactional
    public Task updateTask(Task task) {
        return taskDao.updateTask(task);
    }

    @Transactional
    public TaskLog insertTaskLog(TaskLog taskLog) {
        return taskDao.insertTaskLog(taskLog);
    }

    public Page<TaskLog> getPageForTaskLog(int page, int rows) {
        int start = (page - 1) * rows;
        int total = taskDao.counTaskLog();
        List<TaskLog> taskLogs = taskDao.queryTaskLogs(start, rows);
        return Page.create(total, taskLogs);
    }
}