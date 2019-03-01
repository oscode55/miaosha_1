package com.lijiecheng.miaosha.service;

import com.lijiecheng.miaosha.domain.MiaoshaOrder;
import com.lijiecheng.miaosha.domain.MiaoshaUser;
import com.lijiecheng.miaosha.domain.OrderInfo;
import com.lijiecheng.miaosha.redis.MiaoshaKey;
import com.lijiecheng.miaosha.redis.RedisService;
import com.lijiecheng.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiaoshaService {
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;

	@Autowired
	RedisService redisService;

	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减库存 下订单 写入秒杀订单
		boolean success =goodsService.reduceStock(goods);
		if(success){
			return orderService.createOrder(user,goods);
		}else{
			setGoodsOver(goods.getId());//设置已经售罄
			return null;
		}
	}


	public void reset(List<GoodsVo> goodsList) {
		goodsService.resetStock(goodsList);
		orderService.deleteOrders();
	}

	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if(order != null) {//秒杀成功
			return order.getOrderId();
		}else {
			boolean isOver = getGoodsOver(goodsId);
			if(isOver) {
				return -1;
			}else {
				return 0;
			}
		}
	}

	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
	}

	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
	}
	
}
