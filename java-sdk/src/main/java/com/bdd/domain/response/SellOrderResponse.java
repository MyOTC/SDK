package com.bdd.domain.response;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单详情
 */
@Getter
@Setter
@ToString
public class SellOrderResponse {

    /**
     *BDD平台订单号
     */
    private String tradeSn;
    /**
     *卖币数量
     */
    private String amount;

    /**
     *手续费
     */
    private String merchantFee;

    /**
     *订单状态 1-已创建（未审核） 2-未付款 3-已付款待收款 4-已成交 5-已取消
     */
    private Integer status;

    /**
     *订单创建时间
     */
    private Date createTime;
}
