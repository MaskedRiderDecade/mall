package com.tmall.mall.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmall.mall.MallApplicationTests;
import com.tmall.mall.form.CartAddForm;
import com.tmall.mall.form.CartUpdateForm;
import com.tmall.mall.vo.CartVo;
import com.tmall.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ICartServiceTest extends MallApplicationTests {

    @Autowired
    private ICartService cartService;

    private Gson gson=new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void add() {
        CartAddForm cartAddForm = new CartAddForm();
        cartAddForm.setProductId(26);
        cartAddForm.setSelected(true);
        cartService.add(1,cartAddForm);

    }

    @Test
    public void list(){
        ResponseVo<CartVo> list = cartService.list(1);
        log.info("list={}",gson.toJson(list));
    }

    @Test
    public void update(){
        CartUpdateForm form=new CartUpdateForm();
        form.setQuantity(5);
        form.setSelected(true);
        ResponseVo<CartVo>responseVo=cartService.update(1,26,form);
        log.info("list={}",gson.toJson(responseVo));
    }

    @Test
    public void delete(){
        ResponseVo<CartVo>responseVo=cartService.delete(1,26);
        log.info("result={}",gson.toJson(responseVo));
    }

    @Test
    public void selectAll(){
        ResponseVo<CartVo>responseVo=cartService.selectAll(1);
        log.info("result={}",gson.toJson(responseVo));

    }

    @Test
    public void unSelectAll(){
        ResponseVo<CartVo>responseVo=cartService.unSelectAll(1);
        log.info("result={}",gson.toJson(responseVo));

    }

    @Test
    public void sum(){
        ResponseVo<Integer>responseVo=cartService.sum(1);
        log.info("result={}",gson.toJson(responseVo));

    }
}
