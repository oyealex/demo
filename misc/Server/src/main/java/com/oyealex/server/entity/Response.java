/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * TODO 2020/8/28 The Response
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/28
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(chain = true)
@ToString
public class Response<T> {
    @SuppressWarnings("rawtypes")
    private static final Response EMPTY_SUCCESS = new Response<>(true, null, null);

    @SuppressWarnings("rawtypes")
    private static final Response EMPTY_FAIL = new Response<>(false, null, null);

    private final boolean success;

    private final String msg;

    private final T data;

    public static <T> Response<T> success(T data) {
        return new Response<>(true, "", data);
    }

    public static <T> Response<T> fail(String msg) {
        return new Response<>(false, msg, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Response<T> success() {
        return (Response<T>) EMPTY_SUCCESS;
    }

    @SuppressWarnings("unchecked")
    public static <T> Response<T> fail() {
        return (Response<T>) EMPTY_FAIL;
    }
}
