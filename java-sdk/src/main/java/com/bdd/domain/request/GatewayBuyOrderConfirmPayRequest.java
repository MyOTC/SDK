package com.bdd.domain.request;

import javax.validation.constraints.NotNull;

import com.bdd.domain.BddPayload;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付网关买币下单
 * @author qxx on 2019/1/7.
 */
@Getter
@Setter
@ToString
public class GatewayBuyOrderConfirmPayRequest {
    /**
     * 商户订单号
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "outOrderSn")
    private String outOrderSn;
}
