package com.lijiecheng.miaosha.redis;

public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super(60,prefix);//一分钟失效
	}
	public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}
