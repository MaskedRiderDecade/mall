package com.tmall.mall.controller;

import com.tmall.mall.consts.MallConst;
import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.form.UserLoginForm;
import com.tmall.mall.form.UserRegisterForm;
import com.tmall.mall.pojo.User;
import com.tmall.mall.service.impl.UserServiceImpl;
import com.tmall.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userForm){
        User user=new User();
        //复制对象
        BeanUtils.copyProperties(userForm,user);
        return  userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  HttpSession session){
        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());

        //设置session
        session.setAttribute(MallConst.CURRENT_USER,userResponseVo.getData());
        return userResponseVo;
    }
    @GetMapping("/user")
    public ResponseVo <User> userInfo(HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);

        user.setPassword("");

        return ResponseVo.success(user);
    }

    @PostMapping("/user/logout")
    public ResponseVo logout(HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        if(user==null){
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        session.removeAttribute(MallConst.CURRENT_USER);

        return ResponseVo.success();
    }

}
