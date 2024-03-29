package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.oyealex.pipe.basis.Pipe.generate;
import static com.oyealex.pipe.basis.Pipe.iterate;
import static com.oyealex.pipe.basis.Pipe.list;
import static com.oyealex.pipe.basis.Pipe.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PipeTestBase
 *
 * @author oyealex
 * @since 2023-04-28
 */
public abstract class PipeTestFixture { // TODO 2023-06-07 01:24 全面增加对flag优化内容的测试
    protected static final String SOME_STR = "SOME_STR";

    protected static final int NORMAL_SIZE = 20;

    protected static Pipe<Integer> integerPipe() {
        return infiniteIntegerPipe().limit(NORMAL_SIZE);
    }

    protected static Pipe<Integer> infiniteIntegerPipe() {
        return iterate(0, i -> i + 1);
    }

    private static Pipe<Integer> evenIntegerPipe() {
        return infiniteEvenIntegerPipe().limit(NORMAL_SIZE);
    }

    private static Pipe<Integer> infiniteEvenIntegerPipe() {
        return iterate(0, i -> i + 2);
    }

    private static Pipe<Integer> oddIntegerPipe() {
        return infiniteOddIntegerPipe().limit(NORMAL_SIZE);
    }

    private static Pipe<Integer> infiniteOddIntegerPipe() {
        return iterate(1, i -> i + 2);
    }

    protected static Pipe<String> integerStrPipe() {
        return infiniteIntegerStrPipe().limit(NORMAL_SIZE);
    }

    protected static Pipe<String> infiniteIntegerStrPipe() {
        return infiniteIntegerPipe().map(String::valueOf);
    }

    protected static Pipe<String> evenIntegerStrPipe() {
        return infiniteEvenIntegerStrPipe().limit(NORMAL_SIZE);
    }

    protected static Pipe<String> infiniteEvenIntegerStrPipe() {
        return infiniteEvenIntegerPipe().map(String::valueOf);
    }

    protected static Pipe<String> oddIntegerStrPipe() {
        return infiniteOddIntegerStrPipe().limit(NORMAL_SIZE);
    }

    protected static Pipe<String> infiniteOddIntegerStrPipe() {
        return infiniteOddIntegerPipe().map(String::valueOf);
    }

    protected static Pipe<String> prefixedIntegerStrPipe(String prefix) {
        return infiniteIntegerStrPipe().map(v -> prefix + v);
    }

    protected static Pipe<String> randomStrPipe() {
        return infiniteRandomStrPipe().limit(NORMAL_SIZE);
    }

    protected static Pipe<String> infiniteRandomStrPipe() {
        Random random = new Random();
        return generate((Supplier<? extends String>) () -> {
            char[] chars = new char[random.nextInt(10)];
            for (int i = 0; i < chars.length; i++) {
                switch (random.nextInt(3)) {
                    case 0:
                        chars[i] = (char) ('0' + random.nextInt(10));
                        break;
                    case 1:
                        chars[i] = (char) ('A' + random.nextInt(26));
                        break;
                    default:
                        chars[i] = (char) ('a' + random.nextInt(26));
                        break;
                }
            }
            return new String(chars);
        });
    }

    protected static List<Integer> genIntegerList() {
        return infiniteIntegerPipe().limit(NORMAL_SIZE).toList();
    }

    protected static List<String> genRandomStrList() {
        return infiniteRandomStrPipe().limit(NORMAL_SIZE).toList();
    }

    protected static List<String> genIntegerStrList(int size) {
        return infiniteIntegerStrPipe().limit(NORMAL_SIZE).toList();
    }

    protected static List<String> genIntegerStrList() {
        return genIntegerStrList(NORMAL_SIZE);
    }

    protected static Pipe<Integer> oddIntegerWithNullsPipe() {
        return infiniteOddIntegerWithNullsPipe().limit(NORMAL_SIZE);
    }

    protected static Pipe<Integer> infiniteOddIntegerWithNullsPipe() {
        return infiniteIntegerPipe().map(value -> isOdd(value) ? null : value);
    }

    protected static Pipe<String> oddIntegerStrWithNullsPipe() {
        return infiniteOddIntegerStrWithNullsPipe().limit(NORMAL_SIZE);
    }

    protected static Pipe<String> infiniteOddIntegerStrWithNullsPipe() {
        return infiniteIntegerPipe().map(value -> isOdd(value) ? null : String.valueOf(value));
    }

    protected static List<String> genOddIntegerStrWithNullsList() {
        return infiniteOddIntegerStrWithNullsPipe().limit(NORMAL_SIZE).toList();
    }

    protected static <T> List<T> duplicateList(List<T> sample) {
        return list(sample).append(list(sample)).shuffle().toList();
    }

    protected static boolean isOdd(Number value) {
        return (value.intValue() & 1) == 1;
    }

    protected static boolean isEven(Number value) {
        return (value.intValue() & 1) == 0;
    }

    @SafeVarargs
    protected static <T> List<T> addAll(Collection<? extends T>... lists) {
        List<T> all = new ArrayList<>();
        for (Collection<? extends T> list : lists) {
            all.addAll(list);
        }
        return all;
    }

    protected <T> void assertEqualsWithType(T expected, T actual) {
        assertEquals(expected, actual);
        assertEquals((expected == null ? new Object() : expected).getClass(),
            (actual == null ? new Object() : actual).getClass());
    }

    protected <T> void assertEqualsWithType(Collection<T> expected, Collection<T> actual) {
        assertEquals(expected, actual);
        Iterator<T> expectedIterator = expected.iterator();
        Iterator<T> actualIterator = actual.iterator();
        while (expectedIterator.hasNext() && actualIterator.hasNext()) {
            assertEquals(expectedIterator.next().getClass(), actualIterator.next().getClass());
        }
    }

    protected static Pipe<UnComparableTestDouble> genUnComparablePipe() {
        return of(new UnComparableTestDouble(), new UnComparableTestDouble());
    }

    protected static <R, T> BiFunction<R, T, R> wrapToFunction(BiConsumer<R, T> consumer) {
        return (result, value) -> {
            consumer.accept(result, value);
            return result;
        };
    }

    protected static class UnComparableTestDouble {
        public UnComparableTestDouble() {}
    }

    protected static class ComparableTestDouble implements Comparable<ComparableTestDouble> {
        private final String value;

        private boolean compareToCalled = false;

        public ComparableTestDouble(String value) {
            this.value = value;
        }

        public boolean isCompareToCalled() {
            return compareToCalled;
        }

        public void reset() {
            compareToCalled = false;
        }

        @Override
        public int compareTo(@NotNull ComparableTestDouble o) {
            compareToCalled = true;
            return value.length() - o.value.length();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ComparableTestDouble that = (ComparableTestDouble) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    protected static class RecordedCounter<T> {
        private final Map<T, Integer> ordered = new HashMap<>();

        private int count = 0;

        public RecordedCounter() {
        }

        public int getOrder(T value) {
            return ordered.computeIfAbsent(value, ignored -> count++);
        }
    }
}

