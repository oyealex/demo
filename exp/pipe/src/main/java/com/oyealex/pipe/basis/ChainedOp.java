package com.oyealex.pipe.basis;

import com.oyealex.pipe.utils.CheckUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 支持链接的操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
abstract class ChainedOp<IN, OUT> implements Op<IN> {
    /** 下一个操作 */
    protected final Op<? super OUT> nextOp;

    public ChainedOp(Op<? super OUT> nextOp) {
        this.nextOp = Objects.requireNonNull(nextOp);
    }

    @Override
    public void begin(long size) {
        nextOp.begin(size);
    }

    @Override
    public void end() {
        nextOp.end();
    }

    @Override
    public boolean canShortCircuit() {
        return nextOp.canShortCircuit();
    }

    static abstract class OrderlyOp<IN, OUT> extends ChainedOp<IN, OUT> {
        protected long index = 0L;

        OrderlyOp(Op<? super OUT> nextOp) {
            super(nextOp);
        }
    }

    static abstract class NonShortCircuitOp<IN, OUT> extends ChainedOp<IN, OUT> {
        protected boolean isShortCircuitRequested = false;

        NonShortCircuitOp(Op<? super OUT> nextOp) {
            super(nextOp);
        }

        @Override
        public boolean canShortCircuit() {
            isShortCircuitRequested = true;
            return false;
        }
    }

    static abstract class CollectedOp<IN, OUT> extends NonShortCircuitOp<IN, OUT> {
        protected List<IN> elements;

        CollectedOp(Op<? super OUT> nextOp) {
            super(nextOp);
        }

        @Override
        public void begin(long size) {
            CheckUtil.checkArraySize(size);
            elements = size >= 0 ? new ArrayList<>((int) size) : new ArrayList<>();
        }

        @Override
        public void accept(IN var) {
            elements.add(var);
        }
    }
}
