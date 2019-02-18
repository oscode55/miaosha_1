package com.lijiecheng.miaosha;

import com.lijiecheng.miaosha.controller.DemoController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: myname
 * @Date: 2019/2/13 14:11
 */

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) throws Exception{
        SpringApplication.run(MainApplication.class,args);
    }
}
