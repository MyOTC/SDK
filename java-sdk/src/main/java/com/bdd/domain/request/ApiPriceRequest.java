package com.bdd.domain.request;

import javax.validation.constraints.NotNull;

import com.bdd.domain.BddPayload;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 查询价格
 */
@Getter
@Setter
@ToString
public class ApiPriceRequest {
    /**
     * 币种:btc usdt ...
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "variety")
    private String variety;
    /**
     * 法币 CNY
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "currency")
    private String currency;

}
