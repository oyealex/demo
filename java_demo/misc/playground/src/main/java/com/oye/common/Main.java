package com.oye.common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Main
 *
 * @author oyealex
 * @since 2023-04-24
 */
public class Main {
    static Map<String, Object> data = new HashMap<>();

    public static void main(String[] args) {
        data.put("1", 1);
        data.put("a", "a");
        String v1 = get("1");
        System.out.println(v1);
    }

    static <T> T get(String key){
        return (T) data.get(key);
    }
}
