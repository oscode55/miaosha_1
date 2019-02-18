package com.lijiecheng.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: myname
 * @Date: 2019/2/14 17:12
 */
@Service
public class RedisPoolFactory {

    @Autowired
    private RedisConfig redisConfig;

    @Bean
    public JedisPool JedisFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());;
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
        JedisPool jp = new JedisPool(poolConfig,redisConfig.getHost(),
                redisConfig.getPort(),
                redisConfig.getTimeout()*1000,
                redisConfig.getPassword(),0);
        return jp;
    }

}
