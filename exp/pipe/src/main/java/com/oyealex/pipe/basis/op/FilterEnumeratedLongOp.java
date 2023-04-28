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

    private long index;

    FilterEnumeratedLongOp(Op<IN> op, LongBiPredicate<? super IN> predicate) {
        super(op);
        this.predicate = predicate;
        index = 0L;
    }

    @Override
    public void accept(IN in) {
        if (predicate.test(index++, in)) {
            next.accept(in);
        }
    }
}
