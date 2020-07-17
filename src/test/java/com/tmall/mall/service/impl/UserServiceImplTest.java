package com.tmall.mall.service.impl;

import com.tmall.mall.MallApplicationTests;
import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.enums.RoleEnum;
import com.tmall.mall.pojo.User;
import com.tmall.mall.service.IUserService;
import com.tmall.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImplTest extends MallApplicationTests {

    public static final String USERNAME="JACK";

    public static final String PASSWORD="123456";


    @Autowired
    private IUserService userService;

    @Before
    public void register() {
        User user=new User(USERNAME,PASSWORD,"JAC1K@qq.com", RoleEnum.CUSTOMER.getCode());
        userService.register(user);

    }

    @Test
    public void login(){
        ResponseVo<User> responseVo=userService.login(USERNAME,PASSWORD);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());

    }
}