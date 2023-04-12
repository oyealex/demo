package com.oye.ibmp.client.domain.config;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        return new MyRedissonCacheManager(client, config.getConfigs());
    }

    @Getter
    @ToString
    public static class RedissonCacheConfig {
        public Map<String, CacheConfig> configs = new HashMap<>();

        public void addConfig(String key, long ttl, long maxIdleTime) {
            configs.put(key, new CacheConfig(ttl, maxIdleTime));
        }
    }

    private static class MyRedissonCacheManager extends RedissonSpringCacheManager {
        private final RedissonClient redissonClient;

        public MyRedissonCacheManager(RedissonClient redisson, Map<String, ? extends CacheConfig> config) {
            super(redisson, config);
            this.redissonClient = redisson;
        }

        @Override
        public Cache getCache(String name) {
            return super.getCache(name);
        }

        @Override
        protected RMap<Object, Object> getMap(String name, CacheConfig config) {
            log.info("## build new map for cache {}, config: {}", name, config);
            return redissonClient.getLocalCachedMap(name,
                LocalCachedMapOptions.defaults().cacheSize(1).timeToLive(2, TimeUnit.SECONDS)
                    .maxIdle(2, TimeUnit.SECONDS).evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU)
                    .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.LOAD)
                    .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE));
        }

        @Override
        protected RMapCache<Object, Object> getMapCache(String name, CacheConfig config) {
            log.info("## build new map cache for cache {}, config: {}", name, config);
            return super.getMapCache(name, config);
        }
    }
}
