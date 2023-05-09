package com.oyealex.pipe.basis;

import java.util.function.Consumer;

/**
 * 过滤操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
class PeekOp<IN> extends ChainedOp<IN, IN> {
    private final Consumer<? super IN> consumer;

    PeekOp(Op<IN> nextOp, Consumer<? super IN> consumer) {
        super(nextOp);
        this.consumer = consumer;
    }

    @Override
    public void accept(IN in) {
        consumer.accept(in);
        nextOp.accept(in);
    }
}
