package com.lijiecheng.miaosha.controller;

import com.lijiecheng.miaosha.domain.MiaoshaUser;
import com.lijiecheng.miaosha.redis.RedisService;
import com.lijiecheng.miaosha.service.MiaoshaUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_list")
    public String list(Model model,MiaoshaUser user){
        model.addAttribute("user", user);
        return "goods_list";
    }

}
