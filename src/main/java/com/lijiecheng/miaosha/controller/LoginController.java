package com.lijiecheng.miaosha.controller;

import com.lijiecheng.miaosha.domain.MiaoshaUser;
import com.lijiecheng.miaosha.result.CodeMsg;
import com.lijiecheng.miaosha.result.Result;
import com.lijiecheng.miaosha.service.MiaoshaUserService;
import com.lijiecheng.miaosha.util.ValidatorUtil;
import com.lijiecheng.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.Validator;

/**
 * @Author: myname
 * @Date: 2019/2/19 2:28
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private MiaoshaUserService userService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        //登录
        String token = userService.login(response, loginVo);
        return Result.success(token);
    }




}
