package com.power.wechatpush.service;

import com.alibaba.fastjson.JSONObject;
import com.power.wechatpush.dao.WxUserDao;
import com.power.wechatpush.dao.entity.WxUser;
import com.power.wechatpush.util.Page;
import com.power.wechatpush.wechat.AccessTokenService;
import com.power.wechatpush.wechat.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class WxUserService {

    public static final String TEMPLATE_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";

    @Autowired
    private WxUserDao wxUserDao;

    @Autowired
    private AccessTokenService accessTokenService;

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


    public String sendTemplateMessage(String openid, String templateId, String url, Map<String, Object> data) {
        String accessToken = accessTokenService.getAccessToken().getAccessToken();
        String requestUrl = String.format(TEMPLATE_MESSAGE, accessToken);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", openid);
        jsonObject.put("template_id", templateId);
        jsonObject.put("topcolor", "#FF0000");
        jsonObject.put("url", url);
        if (data != null) {
            JSONObject jsonData = new JSONObject();
            JSONObject jsonValue = null;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                jsonValue = new JSONObject();
                jsonValue.put("value", entry.getValue());
                jsonValue.put("color", "#173177");
                jsonData.put(entry.getKey(), jsonValue);
            }
            jsonObject.put("data", jsonData);
        }
        return HttpUtil.doPostJson(requestUrl, jsonObject.toJSONString());
    }

    @Transactional
    public void saveUserMediaFile(String openId, String batchNo) {
        wxUserDao.saveUserMediaFile(openId, batchNo);
    }
}
