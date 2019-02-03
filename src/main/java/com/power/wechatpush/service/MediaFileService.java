package com.power.wechatpush.service;

import com.power.wechatpush.dao.MediaFileDao;
import com.power.wechatpush.dao.entity.MediaFile;
import com.power.wechatpush.util.Page;
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

    @Transactional
    public MediaFile saveMediaFile(MediaFile mediaFile) {
        return mediaFileDao.saveMediaFile(mediaFile);
    }

    public List<MediaFile> queryMediaFileByBatchNo(String batchNo) {
        return mediaFileDao.queryMediaFiles(batchNo, -1, -1);
    }

    public MediaFile getMediaFile(Long id) {
        return mediaFileDao.getMediaFile(id);
    }

    public Page<MediaFile> getPageForMeidaFile(String batchNo, int page, int rows) {
        int start = (page - 1) * rows;
        int total = mediaFileDao.countMediaFile(batchNo);
        List<MediaFile> mediaFiles = mediaFileDao.queryMediaFiles(batchNo, start, total);
        return Page.create(total, mediaFiles);
    }


    public Page<MediaFile> getPageForUserMeidaFile(String openId, String type, int page, int rows) {
        int start = (page - 1) * rows;
        int total = mediaFileDao.countUserMediaFile(openId, type);
        List<MediaFile> mediaFiles = mediaFileDao.queryUserMediaFiles(openId, type, start, total);
        return Page.create(total, mediaFiles);
    }
}
