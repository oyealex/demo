package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.IntBiConsumer;
import com.oyealex.pipe.basis.functional.IntBiFunction;
import com.oyealex.pipe.basis.functional.IntBiPredicate;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.basis.op.Op;
import com.oyealex.pipe.basis.op.Ops;
import com.oyealex.pipe.basis.op.TerminalOp;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
    /**
     * 源节点，缓存指针以加速访问
     */
    @SuppressWarnings("rawtypes")
    private final ReferencePipe sourcePipe;

    /**
     * 此节点的前置节点，当且仅当此节点为源节点时为null
     */
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
    public <R> Pipe<R> mapEnumerated(IntBiFunction<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<R> op) {
                return Ops.mapEnumeratedOp(op, mapper);
            }
        };
    }

    @Override
    public <R> Pipe<R> mapEnumeratedLong(LongBiFunction<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<R> op) {
                return Ops.mapEnumeratedLongOp(op, mapper);
            }
        };
    }

    @Override
    public <R> Pipe<R> flatMap(Function<? super OUT, ? extends Pipe<? extends R>> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<R> op) {
                return Ops.flatMapOP(op, mapper);
            }
        };
    }

    @Override
    public Pipe<OUT> distinct() {
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.distinctOp(op);
            }
        };
    }

    @Override
    public <R> Pipe<OUT> distinctBy(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.distinctByOp(op, mapper);
            }
        };
    }

    @Override
    public Pipe<OUT> sort() {
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sortOp(op, null);
            }
        };
    }

    @Override
    public Pipe<OUT> sort(Comparator<? super OUT> comparator) {
        requireNonNull(comparator);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sortOp(op, comparator);
            }
        };
    }

    @Override
    public <R> Pipe<OUT> sortBy(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return null;
            }
        };
    }

    @Override
    public <R> Pipe<OUT> sortBy(Function<? super OUT, ? extends R> mapper, Comparator<? super OUT> comparator) {
        // TODO 2023-05-03 02:25
        return null;
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
    public Pipe<OUT> skip(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("skip size cannot be negative, size: " + size);
        }
        if (size == 0) {
            return this;
        }
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.skipOp(op, size);
            }
        };
    }

    @Override
    public Pipe<OUT> prepend(Iterator<? extends OUT> iterator) {
        requireNonNull(iterator);
        // TODO 2023-05-03 01:46 采用转为迭代器的方式实现，避免内部缓存，参考Stream.concat
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.prependOp(op, iterator);
            }
        };
    }

    @Override
    public Pipe<OUT> append(Iterator<? extends OUT> iterator) {
        requireNonNull(iterator);
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.appendOp(op, iterator);
            }
        };
    }

    @Override
    public Pipe<Pipe<OUT>> partition(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("partition size cannot be less then 1, size: " + size);
        }
        return new ReferencePipe<>(this) {
            @Override
            protected Op<OUT> wrapOp(Op<Pipe<OUT>> op) {
                return Ops.partitionOp(op, size);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super OUT> action) {
        requireNonNull(action);
        evaluate(Ops.forEachOp(action));
    }

    @Override
    public void forEachEnumerated(IntBiConsumer<? super OUT> action) {
        requireNonNull(action);
        evaluate(Ops.forEachEnumeratedOp(action));
    }

    @Override
    public Optional<OUT> min() {
        return evaluate(Ops.minMaxOp(true, null));
    }

    @Override
    public Optional<OUT> min(Comparator<? super OUT> comparator) {
        return evaluate(Ops.minMaxOp(true, comparator));
    }

    @Override
    public Optional<OUT> max() {
        return evaluate(Ops.minMaxOp(false, null));
    }

    @Override
    public Optional<OUT> max(Comparator<? super OUT> comparator) {
        return evaluate(Ops.minMaxOp(false, comparator));
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
