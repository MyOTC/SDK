package com.bdd.service

import com.bdd.domain.request.ApiOrderDetailRequest
import com.bdd.domain.request.ApiPriceRequest
import com.bdd.domain.request.ApiSellOrderRequest
import com.bdd.domain.request.GatewayBuyOrderConfirmPayRequest
import com.bdd.domain.request.GatewayBuyOrderRequest
import com.bdd.domain.request.GatewaySellOrderDetailRequest
import spock.lang.Specification
/**
 * 订单相关操作测试类
 * @author qxx on 2019/1/4.
 */
class OrderServiceTest extends Specification {

    private static OrderService orderService

    void setup() {
        orderService = new OrderService("mqqzc2lm-qomnzbj1-5zpihc19-n2743","pcezw3lb-oaro4yfi-oy6h7vzs-s0536","http://gateway.rmmlm.com")
    }

    /**
     * 获取价格
     */
    def "getPrice"() {
        def request = new ApiPriceRequest()
        request.variety = "usdt"
        request.currency = "CNY"
        def response = orderService.getPrice(request)
        expect: response.variety != null
    }

    /**
     * 获取账号信息
     */
    def "getAccount"() {
        def response = orderService.getAccount()
        expect: response != null
    }

    /**
     * 获取订单详情
     */
    def "getOrderDetail"() {
        def request = new ApiOrderDetailRequest()
        request.outOrderSn = "1546856721812"
        def response = orderService.getOrderDetail(request)
        println response
        expect: response != null
    }

    /**
     * 出金下单(创建卖币订单)
     */
    def "createSellOrder"() {
        def request = new ApiSellOrderRequest()
        request.outOrderSn = System.currentTimeMillis().toString()
        request.amount = "100"
        request.unit = 2
        request.currency = "CNY"
        request.variety = "usdt"
        request.outUid = "10083"
        request.paymentType = 3
        request.name = "张三"
        request.idNumber = "222301196708070315"
        request.bank = "中国银行"
        request.bankName = "软件人支行"
        request.number = "12345654123"

        def response = orderService.createSellOrder(request)
        expect: response != null
    }

    /**
     * 创建买币订单
     */
    def "buyUrl"() {
        def request = new GatewayBuyOrderRequest()
        request.outOrderSn = System.currentTimeMillis().toString()
        request.amount = "2"
        request.unit = 2
        request.currency = "CNY"
        request.variety = "usdt"
        request.outUid = "10105"
        request.callback = "http://ngrok.xiaomiqiu.cn:33769/callbackURI"
        request.name = "张三"
        request.idNumber = "222301196708070315"
        request.mobile = "17316227689"
        def url = orderService.buyUrl(request)
        expect: url != null
    }

    /**
     * 确认付款
     */
    def "confirmPayUrl"() {
        def request = new GatewayBuyOrderConfirmPayRequest()
        request.outOrderSn = "1546913559705"
        def url = orderService.confirmPayUrl(request)
        expect: url != null
    }

    /**
     * 支付网关出金卖币订单详情
     */
    def "sellOrderDetailUrl"() {
        def request = new GatewaySellOrderDetailRequest()
        request.outOrderSn ="1546872415745"
        def url = orderService.sellOrderDetailUrl(request)
        expect: url != null
    }
}
