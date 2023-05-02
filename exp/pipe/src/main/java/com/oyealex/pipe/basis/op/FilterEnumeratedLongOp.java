package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.functional.LongBiPredicate;

/**
 * FilterEnumeratedLongOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class FilterEnumeratedLongOp<IN> extends ChainedOp<IN, IN> {
    private final LongBiPredicate<? super IN> predicate;

    private long index = 0L;

    FilterEnumeratedLongOp(Op<IN> nextOp, LongBiPredicate<? super IN> predicate) {
        super(nextOp);
        this.predicate = predicate;
    }

    @Override
    public void accept(IN in) {
        if (predicate.test(index++, in)) {
            nextOp.accept(in);
        }
    }
}
