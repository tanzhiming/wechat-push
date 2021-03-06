package com.power.wechatpush.wechat;

import com.alibaba.fastjson.JSONObject;
import com.power.wechatpush.dao.entity.WxUser;
import com.power.wechatpush.service.WxUserService;
import com.power.wechatpush.wechat.domain.AccessToken;
import com.power.wechatpush.wechat.util.HttpUtil;
import com.power.wechatpush.wechat.util.MessageUtil;
import com.power.wechatpush.wechat.util.SignUtil;
import com.power.wechatpush.wechat.util.XmlUtil;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RestController
@RequestMapping("/wx")
public class WxController {

    private static Logger LOG = LoggerFactory.getLogger(WxController.class);

    @Value("${wx.token}")
    private String token;

    @Value("${wx.AppID}")
    private String appID;

    @Value("${wx.AppSecret}")
    private String appSecret;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private WxUserService wxUserService;

    private static final String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

    @GetMapping(produces = {"text/plain;charset=UTF-8"})
    public String wxCheck(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr) {
        boolean b = SignUtil.checkSigature(signature, token, timestamp, nonce);
        LOG.info("微信回调检测！{}", b);
        return b ? echostr : "微信签名验证失败";
    }

    @PostMapping
    public void wxAccess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        String message = "success";
        try {
            //把微信返回的xml信息转义成map
            Map<String, String> map = XmlUtil.xmlToMap(req);
            String fromUserName = map.get("FromUserName");//消息来源用户标识
            String toUserName = map.get("ToUserName");//消息目的用户标识
            String msgType = map.get("MsgType");//消息类型
            String content = map.get("Content");//消息内容

            String eventType = map.get("Event");
            if(MessageUtil.MSGTYPE_EVENT.equals(msgType)){//如果为事件类型
                if(MessageUtil.MESSAGE_SUBSCIBE.equals(eventType)){//处理订阅事件
                    // 获取用户信息
                    String jsonUserStr = getUserInfo(fromUserName);
                    WxUser wxUser = JSONObject.parseObject(jsonUserStr, WxUser.class);
                    wxUserService.saveUser(wxUser);
                }else if(MessageUtil.MESSAGE_UNSUBSCIBE.equals(eventType)){//处理取消订阅事件
                    wxUserService.unSubscribe(fromUserName);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }finally{
            out.println(message);
            if(out!=null){
                out.close();
            }
        }
    }

    @GetMapping("/echo")
    public String echo() {
        AccessToken accessToken = accessTokenService.getAccessToken();
        System.out.println(accessToken);
        return "hello";
    }


    @PostMapping("/menu")
    public String menu(@RequestBody String menuJson) {
        AccessToken accessToken = accessTokenService.getAccessToken();
        String postUrl = String.format("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s", accessToken.getAccessToken());
        return HttpUtil.doPostJson(postUrl, menuJson);
    }


    @GetMapping("/menu")
    public String menu() {
        AccessToken accessToken = accessTokenService.getAccessToken();
        String url = String.format("https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s", accessToken.getAccessToken());
        return HttpUtil.doGet(url);
    }

    public String getUserInfo(String openId) {
        return HttpUtil.doGet(String.format(USER_INFO_URL, accessTokenService.getAccessToken().getAccessToken(), openId));
    }

}
