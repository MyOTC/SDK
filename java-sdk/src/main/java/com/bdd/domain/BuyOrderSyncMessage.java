package com.bdd.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 买币下单 BDD 同步回调参数
 * @author qxx on 2019/1/7.
 */
@ToString
@Getter
@Setter
public class BuyOrderSyncMessage {
    /**
     * otc 订单号
     */
    private String orderSn;
    /**
     * 第三方订单号
     */
    private String outOrderSn;
    /**
     * 结果 1-下单成功 2-订单已取消 3-余额不足 4-其他错误
     */
    private String result;
    /**
     * message
     */
    private String msg;
    /**
     * 法币总金额
     */
    private String total;
    /**
     * 买币数量
     */
    private String amount;
    /**
     * 单价
     */
    private String price;
}
