package com.bdd.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单状态变化异步通知的业务消息
 * @author qxx on 2019/1/2.
 */
@Getter
@Setter
@ToString
public class OrderStatusAsyncNotifyMessage {
    /**
     * 商户订单号
     */
    private String outOrderSn;
    /**
     * 订单状态
     */
    private Integer status;
    /**
     * 币数量
     */
    private String amount;
    /**
     * 法币总额
     */
    private String total;
    /**
     * 手续费
     */
    private String fee;
    /**
     * 单价
     */
    private String price;

    /**
     * 取消类型:1系统取消 2用户取消 3商家取消
     */
    private Integer cancelType;

    /**
     * 消息类型:OrderNotice
     */
    private String noticeType;

    /**
     * 对应的操作
     101	入金	已匹配到承兑商
     102	入金	您的用户确认付款
     103	入金	承兑商确认收款放币
     104	入金	您的用户取消入金订单
     105	入金	系统重开入金订单
     201	出金	审核通过，即匹配到承兑商
     202	出金	审核失败，订单取消
     203	出金	承兑商付款
     304	出金	您的用户确认收款
     205	出金	承兑商取消订单
     */
    private Integer noticeAction;

    /**
     * 时间
     */
    private String noticeTimestamp;
}
