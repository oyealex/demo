package com.oyealex.pipe.basis.op;

/**
 * CountOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class CountOp<IN> implements TerminalOp<IN, Long> {
    private long count = 0L;

    @Override
    public void accept(IN in) {
        count++;
    }

    @Override
    public Long get() {
        return count;
    }
}
