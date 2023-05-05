package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.IntBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.basis.op.Op;
import com.oyealex.pipe.basis.op.Ops;
import com.oyealex.pipe.basis.op.TerminalOp;
import com.oyealex.pipe.flag.PipeFlag;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.oyealex.pipe.basis.Pipes.empty;
import static com.oyealex.pipe.flag.PipeFlag.IS_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
import static com.oyealex.pipe.flag.PipeFlag.IS_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
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

    /** 流水线标记 */
    private final int flag;

    ReferencePipe(int flag) {
        this.sourcePipe = this;
        this.prePipe = null;
        this.flag = flag;
    }

    ReferencePipe(ReferencePipe<?, ? extends IN> prePipe, int opFlag) {
        this.sourcePipe = prePipe.sourcePipe;
        this.prePipe = prePipe;
        this.flag = PipeFlag.combine(prePipe.flag, opFlag);
    }

    protected Spliterator<?> getDataSource() {
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
    public Pipe<OUT> keepIf(Predicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new ReferencePipe<>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.filterOp(op, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> keepWhile(Predicate<? super OUT> predicate) {
        return new ReferencePipe<>(this, 0) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.keepOrDropWhileOp(op, true, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> keepWhileEnumerated(LongBiPredicate<? super OUT> predicate) {
        return Pipe.super.keepWhileEnumerated(predicate);
    }

    @Override
    public Pipe<OUT> dropWhile(Predicate<? super OUT> predicate) {
        return new ReferencePipe<>(this, 0) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.keepOrDropWhileOp(op, false, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> dropWhileEnumerated(LongBiPredicate<? super OUT> predicate) {
        return Pipe.super.dropWhileEnumerated(predicate);
    }

    @Override
    public Pipe<OUT> filterEnumerated(LongBiPredicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new ReferencePipe<>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.filterEnumeratedOp(op, predicate);
            }
        };
    }

    @Override
    public <R> Pipe<R> map(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT) {
            @Override
            protected Op<OUT> wrapOp(Op<R> op) {
                return Ops.mapOp(op, mapper);
            }
        };
    }

    @Override
    public <R> Pipe<R> mapEnumerated(LongBiFunction<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT) {
            @Override
            protected Op<OUT> wrapOp(Op<R> op) {
                return Ops.mapEnumeratedOp(op, mapper);
            }
        };
    }

    @Override
    public <R> Pipe<R> flatMap(Function<? super OUT, ? extends Pipe<? extends R>> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT | NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<R> op) {
                return Ops.flatMapOP(op, mapper);
            }
        };
    }

    @Override
    public Pipe<OUT> distinct() {
        return new ReferencePipe<>(this, IS_DISTINCT | NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.distinctOp(op);
            }
        };
    }

    @Override
    public <R> Pipe<OUT> distinctBy(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_DISTINCT | NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.distinctByOp(op, mapper);
            }
        };
    }

    @Override
    public Pipe<OUT> sort() {
        if (PipeFlag.SORTED.isSet(flag)) {
            return this; // 已经排序了，无需再次排序
        }
        return new ReferencePipe<>(this, IS_SORTED | NOT_REVERSED_SORTED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sortOp(op, null);
            }
        };
    }

    @Override
    public Pipe<OUT> sort(Comparator<? super OUT> comparator) {
        requireNonNull(comparator);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sortOp(op, comparator);
            }
        };
    }

    @Override
    public <R extends Comparable<? super R>> Pipe<OUT> sortBy(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sortOp(op, Comparator.comparing(mapper));
            }
        };
    }

    @Override
    public <R> Pipe<OUT> sortBy(Function<? super OUT, ? extends R> mapper, Comparator<? super R> comparator) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sortOp(op, Comparator.comparing(mapper, comparator));
            }
        };
    }

    @Override
    public Pipe<OUT> limit(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("limit size cannot be negative, size: " + size);
        }
        if (size == 0) {
            return empty();
        }
        if (size == Long.MAX_VALUE) {
            return this;
        }
        return new ReferencePipe<>(this, NOT_SIZED | IS_SHORT_CIRCUIT) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sliceOp(op, 0, size);
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
        if (size == Long.MAX_VALUE) {
            return empty();
        }
        return new ReferencePipe<>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sliceOp(op, size, Long.MAX_VALUE);
            }
        };
    }

    @Override
    public Pipe<OUT> slice(long startInclusive, long endExclusive) {
        if (startInclusive < 0 || endExclusive < 0 || startInclusive > endExclusive) {
            throw new IllegalArgumentException("invalid slice bound: [" + startInclusive + ", " + endExclusive + ")");
        }
        if (startInclusive == endExclusive) {
            return empty();
        }
        if (startInclusive == 0 && endExclusive == Long.MAX_VALUE) {
            return this;
        }
        return new ReferencePipe<>(this, NOT_SIZED | IS_SHORT_CIRCUIT) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sliceOp(op, startInclusive, endExclusive - startInclusive);
            }
        };
    }

    @Override
    public Pipe<OUT> prepend(Iterator<? extends OUT> iterator) {
        requireNonNull(iterator);
        // TODO 2023-05-03 01:46 采用转为迭代器的方式实现，避免内部缓存，参考Stream.concat
        return new ReferencePipe<>(this, 0) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.prependOp(op, iterator);
            }
        };
    }

    @Override
    public Pipe<OUT> append(Iterator<? extends OUT> iterator) {
        requireNonNull(iterator);
        return new ReferencePipe<>(this, 0) {
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
        return new ReferencePipe<>(this, 0) {
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
    public Optional<OUT> findFirst() {
        return Pipe.super.findFirst();
    }

    @Override
    public Optional<OUT> findLast() {
        return Pipe.super.findLast();
    }

    @Override
    public Optional<OUT> findAny() {
        return Pipe.super.findAny();
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
