package com.lijiecheng.miaosha.redis;

/**
 * @Author: myname
 * @Date: 2019/2/22 2:28
 */
public class MiaoshaUserKey extends BasePrefix {
    private MiaoshaUserKey(String prefix){
        super(prefix);
    }
    public static MiaoshaUserKey token = new MiaoshaUserKey("tk");

}
