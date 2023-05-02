package com.oyealex.pipe.basis.op;

import java.util.function.Predicate;

/**
 * 过滤操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
class FilterOp<IN> extends ChainedOp<IN, IN> {
    private final Predicate<? super IN> predicate;

    FilterOp(Op<IN> nextOp, Predicate<? super IN> predicate) {
        super(nextOp);
        this.predicate = predicate;
    }

    @Override
    public void begin(long size) {
        nextOp.begin(-1);
    }

    @Override
    public void accept(IN in) {
        if (predicate.test(in)) {
            nextOp.accept(in);
        }
    }
}
