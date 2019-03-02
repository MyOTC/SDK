package com.bdd.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import com.alibaba.fastjson.JSON;

import com.bdd.domain.BuyOrderSyncMessage;
import com.bdd.domain.Message;
import com.bdd.domain.OrderStatusAsyncNotifyMessage;
import com.bdd.exception.BddException;
import com.bdd.exception.InvalidParameterException;
import com.bdd.utils.ApiIdentityUtil;
import com.google.common.base.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 订单状态改变后的异步通知
 *
 */
@Slf4j
public class OrderNotifyService {

    /**
     * 秘钥: 渠道商配置的回调签名秘钥
     * @see <a href="http://merchant.rmmlm.com/merchant/config/chanel_set">测试环境</a>
     */
    private final String SECRET_KEY;

    /**
     * 构造方法
     * @param secretKey
     */
    public OrderNotifyService(@NonNull String secretKey) {
        this.SECRET_KEY = secretKey;
    }

    /**
     * 支付网关,买币下单币多多同步回调
     * 注意判断 BuyOrderSyncMessage result的状态
     * @param requestURL  httpServletRequest.getRequestURL().toString()
     * @param queryString  httpServletRequest.getQueryString()
     * @param function
     */
    public BuyOrderSyncMessage buyCallback(@NonNull String requestURL, @NonNull String queryString, Function<BuyOrderSyncMessage, Boolean> function) {
        log.info("requestURL={},queryString={}", requestURL, queryString);
        Pair<Boolean, BuyOrderSyncMessage> pair;
        try {
            pair = ApiIdentityUtil.verifySign(HmacAlgorithms.HMAC_SHA_256.getName(), SECRET_KEY, requestURL + "?" + queryString);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BddException(e.getMessage());
        }
        if (!pair.getLeft()) {
            throw new SecurityException("Signature invalid.");
        }
        BuyOrderSyncMessage buyOrderSyncMessage = pair.getRight();
        if (function != null) {
            function.apply(buyOrderSyncMessage);
        }
        return buyOrderSyncMessage;
    }


    /**
     * 订单状态变化处理
     *
     * @param messageJson  从 HttpServletRequest的 body中获取: String messageJson = IOUtils.toString(request.getInputStream(), "UTF-8");
     *      或者Controller这样定义:
     *     public void orderStatusNotify(@RequestBody String messageJson){...}
     * @param function 回调的 Function
     * @throws IOException
     */
    public OrderStatusAsyncNotifyMessage orderStatusNotifyHandle(@NonNull String messageJson,Function<OrderStatusAsyncNotifyMessage, Boolean> function)
        throws IOException {
        log.info("order Status notify message received:{}", messageJson);
        //获取消息体
        Message message;
        try {
            message = JSON.parseObject(messageJson, Message.class);
        } catch (Exception e) {
            throw new InvalidParameterException("message format error.");
        }

        if (message == null || message.getType() == null) {
            log.info("message type not exist");
            throw new InvalidParameterException("Unexpected signature version. Unable to verify signature.");
        }

        // 验证通知队列签名
        if (!"1".equals(message.getSignatureVersion()) || !isMessageSignatureValid(message)) {
            throw new SecurityException("Signature verification failed.");
        }
        OrderStatusAsyncNotifyMessage orderStatusAsyncNotifyMessage = null;
        //处理不同消息类型
        if (message.getType().equals("Notification")) {
            // 验证业务签名
            if (!ApiIdentityUtil.sign(SECRET_KEY, HmacAlgorithms.HMAC_SHA_256.getName(),message.getMessage()).equals(message.getSubject())) {
                log.info("Business signature verification failed.");
                throw new SecurityException("Unexpected signature version. Unable to verify signature.");
            }
             orderStatusAsyncNotifyMessage = JSON.parseObject(message.getMessage(), OrderStatusAsyncNotifyMessage.class);
            //具体业务逻辑逻辑
            if (function != null) {
                function.apply(orderStatusAsyncNotifyMessage);
            }
        } else if (message.getType().equals("SubscriptionConfirmation")) {
            //确认订阅 请求SubscribeURL即可
            subscription(message);
        } else if (message.getType().equals("UnsubscribeConfirmation")) {
            //用户取消订阅三天后生效 (取消方法 builder.toString() 中获取UnSubscribeURL 请求即可)
            //防止误删 再次发起订阅 可根据具体情况选择注释下边代码
            subscription(message);
        } else {
            log.info("Unknown message type.");
        }
        log.info("Done processing message: " + message.getMessageId());
        return orderStatusAsyncNotifyMessage;
    }

    /**
     * 确认订阅
     * @param msg
     * @throws IOException
     */
    private void subscription(Message msg) throws IOException {
        //请求订阅的SubscribeURL 确认订阅
        InputStream inputStream = new URL(msg.getSubscribeURL()).openStream();
        String response = IOUtils.toString(inputStream, "UTF-8");
        log.info("Subscription confirmation (" + msg.getSubscribeURL() + ") Return value: " + response);
    }

    /****************以下为亚马逊队列验证验签方法********************/
    private static boolean isMessageSignatureValid(Message msg) {
        try {
            URL url = new URL(msg.getSigningCertURL());
            InputStream inStream = url.openStream();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
            inStream.close();

            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(cert.getPublicKey());
            sig.update(getMessageBytesToSign(msg));
            return sig.verify(Base64.decodeBase64(msg.getSignature()));
        } catch (Exception e) {
            throw new SecurityException("Verify method failed.", e);
        }
    }

    private static byte[] getMessageBytesToSign(Message msg) {
        byte[] bytesToSign = null;
        if (msg.getType().equals("Notification")) {
            bytesToSign = buildNotificationStringToSign(msg).getBytes();
        } else if (msg.getType().equals("SubscriptionConfirmation") || msg.getType().equals("UnsubscribeConfirmation")) {
            bytesToSign = buildSubscriptionStringToSign(msg).getBytes();
        }
        return bytesToSign;
    }

    private static String buildNotificationStringToSign(Message msg) {
        String stringToSign = null;
        stringToSign = "Message\n";
        stringToSign += msg.getMessage() + "\n";
        stringToSign += "MessageId\n";
        stringToSign += msg.getMessageId() + "\n";
        if (msg.getSubject() != null) {
            stringToSign += "Subject\n";
            stringToSign += msg.getSubject() + "\n";
        }
        stringToSign += "Timestamp\n";
        stringToSign += msg.getTimestamp() + "\n";
        stringToSign += "TopicArn\n";
        stringToSign += msg.getTopicArn() + "\n";
        stringToSign += "Type\n";
        stringToSign += msg.getType() + "\n";
        return stringToSign;
    }

    private static String buildSubscriptionStringToSign(Message msg) {
        String stringToSign = null;
        stringToSign = "Message\n";
        stringToSign += msg.getMessage() + "\n";
        stringToSign += "MessageId\n";
        stringToSign += msg.getMessageId() + "\n";
        stringToSign += "SubscribeURL\n";
        stringToSign += msg.getSubscribeURL() + "\n";
        stringToSign += "Timestamp\n";
        stringToSign += msg.getTimestamp() + "\n";
        stringToSign += "Token\n";
        stringToSign += msg.getToken() + "\n";
        stringToSign += "TopicArn\n";
        stringToSign += msg.getTopicArn() + "\n";
        stringToSign += "Type\n";
        stringToSign += msg.getType() + "\n";
        return stringToSign;
    }

}
