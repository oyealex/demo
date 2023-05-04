package com.oyealex.pipe.basis.op;

import java.util.Comparator;
import java.util.Optional;

/**
 * ForEachOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
class MinMaxOp<IN> implements TerminalOp<IN, Optional<IN>> {
    private IN minOrMax;

    private final Comparator<? super IN> comparator;

    MinMaxOp(boolean requireMin, Comparator<? super IN> comparator) {
        // TODO 2023-05-03 02:27
        this.comparator = comparator;
    }

    @Override
    public void accept(IN in) {
    }

    @Override
    public Optional<IN> get() {
        return Optional.empty();
    }
}
