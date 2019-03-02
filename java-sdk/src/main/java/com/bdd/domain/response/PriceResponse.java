package com.bdd.domain.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 查询价格
 */
@Getter
@Setter
@ToString
public class PriceResponse {

    /**
     * 币种代码
     */
    private String variety;
    /**
     *法币
     */
    private String currency;
    /**
     *入金价格
     */
    private BigDecimal buy;
    /**
     *出金价格
     */
    private BigDecimal sell;
}
