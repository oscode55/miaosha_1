package com.lijiecheng.miaosha.redis;

/**
 * @Author: myname
 * @Date: 2019/2/15 3:01
 */
public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;
    private String prefix;

    public BasePrefix() {

    }

    public BasePrefix(String prefix){ //0代表永不过期
        this(0,prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSecond() { //默认0 代表永不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":"+ prefix;
    }
}
