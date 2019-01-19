package com.power.wechatpush.service;

import com.power.wechatpush.dao.MediaFileDao;
import com.power.wechatpush.dao.entity.MediaFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MediaFileService {

    @Autowired
    private MediaFileDao mediaFileDao;

    @Transactional
    public void saveMediaFiles(List<MediaFile> mediaFiles) {
        mediaFileDao.saveMediaFiles(mediaFiles);
    }

    public List<MediaFile> queryMediaFileByBatchNo(String batchNo) {
        return mediaFileDao.queryMediaFiles(batchNo, -1, -1);
    }

    public MediaFile getMediaFile(Long id) {
        return mediaFileDao.getMediaFile(id);
    }
}
