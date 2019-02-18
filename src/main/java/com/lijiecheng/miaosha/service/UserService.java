package com.lijiecheng.miaosha.service;

import com.lijiecheng.miaosha.dao.UserDao;
import com.lijiecheng.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: myname
 * @Date: 2019/2/14 15:12
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    public User getById(int id){
        return userDao.getById(id);
    }

//    @Transactional
    public boolean tx(){
        //1.执行成功
        User u1 = new User();
        u1.setId(2);
        u1.setName("222");
        userDao.insert(u1);

        //2.执行失败
        User u2 = new User();
        u2.setId(1);
        u2.setId(1);
        u2.setName("1111");
        userDao.insert(u2);

        //观察结果 是否回滚了1
        return true;
    }
}
