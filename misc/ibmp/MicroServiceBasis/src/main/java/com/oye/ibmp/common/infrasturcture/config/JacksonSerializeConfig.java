package com.oye.ibmp.common.infrasturcture.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson Serialize Config
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-12-04
 */
@Configuration
public class JacksonSerializeConfig {
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
