/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * TODO 2020/8/30 The GlobalSerializeConfig
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Configuration
public class GlobalSerializeConfig {
    @Bean
    public Gson getGson() {
        return new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();
    }

    @Bean
    public GsonHttpMessageConverter getGsonHttpMessageConverter() {
        return new GsonHttpMessageConverter(getGson());
    }
}
