package com.tmall.mall.service.impl;

import com.tmall.mall.dao.UserMapper;
import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.enums.RoleEnum;
import com.tmall.mall.pojo.User;
import com.tmall.mall.service.IUserService;
import com.tmall.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    //注册
    @Override
    public ResponseVo register(User user){
        //写入数据库，写入前先校验username，邮箱不能重复
        int countByUsername=userMapper.countByUsername(user.getUsername());
        if(countByUsername>0){
            return ResponseVo.error(ResponseEnum.USERNAME_EXIST);
        }

        int countByEmail=userMapper.countByEmail(user.getEmail());
        if(countByEmail>0){
            return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
        }
        user.setRole(RoleEnum.CUSTOMER.getCode());

        //MD5摘要算法(Spring自带)
        user.setPassword(DigestUtils.md5DigestAsHex(
                user.getPassword().getBytes(StandardCharsets.UTF_8)));

//        写入数据库
        int result=userMapper.insertSelective(user);
        if(result==0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if(user==null){
            //用户不存在，但是不能告诉客户端
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);

        }
        if (!user.getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            //用户名或密码错误
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);

        }

        user.setPassword("");
        return ResponseVo.success(user);

    }




}
