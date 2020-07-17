package com.tmall.mall.service;

import com.tmall.mall.pojo.User;
import com.tmall.mall.vo.ResponseVo;

public interface IUserService {
    //注册
    ResponseVo register(User user);
    //登录
    ResponseVo login(String username,String password);


}
