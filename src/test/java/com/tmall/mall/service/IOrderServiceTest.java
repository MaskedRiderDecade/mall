package com.tmall.mall.service;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmall.mall.MallApplicationTests;
import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.form.CartAddForm;
import com.tmall.mall.vo.OrderVo;
import com.tmall.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class IOrderServiceTest extends MallApplicationTests {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ICartService cartService;

    private Integer uid=1;

    private Integer shippingId=9;

    private Gson gson=new GsonBuilder().setPrettyPrinting().create();

    private Integer productId=26;

    @Before
    public void before(){
        CartAddForm cartAddForm = new CartAddForm();
        cartAddForm.setProductId(productId);
        cartAddForm.setSelected(true);
        cartService.add(1,cartAddForm);
    }

    @Test
    public void create() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = orderService.list(uid,1,2);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void detail() {
        ResponseVo<OrderVo> responseVo = orderService.detail(uid,1589977236976L);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void cancel(){
        ResponseVo<OrderVo> responseVo = orderService.cancel(uid,1589977236976L);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}