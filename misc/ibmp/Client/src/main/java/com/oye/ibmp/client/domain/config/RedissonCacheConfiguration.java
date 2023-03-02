package com.oye.ibmp.client.domain.config;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RedissonCacheConfiguration
 *
 * @author oyealex
 * @since 2023-01-18
 */
@Slf4j
@EnableCaching
@Configuration
@ConditionalOnExpression("T(Boolean).parseBoolean(${app.cache.enabled})")
public class RedissonCacheConfiguration {
    @Bean
    public CacheManager redissonCacheManager(RedissonClient client, RedissonCacheConfig config) {
        return new RedissonSpringCacheManager(client, config.getConfigs());
    }

    @Getter
    @ToString
    public static class RedissonCacheConfig {
        public Map<String, CacheConfig> configs = new HashMap<>();

        public void addConfig(String key, long ttl, long maxIdleTime) {
            configs.put(key, new CacheConfig(ttl, maxIdleTime));
        }
    }
}
