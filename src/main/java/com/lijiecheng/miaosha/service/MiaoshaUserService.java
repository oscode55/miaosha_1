package com.lijiecheng.miaosha.service;

import com.lijiecheng.miaosha.dao.MiaoshaUserDao;
import com.lijiecheng.miaosha.domain.MiaoshaUser;
import com.lijiecheng.miaosha.exception.GlobalException;
import com.lijiecheng.miaosha.redis.MiaoshaUserKey;
import com.lijiecheng.miaosha.redis.RedisService;
import com.lijiecheng.miaosha.result.CodeMsg;
import com.lijiecheng.miaosha.util.MD5Util;
import com.lijiecheng.miaosha.util.UUIDUtil;
import com.lijiecheng.miaosha.vo.LoginVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: myname
 * @Date: 2019/2/19 3:04
 */
@Service
@Slf4j
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getById(id);
    }

    public String login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //验证密码
        String dbPass = user.getPassword();//数据库存储的密码

        String saltDB = user.getSalt();

        //根据表单计算出来的密码
        String calcPass = MD5Util.formPassToDBPass(formPass,saltDB);
        if(!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);

        return token;
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
        //延长有效期
        if(user!=null){
            addCookie(response,token,user);
        }
        return user;
    }

    private void addCookie(HttpServletResponse response,String token,MiaoshaUser user){

        redisService.set(MiaoshaUserKey.token,token,user);

        Cookie cookie = new Cookie(COOKI_NAME_TOKEN,token);
//        cookie.setMaxAge(MiaoshaUserKey.token.expireSecond());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
