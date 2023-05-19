package com.oyealex.pipe.basis;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.oyealex.pipe.flag.PipeFlag.EMPTY;
import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;

/**
 * TakeOrDropLastOp
 *
 * @author oyealex
 * @since 2023-05-19
 */
class TakeOrDropLastOp<T> extends RefPipe<T, T> {
    private final boolean isTake;

    private final int count;

    TakeOrDropLastOp(RefPipe<?, ? extends T> prePipe, boolean isTake, int count) {
        // 如果是丢弃最后N个，则在丢弃数量满足后可以短路
        super(prePipe, NOT_SIZED | (isTake ? EMPTY : IS_SHORT_CIRCUIT));
        this.isTake = isTake;
        this.count = count;
    }

    @Override
    protected Op<T> wrapOp(Op<T> nextOp) {
        return new ChainedOp<>(nextOp) {
            private long countBeforeLast;

            private ArrayList<T> buf;

            private int startIndex;

            private Consumer<T> acceptAction;

            private Runnable endAction;

            private Supplier<Boolean> shortCircuitJudge;

            @Override
            public void begin(long size) {
                if (size < 0) {
                    buf = new ArrayList<>(count);
                    startIndex = 0;
                    acceptAction = this::acceptOnUnsized;
                    shortCircuitJudge = nextOp::canShortCircuit;
                    endAction = this::endOnUnsized;
                } else {
                    countBeforeLast = Math.max(0, size - count);
                    nextOp.begin(isTake ? Math.min(size, count) : countBeforeLast);
                    acceptAction = this::acceptOnSized;
                    shortCircuitJudge = this::canShortCircuitOnSized;
                    endAction = () -> {};
                }
            }

            @Override
            public void accept(T value) {
                acceptAction.accept(value);
            }

            @Override
            public void end() {
                endAction.run();
                nextOp.end();
            }

            @Override
            public boolean canShortCircuit() {
                return shortCircuitJudge.get();
            }

            private void acceptOnSized(T value) {
                if (isTake) {
                    if (countBeforeLast <= 0) {
                        nextOp.accept(value);
                    } else {
                        countBeforeLast--;
                    }
                } else if (countBeforeLast-- > 0) {
                    nextOp.accept(value);
                }
            }

            private boolean canShortCircuitOnSized() {
                return (!isTake && countBeforeLast <= 0) || nextOp.canShortCircuit();
            }

            private void acceptOnUnsized(T value) {
                if (buf.size() < count) {
                    buf.add(value);
                } else {
                    if (!isTake) {
                        nextOp.accept(buf.get(startIndex));
                    }
                    buf.set(startIndex, value);
                    if (++startIndex >= count) {
                        startIndex -= count;
                    }
                }
            }

            private void endOnUnsized() {
                ArrayList<T> data = buf;
                buf = null;
                if (isTake) {
                    for (int i = startIndex; i < data.size(); i++) {
                        if (nextOp.canShortCircuit()) {
                            return;
                        }
                        nextOp.accept(data.get(i));
                    }
                    for (int i = 0; i < startIndex; i++) {
                        if (nextOp.canShortCircuit()) {
                            return;
                        }
                        nextOp.accept(data.get(i));
                    }
                }
            }
        };
    }
}
