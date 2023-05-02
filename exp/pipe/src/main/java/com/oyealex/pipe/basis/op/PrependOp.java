package com.oyealex.pipe.basis.op;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * PrependOp
 *
 * @author oyealex
 * @since 2023-05-03
 */
class PrependOp<IN> extends ChainedOp<IN, IN> {
    private final Iterator<? extends IN> iterator;

    private List<IN> cached;

    PrependOp(Op<IN> op, Iterator<? extends IN> iterator) {
        super(op);
        this.iterator = iterator;
    }

    @Override
    public void begin(long size) {
        cached = new ArrayList<>();
        nextOp.begin(size);
    }

    @Override
    public void accept(IN in) {
        cached.add(in);
    }

    @Override
    public void end() {
        iterator.forEachRemaining(nextOp);
        cached.forEach(nextOp);
        nextOp.end();
    }
}
