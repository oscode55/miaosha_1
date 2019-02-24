package com.lijiecheng.miaosha;

import com.lijiecheng.miaosha.controller.DemoController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * @Author: myname
 * @Date: 2019/2/13 14:11
 */

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) throws Exception{
        SpringApplication.run(MainApplication.class,args);
    }

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(MainApplication.class);
//    }
}
