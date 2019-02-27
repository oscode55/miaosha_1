package com.lijiecheng.miaosha.service;

import java.util.List;

import com.lijiecheng.miaosha.dao.GoodsDao;
import com.lijiecheng.miaosha.domain.MiaoshaGoods;
import com.lijiecheng.miaosha.exception.GlobalException;
import com.lijiecheng.miaosha.result.CodeMsg;
import com.lijiecheng.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsService {
	
	@Autowired
	GoodsDao goodsDao;
	
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVo();
	}

	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return goodsDao.getGoodsVoByGoodsId(goodsId);
	}

	public void reduceStock(GoodsVo goods) {
		MiaoshaGoods g = new MiaoshaGoods();
		g.setGoodsId(goods.getId());
		if( goodsDao.reduceStock(g) <=0){
			throw new GlobalException(CodeMsg.MIAO_SHA_OVER);
		}
	}
	
	
	
}
