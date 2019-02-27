package com.lijiecheng.miaosha.redis;

/**
 * @Author: myname
 * @Date: 2019/2/22 2:28
 */
public class MiaoshaUserKey extends BasePrefix {

    private MiaoshaUserKey(int expireSeconds,String prefix){
        super(expireSeconds,prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(0,"tk");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");
}
