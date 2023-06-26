package com.oyealex.exp.annotationex.example;

/**
 * SingletonExample
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-11-29
 */
public class SingletonExample {
    public static SingletonExample getSingleton() {
        return $SingletonExampleInnerSingletonHolder.INSTANCE;
    }

    private static class $SingletonExampleInnerSingletonHolder {
        private final static SingletonExample INSTANCE = new SingletonExample();
    }
}
