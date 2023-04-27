package com.oyealex.pipe;

import com.oyealex.pipe.functional.IntBiPredicate;
import com.oyealex.pipe.functional.LongBiPredicate;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * 抽象流水线
 *
 * @author oyealex
 * @since 2023-03-04
 */
abstract class AbstractPipe<IN, OUT> implements Pipe<OUT> {
    /** 流水线的源节点，不会为null */
    private final AbstractPipe<?, ?> sourcePipe;

    /** 此节点的前置节点，当且仅当此节点为源节点时为null */
    private final AbstractPipe<?, ?> prePipe;

    private Iterator<?> sourceIterator;

    /** 流水线关闭时执行的动作 */
    private Runnable closeAction;

    AbstractPipe(Iterator<?> sourceIterator) {
        this.sourcePipe = this;
        this.prePipe = null;
        this.sourceIterator = sourceIterator;
    }

    AbstractPipe(AbstractPipe<?, ?> prePipe) {
        this.sourcePipe = prePipe.sourcePipe;
        this.prePipe = prePipe;
    }

    abstract Op<IN> wrapOp(Op<OUT> op);

    @SuppressWarnings("unchecked")
    final <R> R evaluate(TerminalOp<OUT, R> terminalOp) {
        Op<IN> op = wrapAllOp(terminalOp);
        op.begin(-1);
        Iterator<?> iterator = sourcePipe.sourceIterator;
        while (iterator.hasNext()) {
            Object value = iterator.next();
            op.accept((IN) value);
        }
        op.end();
        return terminalOp.get();
    }

    @SuppressWarnings("unchecked")
    private Op<IN> wrapAllOp(Op<OUT> op) {
        for (@SuppressWarnings("rawtypes") AbstractPipe pipe = this; pipe.prePipe != null; pipe = pipe.prePipe) {
            op = pipe.wrapOp(op);
        }
        return (Op<IN>) op;
    }

    @Override
    public Pipe<OUT> filter(Predicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new AbstractPipe<OUT, OUT>(this) {
            @Override
            Op<OUT> wrapOp(Op<OUT> op) {
                return new Op.ChainedOp<>(op) {
                    @Override
                    public void begin(long size) {
                        next.begin(-1);
                    }

                    @Override
                    public void accept(OUT out) {
                        if (predicate.test(out)) {
                            next.accept(out);
                        }
                    }
                };
            }
        };
    }

    @Override
    public Pipe<OUT> filterEnumerated(IntBiPredicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new AbstractPipe<OUT, OUT>(this) {
            @Override
            Op<OUT> wrapOp(Op<OUT> op) {
                return new Op.ChainedOp<>(op) {
                    private int index = 0;

                    @Override
                    public void accept(OUT out) {
                        if (predicate.test(index++, out)) {
                            next.accept(out);
                        }
                    }
                };
            }
        };
    }

    @Override
    public Pipe<OUT> filterEnumeratedLong(LongBiPredicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new AbstractPipe<OUT, OUT>(this) {
            @Override
            Op<OUT> wrapOp(Op<OUT> op) {
                return new Op.ChainedOp<>(op) {
                    private long index = 0L;

                    @Override
                    public void accept(OUT out) {
                        if (predicate.test(index++, out)) {
                            next.accept(out);
                        }
                    }
                };
            }
        };
    }

    @Override
    public <R> Pipe<R> map(Function<? super OUT, ? extends R> mapper) {
        return new AbstractPipe<OUT, R>(this) {
            @Override
            Op<OUT> wrapOp(Op<R> op) {
                return new Op.ChainedOp<>(op) {
                    @Override
                    public void accept(OUT out) {
                        next.accept(mapper.apply(out));
                    }
                };
            }
        };
    }

    @Override
    public Pipe<OUT> limit(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("limit size cannot be negative, size: " + size);
        }
        if (size == 0) {
            return Pipes.empty();
        }
        return new AbstractPipe<OUT, OUT>(this) {
            @Override
            Op<OUT> wrapOp(Op<OUT> op) {
                return new Op.ChainedOp<>(op) {
                    private long limited = 0L;
                    @Override
                    public void accept(OUT out) {
                        if (limited < size) {
                            limited++;
                        }else {
                            next.accept(out);
                        }
                    }
                };
            }
        };
    }

    @Override
    public void forEach(Consumer<? super OUT> action) {
        requireNonNull(action);
        evaluate(new TerminalOp<OUT, Void>() {
            @Override
            public void accept(OUT out) {
                action.accept(out);
            }

            @Override
            public Void get() {
                return null;
            }
        });
    }

    @Override
    public long count() {
        return evaluate(new TerminalOp<>() {
            private long count;

            @Override
            public void accept(OUT out) {
                count++;
            }

            @Override
            public Long get() {
                return count;
            }
        });
    }

    @Override
    public List<OUT> toList() {
        return evaluate(CollectorUtil.makeToListTerminalOp());
    }

    @Override
    public Pipe<OUT> onClose(Runnable closeAction) {
        requireNonNull(closeAction);
        if (sourcePipe.closeAction == null) {
            sourcePipe.closeAction = closeAction;
        } else {
            sourcePipe.closeAction = composeCloseAction(sourcePipe.closeAction, closeAction);
        }
        return this;
    }

    @Override
    public void close() {
        Runnable action = sourcePipe.closeAction;
        if (action != null) {
            sourcePipe.closeAction = null;
            action.run();
        }
    }

    private static Runnable composeCloseAction(Runnable action, Runnable anotherAction) {
        return () -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                try {
                    anotherAction.run();
                } catch (Throwable anotherThrowable) {
                    try {
                        throwable.addSuppressed(anotherThrowable);
                    } catch (Throwable ignore) {}
                }
                throw throwable;
            }
            anotherAction.run();
        };
    }

    static class PipeHead<IN, OUT> extends AbstractPipe<IN, OUT> {
        public PipeHead(Iterator<?> sourceIterator) {
            super(sourceIterator);
        }

        @Override
        Op<IN> wrapOp(Op<OUT> op) {
            throw new UnsupportedOperationException();
        }
    }
}
