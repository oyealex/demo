package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.functional.LongBiFunction;

/**
 * MapOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class MapEnumeratedLongOp<IN, OUT> extends ChainedOp<IN, OUT> {
    private final LongBiFunction<? super IN, ? extends OUT> mapper;

    private long index = 0L;

    MapEnumeratedLongOp(Op<OUT> op, LongBiFunction<? super IN, ? extends OUT> mapper) {
        super(op);
        this.mapper = mapper;
    }

    @Override
    public void accept(IN in) {
        next.accept(mapper.apply(index++, in));
    }
}
