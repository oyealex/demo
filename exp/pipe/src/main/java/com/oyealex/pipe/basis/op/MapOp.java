package com.oyealex.pipe.basis.op;

import java.util.function.Function;

/**
 * MapOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class MapOp<IN, OUT> extends ChainedOp<IN, OUT> {
    private final Function<? super IN, ? extends OUT> mapper;

    MapOp(Op<OUT> op, Function<? super IN, ? extends OUT> mapper) {
        super(op);
        this.mapper = mapper;
    }

    @Override
    public void accept(IN in) {
        next.accept(mapper.apply(in));
    }
}
