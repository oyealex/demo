package com.oyealex.pipe.basis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;

/**
 * SelectedFirstOrLastOp
 *
 * @author oyealex
 * @since 2023-05-31
 */
abstract class SelectToFirstOrLastOp<T> extends RefPipe<T, T> {
    protected final Predicate<? super T> select;

    protected SelectToFirstOrLastOp(RefPipe<?, ? extends T> prePipe, Predicate<? super T> select) {
        super(prePipe, NOT_SIZED | NOT_REVERSED_SORTED);
        this.select = select;
    }

    static class ToFirst<T> extends SelectToFirstOrLastOp<T> {
        ToFirst(RefPipe<?, ? extends T> prePipe, Predicate<? super T> select) {
            super(prePipe, select);
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp.ToList<T, T>(nextOp) {
                private List<T> selected;

                @Override
                public void begin(long size) {
                    super.begin(size);
                    selected = new ArrayList<>();
                }

                @Override
                public void accept(T value) {
                    if (select.test(value)) {
                        selected.add(value);
                    } else {
                        elements.add(value);
                    }
                }

                @Override
                public void end() {
                    nextOp.begin(selected.size() + elements.size());
                    if (isShortCircuitRequested) {
                        for (T value : selected) {
                            if (nextOp.canShortCircuit()) {
                                break;
                            }
                            nextOp.accept(value);
                        }
                        for (T value : elements) {
                            if (nextOp.canShortCircuit()) {
                                break;
                            }
                            nextOp.accept(value);
                        }
                    } else {
                        selected.forEach(nextOp);
                        elements.forEach(nextOp);
                    }
                    nextOp.end();
                    selected = null;
                    elements = null;
                }
            };
        }
    }

    static class ToLast<T> extends SelectToFirstOrLastOp<T> {
        ToLast(RefPipe<?, ? extends T> prePipe, Predicate<? super T> select) {
            super(prePipe, select);
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp.ShortCircuitRecorded<T, T>(nextOp) {
                private List<T> selected;

                @Override
                public void begin(long size) {
                    nextOp.begin(size);
                    selected = new ArrayList<>();
                }

                @Override
                public void accept(T value) {
                    if (select.test(value)) {
                        selected.add(value);
                    } else {
                        nextOp.accept(value);
                    }
                }

                @Override
                public void end() {
                    if (isShortCircuitRequested) {
                        for (T value : selected) {
                            if (nextOp.canShortCircuit()) {
                                break;
                            }
                            nextOp.accept(value);
                        }
                    } else {
                        selected.forEach(nextOp);
                    }
                    selected = null;
                    nextOp.end();
                }
            };
        }
    }
}
