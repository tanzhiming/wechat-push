package com.power.wechatpush.dao;

import com.power.wechatpush.dao.entity.DevicePo;
import com.power.wechatpush.video.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class DeviceDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public void saveDevices(List<DevicePo> deviceList) {
        String sql = "insert into tb_device(puid,`name`, `type`, `index`, usable, description, parent_id, device_flag) ";
        sql += "values (:ppuid, :name, :type, :index, :usable, :description, :parentId, :deviceFlag)";
        jdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(deviceList));
    }

    public long saveDevice(DevicePo device) {
        String sql = "insert into tb_device(puid,`name`, `type`, `index`, usable, description, parent_id, device_flag) ";
        sql += "values (:ppuid, :name, :type, :index, :usable, :description, :parentId, :deviceFlag)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(device), keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void deleteAllDevices() {
        String sql = "delete from tb_device";
        jdbcTemplate.update(sql, new HashMap<>());
    }
}
