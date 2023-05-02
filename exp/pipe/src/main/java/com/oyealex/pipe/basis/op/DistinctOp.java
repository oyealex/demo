package com.oyealex.pipe.basis.op;

import java.util.HashSet;
import java.util.Set;

/**
 * 去重操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
class DistinctOp<IN> extends ChainedOp<IN, IN> {
    private Set<IN> seen;

    DistinctOp(Op<IN> nextOp) {
        super(nextOp);
    }

    @Override
    public void begin(long size) {
        seen = new HashSet<>();
        nextOp.begin(-1);
    }

    @Override
    public void accept(IN in) {
        if (seen.add(in)) {
            nextOp.accept(in);
        }
    }
}
