package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.Pipe;

import java.util.function.Function;

class FlatMapXOp<R, OUT> extends ChainedOp<OUT, R> {
    private final Function<? super OUT, ? extends Pipe<? extends R>> mapper;

    public FlatMapXOp(Op<R> op, Function<? super OUT, ? extends Pipe<? extends R>> mapper) {
        super(op);
        this.mapper = mapper;
    }

    @Override
    public void accept(OUT out) {
        try (Pipe<? extends R> pipe = mapper.apply(out)) {
            if (pipe != null) {
                pipe.forEach(next);
            }
        }
    }
}
