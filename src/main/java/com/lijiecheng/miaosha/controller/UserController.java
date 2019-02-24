package com.lijiecheng.miaosha.controller;

import com.lijiecheng.miaosha.domain.MiaoshaUser;
import com.lijiecheng.miaosha.redis.RedisService;
import com.lijiecheng.miaosha.result.Result;
import com.lijiecheng.miaosha.service.MiaoshaUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Autowired
    MiaoshaUserService userService;
	
	@Autowired
    RedisService redisService;
	
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
//        log.info(String.valueOf(user.getId()));
        return Result.success(user);
    }
    
}
