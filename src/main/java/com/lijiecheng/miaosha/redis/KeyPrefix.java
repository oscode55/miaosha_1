package com.lijiecheng.miaosha.redis;

/**
 * @Author: myname
 * @Date: 2019/2/15 3:00
 */
public interface KeyPrefix {
    int expireSecond();
    String getPrefix();
}
