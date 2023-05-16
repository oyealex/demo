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
        = "this is some strings who are ready to prepend or append into pipes".split(
        "[ ,]");


    protected static Pipe<String> evenPipe() {
        return Pipes.iterate(0, i -> i + 2).map(String::valueOf);
    }

    protected static Pipe<String> oddPipe() {
        return Pipes.iterate(1, i -> i + 2).map(String::valueOf);
    }
}
