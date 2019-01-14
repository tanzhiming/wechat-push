package com.power.wechatpush.dao;

import com.power.wechatpush.dao.entity.Task;
import com.power.wechatpush.dao.entity.TaskLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TaskDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<Task> listTasks(){
        String sql = "select f_id id, f_name name, f_type type, f_cron_express cronExpress, f_detail detail, f_job_name jobName, f_job_group jobGroup from tb_task";
        List<Task> tasks = jdbcTemplate.query(sql, new HashMap<>(), new BeanPropertyRowMapper(Task.class));
        return tasks;
    }

    public Task getTask(Long id){
        String sql = "select f_id id, f_name name, f_type type, f_cron_express cronExpress, f_detail detail, f_job_name jobName, f_job_group jobGroup from tb_task where f_id = :id";
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        List<Task> tasks = jdbcTemplate.query(sql, param, new BeanPropertyRowMapper(Task.class));
        Task task = null;
        if (tasks != null && tasks.size() > 0) {
            task = tasks.get(0);
        }
        return task;
    }


    public void deleteTask(Long id){
        String sql = "delete from tb_task where f_id = :id";
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        jdbcTemplate.update(sql, param);
    }

    public Task saveTask(Task task) {
        String sql = "insert into tb_task(f_name, f_type, f_cron_express, f_detail, f_job_name, f_job_group) values (:name, :type, :cronExpress, :detail, :jobName, :jobGroup)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(task), keyHolder);
        Long id = keyHolder.getKey().longValue();
        task.setId(id);
        return task;
    }

    public Task updateTask(Task task) {
        StringBuilder sql = new StringBuilder("update tb_task set f_name = :name, f_type = :type, f_detail = :detail");
        if (task.getCronExpress() != null) {
            sql.append(", f_cron_express = :cronExpress");
        } else {
            sql.append(", f_cron_express = null");
        }
        if (task.getJobName() != null) {
            sql.append(", f_job_name = :jobName");
        } else {
            sql.append(", f_job_name = null");
        }
        if (task.getJobGroup() != null) {
            sql.append(", f_job_group = :jobGroup");
        } else {
            sql.append(", f_job_group = null");
        }
        sql.append(" where f_id = :id");
        jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(task));
        return task;
    }


    public TaskLog insertTaskLog(TaskLog taskLog) {
        String sql = "insert into tb_task_log(f_task_name, f_task_type, f_log, f_create_time) values (:taskName, :taskType, :log, :createTime)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(taskLog), keyHolder);
        Long id = keyHolder.getKey().longValue();
        taskLog.setId(id);
        return taskLog;
    }

    public List<TaskLog> queryTaskLogs(int start, int maxResults) {
        StringBuilder sql = new StringBuilder("select f_id id, f_task_name taskName, f_task_type TaskType, f_log log, f_create_time createTime from tb_task_log");
        sql.append(" order by f_create_time desc limit :start, :maxResults");
        Map<String, Object> param = new HashMap<>();
        param.put("start", start);
        param.put("maxResults", maxResults);
        List<TaskLog> taskLogs = jdbcTemplate.query(sql.toString(), param, new BeanPropertyRowMapper(TaskLog.class));
        return taskLogs;
    }

    public int counTaskLog() {
        String sql = "select count(1) from tb_task_log";
        Long count = jdbcTemplate.queryForObject(sql, new HashMap<>(), Long.class);
        return count.intValue();
    }

}
