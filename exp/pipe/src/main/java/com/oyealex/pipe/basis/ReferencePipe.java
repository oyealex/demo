package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.IntBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.basis.op.Op;
import com.oyealex.pipe.basis.op.Ops;
import com.oyealex.pipe.basis.op.TerminalOp;
import com.oyealex.pipe.flag.PipeFlag;
import com.oyealex.pipe.spliterator.ConcatSpliterator;

import java.util.Comparator;
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
import static com.oyealex.pipe.flag.PipeFlag.NOTHING;
import static com.oyealex.pipe.flag.PipeFlag.NOT_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
import static java.lang.Long.MAX_VALUE;
import static java.util.Objects.requireNonNull;

/**
 * 基于引用的抽象流水线
 *
 * @author oyealex
 * @since 2023-03-04
 */
// TODO 2023-05-06 22:43 关注流水线的重复消费问题，参见 java.util.stream.AbstractPipeline.linkedOrConsumed
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
    final int flag;

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

    protected Spliterator<?> takeDataSource() {
        throw new UnsupportedOperationException("source pipe required");
    }

    protected abstract Op<IN> wrapOp(Op<OUT> op);

    @SuppressWarnings("unchecked")
    private <R> R evaluate(TerminalOp<OUT, R> terminalOp) {
        processDataWithOp(sourcePipe.takeDataSource(), terminalOp);
        return terminalOp.get();
    }

    <OP extends Op<OUT>> void processDataWithOp(Spliterator<IN> dataSource, OP tailOp) {
        Op<IN> wrappedOp = wrapAllOp(tailOp);
        wrappedOp.begin(dataSource.getExactSizeIfKnown());
        if (PipeFlag.SHORT_CIRCUIT.isSet(flag)) {
            // 如果允许短路，则尝试短路处理
            do {/*noop*/} while (!wrappedOp.cancellationRequested() && dataSource.tryAdvance(wrappedOp));
        } else {
            dataSource.forEachRemaining(wrappedOp);
        }
        wrappedOp.end();
    }

    @SuppressWarnings("unchecked")
    Op<IN> wrapAllOp(Op<OUT> tailOp) {
        Op<?> resultOp = tailOp;
        for (@SuppressWarnings("rawtypes") ReferencePipe pipe = this; pipe.prePipe != null; pipe = pipe.prePipe) {
            resultOp = pipe.wrapOp(resultOp);
        }
        return (Op<IN>) resultOp;
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
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.keepOrDropWhileOp(op, true, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> keepWhileEnumerated(LongBiPredicate<? super OUT> predicate) {
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.keepOrDropWhileEnumeratedOp(op, true, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> dropWhile(Predicate<? super OUT> predicate) {
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.keepOrDropWhileOp(op, false, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> dropWhileEnumerated(LongBiPredicate<? super OUT> predicate) {
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.keepOrDropWhileEnumeratedOp(op, false, predicate);
            }
        };
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
    public Pipe<OUT> peek(Consumer<? super OUT> consumer) {
        requireNonNull(consumer);
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.peekOp(op, consumer);
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
        if (size == MAX_VALUE) {
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
        if (size == MAX_VALUE) {
            return empty();
        }
        return new ReferencePipe<>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> op) {
                return Ops.sliceOp(op, size, MAX_VALUE);
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
        if (startInclusive == 0 && endExclusive == MAX_VALUE) {
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
    public Pipe<OUT> prepend(Spliterator<? extends OUT> spliterator) {
        requireNonNull(spliterator);
        @SuppressWarnings("unchecked") ConcatSpliterator<OUT, Spliterator<OUT>> finalSpliterator
            = new ConcatSpliterator<>((Spliterator<OUT>) spliterator, toSpliterator());
        Pipe<OUT> pipe = Pipes.pipe(finalSpliterator);
        return pipe.onClose(this::close);
    }

    @Override
    public Pipe<OUT> append(Spliterator<? extends OUT> spliterator) {
        requireNonNull(spliterator);
        @SuppressWarnings("unchecked") ConcatSpliterator<OUT, Spliterator<OUT>> finalSpliterator
            = new ConcatSpliterator<>(toSpliterator(), (Spliterator<OUT>) spliterator);
        Pipe<OUT> pipe = Pipes.pipe(finalSpliterator);
        return pipe.onClose(this::close);
    }

    @Override
    public Pipe<Pipe<OUT>> partition(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("partition size cannot be less then 1, size: " + size);
        }
        return new ReferencePipe<>(this, NOTHING) {
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
    @SuppressWarnings("unchecked")
    public Spliterator<OUT> toSpliterator() {
        return this == sourcePipe ? (Spliterator<OUT>) sourcePipe.takeDataSource() :
            new PipeSpliterator<>(this, (Spliterator<IN>) sourcePipe.takeDataSource());
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
