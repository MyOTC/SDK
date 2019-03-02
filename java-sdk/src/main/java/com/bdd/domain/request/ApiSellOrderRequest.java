package com.bdd.domain.request;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.bdd.domain.BddPayload;
import com.bdd.domain.BddPayload.Group1;
import com.bdd.domain.BddPayload.Group2;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

/**
 * 出金下单
 * @author qxx
 */
@Getter
@Setter
public class ApiSellOrderRequest {
    /**
     * 币种 usdt
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "variety")
    private String variety;
    /**
     * 法币类型 CNY
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "currency")
    private String currency;
    /**
     * 商户用户id
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "outUid")
    private String outUid;
    /**
     * 商户订单号
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "outOrderSn")
    private String outOrderSn;
    /**
     * 支付类型 1-支付宝 2-微信 3-银行卡
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "paymentType")
    @Range(payload = BddPayload.InvalidParameter.class, min = 1, max = 3, message = "must be range between 1 and 3")
    private Integer paymentType;
    /**
     * 币数量 大于0,最多8位小数
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "amount")
    @DecimalMin(value = "0", payload = BddPayload.InvalidParameter.class, inclusive = false,message = "must be grater than zero")
    private String amount;

    /**
     * 单位(暂时只支持2)  1-amount为法币数量  2- amount 为币数量
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "unit")
    @Range(payload = BddPayload.InvalidParameter.class, min = 2, max = 2, message = "must be 2")
    private Integer unit = 2;
    /**
     * 收款人姓名
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "name")
    private String name;
    /**
     * 收款用户身份证号
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "idNumber")
    private String idNumber;
    /**
     * 收款人手机号
     */
    private String mobile;
    /**
     * 收款人银行卡账号 或者 微信 支付宝 账号
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "number")
    private String number;
    /**
     * 收款人银行名称
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "bank",groups = {Group1.class})
    private String bank;

    /**
     * 收款人开户行
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "bankName",groups = {Group1.class})
    private String bankName;

    /**
     *微信 支付宝 二维码图片地址
     */
    @NotNull(payload = BddPayload.MissingParameter.class, message = "qrCode",groups = {Group2.class})
    private String qrCode;

}
