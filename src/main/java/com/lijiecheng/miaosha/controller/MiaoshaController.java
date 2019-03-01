package com.lijiecheng.miaosha.controller;

import com.lijiecheng.miaosha.domain.MiaoshaOrder;
import com.lijiecheng.miaosha.domain.MiaoshaUser;
import com.lijiecheng.miaosha.rabbitmq.MQSender;
import com.lijiecheng.miaosha.rabbitmq.MiaoshaMessage;
import com.lijiecheng.miaosha.redis.GoodsKey;
import com.lijiecheng.miaosha.redis.MiaoshaKey;
import com.lijiecheng.miaosha.redis.OrderKey;
import com.lijiecheng.miaosha.redis.RedisService;
import com.lijiecheng.miaosha.result.CodeMsg;
import com.lijiecheng.miaosha.result.Result;
import com.lijiecheng.miaosha.service.GoodsService;
import com.lijiecheng.miaosha.service.MiaoshaService;
import com.lijiecheng.miaosha.service.MiaoshaUserService;
import com.lijiecheng.miaosha.service.OrderService;
import com.lijiecheng.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;

	@Autowired
	MQSender sender;

	private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();

	/**
	 * 系统初始化
	 * */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if(goodsList == null) {
			return;
		}
		for(GoodsVo goods : goodsList) {
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}

	@RequestMapping(value="/reset", method=RequestMethod.GET)
	@ResponseBody
	public Result<Boolean> reset(Model model) {
		//缓存库存数量重置 秒杀-内存标记重置
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		for(GoodsVo goods : goodsList) {
			goods.setStockCount(10);
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
			localOverMap.put(goods.getId(), false);
		}
		//删除秒杀订单缓存
		redisService.delete(OrderKey.getMiaoshaOrderByUidGid);

		//删除商品售罄标记缓存
		redisService.delete(MiaoshaKey.isGoodsOver);

		//与缓存同步
		miaoshaService.reset(goodsList);

		return Result.success(true);
	}

	@RequestMapping(value="/do_miaosha", method= RequestMethod.POST)
	@ResponseBody
	public Result<Integer> miaosha(MiaoshaUser user,
								   @RequestParam("goodsId")long goodsId) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}

		//内存标记，减少redis访问 判断是否已经售卖完了
		boolean over = localOverMap.get(goodsId);
		if(over) {
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}

        //预减库存
		long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);//10
		if(stock < 0) { //==0这种情况是可以的
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}

        //判断是否已经秒杀到了 缓存取
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null) {
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}

        //入队
		MiaoshaMessage mm = new MiaoshaMessage();
		mm.setUser(user);
		mm.setGoodsId(goodsId);
		sender.sendMiaoshaMessage(mm);
		return Result.success(0);//排队中
	}


	/**
	 * orderId：成功
	 * -1：秒杀失败
	 * 0： 排队中
	 * */
	@RequestMapping(value="/result", method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> miaoshaResult(MiaoshaUser user,
									  @RequestParam("goodsId")long goodsId) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
		return Result.success(result);
	}
}
