package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiFunction;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.oyealex.pipe.flag.PipeFlag.IS_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;

/**
 * DistinctStage
 *
 * @author oyealex
 * @since 2023-05-10
 */
abstract class DistinctOp<T> extends RefPipe<T, T> {
    protected DistinctOp(RefPipe<?, ? extends T> prePipe, int extraFlag) {
        super(prePipe, extraFlag | NOT_SIZED);
    }

    static class NaturalSorted<T> extends DistinctOp<T> {
        NaturalSorted(RefPipe<?, ? extends T> prePipe) {
            super(prePipe, IS_DISTINCT);
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp<>(nextOp) {
                private T last;

                private boolean nullExisted;

                @Override
                public void begin(long size) {
                    last = null;
                    nullExisted = false;
                    nextOp.begin(-1);
                }

                @Override
                public void end() {
                    last = null;
                    nullExisted = false;
                    nextOp.end();
                }

                @Override
                public void accept(T var) {
                    if (var == null) {
                        if (!nullExisted) {
                            nullExisted = true;
                            nextOp.accept(null);
                        }
                    } else if (!var.equals(last)) {
                        nextOp.accept(var);
                    }
                    last = var;
                }
            };
        }
    }

    static class Normal<T> extends DistinctOp<T> {
        Normal(RefPipe<?, ? extends T> prePipe) {
            super(prePipe, IS_DISTINCT);
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new SetBasedDistinctOp<T, T>(nextOp) {
                @Override
                protected T mapToKey(T var) {
                    return var;
                }
            };
        }
    }

    static class NormalKeyed<T, K> extends DistinctOp<T> {
        private final Function<? super T, ? extends K> mapper;

        NormalKeyed(RefPipe<?, ? extends T> prePipe, Function<? super T, ? extends K> mapper) {
            super(prePipe, NOT_DISTINCT); // 根据key去重，不是自然序去重
            this.mapper = mapper;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new SetBasedDistinctOp<T, K>(nextOp) {
                @Override
                protected K mapToKey(T var) {
                    return mapper.apply(var);
                }
            };
        }
    }

    static class OrderlyKeyed<T, K> extends DistinctOp<T> {
        private final LongBiFunction<? super T, ? extends K> mapper;

        OrderlyKeyed(RefPipe<?, ? extends T> prePipe, LongBiFunction<? super T, ? extends K> mapper) {
            super(prePipe, NOT_DISTINCT); // 根据key去重，不是自然序去重
            this.mapper = mapper;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new SetBasedDistinctOp<T, K>(nextOp) {
                private long index = 0L;

                @Override
                protected K mapToKey(T var) {
                    return mapper.apply(index++, var);
                }
            };
        }
    }

    private abstract static class SetBasedDistinctOp<T, K> extends ChainedOp<T, T> {
        private Set<K> seen;

        public SetBasedDistinctOp(Op<T> nextOp) {super(nextOp);}

        @Override
        public void begin(long size) {
            seen = new HashSet<>();
            nextOp.begin(-1);
        }

        @Override
        public void end() {
            seen = null;
            nextOp.end();
        }

        @Override
        public void accept(T var) {
            if (seen.add(mapToKey(var))) {
                nextOp.accept(var);
            }
        }

        protected abstract K mapToKey(T var);
    }
}
