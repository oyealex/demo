package com.oyealex.pipe.basis;

import java.util.function.Predicate;

import static com.oyealex.pipe.flag.PipeFlag.EMPTY;
import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;

/**
 * LimitOp
 *
 * @author oyealex
 * @since 2023-04-28
 */
abstract class SliceOp<IN> extends RefPipe<IN, IN> { // TODO 2023-05-31 00:11 已经达到限制条件后不再执行多余的判断方法
    protected final long skip;

    protected final long limit;

    protected SliceOp(RefPipe<?, ? extends IN> prePipe, long skip, long limit) {
        super(prePipe, NOT_SIZED | (limit != Long.MAX_VALUE ? IS_SHORT_CIRCUIT : EMPTY));
        this.skip = skip;
        this.limit = limit;
    }

    static class Normal<IN> extends SliceOp<IN> {
        Normal(RefPipe<?, ? extends IN> prePipe, long skip, long limit) {
            super(prePipe, skip, limit);
        }

        @Override
        protected Op<IN> wrapOp(Op<IN> nextOp) {
            return new InternalOp<IN>(nextOp, limit) {
                @Override
                public void begin(long size) {
                    if (size <= 0) {
                        nextOp.begin(size);
                    } else {
                        nextOp.begin(Math.min(Math.max(0, size - skip), limit));
                    }
                }

                @Override
                public void accept(IN in) {
                    if (skipped < skip) {
                        skipped++;
                    } else if (limited < limit) {
                        limited++;
                        nextOp.accept(in);
                    }
                }
            };
        }
    }

    static class PredicatedSlice<IN> extends SliceOp<IN> {
        protected final Predicate<? super IN> predicate;

        PredicatedSlice(RefPipe<?, ? extends IN> prePipe, long skip, long limit, Predicate<? super IN> predicate) {
            super(prePipe, skip, limit);
            this.predicate = predicate;
        }

        @Override
        protected Op<IN> wrapOp(Op<IN> nextOp) {
            return new InternalOp<IN>(nextOp, limit) {
                @Override
                public void begin(long size) {
                    nextOp.begin(-1);
                }

                @Override
                public void accept(IN in) {
                    if (skipped < skip) {
                        if (predicate.test(in)) {
                            skipped++;
                        }
                    } else if (limited < limit) {
                        if (predicate.test(in)) {
                            limited++;
                        }
                        nextOp.accept(in);
                    }
                }
            };
        }
    }

    static class PredicatedSkip<IN> extends PredicatedSlice<IN> {
        PredicatedSkip(RefPipe<?, ? extends IN> prePipe, long skip, Predicate<? super IN> predicate) {
            super(prePipe, skip, Long.MAX_VALUE, predicate);
        }

        @Override
        protected Op<IN> wrapOp(Op<IN> nextOp) {
            return new InternalOp<IN>(nextOp, limit) {
                @Override
                public void begin(long size) {
                    nextOp.begin(-1);
                }

                @Override
                public void accept(IN in) {
                    if (skipped < skip) {
                        if (predicate.test(in)) {
                            skipped++;
                        }
                    } else {
                        nextOp.accept(in);
                    }
                }
            };
        }
    }

    static class PredicatedLimit<IN> extends PredicatedSlice<IN> {
        PredicatedLimit(RefPipe<?, ? extends IN> prePipe, long limit, Predicate<? super IN> predicate) {
            super(prePipe, 0, limit, predicate);
        }

        @Override
        protected Op<IN> wrapOp(Op<IN> nextOp) {
            return new InternalOp<IN>(nextOp, limit) {
                @Override
                public void begin(long size) {
                    nextOp.begin(-1);
                }

                @Override
                public void accept(IN in) {
                    if (limited < limit) {
                        if (predicate.test(in)) {
                            limited++;
                        }
                        nextOp.accept(in);
                    }
                }
            };
        }
    }

    private static abstract class InternalOp<IN> extends ChainedOp<IN, IN> {
        protected final long limit;

        protected long skipped = 0L;

        protected long limited = 0L;

        protected InternalOp(Op<? super IN> nextOp, long limit) {
            super(nextOp);
            this.limit = limit;
        }

        @Override
        public boolean canShortCircuit() {
            return limited >= limit || nextOp.canShortCircuit();
        }
    }
}
