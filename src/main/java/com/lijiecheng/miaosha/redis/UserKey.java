package com.lijiecheng.miaosha.redis;

/**
 * @Author: myname
 * @Date: 2019/2/15 3:14
 */
public class UserKey extends BasePrefix {

    private UserKey(String prefix){
        super(prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");



}
