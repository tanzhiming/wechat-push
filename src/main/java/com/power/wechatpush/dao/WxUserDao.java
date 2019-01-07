package com.power.wechatpush.dao;

import com.power.wechatpush.dao.entity.WxUser;
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
public class WxUserDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public void deleteUser(String openid) {
        String sql = "delete from tb_wx_user where openid = :openid";
        Map<String, Object> param = new HashMap<>();
        param.put("openid", openid);
        jdbcTemplate.update(sql, param);
    }

    public Long saveUser(WxUser user) {
        String sql ="insert into tb_wx_user(openid, nickname, sex, city, country, province, headimgurl, subscribe, remark)";
        sql += "values (:openid, :nickname, :sex, :city, :country, :province, :headimgurl, :subscribe, :remark)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(user), keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void unSubscribe(String openid) {
        String sql = "update tb_wx_user set subscribe = 0 where openid = :openid";
        Map<String, Object> param = new HashMap<>();
        param.put("openid", openid);
        jdbcTemplate.update(sql, param);
    }

    public void updateRemark(String openid, String remark) {
        String sql = "update tb_wx_user set remark = :remark where openid = :openid";
        Map<String, Object> param = new HashMap<>();
        param.put("openid", openid);
        param.put("remark", remark);
        jdbcTemplate.update(sql, param);
    }


    public List<WxUser> queryUsers(int start, int maxResults) {

        StringBuilder sql = new StringBuilder("select openid, nickname, sex, city, country, province, headimgurl, subscribe, remark from tb_wx_user limit :start, :maxResults");
        Map<String, Object> param = new HashMap<>();
        param.put("start", start);
        param.put("maxResults", maxResults);
        List<WxUser> exportFiles = jdbcTemplate.query(sql.toString(), param, new BeanPropertyRowMapper(WxUser.class));
        return exportFiles;
    }


    public int countUsers() {
        String sql = "select count(1) from tb_wx_user";
        Long count = jdbcTemplate.queryForObject(sql, new HashMap<>(), Long.class);
        return count.intValue();
    }

}
