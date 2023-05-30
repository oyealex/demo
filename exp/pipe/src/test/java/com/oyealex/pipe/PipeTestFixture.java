package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import com.oyealex.pipe.basis.api.Pipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * PipeTestBase
 *
 * @author oyealex
 * @since 2023-04-28
 */
public abstract class PipeTestFixture {
    protected static final String SOME_STR = "SOME_STR";

    protected static final int NORMAL_SIZE = 20;

    protected static Pipe<Integer> infiniteIntegerPipe() {
        return Pipes.iterate(0, i -> i + 1);
    }

    private static Pipe<Integer> infiniteEvenIntegerPipe() {
        return Pipes.iterate(0, i -> i + 2);
    }

    private static Pipe<Integer> infiniteOddIntegerPipe() {
        return Pipes.iterate(1, i -> i + 2);
    }

    protected static Pipe<String> infiniteIntegerStrPipe() {
        return infiniteIntegerPipe().map(String::valueOf);
    }

    protected static Pipe<String> infiniteEvenIntegerStrPipe() {
        return infiniteEvenIntegerPipe().map(String::valueOf);
    }

    protected static Pipe<String> infiniteOddIntegerStrPipe() {
        return infiniteOddIntegerPipe().map(String::valueOf);
    }

    protected static Pipe<String> prefixedIntegerStrPipe(String prefix) {
        return infiniteIntegerStrPipe().map(v -> prefix + v);
    }

    protected static Pipe<String> infiniteRandomStrPipe() {
        Random random = new Random();
        return Pipes.generate(() -> {
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

    protected static List<Integer> generateIntegerList() {
        return infiniteIntegerPipe().limit(NORMAL_SIZE).toList();
    }

    protected static List<String> generateRandomStrList() {
        return infiniteRandomStrPipe().limit(NORMAL_SIZE).toList();
    }

    protected static List<String> generateIntegerStrList(int size) {
        return infiniteIntegerStrPipe().limit(NORMAL_SIZE).toList();
    }

    protected static List<String> generateIntegerStrList() {
        return generateIntegerStrList(NORMAL_SIZE);
    }

    protected static Pipe<Integer> infiniteOddIntegerWithNullsPipe() {
        return infiniteIntegerPipe().map(value -> (value & 1) == 1 ? null : value);
    }

    protected static Pipe<String> infiniteOddIntegerStrWithNullsPipe() {
        return infiniteIntegerPipe().map(value -> (value & 1) == 1 ? null : String.valueOf(value));
    }

    protected static List<String> generateOddIntegerStrWithNullsList() {
        return infiniteOddIntegerStrWithNullsPipe().limit(NORMAL_SIZE).toList();
    }

    protected static <T> List<T> duplicateList(List<T> sample) {
        return Pipes.list(sample).append(Pipes.list(sample)).shuffle().toList();
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
}