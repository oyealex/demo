package com.oye.ibmp.common.interfaces.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final ServiceInstancesProperties instances;

    @Bean
    public DiscoveryClient discoveryClient() {
        SimpleDiscoveryProperties properties = new SimpleDiscoveryProperties();
        properties.setInstances(instances.toHandledInstances());
        properties.init();
        log.info("service instances: {}", properties.getInstances());
        return new SimpleDiscoveryClient(properties);
    }

    @Getter
    @Setter
    @ToString
    @Component
    @ConfigurationProperties(prefix = "app.adaptors")
    public static class ServiceInstancesProperties {
        private Map<String, InnerDefaultServiceInstance> services = new HashMap<>();

        public Map<String, List<DefaultServiceInstance>> toHandledInstances() {
            return services.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, entry -> singletonList(entry.getValue())));
        }
    }

    @Getter
    @Setter
    private static class InnerDefaultServiceInstance extends DefaultServiceInstance {
        private Map<String, String> urls = new HashMap<>();

        @Override
        public String toString() {
            return super.toString() + ", urls=" + urls;
        }
    }
}
