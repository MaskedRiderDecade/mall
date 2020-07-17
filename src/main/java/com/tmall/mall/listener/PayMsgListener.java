package com.tmall.mall.listener;

import com.google.gson.Gson;
import com.tmall.mall.pojo.PayInfo;
import com.tmall.mall.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RabbitListener(queues = "payNotify")
public class PayMsgListener {

    @Autowired
    private IOrderService orderService;

    @RabbitHandler
    public void process(String msg) {
        log.info("接收到消息={}", msg);
        PayInfo payInfo = new Gson().fromJson(msg, PayInfo.class);
        if(payInfo.getPlatformStatus().equals("SUCCESS")){
            //支付成功，修改订单状态
           orderService.paid(payInfo.getOrderNo());
        }
    }
}
