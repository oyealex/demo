package com.oyealex.pipe;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

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

    /** 此节点的后置节点，当且仅当此节点为末端节点时为null */
    private AbstractPipe<?, ?> nextPipe;

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
        prePipe.nextPipe = this;
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
        return new AbstractPipe<OUT, OUT>(this) {
            @Override
            Op<OUT> wrapOp(Op<OUT> op) {
                return new Op.ChainedOp<OUT, OUT>(op) {
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
    public <R> Pipe<R> map(Function<? super OUT, ? extends R> mapper) {
        return new AbstractPipe<OUT, R>(this) {
            @Override
            Op<OUT> wrapOp(Op<R> op) {
                return new Op.ChainedOp<OUT, R>(op) {
                    @Override
                    public void accept(OUT out) {
                        next.accept(mapper.apply(out));
                    }
                };
            }
        };
    }

    @Override
    public long count() {
        return evaluate(new TerminalOp<OUT, Long>() {
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
        Objects.requireNonNull(closeAction);
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
