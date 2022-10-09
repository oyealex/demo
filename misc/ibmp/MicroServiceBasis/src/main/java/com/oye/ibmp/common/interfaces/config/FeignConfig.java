package com.oye.ibmp.common.interfaces.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableFeignClients(basePackages = "com.oye.ibmp")
@RequiredArgsConstructor
public class FeignConfig {
    private final SimpleDiscoveryProperties properties;

    @Bean
    public DiscoveryClient discoveryClient() {
        log.info("## build client: {}", properties.getInstances());
        return new SimpleDiscoveryClient(properties);
    }
}
