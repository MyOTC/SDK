package com.bdd.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单状态改变异步通知的消息
 */
@Getter
@Setter
@ToString
public class Message {
    /**
     * 消息签名
     */
    private String subject;
    /**
     * 消息体内容
     */
    private String message;

    /*******************以下为亚马逊通知队列相关参数,非业务相关************************/

    /**
     * 消息类型:Notification(通知消息)  SubscriptionConfirmation(确认订阅消息) UnsubscribeConfirmation(用户取消订阅消息)
     */
    private String type;
    /**
     * messageId
     */
    private String messageId;
    /**
     * 令牌
     */
    private String token;
    /**
     * 主题
     */
    private String topicArn;
    /**
     * 订阅url
     */
    private String subscribeURL;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 签名版本号
     */
    private String signatureVersion;
    /**
     * 亚马逊队列签名字符串
     */
    private String signature;
    /**
     * 验证签名获取证书url
     */
    private String signingCertURL;
}
