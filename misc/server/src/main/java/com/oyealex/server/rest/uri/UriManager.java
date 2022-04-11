/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.rest.uri;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO 2020/8/30 The Uris
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Component
public class UriManager {
    public static final String STUDENT = "/student";

    public static final String TEACHER = "/teacher";

    public static final String STUDENT_BY_ID = "/student/{id}";

    public static final String STUDENT_BY_ID_2 = "/student/{id}/get";

    private final ParameterizedTypeReference<?> defaultResponseType =
        new ParameterizedTypeReference<Map<String, ?>>() {
        };

    private final Map<String, ParameterizedTypeReference<?>> uriToResponseTypeMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> ParameterizedTypeReference<T> getExpectedResponseType(HttpMethod method, String uri) {
        return (ParameterizedTypeReference<T>) uriToResponseTypeMap.getOrDefault(getUriKey(method.name(), uri),
            defaultResponseType);
    }

    public void registerAll(Map<String, ParameterizedTypeReference<?>> map) {
        uriToResponseTypeMap.putAll(map);
    }

    private String getUriKey(String methodName, String uri) {
        return methodName + uri;
    }
}
