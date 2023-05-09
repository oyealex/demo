package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiConsumer;

/**
 * 过滤操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
class PeekOrderlyOp<IN> extends ChainedOp<IN, IN> {
    private final LongBiConsumer<? super IN> consumer;

    private long index = 0L;

    PeekOrderlyOp(Op<IN> nextOp, LongBiConsumer<? super IN> consumer) {
        super(nextOp);
        this.consumer = consumer;
    }

    @Override
    public void accept(IN in) {
        consumer.accept(index++, in);
        nextOp.accept(in);
    }
}
