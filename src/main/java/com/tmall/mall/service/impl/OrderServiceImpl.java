package com.tmall.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tmall.mall.dao.OrderItemMapper;
import com.tmall.mall.dao.OrderMapper;
import com.tmall.mall.dao.ProductMapper;
import com.tmall.mall.dao.ShippingMapper;
import com.tmall.mall.enums.OrderStatusEnum;
import com.tmall.mall.enums.PaymentTypeEnum;
import com.tmall.mall.enums.ProductStatusEnum;
import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.pojo.*;
import com.tmall.mall.service.ICartService;
import com.tmall.mall.service.IOrderService;
import com.tmall.mall.vo.OrderItemVo;
import com.tmall.mall.vo.OrderVo;
import com.tmall.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ICartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;


//transactional事务注解，数据库引擎必须是InnoDB,出现runtimeException时进行回滚
    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
//        收货校验（要查出来）
        Shipping shipping=shippingMapper.selectByUidAndShippingId(uid, shippingId);
        if(shipping==null){
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }
//        获取购物车，校验（是否有商品，库存是否充足）
        List<Cart> cartList=cartService.listForCart(uid).stream().filter(Cart::getProductSelected).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(cartList)){
            return  ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }
        //获取所有productId，一次查询数据库
        Set<Integer> productIdSet = cartList.stream().map(Cart::getProductId).collect(Collectors.toSet());
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);

        Map<Integer,Product>map=productList.stream().collect(Collectors.toMap(Product::getId,product->product));
        Long orderNo=generateOrderNo();
        List<OrderItem>orderItemList=new ArrayList<>();
        for(Cart cart:cartList){
            Product product = map.get(cart.getProductId());
            if(product==null){
                return  ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,"productId="+cart.getProductId());
            }
            //商品上下架状态
            if(!ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus())){
                return ResponseVo.error(ResponseEnum.PRODUCT_OFFSALE_OR_DELETE,"商品不是在售状态："+product.getName());
            }

            //        库存是否充足
            if(product.getStock()<cart.getQuantity()){
                return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR,product.getName()+"库存还剩下"+product.getStock());
            }


            OrderItem orderItem = buildOrderItem(uid, orderNo, product, cart.getQuantity());
            orderItemList.add(orderItem);

//            减库存
            product.setStock(product.getStock()-cart.getQuantity());
            int row = productMapper.updateByPrimaryKeySelective(product);
            if(row<=0){
                return ResponseVo.error(ResponseEnum.ERROR);
            }
        }
        //        计算总价格（被选中的）
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);

//          生成订单，入库：order和orderItem，要用事务确保两个表的数据操作一致

        int rowForOrder = orderMapper.insertSelective(order);
        if(rowForOrder<=0){
           return ResponseVo.error(ResponseEnum.ERROR);
        }
        int rowForOrderItem = orderItemMapper.batchInsert(orderItemList);
        if(rowForOrderItem<=0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
//        清理购物车（去掉选中的商品）
//        redis有事务，只是打包命令，不能回滚
        for (Cart cart : cartList) {
            cartService.delete(uid,cart.getProductId());
        }

//        返回给前端OrderVo
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);

        return ResponseVo.success(orderVo);


    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order==null||!order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        Set<Long> orderNoSet=new HashSet();
        orderNoSet.add(order.getOrderNo());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUid(uid);
        Set<Long> orderNoSet = orderList.stream().map(Order::getOrderNo).collect(Collectors.toSet());
        //这个orderItemList是这个用户的所有order的orderItem构成的list
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        //返回list 用groupby
        Map<Long,List<OrderItem>>orderItemMap=orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getOrderNo));

        Set<Integer> shippingIdSet = orderList.stream().map(Order::getShippingId).collect(Collectors.toSet());
        List<Shipping>shippingList=shippingMapper.selectByIdSet(shippingIdSet);
        Map<Integer,Shipping>shippingMap=shippingList.stream().collect(Collectors.toMap(Shipping::getId,shipping -> shipping));

        List<OrderVo>orderVoList=new ArrayList<>();
        for (Order order : orderList) {
            OrderVo orderVo = buildOrderVo(order, orderItemMap.get(order.getOrderNo()), shippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }
        PageInfo pageInfo=new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ResponseVo.success(pageInfo);


    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order==null||!order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
//        只有未付款的订单能取消
        if(!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            return ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }
        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCloseTime(new Date());
        int row=orderMapper.updateByPrimaryKeySelective(order);
        if(row<0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public void paid(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order==null){
            throw new RuntimeException(ResponseEnum.ORDER_NOT_EXIST.getDesc()+"订单id："+orderNo);
        }
//        只有未付款的订单能付款
        if(!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            throw new RuntimeException(ResponseEnum.ORDER_STATUS_ERROR.getDesc()+"订单id："+orderNo);
        }
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());
        int row=orderMapper.updateByPrimaryKeySelective(order);
        if(row<0){
            throw new RuntimeException("将订单更新为已支付时失败，订单id："+orderNo);
        }
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order,orderVo);

        List<OrderItemVo> orderItemVoList = orderItemList.stream().map(e -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(e, orderItemVo);
            return orderItemVo;
        }).collect(Collectors.toList());

        orderVo.setOrderItemVoList(orderItemVoList);

        if(shipping!=null){
            orderVo.setShippingId(shipping.getId());
            orderVo.setShippingVo(shipping);
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setPaymentTypeDesc("在线支付");
        }

        if(orderVo.getStatus()==10) {
            orderVo.setStatusDesc(OrderStatusEnum.NO_PAY.getDesc());
        }else if(orderVo.getStatus()==20){
            orderVo.setStatusDesc(OrderStatusEnum.PAID.getDesc());
        }

        return orderVo;
    }

    private Order buildOrder(Integer uid,Long orderNo,Integer shippingId,List<OrderItem> orderItemList) {
        Order order=new Order();
        order.setUserId(uid);
        order.setOrderNo(orderNo);
        order.setShippingId(shippingId);
        order.setPayment(orderItemList.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO,BigDecimal::add));
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());

        return order;


    }

    //如果是企业级，会使用分布式id
    private Long generateOrderNo() {
        return System.currentTimeMillis()+new Random().nextInt(999);
    }

    private OrderItem buildOrderItem(Integer uid,Long orderNo,Product product,Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(uid);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }
}
