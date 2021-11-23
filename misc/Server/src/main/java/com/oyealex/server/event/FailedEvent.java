/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * FailedEvent
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-24
 */
@Getter
@RequiredArgsConstructor
public class FailedEvent<E> implements ResolvableTypeProvider {
    @NotNull
    private final E data;

    @NotNull
    private final Throwable cause;

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forClass(getData().getClass()));
    }
}
