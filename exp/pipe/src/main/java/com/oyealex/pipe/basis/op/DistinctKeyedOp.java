package com.oyealex.pipe.basis.op;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * 去重操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
class DistinctKeyedOp<IN, R> extends ChainedOp<IN, IN> {
    private final Function<? super IN, ? extends R> mapper;

    private Set<R> seen;

    DistinctKeyedOp(Op<IN> op, Function<? super IN, ? extends R> mapper) {
        super(op);
        this.mapper = mapper;
    }

    @Override
    public void begin(long size) {
        seen = new HashSet<>();
        next.begin(-1);
    }

    @Override
    public void accept(IN in) {
        if (seen.add(mapper.apply(in))) {
            next.accept(in);
        }
    }
}
