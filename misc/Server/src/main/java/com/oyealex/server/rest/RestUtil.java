/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.rest;

import com.oyealex.server.endpoint.EndPoint;
import com.oyealex.server.rest.uri.UriManager;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * TODO 2020/8/30 The RestUtil
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Slf4j
@Component
public class RestUtil {
    @Setter(onMethod_ = {@Autowired})
    private RestTemplate restTemplate;

    @Setter(onMethod_ = {@Autowired})
    private UriManager uriManager;

    public <T> Optional<T> get(@NonNull EndPoint endPoint, @NonNull String uri, HttpHeaders headers,
        Object... uriVariables) {
        return request(endPoint, HttpMethod.GET, uri, null, headers, uriVariables);
    }

    public <T> Optional<T> put(@NonNull EndPoint endPoint, @NonNull String uri, Object body, HttpHeaders headers,
        Object... uriVariables) {
        return request(endPoint, HttpMethod.PUT, uri, body, headers, uriVariables);
    }

    public <T> Optional<T> post(@NonNull EndPoint endPoint, @NonNull String uri, Object body, HttpHeaders headers,
        Object... uriVariables) {
        return request(endPoint, HttpMethod.POST, uri, body, headers, uriVariables);
    }

    public <T> Optional<T> delete(@NonNull EndPoint endPoint, @NonNull String uri, HttpHeaders headers,
        Object... uriVariables) {
        return request(endPoint, HttpMethod.DELETE, uri, null, headers, uriVariables);
    }

    private <T> Optional<T> request(EndPoint endPoint, HttpMethod method, String uri, Object body,
        HttpHeaders headers,
        Object... uriVariables) {
        try {
            return Optional.<ResponseEntity<T>>of(restTemplate.exchange(getCompleteUrl(endPoint, uri), method,
                buildHttpEntity(body, headers),
                uriManager.getExpectedResponseType(method, uri),
                uriVariables)).map(HttpEntity::getBody);
        } catch (Exception exception) {
            log.error("request {} {} {} with headers: {}, body: {} failed: ", method, uri, uriVariables, headers,
                body, exception);
            return Optional.empty();
        }
    }

    private String getCompleteUrl(EndPoint endPoint, String uri) {
        return endPoint.getUrlLeadingWithSchema("http") + uri;
    }

    private <T> HttpEntity<T> buildHttpEntity(T body, HttpHeaders headers) {
        return body == null && headers == null ? null : new HttpEntity<>(body, headers);
    }
}
