package com.oye.ibmp.client.domain.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RedissonCacheableAnnotationProcessor
 *
 * @author oyealex
 * @since 2023-01-18
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("T(Boolean).parseBoolean(${app.cache.enabled})")
public class RedissonCacheableAnnotationProcessor implements BeanPostProcessor {
    private final ConfigurableBeanFactory factory;

    private final ThreadPoolTaskScheduler scheduler;

    private final RedissonCacheConfiguration.RedissonCacheConfig config
        = new RedissonCacheConfiguration.RedissonCacheConfig();

    private final List<RefreshTask> refreshTasks = new ArrayList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> type = ClassUtils.getUserClass(bean);
        for (Method method : ReflectionUtils.getUniqueDeclaredMethods(type)) {
            RedissonCacheable redissonCacheable = method.getAnnotation(RedissonCacheable.class);
            if (redissonCacheable == null) {
                continue;
            }
            TimeUnit unit = redissonCacheable.unit();
            long ttl = unit.toMillis(Math.max(redissonCacheable.ttl(), 0L));
            long maxIdleTime = unit.toMillis(Math.max(redissonCacheable.maxIdleTime(), 0L));
            long refreshRate = unit.toMillis(Math.max(redissonCacheable.refreshRate(), 0L));
            Set<String> names = Stream
                .concat(Arrays.stream(redissonCacheable.cacheNames()), Arrays.stream(redissonCacheable.value()))
                .filter(StringUtils::hasText).collect(Collectors.toSet());
            if (ttl > 0L || maxIdleTime > 0L) {
                for (String name : names) {
                    config.addConfig(name, ttl, maxIdleTime);
                    log.info("register redisson cache config with name: {}, ttl: {}, maxIdleTime: {}", name, ttl,
                        maxIdleTime);
                }
            }
            if (refreshRate > 0L) {
                String refreshMethod = redissonCacheable.refreshMethod();
                try {
                    MethodInvoker invoker = new MethodInvoker();
                    invoker.setTargetObject(bean);
                    invoker.setTargetMethod(refreshMethod);
                    invoker.prepare();
                    refreshTasks.add(new RefreshTask(names, invoker, refreshRate));
                } catch (ClassNotFoundException | NoSuchMethodException exception) {
                    throw new BeanCreationException("redisson cache refresh method not found", exception);
                }
            }
        }
        return bean;
    }

    @PostConstruct
    public void registerConfig() {
        factory.registerSingleton("redissonCacheConfig", config);
        refreshTasks.forEach(task -> {
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    task.invoker.invoke();
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }, new Date(), task.rate);
        });
        log.info("redissonCacheConfig registered");
    }

    @Getter
    @RequiredArgsConstructor
    private static class RefreshTask {
        private final Set<String> names;

        private final MethodInvoker invoker;

        private final long rate;
    }
}
