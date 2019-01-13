package com.power.wechatpush.controller;

import com.alibaba.fastjson.JSONObject;
import com.power.wechatpush.dao.entity.WxUser;
import com.power.wechatpush.service.WxUserService;
import com.power.wechatpush.util.Page;
import com.power.wechatpush.wechat.AccessTokenService;
import com.power.wechatpush.wechat.domain.WxError;
import com.power.wechatpush.wechat.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class WxUserController {

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private AccessTokenService accessTokenService;

    private static final String UPDATE_REMARK_URL= "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=%s";

    @GetMapping("/list")
    public Page<WxUser> getUsers(@RequestParam(name="page", required = true) Integer page,
                                 @RequestParam(name="rows", required = true) Integer rows) {
        return wxUserService.getPageForUser(page, rows);
    }

    @PutMapping("/remark")
    public boolean updateRemark(@RequestParam(name="openid", required = true) String openid,
                                @RequestParam(name="remark", required = true) String remark) {

        String json = String.format("{\"openid\":\"%s\",  \"remark\":\"%s\"}", openid, remark);
        String respJson = HttpUtil.doPostJson(String.format(UPDATE_REMARK_URL, accessTokenService.getAccessToken().getAccessToken()), json);
        WxError wxError = JSONObject.parseObject(respJson, WxError.class);
        if (wxError.getErrcode() == 0) {
            wxUserService.updateRemark(openid, remark);
        } else {
            return false;
        }
        return true;
    }

}
