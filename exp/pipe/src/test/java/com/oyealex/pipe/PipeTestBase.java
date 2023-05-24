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
public abstract class PipeTestBase {
    protected static final String[] ELEMENTS
        = "these are unit tests for pipe filter api, and these unit tests can test if the pipe api work rightly".split(
        "[ ,]");

    protected static final String[] OTHER_ELEMENTS
        = "this is some strings who are ready to prepend or append into pipes".split("[ ,]");

    protected static Pipe<Integer> seqIntegerPipe() {
        return Pipes.iterate(0, i -> i + 1);
    }

    private static Pipe<Integer> evenIntegerPipe() {
        return Pipes.iterate(0, i -> i + 2);
    }

    private static Pipe<Integer> oddIntegerPipe() {
        return Pipes.iterate(1, i -> i + 2);
    }

    protected static Pipe<String> seqStrPipe() {
        return seqIntegerPipe().map(String::valueOf);
    }

    protected static Pipe<String> evenStrPipe() {
        return evenIntegerPipe().map(String::valueOf);
    }

    protected static Pipe<String> oddStrPipe() {
        return oddIntegerPipe().map(String::valueOf);
    }

    protected static Pipe<String> prefixedSeqStrPipe(String prefix) {
        return seqStrPipe().map(v -> prefix + v);
    }

    protected static Pipe<String> generateRandStr() {
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

    protected static List<String> generateRandStrList() {
        return generateRandStr().limit(10).toList();
    }
}
