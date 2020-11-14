/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.rest.uri.auto.scan;

import com.oyealex.server.rest.uri.UriManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO 2020/8/31 The PostRegister
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/31
 */
@Slf4j
@Component
public class RestResponseTypeCache implements BeanPostProcessor {
    private final Map<String, ParameterizedTypeReference<?>> uriToResponseTypeMapCache = new HashMap<>();

    private final String[] defaultClassBaseRequestUris = {""};

    private final String[] defaultMethodValidUris = {};

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        parseBeanDefinition(bean);
        return bean;
    }

    private void parseBeanDefinition(Object bean) {
        final String[] classBaseRequestUris = getClassBaseRequestUris(bean);
        final Method[] allClassMethods = bean.getClass().getMethods();
        Arrays.stream(allClassMethods).filter(this::isRequestMappingMethod)
            .forEach(classMethod -> registerMethodIfNeed(classBaseRequestUris, classMethod));
    }

    private String[] getClassBaseRequestUris(Object bean) {
        RequestMapping requestMappingAnnotation = bean.getClass().getDeclaredAnnotation(RequestMapping.class);
        if (requestMappingAnnotation != null) {
            return getValidUris(requestMappingAnnotation.value(), requestMappingAnnotation.path());
        } else {
            return defaultClassBaseRequestUris;
        }
    }

    private boolean isRequestMappingMethod(Method method) {
        return Arrays.stream(method.getAnnotations()).anyMatch(this::isRequestMappingAnnotation);
    }

    private boolean isRequestMappingAnnotation(Annotation annotation) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        return annotationType.isAnnotationPresent(Mapping.class) ||
            annotationType.isAnnotationPresent(RequestMapping.class);
    }

    private void registerMethodIfNeed(String[] classBaseRequestUris, Method method) {
        final Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (isAnnotatedBy(annotation, Mapping.class)) {
                handleRequestMapping(classBaseRequestUris, annotation, method);
                return;
            } else if (isAnnotatedBy(annotation, RequestMapping.class)) {
                handleMethodMapping(classBaseRequestUris, annotation, method);
                return;
            }
        }
    }

    private boolean isAnnotatedBy(Annotation annotation,
        Class<? extends Annotation> targetAnnotationClass) {
        return annotation.annotationType().isAnnotationPresent(targetAnnotationClass);
    }

    private void handleRequestMapping(String[] classBaseRequestUris, Annotation annotation, Method method) {
        if (!(annotation instanceof RequestMapping)) {
            return;
        }

        RequestMapping requestMappingAnnotation = (RequestMapping) annotation;
        batchRegisterRequestMethodAndUriToResponseType(classBaseRequestUris,
            requestMappingAnnotation.method(),
            getValidUrisFromRequestMapping(requestMappingAnnotation),
            method.getGenericReturnType());
    }

    private void handleMethodMapping(String[] classBaseRequestUris, Annotation methodMappingAnnotation,
        Method method) {
        RequestMapping requestMappingAnnotation = methodMappingAnnotation.annotationType()
            .getDeclaredAnnotation(RequestMapping.class);
        batchRegisterRequestMethodAndUriToResponseType(classBaseRequestUris,
            requestMappingAnnotation.method(),
            getValidUrisFromMethodMapping(methodMappingAnnotation),
            method.getGenericReturnType());
    }

    private String[] getValidUrisFromMethodMapping(Annotation annotation) {
        if (annotation instanceof GetMapping) {
            GetMapping getMappingAnn = (GetMapping) annotation;
            return getValidUris(getMappingAnn.value(), getMappingAnn.path());
        } else if (annotation instanceof PostMapping) {
            PostMapping getMappingAnn = (PostMapping) annotation;
            return getValidUris(getMappingAnn.value(), getMappingAnn.path());
        } else if (annotation instanceof PutMapping) {
            PutMapping getMappingAnn = (PutMapping) annotation;
            return getValidUris(getMappingAnn.value(), getMappingAnn.path());
        } else if (annotation instanceof DeleteMapping) {
            DeleteMapping getMappingAnn = (DeleteMapping) annotation;
            return getValidUris(getMappingAnn.value(), getMappingAnn.path());
        } else if (annotation instanceof PatchMapping) {
            PatchMapping getMappingAnn = (PatchMapping) annotation;
            return getValidUris(getMappingAnn.value(), getMappingAnn.path());
        } else {
            return defaultMethodValidUris;
        }
    }

    private void batchRegisterRequestMethodAndUriToResponseType(String[] classBaseRequestUris,
        RequestMethod[] methods, String[] uris,
        Type responseType) {
        Arrays.stream(classBaseRequestUris)
            .forEach(classBaseRequestPath ->
                batchRegisterUriResponseTypeWithClassBaseRequestPath(classBaseRequestPath,
                    methods,
                    uris,
                    responseType));
    }

    private void batchRegisterUriResponseTypeWithClassBaseRequestPath(String classBaseRequestPath,
        RequestMethod[] methods, String[] uris,
        Type responseType) {
        Arrays.stream(getValidMethod(methods))
            .forEach(requestMethod -> batchRegisterUriToResponseType(requestMethod,
                classBaseRequestPath,
                uris,
                responseType));
    }

    private RequestMethod[] getValidMethod(RequestMethod[] requestMethods) {
        return requestMethods.length == 0 ? new RequestMethod[]{RequestMethod.GET} : requestMethods;
    }

    private void batchRegisterUriToResponseType(RequestMethod requestMethod, String classBaseRequestPath,
        String[] uris, Type responseType) {
        Arrays.stream(uris).forEach(uri -> registerOneUri(requestMethod, classBaseRequestPath + uri, responseType));
    }

    private String[] getValidUrisFromRequestMapping(RequestMapping requestMappingAnn) {
        return getValidUris(requestMappingAnn.value(), requestMappingAnn.path());
    }

    private String[] getValidUris(String[] values, String[] paths) {
        return values.length == 0 ? paths : values;
    }

    private void registerOneUri(RequestMethod method, String uri, Type responseType) {
        registerRestResponseType(method.name(), uri, responseType);
    }

    private void registerRestResponseType(String methodName, String uri, Type responseType) {
        uriToResponseTypeMapCache.put(getUriKey(methodName, uri), ParameterizedTypeReference.forType(responseType));
    }

    private String getUriKey(String methodName, String uri) {
        return methodName + uri;
    }

    public void registerCacheTo(UriManager uriManager) {
        uriManager.registerAll(uriToResponseTypeMapCache);
        uriToResponseTypeMapCache.clear();
    }
}
