package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.Pipe;

import java.util.function.Function;

/**
 * @param <R>
 * @param <IN>
 */
class FlatMapOp<IN, R> extends ChainedOp<IN, R> {
    private final Function<? super IN, ? extends Pipe<? extends R>> mapper;

    FlatMapOp(Op<R> nextOp, Function<? super IN, ? extends Pipe<? extends R>> mapper) {
        super(nextOp);
        this.mapper = mapper;
    }

    @Override
    public void accept(IN out) {
        try (Pipe<? extends R> pipe = mapper.apply(out)) {
            if (pipe != null) {
                pipe.forEach(nextOp);
            }
        }
    }
}
