package com.oye.ibmp.server.infrastructure.cache.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基于Redisson的缓存配置
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-12-04
 */
@Configuration
@EnableCaching
public class RedissonCacheConfig {
    @Bean
    CacheManager cacheManager(RedissonClient client) {
        return new RedissonSpringCacheManager(client);
    }
}
