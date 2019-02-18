package com.lijiecheng.miaosha.controller;

import com.lijiecheng.miaosha.domain.User;
import com.lijiecheng.miaosha.redis.KeyPrefix;
import com.lijiecheng.miaosha.redis.RedisService;
import com.lijiecheng.miaosha.redis.UserKey;
import com.lijiecheng.miaosha.result.CodeMsg;
import com.lijiecheng.miaosha.result.Result;
import com.lijiecheng.miaosha.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.imooc.miaosha.result.CodeMsg;
//import com.imooc.miaosha.result.Result;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    //1.rest api json输出 2.页面
    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello,imooc");
//	        return new Result(0, "success", "hello,imooc");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
//	 		return new Result(500102, "XXX");
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "Joshua");
        return "hello";
    }

    @Autowired
    private UserService userService;

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(@Param("id") int id){
        User user = userService.getById(id);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public void dbtx(){
        userService.tx();
    }

    @Autowired
    private RedisService redisService;

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User(1,"11111");
        // UserKey:id1
        redisService.set(UserKey.getById,""+1,user);

//        redisService.get("key2",String.class);
        return Result.success(true);
    }

}
