package com.lijiecheng.miaosha.dao;

import com.lijiecheng.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: myname
 * @Date: 2019/2/14 15:11
 */
@Mapper
public interface UserDao {

    @Select("select * from user where id=#{id}")
    User getById(@Param("id") int id);

    @Insert("insert into user(id,name) values(#{id},#{name})")
    int insert(User user);
}
