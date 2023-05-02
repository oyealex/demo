package com.oyealex.pipe.basis.op;

import com.oyealex.pipe.basis.functional.IntBiConsumer;

/**
 * ForEachOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class ForEachEnumeratedOp<IN> implements TerminalOp<IN, Void> {
    private final IntBiConsumer<? super IN> action;

    private int index = 0;

    ForEachEnumeratedOp(IntBiConsumer<? super IN> action) {this.action = action;}

    @Override
    public void accept(IN in) {
        action.accept(index++, in);
    }

    @Override
    public Void get() {
        return null;
    }
}
