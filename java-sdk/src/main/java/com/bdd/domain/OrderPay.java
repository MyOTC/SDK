package com.bdd.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderPay {
    /**
     * 支付类型 1 支付宝 2 微信 3 银行卡
     */
    private Integer type;

    /**
     * 支付宝账号或银行卡账号(type = 1|2 )
     */
    private String number;

    /**
     * 支付宝收款人姓名，开户人姓名(type = 1|2 )
     */
    private String name;

    /**
     * 支付宝、微信收款二维码(type = 1|2 )
     */
    private String url;

    /**
     * 银行名称(type = 3 )
     */
    private String bank;

    /**
     * 开户行(type = 3 )
     */
    private String bankName;
}
