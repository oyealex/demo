package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import com.oyealex.pipe.basis.api.Pipe;

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
}
