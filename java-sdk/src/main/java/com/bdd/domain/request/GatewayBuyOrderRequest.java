package com.bdd.domain.request;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.bdd.domain.BddPayload;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

/**
 * 支付网关买币下单
 * @author qxx on 2019/1/7.
 */
@Getter
@Setter
@ToString
public class GatewayBuyOrderRequest {
    /**
     * 商户订单号
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "outOrderSn")
    private String outOrderSn;
    /**
     *数字货币币种:USDT
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "variety")
    private String variety;
    /**
     *法币:CNY
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "currency")
    private String currency;

    /**
     *金额、币数量  法币保留两位小数， 数字货币保留四位
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "amount")
    @DecimalMin(value = "0", payload = BddPayload.InvalidParameter.class, inclusive = false,message = "must be grater than zero")
    private String amount;

    /**
     *amount 单位 1- amount 为法币金额 2- amount 为数字货币数量
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "unit")
    @Range(payload = BddPayload.InvalidParameter.class, min = 2, max = 2, message = "must be 2")
    private Integer unit = 2;

    /**
     *商户用户唯一标识
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "outUid")
    private String outUid;

    /**
     *创建买币订单后的同步回调url 用来接收订单信息
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "callback")
    private String callback;

    /**
     *交易完成时要跳转到商户的地址
     */
    private String redirect;
    /**
     *商户用户真实姓名
     */
    private String name;
    /**
     *商户用户身份证号
     */
    private String idNumber;
    /**
     *户用户手机号
     */
    private String mobile;
}
