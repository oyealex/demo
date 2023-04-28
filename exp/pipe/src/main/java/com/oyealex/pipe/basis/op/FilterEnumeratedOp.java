package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.functional.IntBiPredicate;

/**
 * 支持
 *
 * @author oyealex
 * @since 2023-04-28
 */
class FilterEnumeratedOp<IN> extends ChainedOp<IN, IN> {
    private final IntBiPredicate<? super IN> predicate;

    private int index;

    FilterEnumeratedOp(Op<IN> op, IntBiPredicate<? super IN> predicate) {
        super(op);
        this.predicate = predicate;
        index = 0;
    }

    @Override
    public void accept(IN in) {
        if (predicate.test(index++, in)) {
            next.accept(in);
        }
    }
}
