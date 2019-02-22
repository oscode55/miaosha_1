package com.lijiecheng.miaosha.util;

import java.util.UUID;

/**
 * @Author: myname
 * @Date: 2019/2/22 2:25
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
