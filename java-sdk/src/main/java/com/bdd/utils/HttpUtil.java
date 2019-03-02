package com.bdd.utils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClients;

/**
 * HttpUtil
 * @author qxx on 2019/1/2.
 */
public class HttpUtil {

    /**
     * 生成一个默认的HttpClient
     * @return
     */
    public static HttpClient getHttpClient() {
        int timeout = (int) TimeUnit.SECONDS.toMillis(10);
        return HttpClients.custom()
                .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(timeout).setSoKeepAlive(true).setTcpNoDelay(true).build())
                .setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(StandardCharsets.UTF_8).build())
                .setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build())
                .setMaxConnPerRoute(20)
                .setMaxConnTotal(200)
                .build();
    }
}
