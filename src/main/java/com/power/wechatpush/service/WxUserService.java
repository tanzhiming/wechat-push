package com.power.wechatpush.service;

import com.power.wechatpush.dao.WxUserDao;
import com.power.wechatpush.dao.entity.WxUser;
import com.power.wechatpush.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WxUserService {

    @Autowired
    private WxUserDao wxUserDao;

    @Transactional
    public Long saveUser(WxUser user) {
        wxUserDao.deleteUser(user.getOpenid());
       return wxUserDao.saveUser(user);
    }

    public void unSubscribe(String openid) {
        wxUserDao.unSubscribe(openid);
    }

    public Page<WxUser> getPageForUser(int page, int rows) {
        int start = (page - 1) * rows;
        int total = wxUserDao.countUsers();
        List<WxUser> wxUsers = wxUserDao.queryUsers(start, rows);
        return Page.create(total, wxUsers);
    }

    public void updateRemark(String openid, String remark) {
        wxUserDao.updateRemark(openid, remark);
    }
}
