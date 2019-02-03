package com.power.wechatpush.dao;

import com.power.wechatpush.dao.entity.DevicePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DeviceDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public void saveDevices(List<DevicePo> deviceList) {
        String sql = "insert into tb_device(puid, name, type, `index`, usable, description, parent_id) ";
        sql += "values (:puid, :name, :type, :index, :usable, :description, :parentId)";
        jdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(deviceList));
    }

    public long saveDevice(DevicePo device) {
        String sql = "insert into tb_device(puid, name, type, `index`, usable, description, parent_id) ";
        sql += "values (:puid, :name, :type, :index, :usable, :description, :parentId)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(device), keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void deleteAllDevices() {
        String sql = "delete from tb_device";
        jdbcTemplate.update(sql, new HashMap<>());
    }

    public DevicePo getDeviceById(Long id) {
        String sql = "select id, puid, name, type, `index`, usable, description, parent_id from tb_device where id=:id";
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        List<DevicePo> devicePos = jdbcTemplate.query(sql, param, new BeanPropertyRowMapper(DevicePo.class));
        if (devicePos != null && devicePos.size() > 0) {
            return devicePos.get(0);
        }
        return null;
    }

    public List<DevicePo> queryDevices(String name, String type, Long parentId, int start, int maxResults, boolean excludeNoExistsChild) {
        StringBuilder sql = new StringBuilder("select id, puid, name, type, `index`, usable, description, parent_id from tb_device t where 1=1");
        Map<String, Object> param = new HashMap<>();
        if (!StringUtils.isEmpty(name)) {
            param.put("name", '%'+name + '%');
            sql.append(" and name like :name");
        }
        if (!StringUtils.isEmpty(type)) {
            param.put("type", type);
            sql.append(" and type = :type");
        }

        if (parentId != null) {
            sql.append(" and parent_id = :parentId");
            param.put("parentId", parentId);
        }

        if (excludeNoExistsChild) {
            sql.append(" and exists ( select 1 from tb_device a where a.parent_id = t.id and a.type = 'IV')");
        }

        sql.append(" order by usable desc, name");
        if (maxResults != -1) {
            sql.append("  limit :start, :maxResults");
            param.put("start", start);
            param.put("maxResults", maxResults);
        }
        return jdbcTemplate.query(sql.toString(), param, new BeanPropertyRowMapper<>(DevicePo.class));
    }


    public int countDevices(String name, String type, Long parentId, boolean excludeNoExistsChild) {
        StringBuilder sql = new StringBuilder("select count(1) from tb_device t where 1=1");
        Map<String, Object> param = new HashMap<>();
        if (!StringUtils.isEmpty(name)) {
            param.put("name", '%'+name + '%');
            sql.append(" and name like :name");
        }
        if (!StringUtils.isEmpty(type)) {
            param.put("type", type);
            sql.append(" and type = :type");
        }

        if (parentId != null) {
            sql.append(" and parent_id = :parentId");
            param.put("parentId", parentId);
        }

        if (excludeNoExistsChild) {
            sql.append(" and exists ( select 1 from tb_device a where a.parent_id = t.id and a.type = 'IV')");
        }

        Long count = jdbcTemplate.queryForObject(sql.toString(), param, Long.class);
        return count.intValue();
    }

    public int countDevices(String name, String type, Long parentId) {
        boolean excludeNoExistsChild = false;
        if (parentId == null) {
            excludeNoExistsChild = true;
        }
        return countDevices(name, type, parentId, excludeNoExistsChild);
    }

    public List<DevicePo> queryDevices(String name, String type, Long parentId, int start, int maxResults) {
        boolean excludeNoExistsChild = false;
        if (parentId == null) {
            excludeNoExistsChild = true;
        }
        return this.queryDevices(name, type, parentId, start, maxResults, excludeNoExistsChild);
    }

}
