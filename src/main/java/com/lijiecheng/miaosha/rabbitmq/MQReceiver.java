package com.lijiecheng.miaosha.rabbitmq;

import com.lijiecheng.miaosha.domain.MiaoshaOrder;
import com.lijiecheng.miaosha.domain.MiaoshaUser;
import com.lijiecheng.miaosha.redis.RedisService;
import com.lijiecheng.miaosha.service.GoodsService;
import com.lijiecheng.miaosha.service.MiaoshaService;
import com.lijiecheng.miaosha.service.OrderService;
import com.lijiecheng.miaosha.vo.GoodsVo;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import sun.rmi.transport.Channel;

import java.io.IOException;


@Service
@Slf4j
public class MQReceiver {

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,com.rabbitmq.client.Channel channel) {
        log.info("receive message:" + message);
        MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }
        //把mysql的异常捕获 让消息不会重发
        try {
            miaoshaService.miaosha(user, goods);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//		@RabbitListener(queues=MQConfig.QUEUE)
//		public void receive(String message) {
//			log.info("receive message:"+message);
//		}
//
//		@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
//		public void receiveTopic1(String message) {
//			log.info(" topic  queue1 message:"+message);
//		}
//
//		@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
//		public void receiveTopic2(String message) {
//			log.info(" topic  queue2 message:"+message);
//		}
//
//		@RabbitListener(queues=MQConfig.HEADER_QUEUE)
//		public void receiveHeaderQueue(byte[] message) {
//			log.info(" header  queue message:"+new String(message));
//		}


}
