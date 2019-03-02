package com.bdd.service

import com.alibaba.fastjson.JSON
import com.bdd.domain.BuyOrderSyncMessage
import com.bdd.domain.OrderStatusAsyncNotifyMessage
import com.google.common.base.Function
import spock.lang.Specification

/**
 * 创建买币订单后同步回调接口测试
 * @author qxx on 2019/1/4.
 */
class OrderNotifyServiceTest extends Specification {

    private static OrderNotifyService orderNotifyService
    //已付款的通知消息
    private static String notification = """{
      "Type" : "Notification",
      "MessageId" : "9898dad8-405e-5874-a273-8e694b7cd224",
      "TopicArn" : "arn:aws:sns:us-east-2:713166642506:T_BDD_PAYMENT_CHANNEL_UID_8",
      "Subject" : "sS4FbkMenwlGeE4j9FUAxs9Lfihrhz7PN+onD+EvYeM=",
      "Message" : "{\\"amount\\":0.01000000,\\"fee\\":0.00010000,\\"noticeAction\\":101,\\"noticeType\\":\\"OrderNotice\\",\\"outOrderSn\\":\\"1547110615308\\",\\"price\\":7.0200,\\"status\\":2,\\"total\\":0.08000000}",
      "Timestamp" : "2019-01-10T08:57:06.316Z",
      "SignatureVersion" : "1",
      "Signature" : "acAXEZ5e+mJO8SHiBBxWz0JdEy1N5OEwCWsQVmN4r0zN8cJ9fy4UilVIPXvyOCf81WLtDWiPNFCnIC4z4cBJRVfrhRq0VhgSAG5F74b3LJyyZ3Rmn4LMMcPvih55RnBhluotcRDYPflY8kRpUhPWVRoA6bjdeHezyVmEAVeDYt3p0cxIqAH4ooOge1a3wbNQXlxtYGlMMnXPl5HF4OQNtiOVt2OW/+GDKSaJKTCW1mMHpMcFdl1fVySwhW+ccFWFdp7snHisFGQyw8XfoBsTeORbncfJMmJI2XVZTG3ejCRasEiIICDnHHq7KBa3m9Phhr6YIWkHhUtPGrDNhWz/5A==",
      "SigningCertURL" : "https://sns.us-east-2.amazonaws.com/SimpleNotificationService-ac565b8b1a6c5d002d285f9598aa1d9b.pem",
      "UnsubscribeURL" : "https://sns.us-east-2.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-east-2:713166642506:T_BDD_PAYMENT_CHANNEL_UID_8:565fca06-8ea9-470d-a781-7ee925214470"
    }"""

    void setup() {
        orderNotifyService = new OrderNotifyService("QHqNZXZZ2XRm")
    }

    def "buyCallback"() {
        def function = new Function<BuyOrderSyncMessage, Boolean>() {
            @Override
            Boolean apply(BuyOrderSyncMessage params) {
                println JSON.toJSONString(params)
                return Boolean.TRUE
            }
        }
        BuyOrderSyncMessage params = new BuyOrderSyncMessage()
        orderNotifyService.buyCallback("", "", function)
        expect: true
    }

    def "notification"() {
        //创建回调的Function 如果业务处理失败可抛出异常 重复消费
        def function = new Function<OrderStatusAsyncNotifyMessage, Boolean>() {
            @Override
            Boolean apply(OrderStatusAsyncNotifyMessage orderNotifyMessage) {
                println JSON.toJSONString(orderNotifyMessage)
                return Boolean.TRUE
            }
        }
        orderNotifyService.orderStatusNotifyHandle(notification, function)
        expect: true
    }

}
