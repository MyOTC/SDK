package com.bdd.domain.request;

import javax.validation.constraints.NotNull;

import com.bdd.domain.BddPayload;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 网关出金订单详情
 * @author qxx on 2019/1/7.
 */
@Getter
@Setter
@ToString
public class GatewaySellOrderDetailRequest {
    /**
     * 商户订单号
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "outOrderSn")
    private String outOrderSn;

    /**
     * 交易完成时要跳转到商户的地址
     */
    private String redirect;
}
