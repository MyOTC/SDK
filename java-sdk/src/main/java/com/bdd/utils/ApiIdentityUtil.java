package com.bdd.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeSet;

import com.alibaba.fastjson.JSON;

import com.bdd.domain.BuyOrderSyncMessage;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.beanutils.BeanUtils;

/**
 * 签名工具类
 */
public class ApiIdentityUtil {

    private static final Logger log = LoggerFactory.getLogger(ApiIdentityUtil.class);

    private static class ApiIdentityParams {
        private static final String ACCESS_KEY_ID = "AccessKeyId";
        private static final String SIGNATURE_VERSION = "SignatureVersion";
        private static final String SIGNATURE_METHOD = "SignatureMethod";
        private static final String TIMESTAMP = "Timestamp";
        private static final String SIGNATURE = "Signature";
    }

    /**
     * 需要签名参数
     */
    private static String getSignParamsStr(String accessKeyId, String signatureVersion, String signatureMethod,
                                           String timestamp, Map<String, String> params) {
        params.remove(ApiIdentityParams.SIGNATURE);
        params.put(ApiIdentityParams.ACCESS_KEY_ID, accessKeyId);
        params.put(ApiIdentityParams.SIGNATURE_VERSION, signatureVersion);
        params.put(ApiIdentityParams.SIGNATURE_METHOD, signatureMethod);
        params.put(ApiIdentityParams.TIMESTAMP, timestamp);

        TreeSet<String> keys = new TreeSet<>(params.keySet());
        StringBuilder paramsStr = new StringBuilder();
        for (String k : keys) {
            paramsStr.append(k).append('=').append(urlEncode(params.get(k))).append('&');
        }
        return paramsStr.toString();
    }

    /**
     * 生成支付网关API接口请求的签名
     */
    public static String sign(String accessKeyId, String accessKeySecret, String signatureVersion,
                              String signatureMethod, String timestamp, String method, String host, String uri,
                              Map<String, String> params) {
        String paramsStr = getSignParamsStr(accessKeyId, signatureVersion, signatureMethod, timestamp, params);
        String strToSign = StringUtils.join(new String[] {method.toUpperCase(), host.toLowerCase(), uri,
            paramsStr.substring(0, paramsStr.length() - 1)}, "\n");
        String sign = sign(accessKeySecret, signatureMethod, strToSign);
        params.put(ApiIdentityParams.SIGNATURE, sign);
        return JSON.toJSONString(params);
    }

    /**
     * 生成支付网关请求的签名url
     */
    public static String getSignUrl(String accessKeyId, String accessKeySecret, String signatureVersion,
                                    String signatureMethod, String timestamp, String url, Map<String, String> params) {

        String paramsStr = getSignParamsStr(accessKeyId, signatureVersion, signatureMethod, timestamp, params);
        String strToSign = url + "?" + paramsStr;
        try {
            strToSign += ApiIdentityParams.SIGNATURE + "=" + URLEncoder.encode(signUrl(accessKeySecret, signatureMethod, strToSign),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strToSign;
    }

    /**
     * 对参数进行排序
     */
    public static String signUrl(String accessKeySecret, String signatureMethod, String url) {
        if (url == null) {
            return null;
        }
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        List<NameValuePair> nameValuePairList = URLEncodedUtils.parse(uri, Charset.forName("UTF-8"));
        Collections.sort(nameValuePairList, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Iterator<NameValuePair> iterator = nameValuePairList.iterator();
        while (iterator.hasNext()) {
            NameValuePair next = iterator.next();
            if (next.getName().equals(ApiIdentityParams.SIGNATURE)) {
                iterator.remove();
            }
        }
        String queryString = URLEncodedUtils.format(nameValuePairList, Charset.forName("UTF-8"));
        String strToSign = url.split("\\?")[0] + "?" + queryString;
        return sign(accessKeySecret, signatureMethod, strToSign);
    }

    /**
     * 对字符串strToSign做签名
     */
    public static String sign(String accessKeySecret, String signatureMethod, String strToSign) {
        String signature = null;
        if (Objects.equals(signatureMethod, HmacAlgorithms.HMAC_SHA_256.getName())) {
            signature = Base64.encodeBase64String(
                new HmacUtils(HmacAlgorithms.HMAC_SHA_256, accessKeySecret).hmac(strToSign));
        }
        return signature;
    }

    public static String gmtNow() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Z"));
        return format.format(new Date());
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("UTF-8 encoding not supported!");
        }
    }

    /**
     * 买币同步回调 验证url 签名 并转换 BuyOrderSyncMessage
     */
    public static Pair<Boolean, BuyOrderSyncMessage> verifySign(String signMethod, String accessKeySecret,String url)
        throws Exception {
        int i = url.lastIndexOf("&Signature=");
        if (i == 0) {
            log.info("url invalid,url={}", url);
            return Pair.of(Boolean.FALSE, null);
        }
        //签名url解码
        String sign1 = URLDecoder.decode(url.substring(i + "&Signature=".length()), "UTF-8");
        //对url进行签名
        String sign2 = sign(accessKeySecret, signMethod, url.substring(0, i));
        boolean equals = sign1.equals(sign2);
        if (!equals) {
            log.info("Sign invalid url={},sign1={},sign2={},signMethod={},accessKeySecret={}", url, sign1, sign2,signMethod,accessKeySecret);
        }
        return Pair.of(equals, parseBuyOrderSyncMessage(url));
    }

    /**
     * 将请求的参数转为BuyOrderSyncMessage
     *
     * @param url
     * @return
     * @throws Exception
     */
    private static BuyOrderSyncMessage parseBuyOrderSyncMessage(String url) throws Exception {
        URI uri = new URI(url);
        List<NameValuePair> nameValuePairList = URLEncodedUtils.parse(uri, Charset.forName("UTF-8"));
        BuyOrderSyncMessage buyOrderSyncMessage = new BuyOrderSyncMessage();
        for (NameValuePair nameValuePair : nameValuePairList) {
            BeanUtils.setProperty(buyOrderSyncMessage, nameValuePair.getName(), nameValuePair.getValue());
        }
        return buyOrderSyncMessage;
    }

    /**
     * Converts a JavaBean to a map.
     *
     * @param bean JavaBean to convert
     * @return map converted
     */
    public static Map<String, String> toMap(Object bean) {
        Map<String, String> returnMap = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!"class".equals(propertyName)) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean, new Object[0]);
                    if (result != null) {
                        returnMap.put(propertyName, result.toString());
                    }
                }
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();    // failed to call setters
        } catch (IntrospectionException e) {
            e.printStackTrace();    // failed to get class fields
        } catch (IllegalAccessException e) {
            e.printStackTrace();    // failed to instant JavaBean
        }

        return returnMap;
    }

}
