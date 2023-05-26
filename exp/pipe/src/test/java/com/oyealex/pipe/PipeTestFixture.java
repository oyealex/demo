package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import com.oyealex.pipe.basis.api.Pipe;

import java.util.List;
import java.util.Random;

/**
 * PipeTestBase
 *
 * @author oyealex
 * @since 2023-04-28
 */
public abstract class PipeTestFixture {
    protected static final String SOME_STR = "SOME_STR";

    protected static final String[] ELEMENTS
        = "these are unit tests for pipe filter api, and these unit tests can test if the pipe api work rightly".split(
        "[ ,]");

    protected static final String[] OTHER_ELEMENTS
        = "this is some strings who are ready to prepend or append into pipes".split("[ ,]");

    private static final int NORMAL_SIZE = 10;

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

    protected static List<String> generateRandStrList() {
        return infiniteRandomStrPipe().limit(NORMAL_SIZE).toList();
    }

    protected static List<String> generateIntegerStrList() {
        return infiniteIntegerStrPipe().limit(NORMAL_SIZE).toList();
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
}
