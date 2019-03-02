package com.bdd.domain.response;

import java.util.Date;
import java.util.List;

import com.bdd.domain.OrderPay;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单详情
 */
@Getter
@Setter
@ToString
public class OrderInfoResponse {

    /**
     *平台订单号
     */
    private String tradeSn;
    /**
     *商户订单号
     */
    private String outOrderSn;

    /**
     *法币:CNY
     */
    private String currency;

    /**
     *otc 订单编号
     */
    private String otcOrderSn;
    /**
     *商户用户id
     */
    private String outUid;
    /**
     *订单类型 1-买 2-卖
     */
    private Integer type;
    /**
     *币种代码
     */
    private String varietyCode;
    /**
     *订单状态 1-已创建（未审核） 2-未付款 3-已付款待收款 4-已成交 5-已取消
     */
    private Integer status;
    /**
     *币数量
     */
    private String amount;
    /**
     *单价
     */
    private String dealPrice;
    /**
     *手续费
     */
    private String merchantFee;
    /**
     *法币总额
     */
    private String total;
    /**
     *订单创建时间
     */
    private Date createTime;

    /**
     *出金订单审核状态，1-待审核 2-通过 3-拒绝
     */
    private Integer approveStatus;

    /**
     *	卖币审核时间
     */
    private Date approveTime;

    /**
     *匹配到承兑商时间
     */
    private Date applyTime;

    /**
     *确认付款时间
     */
    private Date payTime;

    /**
     *确认收款时间
     */
    private Date confirmTime;
    /**
     *取消类型 1-用户 2-系统
     */
    private Integer cancelStatus;

    /**
     *取消时间
     */
    private Date cancelTime;

    /**
     *申诉状态
     */
    private Integer appealStatus;

    /**
     * 申诉信息
     */
    private String appeal;

    /**
     *申诉编号
     */
    private Long appealId;
    /**
     *申诉时间
     */
    private Date appealTime;
    /**
     * 申诉关闭时间
     */
    private String appealDealTime;
    /**
     * 付款截止时间，还有多少毫秒付款超时
     */
    private Long endPayTime;

    /**
     * 承兑商姓名
     */
    private System providerName;

    /**
     * 支付信息
     */
    private List<OrderPay> paymentJson;

    /**
     * 截止确认收款时间
     */
    private Date endConfirmTime;

}
