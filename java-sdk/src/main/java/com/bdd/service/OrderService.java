package com.bdd.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;

import com.bdd.domain.AssetAccountDomain;
import com.bdd.domain.BddPayload.Group1;
import com.bdd.domain.BddPayload.Group2;
import com.bdd.domain.JsonResult;
import com.bdd.domain.request.ApiOrderDetailRequest;
import com.bdd.domain.request.ApiPriceRequest;
import com.bdd.domain.request.ApiSellOrderRequest;
import com.bdd.domain.request.GatewayBuyOrderConfirmPayRequest;
import com.bdd.domain.request.GatewayBuyOrderRequest;
import com.bdd.domain.request.GatewaySellOrderDetailRequest;
import com.bdd.domain.response.OrderInfoResponse;
import com.bdd.domain.response.PriceResponse;
import com.bdd.domain.response.SellOrderResponse;
import com.bdd.exception.BddException;
import com.bdd.exception.InvalidParameterException;
import com.bdd.utils.ApiIdentityUtil;
import com.bdd.utils.HttpUtil;
import com.bdd.utils.ValidatorHelper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * 订单相关
 *
 * @see <a href="https://github.com/bestotc/APIDOC/wiki/%E8%AE%A2%E5%8D%95%E6%8E%A5%E5%8F%A3" target="_top">API文档</a>
 */
@Slf4j
public class OrderService {
    /**
     * 秘匙
     */
    private final String ACCESS_KEY_ID;

    /**
     * 秘钥
     */
    private final String ACCESS_KEY_SECRET;

    /**
     * 服务器地址
     */
    private final String HOST;
    /**
     * 协议+ HOST
     */
    private final String DOMAIN;

    /**
     * httpClient
     */
    private final HttpClient httpClient;

    /**
     * 构造方法
     *
     * @param accessKeyId     秘匙
     * @param accessKeySecret 秘钥
     * @param bddDomain otc域名全称 测试:http://gateway.rmmlm.com
     */
    public OrderService(@NonNull String accessKeyId, @NonNull String accessKeySecret, @NonNull String bddDomain) {
        this.ACCESS_KEY_ID = accessKeyId;
        this.ACCESS_KEY_SECRET = accessKeySecret;
        this.httpClient = HttpUtil.getHttpClient();
        this.HOST = getHost(bddDomain);
        this.DOMAIN = bddDomain;
    }

    /**
     * 自带httpClient构造方法
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param httpClient      指定httpClient
     */
    public OrderService(@NonNull String accessKeyId, @NonNull String accessKeySecret, @NonNull String bddDomain, @NonNull HttpClient httpClient) {
        this.ACCESS_KEY_ID = accessKeyId;
        this.ACCESS_KEY_SECRET = accessKeySecret;
        this.httpClient = httpClient;
        this.HOST = getHost(bddDomain);
        this.DOMAIN = bddDomain;
    }

    /**
     * 获取账号信息
     */
    public List<AssetAccountDomain> getAccount() {
        Map<String, String> params = new HashMap<>();
        String uri = "/v1/api/open/account";
        log.info("getAccount,request={}");
        return callForList(uri, params, AssetAccountDomain.class);
    }

    /**
     * 查询价格
     */
    public PriceResponse getPrice(ApiPriceRequest request) {
        ValidatorHelper.validator(request);
        Map<String, String> params = ApiIdentityUtil.toMap(request);
        String uri = "/v1/api/open/price";
        log.info("getPrice,request={}",request);
        return call(uri, params, PriceResponse.class);
    }

    /**
     * 获取订单详情
     */
    public OrderInfoResponse getOrderDetail(ApiOrderDetailRequest request) {
        ValidatorHelper.validator(request);
        Map<String, String> params = ApiIdentityUtil.toMap(request);
        String uri = "/v1/api/open/order/detail";
        log.info("getOrderDetail,request={}",request);
        return callForDetail(uri, params, OrderInfoResponse.class);
    }

    /**
     * 出金下单(创建卖币订单)
     */
    public SellOrderResponse createSellOrder(ApiSellOrderRequest request) {
        ValidatorHelper.validator(request);
        Integer paymentType = request.getPaymentType();
        if (paymentType == 3) {
            ValidatorHelper.validator(request, Group1.class);
        } else {
            ValidatorHelper.validator(request, Group2.class);
        }
        Map<String, String> params = ApiIdentityUtil.toMap(request);
        String uri = "/v1/api/open/order/sell";
        log.info("getAccount,request={}",request);
        return call(uri, params, SellOrderResponse.class);
    }

    /**
     * 支付网关卖币订单(出金)查看详情
     * @param request
     * @return 生成支付网关订单详情url
     */
    public String sellOrderDetailUrl(GatewaySellOrderDetailRequest request) {
        ValidatorHelper.validator(request);
        Map<String, String> param = ApiIdentityUtil.toMap(request);
        log.info("sellOrderDetail,request={}", request);
        String url = ApiIdentityUtil.getSignUrl(ACCESS_KEY_ID, ACCESS_KEY_SECRET, "1",
            HmacAlgorithms.HMAC_SHA_256.getName(), ApiIdentityUtil.gmtNow(), DOMAIN + "/gateway/v1/receipt", param);
        log.info("sellOrderDetail,url={}", url);
        return url;
    }

    /**
     * 支付网关创建买币订单(入金)
     * @param request
     * @return 买币订单的url
     */
    public String buyUrl(GatewayBuyOrderRequest request) {
        ValidatorHelper.validator(request);
        Map<String, String> param = ApiIdentityUtil.toMap(request);
        log.info("buyUrl,request={}", request);
        String url = ApiIdentityUtil.getSignUrl(ACCESS_KEY_ID, ACCESS_KEY_SECRET,"1", HmacAlgorithms.HMAC_SHA_256.getName(),
            ApiIdentityUtil.gmtNow(),DOMAIN+"/gateway/v1/pay", param);
        log.info("buyUrl,url={}", url);
        return url;
    }

    /**
     * 支付网关买币订单确认付款
     * @param request
     * @return 发起付款的url
     */
    public String confirmPayUrl(GatewayBuyOrderConfirmPayRequest request) {
        ValidatorHelper.validator(request);
        Map<String, String> param = ApiIdentityUtil.toMap(request);
        log.info("confirmPayUrl,request={}", request);
        String url = ApiIdentityUtil.getSignUrl(ACCESS_KEY_ID, ACCESS_KEY_SECRET, "1",
            HmacAlgorithms.HMAC_SHA_256.getName(), ApiIdentityUtil.gmtNow(), DOMAIN + "/gateway/v1/confirmPay", param);
        log.info("confirmPayUrl,url={}", url);
        return url;
    }

    private <T> T call(String uri, Map<String, String> params, Class<T> clazz) {
        return baseCall(uri, params).getBody().toJavaObject(clazz);
    }

    /**
     * 详情接口会出现null的情况
     */
    private <T> T callForDetail(String uri, Map<String, String> params, Class<T> clazz) {
        JSON jsonBody = baseCall(uri, params).getBody();
        if (jsonBody == null) {
            return null;
        }
        return jsonBody.toJavaObject(clazz);
    }

    private <T> List<T> callForList(String uri, Map<String, String> params, Class<T> clazz) {
        return ((JSONArray)baseCall(uri, params).getBody()).toJavaList(clazz);
    }

    private <T> T call(String uri, Map<String, String> params, TypeReference<T> typeReference) {
        return JSON.parseObject(baseCall(uri, params).getBody().toJSONString(), typeReference);
    }

    /**
     * 生成签名的url并发起请求
     * @param uri 接口uri
     * @param params 参数
     * @return
     */
    private JsonResult<JSON> baseCall(String uri, Map<String, String> params) {
        String json = ApiIdentityUtil.sign(ACCESS_KEY_ID, ACCESS_KEY_SECRET, "1", HmacAlgorithms.HMAC_SHA_256.getName(), ApiIdentityUtil.gmtNow(), HttpPost.METHOD_NAME,
            HOST, uri, params);
        log.info("requestData={}", json);
        HttpPost httpPost = new HttpPost(DOMAIN + uri);
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        String responseData;
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            InputStream inputStream = httpResponse.getEntity().getContent();
            responseData = IOUtils.toString(inputStream);
            inputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BddException("HttpClient IOException");
        }
        log.info("responseData={}", responseData);
        JsonResult<JSON> jsonResult = JSON.parseObject(responseData, new TypeReference<JsonResult<JSON>>() {});
        if(200 !=jsonResult.getCode()){
            throw new BddException("call bdd service error");
        }
        return jsonResult;
    }

    /**
     * 获取host (签名需要)
     * @param otcDomain
     * @return
     */
    private String getHost(String otcDomain) {
        String urlPattern = "^((https|http)://)"
            + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"
            + "|"
            + "([0-9a-z_!~*'()-]+\\.)*"
            + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."
            + "[a-z]{2,6})"
            + "(:[0-9]{1,5})?";
        boolean matches = Pattern.compile(urlPattern).matcher(otcDomain).matches();
        if (!matches) {
            throw new InvalidParameterException("otcDomain");
        }
        String[] split = otcDomain.split("//");
        return split[1];
    }


}
