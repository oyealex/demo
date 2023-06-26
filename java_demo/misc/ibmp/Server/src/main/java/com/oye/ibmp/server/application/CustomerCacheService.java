package com.oye.ibmp.server.application;

import com.oye.ibmp.common.domain.common.entity.vo.CustomerVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存测试类
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-12-04
 */
@Service
public class CustomerCacheService {
    private final AtomicInteger counter = new AtomicInteger();

    @Cacheable(value = "customer", key = "#id")
    public CustomerVo getById(String id) {
        return new CustomerVo().setId(id).setName("name." + counter.incrementAndGet());
    }
}
