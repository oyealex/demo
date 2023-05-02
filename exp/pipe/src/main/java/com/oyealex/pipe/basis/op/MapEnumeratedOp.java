package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.functional.IntBiFunction;

/**
 * MapOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class MapEnumeratedOp<IN, OUT> extends ChainedOp<IN, OUT> {
    private final IntBiFunction<? super IN, ? extends OUT> mapper;

    private int index = 0;

    MapEnumeratedOp(Op<OUT> nextOp, IntBiFunction<? super IN, ? extends OUT> mapper) {
        super(nextOp);
        this.mapper = mapper;
    }

    @Override
    public void accept(IN in) {
        nextOp.accept(mapper.apply(index++, in));
    }
}
