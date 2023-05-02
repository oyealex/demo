package com.oyealex.pipe.basis.op;

import java.util.ArrayList;
import java.util.Comparator;

import static java.util.Objects.requireNonNullElseGet;

/**
 * SortedOp
 *
 * @author oyealex
 * @since 2023-05-03
 */
class SortOp<IN> extends ChainedOp<IN, IN> {
    private ArrayList<IN> list;

    private final Comparator<? super IN> comparator;

    @SuppressWarnings("unchecked")
    SortOp(Op<? super IN> nextOp, Comparator<? super IN> comparator) {
        super(nextOp);
        this.comparator = requireNonNullElseGet(comparator, () -> (Comparator<? super IN>) Comparator.naturalOrder());
    }

    @Override
    public void begin(long size) {
        list = new ArrayList<>();
        nextOp.begin(size);
    }

    @Override
    public void accept(IN in) {
        list.add(in);
    }

    @Override
    public void end() {
        list.sort(comparator);
        list.forEach(nextOp);
        nextOp.end();
        list = null;
    }
}
