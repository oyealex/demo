/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.rest.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * TODO 2020/8/30 The RestTemplateConfig
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate getRestTemplate(GsonHttpMessageConverter gsonHttpMessageConverter) {
        return new RestTemplateBuilder()
            .additionalMessageConverters(gsonHttpMessageConverter)
            .setConnectTimeout(Duration.ofSeconds(5L))
            .setReadTimeout(Duration.ofSeconds(5L))
            .build();
    }
}
