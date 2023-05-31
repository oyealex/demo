package com.oyealex.pipe.basis;

import com.oyealex.pipe.utils.MiscUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 支持链接的操作
 *
 * @author oyealex
 * @since 2023-04-28
 */
abstract class ChainedOp<IN, OUT> implements Op<IN> { // OPT 2023-05-31 22:53 所有需要搜集全部元素才能继续的操作在遇到无限流时立即抛出异常
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

    static abstract class Orderly<IN, OUT> extends ChainedOp<IN, OUT> {
        protected long index = 0L;

        Orderly(Op<? super OUT> nextOp) {
            super(nextOp);
        }
    }

    static abstract class ShortCircuitRecorded<IN, OUT> extends ChainedOp<IN, OUT> {
        protected boolean isShortCircuitRequested = false;

        ShortCircuitRecorded(Op<? super OUT> nextOp) {
            super(nextOp);
        }

        @Override
        public boolean canShortCircuit() {
            isShortCircuitRequested = true;
            return super.canShortCircuit();
        }

        protected boolean shouldShortCircuit() {
            return isShortCircuitRequested && super.canShortCircuit();
        }
    }

    static abstract class NonShortCircuit<IN, OUT> extends ChainedOp<IN, OUT> {
        protected boolean isShortCircuitRequested = false;

        NonShortCircuit(Op<? super OUT> nextOp) {
            super(nextOp);
        }

        @Override
        public boolean canShortCircuit() {
            isShortCircuitRequested = true;
            return false;
        }
    }

    static abstract class ToList<IN, OUT> extends NonShortCircuit<IN, OUT> {
        protected List<IN> elements; // OPT 2023-05-19 01:31 进一步优化，使用支持多段数组的方式避免数组扩容拷贝

        ToList(Op<? super OUT> nextOp) {
            super(nextOp);
        }

        @Override
        public void begin(long size) {
            MiscUtil.checkArraySize(size);
            elements = size >= 0 ? new ArrayList<>((int) size) : new ArrayList<>();
        }

        @Override
        public void accept(IN value) {
            elements.add(value);
        }
    }

    /**
     * 数组中继操作，先把元素收集到列表中，执行一些特定操作后再继续传递元素到下游操作。
     *
     * @param <T> 元素类型
     */
    static abstract class ListRepeater<T> extends ToList<T, T> {
        ListRepeater(Op<? super T> nextOp) {
            super(nextOp);
        }

        @Override
        public void end() {
            beforeEnd();
            nextOp.begin(elements.size());
            if (isShortCircuitRequested) {
                for (T value : elements) {
                    if (nextOp.canShortCircuit()) {
                        break;
                    }
                    nextOp.accept(value);
                }
            } else {
                elements.forEach(nextOp);
            }
            nextOp.end();
            elements = null;
        }

        protected void beforeEnd() {
            // override
        }
    }
}
