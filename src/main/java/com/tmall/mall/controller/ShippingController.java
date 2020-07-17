package com.tmall.mall.controller;

import com.tmall.mall.consts.MallConst;
import com.tmall.mall.form.ShippingForm;
import com.tmall.mall.pojo.User;
import com.tmall.mall.service.IShippingService;
import com.tmall.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class ShippingController {

    @Autowired
    private IShippingService shippingService;

    @PostMapping("/shippings")
    public ResponseVo add(@Valid @RequestBody ShippingForm form, HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return shippingService.add(user.getId(),form);
    }

    @DeleteMapping("/shippings/{shippingId}")
    public ResponseVo delete(@PathVariable Integer shippingId,HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return shippingService.delete(user.getId(),shippingId);
    }

    @PutMapping("/shippings/{shippingId}")
    public ResponseVo update(@Valid @RequestBody ShippingForm form,@PathVariable Integer shippingId,HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return shippingService.update(user.getId(),shippingId,form);
    }

    @GetMapping("/shippings")
    public ResponseVo list(@RequestParam(required=false,defaultValue = "1") Integer pageNum, HttpSession session,
                           @RequestParam(required=false,defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return shippingService.list(user.getId(),pageNum,pageSize);
    }



}
