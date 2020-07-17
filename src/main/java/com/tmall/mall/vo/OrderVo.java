package com.tmall.mall.vo;

import com.tmall.mall.pojo.Shipping;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVo {
    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private String paymentTypeDesc;

    private Integer postage;

    private Integer status;

    private String statusDesc;

    private Date paymentTime;

    private Date sendTime;

    private Date endTime;

    private Date closeTime;

    private Date createTime;

    private List<OrderItemVo> orderItemVoList;

    private Integer shippingId;

    private Shipping shippingVo;

    private String receiverName;
}
