/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oye.common;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * 临时测试类
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/10/19
 */
@Slf4j
public class Test {
    private static final Duration TIMEOUT = Duration.of(10L, ChronoUnit.SECONDS);

    public static void main(String[] args)
        throws URISyntaxException, IOException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCertificates = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                }
            }};
        SSLParameters parameters = new SSLParameters();
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, trustAllCertificates, new SecureRandom());
        HttpClient client = HttpClient.newBuilder()
            .sslParameters(parameters)
            .sslContext(context)
            .connectTimeout(TIMEOUT).version(HttpClient.Version.HTTP_2).build();
        HttpRequest request =
            HttpRequest.newBuilder().uri(new URI("https://www.baidu.com")).GET().timeout(TIMEOUT).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}
