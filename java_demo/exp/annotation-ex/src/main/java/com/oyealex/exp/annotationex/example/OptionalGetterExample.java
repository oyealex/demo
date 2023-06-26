package com.oyealex.exp.annotationex.example;

import java.util.Optional;

/**
 * OptionalGetterExample
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-12-04
 */
public class OptionalGetterExample {
    private String name;

    public Optional<String> getNameOpt() {
        return Optional.ofNullable(name);
    }
}
