/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.annotation;

import com.oyealex.server.controller.CommonController;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO 2020/8/30 The AnnotationTestMain
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Slf4j
public class AnnotationTestMain {
    public static void main(String[] args) {
        log.info("{}", parseMethodToReturnTypeMap(CommonController.class));
    }

    // ParameterizedTypeReference
    private static <T> Map<String, Type> parseMethodToReturnTypeMap(Class<T> type) {
        Map<String, Type> methodToReturnTypeMap = new HashMap<>();
        Method[] allMethods = type.getDeclaredMethods();
        Arrays.stream(allMethods).forEach(method ->
            methodToReturnTypeMap.put(method.getName(),
                method.getGenericReturnType()));
        return methodToReturnTypeMap;
    }
}
