package com.tmall.mall.service;

import com.github.pagehelper.PageInfo;
import com.tmall.mall.MallApplicationTests;
import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.form.ShippingForm;
import com.tmall.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
public class IShippingServiceTest extends MallApplicationTests {

    @Autowired
    private IShippingService shippingService;

    private Integer uid=1;

    private ShippingForm form = new ShippingForm();

    Integer shippingId;

    @Before
    public void before(){
        form.setReceiverName("高达");
        form.setReceiverAddress("月神2");
        form.setReceiverCity("上海市");
        form.setReceiverDistrict("东城区");
        form.setReceiverMobile("1891");
        form.setReceiverPhone("5464");
        form.setReceiverProvince("上海直辖市");
        form.setReceiverZip("200540");
        add();
    }

    public void add() {
        ResponseVo <Map<String,Integer>> responseVo=shippingService.add(uid,form);
        log.info("result={}",responseVo);
        this.shippingId=responseVo.getData().get("shippingId");
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @After
    public void delete() {
        ResponseVo responseVo=shippingService.delete(uid,shippingId);
        log.info("result={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void update() {
        form.setReceiverProvince("吉翁公国");

        ResponseVo responseVo=shippingService.update(uid,shippingId,form);
        log.info("result={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());

    }

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = shippingService.list(uid, 1, 3);
        log.info("result={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}