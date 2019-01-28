package com.power.wechatpush.dao;

import com.power.wechatpush.dao.entity.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StaffDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    public Staff findStaffByName(String name){
        String sql = "select f_id id, f_name name, f_password password, f_status status from tb_staff where f_name = :name";
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        List<Staff> staffList = jdbcTemplate.query(sql, param, new BeanPropertyRowMapper(Staff.class));
        Staff staff = null;
        if (staffList != null && staffList.size() > 0) {
            staff = staffList.get(0);
        }
        return staff;
    }
}
