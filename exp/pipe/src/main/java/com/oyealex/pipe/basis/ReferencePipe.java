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
 * 基于引用的抽象流水线
 *
 * @author oyealex
 * @since 2023-03-04
 */
abstract class ReferencePipe<IN, OUT> implements Pipe<OUT> {
    /** 源节点，缓存指针以加速访问 */
    @SuppressWarnings("rawtypes")
    private final ReferencePipe sourcePipe;

    /** 此节点的前置节点，当且仅当此节点为源节点时为null */
    @SuppressWarnings("rawtypes")
    private final ReferencePipe prePipe;

    ReferencePipe() {
        this.sourcePipe = this;
        this.prePipe = null;
    }

    ReferencePipe(ReferencePipe<?, ? extends IN> prePipe) {
        this.sourcePipe = prePipe.sourcePipe;
        this.prePipe = prePipe;
    }

    protected Iterator<?> getDataSource() {
        throw new UnsupportedOperationException("source pipe required");
    }

    protected abstract Op<IN> wrapOp(Op<OUT> op);

    @SuppressWarnings("unchecked")
    private <R> R evaluate(TerminalOp<OUT, R> terminalOp) {
        Op<IN> op = wrapAllOp(terminalOp);
        op.begin(-1);
        sourcePipe.getDataSource().forEachRemaining(value -> op.accept((IN) value));
        op.end();
        return terminalOp.get();
    }

    @SuppressWarnings("unchecked")
    private Op<IN> wrapAllOp(Op<OUT> op) {
        for (@SuppressWarnings("rawtypes") ReferencePipe pipe = this; pipe.prePipe != null; pipe = pipe.prePipe) {
            op = pipe.wrapOp(op);
        }
        return (Op<IN>) op;
    }

    @Override
    public Pipe<OUT> filter(Predicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.filterOp(op, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> filterEnumerated(IntBiPredicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.filterEnumeratedOp(op, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> filterEnumeratedLong(LongBiPredicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.filterEnumeratedLongOp(op, predicate);
            }
        };
    }

    @Override
    public <R> Pipe<R> map(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<R> op) {
                return Ops.mapOp(op, mapper);
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
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.gettLimitOp(op, size);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super OUT> action) {
        requireNonNull(action);
        evaluate(Ops.foreachOp(action));
    }

    @Override
    public long count() {
        return evaluate(Ops.countOp());
    }

    @Override
    public List<OUT> toList() {
        return evaluate(Containers.makeToListTerminalOp());
    }

    @Override
    public Pipe<OUT> onClose(Runnable closeAction) {
        sourcePipe.onClose(requireNonNull(closeAction));
        return this;
    }

    @Override
    public void close() {
        sourcePipe.close();
    }
}
