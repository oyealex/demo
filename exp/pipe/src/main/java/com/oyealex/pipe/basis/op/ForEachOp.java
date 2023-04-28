package com.oyealex.pipe.basis.op;

import java.util.function.Consumer;

/**
 * ForEachOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class ForEachOp<IN> implements TerminalOp<IN, Void> {
    private final Consumer<? super IN> action;

    ForEachOp(Consumer<? super IN> action) {this.action = action;}

    @Override
    public void accept(IN in) {
        action.accept(in);
    }

    @Override
    public Void get() {
        return null;
    }
}
