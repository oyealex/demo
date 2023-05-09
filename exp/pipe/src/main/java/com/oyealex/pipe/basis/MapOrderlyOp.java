package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiFunction;

/**
 * MapOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class MapOrderlyOp<IN, OUT> extends ChainedOp<IN, OUT> {
    private final LongBiFunction<? super IN, ? extends OUT> mapper;

    private long index = 0L;

    MapOrderlyOp(Op<OUT> nextOp, LongBiFunction<? super IN, ? extends OUT> mapper) {
        super(nextOp);
        this.mapper = mapper;
    }

    @Override
    public void accept(IN in) {
        nextOp.accept(mapper.apply(index++, in));
    }
}
