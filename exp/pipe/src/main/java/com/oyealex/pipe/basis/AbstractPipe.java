package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.IntBiPredicate;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.basis.op.Op;
import com.oyealex.pipe.basis.op.Ops;
import com.oyealex.pipe.basis.op.TerminalOp;

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
abstract class AbstractPipe<IN, T> implements Pipe<T> {
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

    abstract Op<IN> wrapOp(Op<T> op);

    @SuppressWarnings("unchecked")
    final <R> R evaluate(TerminalOp<T, R> terminalOp) {
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
    private Op<IN> wrapAllOp(Op<T> op) {
        for (@SuppressWarnings("rawtypes") AbstractPipe pipe = this; pipe.prePipe != null; pipe = pipe.prePipe) {
            op = pipe.wrapOp(op);
        }
        return (Op<IN>) op;
    }

    @Override
    public Pipe<T> filter(Predicate<? super T> predicate) {
        requireNonNull(predicate);
        return new AbstractPipe<T, T>(this) {
            @Override
            Op<T> wrapOp(Op<T> op) {
                return Ops.filterOp(op, predicate);
            }
        };
    }

    @Override
    public Pipe<T> filterEnumerated(IntBiPredicate<? super T> predicate) {
        requireNonNull(predicate);
        return new AbstractPipe<T, T>(this) {
            @Override
            Op<T> wrapOp(Op<T> op) {
                return Ops.filterEnumeratedOp(op, predicate);
            }
        };
    }

    @Override
    public Pipe<T> filterEnumeratedLong(LongBiPredicate<? super T> predicate) {
        requireNonNull(predicate);
        return new AbstractPipe<T, T>(this) {
            @Override
            Op<T> wrapOp(Op<T> op) {
                return Ops.filterEnumeratedLongOp(op, predicate);
            }
        };
    }

    @Override
    public <R> Pipe<R> map(Function<? super T, ? extends R> mapper) {
        requireNonNull(mapper);
        return new AbstractPipe<T, R>(this) {
            @Override
            Op<T> wrapOp(Op<R> op) {
                return Ops.mapOp(op, mapper);
            }
        };
    }

    @Override
    public Pipe<T> limit(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("limit size cannot be negative, size: " + size);
        }
        if (size == 0) {
            return Pipes.empty();
        }
        return new AbstractPipe<T, T>(this) {
            @Override
            Op<T> wrapOp(Op<T> op) {
                return Ops.gettLimitOp(op, size);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        requireNonNull(action);
        evaluate(Ops.foreachOp(action));
    }

    @Override
    public long count() {
        return evaluate(Ops.countOp());
    }

    @Override
    public List<T> toList() {
        return evaluate(Containers.makeToListTerminalOp());
    }

    @Override
    public Pipe<T> onClose(Runnable closeAction) {
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
}
