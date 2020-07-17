package com.tmall.mall.service;

import com.tmall.mall.form.CartAddForm;
import com.tmall.mall.form.CartUpdateForm;
import com.tmall.mall.pojo.Cart;
import com.tmall.mall.vo.CartVo;
import com.tmall.mall.vo.ResponseVo;

import java.util.List;

public interface ICartService {
    ResponseVo<CartVo>add(Integer uid,CartAddForm form);
    ResponseVo<CartVo>list(Integer uid);
    ResponseVo<CartVo>update(Integer uid, Integer productId, CartUpdateForm form);
    ResponseVo<CartVo>delete(Integer uid, Integer productId);
    ResponseVo<CartVo>selectAll(Integer uid);
    ResponseVo<CartVo>unSelectAll(Integer uid);
    ResponseVo<Integer>sum(Integer uid);
    List<Cart>listForCart(Integer uid);


}