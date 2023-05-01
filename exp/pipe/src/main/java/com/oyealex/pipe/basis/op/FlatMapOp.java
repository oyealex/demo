package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.Pipe;

import java.util.function.Function;

/**
 * MapOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class FlatMapOp<IN, OUT> extends ChainedOp<IN, OUT> {
    private final Function<? super IN, ? extends Pipe<? extends OUT>> mapper;

    FlatMapOp(Op<? super OUT> op, Function<? super IN, ? extends Pipe<? extends OUT>> mapper) {
        super(op);
        this.mapper = mapper;
    }

    @Override
    public void accept(IN in) {
        try (Pipe<? extends OUT> pipe = mapper.apply(in)) {
            if (pipe != null) {
                pipe.forEach(next);
            }
        }
    }
}
