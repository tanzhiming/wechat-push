package com.power.wechatpush.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/push")
public class PushController {

    @GetMapping("/config/{openId}")
    public String toPushConfig(@PathVariable String openId,
                               @RequestParam(value = "nickname", required = true) String nickname,
                               @RequestParam(value = "remark", required = false) String remark, Model model) {
        model.addAttribute("nickname", nickname);
        model.addAttribute("remark", remark);
        model.addAttribute("openId", openId);
        return "push-config";
    }
}
