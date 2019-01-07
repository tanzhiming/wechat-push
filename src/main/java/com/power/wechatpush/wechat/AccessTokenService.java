package com.power.wechatpush.wechat;

import com.alibaba.fastjson.JSONObject;
import com.power.wechatpush.wechat.domain.AccessToken;
import com.power.wechatpush.wechat.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AccessTokenService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenService.class);

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Value("${wx.token}")
    private String token;

    @Value("${wx.AppID}")
    private String appID;

    @Value("${wx.AppSecret}")
    private String appSecret;

    public AccessToken accessToken;

    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                retriveAccessToken();
            }
        }, 0, 7000, TimeUnit.SECONDS);
    }

    public void retriveAccessToken() {
        String reqUrl = String.format(ACCESS_TOKEN_URL, appID, appSecret);
        String respMessage = HttpUtil.doGet(reqUrl);
        JSONObject jsonObject = JSONObject.parseObject(respMessage);
        String accessTokenStr = jsonObject.getString("access_token");
        if (StringUtils.isEmpty(accessTokenStr)) {
            LOGGER.error("access_token retrive error, {}", jsonObject.toJSONString());
        }
        accessToken = new AccessToken();
        accessToken.setAccessToken(accessTokenStr);
        accessToken.setExpiresIn(jsonObject.getIntValue("expires_in"));
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }
}
