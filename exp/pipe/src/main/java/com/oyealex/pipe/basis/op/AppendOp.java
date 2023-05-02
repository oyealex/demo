package com.oyealex.pipe.basis.op;

import java.util.Iterator;

/**
 * AppendOp
 *
 * @author oyealex
 * @since 2023-05-03
 */
class AppendOp<IN> extends ChainedOp<IN, IN> {
    private final Iterator<? extends IN> iterator;

    AppendOp(Op<IN> op, Iterator<? extends IN> iterator) {
        super(op);
        this.iterator = iterator;
    }

    @Override
    public void accept(IN in) {
        nextOp.accept(in);
    }

    @Override
    public void end() {
        iterator.forEachRemaining(nextOp);
        nextOp.end();
    }
}
