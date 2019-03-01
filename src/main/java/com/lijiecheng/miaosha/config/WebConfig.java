package com.lijiecheng.miaosha.config;

import com.lijiecheng.miaosha.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @Author: myname
 * @Date: 2019/2/22 3:32
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{


    @Autowired
    UserArgumentResolver userArgumentResolver;



    //mvc参数注入
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    @Autowired
    AccessInterceptor accessInterceptor;

    //拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }



}
