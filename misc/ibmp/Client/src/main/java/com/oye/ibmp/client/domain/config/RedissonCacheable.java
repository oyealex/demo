package com.oye.ibmp.client.domain.config;

import org.redisson.Redisson;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * 基于{@link Redisson}对{@link Cacheable}的扩展
 *
 * @author oyealex
 * @since 2023-01-18
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Cacheable(value = "")
public @interface RedissonCacheable {
    /** {@link Cacheable#value()}的别名 */
    @AliasFor(annotation = Cacheable.class, value = "value")
    String[] value() default {};

    /** {@link Cacheable#cacheNames()}的别名 */
    @AliasFor(annotation = Cacheable.class, value = "cacheNames")
    String[] cacheNames() default {};

    /** {@link Cacheable#key()}的别名 */
    @AliasFor(annotation = Cacheable.class, value = "key")
    String key() default "";

    /** {@link Cacheable#keyGenerator()}的别名 */
    @AliasFor(annotation = Cacheable.class, value = "keyGenerator")
    String keyGenerator() default "";

    /** {@link Cacheable#cacheManager()}的别名 */
    @AliasFor(annotation = Cacheable.class, value = "cacheManager")
    String cacheManager() default "";

    /** {@link Cacheable#cacheResolver()}的别名 */
    @AliasFor(annotation = Cacheable.class, value = "cacheResolver")
    String cacheResolver() default "";

    /** {@link Cacheable#condition()}的别名 */
    @AliasFor(annotation = Cacheable.class, value = "condition")
    String condition() default "";

    /** {@link Cacheable#unless()}的别名 */
    @AliasFor(annotation = Cacheable.class, value = "unless")
    String unless() default "";

    /** {@link Cacheable#sync()}的别名 */
    @AliasFor(annotation = Cacheable.class, value = "sync")
    boolean sync() default false;

    /** 最大存活时间 */
    long ttl() default 0L;

    /** 最大空闲时间 */
    long maxIdleTime() default 0L;

    /** 自动刷新间隔 */
    long refreshRate() default 0L;

    /** 时间单位 */
    TimeUnit unit() default TimeUnit.MILLISECONDS;

    /**
     * 缓存刷新方法名称
     * <p/>
     * 方法必须接受一个{@link BiConsumer}参数，用于接受需要刷新的缓存，第一个参数为缓存的String类型key，第二个参数为缓存的值对象，
     * 方法的返回值会被忽略，异常会被记录日志
     */
    String refreshMethod() default "";
}
