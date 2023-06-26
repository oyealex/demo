package com.oye.common.autoset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * AutoSetTest
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-11-13
 */
public class AutoSetTest {
    public static void main(String[] args) {

    }

    private static class AutoSetFactory {
        private static final Map<Class<?>, SetupFunction> CACHE = new ConcurrentHashMap<>();

        public static void setup(@NotNull Object instance, @NotNull String key, @NotNull String value) {
            Class<?> type = instance.getClass();
            SetupFunction function = CACHE.computeIfAbsent(type, AutoSetFactory::parse);
            try {
                function.setup(instance, key, value);
            } catch (IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }

        private static SetupFunction parse(Class<?> type) {
            Map<String, CachedMethod> functions = new HashMap<>();
            for (Field field : type.getFields()) {
                AutoSet autoSet = field.getAnnotation(AutoSet.class);
                if (autoSet == null) {
                    continue;
                }
                field.setAccessible(true);

            }
            // Map<String, CachedMethod> functions = Arrays.stream(type.getFields())
            //         .filter(field -> field.isAnnotationPresent(AutoSet.class)).peek(field -> field.setAccessible
            //         (true))
            //         .collect(toUnmodifiableMap(field -> field.getAnnotation(AutoSet.class).key(),
            //                 field -> new CachedMethod(field, null)));
            return new SetupFunction(functions);
        }

        private static CachedMethod buildCachedMethod(Field field) {
            return null;
        }

        @RequiredArgsConstructor
        private static class SetupFunction {
            private final Map<String, CachedMethod> functions;

            private void setup(Object instance, String key, String value) throws IllegalAccessException {
                CachedMethod cachedMethod = functions.get(key);
                if (cachedMethod == null) {
                    return;
                }
                cachedMethod.field.set(instance, value);
            }
        }

        @Getter
        @RequiredArgsConstructor
        private static class CachedMethod {
            private final Field field;

            @Nullable
            private final Method valueFunction;
        }
    }

    @Setter
    @Getter
    @ToString
    @Accessors(chain = true)
    private static class Person {
        @AutoSet
        private String name;

        @AutoSet(key = "local address")
        private String address;
    }

    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AutoSet {
        String key() default "";

        boolean setSupper() default false;

        boolean ignoreCase() default false;
    }
}
