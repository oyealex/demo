package com.oyealex.pipe.basis;

import java.util.function.Function;

/**
 * MapOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class MapOp<IN, OUT> extends ChainedOp<IN, OUT> {
    private final Function<? super IN, ? extends OUT> mapper;

    MapOp(Op<OUT> nextOp, Function<? super IN, ? extends OUT> mapper) {
        super(nextOp);
        this.mapper = mapper;
    }

    @Override
    public void accept(IN in) {
        nextOp.accept(mapper.apply(in));
    }
}
