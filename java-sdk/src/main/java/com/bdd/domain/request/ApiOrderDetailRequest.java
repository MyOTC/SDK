package com.bdd.domain.request;

import javax.validation.constraints.NotNull;

import com.bdd.domain.BddPayload;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 获取订单详情
 */
@Getter
@Setter
@ToString
public class ApiOrderDetailRequest {

    /**
     *商户订单号
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "outOrderSn")
    private String outOrderSn;
}
