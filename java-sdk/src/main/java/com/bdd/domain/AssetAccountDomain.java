package com.bdd.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 账号资产信息
 */
@Getter
@Setter
@ToString
public class AssetAccountDomain {
    /**
     * 币种:usdt
     */
    private String varietyCode;
    /**
     * 余额
     */
    private String balance;
    /**
     * 可用
     */
    private String available;
    /**
     * 冻结
     */
    private String freeze;
}