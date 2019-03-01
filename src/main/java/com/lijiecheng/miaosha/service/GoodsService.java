package com.lijiecheng.miaosha.service;

import java.util.List;

import com.lijiecheng.miaosha.dao.GoodsDao;
import com.lijiecheng.miaosha.domain.MiaoshaGoods;
import com.lijiecheng.miaosha.exception.GlobalException;
import com.lijiecheng.miaosha.result.CodeMsg;
import com.lijiecheng.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoodsService {
	
	@Autowired
	GoodsDao goodsDao;
	
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVo();
	}

	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return goodsDao.getGoodsVoByGoodsId(goodsId);
	}

	public boolean reduceStock(GoodsVo goods) {
		MiaoshaGoods g = new MiaoshaGoods();
		g.setGoodsId(goods.getId());
		int ret = goodsDao.reduceStock(g);
		return ret >0;
	}

	public void resetStock(List<GoodsVo> goodsList) {
		log.info("数据库-库存重置");
		for(GoodsVo goods : goodsList ) {
			MiaoshaGoods g = new MiaoshaGoods();
			g.setGoodsId(goods.getId());
			g.setStockCount(goods.getStockCount());
			goodsDao.resetStock(g);
		}
	}
	
	
}
