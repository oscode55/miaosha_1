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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/miaosha")
@Slf4j
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
		redisService.delete(MiaoshaKey.isGoodsOver);
	}

	@RequestMapping(value="/reset", method=RequestMethod.GET)
	@ResponseBody
	public Result<Boolean> reset(Model model) {
		log.info("/miaosha/reset");
		List<GoodsVo> goodsList = goodsService.listGoodsVo();

		for(GoodsVo goods : goodsList) {
			goods.setStockCount(10);
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
			localOverMap.put(goods.getId(), false);
		}

		log.info("删除秒杀订单缓存");
		redisService.delete(OrderKey.getMiaoshaOrderByUidGid);

		log.info("删除商品售罄标志缓存");
		redisService.delete(MiaoshaKey.isGoodsOver);

		//与缓存同步
		miaoshaService.reset(goodsList);

		return Result.success(true);
	}

	@RequestMapping(value="/{path}/do_miaosha", method= RequestMethod.POST)
	@ResponseBody
	public Result<Integer> miaosha(MiaoshaUser user,
								   @RequestParam("goodsId")long goodsId,
								   @PathVariable("path") String path) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}

        //验证path
		boolean check = miaoshaService.checkPath(user, goodsId, path);
		if(!check){
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}

		//内存标记，减少redis访问 判断是否已经售卖完了
		boolean over = localOverMap.get(goodsId);
		if(over) {
			log.info("商品已售罄");
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
			log.info("用户已经秒杀到这款商品:"+order.toString());
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}

        //入队
		MiaoshaMessage mm = new MiaoshaMessage();
		mm.setUser(user);
		mm.setGoodsId(goodsId);
		sender.sendMiaoshaMessage(mm);
		return Result.success(0);//排队中
	}

	@RequestMapping(value="/do_miaosha", method= RequestMethod.POST)
	@ResponseBody
	public Result<Integer> miaosha2(MiaoshaUser user,@RequestParam("goodsId")long goodsId) {

		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}

		//内存标记，减少redis访问 判断是否已经售卖完了
		boolean over = localOverMap.get(goodsId);
		if(over) {
			log.info("商品已售罄");
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
			log.info("用户已经秒杀到这款商品:"+order.toString());
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



	@RequestMapping(value="/path", method=RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
										 @RequestParam("goodsId")long goodsId,
										 @RequestParam(value="verifyCode", defaultValue="0")int verifyCode) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
		if(!check) {
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		String path  =miaoshaService.createMiaoshaPath(user, goodsId);
		return Result.success(path);
	}


	@RequestMapping(value="/verifyCode", method=RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, MiaoshaUser user,
											  @RequestParam("goodsId")long goodsId) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		try {
			BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
			OutputStream out = response.getOutputStream();
			ImageIO.write(image, "JPEG", out);
			out.flush();
			out.close();
			return null;
		}catch(Exception e) {
			e.printStackTrace();
			return Result.error(CodeMsg.MIAOSHA_FAIL);
		}
	}
}
