package com.tmall.mall.service.impl;

import com.google.gson.Gson;
import com.mysql.cj.util.StringUtils;
import com.tmall.mall.dao.ProductMapper;
import com.tmall.mall.enums.ProductStatusEnum;
import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.form.CartAddForm;
import com.tmall.mall.form.CartUpdateForm;
import com.tmall.mall.pojo.Cart;
import com.tmall.mall.pojo.Product;
import com.tmall.mall.service.ICartService;
import com.tmall.mall.vo.CartProductVo;
import com.tmall.mall.vo.CartVo;
import com.tmall.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class CartServiceImpl implements ICartService {

    private final static String CART_REDIS_KEY_TEMPLATE="cart_%d";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Gson gson=new Gson();

    @Override
    public ResponseVo<CartVo> add(Integer uid,CartAddForm form) {
        Integer quantity=1;
        Product product = productMapper.selectByPrimaryKey(form.getProductId());
        //商品是否存在
        if(product==null){
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }
//        商品状态是否正常
        if(!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())){
            return ResponseVo.error(ResponseEnum.PRODUCT_OFFSALE_OR_DELETE);
        }
//        商品是否还有库存
        if(product.getStock()<=0){
            return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR);

        }

        //写入redis
        HashOperations<String,String,String>opsForHash=redisTemplate.opsForHash();

        String redisKey=String.format(CART_REDIS_KEY_TEMPLATE,uid);

        String value=opsForHash.get(redisKey, String.valueOf(product.getId()));
        Cart cart;
        if(StringUtils.isEmptyOrWhitespaceOnly(value)){
            //没有该商品
            cart=new Cart(product.getId(),quantity,form.getSelected());

        }else{
            cart = gson.fromJson(value, Cart.class);
            cart.setQuantity(cart.getQuantity()+quantity);

        }

                opsForHash.put(String.format(CART_REDIS_KEY_TEMPLATE,uid),
                String.valueOf(product.getId()),
                gson.toJson(cart));

        return list(uid);

    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        HashOperations<String,String,String>opsForHash=redisTemplate.opsForHash();

        String redisKey=String.format(CART_REDIS_KEY_TEMPLATE,uid);
        CartVo cartVo=new CartVo();
        List<CartProductVo> cartProductVoList=new ArrayList<>();

        Boolean selectAll=true;
        Integer cartTotalQuantity=0;
        BigDecimal cartTotalPrice=BigDecimal.ZERO;

        Map<String, String> entries = opsForHash.entries(redisKey);


        //利用in进行了优化
        CartVo cartVo1=new CartVo();
        List<CartProductVo> cartProductVoList1=new ArrayList<>();

        Boolean selectAll1=true;
        Integer cartTotalQuantity1=0;
        BigDecimal cartTotalPrice1=BigDecimal.ZERO;
        Set<Integer>cartProductIdSet=new HashSet<>();
        for(Map.Entry<String, String> entry : entries.entrySet()){
            Integer productId = Integer.valueOf(entry.getKey());
            cartProductIdSet.add(productId);
        }
        List<Product> productList = productMapper.selectByProductIdSet(cartProductIdSet);
        for(Product product:productList){
            Integer productId=product.getId();
            String productIdKey=productId.toString();
            String s = entries.get(productIdKey);
            Cart cart=gson.fromJson(entries.get(productIdKey),Cart.class);
            if(cart!=null){
                CartProductVo cartProductVo=new CartProductVo(productId,
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected()
                );
                cartProductVoList1.add(cartProductVo);
                if(!cart.getProductSelected()){
                    selectAll1=false;
                }else {
                    //计算选中的商品的总价
                    cartTotalPrice1 = cartTotalPrice1.add(cartProductVo.getProductTotalPrice());
                }
                cartTotalQuantity1+=cart.getQuantity();
            }
        }
        cartVo1.setSelectedAll(selectAll1);
        cartVo1.setCartTotalQuantity(cartTotalQuantity1);
        cartVo1.setCartTotalPrice(cartTotalPrice1);
        cartVo1.setCartProductVoList(cartProductVoList1);




//        for (Map.Entry<String, String> entry : entries.entrySet()) {
//            Integer productId = Integer.valueOf(entry.getKey());
//            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
//            //TODO 需要优化,使用mysql的in
//            Product product = productMapper.selectByPrimaryKey(productId);
//            if(product!=null){
//                CartProductVo cartProductVo=new CartProductVo(productId,
//                        cart.getQuantity(),
//                        product.getName(),
//                        product.getSubtitle(),
//                        product.getMainImage(),
//                        product.getPrice(),
//                        product.getStatus(),
//                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
//                        product.getStock(),
//                        cart.getProductSelected()
//                        );
//                cartProductVoList.add(cartProductVo);
//                if(!cart.getProductSelected()){
//                    selectAll=false;
//                }else {
//                    //计算选中的商品的总价
//                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
//                }
//            }
//            cartTotalQuantity+=cart.getQuantity();
//        }
//        cartVo.setSelectAll(selectAll);
//        cartVo.setCartTotalQuantity(cartTotalQuantity);
//        cartVo.setCartTotalPrice(cartTotalPrice);
//        cartVo.setCartProductVoList(cartProductVoList);

        return ResponseVo.success(cartVo1);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form) {
        HashOperations<String,String,String>opsForHash=redisTemplate.opsForHash();

        String redisKey=String.format(CART_REDIS_KEY_TEMPLATE,uid);

        String value=opsForHash.get(redisKey, String.valueOf(productId));
        if(StringUtils.isEmptyOrWhitespaceOnly(value)){
            //没有该商品,数据有问题，报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);

        }
        Cart cart = gson.fromJson(value, Cart.class);
        //有，修改内容
        if(form.getQuantity()!=null&&form.getQuantity()>0){
            cart.setQuantity(form.getQuantity());
        }
        if(form.getSelected()!=null){
            cart.setProductSelected(form.getSelected());
        }
        opsForHash.put(redisKey,String.valueOf(productId),gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String,String,String>opsForHash=redisTemplate.opsForHash();

        String redisKey=String.format(CART_REDIS_KEY_TEMPLATE,uid);

        String value=opsForHash.get(redisKey, String.valueOf(productId));
        if(StringUtils.isEmptyOrWhitespaceOnly(value)){
            //没有该商品,数据有问题，报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);

        }
        //有，修改内容
        opsForHash.delete(redisKey,String.valueOf(productId));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        HashOperations<String,String,String>opsForHash=redisTemplate.opsForHash();
        String redisKey=String.format(CART_REDIS_KEY_TEMPLATE,uid);
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(true);
            opsForHash.put(redisKey,String.valueOf(cart.getProductId()),gson.toJson(cart));
        }
        return list(uid);

    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        HashOperations<String,String,String>opsForHash=redisTemplate.opsForHash();
        String redisKey=String.format(CART_REDIS_KEY_TEMPLATE,uid);
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(false);
            opsForHash.put(redisKey,String.valueOf(cart.getProductId()),gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer sum= listForCart(uid).stream().map(Cart::getQuantity).reduce(0, Integer::sum);
        return ResponseVo.success(sum);
    }

    public List<Cart>listForCart(Integer uid){
        HashOperations<String,String,String>opsForHash=redisTemplate.opsForHash();
        String redisKey=String.format(CART_REDIS_KEY_TEMPLATE,uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        List<Cart>cartList=new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cartList.add(gson.fromJson(entry.getValue(),Cart.class));
        }
        return cartList;
    }
}
