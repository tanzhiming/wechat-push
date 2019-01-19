package com.power.wechatpush.dao;

import com.power.wechatpush.dao.entity.MediaFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MediaFileDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void saveMediaFiles(List<MediaFile> mediaFiles) {
        String sql = "insert into tb_media_file(batch_no, task_name, file_type, file_name, create_time, dev_name, res_name)" +
                " values (:batchNo, :taskName, :fileType, :fileName, :createTime, :devName, :resName)";
        jdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(mediaFiles));
    }

    public void saveMediaFile(MediaFile mediaFiles) {
        String sql = "insert into tb_media_file(batch_no, task_name, file_type, file_name, create_time)" +
                " values (:batchNo, :taskName, :fileType, :fileName, :createTime)";
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(mediaFiles));
    }

    public MediaFile getMediaFile(Long id) {
        String sql = "select id, batch_no batchNo, task_name taskName, file_type fileType, file_name fileName, create_time createTime,dev_name devName, res_name resName from tb_media_file where id=:id";
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        List<MediaFile> mediaFiles = jdbcTemplate.query(sql, param, new BeanPropertyRowMapper<>(MediaFile.class));
        if (mediaFiles != null && mediaFiles.size() > 0) {
            return mediaFiles.get(0);
        }
        return null;
    }

    public List<MediaFile> queryMediaFiles(String batchNo, int start, int maxResults) {
        StringBuilder sql = new StringBuilder("select id, batch_no batchNo, task_name taskName, file_type fileType, file_name fileName, create_time createTime,dev_name devName, res_name resName from tb_media_file where 1=1");
        Map<String, Object> param = new HashMap<>();
        if (!StringUtils.isEmpty(batchNo)) {
            sql.append(" and batch_no = :batchNo");
            param.put("batchNo", batchNo);
        }
        sql.append(" order by create_time desc");
        if (start != -1) {
            sql.append(" limit :start, :maxResults");
            param.put("start", start);
            param.put("maxResults", maxResults);
        }
        List<MediaFile> mediaFiles = jdbcTemplate.query(sql.toString(), param, new BeanPropertyRowMapper(MediaFile.class));
        return mediaFiles;
    }

    public int countMediaFile(String batchNo) {
        String sql = "select count(1) from tb_media_file where 1=1";
        Map<String, Object> param = new HashMap<>();
        if (!StringUtils.isEmpty(batchNo)) {
            sql += " and batch_no = :batchNo";
            param.put("batchNo", batchNo);
        }
        Long count = jdbcTemplate.queryForObject(sql, param, Long.class);
        return count.intValue();
    }



}


